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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.objects.NamedText;

/**
 * @author bastian.bergerhoff
 */
public class OrganizeTestTextDialog extends Dialog {

  public static final int TYPE_LOAD = 0;

  public static final int TYPE_SAVE = 1;

  public static final int TYPE_ORGANIZE = 2;

  private boolean okPressed;

  private final int type;

  private Text nameText;

  private String textToSave;

  private String nameToSave;

  private Label messageLabel;

  private List nameList;

  private NamedText selectedText;

  private Text testTextField;

  private static final int DELETE_BUTTON_ID = Integer.MAX_VALUE - 1;

  private static final int BROWSE_BUTTON_ID = Integer.MAX_VALUE - 2;

  private String selectedPath;

  /**
   * The constructor.
   *
   * @param p_parentShell
   * @param p_type
   */
  public OrganizeTestTextDialog(Shell p_parentShell, int p_type) {
    super(p_parentShell);
    this.type = p_type;
  }

  /**
   * Returns the selected text or <code>null</code> if a button other than 'ok' was pressed (or the dialog is not in 'LOAD'-mode).
   *
   * @return the selected text or <code>null</code>
   */
  public NamedText getSelectedText() {
    if (okPressed) {
      return this.selectedText;
    } else {
      return null;
    }
  }

  /**
   * Sets the text to be saved to the passed text.
   *
   * @param p_text
   *          the text to be saved
   */
  public void setTextToSave(String p_text) {
    this.textToSave = p_text;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    getShell().setText(Messages.getString("dialogs.OrganizeTestTextDialog.title")); //$NON-NLS-1$
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
    switch (type) {
      case TYPE_SAVE:
        return createDialogAreaContentsForSave(composite);
      case TYPE_LOAD:
        return createDialogAreaContentsForLoad(composite);
      case TYPE_ORGANIZE:
        return createDialogAreaContentsForOrganize(composite);
      default:
        throw new IllegalStateException(Messages.getString("dialogs.OrganizeTestTextDialog.errror.message1") + type); //$NON-NLS-1$
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    switch (type) {
      case TYPE_SAVE:
        super.createButtonsForButtonBar(parent);
        return;
      case TYPE_LOAD:
        createButtonsForLoad(parent);
        return;
      case TYPE_ORGANIZE:
        createButtonsForOrganize(parent);
        return;
      default:
        throw new IllegalStateException(Messages.getString("dialogs.OrganizeTestTextDialog.error.message2") + type); //$NON-NLS-1$
    }
  }

  private void createButtonsForLoad(Composite parent) {
    createButton(parent, BROWSE_BUTTON_ID, Messages.getString("dialogs.OrganizeTestTextDialog.button.browse"), false); //$NON-NLS-1$
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  private void createButtonsForOrganize(Composite parent) {
    createButton(parent, DELETE_BUTTON_ID, Messages.getString("dialogs.OrganizeTestTextDialog.button.delete"), false); //$NON-NLS-1$
    createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("dialogs.OrganizeTestTextDialog.button.close"), true); //$NON-NLS-1$
  }

  private Control createDialogAreaContentsForOrganize(Composite p_composite) {
    messageLabel = new Label(p_composite, SWT.NULL);
    messageLabel.setText(""); //$NON-NLS-1$
    GridData gd = new GridData(GridData.VERTICAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    gd.horizontalSpan = 2;
    gd.widthHint = 400;
    messageLabel.setLayoutData(gd);
    Label nameLabel = new Label(p_composite, SWT.NULL);
    nameLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.nameLabel.text1")); //$NON-NLS-1$
    gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gd.grabExcessHorizontalSpace = false;
    nameLabel.setLayoutData(gd);
    nameList = new List(p_composite, SWT.V_SCROLL);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    gd.heightHint = 100;
    nameList.setLayoutData(gd);
    nameList.setItems(QuickRExPlugin.getDefault().getTestTextNames());
    nameList.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (nameList.getSelection() != null && nameList.getSelection().length > 0) {
          testTextField.setText(QuickRExPlugin.getDefault().getTestTextByName(nameList.getSelection()[0]).getText());
        }
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });
    Label textLabel = new Label(p_composite, SWT.NULL);
    textLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.textLabel.text1")); //$NON-NLS-1$
    gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gd.grabExcessHorizontalSpace = false;
    textLabel.setLayoutData(gd);
    testTextField = new Text(p_composite, SWT.READ_ONLY | SWT.LEAD | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.RESIZE);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    gd.grabExcessVerticalSpace = true;
    gd.heightHint = 250;
    gd.widthHint = 400;
    testTextField.setLayoutData(gd);
    testTextField.setBackground(new Color(getShell().getDisplay(), new RGB(255, 255, 255)));
    return p_composite;
  }

