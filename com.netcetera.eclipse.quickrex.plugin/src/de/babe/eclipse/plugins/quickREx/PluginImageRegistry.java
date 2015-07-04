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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author bastian.bergerhoff, georg.sendt
 */
public class PluginImageRegistry extends ImageRegistry {

  public static final String IMG_JCOPY = "IMG_JCOPY"; //$NON-NLS-1$
  public static final String IMG_STOP = "IMG_STOP"; //$NON-NLS-1$

  /**
   * The constructor.
   *
   * @param plugin
   */
  PluginImageRegistry(QuickRExPlugin plugin) {
    String pluginId = plugin.getBundle().getSymbolicName();
    ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, "icons/jcopy.gif");  //$NON-NLS-1$
    put(IMG_JCOPY, descriptor);

    descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, "icons/stop.gif");  //$NON-NLS-1$
    put(IMG_STOP, descriptor);
  }
}