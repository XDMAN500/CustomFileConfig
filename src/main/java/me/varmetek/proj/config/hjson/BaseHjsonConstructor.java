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

package me.varmetek.proj.config.hjson;

import com.google.common.base.Preconditions;
import me.varmetek.proj.config.util.ConfigUtility;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.util.*;

public class BaseHjsonConstructor
{

  protected final HjsonConfigurationOptions options;
  protected final HjsonConfiguration configuration;
  public BaseHjsonConstructor (HjsonConfiguration hjsonConfiguration){
    this.configuration = Preconditions.checkNotNull(hjsonConfiguration);
    this.options = configuration.options();
  }


  /**
   *
   * Attempt implicit construction
   *
   * */

  public Object constructObject(JsonValue value){
    if(value == null || value == JsonValue.NULL){
      return null;

    }
    if(value.isBoolean()){
      return value.asBoolean();

    }

    if(value.isNumber()){
      return value.asDouble();

    }

    if(value.isString()){
      String vl = value.asString();
      final String intNotationSuffix = options.getIntegerTag();
      if(vl.endsWith(intNotationSuffix)){
        String toParse = vl.substring(0,vl.length()-intNotationSuffix.length());

        try{
         return Integer.parseInt(toParse);
        }catch (NumberFormatException ignored){ }
      }

      return value.asString();
    }

    if(value.isArray()){
      return constructList( value, null);


    }

    if(value.isObject()){
      return constructMap(value);


    }

    throw new HjsonException("Could not implicitly construct "+ value);


  }


  /**
   *
   * Attempt explicit construction
   *
   * */
  public Object constructObject(JsonValue value, Class<?> requested){
    if(requested == null) return constructObject(value);
    if(value == null || value == JsonValue.NULL){
      return JsonValue.NULL;
    }

    if(value.isBoolean()){
      if(requested == boolean.class || requested == Boolean.class ){
        return value.asBoolean();
      }

    }

    if(value.isNumber()){
      if (ConfigUtility.requestIsNumber(requested)){
        return value.asDouble();
      }


    }

    if(value.isString()){
      if(String.class == requested){
        return value.asString();
      }else if(ConfigUtility.requestIsNumber(requested)){
        String vl = value.asString();
        final String intNotationSuffix = options.getIntegerTag();
        if (vl.endsWith(intNotationSuffix)){
          String toParse = vl.substring(0, vl.length() - intNotationSuffix.length());

          try {
            return Integer.parseInt(toParse);
          } catch (NumberFormatException ex) {
            throw new HjsonException("Cannot construct " + value.toString(Stringify.PLAIN) + " into an integer");
          }
        } else {
          throw new HjsonException(vl + " is not an integer");
        }
      }

    }

    if(value.isArray()){
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
    if(value.isObject()){
      if (Map.class.isAssignableFrom(requested)){
        return constructMap(value);
      }

    }
    throw new HjsonException("Could not explicitly construct "+ requested.getCanonicalName() + " from "+ value.toString(Stringify.PLAIN));



  }



  protected Map<String,Object> constructMap(JsonValue value){
    JsonObject object = value.asObject();
    Map<String,Object> map = new HashMap<>();
    for (JsonObject.Member val : object) {
      map.put(val.getName(), constructObject(val.getValue()));
    }
    return map;
  }

  protected List<Object> constructList(JsonValue value, Class requested){
    JsonArray array = value.asArray();
    List<Object> result = new ArrayList<>(array.size());
    Class<?> componentType = requested == null? null: requested.getComponentType();
    for (JsonValue val : array) {
      result.add(constructObject(val,componentType));
    }

    return result;

  }
}
