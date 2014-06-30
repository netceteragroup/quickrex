/*******************************************************************************
 * Copyright (c) 2006 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.objects.RELibraryEntryEditorInput;
import de.babe.eclipse.plugins.quickREx.views.RELibraryView;

/**
 * @author bastian.bergerhoff
 */
public class RELibraryEntryEditor extends FormEditor {

  public static final String ID = "de.babe.eclipse.plugins.quickREx.editors.RELibraryEntryEditor"; //$NON-NLS-1$

  private boolean isDirty = false;

  private RELibraryEntryFormPage entryPage;

  /**
   * The constructor.
   */
  public RELibraryEntryEditor() {
    super();
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#getTitleToolTip()
   */
  @Override
  public String getTitleToolTip() {
    return ""; //$NON-NLS-1$
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
   */
  @Override
  public void init(IEditorSite site, IEditorInput input) {
    if (!(input instanceof RELibraryEntryEditorInput)) {
      throw new IllegalStateException();
    }
    setSite(site);
    setInput(input);
    setPartName(((RELibraryEntryEditorInput) input).getRELibraryEntry().getTitle());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
   */
  @Override
  protected void addPages() {
    try {
      entryPage = new RELibraryEntryFormPage(this);
      addPage(entryPage);
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public void doSave(IProgressMonitor monitor) {
    if (getMyEditorInput().getRELibraryEntry().getCategory().containsEntryWithTitle(entryPage.getTitleText().getText())
        && !getMyEditorInput().getRELibraryEntry().getTitle().equals(entryPage.getTitleText().getText())) {
      MessageDialog.openError(getSite().getShell(), Messages.getString("editors.RELibraryEntryEditor.titleExists.dlg.title"), //$NON-NLS-1$
          Messages.getString("editors.RELibraryEntryEditor.titleExists.dlg.text")); //$NON-NLS-1$
      monitor.setCanceled(true);
      return;
    }
    entryPage.doSave(monitor);
    setPartName(((RELibraryEntryEditorInput) getEditorInput()).getRELibraryEntry().getTitle());
    setIsDirty(false);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart#doSaveAs()
   */
  @Override
  public void doSaveAs() {
    // not possible since isSaveAsAllowed() returns false
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
   */
  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  /**
   * Returns the EditorInput, already of the correct class.
   *
   * @return the EditorInput of this editor
   */
  private RELibraryEntryEditorInput getMyEditorInput() {
    return (RELibraryEntryEditorInput) super.getEditorInput();
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.ISaveablePart#isDirty()
   */
  @Override
  public boolean isDirty() {
    return this.isDirty;
  }

  /**
   * Sets the dirty-state of the editor to the passed value, firing a
   * PropertyChangeEvent.
   *
   * @param flag the new value for the dirty-state
   */
  public void setIsDirty(boolean flag) {
    this.isDirty = flag;
    firePropertyChange(PROP_DIRTY);
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  @Override
  public void setFocus() {
    if (QuickRExPlugin.getDefault().isLinkRELibViewWithEditor()) {
      try {
        RELibraryView view = (RELibraryView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(RELibraryView.ID);
        if (view != null) {
          view.expandToEntry(getMyEditorInput().getRELibraryEntry());
        }
      } catch (Exception e) {
        // nop
      }
    }
    super.setFocus();
  }
}