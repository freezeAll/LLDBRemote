// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package top.berialsloth.lldbremote;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.EditorTextField;
import com.intellij.util.ui.GridBag;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfigurationSettingsEditor;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LLDBRemoteSettingsEditor extends CMakeAppRunConfigurationSettingsEditor {
    private LabeledComponent<TextFieldWithBrowseButton> lldbBinaryPath;
    private LabeledComponent<EditorTextField> initUrl;
    private LabeledComponent<ComboBox> remotePlatform;
    private LabeledComponent<EditorTextField> remoteWorkingDir;


    public LLDBRemoteSettingsEditor(Project project, @NotNull CMakeBuildConfigurationHelper cMakeBuildConfigurationHelper) {
        super(project, cMakeBuildConfigurationHelper);
    }


    @Override
    protected void resetEditorFrom(CMakeAppRunConfiguration cmakeConfig) {
        super.resetEditorFrom(cmakeConfig);
        LLDBRemoteRunConfiguration config = (LLDBRemoteRunConfiguration)cmakeConfig;
        lldbBinaryPath.getComponent().setText(config.getLLDBBinaryPath());
        initUrl.getComponent().setText(config.getLLDBInitUrl());
        remotePlatform.getComponent().setSelectedItem(config.getRemotePlatform());
        remoteWorkingDir.getComponent().setText(config.getRemoteWorkingDir());
    }

    @Override
    protected void applyEditorTo(@NotNull CMakeAppRunConfiguration  cmakeConfig) throws ConfigurationException {
        super.applyEditorTo(cmakeConfig);
        LLDBRemoteRunConfiguration config = (LLDBRemoteRunConfiguration)cmakeConfig;
        config.setLLDBBinaryPath(lldbBinaryPath.getComponent().getText());
        config.setLLDBInitUrl(initUrl.getComponent().getText());
        config.setRemotePlatform(remotePlatform.getComponent().getSelectedItem().toString());
        config.setRemoteWorkingDir(remoteWorkingDir.getComponent().getText());
    }


    @NotNull
    protected void createEditorInner(JPanel panel, GridBag gridBag)
    {
        super.createEditorInner(panel,gridBag);
        lldbBinaryPath = new LabeledComponent<>();
        lldbBinaryPath.setLabelLocation("West");
        lldbBinaryPath.setText("LLDB Binary Path");
        lldbBinaryPath.setComponent(new TextFieldWithBrowseButton());
        initUrl = new LabeledComponent<>();
        initUrl.setText("'target connect'arg");
        initUrl.setLabelLocation("West");
        initUrl.setComponent(new EditorTextField());
        remotePlatform = new LabeledComponent<>();
        remotePlatform.setLabelLocation("West");
        remotePlatform.setText("'platform select' arg");
        remotePlatform.setComponent(new ComboBox());
        remotePlatform.getComponent().addItem("remote-windows");
        remoteWorkingDir = new LabeledComponent<>();
        remoteWorkingDir.setText("'platform setting -w' arg");
        remoteWorkingDir.setLabelLocation("West");
        remoteWorkingDir.setComponent(new EditorTextField());
        panel.add(lldbBinaryPath,gridBag.nextLine());
        panel.add(initUrl,gridBag.nextLine());
        panel.add(remotePlatform,gridBag.nextLine());
        panel.add(remoteWorkingDir,gridBag.nextLine());
    }


}