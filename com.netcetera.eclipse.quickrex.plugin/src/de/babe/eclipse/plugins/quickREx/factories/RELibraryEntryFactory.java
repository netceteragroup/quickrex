/*******************************************************************************
 * Copyright (c) 2006 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.factories;

import java.util.StringTokenizer;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.objects.REBook;
import de.babe.eclipse.plugins.quickREx.objects.RELibraryEntry;
import de.babe.eclipse.plugins.quickREx.objects.RELibraryEntryEditorInput;
import de.babe.eclipse.plugins.quickREx.objects.RELibraryEntryPersistable;

/**
 * @author bastian.bergerhoff
 */
public class RELibraryEntryFactory implements IElementFactory {

  public final static String ID = "de.babe.eclipse.plugins.quickREx.factories.RELibraryEntryFactory"; //$NON-NLS-1$

  /* (non-Javadoc)
   * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
   */
  @Override
  public IAdaptable createElement(IMemento memento) {
    try {
      String entryPath = memento.getString(RELibraryEntryPersistable.ENTRY_PATH_KEY);
      StringTokenizer tok = new StringTokenizer(entryPath, "\\", false); //$NON-NLS-1$
      String bookName = tok.nextToken();
      REBook book = QuickRExPlugin.getDefault().getReBookWithName(bookName);
      RELibraryEntry entry = book.getCategoryWithName(tok.nextToken()).getEntryWithTitle(tok.nextToken());
      return new RELibraryEntryEditorInput(entry, book.isQuickRExBook());
    } catch (Exception e) {
      return null;
    }
  }

}