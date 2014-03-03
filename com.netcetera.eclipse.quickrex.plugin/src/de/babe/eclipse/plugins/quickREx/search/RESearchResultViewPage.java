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

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import de.babe.eclipse.plugins.quickREx.PluginImageRegistry;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.editors.RELibraryEntryEditor;
import de.babe.eclipse.plugins.quickREx.objects.REBook;
import de.babe.eclipse.plugins.quickREx.objects.RECategory;
import de.babe.eclipse.plugins.quickREx.objects.RELibraryEntry;
import de.babe.eclipse.plugins.quickREx.objects.RELibraryEntryEditorInput;
import de.babe.eclipse.plugins.quickREx.views.RELibraryView;

/**
 * @author bastian.bergerhoff
 */
public class RESearchResultViewPage extends AbstractTextSearchViewPage {

  private TreeViewer viewer;

  /**
   * The constructor
   */
  public RESearchResultViewPage() {
    super(AbstractTextSearchViewPage.FLAG_LAYOUT_TREE);
  };

  /* (non-Javadoc)
   * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#elementsChanged(java.lang.Object[])
   */
  @Override
  protected void elementsChanged(Object[] objects) {
    viewer.refresh();
  }

  /* (non-Javadoc)
   * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#clear()
   */
  @Override
  protected void clear() {
    ((RESearchResult)viewer.getInput()).doRemoveAll();
    viewer.refresh();
  }

  /* (non-Javadoc)
   * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
   */
  @Override
  protected void configureTreeViewer(final TreeViewer viewer) {
    this.viewer = viewer;
    viewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(DoubleClickEvent event) {
        try {
          RELibraryEntry selected = (RELibraryEntry)viewer.getTree().getSelection()[0].getData();
          if (((REBook)viewer.getTree().getSelection()[0].getParentItem().getParentItem().getData()).getName().equals(REBook.DEFAULT_BOOK_NAME)) {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new RELibraryEntryEditorInput(selected, true),
                RELibraryEntryEditor.ID);
          } else {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new RELibraryEntryEditorInput(selected, false),
                RELibraryEntryEditor.ID);
            selected.addTitleChangeListener((RELibraryView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                RELibraryView.ID));
          }
        } catch (Exception ex) {
          // nop
        }
      }
    });

    viewer.setContentProvider(new ITreeContentProvider() {

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IContentProvider#dispose()
       */
      @Override
      public void dispose() {
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
       */
      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
       */
      @Override
      public Object[] getElements(Object inputElement) {
        return ((RESearchResult)getInput()).getBooksWithMatches();
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
       */
      @Override
      public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof REBook) {
          return ((RESearchResult)getInput()).getMatchingCategoriesInBook((REBook)parentElement);
        } else if (parentElement instanceof RECategory) {
          return ((RESearchResult)getInput()).getMatchesInCategory((RECategory)parentElement);
        } else {
          return new Object[0];
        }
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
       */
      @Override
      public Object getParent(Object element) {
        if (element instanceof RELibraryEntry) {
          return ((RELibraryEntry)element).getCategory();
        } else if (element instanceof RECategory) {
          return ((RECategory)element).getBook();
        } else {
          return null;
        }
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
       */
      @Override
      public boolean hasChildren(Object element) {
        return (element instanceof REBook || element instanceof RECategory);
      }
    });

    viewer.setLabelProvider(new LabelProvider() {
      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
       */
      @Override
      public Image getImage(Object element) {
        if (element instanceof REBook) {
          return ((PluginImageRegistry)QuickRExPlugin.getDefault().getImageRegistry()).getImageDescriptor(PluginImageRegistry.IMG_BOOK).createImage();
        } else if (element instanceof RECategory) {
          return ((PluginImageRegistry)QuickRExPlugin.getDefault().getImageRegistry()).getImageDescriptor(PluginImageRegistry.IMG_CATEGORY)
              .createImage();
        } else if (element instanceof RELibraryEntry) {
          return ((PluginImageRegistry)QuickRExPlugin.getDefault().getImageRegistry()).getImageDescriptor(PluginImageRegistry.IMG_REG_EXP)
              .createImage();
        } else {
          return null;
        }
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
       */
      @Override
      public String getText(Object element) {
        if (element instanceof REBook) {
          return ((REBook)element).getName();
        } else if (element instanceof RECategory) {
          return ((RECategory)element).getName();
        } else if (element instanceof RELibraryEntry) {
          return ((RELibraryEntry)element).getTitle();
        } else {
          return null;
        }
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
       */
      @Override
      public boolean isLabelProperty(Object element, String property) {
        return true;
      }
    });

  }

  /* (non-Javadoc)
   * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTableViewer(org.eclipse.jface.viewers.TableViewer)
   */
  @Override
  protected void configureTableViewer(TableViewer viewer) {
  }

}