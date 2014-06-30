/*******************************************************************************
 * Copyright (c) 2006 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;

/**
 * @author bastian.bergerhoff
 */
public class QuickRExPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  public static final String P_DO_TIMEOUT = "de.babe.eclipse.plugins.quickREx.doTimeout"; //$NON-NLS-1$
  public static final String P_TIMEOUT = "de.babe.eclipse.plugins.quickREx.timeout"; //$NON-NLS-1$
  public static final String P_LIVE_EVAL = "de.babe.eclipse.plugins.quickREx.liveEval"; //$NON-NLS-1$
  private BooleanFieldEditor timeoutCheck;
  private StringFieldEditor timeoutValue;

  /**
   * The constructor
   */
  public QuickRExPreferencesPage() {
    super(FLAT);
    setPreferenceStore(QuickRExPlugin.getDefault().getPreferenceStore());
    setDescription(Messages.getString("preferences.QuickRExPreferencesPage.description")); //$NON-NLS-1$
    initializeDefaults();
  }

  private void initializeDefaults() {
    IPreferenceStore store = getPreferenceStore();
    store.setDefault(P_DO_TIMEOUT, true);
    store.setDefault(P_TIMEOUT, "60"); //$NON-NLS-1$
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
   */
  @Override
  public void createFieldEditors() {
    timeoutCheck = new BooleanFieldEditor(P_DO_TIMEOUT, Messages.getString("preferences.QuickRExPreferencesPage.field.timeOutActive.label"), getFieldEditorParent());  //$NON-NLS-1$
    addField(timeoutCheck);
    timeoutValue = new StringFieldEditor(P_TIMEOUT, Messages.getString("preferences.QuickRExPreferencesPage.field.timeOutValue.label"), getFieldEditorParent()); //$NON-NLS-1$
    addField(timeoutValue);
    adjustGridLayout();
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  @Override
  public void init(IWorkbench workbench) {
  }
}