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
public class LoadTestTextAction extends Action {

  public LoadTestTextAction() {
    super(""); //$NON-NLS-1$
    this.setText(Messages.getString("views.QuickRExView.loadTextAction.text")); //$NON-NLS-1$
    this.setToolTipText(Messages.getString("views.QuickRExView.loadTextAction.tooltip")); //$NON-NLS-1$
    this.setImageDescriptor(((PluginImageRegistry)QuickRExPlugin.getDefault().getImageRegistry())
        .getImageDescriptor(PluginImageRegistry.IMG_LOAD_TT));
    this.setId("de.babe.eclipse.plugins.quickREx.actions.LoadTestTextAction"); //$NON-NLS-1$
  }

  public void run() {
    try {
      ((QuickRExView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
          .getActivePage().showView(QuickRExView.ID)).handleLoadTextButtonPressed();
    } catch (PartInitException e) {
      // Bad luck...
    }
  }
}