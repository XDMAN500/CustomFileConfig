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
import me.varmetek.proj.config.util.ConfigUtility;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;


public class BaseHoconRepresentor
{

  protected HoconConfiguration configuration;
  protected HoconConfigurationOptions options;

  public BaseHoconRepresentor (HoconConfiguration hoconConfiguration){
    this.configuration = Preconditions.checkNotNull(hoconConfiguration);
    this.options = configuration.options();
  }

  public ConfigValue representData(Object data){
    if(data == null){
      return ConfigValueFactory.fromAnyRef(null);
    }

    if(data instanceof Boolean){
      return ConfigValueFactory.fromAnyRef(data);

    }



    if(data instanceof Character){
      return ConfigValueFactory.fromAnyRef(String.valueOf(data));
    }


    if(data instanceof String){
      return ConfigValueFactory.fromAnyRef(data);
    }

    if(data instanceof Number){
      Number num = (Number)data;

      if(num instanceof Byte || num instanceof Short || num instanceof  Integer || num instanceof BigInteger){
        final String intNotationSuffix = options.getIntegerTag();
        String vl = new StringBuilder().append(num.intValue()).append(intNotationSuffix).toString();
        return ConfigValueFactory.fromAnyRef(vl);
      }else if(   data instanceof Double || data instanceof BigDecimal){
        return ConfigValueFactory.fromAnyRef(num.doubleValue());
      }else  if(   data instanceof Float){
        return ConfigValueFactory.fromAnyRef(num.floatValue());
      }

      throw new HoconException("Unexpected number '" + data.getClass().getCanonicalName() + "'");
    }



    if(data.getClass().isArray()){
      Object[] arr = ConfigUtility.castArray(data);
      List<ConfigValue> array = new ArrayList<>(arr.length);
      for (int i = 0; i < arr.length; i++) {

        array.add(ConfigValueFactory.fromAnyRef(representData(arr[i])));
      }
      return ConfigValueFactory.fromAnyRef(array);




    }



    if(data instanceof Collection){
      Collection<?> arr = (Collection<?>) data;
      List<ConfigValue> array = new ArrayList<>(arr.size());
      for(Object i: arr){
        array.add(ConfigValueFactory.fromAnyRef(representData(i)));
      }
      return ConfigValueFactory.fromAnyRef(array);
    }

    if(data instanceof Map){
     Map<String,Object> object = new HashMap<>();
     
      Map<?,?> map = (Map<?,?>)data;
      for(Map.Entry<?,?> entry: map.entrySet()){
        if(entry.getKey() == null) continue;
        object.put(entry.getKey().toString(),representData(entry.getValue()));
      }
      return ConfigValueFactory.fromAnyRef(object);
    }

    throw new HoconException(
      new  StringBuilder("Type \"").append(data.getClass().getCanonicalName()).append("\" could not be represented").toString());




  }



}
