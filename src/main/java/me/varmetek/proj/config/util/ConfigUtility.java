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

package me.varmetek.proj.config.util;

import com.google.common.base.Preconditions;
import org.bukkit.configuration.MemorySection;

import java.util.List;
import java.util.Set;

public final class ConfigUtility
{
  public static  boolean requestIsNumber(Class<?> requested){
    return requestIsDecimal(requested) || requestIsInteger(requested);
  }

  public static boolean requestIsGroup(Class<?> requested){
    return requested.isArray() ||List.class.isAssignableFrom(requested) || Set.class.isAssignableFrom(requested);
  }

  public static boolean requestIsDecimal(Class<?> requested){
    return requested == float.class || requested == Float.class
             || requested == double.class || requested == Double.class;
  }

  public static  boolean requestIsInteger(Class<?> requested){
    return requested == int.class || requested ==Integer.class
             || requested == byte.class || requested ==Byte.class
             || requested == short.class || requested == Short.class
             || requested == long.class || requested == Long.class;
  }

  public static  Object[] castArray(int[] on){
    Preconditions.checkArgument(on != null,"Array cannot be null");
   Object[] array = new Object[on.length];
   for(int i = 0; i< on.length ; i++){
     array[i] = on[i];
   }
   return array;
  }

  public static  Object[] castArray(byte[] on){
    Preconditions.checkArgument(on != null,"Array cannot be null");
    Object[] array = new Object[on.length];
    for(int i = 0; i< on.length ; i++){
      array[i] = on[i];
    }
    return array;
  }

  public static  Object[] castArray(short[] on){
    Preconditions.checkArgument(on != null,"Array cannot be null");
    Object[] array = new Object[on.length];
    for (int i = 0; i < on.length; i++) {
      array[i] = on[i];
    }
    return array;
  }
  public static  Object[] castArray(long[] on){
    Preconditions.checkArgument(on != null,"Array cannot be null");
    Object[] array = new Object[on.length];
    for(int i = 0; i< on.length ; i++){
      array[i] = on[i];
    }
    return array;
  }


  public static  Object[] castArray(float[] on){
    Preconditions.checkArgument(on != null,"Array cannot be null");
    Object[] array = new Object[on.length];
    for(int i = 0; i< on.length ; i++){
      array[i] = on[i];
    }
    return array;
  }

  public static  Object[] castArray(double[] on){
    Preconditions.checkArgument(on != null,"Array cannot be null");
    Object[] array = new Object[on.length];
    for(int i = 0; i< on.length ; i++){
      array[i] = on[i];
    }
    return array;
  }

  public static  Object[] castArray(boolean[] on){
    Preconditions.checkArgument(on != null,"Array cannot be null");
    Object[] array = new Object[on.length];
    for(int i = 0; i< on.length ; i++){
      array[i] = on[i];
    }
    return array;
  }

  public static  Object[] castArray(char[] on){
    Preconditions.checkArgument(on != null,"Array cannot be null");
    Object[] array = new Object[on.length];
    for(int i = 0; i< on.length ; i++){
      array[i] = on[i];
    }
    return array;
  }

  public static  Object[] castArray(Object on){
    Preconditions.checkArgument(on != null,"Array cannot be null");
    if (on instanceof Object[]){
      return (Object[])on;
    } else {
      Class<?> type = on.getClass();
      if (byte[].class == type){
        return castArray((byte[]) on);
      } else if (short[].class == type){
        return castArray((short[]) on);
      } else if (int[].class == type){
        return castArray((int[]) on);
      } else if (long[].class == type){
        return castArray((long[]) on);
      } else if (float[].class == type){
        return castArray((float[]) on);
      } else if (double[].class == type){
        return castArray((double[]) on);
      } else if (char[].class == type){
        return castArray((char[]) on);
      } else if (boolean[].class == type){
        return castArray((boolean[]) on);
      } else {
        throw new UnsupportedOperationException("Unexpected primitive '" + type.getCanonicalName() + "'");
      }

    }
  }

  public static void copyConfig(MemorySection from, MemorySection to){
    Preconditions.checkArgument(from!= null);
    Preconditions.checkArgument(to!= null);
    for(String key: to.getKeys(false)){
      to.set(key,null);
    }


    for(String key: from.getKeys(false)){
      to.set(key,from.get(key));
    }
  }
}
