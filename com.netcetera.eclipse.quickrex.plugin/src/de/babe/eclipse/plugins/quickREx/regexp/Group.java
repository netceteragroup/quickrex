/*******************************************************************************
 * Copyright (c) 2005 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *     Georg Sendt - added code for groups with IDs
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.regexp;

/**
 * @author bastian.bergerhoff, georg.sendt
 */
public class Group {

  private final int index;
  private final int start;
  private final int end;
  private final String text;


  /**
   * The constructor. Instances of this class are immutable.
   *
   * @param index the index of this group
   * @param text the textual contents of this group
   * @param start the start-index of this group
   * @param end the end-index of this group
   */
  Group(int index, String text, int start, int end) {
    this.index = index;
    this.text = text;
    this.start = start;
    this.end = end;
  }


  /**
   * Returns the index of this group.
   *
   * @return the index
   */
  public int getIndex() {
    return this.index;
  }

  /**
   * Returns the start-index of this group.
   *
   * @return the start-index
   */
  public int getStart() {
    return this.start;
  }

  /**
   * Returns the end-index of this group.
   *
   * @return the end-index
   */
  public int getEnd() {
    return this.end;
  }

  /**
   * Returns (a one-line representation of) the textual contents of this group
   * where line-breaks and tabs are represented by their escape-sequences.
   *
   * @return the textual contents
   */
  public String getText() {
    if (this.text == null) {
      return null;
    }
    return this.text
        .replaceAll("\r", "\\\u005C\\u0072") //$NON-NLS-1$ //$NON-NLS-2$
        .replaceAll("\n", "\\\u005C\\u006E") //$NON-NLS-1$ //$NON-NLS-2$
        .replaceAll("\t", "\\\u005C\\u0074"); //$NON-NLS-1$ //$NON-NLS-2$
  }
}
