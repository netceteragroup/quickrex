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

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;

/**
 * @author bastian.bergerhoff
 */
public class NewREBookWizardPage extends WizardPage {
  private Text nameText;

  private Text fileText;

  /**
   * The constructor.
   */
  public NewREBookWizardPage() {
    super("wizardPage"); //$NON-NLS-1$
    setTitle(Messages.getString("wizards.NewREBookWizardPage.title")); //$NON-NLS-1$
    setDescription(Messages.getString("wizards.NewREBookWizardPage.description")); //$NON-NLS-1$
  }

  /**
   * @see IDialogPage#createControl(Composite)
   */
  @Override
  public void createControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NULL);
    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 3;
    layout.verticalSpacing = 9;
    Label label = new Label(container, SWT.NULL);
    label.setText(Messages.getString("wizards.NewREBookWizardPage.fileLabel.text")); //$NON-NLS-1$

    fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    fileText.setLayoutData(gd);
    fileText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });

    Button button = new Button(container, SWT.PUSH);
    button.setText(Messages.getString("wizards.NewREBookWizardPage.fileButton.text")); //$NON-NLS-1$
    button.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse();
      }
    });
    label = new Label(container, SWT.NULL);
    label.setText(Messages.getString("wizards.NewREBookWizardPage.nameLabel.text")); //$NON-NLS-1$

    nameText = new Text(container, SWT.BORDER | SWT.SINGLE);
    gd = new GridData(GridData.FILL_HORIZONTAL);
    nameText.setLayoutData(gd);
    nameText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    dialogChanged();
    setControl(container);
  }

  /**
   * Uses the standard container selection dialog to choose the new value for the container field.
   */
  private void handleBrowse() {
    FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
    dialog.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$
    fileText.setText(dialog.open());
  }

  /**
   * Ensures that both text fields are set and the book-name is not yet in use.
   */
  private void dialogChanged() {
    if (getFilePath().length() == 0) {
      updateStatus(Messages.getString("wizards.NewREBookWizardPage.message1")); //$NON-NLS-1$
      return;
    }
    if (getBookName().length() == 0) {
      updateStatus(Messages.getString("wizards.NewREBookWizardPage.message2")); //$NON-NLS-1$
      return;
    }
    if (QuickRExPlugin.getDefault().reBookWithNameExists(getBookName())) {
      updateStatus(Messages.getString("wizards.NewREBookWizardPage.message3")); //$NON-NLS-1$
      return;
    }
    updateStatus(null);
  }

  private void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(message == null);
  }

  /**
   * Returns the path.
   *
   * @return the path
   */
  public String getFilePath() {
    return fileText.getText();
  }

  /**
   * Returns the book-name.
   *
   * @return the book-name
   */
  public String getBookName() {
    return nameText.getText();
  }
}