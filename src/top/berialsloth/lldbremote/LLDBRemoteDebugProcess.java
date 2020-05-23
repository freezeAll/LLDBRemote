package top.berialsloth.lldbremote;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.cidr.execution.RunParameters;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;

import org.jetbrains.annotations.NotNull;


public class LLDBRemoteDebugProcess extends CidrDebugProcess {

    @NotNull LLDBRemoteRunConfiguration myConfig;

    public LLDBRemoteDebugProcess(@NotNull RunParameters parameters, @NotNull XDebugSession session,
                                  @NotNull TextConsoleBuilder consoleBuilder,
                                  @NotNull LLDBRemoteRunConfiguration myConfig,
                                  @NotNull ConsoleFilterProvider consoleFilterProvider
                                  ) throws ExecutionException {
        super(parameters, session, consoleBuilder,consoleFilterProvider);
        this.myConfig = myConfig;
    }

    @Override
    protected DebuggerDriver.@NotNull Inferior doLoadTarget(@NotNull DebuggerDriver debuggerDriver) throws ExecutionException {
        debuggerDriver.setRedirectOutputToFiles(true);//always on "."
        return debuggerDriver.loadForLaunch(myRunParameters.getInstaller(),myRunParameters.getArchitectureId());
    }

}
