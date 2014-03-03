/*******************************************************************************
 * Copyright (c) 2005 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;

/**
 * @author bastian.bergerhoff
 */
public class OrganizeREsDialog extends Dialog {

  private Label messageLabel;

  private List reList;

  private static final int DELETE_BUTTON_ID = Integer.MAX_VALUE - 1;

  /**
   * The constructor
   * 
   * @param p_parentShell
   */
  public OrganizeREsDialog(Shell p_parentShell) {
    super(p_parentShell);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, DELETE_BUTTON_ID, Messages.getString("dialogs.OrganizeREsDialog.button.delete"), false); //$NON-NLS-1$
    createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("dialogs.OrganizeREsDialog.button.close"), true); //$NON-NLS-1$
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    getShell().setText(Messages.getString("dialogs.OrganizeREsDialog.title")); //$NON-NLS-1$
    // create a composite with standard margins and spacing
    Composite composite = new Composite(parent, SWT.NONE | SWT.RESIZE);
    GridLayout layout = new GridLayout();
    layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
    layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
    layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
    layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
    layout.numColumns = 2;
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    applyDialogFont(composite);
    messageLabel = new Label(composite, SWT.NULL);
    messageLabel.setText(Messages.getString("dialogs.OrganizeREsDialog.messageLabel1")); //$NON-NLS-1$
    GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    gd.horizontalSpan = 2;
    gd.widthHint = 400;
    messageLabel.setLayoutData(gd);
    Label nameLabel = new Label(composite, SWT.NULL);
    nameLabel.setText(Messages.getString("dialogs.OrganizeREsDialog.nameLabel1")); //$NON-NLS-1$
    gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gd.grabExcessHorizontalSpace = false;
    nameLabel.setLayoutData(gd);
    reList = new List(composite, SWT.MULTI | SWT.V_SCROLL);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
    gd.grabExcessHorizontalSpace = true;
    gd.grabExcessVerticalSpace = true;
    gd.heightHint = 100;
    reList.setLayoutData(gd);
    reList.setItems(QuickRExPlugin.getDefault().getRegularExpressions());
    return composite;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
   */
  @Override
  protected void cancelPressed() {
    this.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
   */
  @Override
  protected void buttonPressed(int buttonId) {
    super.buttonPressed(buttonId);
    if (DELETE_BUTTON_ID == buttonId) {
      deletePressed();
    }
  }

  private void deletePressed() {
    if (reList.getSelection() == null || reList.getSelection().length == 0) {
      messageLabel.setText(Messages.getString("dialogs.OrganizeREsDialog.messageLabel2")); //$NON-NLS-1$
      messageLabel.redraw();
      return;
    } else {
      QuickRExPlugin.getDefault().deleteRegularExpressions(reList.getSelection());
      reList.setItems(QuickRExPlugin.getDefault().getRegularExpressions());
      messageLabel.setText(Messages.getString("dialogs.OrganizeREsDialog.messageLabel3")); //$NON-NLS-1$
      messageLabel.redraw();
    }
  }

}