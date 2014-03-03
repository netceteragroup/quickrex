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

import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

import de.babe.eclipse.plugins.quickREx.factories.RELibraryEntryFactory;

/**
 * @author bastian.bergerhoff
 */
public class RELibraryEntryPersistable implements IPersistableElement {

  private final RELibraryEntry entry;

  public final static String ENTRY_PATH_KEY = "de.babe.eclipse.plugins.quickREx.objects.RELibraryEntry.PATH"; //$NON-NLS-1$

  /**
   * The constructor
   * 
   * @param entry
   */
  public RELibraryEntryPersistable(RELibraryEntry entry) {
    this.entry = entry;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IPersistableElement#getFactoryId()
   */
  public String getFactoryId() {
    return RELibraryEntryFactory.ID;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IPersistableElement#saveState(org.eclipse.ui.IMemento)
   */
  public void saveState(IMemento memento) {
    try {
      memento.putString(ENTRY_PATH_KEY, entry.getPath());
    } catch (Exception e) {
      // nop
    }
  }
}