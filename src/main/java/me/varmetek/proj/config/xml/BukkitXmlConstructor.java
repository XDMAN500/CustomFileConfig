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

package me.varmetek.proj.config.xml;

import me.varmetek.proj.demo.CustomFileConfigurationPlugin;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jdom2.Content;
import org.jdom2.Element;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BukkitXmlConstructor extends BaseXmlConstructor
{




  public BukkitXmlConstructor (XmlConfiguration xmlConfiguration){
    super(xmlConfiguration);
  }

  /**
   * Attempt implicit construction
   */

  @Override
  public Object constructObject (Content value){
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
  public Object constructObject(Content value, Class<?> requested){
    if(requested == null) return constructObject(value);
    if(value == null) return super.constructObject(null,requested);


    if(ConfigurationSerializable.class.isAssignableFrom(requested)){
      Object res = constructBukkit(value);
      if(res != null){
        return res;
      }else{
        throw new XmlException(String.format("Could not construct %1$s from %2$s", requested.getCanonicalName() , value.getCType().getClass()));
      }
    }


    return super.constructObject(value, requested);

  }


  protected ConfigurationSerializable  constructBukkit(Content value){
    if(!(value instanceof  Element)) return null;
    Element root = (Element)value;
    String bukkitTag = root.getChildText("_BUKKITTYPE_");
    if(bukkitTag == null) return null;


    List<Element> direct = root.getChildren();

    Map<String,Object> data = new LinkedHashMap(direct.size());
    data.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,bukkitTag);
    for (int i = 0; i< direct.size(); i++) {
      if(!(direct.get(i) instanceof Element)) continue;

      Element element = (Element)direct.get(i);

      Object val = constructObject(element);
      data.put(element.getName(), val);
    }

    try {

      return ConfigurationSerialization.deserializeObject(data);

    } catch (RuntimeException ex) {

      throw new XmlException("Could not deserialize object", ex);
    }



  }

}
