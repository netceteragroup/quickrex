/*******************************************************************************
 * Copyright (c) 2005, 2007 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *     Georg Sendt - fixed a potential NPE
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx;

import java.text.StringCharacterIterator;

/**
 * @author bastian.bergerhoff, georg.sendt
 */
public abstract class StringUtils {

  /**
   * Returns the passed String with any occurrence of " replaced by \"
   * and any occurrence of \ replaced by \\.
   *
   * @param text the String to escape
   * @return the escaped String or "" if null was passed.
   */
  public static String escapeForJava(String text) {
    StringBuilder retBuffer = new StringBuilder();
    StringCharacterIterator it = new StringCharacterIterator(text);
    for (char c = it.first(); c != StringCharacterIterator.DONE; c = it.next()) {
      if (c == '\\') {
        retBuffer.append("\\\\"); //$NON-NLS-1$
      } else if (c == '"') {
        retBuffer.append("\\\""); //$NON-NLS-1$
      } else {
        retBuffer.append(c);
      }
    }
    return retBuffer.toString();
  }

  /**
   * Returns the first line of the passed String.
   *
   * @param text the String to return the first line from
   * @return the first line of the passed String in case p_text is null return empty String
   */
  public static String firstLine(String text) {
    if (text == null) {
      return ""; //$NON-NLS-1$
    }

    if (text.indexOf("\r") >= 0) { //$NON-NLS-1$
      return text.substring(0, text.indexOf("\r")); //$NON-NLS-1$
    } else {
      return text;
    }
  }
}
