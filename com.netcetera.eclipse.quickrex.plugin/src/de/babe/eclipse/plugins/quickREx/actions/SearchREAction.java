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
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.PluginImageRegistry;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.views.RELibraryView;

/**
 * @author bastian.bergerhoff
 */
public class SearchREAction extends Action {

  public SearchREAction() {
    super("", IAction.AS_PUSH_BUTTON); //$NON-NLS-1$
    this.setText(Messages.getString("views.RELibraryView.actions.search.name")); //$NON-NLS-1$
    this.setToolTipText(Messages.getString("views.RELibraryView.actions.search.tooltip")); //$NON-NLS-1$
    this.setImageDescriptor(((PluginImageRegistry)QuickRExPlugin.getDefault().getImageRegistry())
        .getImageDescriptor(PluginImageRegistry.IMG_SEARCH_RE));
    this.setId("de.babe.eclipse.plugins.quickREx.actions.SearchREAction"); //$NON-NLS-1$
  }

  @Override
  public void run() {
    try {
      ((RELibraryView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(RELibraryView.ID)).performSearchRE();
    } catch (PartInitException e) {
      // Bad luck...
    }
  }
}