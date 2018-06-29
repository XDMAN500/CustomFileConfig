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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

public class BaseHjsonRepresentor{




  protected final HjsonConfiguration configuration;
  protected final HjsonConfigurationOptions options;

  public BaseHjsonRepresentor (HjsonConfiguration hjsonConfiguration){
    this.configuration = Preconditions.checkNotNull(hjsonConfiguration);
    options = configuration.options();
  }


  public JsonValue representData(Object data){
    if(data == null){
      return JsonValue.NULL;
    }

    if(data instanceof Boolean){
        return JsonValue.valueOf(((Boolean) data).booleanValue());

    }

    if(data instanceof String){
        return JsonValue.valueOf((String)data);
    }

    if(data instanceof Character){
        return JsonValue.valueOf(String.valueOf(data));
    }
    if(data instanceof Number){
      Number num = (Number)data;

      if(num instanceof Byte || num instanceof Short || num instanceof  Integer || num instanceof Long
           ||num instanceof BigInteger){
        String vl = new StringBuilder().append(num.intValue()).append(options.getIntegerTag()).toString();
        return JsonValue.valueOf(vl);
      }else if(   data instanceof Double || data instanceof BigDecimal){
        return JsonValue.valueOf(num.doubleValue());
      }else  if(   data instanceof Float){
        return JsonValue.valueOf(num.floatValue());
      }

      throw new HjsonException("Unexpected number '" + data.getClass().getCanonicalName() + "'");
    }

    if(data.getClass().isArray()){
      Object[] arr = ConfigUtility.castArray(data);
      JsonArray array = new JsonArray();
      for (int i = 0; i < arr.length; i++) {

        array.add(representData(arr[i]));
      }
      return array;



    }


    if(data instanceof Collection){
        Collection<?> arr = (Collection<?>) data;
        JsonArray array = new JsonArray();
        for (Object i : arr) {
          array.add(representData(i));
        }
        return array;
    }

    if(data instanceof Map){
      JsonObject object = new JsonObject();
      Map<?,?> map = (Map<?,?>)data;
      for(Map.Entry<?,?> entry: map.entrySet()){
        if(entry.getKey() == null) continue;
        object.set(entry.getKey().toString(),representData(entry.getValue()));
      }
      return object;
    }

    throw new HjsonException(
      new  StringBuilder("Type \"").append(data.getClass().getCanonicalName()).append("\" could not be represented").toString());




 }



}
