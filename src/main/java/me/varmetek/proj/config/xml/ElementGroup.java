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

import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Parent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ElementGroup extends Content implements  Iterable<Element>
{

  private final List<Element> elements = new ArrayList<>();


  protected ElementGroup (){
    super(null);
  }


  public void add(Element elem){
    if(elem != null){
      elements.add(elem);
    }
  }


  public Element getElement(int index){
    return elements.get(index);
  }

  public int getSize(){
    return elements.size();
  }

  public boolean isEmpty(){
    return elements.isEmpty();
  }
  @Override
  public String getValue (){
    return null;
  }


  @Override
  public Iterator<Element> iterator (){
    return elements.iterator();
  }

  @Override
  protected Content setParent(Parent parent) {
    for(Element elem: elements){
      parent.addContent(elem);
    }
    return this;
  }

  public void unify(Parent parent){
    elements.forEach(parent::addContent);
  }
  public List<Element> getContent() {
    return elements;
  }
}
