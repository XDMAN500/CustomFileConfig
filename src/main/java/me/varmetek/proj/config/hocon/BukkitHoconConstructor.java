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
import com.typesafe.config.ConfigValueFactory;
import com.typesafe.config.ConfigValueType;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.LinkedHashMap;
import java.util.Map;

public class BukkitHoconConstructor extends BaseHoconConstructor
{

  public BukkitHoconConstructor (HoconConfiguration hoconConfiguration){
    super(hoconConfiguration);
  }

  /**
   *
   * Attempt implicit construction
   *
   * */

  @Override
  public Object constructObject(ConfigValue value){
    if(value == null) return super.constructObject(null);
    Object res = constructBukkit(value);
    if(res != null){
      return res;
    }


    return super.constructObject(value);
  }

  /**
   *
   * Attempt explicit construction
   *
   * */
  @Override
  public Object constructObject(ConfigValue value, Class<?> requested){
    if(requested == null) return constructObject(value);
    if(value == null) return super.constructObject(null,requested);

    if( ConfigurationSerializable.class.isAssignableFrom(requested)){
      Object res = constructBukkit(value);
      if(res != null){
        return res;
      }else{
        throw new HoconException(String.format("Could not construct %1$s from %2$s", requested.getCanonicalName() , value.valueType().name()));
      }
    }

    return super.constructObject(value, requested);

  }

  protected ConfigurationSerializable  constructBukkit(ConfigValue value){
    ConfigValueType valueType = value.valueType();
    if(valueType !=  ConfigValueType.OBJECT ) return null;

    Map<String,Object> raw = (Map<String,Object>) value.unwrapped();
    if(raw.get(ConfigurationSerialization.SERIALIZED_TYPE_KEY) == null) return null;

    String bukkitType = (String) raw.get(ConfigurationSerialization.SERIALIZED_TYPE_KEY);


    Map<String,Object> data = new LinkedHashMap(raw.size());
    for (Map.Entry<String,Object> member : raw.entrySet()) {
      data.put(member.getKey(), constructObject(ConfigValueFactory.fromAnyRef(member.getValue())));
    }

    try {
      return ConfigurationSerialization.deserializeObject(data);
    } catch (IllegalArgumentException ex) {
      throw new HoconException("Could not deserialize object", ex);
    }
    }


}
