package top.berialsloth.lldbremote;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.ThrowableComputable;
import com.jetbrains.cidr.ArchitectureType;
import com.jetbrains.cidr.execution.*;
import com.jetbrains.cidr.execution.debugger.backend.*;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriver;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriverException;
import com.jetbrains.cidr.execution.debugger.backend.lldb.ProtobufMessageFactory;
import com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Model;
import com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.ProtocolResponses;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Protocol.*;
import java.util.*;

public class LLDBRemoteDebuggerDriver extends LLDBDriver {

    private ArchitectureType myArchitectureType;
    private LLDBRemoteRunConfiguration projectConfiguration;
    private Handler myHandler;

    public LLDBRemoteDebuggerDriver(@NotNull Handler handler, @NotNull LLDBDriverConfiguration starter, @NotNull ArchitectureType architectureType,@NotNull LLDBRemoteRunConfiguration projectConfiguration) throws ExecutionException {
        super(handler,starter, architectureType);
        this.projectConfiguration = projectConfiguration;
        this.myArchitectureType = architectureType;
        this.myHandler = handler;
    }

    @Override
    public DebuggerDriver.Inferior loadForLaunch(@NotNull Installer installer,@NotNull String s) throws ExecutionException {
        GeneralCommandLine commandLine = installer.install();
        var platformReq = ProtobufMessageFactory.handleConsoleCommand(-1,-1,"platform select " + projectConfiguration.getRemotePlatform());
        var platformRes = new LLDBDriver.ThrowIfNotValid("");
        getProtobufClient().sendMessageAndWaitForReply(platformReq, ProtocolResponses.HandleConsoleCommand_Res.class,platformRes);
        platformRes.throwIfNeeded();
        var connectReq = ProtobufMessageFactory.handleConsoleCommand(-1,-1,"platform connect " + projectConfiguration.getLLDBInitUrl());
        var connectRes = new LLDBDriver.ThrowIfNotValid("");
        getProtobufClient().sendMessageAndWaitForReply(connectReq, ProtocolResponses.HandleConsoleCommand_Res.class,connectRes);
        platformRes.throwIfNeeded();

        var settingReq = ProtobufMessageFactory.handleConsoleCommand(-1,-1,"platform setting -w " + projectConfiguration.getRemoteWorkingDir());
        var settingRes = new LLDBDriver.ThrowIfNotValid("");
        getProtobufClient().sendMessageAndWaitForReply(settingReq, ProtocolResponses.HandleConsoleCommand_Res.class,settingRes);
        platformRes.throwIfNeeded();

        this.sendCreateTargetRequest(ProtobufMessageFactory.createTarget(installer.getExecutableFile().getPath(), myArchitectureType.getId()));

        return new Inferior(0) {

            protected long startImpl() throws ExecutionException {
                return magic(commandLine, () -> {
                    executeConsoleCommand("platform status");
                    executeConsoleCommand("target list");
                    var req = createLaunchMsg(commandLine, ".",".", ".");
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

    private long magic(@NotNull GeneralCommandLine commandLine, @NotNull ThrowableComputable<CompositeRequest, ExecutionException> inpReq, boolean var3) throws ExecutionException {
        final Ref pidMsg = new Ref();
        LLDBDriver.ThrowIfNotValid res = new LLDBDriver.ThrowIfNotValid<ProtocolResponses.Launch_Res>(CidrDebuggerBundle.message("lldb.launch.process.fail", new Object[0])) {
            public void consume(ProtocolResponses.Launch_Res message) {
                super.consume(message);
                if (this.isValid()) {
                    pidMsg.set(message.getPid());
                }
            }
        };
        CompositeRequest req = (CompositeRequest)inpReq.compute();
        this.printTargetCommandLine(commandLine);
        this.getProtobufClient().sendMessageAndWaitForReply(req, ProtocolResponses.Launch_Res.class, res);
        if (var3 && !res.isValid() && "process launch failed: Locked".equals(res.getMessage())) {
            throw new LLDBDriverException(CidrDebuggerBundle.message("debug.lldb.lockedDeviceUserMessage", new Object[]{ApplicationNamesInfo.getInstance().getProductName()}));
        } else {
            res.throwIfNeeded();
            return (Long)pidMsg.get();
        }
    }

    private void detachProcess() throws ExecutionException {
        LLDBDriver.ThrowIfNotValid res = new LLDBDriver.ThrowIfNotValid(CidrDebuggerBundle.message("lldb.detach.process.fail", new Object[0]));
        this.getProtobufClient().sendMessageAndWaitForReply(ProtobufMessageFactory.detach(), ProtocolResponses.Detach_Res.class, res);
        if (!res.isValid() && !printProcessMsg(res.getMessage())) {
            res.throwIfNeeded();
        }
        this.handleDetached();
    }

    private boolean destoryProcess() throws ExecutionException {
        Ref rtn = Ref.create(false);
        getProtobufClient().sendMessageAndWaitForReply(ProtobufMessageFactory.kill(), ProtocolResponses.Kill_Res.class, (var2x) -> {
            ProtocolResponses.CommonResponse res = var2x.getCommonResponse();
            if (res.getIsValid()) {
                rtn.set(true);
            } else {
                String errMsg = res.getErrorMessage();
                if ("process not exist".equals(errMsg)) {
                    rtn.set(false);
                }
            }
        });
        return (Boolean)rtn.get();
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

    private static com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Protocol.Launch_Req.Builder buildLaunchReq(String none, GeneralCommandLine commandLine, @Nullable String in, @Nullable String out, @Nullable String err) {
        com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Protocol.Launch_Req.Builder reqBuilder = Launch_Req.newBuilder();
        com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Model.CommandLine.Builder builder = Model.CommandLine.newBuilder();
        builder.setExePath("");
        builder.setWorkingDir("");
        Map env = commandLine.getEffectiveEnvironment();
        for (var iter: env.keySet()) {
            //Add console env
        }
        String[] paramters = commandLine.getParametersList().getArray();
        for(int i = 0; i < paramters.length; ++i) {
            builder.addParam(paramters[i]);
        }
        if (in != null) {
            builder.setStdinPath(in);
        }
        if (out != null) {
            builder.setStdoutPath(out);
        }
        if (err != null) {
            builder.setStderrPath(err);
        }
        reqBuilder.setCommandLine(builder);
        return reqBuilder;
    }

    public static CompositeRequest createLaunchMsg(GeneralCommandLine targetCommandLine, @Nullable String stdinPath, @Nullable String stdoutPath, @Nullable String stderrPath) {
        com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Protocol.CompositeRequest.Builder builder = CompositeRequest.newBuilder();
        com.jetbrains.cidr.execution.debugger.backend.lldb.auto_generated.Protocol.Launch_Req.Builder req = buildLaunchReq(targetCommandLine.getExePath(), targetCommandLine, stdinPath, stdoutPath, stderrPath);
        builder.setLaunch(req);
        return builder.build();
    }


}
