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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;

/**
 * @author bastian.bergerhoff
 */
public class REBook {

  public static final String INSTANCE_QNAME = "reBook"; //$NON-NLS-1$

  public static final String NAME_QNAME = "name"; //$NON-NLS-1$

  public static final String PATH_QNAME = "path"; //$NON-NLS-1$

  public static final String DEFAULT_BOOK_NAME = "QuickREx"; //$NON-NLS-1$

  private String name;

  private String path;

  private List<RECategory> contents;

  /**
   * The constructor.
   *
   * @param p_name
   * @param p_path
   */
  public REBook(String p_name, String p_path) {
    this.name = p_name;
    this.path = p_path;
  }

  /**
   * Sets the name
   *
   * @param p_name the name to set
   */
  public void setName(String p_name) {
    this.name = p_name;
  }

  /**
   * Returns the name
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the path
   *
   * @param p_path the path to set
   */
  public void setPath(String p_path) {
    this.path = p_path;
  }

  /**
   * Returns the path
   *
   * @return the path
   */
  public String getPath() {
    return this.path;
  }

  /**
   * Sets the contents (i.e. the categories in this book), updating the
   * book in all the passed RECategories to <code>this</code>
   *
   * @param contents an ArrayList holding RECategory-entries with the contents of this book
   */
  public void setContents(List<RECategory> contents) {
    for (RECategory category : contents) {
      category.setBook(this);
    }
    this.contents = contents;
  }

  /**
   * Returns the contents of this book.
   *
   * @return an ArrayList holding RECategory-entries with the contents of this book
   */
  public List<RECategory> getContents() {
    return this.contents;
  }

  /**
   * Returns an XML-representation of this object, using the passed String as a prefix for each line.
   *
   * @param p_prefix
   *          the prefix for the line
   * @param p_depth the indentation-depth
   * @return an XML-String-representation of this object
   */
  public String toXMLString(String p_prefix, int p_depth) {
    String prefix = p_prefix;
    for (int i = 1; i < p_depth; i++) {
      prefix += p_prefix;
    }
    StringBuilder retBuffer = new StringBuilder(prefix);
    retBuffer.append("<").append(INSTANCE_QNAME).append(">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
    retBuffer.append(prefix).append(p_prefix).append("<").append(NAME_QNAME).append(">").append(replaceIllegalChars(this.name)).append("</").append( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        NAME_QNAME).append(">\r\n"); //$NON-NLS-1$
    retBuffer.append(prefix).append(p_prefix).append("<").append(PATH_QNAME).append(">").append(replaceIllegalChars(this.path)).append("</").append( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        PATH_QNAME).append(">\r\n"); //$NON-NLS-1$
    retBuffer.append(prefix).append("</").append(INSTANCE_QNAME).append(">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
    return retBuffer.toString();
  }

  private String replaceIllegalChars(String p_input) {
    return p_input.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
  }

  /**
   * Writes the xml-file with the serialized representation of this book
   *
   * @param monitor the progress-monitor
   */
  public void writeFile(IProgressMonitor monitor) {
    File reFile = new File(getPath());
    try {
      FileOutputStream fos = new FileOutputStream(reFile);
      fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<regularExpressionLibrary>\r\n".getBytes("UTF8")); //$NON-NLS-1$ //$NON-NLS-2$
      for (int i = 0; i < getContents().size(); i++) {
        fos.write(getContents().get(i).toXMLString("\t").getBytes("UTF8")); //$NON-NLS-1$ //$NON-NLS-2$
      }
      fos.write("</regularExpressionLibrary>".getBytes("UTF8")); //$NON-NLS-1$ //$NON-NLS-2$
      fos.close();
    } catch (Exception e) {
      IStatus status = new Status(IStatus.WARNING, QuickRExPlugin.ID, 31, Messages.getString("objects.REBook.error.message1"), e); //$NON-NLS-1$
      QuickRExPlugin.getDefault().getLog().log(status);
    }
  }

  /**
   * Adds the passed RECategory to the contents of this book at the passed position.
   *
   * @param newCat the new RECategory
   * @param newPosition the position for the new category
   */
  public void addCategory(RECategory newCat, int newPosition) {
    if (contents == null) {
      if (newPosition != 0) {
        throw new IllegalArgumentException(Messages.getString("objects.REBook.error.message2")); //$NON-NLS-1$
      }
      contents = new ArrayList<>();
      contents.add(newCat);
    } else {
      if (newPosition >= 0 && newPosition <= contents.size() - 1) {
        contents.add(newPosition, newCat);
      } else if (newPosition == contents.size()) {
        contents.add(newCat);
      } else {
        throw new IllegalArgumentException(Messages.getString("objects.REBook.error.message3")); //$NON-NLS-1$
      }
    }
  }

  /**
   * Removes the passed RECategory from this book.
   *
   * @param p_cat the category to remove
   */
  public void removeCategory(RECategory p_cat) {
    this.contents.remove(p_cat);
  }

  /**
   * Returns the category with the passed name if it is among the
   * categories of this book. If not, <code>null</code> is returned
   *
   * @param p_catName the name for the category to return
   * @return the category or <code>null</code>
   */
  public RECategory getCategoryWithName(String p_catName) {
    try {
      for (RECategory cat : contents) {
        if (cat.getName().equals(p_catName)) {
          return cat;
        }
      }
      return null;
    } catch (NullPointerException npe) {
      return null;
    }
  }

  /**
   * Returns <code>true</code> if and only if a category with the passed name exists in this book.
   *
   * @param catName the name to look for
   * @return <code>true</code> if and only if a category with the passed name exists in this book
   */
  public boolean containsCategoryWithName(String catName) {
    return getCategoryWithName(catName) != null;
  }

  /**
   * Returns <code>true</code> if and only if this is the default-book (identified
   * by the name!).
   *
   * @return <code>true</code> if and only if this is the default-book
   */
  public boolean isQuickRExBook() {
    return DEFAULT_BOOK_NAME.equals(getName());
  }
}