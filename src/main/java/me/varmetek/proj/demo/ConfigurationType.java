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

import me.varmetek.proj.config.gson.GsonConfiguration;
import me.varmetek.proj.config.hjson.HjsonConfiguration;
import me.varmetek.proj.config.hocon.HoconConfiguration;
import me.varmetek.proj.config.xml.XmlConfiguration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class ConfigurationType<D extends FileConfiguration>
{


  public static final HjsonType HJSON = new HjsonType();
  public static final GsonType GSON = new GsonType();
  public static final YamlType YAML = new YamlType();
  public static final HoconType HOCON = new HoconType();
  public static final XmlType XML = new XmlType();

  private final String fileResource;
  private final String name;
  private final String description;

  protected ConfigurationType(String name, String fileResorce, String description){
    this.name = name;
    this.fileResource = fileResorce;
    this.description = description;

  }

  public String getName(){
    return name;
  }

  public String getFileResource (){
    return fileResource;
  }

  public String getDescription (){
    return description;
  }

  public abstract boolean isThisType(FileConfiguration config);

  public  final void loadConfigurationFile(FileConfiguration config) throws IOException, InvalidConfigurationException{
    CustomFileConfigurationPlugin plugin = CustomFileConfigurationPlugin.INSTANCE;
    plugin.saveResource(getFileResource(), false);
    File file = new File(plugin.getDataFolder(), getFileResource());
    config.load(file);
  }

  public abstract D createConfiguration();

  public  void saveConfigurationFile(FileConfiguration config) throws IOException{
    CustomFileConfigurationPlugin plugin = CustomFileConfigurationPlugin.INSTANCE;
    config.save(new File(plugin.getDataFolder(),getFileResource()));
  }

  public void resetConfig(){
    CustomFileConfigurationPlugin plugin = CustomFileConfigurationPlugin.INSTANCE;
    plugin.saveResource(getFileResource(), true);
  }




  public static class HjsonType extends ConfigurationType<HjsonConfiguration>
  {

    protected HjsonType (){
      super("Hjson", "playground.hjson", "The Hjson Config type. Visit https://hjson.org for more details");
    }

    @Override
    public boolean isThisType (FileConfiguration config){
      return config != null && config instanceof HjsonConfiguration;
    }



    @Override
    public HjsonConfiguration createConfiguration (){
      return new HjsonConfiguration();
    }




  }


  public static class GsonType extends ConfigurationType<GsonConfiguration>
  {

    protected GsonType (){
      super("Gson", "playground.gson", "The Gson Config type. Visit github.com/google/gson for more details");
    }

    @Override
    public boolean isThisType (FileConfiguration config){
      return config != null && config instanceof GsonConfiguration;
    }

    @Override
    public GsonConfiguration createConfiguration (){
      return new GsonConfiguration();
    }


  }

  public static class YamlType extends ConfigurationType<YamlConfiguration>
  {

    protected YamlType (){
      super("Yaml", "playground.yml", "the default bukkit config type");
    }

    @Override
    public boolean isThisType (FileConfiguration config){
      return config != null && config instanceof YamlConfiguration;
    }

    @Override
    public YamlConfiguration createConfiguration (){
      return new YamlConfiguration();
    }


  }

  public static class HoconType extends ConfigurationType<HoconConfiguration>
  {

    protected HoconType (){
      super("Hocon", "playground.conf", "The Hocon config type. Visit github.com/lightbend/config for more details");
    }

    @Override
    public boolean isThisType (FileConfiguration config){
      return config != null && config instanceof HoconConfiguration;
    }

    @Override
    public HoconConfiguration createConfiguration (){
      return new HoconConfiguration();
    }


  }

  public static class XmlType extends ConfigurationType<XmlConfiguration>
  {

    protected XmlType (){
      super("Xml", "playground.xml", "The Xml config type. Using the JDOM Library. Compliant with http://yaml.org/xml. More information at www.jdom.org ");
    }

    @Override
    public boolean isThisType (FileConfiguration config){
      return config != null && config instanceof XmlConfiguration;
    }

    @Override
    public XmlConfiguration createConfiguration (){
      return new XmlConfiguration();
    }


  }
}
