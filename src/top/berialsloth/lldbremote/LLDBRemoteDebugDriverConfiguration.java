package top.berialsloth.lldbremote;

import com.intellij.execution.ExecutionException;
import com.jetbrains.cidr.ArchitectureType;
import com.jetbrains.cidr.cpp.execution.debugger.backend.CLionLLDBDriverConfiguration;
import com.jetbrains.cidr.cpp.toolchains.CPPEnvironment;
import com.jetbrains.cidr.execution.debugger.CidrStackFrame;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriver;
import org.jetbrains.annotations.NotNull;

public class LLDBRemoteDebugDriverConfiguration extends CLionLLDBDriverConfiguration {
    private LLDBRemoteRunConfiguration projectConfiguration;
    private CPPEnvironment myEnvironment;

    public LLDBRemoteDebugDriverConfiguration(@NotNull LLDBRemoteRunConfiguration projectConfiguration,CPPEnvironment myEnvironment)
    {
        super(projectConfiguration.getProject(), myEnvironment);
        this.projectConfiguration = projectConfiguration;
    }

    @Override
    public @NotNull String getDriverName() {
        return "LLDB Remote Debug Driver";
    }

    @Override
    public @NotNull LLDBDriver createDriver(DebuggerDriver.@NotNull Handler handler, @NotNull ArchitectureType architectureType) throws ExecutionException {
        var driver = new LLDBRemoteDebuggerDriver(handler,this,architectureType,projectConfiguration);
        driver.setAutorunScriptName("jb_lldb_init");
        driver.setStepIntoClassName("jb_lldb_stepping.StepIn");
        driver.setStepOverClassName("jb_lldb_stepping.StepOver");
        driver.setStepOutClassName((String)null);
        return driver;
    }

}
