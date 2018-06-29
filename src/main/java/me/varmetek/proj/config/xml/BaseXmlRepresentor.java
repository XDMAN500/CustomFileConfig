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
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import org.bukkit.Bukkit;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Parent;
import org.jdom2.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BaseXmlRepresentor
{




  protected final XmlConfiguration configuration;
  protected final XmlConfigurationOptions options;

  public BaseXmlRepresentor (XmlConfiguration xmlConfiguration){
    this.configuration = Preconditions.checkNotNull(xmlConfiguration);
    options = configuration.options();
  }


  public Content representData(Object data){

    if(data == null){
      return null;
    }



    if(data instanceof Boolean){

        return new Text(String.valueOf(data));

    }

    if(data instanceof String){
        return new Text((String)data);
    }

    if(data instanceof Character){
        return  new Text(String.valueOf(data));
    }
    if(data instanceof Number){
      return  new Text(String.valueOf(data));
    }

    if(data.getClass().isArray()){
      ContentSeq frag = new ContentSeq();
      Object[] arr = (Object[]) data;
      for(Object i: arr){

        frag.add(representData(i));
      }
      return frag;

    }


    if(data instanceof Collection){

      ContentSeq frag = new ContentSeq();
      Collection<?> arr = (Collection<?>) data;


      for(Object i: arr){

        if(i == null) Bukkit.getLogger().info("   element is null");
        frag.add(representData(i));
      }
      return frag;

    }

    if(data instanceof Map){
     ElementGroup group = new ElementGroup();


      Map<?,?> map = (Map<?,?>)data;

      for(Map.Entry<?,?> entry: map.entrySet()){
        if(entry.getKey() == null) continue;
        Element elem = new Element(entry.getKey().toString());
       group.add(elem);

       unify(elem,representData(entry.getValue()));


      }
      return group;
    }

    throw new XmlException(
      new  StringBuilder("Type \"").append(data.getClass().getCanonicalName()).append("\" could not be represented").toString());




 }



  protected void unify(Parent parent, Content content){
    if(content == null) return;
    if(content  instanceof ElementGroup){
      ((ElementGroup)content).unify(parent);
    }else if(content instanceof ContentSeq){
      ((ContentSeq)content).unify(parent);

    }else{
      parent.addContent(content);
    }

  }

}
