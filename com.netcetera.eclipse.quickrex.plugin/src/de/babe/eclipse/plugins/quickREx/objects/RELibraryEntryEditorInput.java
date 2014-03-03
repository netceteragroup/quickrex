/*******************************************************************************
 * Copyright (c) 2006 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.objects;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author bastian.bergerhoff
 */
public class RELibraryEntryEditorInput implements IEditorInput {

  private final RELibraryEntry entry;

  private IPersistableElement persistableElement;

  private boolean readOnly;

  /**
   * The constructor
   * 
   * @param entry
   * @param readOnly
   */
  public RELibraryEntryEditorInput(RELibraryEntry entry, boolean readOnly) {
    this.entry = entry;
    this.persistableElement = new RELibraryEntryPersistable(entry);
    this.readOnly = readOnly;
  }

  /**
   * Returns <code>true</code> if and only if this EditorInput is read-only
   * 
   * @return <code>true</code> if and only if this EditorInput is read-only
   */
  public boolean isReadOnly() {
    return this.readOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IEditorInput#exists()
   */
  @Override
  public boolean exists() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IEditorInput#getName()
   */
  @Override
  public String getName() {
    return entry.getPath();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IEditorInput#getPersistable()
   */
  @Override
  public IPersistableElement getPersistable() {
    return this.persistableElement;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IEditorInput#getToolTipText()
   */
  @Override
  public String getToolTipText() {
    return ""; //$NON-NLS-1$
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
   */
  @Override
  public Object getAdapter(Class adapter) {
    return null;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object other) {
    try {
      if (((RELibraryEntryEditorInput)other).getRELibraryEntry() == getRELibraryEntry()) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns the RELibraryEntry which is represented by this EditorInput
   * 
   * @return the RELibraryEntry which is represented by this EditorInput
   */
  public RELibraryEntry getRELibraryEntry() {
    return entry;
  }
}