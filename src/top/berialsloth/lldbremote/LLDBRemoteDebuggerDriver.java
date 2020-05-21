package top.berialsloth.lldbremote;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.BaseProcessHandler;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Consumer;
import com.intellij.xdebugger.impl.settings.DebuggerConfigurable;
import com.jetbrains.cidr.ArchitectureType;
import com.jetbrains.cidr.execution.CidrDebuggerBundle;
import com.jetbrains.cidr.execution.Installer;
import com.jetbrains.cidr.execution.ProcessOutputReaders;
import com.jetbrains.cidr.execution.CidrExecUtil;
import com.jetbrains.cidr.execution.debugger.CidrDebuggerLog;

import com.jetbrains.cidr.execution.debugger.backend.*;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriver;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriverException;
import com.jetbrains.cidr.execution.debugger.backend.lldb.ProtobufMessageFactory;
import com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Model;
import com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.ProtocolResponses;
import com.jetbrains.cidr.execution.debugger.memory.Address;
import com.jetbrains.cidr.execution.debugger.memory.AddressRange;
import com.jetbrains.cidr.lang.types.d;
import com.jetbrains.cidr.system.HostMachine;
import com.jetbrains.cidr.system.LocalHost;
import com.pty4j.unix.Pty;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Protocol.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;

import static com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBBinUrlProvider.lldb;

public class LLDBRemoteDebuggerDriver extends LLDBDriver {
    private LLDBRemoteRunConfiguration projectConfiguration;
    public LLDBRemoteDebuggerDriver(@NotNull Handler handler, @NotNull LLDBDriverConfiguration starter, @NotNull ArchitectureType architectureType,@NotNull LLDBRemoteRunConfiguration projectConfiguration) throws ExecutionException {
        super(handler,starter, architectureType);
        this.projectConfiguration = projectConfiguration;

    }
    /*
    @Override
    public boolean interrupt() throws ExecutionException {
        //return super.interrupt();
        Ref var1 = new Ref();
        CompositeRequest var2 = ProtobufMessageFactory.handleConsoleCommand(-1,-1,"process interrupt");
        this.getProtobufClient().sendMessageAndWaitForReply(var2, ProtocolResponses.HandleConsoleCommand_Res.class, (var1x) -> {
            var1.set(var1x.getCommonResponse().getIsValid());
        });
        return var1.isNull() ? false : (Boolean)var1.get();
    }

    @Override
    public DebuggerDriver.Inferior loadForLaunch(@NotNull Installer installer,@NotNull String s) throws ExecutionException {

        var connectReq = ProtobufMessageFactory.connectPlatform(projectConfiguration.getRemotePlatform(),projectConfiguration.getLLDBInitUrl());
        LLDBDriver.ThrowIfNotValid connectRes = new LLDBDriver.ThrowIfNotValid("Couldn't connect platform");
        this.getProtobufClient().sendMessageAndWaitForReply(connectReq, ProtocolResponses.ConnectPlatform_Res.class,connectRes);
        if(connectRes.isValid())
        {
            connectRes.throwIfNeeded();
        }



        //this.executeConsoleCommand("platform select " + projectConfiguration.getRemotePlatform());
        //this.executeConsoleCommand("platform connect " + projectConfiguration.getLLDBInitUrl());
        //this.executeConsoleCommand("platform setting -w " + projectConfiguration.getRemoteWorkingDir());

        var createReq = ProtobufMessageFactory.handleConsoleCommand(-1,-1,"target create " + installer.getExecutableFile().getAbsolutePath());
        LLDBDriver.ThrowIfNotValid createRes = new LLDBDriver.ThrowIfNotValid("Couldn't create target platform");
        this.getProtobufClient().sendMessageAndWaitForReply(createReq, ProtocolResponses.HandleConsoleCommand_Res.class, createRes);
        if (createRes.isValid()) {
            createRes.throwIfNeeded();
        }
        //this.executeConsoleCommand("target list");
        var var3 = installer.install();
        return new Inferior(0) {
            //private static final long b = d.a(5211155669675329941L, -3184827089624947173L, MethodHandles.lookup().lookupClass()).a(243394152416636L);

            protected long startImpl() throws ExecutionException {
                return magic(var3, () -> {
                    return ProtobufMessageFactory.handleConsoleCommand(-1l, -1, "run");
                }, true);
            }

            @Override
            protected void detachImpl() throws ExecutionException {

            }

            @Override
            protected boolean destroyImpl() throws ExecutionException {
                return false;
            }

            //return null;
        };
    }


     */
    private long magic(@NotNull GeneralCommandLine var1, @NotNull ThrowableComputable<CompositeRequest, ExecutionException> var2, boolean var3) throws ExecutionException {
        final Ref var6 = new Ref();
        LLDBDriver.ThrowIfNotValid var7 = new LLDBDriver.ThrowIfNotValid<ProtocolResponses.HandleConsoleCommand_Res>(CidrDebuggerBundle.message("lldb.launch.process.fail", new Object[0])) {
            public void consume(ProtocolResponses.HandleConsoleCommand_Res message) {
                super.consume(message);
                var splited = message.getOut().split(" ");
                var6.set(Long.parseLong(splited[1]));
            }
        };

        CompositeRequest var8 = (CompositeRequest)var2.compute();
        this.printTargetCommandLine(var1);
        this.getProtobufClient().sendMessageAndWaitForReply(var8, ProtocolResponses.HandleConsoleCommand_Res.class, var7);
        if (var3 && !var7.isValid() && "process launch failed: Locked".equals(var7.getMessage())) {
            throw new LLDBDriverException(CidrDebuggerBundle.message("debug.lldb.lockedDeviceUserMessage", new Object[]{ApplicationNamesInfo.getInstance().getProductName()}));
        } else {
            var7.throwIfNeeded();
            return (Long)var6.get();
        }
    }

}
