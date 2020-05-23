package top.berialsloth.lldbremote;

import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.jetbrains.cidr.ArchitectureType;
import com.jetbrains.cidr.CidrBundle;
import com.jetbrains.cidr.ExecutableFileFormatUtil;
import com.jetbrains.cidr.cpp.execution.*;
import com.jetbrains.cidr.cpp.execution.CMakeLauncher;
import com.jetbrains.cidr.cpp.execution.debugger.backend.CLionLLDBDriverConfiguration;
import com.jetbrains.cidr.cpp.toolchains.*;
import com.jetbrains.cidr.execution.*;
import com.jetbrains.cidr.execution.debugger.CidrCustomDebuggerProvider;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.debugger.CidrDebuggerPathManager;
import com.jetbrains.cidr.execution.debugger.CidrLocalDebugProcess;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriver;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.remote.CidrRemoteDebugParameters;
import com.jetbrains.cidr.execution.debugger.remote.CidrRemoteGDBDebugProcess;
import com.jetbrains.cidr.execution.testing.CidrLauncher;
import com.jetbrains.cidr.lang.toolchains.CidrToolEnvironment;
import com.jetbrains.cidr.lang.types.d;
import com.jetbrains.cidr.system.HostMachine;
import com.jetbrains.cidr.system.RemoteInstaller;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import org.apache.xerces.xs.StringList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;


public class LLDBRemoteLauncher extends CMakeLauncher {

    private CidrRunConfigurationExtensionsManager myExtensionsManager = CidrRunConfigurationExtensionsManager.getInstance();
    private LLDBRemoteRunConfiguration myConfiguration;
    private ExecutionEnvironment myEnvironment;

    LLDBRemoteLauncher(@NotNull ExecutionEnvironment environment,@NotNull LLDBRemoteRunConfiguration configuration) {
        super(environment,configuration);
        this.myConfiguration = configuration;
        this.myEnvironment = environment;
    }



    @Override
    public @NotNull CidrDebugProcess createDebugProcess(@NotNull CommandLineState commandLineState, @NotNull XDebugSession xDebugSession) throws ExecutionException {
        var pair = CMakeRunConfigurationUtil.getRunFileAndEnvironment(this);
        CPPEnvironment environment = (CPPEnvironment)pair.component2();
        RunnerSettings runnerSettings = this.myEnvironment.getRunnerSettings();
        ProgramRunner programRunner = this.myEnvironment.getRunner();
        var configurationExtensionContext = new ConfigurationExtensionContext();//magic, why?
        GeneralCommandLine commandLine = this.createCommandLine(commandLineState, pair.component1(), environment, false);

        this.myExtensionsManager.patchCommandLine(this.getConfiguration(),runnerSettings, (CidrToolEnvironment)environment,commandLine,programRunner.getRunnerId(),configurationExtensionContext);
        environment.convertPathVariableToEnv(commandLine);
        commandLineState.setConsoleBuilder(this.createConsoleBuilder(commandLineState, (CidrToolEnvironment)environment,getProjectBaseDir()));
        this.myExtensionsManager.patchCommandLineState(this.getConfiguration(), runnerSettings, (CidrToolEnvironment)environment, getProjectBaseDir(), commandLineState, programRunner.getRunnerId(), configurationExtensionContext);//magic, why?
        CidrDebugConsoleFilterProvider filterProvider = new CidrDebugConsoleFilterProvider((CidrToolEnvironment)environment,getProjectBaseDir() != null ? getProjectBaseDir().toPath() : null);
        LLDBRemoteDebugProcess debugProcess = new LLDBRemoteDebugProcess(createDebugParameters(environment,commandLine),xDebugSession,commandLineState.getConsoleBuilder(),myConfiguration,filterProvider);
        var listener = new ProcessOutputListener();
        configProcessHandler(debugProcess.getProcessHandler(), debugProcess.isDetachDefault(), true, this.getProject());
        this.myExtensionsManager.attachExtensionsToProcess(this.getConfiguration(), debugProcess.getProcessHandler(), (CidrToolEnvironment)environment,runnerSettings,programRunner.getRunnerId(),configurationExtensionContext);
        return debugProcess;
    }

   @NotNull
    protected GeneralCommandLine createCommandLine(@NotNull CommandLineState state, @NotNull File runFile, @NotNull final CPPEnvironment environment, final boolean usePty) throws ExecutionException {
        final Path path = environment.getHostMachine().getPath(runFile.getPath(), new String[0]);
        if (!Files.exists(path, new LinkOption[0])) {
            throw new ExecutionException(CidrBundle.message("run.fileNotFound", new Object[]{path}));
        } else {
            Object outCL = ApplicationManager.getApplication().runReadAction((ThrowableComputable)(new ThrowableComputable() {
                public final GeneralCommandLine compute() {
                    GeneralCommandLine tmpCommandLine = usePty ? (GeneralCommandLine)(new PtyCommandLine()).withUseCygwinLaunch(environment.isCygwin()) : new GeneralCommandLine();
                    tmpCommandLine.setExePath(path.toString());
                    CidrCommandLineConfigurator commandLineConfigurator = new CidrCommandLineConfigurator(getProject(), createCidrProgramParameters(path.getParent().toString()));
                    try {
                        commandLineConfigurator.configureCommandLine(tmpCommandLine);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    try {
                        environment.prepare(tmpCommandLine, CidrToolEnvironment.PrepareFor.RUN);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return tmpCommandLine;
                }
            }));
            return (GeneralCommandLine)outCL;
        }
    }

    @Override
    protected @NotNull Project getProject() {
        return myConfiguration.getProject();
    }

    @NotNull
    protected  RunParameters createDebugParameters(@NotNull CPPEnvironment environment, @NotNull GeneralCommandLine cl) throws ExecutionException {
        TrivialInstaller installer = new TrivialInstaller(cl);
        DebuggerDriverConfiguration lldbRemoteDebugDriverConfiguration = new LLDBRemoteDebugDriverConfiguration(myConfiguration,environment);
        ArchitectureType archType = SystemInfo.isWindows ? ExecutableFileFormatUtil.tryReadPeMachineType(cl.getExePath()) : ArchitectureType.UNKNOWN;
        CidrCustomDebuggerProvider cirdrCustonDebuggerProvider = CidrCustomDebuggerProvider.getInstance(this.myEnvironment);//magic,why?
        if (cirdrCustonDebuggerProvider != null) {
            DebuggerDriverConfiguration var10 = (DebuggerDriverConfiguration)ContainerUtil.getFirstItem(cirdrCustonDebuggerProvider.getDebuggerConfigurations());
            if (var10 != null) {
                return (RunParameters)(new TrivialRunParameters(var10, (Installer)installer, archType));
            }
        }

        CPPToolSet toolSet = environment.getToolSet();
        Intrinsics.checkExpressionValueIsNotNull(toolSet, "environment.toolSet");
        String var11 = toolSet.isDebugSupportDisabled();
        if (var11 != null) {
            throw new ExecutionException(var11);
        } else {
            return (RunParameters)(new TrivialRunParameters(lldbRemoteDebugDriverConfiguration, (Installer)installer, archType));
        }
    }

//magic function
    private final CidrProgramParameters createCidrProgramParameters(final String path) {
        CidrProgramParameters out = new CidrProgramParameters();
        CidrProgramParametersConfigurator var3 = new CidrProgramParametersConfigurator() {

            @NotNull
            protected String getDefaultWorkingDir(@NotNull Project project) {

                return path;
            }
        };
        var3.configureConfiguration((SimpleProgramParameters)out, (CommonProgramRunConfigurationParameters)this.getConfiguration());
        return out;
    }

}
