// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package  top.berialsloth.lldbremote;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationSingletonPolicy;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NotNullLazyValue;
import com.jetbrains.cidr.cpp.execution.CMakeRunConfigurationType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LLDBRemoteRunConfigurationType extends CMakeRunConfigurationType {
  @NotNull
  private static final String FACTORY_ID = "top.berial.lldbremote.config.factory";
  @NotNull
  public static final String TYPE_ID = "top.berial.lldbremote.config.type";
  @NotNull
  public static final NotNullLazyValue<Icon> ICON = new NotNullLazyValue<Icon>() {
    @Override
    protected Icon compute() {
      final Icon icon = IconLoader.findIcon("ocd_run.png", LLDBRemoteRunConfigurationType.class);
      return icon == null ? AllIcons.Icon : icon;
    }

  };

  @NotNull
  @Override
  public String getDisplayName() {
    return "LLDB Remote Debug";
  }

  @Override
  public LLDBRemoteSettingsEditor createEditor(@NotNull Project project) {
    return new LLDBRemoteSettingsEditor(project, getHelper(project));
  }

  public LLDBRemoteRunConfigurationType() {
    super(TYPE_ID,
            FACTORY_ID,
            "OpenOCD Download & Run",
            "Downloads and Runs Embedded Applications using OpenOCD",
            ICON);

    factory = new ConfigurationFactory(this) {
      @NotNull
      @Override
      public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new LLDBRemoteRunConfiguration(project, factory, "");
      }

      @NotNull
      @Override
      public RunConfigurationSingletonPolicy getSingletonPolicy() {
        return RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY;
      }

      @NotNull
      @Override
      public String getId() {
        return FACTORY_ID;
      }
    };
  }


  private final ConfigurationFactory factory;

  @Override
  public Icon getIcon() {
    return AllIcons.General.Information;
  }
  @NotNull
  @Override
  protected LLDBRemoteRunConfiguration createRunConfiguration(@NotNull Project project,
                                                        @NotNull ConfigurationFactory configurationFactory) {
    return new LLDBRemoteRunConfiguration(project, factory, "");
  }
}
