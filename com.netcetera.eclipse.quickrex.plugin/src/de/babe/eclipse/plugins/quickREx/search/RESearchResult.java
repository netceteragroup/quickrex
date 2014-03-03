/*******************************************************************************
 * Copyright (c) 2006 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.PluginImageRegistry;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.objects.REBook;
import de.babe.eclipse.plugins.quickREx.objects.RECategory;
import de.babe.eclipse.plugins.quickREx.objects.RELibraryEntry;

/**
 * @author bastian.bergerhoff
 */
public class RESearchResult extends AbstractTextSearchResult {

  private RESearchQuery query;

  private ArrayList matches = new ArrayList();

  private ArrayList booksWithMatches = new ArrayList();

  private ArrayList categoriesWithMatches = new ArrayList();

  /**
   * The constructor
   * 
   * @param query
   */
  public RESearchResult(RESearchQuery query) {
    this.query = query;
  }

  /* (non-Javadoc)
   * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getEditorMatchAdapter()
   */
  @Override
  public IEditorMatchAdapter getEditorMatchAdapter() {
    return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getFileMatchAdapter()
   */
  @Override
  public IFileMatchAdapter getFileMatchAdapter() {
    return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.search.ui.ISearchResult#getLabel()
   */
  @Override
  public String getLabel() {
    return Messages.getString("search.RESearchResult.label", new Object[] { new Integer(matches.size()), query.getText()}); //$NON-NLS-1$
  }

  /* (non-Javadoc)
   * @see org.eclipse.search.ui.ISearchResult#getTooltip()
   */
  @Override
  public String getTooltip() {
    return this.query.getLabel();
  }

  /* (non-Javadoc)
   * @see org.eclipse.search.ui.ISearchResult#getImageDescriptor()
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    return QuickRExPlugin.getDefault().getImageRegistry().getDescriptor(PluginImageRegistry.IMG_SEARCH_RE);
  }

  /* (non-Javadoc)
   * @see org.eclipse.search.ui.ISearchResult#getQuery()
   */
  @Override
  public ISearchQuery getQuery() {
    return this.query;
  }

  /**
   * Add the passed entry to the ones matching this objects' query
   * 
   * @param entry the entry to be added
   */
  public void addMatchingEntry(RELibraryEntry entry) {
    matches.add(entry);
    if (!booksWithMatches.contains(entry.getCategory().getBook())) {
      booksWithMatches.add(entry.getCategory().getBook());
    }
    if (!categoriesWithMatches.contains(entry.getCategory())) {
      categoriesWithMatches.add(entry.getCategory());
    }
  }

  /**
   * Rebuilds the collections of books and categories containing matches
   */
  private void rebuildBooksAndCategories() {
    booksWithMatches = new ArrayList();
    categoriesWithMatches = new ArrayList();
    for (Iterator iter = matches.iterator(); iter.hasNext();) {
      RELibraryEntry entry = (RELibraryEntry)iter.next();
      if (!booksWithMatches.contains(entry.getCategory().getBook())) {
        booksWithMatches.add(entry.getCategory().getBook());
      }
      if (!categoriesWithMatches.contains(entry.getCategory())) {
        categoriesWithMatches.add(entry.getCategory());
      }
    }
  }

  /**
   * Removes the passed entry from the matches
   * 
   * @param entry the entry to remove
   */
  public void removeEntry(RELibraryEntry entry) {
    matches.remove(entry);
  }

  /**
   * Returns an array of REBooks containing matches.
   * 
   * @return an array of REBooks containing matches
   */
  public REBook[] getBooksWithMatches() {
    return (REBook[])booksWithMatches.toArray(new REBook[booksWithMatches.size()]);
  }

  /**
   * Returns an array of categories which are in the passed book and have matches
   * 
   * @param book the book that the categories should be in
   * @return an array of RECategories
   */
  public RECategory[] getMatchingCategoriesInBook(REBook book) {
    ArrayList retList = new ArrayList();
    List catsInBook = book.getContents();
    for (Iterator iter = catsInBook.iterator(); iter.hasNext();) {
      RECategory cat = (RECategory)iter.next();
      if (categoriesWithMatches.contains(cat)) {
        retList.add(cat);
      }
    }
    return (RECategory[])retList.toArray(new RECategory[retList.size()]);
  }

  /**
   * Returns an array of entries which match and are in the passed category
   * 
   * @param category the category that the entries should be in
   * @return an array or RELibraryEntries
   */
  public RELibraryEntry[] getMatchesInCategory(RECategory category) {
    ArrayList retList = new ArrayList();
    RELibraryEntry[] entriesInCat = category.getCategoryContents();
    for (int i = 0; i < entriesInCat.length; i++) {
      if (matches.contains(entriesInCat[i])) {
        retList.add(entriesInCat[i]);
      }
    }
    return (RELibraryEntry[])retList.toArray(new RELibraryEntry[retList.size()]);
  }

  /* (non-Javadoc)
   * @see org.eclipse.search.ui.text.AbstractTextSearchResult#removeMatch(org.eclipse.search.ui.text.Match)
   */
  @Override
  public void removeMatch(Match match) {
    super.removeMatch(match);
    RELibraryEntry entry = (RELibraryEntry)match.getElement();
    removeEntry(entry);
    rebuildBooksAndCategories();
  }

  /* (non-Javadoc)
   * @see org.eclipse.search.ui.text.AbstractTextSearchResult#removeMatches(org.eclipse.search.ui.text.Match[])
   */
  @Override
  public void removeMatches(Match[] matches) {
    super.removeMatches(matches);
    for (int i = 0; i < matches.length; i++) {
      RELibraryEntry entry = (RELibraryEntry)matches[i].getElement();
      removeEntry(entry);
    }
    rebuildBooksAndCategories();
  }

  /**
   * Remove all matches and rebuild the collections for the books and categories holding matches
   */
  public void doRemoveAll() {
    matches = new ArrayList();
    rebuildBooksAndCategories();
  }
}