  private Control createDialogAreaContentsForLoad(Composite p_composite) {
    messageLabel = new Label(p_composite, SWT.NULL);
    messageLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.messageLabel.text1")); //$NON-NLS-1$
    GridData gd = new GridData(GridData.VERTICAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    gd.horizontalSpan = 2;
    gd.widthHint = 400;
    messageLabel.setLayoutData(gd);
    Label nameLabel = new Label(p_composite, SWT.NULL);
    nameLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.nameLabel.text2")); //$NON-NLS-1$
    gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gd.grabExcessHorizontalSpace = false;
    nameLabel.setLayoutData(gd);
    nameList = new List(p_composite, SWT.V_SCROLL);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    gd.heightHint = 100;
    nameList.setLayoutData(gd);
    nameList.setItems(QuickRExPlugin.getDefault().getTestTextNames());
    nameList.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (nameList.getSelection() != null && nameList.getSelection().length > 0) {
          testTextField.setText(QuickRExPlugin.getDefault().getTestTextByName(nameList.getSelection()[0]).getText());
          selectedPath = null;
        }
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });
    Label textLabel = new Label(p_composite, SWT.NULL);
    textLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.textLabel.text2")); //$NON-NLS-1$
    gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gd.grabExcessHorizontalSpace = false;
    textLabel.setLayoutData(gd);
    testTextField = new Text(p_composite, SWT.READ_ONLY | SWT.LEAD | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.RESIZE);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    gd.grabExcessVerticalSpace = true;
    gd.heightHint = 250;
    gd.widthHint = 400;
    testTextField.setLayoutData(gd);
    testTextField.setBackground(new Color(getShell().getDisplay(), new RGB(255, 255, 255)));
    return p_composite;
  }

  private Control createDialogAreaContentsForSave(Composite p_composite) {
    messageLabel = new Label(p_composite, SWT.NULL);
    messageLabel.setText(""); //$NON-NLS-1$
    GridData gd = new GridData(GridData.VERTICAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    gd.horizontalSpan = 2;
    gd.widthHint = 400;
    messageLabel.setLayoutData(gd);
    Label nameLabel = new Label(p_composite, SWT.NULL);
    nameLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.nameLabel.text3")); //$NON-NLS-1$
    gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gd.grabExcessHorizontalSpace = false;
    nameLabel.setLayoutData(gd);
    nameText = new Text(p_composite, SWT.BORDER);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    nameText.setLayoutData(gd);
    nameText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent p_e) {
        handleNameTextModified();
      }
    });
    Label textLabel = new Label(p_composite, SWT.NULL);
    textLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.textLabel.text3")); //$NON-NLS-1$
    gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gd.grabExcessHorizontalSpace = false;
    textLabel.setLayoutData(gd);
    testTextField = new Text(p_composite, SWT.READ_ONLY | SWT.LEAD | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.RESIZE);
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
    gd.grabExcessHorizontalSpace = true;
    gd.grabExcessVerticalSpace = true;
    gd.heightHint = 250;
    gd.widthHint = 400;
    testTextField.setLayoutData(gd);
    testTextField.setBackground(new Color(getShell().getDisplay(), new RGB(255, 255, 255)));
    testTextField.setText(this.textToSave);
    return p_composite;
  }

  private void handleNameTextModified() {
    getButton(IDialogConstants.OK_ID).setEnabled(nameText.getText() != null && nameText.getText().trim().length() > 0);
    if (QuickRExPlugin.getDefault().testTextNameExists(nameText.getText())) {
      messageLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.messageLabel.text3")); //$NON-NLS-1$
    } else if (nameText.getText() == null || nameText.getText().trim().length() == 0) {
      messageLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.messageLabel.text4")); //$NON-NLS-1$
    } else {
      messageLabel.setText(""); //$NON-NLS-1$
    }
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
    if (BROWSE_BUTTON_ID == buttonId) {
      browsePressed();
    }
  }

  private void browsePressed() {
    nameList.deselectAll();
    FileDialog subDlg = new FileDialog(getShell(), SWT.OPEN);
    selectedPath = subDlg.open();
    if (selectedPath != null) {
      try {
        selectedText = getFileContents(this.selectedPath);
        testTextField.setText(this.selectedText.getText());
        testTextField.redraw();
      } catch (Exception e) {
        messageLabel.setText(e.getMessage());
        messageLabel.redraw();
      }
    }
  }

  private void deletePressed() {
    if (nameList.getSelection() == null || nameList.getSelection().length == 0) {
      messageLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.messageLabel.text5")); //$NON-NLS-1$
      messageLabel.redraw();
      return;
    } else {
      QuickRExPlugin.getDefault().deleteTestTextByName(nameList.getSelection()[0]);
      testTextField.setText(""); //$NON-NLS-1$
      nameList.setItems(QuickRExPlugin.getDefault().getTestTextNames());
      messageLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.messageLabel.text6")); //$NON-NLS-1$
      messageLabel.redraw();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.Dialog#okPressed()
   */
  @Override
  protected void okPressed() {
    okPressed = true;
    if (type == TYPE_SAVE) {
      if (nameText.getText() == null || nameText.getText().trim().length() == 0) {
        messageLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.messageLabel.text7")); //$NON-NLS-1$
        messageLabel.redraw();
        return;
      } else {
        this.nameToSave = nameText.getText();
      }
    } else if (type == TYPE_LOAD) {
      if (this.selectedPath != null) {
        try {
          this.selectedText = getFileContents(this.selectedPath);
        } catch (Exception e) {
          messageLabel.setText(e.getMessage());
          messageLabel.redraw();
          return;
        }
      } else if (nameList.getSelection() == null || nameList.getSelection().length == 0) {
        messageLabel.setText(Messages.getString("dialogs.OrganizeTestTextDialog.messageLabel.text8")); //$NON-NLS-1$
        messageLabel.redraw();
        return;
      } else {
        this.selectedText = QuickRExPlugin.getDefault().getTestTextByName(nameList.getSelection()[0]);
      }
    }
    this.close();
  }

  private NamedText getFileContents(String p_filePath) throws Exception {
    StringBuilder contents = new StringBuilder();
    File file = new File(p_filePath);
    if (!(file.exists() && file.canRead()) || file.isDirectory()) {
      throw new Exception(Messages.getString("dialogs.OrganizeTestTextDialog.error.message3")); //$NON-NLS-1$
    } else {
      try (FileInputStream fis = new FileInputStream(file);
          Reader reader = new InputStreamReader(fis)) {
        int read = 0;
        char[] buffer = new char[8192];
        while ((read = reader.read(buffer)) != -1) {
          contents.append(buffer, 0, read);
        }
      }
    }
    return new NamedText(p_filePath, contents.toString());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
   */
  @Override
  protected void cancelPressed() {
    okPressed = false;
    this.close();
  }

  /**
   * Returns the NamedText (name and contents of a piece of text) to be saved or <code>null</code> if a button other than 'ok' was pressed or the
   * dialog is not in 'SAVE'-mode.
   *
   * @return the NamedText to be saved or <code>null</code>
   */
  public NamedText getSaveInformation() {
    if (okPressed) {
      return new NamedText(this.nameToSave, this.textToSave == null ? "" : this.textToSave); //$NON-NLS-1$
    } else {
      return null;
    }
  }
}