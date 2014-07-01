/*******************************************************************************
 * Copyright (c) 2005 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *     Georg Sendt - added JRegexp-related implementations
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

/**
 * @author bastian.bergerhoff, georg.sendt
 */
public class PluginImageRegistry extends ImageRegistry {

  private static URL iconBaseURL = null;

  public final static String IMG_ORGANIZE_RES = "IMG_ORGANIZE_RES"; //$NON-NLS-1$

  public final static String IMG_ORGANIZE_TEXTS = "IMG_ORGANIZE_TEXTS"; //$NON-NLS-1$

  public static final String IMG_KEEP_RE = "IMG_KEEP_RE"; //$NON-NLS-1$

  public static final String IMG_SAVE_TT = "IMG_SAVE_TT"; //$NON-NLS-1$

  public static final String IMG_LOAD_TT = "IMG_LOAD_TT"; //$NON-NLS-1$

  public static final String IMG_JCOPY = "IMG_JCOPY"; //$NON-NLS-1$

  public static final String IMG_GREP = "IMG_GREP"; //$NON-NLS-1$

  /**
   * The constructor.
   *
   * @param p_plugin
   */
  public PluginImageRegistry(QuickRExPlugin p_plugin) {
    super();
    try {
      if (iconBaseURL == null) {
        iconBaseURL = p_plugin.find(new Path("/icons/")); //$NON-NLS-1$
      }
      put(IMG_ORGANIZE_RES, ImageDescriptor.createFromURL(new URL(iconBaseURL, "orgREs.gif"))); //$NON-NLS-1$
      put(IMG_ORGANIZE_TEXTS, ImageDescriptor.createFromURL(new URL(iconBaseURL, "orgTestTexts.gif"))); //$NON-NLS-1$
      put(IMG_KEEP_RE, ImageDescriptor.createFromURL(new URL(iconBaseURL, "saveRE.gif"))); //$NON-NLS-1$
      put(IMG_SAVE_TT, ImageDescriptor.createFromURL(new URL(iconBaseURL, "saveText.gif"))); //$NON-NLS-1$
      put(IMG_LOAD_TT, ImageDescriptor.createFromURL(new URL(iconBaseURL, "loadText.gif"))); //$NON-NLS-1$
      put(IMG_JCOPY, ImageDescriptor.createFromURL(new URL(iconBaseURL, "jcopy.gif"))); //$NON-NLS-1$
      put(IMG_GREP, ImageDescriptor.createFromURL(new URL(iconBaseURL, "grep.gif"))); //$NON-NLS-1$
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}