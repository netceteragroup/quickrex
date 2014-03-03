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

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author bastian.bergerhoff
 */
public class RegularExpressionsXMLHandler extends DefaultHandler {

	private boolean receivingInstanceInformation = false;

	private StringBuffer currentString = new StringBuffer();

	private ArrayList list;

  /**
   * This instance fills the passed list with instances
   * of RegularExpressions initialized from the XML-file that this
   * Handler is used with. 
   * 
   * @param p_list the list to put the RegularExpressions-instances into
   */
	public RegularExpressionsXMLHandler(ArrayList p_list) {
		this.list = p_list;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int end) {
		if (receivingInstanceInformation) {
			currentString.append(ch, start, end);
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) {
		if (RegularExpression.INSTANCE_QNAME.equals(qName)) {
			list.add(new RegularExpression(currentString.toString()));
			receivingInstanceInformation = false;
			currentString = new StringBuffer();
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (RegularExpression.INSTANCE_QNAME.equals(qName)) {
			receivingInstanceInformation = true;
		}
	}
}