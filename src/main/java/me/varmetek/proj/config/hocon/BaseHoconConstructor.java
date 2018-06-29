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

import com.google.common.base.Preconditions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import com.typesafe.config.ConfigValueType;
import me.varmetek.proj.config.util.ConfigUtility;

import java.util.*;

public class BaseHoconConstructor
{

  protected HoconConfiguration configuration;
  protected HoconConfigurationOptions options;

  public BaseHoconConstructor (HoconConfiguration hoconConfiguration){
    this.configuration = Preconditions.checkNotNull(hoconConfiguration);
    this.options = configuration.options();
  }

  /**
   * Attempt implicit construction
   */

  public Object constructObject (ConfigValue value){
    if (value == null || value.valueType() == ConfigValueType.NULL){
      return ConfigValueFactory.fromAnyRef(null);

    }

    ConfigValueType valueType = value.valueType();
    switch (valueType) {
      case BOOLEAN:
        return value.unwrapped();
      case NUMBER:
        return ((Number) value.unwrapped()).doubleValue();
      case STRING: {
        final String vl = (String) value.unwrapped();
        final String intNotationSuffix = options.getIntegerTag();
        if (vl.endsWith(intNotationSuffix)){
          String toParse = vl.substring(0, vl.length() - intNotationSuffix.length());

          try {
            return Integer.parseInt(toParse);
          } catch (NumberFormatException ex) {}
        }

        return vl;
      }
      case LIST:
        return constructList(value,null);
      case OBJECT:
      return constructMap(value);

    }


    throw new HoconException("Could not implicitly construct " + value);


  }


  /**
   * Attempt explicit construction
   */
  public Object constructObject (ConfigValue value, Class<?> requested){
    if(requested == null) return constructObject(value);

    if (value == null || value.valueType() == ConfigValueType.NULL){
      return null;

    }

    ConfigValueType valueType = value.valueType();

    switch (valueType)

    {

      case BOOLEAN: {
        if (requested == boolean.class || requested == Boolean.class){
          return value.unwrapped();
        }
      }
      break;
      case NUMBER: {
        if (ConfigUtility.requestIsNumber(requested)){
          return value.unwrapped();
        }
      }
      break;
      case STRING:
        if (requested == String.class){
          return value.unwrapped();
        } else if (ConfigUtility.requestIsNumber(requested)){
          String vl = (String) value.unwrapped();
          final String intNotationSuffix = options.getIntegerTag();
          if (vl.endsWith(intNotationSuffix)){
            String toParse = vl.substring(0, vl.length() - intNotationSuffix.length());

            try {
              return Integer.parseInt(toParse);
            } catch (NumberFormatException ex) {
              throw new HoconException("Cannot construct " + value.render() + " into an integer");
            }

          } else {
            throw new HoconException(vl + " is not an integer");
          }
        }
        break;
      case LIST: {

        if(ConfigUtility.requestIsGroup(requested)){
          List<Object> res = constructList(value, requested);
          if(List.class.isAssignableFrom(requested)){
            return res;
          } else if (Set.class.isAssignableFrom(requested)){

            return new HashSet<Object>(res);
          }else if(requested.isArray()){
            return res.toArray();
          }

        }


      }
      break;
      case OBJECT: {
        if (Map.class.isAssignableFrom(requested)){
         return constructMap(value);
        }
        break;

      }

    }

    throw new HoconException("Could not explicitly construct " + requested.getCanonicalName() + " from " + value.render());


  }



  protected Map<String,Object> constructMap(ConfigValue value){
    Map<?,?> object = (Map<?,?>) value.unwrapped();
    Map<String,Object> map = new HashMap<>();
    for (Map.Entry<?,?> val : object.entrySet()) {
      map.put(String.valueOf(val.getKey()), constructObject(ConfigValueFactory.fromAnyRef(val.getValue())));
    }
    return map;
  }

  protected List<Object> constructList(ConfigValue value, Class requested){
    List<Object> array = (List<Object>) value.unwrapped();
    List<Object> result = new ArrayList<>(array.size());
    Class<?> componentType = requested == null? null: requested.getComponentType();
    for (Object val : array) {
      result.add(constructObject(ConfigValueFactory.fromAnyRef(val),componentType));
    }
    return result;

  }


}