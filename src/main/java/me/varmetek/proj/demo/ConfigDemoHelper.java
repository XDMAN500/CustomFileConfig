/*
 * Copyright (c) 2018 Varmetek - MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package me.varmetek.proj.demo;

import com.google.common.base.Preconditions;
import me.varmetek.proj.config.util.ConfigUtility;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public final class ConfigDemoHelper{
  private ConfigurationType<?> type;
  private FileConfiguration config;


  private void checkActive(){
    if(!isActive()) throw new IllegalStateException("ConfgDemoHelper must be activated first");
  }

  public boolean hasChosen(){
    return type != null;
  }

  public boolean isConfigLoaded(){
    return config != null;
  }

  public boolean isActive(){
      return hasChosen() && isConfigLoaded();
  }


  public ConfigurationType getConfigType(){
    hasChosen();
    return type;
  }

  public FileConfiguration getConfigFile(){
    checkActive();
    return config;
  }

  public void selectType(ConfigurationType<?> ty){
    Preconditions.checkNotNull(ty,"Configuration type cannot be null");
    this.type = ty;
    this.config = null;

  }

  public  void selectYaml() {
    selectType(ConfigurationType.YAML);
  }

  public void selectHjson(){
    selectType(ConfigurationType.HJSON);

  }
  public void selectGson(){

    selectType(ConfigurationType.GSON);
  }

  public void selectHocon(){

    selectType(ConfigurationType.HOCON);
  }

  public void selectXml(){

    selectType(ConfigurationType.XML);
  }

  public void loadConfig() throws IOException, InvalidConfigurationException{
    if(!hasChosen()) throw new IllegalStateException("Config type not yet selected");
    this.config = this.type.createConfiguration();
    this.type.loadConfigurationFile(config);

  }

  /***
   * Copies the content from the currently loaded configuration to the selected configuration type
   * All information present in the configuration being copied to will have its original information wiped effective resulting in a complete overwite.
   *
   * @return  the config receiving the copied information
   * */

  public FileConfiguration copyTo(ConfigurationType ty) throws IOException, InvalidConfigurationException{
    if(!hasChosen()) new IllegalArgumentException();
    if(!isActive()) new IllegalStateException();

    FileConfiguration toConfig = ty.createConfiguration();
    ty.loadConfigurationFile(toConfig);

    ConfigUtility.copyConfig(this.config, toConfig);
    return toConfig;

  }
  /***
   * TCopies the content to the currently loaded configuration from the selected configuration type
   * All information present in the currently loaded configuration will have its original information wiped effective resulting in a complete overwite.
   *
   * @return  the config supplying the copied information
   * */
  public FileConfiguration copyFrom(ConfigurationType ty) throws IOException, InvalidConfigurationException{
    if(ty == null) new IllegalArgumentException();
    if(!hasChosen()) new IllegalStateException();

     FileConfiguration fromConfig = ty.createConfiguration();
     ty.loadConfigurationFile(fromConfig);

    ConfigUtility.copyConfig(fromConfig, this.config);
      return fromConfig;
  }




  public void saveConfig() throws IOException{
    checkActive();
    type.saveConfigurationFile(config);
  }

  public void resetConfig(){
    if(!hasChosen()) throw new IllegalStateException("Config type not yet selected");
    type.resetConfig();
    config = this.type.createConfiguration();

  }
}
