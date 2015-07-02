/*******************************************************************************
 * Copyright (c) 2005, 2006 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Takahiro Shida - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author takahiro.shida
 */
public final class Messages {
  private static final String BUNDLE_NAME = "de.babe.eclipse.plugins.quickREx.messages"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  private Messages() {
    throw new AssertionError("not instantiable");
  }

  /**
   * Returns the (localized) string stored under the passed key or '!<key>!' if
   * the string was not found
   *
   * @param key the key for the (localized) string
   * @return the (localized) string or '!<key>!'
   */
  public static String getString(String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  /**
   * Returns the (localized) string stored under the passed key with placeholders
   * replaced by the passed Objects or '!<key>!' if the string was not found
   *
   * @param key the key for the (localized) string
   * @param params parameters for the message
   * @return the (localized) string stored under the passed key
   */
  public static String getString(String key, Object[] params) {
    return MessageFormat.format(getString(key), params);
  }
}