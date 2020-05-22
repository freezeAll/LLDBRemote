package top.berialsloth.lldbremote;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.BaseProcessHandler;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Consumer;
import com.intellij.xdebugger.impl.settings.DebuggerConfigurable;
import com.jetbrains.cidr.ArchitectureType;
import com.jetbrains.cidr.execution.*;
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
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.util.*;

import static com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBBinUrlProvider.lldb;

public class LLDBRemoteDebuggerDriver extends LLDBDriver {
    private ArchitectureType myArchitectureType;
    private LLDBRemoteRunConfiguration projectConfiguration;
    private Handler myHandler;
    private OutputStream processInput;
    public LLDBRemoteDebuggerDriver(@NotNull Handler handler, @NotNull LLDBDriverConfiguration starter, @NotNull ArchitectureType architectureType,@NotNull LLDBRemoteRunConfiguration projectConfiguration) throws ExecutionException {
        super(handler,starter, architectureType);
        this.projectConfiguration = projectConfiguration;
        this.myArchitectureType = architectureType;
        this.myHandler = handler;
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
*/
    @Override
    public DebuggerDriver.Inferior loadForLaunch(@NotNull Installer installer,@NotNull String s) throws ExecutionException {
        //return super.loadForLaunch(installer,s);
        GeneralCommandLine var3 = installer.install();
        var platformReq = ProtobufMessageFactory.handleConsoleCommand(-1,-1,"platform select " + projectConfiguration.getRemotePlatform());
        var platformRes = new LLDBDriver.ThrowIfNotValid("");
        getProtobufClient().sendMessageAndWaitForReply(platformReq, ProtocolResponses.HandleConsoleCommand_Res.class,platformRes);
        platformRes.throwIfNeeded();
        System.out.println( platformRes.getMessage());
        var connectReq = ProtobufMessageFactory.handleConsoleCommand(-1,-1,"platform connect " + projectConfiguration.getLLDBInitUrl());
        var connectRes = new LLDBDriver.ThrowIfNotValid("");
        getProtobufClient().sendMessageAndWaitForReply(connectReq, ProtocolResponses.HandleConsoleCommand_Res.class,connectRes);
        platformRes.throwIfNeeded();
        System.out.println( connectRes.getMessage());

        var settingReq = ProtobufMessageFactory.handleConsoleCommand(-1,-1,"platform setting -w " + projectConfiguration.getRemoteWorkingDir());
        var settingRes = new LLDBDriver.ThrowIfNotValid("");
        getProtobufClient().sendMessageAndWaitForReply(settingReq, ProtocolResponses.HandleConsoleCommand_Res.class,settingRes);
        platformRes.throwIfNeeded();
        System.out.println( settingRes.getMessage());

        String var4 = myArchitectureType.getId();
        this.sendCreateTargetRequest(ProtobufMessageFactory.createTarget(installer.getExecutableFile().getPath(), var4));

        return new Inferior(0) {

            protected long startImpl() throws ExecutionException {
                return magic(var3, () -> {
                    String var4 = null;
                    String var5 = null;
                    if (false) {
                        var reader = initReaders(LocalHost.INSTANCE, var3, !SystemInfo.isWindows);
                        reader.getOutFileAbsolutePath();
                        reader.getErrFileAbsolutePath();
                    }

                    File var10 = var3.getInputFile();

                    String var7;

                    try {
                        if (var10 != null) {
                            if (!var10.isFile() || !var10.canRead()) {
                                throw new FileNotFoundException(CidrDebuggerBundle.message("debug.driver.cannotReadInputFile", new Object[]{var10.getPath()}));
                            }

                            var7 = var10.getPath();
                        } else if (SystemInfo.isWindows) {
                            WinPipe var8 = WinPipe.createOutboundPipe("stdin");
                            processInput = var8.getOutputStream();
                            var7 = var8.getName();
                        } else {
                            Pty var11 = new Pty(true);
                            processInput = var11.getOutputStream();
                            var7 = var11.getSlaveName();
                        }
                    } catch (IOException var9) {
                        CidrDebuggerLog.LOG.error(var9);
                        throw new LLDBDriverException(CidrDebuggerBundle.message("debug.driver.cannotCreatePipe", new Object[]{var9.getMessage()}));
                    }
                    executeConsoleCommand("platform status");
                    executeConsoleCommand("target list");
                    var req = slaunch(var3, ".",".", ".");

                    return req;
                }, false);
            }

            protected void detachImpl() throws ExecutionException {
                detachProcess();
            }

            protected boolean destroyImpl() throws ExecutionException {
                return destoryProcess();
            }
        };


    }



    private long magic(@NotNull GeneralCommandLine var1, @NotNull ThrowableComputable<CompositeRequest, ExecutionException> var2, boolean var3) throws ExecutionException {


        final Ref var6 = new Ref();
        LLDBDriver.ThrowIfNotValid var7 = new LLDBDriver.ThrowIfNotValid<ProtocolResponses.Launch_Res>(CidrDebuggerBundle.message("lldb.launch.process.fail", new Object[0])) {
            public void consume(ProtocolResponses.Launch_Res message) {
                super.consume(message);
                if (this.isValid()) {
                    var6.set(message.getPid());
                }

            }
        };
        CompositeRequest var8 = (CompositeRequest)var2.compute();
     //   var8[commandLine_]
        this.printTargetCommandLine(var1);
        this.getProtobufClient().sendMessageAndWaitForReply(var8, ProtocolResponses.Launch_Res.class, var7);
        if (var3 && !var7.isValid() && "process launch failed: Locked".equals(var7.getMessage())) {
            throw new LLDBDriverException(CidrDebuggerBundle.message("debug.lldb.lockedDeviceUserMessage", new Object[]{ApplicationNamesInfo.getInstance().getProductName()}));
        } else {
            var7.throwIfNeeded();
            return (Long)var6.get();
        }
    }
    private void detachProcess() throws ExecutionException {
        LLDBDriver.ThrowIfNotValid var3 = new LLDBDriver.ThrowIfNotValid(CidrDebuggerBundle.message("lldb.detach.process.fail", new Object[0]));
        this.getProtobufClient().sendMessageAndWaitForReply(ProtobufMessageFactory.detach(), ProtocolResponses.Detach_Res.class, var3);
        if (!var3.isValid() && !printProcessMsg(var3.getMessage())) {
            var3.throwIfNeeded();
        }

        this.handleDetached();
    }

    private boolean destoryProcess() throws ExecutionException {
        Ref var1 = Ref.create();
        Ref var2 = Ref.create(false);
        getProtobufClient().sendMessageAndWaitForReply(ProtobufMessageFactory.kill(), ProtocolResponses.Kill_Res.class, (var2x) -> {
            ProtocolResponses.CommonResponse var5 = var2x.getCommonResponse();
            if (var5.getIsValid()) {
                var2.set(true);
            } else {
                String var6 = var5.getErrorMessage();
                if ("process not exist".equals(var6)) {
                    var2.set(false);
                } else {
                    if (StringUtil.isEmptyOrSpaces(var6)) {
                        var6 = CidrDebuggerBundle.message("lldb.abort.process.fail", new Object[0]);
                    }

                    var1.set(new LLDBDriverException(var6));
                }
            }

        });
        return (Boolean)var2.get();
    }
    private static boolean printProcessMsg(@Nullable String var0) {
        if (var0 == null) {
            return false;
        } else if ("Sending disconnect packet failed.".equals(var0)) {
            return true;
        } else {
            String var3 = "error: process \\d* in state = exited, but cannot detach it in this state.";
            return var0.matches(var3);
        }
    }

    private static com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Protocol.Launch_Req.Builder buildLaunchReq(String var0, GeneralCommandLine var1, @Nullable String var2, @Nullable String var3, @Nullable String var4) {
        com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Protocol.Launch_Req.Builder var5 = Launch_Req.newBuilder();
        com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Model.CommandLine.Builder var6 = Model.CommandLine.newBuilder();
        var6.setExePath("");
        var6.setWorkingDir("");
        Map var7 = var1.getEffectiveEnvironment();
        Iterator var8 = var7.keySet().iterator();

        while(var8.hasNext()) {
            String var9 = (String)var8.next();
            //var6.addEnv(Model.EnvParam.newBuilder().setName(var9).setValue((String)var7.get(var9)));
        }

        String[] var12 = var1.getParametersList().getArray();
        int var13 = var12.length;

        for(int var10 = 0; var10 < var13; ++var10) {
            String var11 = var12[var10];
            var6.addParam(var11);
        }

        if (var2 != null) {
            var6.setStdinPath(var2);
        }

        if (var3 != null) {
            var6.setStdoutPath(var3);
        }

        if (var4 != null) {
            var6.setStderrPath(var4);
        }

        var5.setCommandLine(var6);
        return var5;
    }

    public static CompositeRequest slaunch(GeneralCommandLine targetCommandLine, @Nullable String stdinPath, @Nullable String stdoutPath, @Nullable String stderrPath) {
        com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Protocol.CompositeRequest.Builder var4 = CompositeRequest.newBuilder();
        com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Protocol.Launch_Req.Builder var5 = buildLaunchReq(targetCommandLine.getExePath(), targetCommandLine, stdinPath, stdoutPath, stderrPath);
        var4.setLaunch(var5);
        return var4.build();
    }


}
