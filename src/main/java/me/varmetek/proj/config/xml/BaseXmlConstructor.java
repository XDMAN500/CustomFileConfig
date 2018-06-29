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

import com.google.common.base.Preconditions;
import me.varmetek.proj.config.util.ConfigUtility;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Text;

import java.util.*;

public class BaseXmlConstructor
{

  protected final XmlConfigurationOptions options;
  protected final XmlConfiguration configuration;
  public BaseXmlConstructor (XmlConfiguration xmlConfiguration){
    this.configuration = Preconditions.checkNotNull(xmlConfiguration);
    this.options = configuration.options();
  }


  /**
   *
   * Attempt implicit construction
   *
   * */

  public Object constructObject(Content value){
    if (value == null){
      return null;
    }

    if(value.getCType() == null){
      if(value instanceof ContentSeq){
        ContentSeq seq = (ContentSeq)value;
        List<Object> array = new ArrayList<>();
        for(Content cont: seq){
          array.add(this.constructObject(cont));

        }

        return array;

      }

      if(value instanceof ElementGroup){
        ElementGroup seq = (ElementGroup) value;
        List<Object> array = new ArrayList<>();
        for(Content cont: seq){
          array.add(this.constructObject(cont));

        }

        return array;

      }
    }else {

      switch (value.getCType()) {


        case Text:   {

          Text text = (Text) value;
          String tval = text.getText();

          //Try boolean
          if (tval.equals("true")){

            return Boolean.TRUE;
          } else if (tval.equals("false")){
            return Boolean.FALSE;
          }

          //try number
          try {
            if (tval.indexOf('.') == -1){
              //try int
              return Integer.parseInt(tval);
            } else {
              //try double
              return Double.parseDouble(tval);
            }
          } catch (NumberFormatException ignored) {
          }

          return tval; //default to string

        }

        case Element: {

          Element elem = (Element) value;
          if (elem.getContentSize() == 0){
           return null;
          }


          {
            List<Object> res = null;

            List<Element> seq = elem.getChildren("_");


            if (seq.size() > 0){
              res = constructList(seq,null);
              if (res != null){
                return res;
              }

            }
          }

          {
            Map<?,?> res = null;

            List<Element> seq = elem.getChildren();
            if (seq.size() > 0){
              res = constructMap(seq);
              if (res != null){
                return res;
              }

            }
          }




            return constructObject(elem.getContent(0));

        }


        default: {
          throw new XmlException(value.getCType().name() + " node is not supported");
        }
      }
    }


  throw new XmlException("Could not implicitly construct "+ value);


  }




  /**
   *
   * Attempt explicit construction
   *
   * */
  public Object constructObject(Content value, Class<?> requested){
    if(requested == null) return constructObject(value);
    if (value == null){
      return null;
    }

    if(value.getCType() == null){// Handle Custom Cases. Should not happen.
      if(value instanceof ContentSeq && List.class.isAssignableFrom(requested)){

        ContentSeq seq = (ContentSeq)value;
        List<Object> array = new ArrayList<>();
        for(Content cont: seq){
          array.add(this.constructObject(cont,requested.getComponentType()));

        }

        return array;

      }

      if(value instanceof ElementGroup &&  Map.class.isAssignableFrom(requested)){
        ElementGroup seq = (ElementGroup) value;
        List<Object> array = new ArrayList<>();
        for(Content cont: seq){
          array.add(this.constructObject(cont));

        }

        return array;

      }
    }else {

      switch (value.getCType()) {


        case Text:   {


          Text text = (Text) value;
          String tval = text.getText();
          if(requested == String.class){
            return tval;
          }


          if(requested == boolean.class || requested == Boolean.class){
            //Try boolean
            if (tval.equals("true")){

              return Boolean.TRUE;
            } else if (tval.equals("false")){
              return Boolean.FALSE;
            }
          }

          //try number
          if (ConfigUtility.requestIsNumber(requested)){
            try {
              if (tval.indexOf('.') == -1 && ConfigUtility.requestIsInteger(requested)){
                //try int
                return Integer.parseInt(tval);
              } else if (ConfigUtility.requestIsDecimal(requested)){
                //try double
                return Double.parseDouble(tval);
              }
            } catch (NumberFormatException ignored) {
              throw new XmlException("Could not parse number from "+ tval);
            }
            throw new XmlException("Could not construct number from "+ tval);

          }


        }

        case Element: {

          Element elem = (Element) value;
          if (elem.getContentSize() == 0){
            return null;
          }


          if(ConfigUtility.requestIsGroup(requested)){

            List<Object> res = null;

            List<Element> seq = elem.getChildren("_");


            if (seq.size() > 0){
              res = constructList(seq,requested);
              if (res != null){
                if(List.class.isAssignableFrom(requested)){
                  return res;
                }else if(Set.class.isAssignableFrom(requested)){
                  return new HashSet<Object>(res);
                }else if(requested.isArray()){
                  return res.toArray();
                }

              }

            }
          }

          if(Map.class.isAssignableFrom(requested)){
            Map<?,?>res = null;
            List<Element> seq  = elem.getChildren();
            if (seq.size() > 0){
              res = constructMap(seq);
              if (res != null){
                return res;
              }

            }
          }


          return constructObject(elem.getContent(0), requested);


        }



        default: {
          throw new XmlException(value.getCType().name() + " node is not supported");
        }
      }
    }




    throw new XmlException("Could not explicitly construct "+ requested.getCanonicalName() + " from "+ value.getCType().name());



  }




  protected List<Object> constructList(List<Element> list, Class<?> requested){
    List<Object> result = new ArrayList<>(list.size());
    Class<?> componentType = requested == null? null: requested.getComponentType();
    for(Element el: list){
      if(!el.getName().equals("_")) continue;
      Object res = constructObject(el,componentType);

      result.add(res);

    }
   return result;
  }





  protected Map<String,Object> constructMap(List<Element> list){
    Map<String,Object> result = new LinkedHashMap<>(list.size());

    for(Element el: list){
      Object res = constructObject(el);

      result.put(el.getName(), res);

    }


    return result;
  }









}
