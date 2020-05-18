package top.berialsloth.lldbremote;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;

import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.execution.CidrCommandLineState;
import com.jetbrains.cidr.execution.CidrExecutableDataHolder;
import org.antlr.v4.runtime.misc.NotNull;
import org.jdom.Element;

import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class LLDBRemoteConfiguration extends CMakeAppRunConfiguration implements CidrExecutableDataHolder {
    /*
    public static final int DEF_GDB_PORT = 3333;
    public static final int DEF_TELNET_PORT = 4444;
    private static final String ATTR_GDB_PORT = "gdb-port";
    private static final String ATTR_TELNET_PORT = "telnet-port";
    private static final String ATTR_BOARD_CONFIG = "board-config";
    public static final String ATTR_RESET_TYPE = "reset-type";
    public static final String ATTR_DOWNLOAD_TYPE = "download-type";
    public static final ResetType DEFAULT_RESET = ResetType.INIT;
    public static final String TAG_OPENOCD = "openocd";
    private int gdbPort = DEF_GDB_PORT;
    private int telnetPort = DEF_TELNET_PORT;
    private String boardConfigFile;
    private DownloadType downloadType = DownloadType.ALWAYS;
    private ResetType resetType = DEFAULT_RESET;
*/
    private static final String lldbRemoteTag = "LLDB Remote";
//LLDB 二进制文件路径
    private static final String attrLLDBBinaryPath = "LLDB Binary Path";
    private String LLDBBinaryPath = "";
    public String getLLDBBinaryPath() {
        return LLDBBinaryPath;
    }
    public void setLldbBinaryPath(String inp) {
        LLDBBinaryPath = inp;
    }

    //LLDB 连接URL
    private static final String attrLLDBInitUrl = "'platform connect' args";
    private String LLDBInitUrl = "";
    public String getLLDBInitUrl() {
        return LLDBInitUrl;
    }
    public void setLLDBInitUrl(String inp) {
        LLDBInitUrl = inp;
    }

    //LLDB远程工作目录
    private static final String attrRemoteWorkingDir = "Remote Working Directory";
    private String remoteWorkingDir = "";
    public String getRemoteWorkingDir() {
        return remoteWorkingDir;
    }
    public void setRemoteWorkingDir(String inp) {
        remoteWorkingDir = inp;
    }

    //LLDB将要连接的远程平台
    private static final String attrRemotePlatform = "Remote Platform";
    private RemotePlatform remotePlatform;
    public RemotePlatform getRemotePlatform() {
        return remotePlatform;
    }
    public void setRemotePlatform(RemotePlatform inp) {
        remotePlatform = inp;
    }
    public enum RemotePlatform
    {
        windows;

        @Override
        public String toString() {
            return toBeautyString(super.toString());
        }
    }

    /*
    public enum DownloadType {

        ALWAYS,
        UPDATED_ONLY,
        NONE;

        @Override
        public String toString() {
            return toBeautyString(super.toString());
        }
    }
*/
    public static String toBeautyString(String obj) {
        return StringUtil.toTitleCase(obj.toLowerCase().replace("_", " "));
    }

    public enum ResetType {
        RUN("init;reset run;"),
        INIT("init;reset init;"),
        HALT("init;reset halt"),
        NONE("");

        @Override
        public String toString() {
            return toBeautyString(super.toString());
        }

        ResetType(String command) {
            this.command = command;
        }

        private final String command;

        public final String getCommand() {
            return command;
        }

    }


    @SuppressWarnings("WeakerAccess")
    public LLDBRemoteConfiguration(Project project, ConfigurationFactory configurationFactory, String targetName) {
        super(project, configurationFactory, targetName);
    }
/*
    @Nullable
    @Override
    public CidrCommandLineState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return new CidrCommandLineState(environment, new OpenOcdLauncher(this));
    }
*/


    @Override
    public void readExternal(@NotNull Element parentElement) throws InvalidDataException {
        super.readExternal(parentElement);
        Element element = parentElement.getChild(lldbRemoteTag);
        if(element!=null) {
            LLDBBinaryPath = element.getAttributeValue(attrLLDBBinaryPath);
            LLDBInitUrl = element.getAttributeValue(attrLLDBInitUrl);
            remoteWorkingDir = element.getAttributeValue( attrRemoteWorkingDir);
            remotePlatform = (RemotePlatform)Enum.valueOf(remotePlatform.getDeclaringClass(),element.getAttributeValue( attrRemotePlatform));
        }
    }

    private int readIntAttr(@NotNull Element element, String name, int def) {
        String s = element.getAttributeValue(name);
        if (StringUtil.isEmpty(s)) return def;
        return Integer.parseUnsignedInt(s);
    }

    private <T extends Enum> T readEnumAttr(@NotNull Element element, String name, T def) {
        String s = element.getAttributeValue(name);
        if (StringUtil.isEmpty(s)) return def;
        try {
            //noinspection unchecked
            return (T) Enum.valueOf(def.getDeclaringClass(), s);
        } catch (Throwable t) {
            return def;
        }
    }

    @Override
    public void writeExternal(@NotNull Element parentElement) throws WriteExternalException {
        super.writeExternal(parentElement);
        Element element = new Element(lldbRemoteTag);
        parentElement.addContent(element);
        element.setAttribute(attrLLDBBinaryPath, LLDBBinaryPath);
        element.setAttribute(attrLLDBInitUrl, LLDBInitUrl);
        element.setAttribute(attrRemoteWorkingDir, remoteWorkingDir);
        element.setAttribute(attrRemotePlatform, remotePlatform.name());
    }
/*
    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();
        checkPort(gdbPort);
        checkPort(telnetPort);
        if (gdbPort == telnetPort) {
            throw new RuntimeConfigurationException("Port values should be different");
        }
        if (StringUtil.isEmpty(boardConfigFile)) {
            throw new RuntimeConfigurationException("Board config file is not defined");
        }
    }
*/
    private void checkPort(int port) throws RuntimeConfigurationException {
        if (port <= 1024 || port > 65535)
            throw new RuntimeConfigurationException("Port value must be in the range [1024...65535]");
    }

}