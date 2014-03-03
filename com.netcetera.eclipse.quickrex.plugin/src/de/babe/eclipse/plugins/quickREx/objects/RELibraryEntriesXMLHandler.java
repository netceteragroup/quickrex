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
public class RELibraryEntriesXMLHandler extends DefaultHandler {

  private boolean receivingTitleInformation = false;
  private boolean receivingREInformation = false;
  private boolean receivingDescriptionInformation = false;
  private boolean receivingTestTextInformation = false;
  private boolean receivingSourceInformation = false;

  private StringBuffer currentTitle = new StringBuffer();
  private StringBuffer currentDescription = new StringBuffer();
  private StringBuffer currentRE = new StringBuffer();
  private StringBuffer currentTestText = new StringBuffer();
  private StringBuffer currentSource = new StringBuffer();

  private ArrayList list;

  /**
   * This instance fills the passed list with instances
   * of RELibraryEntry initialized from the XML-file that this
   * Handler is used with. 
   * 
   * @param p_list the list to put the RELibraryEntry-instances into
   */
  public RELibraryEntriesXMLHandler(ArrayList p_list) {
    this.list = p_list;
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#characters(char[], int, int)
   */
  public void characters(char[] ch, int start, int end) {
    if (receivingTitleInformation) {
      currentTitle.append(ch, start, end);
    } else if (receivingREInformation) {
      currentRE.append(ch, start, end);
    } else if (receivingDescriptionInformation) {
      currentDescription.append(ch, start, end);
    } else if (receivingTestTextInformation) {
      currentTestText.append(ch, start, end);
    } else if (receivingSourceInformation) {
      currentSource.append(ch, start, end);
    }
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  public void endElement(String uri, String localName, String qName) {
    if (RELibraryEntry.INSTANCE_QNAME.equals(qName)) {
      list.add(new RELibraryEntry(currentTitle.toString(), currentRE.toString(), currentDescription.toString(), currentTestText.toString(), currentSource.toString()));
      currentTitle = new StringBuffer();
      currentRE = new StringBuffer();
      currentDescription = new StringBuffer();
      currentTestText = new StringBuffer();
      currentSource = new StringBuffer();
      receivingTitleInformation = false;
      receivingREInformation = false;
      receivingDescriptionInformation = false;
      receivingTestTextInformation = false;
      receivingSourceInformation = false;
    } else if (RELibraryEntry.TITLE_QNAME.equals(qName)) {
      receivingTitleInformation = false;
    } else if (RELibraryEntry.RE_QNAME.equals(qName)) {
      receivingREInformation = false;
    } else if (RELibraryEntry.DESCRIPTION_QNAME.equals(qName)) {
      receivingDescriptionInformation = false;
    } else if (RELibraryEntry.TEST_TEXT_QNAME.equals(qName)) {
      receivingTestTextInformation = false;
    } else if (RELibraryEntry.SOURCE_QNAME.equals(qName)) {
      receivingSourceInformation = false;
    }
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    if (RELibraryEntry.INSTANCE_QNAME.equals(qName)) {
    } else if (RELibraryEntry.TITLE_QNAME.equals(qName)) {
      receivingTitleInformation = true;
    } else if (RELibraryEntry.RE_QNAME.equals(qName)) {
      receivingREInformation = true;
    } else if (RELibraryEntry.DESCRIPTION_QNAME.equals(qName)) {
      receivingDescriptionInformation = true;
    } else if (RELibraryEntry.TEST_TEXT_QNAME.equals(qName)) {
      receivingTestTextInformation = true;
    } else if (RELibraryEntry.SOURCE_QNAME.equals(qName)) {
      receivingSourceInformation = true;
    }
  }

  /**
   * Returns the list of entries
   * 
   * @return the list of entries
   */
  public ArrayList getList() {
    return this.list;
  }
}