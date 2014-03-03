/*******************************************************************************
 * Copyright (c) 2006 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.objects;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author bastian.bergerhoff
 */
public class REBooksXMLHandler extends DefaultHandler {

  private boolean receivingNameInformation = false;

  private boolean receivingPathInformation = false;

  private StringBuffer currentName = new StringBuffer();

  private StringBuffer currentPath = new StringBuffer();

  private ArrayList list;

  /**
   * This instance fills the passed list with instances
   * of REBook initialized from the XML-file that this
   * Handler is used with.
   * 
   * @param p_list the list to put the REBook-instances into
   */
  public REBooksXMLHandler(ArrayList p_list) {
    this.list = p_list;
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#characters(char[], int, int)
   */
  @Override
  public void characters(char[] ch, int start, int end) {
    if (receivingNameInformation) {
      currentName.append(ch, start, end);
    } else if (receivingPathInformation) {
      currentPath.append(ch, start, end);
    }
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String uri, String localName, String qName) {
    if (REBook.INSTANCE_QNAME.equals(qName)) {
      list.add(new REBook(currentName.toString(), currentPath.toString()));
      receivingNameInformation = false;
      receivingPathInformation = false;
      currentName = new StringBuffer();
      currentPath = new StringBuffer();
    } else if (REBook.NAME_QNAME.equals(qName)) {
      receivingNameInformation = false;
    } else if (REBook.PATH_QNAME.equals(qName)) {
      receivingPathInformation = false;
    }
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    if (REBook.INSTANCE_QNAME.equals(qName)) {
    } else if (REBook.NAME_QNAME.equals(qName)) {
      receivingNameInformation = true;
    } else if (REBook.PATH_QNAME.equals(qName)) {
      receivingPathInformation = true;
    }
  }
}