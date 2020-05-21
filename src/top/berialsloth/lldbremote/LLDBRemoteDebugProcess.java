package top.berialsloth.lldbremote;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.cidr.execution.RunParameters;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.debugger.CidrLocalDebugProcess;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerCommandException;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LLDBRemoteDebugProcess extends CidrDebugProcess {
    @NotNull LLDBRemoteRunConfiguration myConfig;
    public LLDBRemoteDebugProcess(@NotNull RunParameters parameters, @NotNull XDebugSession session,
                                  @NotNull TextConsoleBuilder consoleBuilder,
                                  @NotNull LLDBRemoteRunConfiguration myConfig
    ) throws ExecutionException {
        super(parameters, session, consoleBuilder);

        this.myConfig = myConfig;
    }

    @Override
    protected DebuggerDriver.@NotNull Inferior doLoadTarget(@NotNull DebuggerDriver debuggerDriver) throws ExecutionException {
        //debuggerDriver.executeConsoleCommand();
        //debuggerDriver.executeConsoleCommand("asd");
        //debuggerDriver.load
        //debuggerDriver.loadForLaunch(myRunParameters.getInstaller(),"x86_64");


        //this.doGetProcessHandler().
        //var inferior =  debuggerDriver.loadForLaunch(myRunParameters.getInstaller(),"");
        //inferior.
        //new DebuggerDriver.Inferior();
        //return debuggerDriver.loadForAttach(myConfig.getCMakeTarget().getName(),true);
        //return debuggerDriver.loadForLaunch(myRunParameters.getInstaller(),"target create \"C:\\Users\\Administrator\\CLionProjects\\untitled1\\cmake-build-debug\\untitled1.exe\"");
        //debuggerDriver.
        //debuggerDriver.loadForLaunch()
        return debuggerDriver.loadForLaunch(myRunParameters.getInstaller(),myRunParameters.getArchitectureId());
    }
}
