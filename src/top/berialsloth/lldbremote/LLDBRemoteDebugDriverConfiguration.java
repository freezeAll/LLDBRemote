package top.berialsloth.lldbremote;

import com.google.protobuf.GeneratedMessage;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.util.Expirable;
import com.jetbrains.cidr.ArchitectureType;
import com.jetbrains.cidr.cpp.execution.debugger.backend.CLionLLDBDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.CidrStackFrame;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriver;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import com.jetbrains.cidr.system.HostMachine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LLDBRemoteDebugDriverConfiguration extends LLDBDriverConfiguration {
    private LLDBRemoteRunConfiguration projectConfiguration;
    public LLDBRemoteDebugDriverConfiguration(@NotNull LLDBRemoteRunConfiguration projectConfiguration)
    {
        super();
        this.projectConfiguration = projectConfiguration;
    }
    @Override
    public @NotNull String getDriverName() {
        return "LLDB Remote Debug Driver";
    }

    @Override
    public @NotNull LLDBDriver createDriver(DebuggerDriver.@NotNull Handler handler, @NotNull ArchitectureType architectureType) throws ExecutionException {
        var driver = new LLDBRemoteDebuggerDriver(handler,this,architectureType,projectConfiguration);
        //DebuggerDriver.Inferior

        return driver;
    }


}
