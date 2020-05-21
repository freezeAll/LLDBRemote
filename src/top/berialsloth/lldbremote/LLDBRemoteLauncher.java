package top.berialsloth.lldbremote;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.jetbrains.cidr.cpp.execution.CLionLauncher;
import com.jetbrains.cidr.cpp.execution.CMakeLauncher;
import com.jetbrains.cidr.cpp.execution.debugger.backend.CLionLLDBDriverConfiguration;
import com.jetbrains.cidr.cpp.toolchains.CPPDebugger;
import com.jetbrains.cidr.cpp.toolchains.CPPToolchains;
import com.jetbrains.cidr.execution.Installer;
import com.jetbrains.cidr.execution.ProcessOutputListener;
import com.jetbrains.cidr.execution.RunParameters;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.TrivialInstaller;
import com.jetbrains.cidr.execution.debugger.CidrDebuggerPathManager;
import com.jetbrains.cidr.execution.debugger.CidrLocalDebugProcess;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriver;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.remote.CidrRemoteDebugParameters;
import com.jetbrains.cidr.execution.debugger.remote.CidrRemoteGDBDebugProcess;
import com.jetbrains.cidr.execution.testing.CidrLauncher;

import org.apache.xerces.xs.StringList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.*;

import static com.sun.jna.platform.win32.Advapi32Util.EventLogType.Informational;

public class LLDBRemoteLauncher extends CidrLauncher {

    private final LLDBRemoteRunConfiguration myConfiguration;

    LLDBRemoteLauncher(LLDBRemoteRunConfiguration inp) {
        this.myConfiguration = inp;
    }
    @Override
    protected ProcessHandler createProcess(@NotNull CommandLineState commandLineState) throws ExecutionException {

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
    protected @NotNull CidrDebugProcess createDebugProcess(@NotNull CommandLineState commandLineState, @NotNull XDebugSession xDebugSession) throws ExecutionException {
        //CidrDebugProcess process;
        //process.
        //new CidrDebugProcess.DebuggerCommand<Cidr>(,,);
        var runner = commandLineState.getEnvironment().getRunner();
        var toolChains = CPPToolchains.getInstance().getDefaultToolchain();

        //CPPDebugger cppDebugger = CPPDebugger.create(CPPDebugger.Kind.BUNDLED_LLDB,myConfiguration.getLLDBBinaryPath());
        //toolChains.setDebugger(cppDebugger);

        xDebugSession.stop();

        //parameters.setRemoteCommand("platform select remote-windows");
        toolChains = toolChains.copy();
        File lldbFile =new File( myConfiguration.getLLDBBinaryPath());
        //gdbPath = gdbFile.getAbsolutePath();

        CPPDebugger cppDebugger = CPPDebugger.bundledLldb();
        toolChains.setDebugger(cppDebugger);
        var lldbDriver = new LLDBRemoteDebugDriverConfiguration(myConfiguration);
        //LLDBRemoteRunConfiguration(,)
        var drive = new LLDBDriverConfiguration();
        var hostMachine = lldbDriver.getHostMachine();
        lldbDriver.getHostMachine().isRemote();
        lldbDriver.getHostMachine().getName();
        lldbDriver.getHostMachine().getHostId();
        //drive.getHostMachine()
        System.out.println(lldbDriver.getHostMachine().isRemote());
        System.out.println(lldbDriver.getHostMachine().getName());
        System.out.println(lldbDriver.getHostMachine().getHostId());
        var pl = lldbDriver.getHostMachine().getProcessList();
        CLionLauncher
        var cl = new GeneralCommandLine()
                .withExePath("C:\\Users\\Administrator\\CLionProjects\\untitled1\\cmake-build-debug\\untitled1.exe");
        //xDebugSession.getConsoleView().print("ASDASDASDA", ConsoleViewContentType.NORMAL_OUTPUT);
        //lldbDriver.createDebugProcessHandler()
        LLDBRemoteRunParameters parameters = new LLDBRemoteRunParameters(lldbDriver,cl,myConfiguration);
        //parameters.setAdditionalCommands(commandList);
        //CLionLLDBDriverConfiguration

        LLDBRemoteDebugProcess debugProcess = new LLDBRemoteDebugProcess(parameters,xDebugSession,commandLineState.getConsoleBuilder(),myConfiguration);
        var listener = new ProcessOutputListener();

        //debugProcess.getProcessHandler().addProcessListener();
        //lldbDriver
        //LLDBRemoteDebugProcess debugProcess = new LLDBRemoteDebugProcess(lldbDriver,xDebugSession,commandLineState.getConsoleBuilder(),myConfiguration);
        return debugProcess;
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
}
