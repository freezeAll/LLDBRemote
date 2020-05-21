package top.berialsloth.lldbremote;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PtyCommandLine;
import com.jetbrains.cidr.ArchitectureType;
import com.jetbrains.cidr.execution.Installer;
import com.jetbrains.cidr.execution.TrivialInstaller;
import com.jetbrains.cidr.execution.RunParameters;

import com.jetbrains.cidr.execution.debugger.CidrDebuggerPathManager;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriver;
import com.jetbrains.cidr.system.HostMachine;
import org.codehaus.groovy.tools.shell.Shell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class LLDBRemoteRunParameters extends RunParameters {
    private DebuggerDriverConfiguration driver;
    private GeneralCommandLine commandLine;
    private LLDBRemoteRunConfiguration myConfig;
    public LLDBRemoteRunParameters(@NotNull DebuggerDriverConfiguration config, @NotNull GeneralCommandLine commandLine, @NotNull LLDBRemoteRunConfiguration myConfig){
        this.driver = config;
        this.commandLine = commandLine;
        this.myConfig = myConfig;

    }
    @Override
    public @NotNull Installer getInstaller() {
        return new Installer() {
            @Override
            public @NotNull GeneralCommandLine install() throws ExecutionException {

                return commandLine;
            }

            @Override
            public @NotNull File getExecutableFile() {

                //var path = lldbpath.getAbsolutePath();
                //System.out.println(path);
                //File file = new File("C:\\Users\\Administrator\\CLionProjects\\untitled1\\cmake-build-debug\\untitled1.exe");
                return new File(commandLine.getExePath());
            }
        };
    }

    @Override
    public @NotNull DebuggerDriverConfiguration getDebuggerDriverConfiguration() {
        return driver;
    }

    @Override
    public @Nullable String getArchitectureId() {
        return null;
    }

    private GeneralCommandLine createLLDBProcess()
    {

        var helprt = myConfig.getHelper();
        GeneralCommandLine commandLine = new GeneralCommandLine()
                .withExePath("%FILENAME%")
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);

        return commandLine;
    }
}
