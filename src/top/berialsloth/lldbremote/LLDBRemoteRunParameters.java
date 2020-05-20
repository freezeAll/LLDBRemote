package top.berialsloth.lldbremote;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PtyCommandLine;
import com.jetbrains.cidr.ArchitectureType;
import com.jetbrains.cidr.execution.Installer;
import com.jetbrains.cidr.execution.RunParameters;
import com.jetbrains.cidr.execution.debugger.CidrDebuggerPathManager;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriver;
import org.codehaus.groovy.tools.shell.Shell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class LLDBRemoteRunParameters extends RunParameters {
    private DebuggerDriverConfiguration driver;
    private CommandLineState commandLineState;
    private LLDBRemoteRunConfiguration myConfig;
    public LLDBRemoteRunParameters(@NotNull DebuggerDriverConfiguration config,@NotNull CommandLineState commandLineState,@NotNull LLDBRemoteRunConfiguration myConfig){
        this.driver = config;
        this.commandLineState = commandLineState;
        this.myConfig = myConfig;

    }
    @Override
    public @NotNull Installer getInstaller() {
        return new Installer() {
            @Override
            public @NotNull GeneralCommandLine install() throws ExecutionException {

                return createLLDBProcess();
            }

            @Override
            public @NotNull File getExecutableFile() {

                //var path = lldbpath.getAbsolutePath();
                //System.out.println(path);
                File file = new File("C:\\Users\\Administrator\\CLionProjects\\untitled1\\cmake-build-debug\\untitled1.exe");
                return file;
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

        GeneralCommandLine commandLine = new GeneralCommandLine()
                .withExePath(myConfig.getLLDBBinaryPath().toString())
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);

        return commandLine;
    }
}
