/*******************************************************************************
 * Copyright (c) 2006 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.wizards;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.objects.REBook;

/**
 * @author bastian.bergerhoff
 */
public class NewREBookWizard extends Wizard implements INewWizard {
  private NewREBookWizardPage page;

  /**
   * The constructor
   */
  public NewREBookWizard() {
    super();
    setNeedsProgressMonitor(true);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.wizard.IWizard#addPages()
   */
  @Override
  public void addPages() {
    page = new NewREBookWizardPage();
    addPage(page);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.wizard.IWizard#performFinish()
   */
  @Override
  public boolean performFinish() {
    final String filePath = page.getFilePath();
    final String bookName = page.getBookName();
    IPath reLibFilePath = new Path(filePath);
    File reLibFile = reLibFilePath.toFile();
    if (reLibFile.exists()) {
      boolean proceed = MessageDialog.openQuestion(getShell(), Messages.getString("wizards.NewREBookWizard.message0"), Messages.getString("wizards.NewREBookWizard.message1")); //$NON-NLS-1$ //$NON-NLS-2$
      if (!proceed) {
        return false;
      } else {
        QuickRExPlugin.getDefault().addREBook(new REBook(bookName, filePath));
        return true;
      }
    } else {
      QuickRExPlugin.getDefault().addREBook(new REBook(bookName, filePath));
      return true;
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
   */
  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
  }
}