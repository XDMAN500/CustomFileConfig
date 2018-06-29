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

import org.bukkit.Bukkit;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Parent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ContentSeq extends Content implements Iterable<Content>
{

  private final List<Content> contents = new ArrayList<>();


  protected ContentSeq (){
    super(null);
  }


  public void add(Content elem){
    if(elem != null){
      if (elem instanceof ContentSeq){
        ContentSeq seq = (ContentSeq) elem;
        for (Content cont : seq) {
            this.add(cont);

        }
      }else if(elem instanceof ElementGroup){
        ElementGroup group = (ElementGroup) elem;
        for (Content cont : group) {
          this.add(cont);

        }
      }else{
        Bukkit.getLogger().info( "ContentSeq adding " + elem.getCType().name());
        contents.add(elem);
      }
    }
  }


  public Content getContent(int index){
    return contents.get(index);
  }


  @Override
  public String getValue (){
    return null;
  }


  @Override
  public Iterator<Content> iterator (){
    return contents.iterator();
  }


  public int getSize(){
    return contents.size();
  }

  public boolean isEmpty(){
    return contents.isEmpty();
  }
  @Override
  protected Content setParent(Parent parent) {
    for(Content elem: contents){
      parent.addContent(elem);
    }
    return this;
  }

  public void unify(Parent parent){

   for(Content cont : contents){
     Element seq = new Element("_");
     if(cont instanceof ContentSeq){
       ((ContentSeq)cont).unify(parent);
     }else if(cont instanceof ElementGroup){
       ((ElementGroup)cont).unify(parent);
     }else {
       seq.addContent(cont);
        parent.addContent(seq);
     }
   }
  }
  public List<Content> getContent() {
    return contents;
  }
}
