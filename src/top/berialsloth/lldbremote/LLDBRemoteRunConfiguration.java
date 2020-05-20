// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package  top.berialsloth.lldbremote;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.StoredProperty;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.execution.TestConfigurationType;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import kotlin.Pair;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.jetbrains.cidr.execution.CidrRunConfiguration;
import com.jetbrains.cidr.execution.CidrCommandLineState;
import com.jetbrains.cidr.execution.CidrExecutableDataHolder;

public class LLDBRemoteRunConfiguration extends CMakeAppRunConfiguration
        implements CidrExecutableDataHolder{
  protected LLDBRemoteRunConfiguration(Project project, ConfigurationFactory factory, String name) {
    super(project, factory, name);

    var opt = getOptions();
  }

  private static final String lldbRemoteTag = "LLDB_Remote";
  //private StoredProperty<String> lldbRemoteTag = RunConfigurationOptions.S("").provideDelegate(this, "scriptName");
  private Pair<String,String> LLDBBinaryPath = new Pair("LLDBBinaryPath","");
  private Pair<String,String> remoteWorkingDir = new Pair("remoteWorkingDir","");
  private Pair<String,String> LLDBInitUrl = new Pair("LLDBInitUrl","");
  private Pair<String,String> remotePlatform = new Pair("remotePlatform","");

  @Override
  public void readExternal(@NotNull Element parentElement) throws InvalidDataException {
    super.readExternal(parentElement);
    Element element = parentElement.getChild(lldbRemoteTag);
    if(element!=null) {
      LLDBBinaryPath = new Pair(LLDBBinaryPath.getFirst(),readAttr(element,LLDBBinaryPath.getFirst()));
      LLDBInitUrl = new Pair(LLDBInitUrl.getFirst(),readAttr(element,LLDBInitUrl.getFirst()));
      remotePlatform = new Pair(remotePlatform.getFirst(),readAttr(element,remotePlatform.getFirst()));
      remoteWorkingDir = new Pair(remoteWorkingDir.getFirst(),readAttr(element,remoteWorkingDir.getFirst()));

    }
  }

  String readAttr(@NotNull Element parentElement,String attr)
  {
    return parentElement.getAttributeValue(attr);
  }

  public String getLLDBBinaryPath() {
    return LLDBBinaryPath.getSecond();
  }

  public void setLLDBBinaryPath(String inp) {
    LLDBBinaryPath = new Pair(LLDBBinaryPath.getFirst(),inp);
  }
  public String getLLDBInitUrl() {
    return LLDBInitUrl.getSecond();
  }

  public void setLLDBInitUrl(String inp) {
    LLDBInitUrl = new Pair(LLDBInitUrl.getFirst(),inp);
  }

  public String getRemotePlatform() {
    return remotePlatform.getSecond();
  }

  public void setRemotePlatform(String inp) {
    remotePlatform = new Pair(remotePlatform.getFirst(),inp);
  }

  public String getRemoteWorkingDir() {
    return remoteWorkingDir.getSecond();
  }

  public void setRemoteWorkingDir(String inp) {
    remoteWorkingDir = new Pair(remoteWorkingDir.getFirst(),inp);
  }


  @NotNull
  @Override
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new LLDBRemoteSettingsEditor(getProject(),getHelper());
  }

  @Override
  public void checkConfiguration() {
  }

  @Override
  public void writeExternal(@NotNull Element parentElement) throws WriteExternalException {
    super.writeExternal(parentElement);
    Element element = new Element(lldbRemoteTag);
    parentElement.addContent(element);
    element.setAttribute(LLDBBinaryPath.getFirst(), LLDBBinaryPath.getSecond());
    element.setAttribute(LLDBInitUrl.getFirst(), LLDBInitUrl.getSecond());
    element.setAttribute(remoteWorkingDir.getFirst(), remoteWorkingDir.getSecond());
    element.setAttribute(remotePlatform.getFirst(), remotePlatform.getSecond());
  }
  @Nullable
  @Override
  public CidrCommandLineState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {

    return new CidrCommandLineState(environment, new LLDBRemoteLauncher(this));
  }
}
