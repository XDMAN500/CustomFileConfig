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

package me.varmetek.proj.config.hocon;


import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigRenderOptions;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public  class HoconConfiguration extends FileConfiguration
{

  protected static final ConfigParseOptions  parseOptions = ConfigParseOptions.defaults();
  protected static final ConfigRenderOptions renderOptions = ConfigRenderOptions.defaults().setFormatted(true).setOriginComments(false).setJson(false);

  public static HoconConfiguration loadConfiguration(File file) {
    Validate.notNull(file, "File cannot be null");
    HoconConfiguration config = new HoconConfiguration();

    try {
      config.load(file);
    } catch (FileNotFoundException var3) {
      ;
    } catch (IOException var4) {
      Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, var4);
    } catch (InvalidConfigurationException var5) {
      Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, var5);
    }

    return config;
  }

  public static HoconConfiguration loadConfiguration(Reader reader) {
    Validate.notNull(reader, "Stream cannot be null");
    HoconConfiguration config = new HoconConfiguration();

    try {
      config.load(reader);
    } catch (IOException var3) {
      Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", var3);
    } catch (InvalidConfigurationException var4) {
      Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", var4);
    }

    return config;
  }

  ///////////////////////////////////////////////////////
  protected BukkitHoconConstructor constructor = new BukkitHoconConstructor(this);
  protected BukkitHoconRepresentor representor = new BukkitHoconRepresentor(this);



  @Override
  public String saveToString (){
    return representor.representData(this.getValues(false)).render(renderOptions);
  }

  @Override
  public void loadFromString (String contents) throws InvalidConfigurationException{
    Validate.notNull(contents, "Contents cannot be null");
    Map<?,?> root;

    try {

      root =  (Map<?,?>)constructor.constructObject(ConfigFactory.parseString(contents,parseOptions).root()) ;;
    } catch (ConfigException var4) {
      throw new InvalidConfigurationException(var4);
    } catch (ClassCastException var5) {
      throw new InvalidConfigurationException("Top level is not a map",var5);

    }

    String header = this.parseHeader(contents);
    if (header.length() > 0) {
      this.options().header(header);
    }

    if (root != null) {
      convertMapsToSections(root, this);
    }
  }

  @Override
  protected String buildHeader (){
    return null;
  }

  @Override
  public HoconConfigurationOptions options() {
    if (this.options == null) {
      this.options = new HoconConfigurationOptions(this);
    }

    return (HoconConfigurationOptions)this.options;
  }

  protected void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {
    Iterator var3 = input.entrySet().iterator();

    while(var3.hasNext()) {
      Map.Entry<?, ?> entry = (Map.Entry)var3.next();
      String key = entry.getKey().toString();
      Object value = entry.getValue();
      if (value instanceof Map) {
        this.convertMapsToSections((Map)value, section.createSection(key));
      } else {
        section.set(key, value);
      }
    }

  }

  protected String parseHeader(String input) {
    //No comments in json standard
    return "";

  }


}
