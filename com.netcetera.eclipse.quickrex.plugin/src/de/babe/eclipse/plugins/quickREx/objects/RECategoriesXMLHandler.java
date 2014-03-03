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
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author bastian.bergerhoff
 */
public class RECategoriesXMLHandler extends DefaultHandler {

  private boolean receivingNameInformation = false;

  private boolean receivingREEntryInformation = false;

  private StringBuffer currentName = new StringBuffer();

  private ArrayList list;

  private RELibraryEntriesXMLHandler libEntryHandler;

  private ArrayList currentEntries = new ArrayList();

  /**
   * This instance fills the passed list with instances of RECategories initialized from the XML-file that this Handler is used with.
   * 
   * @param p_list
   *          the list to put the RECategory-instances into
   */
  public RECategoriesXMLHandler(ArrayList p_list) {
    this.list = p_list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ContentHandler#characters(char[], int, int)
   */
  public void characters(char[] ch, int start, int end) {
    if (receivingNameInformation) {
      currentName.append(ch, start, end);
    } else if (receivingREEntryInformation) {
      libEntryHandler.characters(ch, start, end);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  public void endElement(String uri, String localName, String qName) {
    if (receivingREEntryInformation) {
      libEntryHandler.endElement(uri, localName, qName);
      if (RELibraryEntry.INSTANCE_QNAME.equals(qName)) {
        receivingREEntryInformation = false;
        currentEntries.addAll(libEntryHandler.getList());
      }
    } else {
      if (RECategory.INSTANCE_QNAME.equals(qName)) {
        RECategory cat = new RECategory(currentName.toString(), currentEntries);
        for (Iterator iter = currentEntries.iterator(); iter.hasNext();) {
          RELibraryEntry element = (RELibraryEntry)iter.next();
          element.setCategory(cat);
        }
        list.add(cat);
        currentName = new StringBuffer();
        currentEntries = new ArrayList();
      } else if (RECategory.NAME_QNAME.equals(qName)) {
        receivingNameInformation = false;
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    if (receivingREEntryInformation) {
      libEntryHandler.startElement(uri, localName, qName, attributes);
    } else {
      if (RECategory.INSTANCE_QNAME.equals(qName)) {
      } else if (RECategory.NAME_QNAME.equals(qName)) {
        receivingNameInformation = true;
      } else if (RELibraryEntry.INSTANCE_QNAME.equals(qName)) {
        receivingREEntryInformation = true;
        libEntryHandler = new RELibraryEntriesXMLHandler(new ArrayList());
        libEntryHandler.startElement(uri, localName, qName, attributes);
      }
    }
  }
}