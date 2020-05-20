package top.berialsloth.lldbremote;

import com.google.protobuf.GeneratedMessage;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.BaseProcessHandler;
import com.intellij.lang.Language;
import com.intellij.xdebugger.impl.settings.DebuggerConfigurable;
import com.jetbrains.cidr.ArchitectureType;
import com.jetbrains.cidr.execution.Installer;
import com.jetbrains.cidr.execution.debugger.backend.*;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriver;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.memory.Address;
import com.jetbrains.cidr.execution.debugger.memory.AddressRange;
import com.jetbrains.cidr.system.HostMachine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class LLDBRemoteDebuggerDriver extends LLDBDriver {

    public LLDBRemoteDebuggerDriver(@NotNull Handler handler, @NotNull LLDBDriverConfiguration starter, @NotNull ArchitectureType architectureType) throws ExecutionException {
        super(handler,starter, architectureType);

    }
/*
    @Override
    public DebuggerDriver.Inferior loadForLaunch(@NotNull Installer installer,@NotNull String s)
    {
        //this.getProtobufClient().sendMessageAndWaitForReply(GeneratedMessage,,);
        return null;
    }

*/
}
