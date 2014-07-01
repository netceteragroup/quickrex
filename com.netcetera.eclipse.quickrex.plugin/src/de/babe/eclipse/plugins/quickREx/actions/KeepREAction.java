/*******************************************************************************
 * Copyright (c) 2005, 2007 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.PluginImageRegistry;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.views.QuickRExView;

/**
 * @author bastian.bergerhoff
 */
public class KeepREAction extends Action {

  public KeepREAction() {
    super(""); //$NON-NLS-1$
    this.setText(Messages.getString("views.QuickRExView.keepREAction.text")); //$NON-NLS-1$
    this.setToolTipText(Messages.getString("views.QuickRExView.keepREAction.tooltip")); //$NON-NLS-1$
    PluginImageRegistry imageRegistry = (PluginImageRegistry) QuickRExPlugin.getDefault().getImageRegistry();
    this.setImageDescriptor(imageRegistry.getDescriptor(PluginImageRegistry.IMG_KEEP_RE));
    this.setId("de.babe.eclipse.plugins.quickREx.actions.KeepREAction"); //$NON-NLS-1$
  }

  @Override
  public void run() {
    try {
      ((QuickRExView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
          .getActivePage().showView(QuickRExView.ID)).handleKeepButtonPressed();
    } catch (PartInitException e) {
      // Bad luck...
    }
  }
}