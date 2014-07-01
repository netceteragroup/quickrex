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

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

/**
 * @author bastian.bergerhoff, georg.sendt
 */
public class PluginImageRegistry extends ImageRegistry {

  private static URL iconBaseURL = null;

  public static final String IMG_JCOPY = "IMG_JCOPY"; //$NON-NLS-1$

  /**
   * The constructor.
   *
   * @param p_plugin
   */
  public PluginImageRegistry(QuickRExPlugin p_plugin) {
    super();
    try {
      if (iconBaseURL == null) {
        iconBaseURL = FileLocator.find(p_plugin.getBundle(), new Path("/icons/"), null); //$NON-NLS-1$
      }
      put(IMG_JCOPY, ImageDescriptor.createFromURL(new URL(iconBaseURL, "jcopy.gif"))); //$NON-NLS-1$
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}