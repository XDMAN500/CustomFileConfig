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

package me.varmetek.proj.config.gson;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import me.varmetek.proj.config.util.ConfigUtility;

import java.util.*;

public class BaseGsonConstructor
{

  protected GsonConfiguration configuration;
  protected GsonConfigurationOptions options;

  public BaseGsonConstructor(GsonConfiguration gsonConfiguration){
    this.configuration = Preconditions.checkNotNull(gsonConfiguration);
    this.options = configuration.options();
  }
  /**
   *
   * Attempt implicit construction
   *
   * */

  public Object constructObject(JsonElement value){
    if(value == null || value == JsonNull.INSTANCE){
      return null;

    }


    if(value.isJsonPrimitive()){
      JsonPrimitive prim = value.getAsJsonPrimitive();
      if(prim.isBoolean()){
        return prim.getAsBoolean();
      }

      if(prim.isNumber()){
        return prim.getAsDouble();
      }

      if(prim.isString()){
        String vl = value.getAsString();
        final String intNotationSuffix = options.getIntegerTag();
        if(vl.endsWith(intNotationSuffix)){
          String toParse = vl.substring(0, vl.length() - intNotationSuffix.length());

          try {
            return Integer.parseInt(toParse);
          } catch (NumberFormatException ignored) { }
        }

        return value.getAsString();
      }
      throw new GsonException("Unexpected primitive "+ prim.toString() );
    }





    if(value.isJsonArray()){
      return constructList(value,null);

    }

    if(value.isJsonObject()){
     return  constructMap(value);


    }

    throw new GsonException("Could not implicitly construct "+ value);


  }


  /**
   *
   * Attempt explicit construction
   *
   * */
  public Object constructObject(JsonElement value, Class<?> requested){
    if(requested == null) return constructObject(value);

    if(value == null || value == JsonNull.INSTANCE){
      return JsonNull.INSTANCE;
    }


    if(value.isJsonPrimitive()){
      JsonPrimitive prim = value.getAsJsonPrimitive();
      if(prim.isBoolean()){
        if(requested == boolean.class || requested == Boolean.class ){
          return prim.getAsBoolean();
        }
      }

      if(prim.isNumber()){
        if(ConfigUtility.requestIsNumber(requested)){
          return prim.getAsDouble();
        }
      }

      if(prim.isString()){
        if (String.class == requested){
            return value.getAsString();
        }else if(ConfigUtility.requestIsNumber(requested)){
          String vl = value.getAsString();
          final String intNotationSuffix = options.getIntegerTag();
          if (vl.endsWith(intNotationSuffix)){
            String toParse = vl.substring(0, vl.length() - intNotationSuffix.length());

            try {
              return Integer.parseInt(toParse);
            } catch (NumberFormatException ex) {
              throw new GsonException("Cannot construct " + value.toString() + " into an integer");
            }
          } else {
            throw new GsonException(vl + " is not an integer");
          }


        }
      }
      throw new GsonException("Unexpected primitive "+ prim.toString() );
    }





    if(value.isJsonArray()){
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

    if(value.isJsonObject()){
      if (Map.class.isAssignableFrom(requested)){
        return constructMap(value);
      }

    }

    throw new GsonException("Could not explicitly construct "+ requested.getCanonicalName());


  }


  protected Map<String,Object> constructMap(JsonElement value){
    JsonObject object = value.getAsJsonObject();
    Map<String,Object> map = new HashMap<>();
    for (Map.Entry<String,JsonElement> val : object.entrySet()) {
      map.put(val.getKey(), constructObject(val.getValue()));
    }
    return map;
  }

  protected List<Object> constructList(JsonElement value, Class requested){
    JsonArray array = value.getAsJsonArray();
    List<Object> result = new ArrayList<>(array.size());
    Class<?> componentType = requested == null? null: requested.getComponentType();
     for (JsonElement  val : array) {
      result.add(constructObject(val,componentType));
    }
    return result;


  }
}
