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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.babe.eclipse.plugins.quickREx.Messages;

/**
 * @author bastian.bergerhoff
 */
public class SimpleTextDialog extends Dialog {

  private Text textField;

  private String title;

  private String contents;

  /**
   * The constructor
   * 
   * @param p_parentShell
   * @param p_title
   * @param p_contents
   */
  public SimpleTextDialog(Shell p_parentShell, String p_title, String p_contents) {
    super(p_parentShell);
    this.title = p_title;
    this.contents = p_contents;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    getShell().setText(title);
    // create a composite with standard margins and spacing
    Composite composite = new Composite(parent, SWT.NONE | SWT.RESIZE);
    GridLayout layout = new GridLayout();
    layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
    layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
    layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
    layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
    layout.numColumns = 1;
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    applyDialogFont(composite);
    textField = new Text(composite, SWT.READ_ONLY | SWT.LEAD | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.RESIZE);
    GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    gd.grabExcessVerticalSpace = true;
    gd.heightHint = 250;
    gd.widthHint = 400;
    textField.setLayoutData(gd);
    textField.setBackground(new Color(getShell().getDisplay(), new RGB(255, 255, 255)));
    textField.setText(contents);

    return composite;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("dialogs.SimpleTextDialog.button.close"), true); //$NON-NLS-1$
  }
}