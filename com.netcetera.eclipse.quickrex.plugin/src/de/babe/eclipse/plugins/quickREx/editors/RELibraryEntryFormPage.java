/*******************************************************************************
 * Copyright (c) 2006, 2007 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.contentassist.SubjectControlContentAssistant;
import org.eclipse.jface.contentassist.TextContentAssistSubjectAdapter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.contentassist.ContentAssistHandler;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.objects.RELibraryEntry;
import de.babe.eclipse.plugins.quickREx.objects.RELibraryEntryEditorInput;
import de.babe.eclipse.plugins.quickREx.regexp.RegExpContentAssistProcessor;
import de.babe.eclipse.plugins.quickREx.views.QuickRExView;

/**
 * @author bastian.bergerhoff
 */
public class RELibraryEntryFormPage extends FormPage {

  private static final String ID = "LibEntryFormPage"; //$NON-NLS-1$

  private static final String TITLE = "Details"; //$NON-NLS-1$

  private Text regExpText;

  private Text titleText;

  private Text testTextText;

  private Text descriptionText;

  private Text sourceText;

  private SubjectControlContentAssistant regExpContentAssistant;

  /**
   * The constructor
   * 
   * @param editor
   * @param title
   */
  public RELibraryEntryFormPage(FormEditor editor) {
    super(editor, ID, TITLE);
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
   */
  @Override
  protected void createFormContent(IManagedForm managedForm) {
    FormToolkit tk = managedForm.getToolkit();
    ScrolledForm form = managedForm.getForm();
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    form.getBody().setLayout(layout);

    GridData gd;
    // First row...
    Label titleLabel = tk.createLabel(form.getBody(), Messages.getString("editors.RELibraryEntryFormPage.title.label")); //$NON-NLS-1$
    gd = new GridData();
    gd.grabExcessHorizontalSpace = false;
    titleLabel.setLayoutData(gd);
    titleText = new Text(form.getBody(), SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
    titleText.setEditable(!((RELibraryEntryEditorInput)getEditorInput()).isReadOnly());
    titleText.setText(((RELibraryEntryEditorInput)getEditorInput()).getRELibraryEntry().getTitle());
    titleText.addModifyListener(new ModifyListener() {
      /* (non-Javadoc)
       * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
       */
      @Override
      public void modifyText(ModifyEvent e) {
        ((RELibraryEntryEditor)getEditor()).setIsDirty(true);
      }
    });
    titleText.setFont(JFaceResources.getFont(QuickRExView.EDITOR_FONT_KEY));
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
    gd.horizontalSpan = 1;
    gd.grabExcessHorizontalSpace = true;
    titleText.setLayoutData(gd);
    tk.adapt(titleText, true, true);

    // Second row...
    Label regExpLabel = tk.createLabel(form.getBody(), Messages.getString("editors.RELibraryEntryFormPage.re.label")); //$NON-NLS-1$
    gd = new GridData();
    gd.grabExcessHorizontalSpace = false;
    regExpLabel.setLayoutData(gd);
    regExpText = new Text(form.getBody(), SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
    regExpText.setEditable(!((RELibraryEntryEditorInput)getEditorInput()).isReadOnly());
    regExpText.setText(((RELibraryEntryEditorInput)getEditorInput()).getRELibraryEntry().getRe());
    regExpText.addModifyListener(new ModifyListener() {
      /* (non-Javadoc)
       * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
       */
      @Override
      public void modifyText(ModifyEvent e) {
        ((RELibraryEntryEditor)getEditor()).setIsDirty(true);
      }
    });
    regExpText.setFont(JFaceResources.getFont(QuickRExView.EDITOR_FONT_KEY));
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
    gd.horizontalSpan = 1;
    gd.grabExcessHorizontalSpace = true;
    regExpText.setLayoutData(gd);
    tk.adapt(regExpText, true, true);
    if (!((RELibraryEntryEditorInput)getEditorInput()).isReadOnly()) {
      createRegExpContentAssist();
    }

    // Third row...
    Label testTextLabel = tk.createLabel(form.getBody(), Messages.getString("editors.RELibraryEntryFormPage.testtext.label")); //$NON-NLS-1$
    gd = new GridData();
    gd.grabExcessHorizontalSpace = false;
    testTextLabel.setLayoutData(gd);
    testTextText = new Text(form.getBody(), SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    testTextText.setEditable(!((RELibraryEntryEditorInput)getEditorInput()).isReadOnly());
    testTextText.setText(((RELibraryEntryEditorInput)getEditorInput()).getRELibraryEntry().getTesttext());
    testTextText.addModifyListener(new ModifyListener() {
      /* (non-Javadoc)
       * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
       */
      @Override
      public void modifyText(ModifyEvent e) {
        ((RELibraryEntryEditor)getEditor()).setIsDirty(true);
      }
    });
    testTextText.setFont(JFaceResources.getFont(QuickRExView.EDITOR_FONT_KEY));
    gd = new GridData(GridData.FILL_BOTH);
    gd.horizontalSpan = 1;
    gd.grabExcessHorizontalSpace = true;
    testTextText.setLayoutData(gd);
    tk.adapt(testTextText, true, true);

    // Fourth row...
    Label descriptionLabel = tk.createLabel(form.getBody(), Messages.getString("editors.RELibraryEntryFormPage.desc.label")); //$NON-NLS-1$
    gd = new GridData();
    gd.grabExcessHorizontalSpace = false;
    descriptionLabel.setLayoutData(gd);
    descriptionText = new Text(form.getBody(), SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    descriptionText.setEditable(!((RELibraryEntryEditorInput)getEditorInput()).isReadOnly());
    descriptionText.setText(((RELibraryEntryEditorInput)getEditorInput()).getRELibraryEntry().getDescription());
    descriptionText.addModifyListener(new ModifyListener() {
      /* (non-Javadoc)
       * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
       */
      @Override
      public void modifyText(ModifyEvent e) {
        ((RELibraryEntryEditor)getEditor()).setIsDirty(true);
      }
    });
    descriptionText.setFont(JFaceResources.getFont(QuickRExView.EDITOR_FONT_KEY));
    gd = new GridData(GridData.FILL_BOTH);
    gd.horizontalSpan = 1;
    gd.grabExcessHorizontalSpace = true;
    descriptionText.setLayoutData(gd);
    tk.adapt(descriptionText, true, true);

    // Fifth row...
    Label sourceLabel = tk.createLabel(form.getBody(), Messages.getString("editors.RELibraryEntryFormPage.source.label")); //$NON-NLS-1$
    gd = new GridData();
    gd.grabExcessHorizontalSpace = false;
    sourceLabel.setLayoutData(gd);
    sourceText = new Text(form.getBody(), SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    sourceText.setEditable(!((RELibraryEntryEditorInput)getEditorInput()).isReadOnly());
    sourceText.setText(((RELibraryEntryEditorInput)getEditorInput()).getRELibraryEntry().getSource());
    sourceText.addModifyListener(new ModifyListener() {
      /* (non-Javadoc)
       * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
       */
      @Override
      public void modifyText(ModifyEvent e) {
        ((RELibraryEntryEditor)getEditor()).setIsDirty(true);
      }
    });
    sourceText.setFont(JFaceResources.getFont(QuickRExView.EDITOR_FONT_KEY));
    gd = new GridData(GridData.FILL_BOTH);
    gd.horizontalSpan = 1;
    gd.grabExcessHorizontalSpace = true;
    sourceText.setLayoutData(gd);
    tk.adapt(sourceText, true, true);
  }

  /**
   * Returns the Text holding the title of the entry
   * 
   * @return the Text holding the title
   */
  protected Text getTitleText() {
    return this.titleText;
  }

  private void createRegExpContentAssist() {
    regExpContentAssistant = new SubjectControlContentAssistant();
    regExpContentAssistant.enableAutoActivation(false);
    regExpContentAssistant.enableAutoInsert(true);
    regExpContentAssistant.setContentAssistProcessor(new RegExpContentAssistProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
    regExpContentAssistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
    regExpContentAssistant.setRestoreCompletionProposalSize(QuickRExPlugin.getDefault().getDialogSettings()); //$NON-NLS-1$
    regExpContentAssistant.setInformationControlCreator(new IInformationControlCreator() {
      /*
       * @see org.eclipse.jface.text.IInformationControlCreator#createInformationControl(org.eclipse.swt.widgets.Shell)
       */
      @Override
      public IInformationControl createInformationControl(Shell parent) {
        return new DefaultInformationControl(parent);
      }
    });
    regExpContentAssistant.install(new TextContentAssistSubjectAdapter(regExpText));
    ContentAssistHandler.createHandlerForText(regExpText, regExpContentAssistant);
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public void doSave(IProgressMonitor monitor) {
    RELibraryEntry myEntry = ((RELibraryEntryEditorInput)getEditorInput()).getRELibraryEntry();
    myEntry.setTitle(titleText.getText());
    myEntry.setRe(regExpText.getText());
    myEntry.setTesttext(testTextText.getText());
    myEntry.setDescription(descriptionText.getText());
    myEntry.setSource(sourceText.getText());
    myEntry.doSave(monitor);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPart#dispose()
   */
  @Override
  public void dispose() {
    regExpText.dispose();
    regExpText = null;
    super.dispose();
  }
}