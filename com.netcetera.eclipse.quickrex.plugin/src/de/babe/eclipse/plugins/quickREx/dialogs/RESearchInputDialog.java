/*******************************************************************************
 * Copyright (c) 2006 Bastian Bergerhoff and others
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.search.RESearchQuery;

/**
 * @author bastian.bergerhoff
 */
public class RESearchInputDialog extends Dialog {

  private Text textField;

  private RESearchQuery query;

  private Button buttonTitle;

  private Button buttonRE;

  private Button buttonTestText;

  private Button buttonDesc;

  private Button buttonSource;

  /**
   * The constructor.
   *
   * @param parentShell
   */
  public RESearchInputDialog(Shell parentShell) {
    super(parentShell);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    getShell().setText(Messages.getString("dialogs.RESearchInputDialog.title")); //$NON-NLS-1$
    // create a composite with standard margins and spacing
    Composite composite = new Composite(parent, SWT.NONE | SWT.RESIZE);
    GridLayout layout = new GridLayout();
    layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
    layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
    layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
    layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
    layout.numColumns = 2;
    composite.setLayout(layout);
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.widthHint = 250;
    composite.setLayoutData(gd);
    applyDialogFont(composite);
    Label searchForLabel = new Label(composite, SWT.NULL);
    searchForLabel.setText(Messages.getString("dialogs.RESearchInputDialog.label.searchFor.text")); //$NON-NLS-1$
    gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gd.grabExcessHorizontalSpace = false;
    searchForLabel.setLayoutData(gd);
    textField = new Text(composite, SWT.BORDER | SWT.SINGLE | SWT.LEAD | SWT.RESIZE);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    textField.setLayoutData(gd);

    Label searchInLabel = new Label(composite, SWT.NULL);
    searchInLabel.setText(Messages.getString("dialogs.RESearchInputDialog.label.searchIn.text")); //$NON-NLS-1$
    gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gd.grabExcessHorizontalSpace = false;
    gd.verticalSpan = 5;
    searchInLabel.setLayoutData(gd);
    buttonTitle = new Button(composite, SWT.CHECK);
    buttonTitle.setText(Messages.getString("dialogs.RESearchInputDialog.checkbox.searchIn.title.text")); //$NON-NLS-1$
    buttonTitle.setSelection((QuickRExPlugin.getDefault().getLastSearchScope() & RESearchQuery.SCOPE_TITLE) == RESearchQuery.SCOPE_TITLE);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    buttonTitle.setLayoutData(gd);

    buttonRE = new Button(composite, SWT.CHECK);
    buttonRE.setText(Messages.getString("dialogs.RESearchInputDialog.checkbox.searchIn.re.text")); //$NON-NLS-1$
    buttonRE.setSelection((QuickRExPlugin.getDefault().getLastSearchScope() & RESearchQuery.SCOPE_RE) == RESearchQuery.SCOPE_RE);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    buttonRE.setLayoutData(gd);

    buttonTestText = new Button(composite, SWT.CHECK);
    buttonTestText.setText(Messages.getString("dialogs.RESearchInputDialog.checkbox.searchIn.testtext.text")); //$NON-NLS-1$
    buttonTestText.setSelection((QuickRExPlugin.getDefault().getLastSearchScope() & RESearchQuery.SCOPE_TESTTEXT) == RESearchQuery.SCOPE_TESTTEXT);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    buttonTestText.setLayoutData(gd);

    buttonDesc = new Button(composite, SWT.CHECK);
    buttonDesc.setText(Messages.getString("dialogs.RESearchInputDialog.checkbox.searchIn.desc.text")); //$NON-NLS-1$
    buttonDesc.setSelection((QuickRExPlugin.getDefault().getLastSearchScope() & RESearchQuery.SCOPE_DESC) == RESearchQuery.SCOPE_DESC);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    buttonDesc.setLayoutData(gd);

    buttonSource = new Button(composite, SWT.CHECK);
    buttonSource.setText(Messages.getString("dialogs.RESearchInputDialog.checkbox.searchIn.source.text")); //$NON-NLS-1$
    buttonSource.setSelection((QuickRExPlugin.getDefault().getLastSearchScope() & RESearchQuery.SCOPE_SOURCE) == RESearchQuery.SCOPE_SOURCE);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    buttonSource.setLayoutData(gd);
    return composite;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
   */
  @Override
  protected void buttonPressed(int buttonId) {
    if (buttonId == IDialogConstants.OK_ID) {
      query = new RESearchQuery(getText(), getSearchScope());
      QuickRExPlugin.getDefault().setLastSearchScope(getSearchScope());
    }
    super.buttonPressed(buttonId);
  }

  /**
   * Returns the RESearchQuery initialized by this dialog.
   *
   * @return the query
   */
  public RESearchQuery getQuery() {
    return query;
  }

  private int getSearchScope() {
    int scope = 0;
    if (buttonTitle.getSelection()) {
      scope += RESearchQuery.SCOPE_TITLE;
    }
    if (buttonRE.getSelection()) {
      scope += RESearchQuery.SCOPE_RE;
    }
    if (buttonTestText.getSelection()) {
      scope += RESearchQuery.SCOPE_TESTTEXT;
    }
    if (buttonDesc.getSelection()) {
      scope += RESearchQuery.SCOPE_DESC;
    }
    if (buttonSource.getSelection()) {
      scope += RESearchQuery.SCOPE_SOURCE;
    }
    return scope;
  }

  private String getText() {
    return textField.getText();
  }

}