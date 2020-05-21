package top.berialsloth.lldbremote;

import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.execution.configurations.SimpleProgramParameters;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
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
import kotlin.jvm.internal.Intrinsics;
import org.apache.xerces.xs.StringList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.Key;
import java.util.*;

import static com.sun.jna.platform.win32.Advapi32Util.EventLogType.Informational;

public class LLDBRemoteLauncher extends CMakeLauncher {

    private LLDBRemoteRunConfiguration myConfiguration;
    private ExecutionEnvironment myEnvironment;
    LLDBRemoteLauncher(@NotNull ExecutionEnvironment environment,@NotNull LLDBRemoteRunConfiguration configuration) {
        super(environment,configuration);
        this.myConfiguration = configuration;
        this.myEnvironment = environment;
        //environment.
    }

    @Override
    public ProcessHandler createProcess(@NotNull CommandLineState commandLineState) throws ExecutionException {

        GeneralCommandLine commandLine = createLLDBProcess();
        OSProcessHandler osProcessHandler = new OSProcessHandler(commandLine);
        //osProcessHandler.
        //ProcessAdapter adapter = new ProcessAdapter();
        //adapter.
        //adapter.startNotified();

        osProcessHandler.addProcessListener(new ProcessAdapter() {

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                super.processTerminated(event);
                String str = "123123123";
                try {
                    event.getProcessHandler().getProcessInput().write(str.getBytes(),0,str.length());
                    event.getProcessHandler().getProcessInput().flush();
                } catch (IOException e) {
                    System.out.println( e.getMessage());
                }
                Project project = commandLineState.getEnvironment().getProject();
                if (event.getExitCode() == 0) {

                } else {
                   //Informational.showFailedDownloadNotification(project);
                }
            }
            @Override
            public void startNotified(@NotNull ProcessEvent event) {
                super.startNotified(event);
                String str = "123123123";
                try {
                    event.getProcessHandler().getProcessInput().write(str.getBytes(),0,str.length());
                    event.getProcessHandler().getProcessInput().flush();
                } catch (IOException e) {
                    System.out.println( e.getMessage());
                }
                Project project = commandLineState.getEnvironment().getProject();
                if (event.getExitCode() == 0) {

                } else {
                    //Informational.showFailedDownloadNotification(project);
                }
            }

        });
        return osProcessHandler;
    }



    @Override
    public @NotNull CidrDebugProcess createDebugProcess(@NotNull CommandLineState commandLineState, @NotNull XDebugSession xDebugSession) throws ExecutionException {
        //CidrDebugProcess process;
        //process.
        //new CidrDebugProcess.DebuggerCommand<Cidr>(,,);
        var pair = CMakeRunConfigurationUtil.getRunFileAndEnvironment(this);
        CPPEnvironment environment = (CPPEnvironment)pair.component2();
        //environment.
        var runner = commandLineState.getEnvironment().getRunner();
        var toolChains = CPPToolchains.getInstance().getDefaultToolchain();

        //CPPDebugger cppDebugger = CPPDebugger.create(CPPDebugger.Kind.BUNDLED_LLDB,myConfiguration.getLLDBBinaryPath());
        //toolChains.setDebugger(cppDebugger);

        xDebugSession.stop();

        //parameters.setRemoteCommand("platform select remote-windows");
        toolChains = toolChains.copy();
        //File lldbFile =new File( myConfiguration.getLLDBBinaryPath());
        //gdbPath = gdbFile.getAbsolutePath();

        GeneralCommandLine commandLine = this.createCommandLine(commandLineState, pair.component1(), environment, false);
        CPPDebugger cppDebugger = CPPDebugger.bundledLldb();
        toolChains.setDebugger(cppDebugger);
        //var lldbDriver = new LLDBRemoteDebugDriverConfiguration(myConfiguration);
        //LLDBRemoteRunConfiguration(,)
        var drive = new LLDBDriverConfiguration();
        //var hostMachine = lldbDriver.getHostMachine();
        //lldbDriver.getHostMachine().isRemote();
        //lldbDriver.getHostMachine().getName();
        //lldbDriver.getHostMachine().getHostId();
        //drive.getHostMachine()
        //System.out.println(lldbDriver.getHostMachine().isRemote());
        //System.out.println(lldbDriver.getHostMachine().getName());
        //System.out.println(lldbDriver.getHostMachine().getHostId());
        //var pl = lldbDriver.getHostMachine().getProcessList();

        //xDebugSession.getConsoleView().print("ASDASDASDA", ConsoleViewContentType.NORMAL_OUTPUT);
        //lldbDriver.createDebugProcessHandler()
        //getDe
        //parameters.setAdditionalCommands(commandList);
        //CLionLLDBDriverConfiguration

        LLDBRemoteDebugProcess debugProcess = new LLDBRemoteDebugProcess(createDebugParameters(environment,commandLine),xDebugSession,commandLineState.getConsoleBuilder(),myConfiguration);
        var listener = new ProcessOutputListener();
        //CLionLauncher
        //debugProcess.getProcessHandler().addProcessListener();
        //lldbDriver
        //LLDBRemoteDebugProcess debugProcess = new LLDBRemoteDebugProcess(lldbDriver,xDebugSession,commandLineState.getConsoleBuilder(),myConfiguration);
        return debugProcess;
    }
   @NotNull
    protected GeneralCommandLine createCommandLine(@NotNull CommandLineState state, @NotNull File runFile, @NotNull final CPPEnvironment environment, final boolean usePty) throws ExecutionException {
        //long var5 = a ^ 40042702986589L;
        Intrinsics.checkParameterIsNotNull(state, "state");
        Intrinsics.checkParameterIsNotNull(runFile, "runFile");
        Intrinsics.checkParameterIsNotNull(environment, "environment");
        Path var10000 = environment.getHostMachine().getPath(runFile.getPath(), new String[0]);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "environment.hostMachine.getPath(runFile.path)");
        final Path var7 = var10000;
        if (!Files.exists(var7, new LinkOption[0])) {
            throw new ExecutionException(CidrBundle.message("run.fileNotFound", new Object[]{var7}));
        } else {
            Object var8 = ApplicationManager.getApplication().runReadAction((ThrowableComputable)(new ThrowableComputable() {
                public final GeneralCommandLine compute() {
                    GeneralCommandLine var3 = usePty ? (GeneralCommandLine)(new PtyCommandLine()).withUseCygwinLaunch(environment.isCygwin()) : new GeneralCommandLine();
                    Intrinsics.checkExpressionValueIsNotNull(var3, "cl");
                    var3.setExePath(var7.toString());
                    CidrCommandLineConfigurator var4 = new CidrCommandLineConfigurator(getProject(), createCidrProgramParameters(var7.getParent().toString()));
                    try {
                        var4.configureCommandLine(var3);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    try {
                        environment.prepare(var3, CidrToolEnvironment.PrepareFor.RUN);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return var3;
                }
            }));
            Intrinsics.checkExpressionValueIsNotNull(var8, "ApplicationManager.getAp…eFor.RUN)\n\n      cl\n    }");
            return (GeneralCommandLine)var8;
        }
    }

    @Override
    protected @NotNull Project getProject() {
        return myConfiguration.getProject();
    }

    private GeneralCommandLine createLLDBProcess()
    {
        GeneralCommandLine commandLine = new PtyCommandLine()
                .withExePath(myConfiguration.getLLDBBinaryPath())
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);

        return commandLine;
    }


    @Override
    public CidrDebugProcess startDebugProcess(@NotNull CommandLineState commandLineState,
                                              @NotNull XDebugSession xDebugSession) throws ExecutionException {

        return super.startDebugProcess(commandLineState,xDebugSession);
    }
    @NotNull
    protected  RunParameters createDebugParameters(@NotNull CPPEnvironment environment, @NotNull GeneralCommandLine cl) throws ExecutionException {
        //long var3 = a ^ 8792920826309L;

        Intrinsics.checkParameterIsNotNull(environment, "environment");
        Intrinsics.checkParameterIsNotNull(cl, "cl");
        HostMachine var10000 = environment.getHostMachine();
        Intrinsics.checkExpressionValueIsNotNull(var10000, "environment.hostMachine");
        HostMachine var5 = var10000;
        TrivialInstaller var6 = var5.isRemote() ? (TrivialInstaller)(new RemoteInstaller(var5, cl)) : new TrivialInstaller(cl);
        //DebuggerDriverConfiguration var7 = CPPDebuggerUtilKt.createDriverConfiguration(this.getProject(), environment);
        DebuggerDriverConfiguration var7 = new LLDBRemoteDebugDriverConfiguration(myConfiguration,environment);
        ArchitectureType var12 = SystemInfo.isWindows ? ExecutableFileFormatUtil.tryReadPeMachineType(cl.getExePath()) : ArchitectureType.UNKNOWN;
        Intrinsics.checkExpressionValueIsNotNull(var12, "if (SystemInfo.isWindows… ArchitectureType.UNKNOWN");
        ArchitectureType var8 = var12;
        CidrCustomDebuggerProvider var9 = CidrCustomDebuggerProvider.getInstance(this.myEnvironment);
        if (var9 != null) {
            DebuggerDriverConfiguration var10 = (DebuggerDriverConfiguration)ContainerUtil.getFirstItem(var9.getDebuggerConfigurations());
            if (var10 != null) {
                return (RunParameters)(new TrivialRunParameters(var10, (Installer)var6, var8));
            }
        }

        CPPToolSet var13 = environment.getToolSet();
        Intrinsics.checkExpressionValueIsNotNull(var13, "environment.toolSet");
        String var11 = var13.isDebugSupportDisabled();
        if (var11 != null) {
            throw new ExecutionException(var11);
        } else {
            return (RunParameters)(new TrivialRunParameters(var7, (Installer)var6, var8));
        }
    }



    private final CidrProgramParameters createCidrProgramParameters(final String var1) {
        CidrProgramParameters var2 = new CidrProgramParameters();
        CidrProgramParametersConfigurator var3 = new CidrProgramParametersConfigurator() {

            @NotNull
            protected String getDefaultWorkingDir(@NotNull Project project) {
                Intrinsics.checkParameterIsNotNull(project, "project");
                return var1;
            }
        };
        var3.configureConfiguration((SimpleProgramParameters)var2, (CommonProgramRunConfigurationParameters)this.getConfiguration());
        return var2;
    }
}
