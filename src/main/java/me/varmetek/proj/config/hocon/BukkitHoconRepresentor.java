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

import com.typesafe.config.ConfigValue;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.LinkedHashMap;
import java.util.Map;

public class BukkitHoconRepresentor extends BaseHoconRepresentor
{
  public BukkitHoconRepresentor (HoconConfiguration hoconConfiguration){
    super(hoconConfiguration);
  }

  @Override
  public ConfigValue representData(Object data){
    if(data instanceof ConfigurationSection){
      return representData(((ConfigurationSection)data).getValues(false));

    }

    if(data instanceof ConfigurationSerializable){
      ConfigurationSerializable cs = (ConfigurationSerializable)data;
      Map<String, Object> values = new LinkedHashMap();
      values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(cs.getClass()));
      values.putAll(cs.serialize());
      return representData(values);
    }

    return super.representData(data);


  }
}
