package top.berialsloth.lldbremote;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.testing.CidrLauncher;
import org.jetbrains.annotations.NotNull;

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
        osProcessHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                super.processTerminated(event);
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
        return super.startDebugProcess(commandLineState, xDebugSession);
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
}
