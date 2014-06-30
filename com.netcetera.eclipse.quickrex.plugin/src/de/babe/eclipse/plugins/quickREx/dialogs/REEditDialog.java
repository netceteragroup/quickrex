/*******************************************************************************
 * Copyright (c) 2007 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.regexp.RECompletionProposal;
import de.babe.eclipse.plugins.quickREx.views.QuickRExView;

/**
 * @author bastian.bergerhoff
 */
public class REEditDialog extends Dialog {

  private StyledText text;
  private Label additionalREInfoLabel;
  private QuickRExView view;
  private List<String> categories;
  private Map<String, List<RECompletionProposal>> expressions;
  private String currentText = "";

  /**
   * The constructor.
   *
   * @param view
   * @param shell
   */
  public REEditDialog(QuickRExView view, Shell shell) {
    super(shell);
    this.view = view;
    categories = QuickRExPlugin.getDefault().getRECategories();
    expressions = QuickRExPlugin.getDefault().getREMappings();
  }

  /**
   * Returns the content of the text-field.
   *
   * @return the contents of the text-field.
   */
  public String getSelectedText() {
    return currentText;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, Messages.getString("dialogs.SimpleTextDialog.button.close"), true); //$NON-NLS-1$
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    getShell().setText(Messages.getString("dialogs.REEditDialog.title")); //$NON-NLS-1$
    // create a composite with standard margins and spacing
    Composite composite = new Composite(parent, SWT.NONE | SWT.RESIZE);
    GridLayout layout = new GridLayout();
    layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
    layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
    layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
    layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);

    layout.numColumns = 3;
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));

    createTextLine(composite);
    createSnippetControls(composite);

    updateMarkup();

    applyDialogFont(composite);
    return composite;
  }

  private void createSnippetControls(Composite composite) {
    for (final String catName : categories) {
      Label catLab = new Label(composite, SWT.NONE);
      catLab.setText(catName);
      GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
      gd.grabExcessHorizontalSpace = false;
      catLab.setLayoutData(gd);
      final Combo catCombo = new Combo(composite, SWT.SINGLE | SWT.READ_ONLY);
      catCombo.setItems(createComboItems(expressions.get(catName)));
      catCombo.addSelectionListener(new SelectionListener() {

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
          if (catCombo.getSelectionIndex() >= 0) {
            additionalREInfoLabel.setText(expressions.get(catName).get(catCombo.getSelectionIndex()).getAdditionalInfo());
          }
        } });
      catCombo.addFocusListener(new FocusListener() {

        @Override
        public void focusGained(FocusEvent e) {
          if (catCombo.getSelectionIndex() >= 0) {
            additionalREInfoLabel.setText(expressions.get(catName).get(catCombo.getSelectionIndex()).getAdditionalInfo());
          } else {
            additionalREInfoLabel.setText(""); //$NON-NLS-1$
          }
        }

        @Override
        public void focusLost(FocusEvent e) {
        } });
      gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      gd.grabExcessHorizontalSpace = true;
      catCombo.setLayoutData(gd);
      final Button catButton = new Button(composite, SWT.PUSH);
      catButton.setText(Messages.getString("dialogs.REEditDialog.insertbutton.title")); //$NON-NLS-1$
      gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
      gd.grabExcessHorizontalSpace = false;
      catButton.setLayoutData(gd);
      catButton.addSelectionListener(new SelectionListener() {

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
          if (catCombo.getSelectionIndex() >= 0) {
            text.insert(expressions.get(catName).get(catCombo.getSelectionIndex()).getInsertString());
            text.setSelection(text.getSelection().x + expressions.get(catName).get(catCombo.getSelectionIndex()).getInsertString().length());
            additionalREInfoLabel.setText(expressions.get(catName).get(catCombo.getSelectionIndex()).getAdditionalInfo());
          }
        } });
    }

    if (!categories.isEmpty()) {
      additionalREInfoLabel = new Label(composite, SWT.WRAP);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.horizontalSpan = 3;
      gd.widthHint = 450;
      gd.heightHint = 70;
      additionalREInfoLabel.setLayoutData(gd);
      additionalREInfoLabel.setText(""); //$NON-NLS-1$
      additionalREInfoLabel.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
    }
  }

  private String[] createComboItems(List<RECompletionProposal> list) {
    List<String> expressionNames = new ArrayList<>();
    for (RECompletionProposal element : list) {
      if (element != null) {
        expressionNames.add(element.getDisplayString());
      }
    }
    return expressionNames.toArray(new String[expressionNames.size()]);
  }

  private void createTextLine(Composite composite) {
    text = new StyledText(composite, SWT.BORDER | SWT.SINGLE);
    GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    gd.horizontalSpan = 3;
    gd.widthHint = 450;
    text.setLayoutData(gd);
    currentText = view.getRegularExpression();
    text.setText(currentText);
    text.setSelection(view.getLastComboSelection());
    text.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent p_e) {
        handleTextModified();
      }
    });
  }

  private void handleTextModified() {
    updateMarkup();
    currentText = text.getText();
    view.setRegularExpression(currentText);
  }

  private void updateMarkup() {
    char[] chars = text.getText().toCharArray();
    List<StyleRange> ranges = new ArrayList<>();
    Stack bStack = new Stack();
    int colorCounter = 3;
    boolean ignoreOnce = false; // flag to ignore next char (escaped brackets...)
    boolean ignoreUntilUnset = false; // flag to ignore until further notice (brackets in character classes)
    for (int i = 0; i < chars.length; i++) {
      char currentChar = chars[i];
      if (!ignoreOnce & !ignoreUntilUnset) {
        if (currentChar == '\\') {
          // ignore the next character, it is escaped...
          ignoreOnce = true;
        } else if (currentChar == '[') {
          ignoreUntilUnset = true;
        } else if (currentChar == '(') {
          StyleRange range = new StyleRange(i, 1, getShell().getDisplay().getSystemColor(colorCounter), getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE), SWT.BOLD);
          ranges.add(range);
          bStack.push(colorCounter);
          colorCounter += 2;
          if (colorCounter == 15) { // avoid shades of grey...
            colorCounter = 3;
          }
          if (colorCounter == 7) { // yellow is practically invisible...
            colorCounter = 9;
          }
        } else if (currentChar == ')') {
          try {
            int openingColor = ((Integer) bStack.pop()).intValue();
            StyleRange range = new StyleRange(i, 1, getShell().getDisplay().getSystemColor(openingColor), getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE), SWT.BOLD);
            ranges.add(range);
          } catch (Exception e) { // no matching opening bracket was found...
            StyleRange range = new StyleRange(i, 1, getShell().getDisplay().getSystemColor(colorCounter), getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE), SWT.BOLD);
            ranges.add(range);
            colorCounter += 2;
            if (colorCounter == 15) { // avoid shades of grey...
              colorCounter = 3;
            }
            if (colorCounter == 7) { // yellow is practically invisible...
              colorCounter = 9;
            }
          }
        }
      } else if (!ignoreOnce & ignoreUntilUnset) {
        if (currentChar == ']') {
          ignoreUntilUnset = false;
        }
      } else if (ignoreOnce) {
        ignoreOnce = false;
      }
    }
    text.setStyleRanges(ranges.toArray(new StyleRange[ranges.size()]));
  }
}