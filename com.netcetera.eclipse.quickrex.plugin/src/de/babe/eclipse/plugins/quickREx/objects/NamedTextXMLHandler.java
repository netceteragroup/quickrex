/*******************************************************************************
 * Copyright (c) 2005 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.objects;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class NamedTextXMLHandler extends DefaultHandler {

  private boolean receivingNameInformation = false;
  private boolean receivingTextInformation = false;

  private StringBuilder currentName = new StringBuilder();
  private StringBuilder currentText = new StringBuilder();

  private List<NamedText> list;

  /**
   * This instance fills the passed list with instances
   * of NamedText initialized from the XML-file that this
   * Handler is used with.
   *
   * @param p_list the list to put the NamedText-instances into
   */
  public NamedTextXMLHandler(List<NamedText> p_list) {
    this.list = p_list;
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#characters(char[], int, int)
   */
  @Override
  public void characters(char[] ch, int start, int end) {
    if (receivingNameInformation) {
      currentName.append(ch, start, end);
    } else if (receivingTextInformation) {
      currentText.append(ch, start, end);
    }
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String uri, String localName, String qName) {
    if (NamedText.INSTANCE_QNAME.equals(qName)) {
      list.add(new NamedText(currentName.toString(), currentText.toString()));
      currentName = new StringBuilder();
      currentText = new StringBuilder();
    } else if (NamedText.NAME_QNAME.equals(qName)) {
      receivingNameInformation = false;
    } else if (NamedText.TEXT_QNAME.equals(qName)) {
      receivingTextInformation = false;
    }
  }

  /* (non-Javadoc)
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    if (NamedText.INSTANCE_QNAME.equals(qName)) {
    } else if (NamedText.NAME_QNAME.equals(qName)) {
      receivingNameInformation = true;
    } else if (NamedText.TEXT_QNAME.equals(qName)) {
      receivingTextInformation = true;
    }
  }
}