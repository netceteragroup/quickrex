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
import java.util.List;

import de.babe.eclipse.plugins.quickREx.Messages;

/**
 * @author bastian.bergerhoff
 */
public class RECategory {

  private String name;

  private List<RELibraryEntry> contents = new ArrayList<>();

  public static final String INSTANCE_QNAME = "category"; //$NON-NLS-1$

  public static final String NAME_QNAME = "name"; //$NON-NLS-1$

  private REBook book;

  /**
   * The constructor.
   *
   * @param p_stringValue
   * @param entries
   */
  public RECategory(String p_stringValue, List<RELibraryEntry> entries) {
    this.name = p_stringValue;
    this.contents = entries;
  }

  /**
   * Sets the book for this category.
   *
   * @param book the book to set
   */
  public void setBook(REBook book) {
    this.book = book;
  }

  /**
   * Sets the name for this category
   *
   * @param p_name the name to set
   */
  public void setName(String p_name) {
    this.name = p_name;
  }

  /**
   * Returns the book that this category belongs to
   *
   * @return the book that this category belongs to
   */
  public REBook getBook() {
    return this.book;
  }

  /**
   * Returns the name of this category
   *
   * @return the name of this category
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the RELibraryEntries in this category, or an emtpy array.
   *
   * @return the RELibraryEntries in this category, or an emtpy array
   */
  public RELibraryEntry[] getCategoryContents() {
    return contents.toArray(new RELibraryEntry[contents.size()]);
  }

  /**
   * Returns an XML-representation of this object, using the passed String as a prefix for each line.
   *
   * @param p_prefix
   *          the prefix for the line
   * @return an XML-String-representation of this object
   */
  public String toXMLString(String p_prefix) {
    StringBuffer retBuffer = new StringBuffer(p_prefix);
    retBuffer.append("<").append(INSTANCE_QNAME).append(">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
    retBuffer.append(p_prefix).append(p_prefix).append("<").append(NAME_QNAME).append(">").append(replaceIllegalChars(this.name)).append("</") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    .append(NAME_QNAME).append(">\r\n"); //$NON-NLS-1$
    for (RELibraryEntry element : contents) {
      retBuffer.append(element.toXMLString(p_prefix, 2));
    }
    retBuffer.append(p_prefix).append("</").append(INSTANCE_QNAME).append(">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
    return retBuffer.toString();
  }

  private String replaceIllegalChars(String p_input) {
    return p_input.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
  }

  /**
   * Adds the passed RELibraryEntry to the contents of this category at the passed position.
   *
   * @param newEntry the new RELibraryEntry
   * @param newPosition the position for the new entry
   */
  public void addEntry(RELibraryEntry newEntry, int newPosition) {
    if (contents == null) {
      if (newPosition != 0) {
        throw new IllegalArgumentException(Messages.getString("objects.RECategory.error.message1")); //$NON-NLS-1$
      }
      contents = new ArrayList<>();
      contents.add(newEntry);
    } else {
      if (newPosition >= 0 && newPosition <= contents.size() - 1) {
        contents.add(newPosition, newEntry);
      } else if (newPosition == contents.size()) {
        contents.add(newEntry);
      } else {
        throw new IllegalArgumentException(Messages.getString("objects.RECategory.error.message2")); //$NON-NLS-1$
      }
    }
  }

  /**
   * Returns the entry with the passed title if it is among the
   * entries of this book. If not, <code>null</code> is returned
   *
   * @param entryTitle the title for the entry to return
   * @return the entry or <code>null</code>
   */
  public RELibraryEntry getEntryWithTitle(String entryTitle) {
    for (RELibraryEntry entry : contents) {
      if (entry.getTitle().equals(entryTitle)) {
        return entry;
      }
    }
    return null;
  }

  /**
   * Returns <code>true</code> if and only if this category contains an entry with the passed title.
   *
   * @param entryTitle the title to look for
   * @return <code>true</code> if and only if this category contains an entry with the passed title
   */
  public boolean containsEntryWithTitle(String entryTitle) {
    return getEntryWithTitle(entryTitle) != null;
  }

  /**
   * Removes the passed entry from this category.
   *
   * @param entry the entry to remove
   */
  public void removeEntry(RELibraryEntry entry) {
    contents.remove(entry);
  }
}