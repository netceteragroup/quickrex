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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * @author bastian.bergerhoff
 */
public class RELibraryEntry {

  public static final String INSTANCE_QNAME = "reLibraryEntry"; //$NON-NLS-1$

  public static final String TITLE_QNAME = "title"; //$NON-NLS-1$

  public static final String RE_QNAME = "re"; //$NON-NLS-1$

  public static final String DESCRIPTION_QNAME = "description"; //$NON-NLS-1$

  public static final String TEST_TEXT_QNAME = "testText"; //$NON-NLS-1$

  public static final String SOURCE_QNAME = "source"; //$NON-NLS-1$

  private String title;

  private String re;

  private String description;

  private String testtext;

  private String source;

  private RECategory category;

  private transient ArrayList listeners;

  /**
   * The constructor
   * 
   * @param p_title
   * @param p_re
   * @param p_description
   * @param p_testtext
   * @param p_source
   */
  public RELibraryEntry(String p_title, String p_re, String p_description, String p_testtext, String p_source) {
    this.title = p_title;
    this.re = p_re;
    this.description = p_description;
    this.testtext = p_testtext;
    this.source = p_source;
    this.listeners = new ArrayList();
  }

  /**
   * Sets the category that this entry belongs to
   * 
   * @param category the category
   */
  public void setCategory(RECategory category) {
    this.category = category;
  }

  /**
   * Sets the description for this entry
   * 
   * @param description the description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Sets the regular expression for this entry
   * 
   * @param re the regular expression
   */
  public void setRe(String re) {
    this.re = re;
  }

  /**
   * Sets the source for this entry
   * 
   * @param source the source
   */
  public void setSource(String source) {
    this.source = source;
  }

  /**
   * Sets the test-text for this entry
   * 
   * @param testtext the test-text
   */
  public void setTesttext(String testtext) {
    this.testtext = testtext;
  }

  /**
   * Sets the title for this entry. This fires a PropertyChangeEvent to all registered
   * TitleChangeListeners
   *  
   * @param title the title
   */
  public void setTitle(String title) {
    if (listeners.size() > 0) {
      PropertyChangeEvent event = new PropertyChangeEvent(this, "title", this.title, title); //$NON-NLS-1$
      this.title = title;
      for (Iterator iter = listeners.iterator(); iter.hasNext();) {
        IPropertyChangeListener listener = (IPropertyChangeListener)iter.next();
        listener.propertyChange(event);
      }
    } else {
      this.title = title;
    }
  }

  /**
   * Returns the category that this entry belongs to
   * 
   * @return the category
   */
  public RECategory getCategory() {
    return this.category;
  }

  /**
   * Returns the description for this entry
   * 
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Returns the regular expression in this entry
   * 
   * @return the regular expression
   */
  public String getRe() {
    return this.re;
  }

  /**
   * Returns the source for this entry
   * 
   * @return the source
   */
  public String getSource() {
    return this.source;
  }

  /**
   * Returns the test-text for this entry
   * 
   * @return the test-text
   */
  public String getTesttext() {
    return this.testtext;
  }

  /**
   * Returns the title of this entry
   * 
   * @return the title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Adds the passed listener to the listeners which are informed when the 
   * title of this entry is changed
   * 
   * @param listener the listener to add
   */
  public void addTitleChangeListener(IPropertyChangeListener listener) {
    if (!listeners.contains(listener)) {
      this.listeners.add(listener);
    }
  }

  /**
   * Removes the passed listener from the list of listeners registered for 
   * changes of the title of this entry
   * 
   * @param listener the listener to remove
   */
  public void removeTitleChangeListener(IPropertyChangeListener listener) {
    if (listeners.contains(listener)) {
      this.listeners.remove(listener);
    }
  }

  /**
   * Returns an XML-representation of this object, using the passed String as a prefix for each line
   * 
   * @param p_prefix
   *          the prefix for the line
   * @param p_depth the indentation-level
   * @return an XML-String-representation of this object
   */
  public String toXMLString(String p_prefix, int p_depth) {
    String prefix = p_prefix;
    for (int i = 1; i < p_depth; i++) {
      prefix += p_prefix;
    }
    StringBuffer retBuffer = new StringBuffer(prefix);
    retBuffer.append("<").append(INSTANCE_QNAME).append(">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
    retBuffer.append(prefix).append(p_prefix).append("<").append(TITLE_QNAME).append(">").append(replaceIllegalChars(this.title)).append("</") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        .append(TITLE_QNAME).append(">\r\n"); //$NON-NLS-1$
    retBuffer.append(prefix).append(p_prefix).append("<").append(RE_QNAME).append(">").append(replaceIllegalChars(this.re)).append("</").append( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        RE_QNAME).append(">\r\n"); //$NON-NLS-1$
    retBuffer.append(prefix).append(p_prefix).append("<").append(DESCRIPTION_QNAME).append(">").append(replaceIllegalChars(this.description)).append( //$NON-NLS-1$ //$NON-NLS-2$
        "</").append(DESCRIPTION_QNAME).append(">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
    retBuffer.append(prefix).append(p_prefix).append("<").append(TEST_TEXT_QNAME).append(">").append(replaceIllegalChars(this.testtext)).append("</") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        .append(TEST_TEXT_QNAME).append(">\r\n"); //$NON-NLS-1$
    retBuffer.append(prefix).append(p_prefix).append("<").append(SOURCE_QNAME).append(">").append(replaceIllegalChars(this.source)).append("</") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        .append(SOURCE_QNAME).append(">\r\n"); //$NON-NLS-1$
    retBuffer.append(prefix).append("</").append(INSTANCE_QNAME).append(">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
    return retBuffer.toString();
  }

  private String replaceIllegalChars(String p_input) {
    return p_input.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
  }

  /**
   * Persists this entry by writing the file for the containing book
   *  
   * @param monitor the progress-monitor
   */
  public void doSave(IProgressMonitor monitor) {
    getCategory().getBook().writeFile(monitor);

  }

  /**
   * Returns the path within the book to this entry. Path's are of a form
   * similar to file-system paths: "bookname\categoryname\title". This works
   * because book-names are unique in the plugin, category-names are unique within
   * books and entry-titles are unique within categories
   * 
   * @return the path within the book for this entry 
   */
  public String getPath() {
    return getCategory().getBook().getName() + "\\" + getCategory().getName() + "\\" + getTitle(); //$NON-NLS-1$ //$NON-NLS-2$
  }
}