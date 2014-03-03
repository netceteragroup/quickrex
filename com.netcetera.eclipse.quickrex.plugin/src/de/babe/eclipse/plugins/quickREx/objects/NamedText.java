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

/**
 * @author bastian.bergerhoff
 */
public class NamedText {

	private String name;
	private String text;
	public static final String INSTANCE_QNAME = "namedText"; //$NON-NLS-1$
	public static final String NAME_QNAME = "name"; //$NON-NLS-1$
	public static final String TEXT_QNAME = "text"; //$NON-NLS-1$

	/**
   * The constructor 
   * 
	 * @param p_name
	 * @param p_text
	 */
	public NamedText(String p_name, String p_text) {
		this.name = p_name;
		this.text = p_text;
	}
	
	/**
   * Returns the name of this text
   * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
   * Returns the text in this text
   * 
	 * @return the text
	 */
	public String getText() {
		return this.text;
	}

	/**
   * Returns an XML-representation of this object, using the passed String
   * as a prefix for each line
   * 
	 * @param p_prefix the prefix for the line
	 * @return an XML-String-representation of this object
	 */
	public String toXMLString(String p_prefix) {
	    StringBuffer retBuffer = new StringBuffer(p_prefix);
		retBuffer.append("<").append(INSTANCE_QNAME).append(">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
		retBuffer.append(p_prefix).append(p_prefix).append("<").append(NAME_QNAME).append(">").append(replaceIllegalChars(this.name)).append("</").append(NAME_QNAME).append(">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		retBuffer.append(p_prefix).append(p_prefix).append("<").append(TEXT_QNAME).append(">").append(replaceIllegalChars(this.text)).append("</").append(TEXT_QNAME).append(">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		retBuffer.append(p_prefix).append("</").append(INSTANCE_QNAME).append(">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
		return retBuffer.toString();
	}

	private String replaceIllegalChars(String p_input) {
		return p_input.replaceAll("&", "&amp;").replaceAll("<", "&lt;") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				.replaceAll(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
