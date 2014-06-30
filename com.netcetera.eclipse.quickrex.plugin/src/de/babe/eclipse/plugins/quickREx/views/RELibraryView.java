/*******************************************************************************
 * Copyright (c) 2006 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.PluginImageRegistry;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.actions.SearchREAction;
import de.babe.eclipse.plugins.quickREx.dialogs.RESearchInputDialog;
import de.babe.eclipse.plugins.quickREx.editors.RELibraryEntryEditor;
import de.babe.eclipse.plugins.quickREx.objects.REBook;
import de.babe.eclipse.plugins.quickREx.objects.RECategory;
import de.babe.eclipse.plugins.quickREx.objects.RELibraryEntry;
import de.babe.eclipse.plugins.quickREx.objects.RELibraryEntryEditorInput;

/**
 * @author bastian.bergerhoff
 */
public class RELibraryView extends ViewPart implements IPropertyChangeListener {

  public static final String ID = "de.babe.eclipse.plugins.quickREx.views.RELibraryView"; //$NON-NLS-1$

  private TreeViewer treeView;

  private Action linkWithEditorAction;

  private Action searchREAction;

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPartControl(Composite parent) {
    createViewContents(parent);
    makeActions();
    contributeToActionBars();
    QuickRExPlugin.getDefault().addRELibraryListener(this);
  }

  private void contributeToActionBars() {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
    fillToolBar(toolbar);
  }

  private void fillToolBar(IToolBarManager manager) {
    manager.add(linkWithEditorAction);
    manager.add(new Separator());
    manager.add(searchREAction);
  }

  private void fillLocalPullDown(IMenuManager manager) {
    manager.add(linkWithEditorAction);
    manager.add(new Separator());
    manager.add(searchREAction);
  }

  private void makeActions() {
    linkWithEditorAction = new Action("", IAction.AS_CHECK_BOX) { //$NON-NLS-1$
      @Override
      public void run() {
        setLinkWithEditor(isChecked());
      }
    };
    linkWithEditorAction.setText(Messages.getString("views.RELibraryView.actions.linkWithEditor.name")); //$NON-NLS-1$
    linkWithEditorAction.setToolTipText(Messages.getString("views.RELibraryView.actions.linkWithEditor.tooltip")); //$NON-NLS-1$
    linkWithEditorAction.setChecked(QuickRExPlugin.getDefault().isLinkRELibViewWithEditor());
    linkWithEditorAction.setImageDescriptor(((PluginImageRegistry) QuickRExPlugin.getDefault().getImageRegistry())
        .getImageDescriptor(PluginImageRegistry.IMG_LINK_WITH_EDITOR));

    searchREAction = new SearchREAction();

  }

  /**
   * Brings up the search-input dialog for searching the library and starts a search.
   */
  public void performSearchRE() {
    RESearchInputDialog dlg = new RESearchInputDialog(getSite().getShell());
    int result = dlg.open();
    if (Dialog.OK == result) {
      NewSearchUI.activateSearchResultView();
      NewSearchUI.runQuery(dlg.getQuery());
    }
  }

  /**
   * Sets the state of linking the tree with the editors.
   *
   * @param flag <code>true</code> or <code>false</code> depending if you want the tree linked or not
   */
  protected void setLinkWithEditor(boolean flag) {
    QuickRExPlugin.getDefault().setLinkRELibViewWithEditor(flag);

  }

  private void createViewContents(Composite parent) {
    FormToolkit tk = new FormToolkit(parent.getDisplay());
    Form form = tk.createForm(parent);
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    form.getBody().setLayout(layout);

    createTreeArea(form);
  }

  private void createTreeArea(Form form) {
    treeView = new TreeViewer(form.getBody(), SWT.H_SCROLL | SWT.V_SCROLL);
    treeView.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(DoubleClickEvent event) {
        try {
          if (treeView.getTree().getSelection()[0].getData() instanceof RELibraryEntry) {
            RELibraryEntry selected = (RELibraryEntry) treeView.getTree().getSelection()[0].getData();
            if (((REBook) treeView.getTree().getSelection()[0].getParentItem().getParentItem().getData()).getName().equals(REBook.DEFAULT_BOOK_NAME)) {
              PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new RELibraryEntryEditorInput(selected, true),
                  RELibraryEntryEditor.ID);
            } else {
              PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new RELibraryEntryEditorInput(selected, false),
                  RELibraryEntryEditor.ID);
              selected.addTitleChangeListener(RELibraryView.this);
            }
          } else {
            if (treeView.getExpandedState(treeView.getTree().getSelection()[0].getData())) {
              treeView.collapseToLevel(treeView.getTree().getSelection()[0].getData(), TreeViewer.ALL_LEVELS);
            } else {
              treeView.expandToLevel(treeView.getTree().getSelection()[0].getData(), 1);
            }
          }
        } catch (Exception ex) {
          // nop
        }
      }
    });

    treeView.setContentProvider(new ITreeContentProvider() {

      @Override
      public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof REBook) {
          REBook book = (REBook) parentElement;
          return book.getContents().toArray(new RECategory[book.getContents().size()]);
        } else if (parentElement instanceof RECategory) {
          RECategory cat = (RECategory) parentElement;
          return cat.getCategoryContents();
        } else {
          return null;
        }
      }

      @Override
      public Object getParent(Object element) {
        if (element instanceof RECategory) {
          RECategory cat = (RECategory) element;
          return cat.getBook();
        } else if (element instanceof RELibraryEntry) {
          RELibraryEntry entry = (RELibraryEntry) element;
          return entry.getCategory();
        } else {
          return null;
        }
      }

      @Override
      public boolean hasChildren(Object element) {
        if (element instanceof REBook) {
          REBook book = (REBook) element;
          return book.getContents() != null && book.getContents().size() > 0;
        } else if (element instanceof RECategory) {
          RECategory cat = (RECategory) element;
          return cat.getCategoryContents() != null && cat.getCategoryContents().length > 0;
        } else {
          return false;
        }
      }

      @Override
      public Object[] getElements(Object inputElement) {
        if (inputElement instanceof REBook[]) {
          // this is called to get the roots of the tree...
          return QuickRExPlugin.getDefault().getREBooks();
        } else {
          return getChildren(inputElement);
        }
      }

      @Override
      public void dispose() {
      }

      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }
    });

    treeView.setLabelProvider(new LabelProvider() {
      @Override
      public Image getImage(Object element) {
        if (element instanceof REBook) {
          return ((PluginImageRegistry) QuickRExPlugin.getDefault().getImageRegistry()).getImageDescriptor(PluginImageRegistry.IMG_BOOK).createImage();
        } else if (element instanceof RECategory) {
          return ((PluginImageRegistry) QuickRExPlugin.getDefault().getImageRegistry()).getImageDescriptor(PluginImageRegistry.IMG_CATEGORY)
              .createImage();
        } else if (element instanceof RELibraryEntry) {
          return ((PluginImageRegistry) QuickRExPlugin.getDefault().getImageRegistry()).getImageDescriptor(PluginImageRegistry.IMG_REG_EXP)
              .createImage();
        } else {
          return null;
        }
      }

      @Override
      public String getText(Object element) {
        if (element instanceof REBook) {
          return ((REBook) element).getName();
        } else if (element instanceof RECategory) {
          return ((RECategory) element).getName();
        } else if (element instanceof RELibraryEntry) {
          return ((RELibraryEntry) element).getTitle();
        } else {
          return null;
        }
      }

      @Override
      public boolean isLabelProperty(Object element, String property) {
        return true;
      }
    });

    treeView.setInput(QuickRExPlugin.getDefault().getREBooks());

    MenuManager mm = new MenuManager();
    mm.setRemoveAllWhenShown(true);
    mm.addMenuListener(new IMenuListener() {
      @Override
      public void menuAboutToShow(IMenuManager mm2) {
        if (treeView.getTree().getSelection()[0].getData() instanceof REBook) {
          createBookContextMenu(mm2);
        } else if (treeView.getTree().getSelection()[0].getData() instanceof RECategory) {
          createCategoryContextMenu(mm2);
        } else if (treeView.getTree().getSelection()[0].getData() instanceof RELibraryEntry) {
          createREContextMenu(mm2);
        }
      }

      private void createCategoryContextMenu(IMenuManager mm2) {
        Action fNewREAction = new Action(Messages.getString("views.RELibraryView.context.category.actions.addRE.name")) { //$NON-NLS-1$
          /*
           * (non-Javadoc)
           *
           * @see org.eclipse.jface.action.Action#run()
           */
          @Override
          public void run() {
            InputDialog dlg = new InputDialog(treeView.getControl().getShell(), Messages.getString("views.RELibraryView.context.category.actions.addRE.dlg.title"), Messages.getString("views.RELibraryView.context.category.actions.add.dlgRE.text"), "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                new IInputValidator() {

              @Override
              public String isValid(String newText) {
                if (newText == null || newText.trim().length() == 0) {
                  return Messages.getString("views.RELibraryView.context.category.actions.add.dlgRE.message1"); //$NON-NLS-1$
                } else if (((RECategory) treeView.getTree().getSelection()[0].getData()).containsEntryWithTitle(newText)) {
                  return Messages.getString("views.RELibraryView.context.category.actions.add.dlgRE.message2"); //$NON-NLS-1$
                } else {
                  return null;
                }
              }

            });
            int retCode = dlg.open();
            if (Dialog.OK == retCode) {
              RECategory selectedCat = (RECategory) treeView.getTree().getSelection()[0].getData();
              RELibraryEntry newEntry = new RELibraryEntry(dlg.getValue(), "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
              newEntry.setCategory(selectedCat);
              selectedCat.addEntry(newEntry, 0);
              treeView.expandToLevel(treeView.getTree().getSelection()[0].getData(), TreeViewer.ALL_LEVELS);
              treeView.refresh();
              try {
                newEntry.addTitleChangeListener(RELibraryView.this);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new RELibraryEntryEditorInput(newEntry, false),
                    RELibraryEntryEditor.ID);
              } catch (Exception e) {
                // TODO: handle exception
              }
            }
          }
        };
        fNewREAction.setEnabled(!((REBook) treeView.getTree().getSelection()[0].getParentItem().getData()).getName().equals(REBook.DEFAULT_BOOK_NAME));
        mm2.add(fNewREAction);
        mm2.add(new Separator());
        Action fNewCategoryAction = new Action(Messages.getString("views.RELibraryView.context.category.actions.addCat.name")) { //$NON-NLS-1$
          /*
           * (non-Javadoc)
           *
           * @see org.eclipse.jface.action.Action#run()
           */
          @Override
          public void run() {
            InputDialog dlg = new InputDialog(treeView.getControl().getShell(), Messages.getString("views.RELibraryView.context.category.actions.addCat.dlg.title"), Messages.getString("views.RELibraryView.context.category.actions.addCat.dlg.text"), "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                new IInputValidator() {

              @Override
              public String isValid(String newText) {
                if (newText == null || newText.trim().length() == 0) {
                  return Messages.getString("views.RELibraryView.context.category.actions.addCat.dlg.message1"); //$NON-NLS-1$
                } else if (((REBook) treeView.getTree().getSelection()[0].getParentItem().getData()).containsCategoryWithName(newText)) {
                  return Messages.getString("views.RELibraryView.context.category.actions.addCat.dlg.message2"); //$NON-NLS-1$
                } else {
                  return null;
                }
              }

            });
            int retCode = dlg.open();
            if (Dialog.OK == retCode) {
              REBook selectedBook = (REBook) treeView.getTree().getSelection()[0].getParentItem().getData();
              RECategory newCat = new RECategory(dlg.getValue(), new ArrayList<RELibraryEntry>());
              newCat.setBook(selectedBook);
              selectedBook.addCategory(newCat, getChildIndex(treeView.getTree().getSelection()[0].getParentItem(),
                  treeView.getTree().getSelection()[0]) + 1);
              treeView.refresh();
            }
          }
        };
        fNewCategoryAction.setEnabled(!((REBook) treeView.getTree().getSelection()[0].getParentItem().getData()).getName().equals(
            REBook.DEFAULT_BOOK_NAME));
        mm2.add(fNewCategoryAction);
        Action fRenameAction = new Action(Messages.getString("views.RELibraryView.context.category.actions.renameCat.name")) { //$NON-NLS-1$
          /*
           * (non-Javadoc)
           *
           * @see org.eclipse.jface.action.Action#run()
           */
          @Override
          public void run() {
            InputDialog dlg = new InputDialog(treeView.getControl().getShell(), Messages.getString("views.RELibraryView.context.category.actions.renameCat.dlg.title"), Messages.getString("views.RELibraryView.context.category.actions.renameCat.dlg.text"), //$NON-NLS-1$ //$NON-NLS-2$
                ((RECategory) treeView.getTree().getSelection()[0].getData()).getName(), new IInputValidator() {

              @Override
              public String isValid(String newText) {
                if (newText == null || newText.trim().length() == 0) {
                  return Messages.getString("views.RELibraryView.context.category.actions.renameCat.dlg.message1"); //$NON-NLS-1$
                } else if (((REBook) treeView.getTree().getSelection()[0].getParentItem().getData()).containsCategoryWithName(newText)
                    && !newText.equals(((RECategory) treeView.getTree().getSelection()[0].getData()).getName())) {
                  return Messages.getString("views.RELibraryView.context.category.actions.renameCat.dlg.message2"); //$NON-NLS-1$
                } else {
                  return null;
                }
              }

            });
            int retCode = dlg.open();
            if (Dialog.OK == retCode) {
              RECategory selected = (RECategory) treeView.getTree().getSelection()[0].getData();
              selected.setName(dlg.getValue());
              treeView.refresh();
            }
          }
        };
        fRenameAction
        .setEnabled(!((REBook) treeView.getTree().getSelection()[0].getParentItem().getData()).getName().equals(REBook.DEFAULT_BOOK_NAME));
        mm2.add(fRenameAction);
        mm2.add(new Separator());
        Action fDeleteAction = new Action(Messages.getString("views.RELibraryView.context.category.actions.deleteCat.name")) { //$NON-NLS-1$
          /*
           * (non-Javadoc)
           *
           * @see org.eclipse.jface.action.Action#run()
           */
          @Override
          public void run() {
            boolean proceed = MessageDialog.openQuestion(treeView.getControl().getShell(), Messages.getString("views.RELibraryView.context.category.actions.deleteCat.dlg.title"), //$NON-NLS-1$
                Messages.getString("views.RELibraryView.context.category.actions.deleteCat.dlg.text")); //$NON-NLS-1$
            if (proceed) {
              REBook selectedBook = (REBook) treeView.getTree().getSelection()[0].getParentItem().getData();
              RECategory selected = (RECategory) treeView.getTree().getSelection()[0].getData();
              RELibraryEntry[] contents = selected.getCategoryContents();
              for (RELibraryEntry content : contents) {
                RELibraryEntryEditorInput inp = new RELibraryEntryEditorInput(content, false);
                IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(inp);
                if (editor != null) {
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editor, false);
                }
              }
              selectedBook.removeCategory(selected);
              treeView.refresh();
            }
          }
        };
        fDeleteAction
        .setEnabled(!((REBook) treeView.getTree().getSelection()[0].getParentItem().getData()).getName().equals(REBook.DEFAULT_BOOK_NAME));
        mm2.add(fDeleteAction);
      }

      private void createREContextMenu(IMenuManager mm2) {
        Action fNewREAction = new Action(Messages.getString("views.RELibraryView.context.re.actions.addRE.name")) { //$NON-NLS-1$
          /*
           * (non-Javadoc)
           *
           * @see org.eclipse.jface.action.Action#run()
           */
          @Override
          public void run() {
            InputDialog dlg = new InputDialog(treeView.getControl().getShell(), Messages.getString("views.RELibraryView.context.re.actions.addRE.dlg.title"), Messages.getString("views.RELibraryView.context.re.actions.add.dlgRE.text"), "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                new IInputValidator() {

              @Override
              public String isValid(String newText) {
                if (newText == null || newText.trim().length() == 0) {
                  return Messages.getString("views.RELibraryView.context.re.actions.add.dlgRE.message1"); //$NON-NLS-1$
                } else if (((RECategory) treeView.getTree().getSelection()[0].getParentItem().getData()).containsEntryWithTitle(newText)) {
                  return Messages.getString("views.RELibraryView.context.re.actions.add.dlgRE.message2"); //$NON-NLS-1$
                } else {
                  return null;
                }
              }

            });
            int retCode = dlg.open();
            if (Dialog.OK == retCode) {
              RECategory selectedCat = (RECategory) treeView.getTree().getSelection()[0].getParentItem().getData();
              RELibraryEntry newEntry = new RELibraryEntry(dlg.getValue(), "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
              newEntry.setCategory(selectedCat);
              selectedCat.addEntry(newEntry,
                  getChildIndex(treeView.getTree().getSelection()[0].getParentItem(), treeView.getTree().getSelection()[0]) + 1);
              treeView.expandToLevel(treeView.getTree().getSelection()[0].getParentItem().getData(), TreeViewer.ALL_LEVELS);
              treeView.refresh();
              try {
                newEntry.addTitleChangeListener(RELibraryView.this);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new RELibraryEntryEditorInput(newEntry, false),
                    RELibraryEntryEditor.ID);
              } catch (Exception e) {
                // TODO: handle exception
              }
            }
          }
        };
        fNewREAction.setEnabled(!((REBook) treeView.getTree().getSelection()[0].getParentItem().getParentItem().getData()).getName().equals(
            REBook.DEFAULT_BOOK_NAME));
        mm2.add(fNewREAction);
        mm2.add(new Separator());
        if (((REBook) treeView.getTree().getSelection()[0].getParentItem().getParentItem().getData()).getName().equals(REBook.DEFAULT_BOOK_NAME)) {
          Action fViewAction = new Action(Messages.getString("views.RELibraryView.context.re.actions.viewRE.name")) { //$NON-NLS-1$
            /*
             * (non-Javadoc)
             *
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
              RELibraryEntry selected = (RELibraryEntry) treeView.getTree().getSelection()[0].getData();
              try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new RELibraryEntryEditorInput(selected, true),
                    RELibraryEntryEditor.ID);
              } catch (Exception e) {
                // TODO: handle exception
              }
            }
          };
          fViewAction.setEnabled(true);
          mm2.add(fViewAction);
        } else {
          Action fEditAction = new Action(Messages.getString("views.RELibraryView.context.re.actions.editRE.name")) { //$NON-NLS-1$
            /*
             * (non-Javadoc)
             *
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
              RELibraryEntry selected = (RELibraryEntry) treeView.getTree().getSelection()[0].getData();
              try {
                selected.addTitleChangeListener(RELibraryView.this);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new RELibraryEntryEditorInput(selected, false),
                    RELibraryEntryEditor.ID);
              } catch (Exception e) {
                // TODO: handle exception
              }
            }
          };
          fEditAction.setEnabled(true);
          mm2.add(fEditAction);
        }
        mm2.add(new Separator());
        Action fDeleteAction = new Action(Messages.getString("views.RELibraryView.context.re.actions.deleteRE.name")) { //$NON-NLS-1$
          /*
           * (non-Javadoc)
           *
           * @see org.eclipse.jface.action.Action#run()
           */
          @Override
          public void run() {
            boolean proceed = MessageDialog.openQuestion(treeView.getControl().getShell(), Messages.getString("views.RELibraryView.context.re.actions.deleteRE.dlg.title"), //$NON-NLS-1$
                Messages.getString("views.RELibraryView.context.re.actions.deleteRE.dlg.text")); //$NON-NLS-1$
            if (proceed) {
              RELibraryEntry selected = (RELibraryEntry) treeView.getTree().getSelection()[0].getData();
              RELibraryEntryEditorInput inp = new RELibraryEntryEditorInput(selected, false);
              IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(inp);
              if (editor != null) {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editor, false);
              }
              selected.getCategory().removeEntry(selected);
              treeView.refresh();
            }
          }
        };
        fDeleteAction.setEnabled(!((REBook) treeView.getTree().getSelection()[0].getParentItem().getParentItem().getData()).getName().equals(
            REBook.DEFAULT_BOOK_NAME));
        mm2.add(fDeleteAction);
        mm2.add(new Separator());
        Action fUseREAction = new Action(Messages.getString("views.RELibraryView.context.re.actions.useRE.name")) { //$NON-NLS-1$
          /*
           * (non-Javadoc)
           *
           * @see org.eclipse.jface.action.Action#run()
           */
          @Override
          public void run() {
            RELibraryEntry selected = (RELibraryEntry) treeView.getTree().getSelection()[0].getData();
            try {
              QuickRExView view = (QuickRExView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(QuickRExView.ID);
              view.setRegularExpression(selected.getRe());
              view.setTestText(selected.getTesttext());
            } catch (Exception e) {
              // TODO: handle exception
            }
          }
        };
        fUseREAction.setEnabled(true);
        mm2.add(fUseREAction);
      }

      private void createBookContextMenu(IMenuManager mm2) {
        Action fNewCategoryAction = new Action(Messages.getString("views.RELibraryView.context.book.actions.addCat.name")) { //$NON-NLS-1$
          /*
           * (non-Javadoc)
           *
           * @see org.eclipse.jface.action.Action#run()
           */
          @Override
          public void run() {
            InputDialog dlg = new InputDialog(treeView.getControl().getShell(), Messages.getString("views.RELibraryView.context.book.actions.addCat.dlg.title"), Messages.getString("views.RELibraryView.context.book.actions.addCat.dlg.text"), "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                new IInputValidator() {

              @Override
              public String isValid(String newText) {
                if (newText == null || newText.trim().length() == 0) {
                  return Messages.getString("views.RELibraryView.context.book.actions.addCat.dlg.message1"); //$NON-NLS-1$
                } else if (((REBook) treeView.getTree().getSelection()[0].getData()).containsCategoryWithName(newText)) {
                  return Messages.getString("views.RELibraryView.context.book.actions.addCat.dlg.message2"); //$NON-NLS-1$
                } else {
                  return null;
                }
              }

            });
            int retCode = dlg.open();
            if (Dialog.OK == retCode) {
              REBook selectedBook = (REBook) treeView.getTree().getSelection()[0].getData();
              RECategory newCat = new RECategory(dlg.getValue(), new ArrayList<RELibraryEntry>());
              newCat.setBook(selectedBook);
              selectedBook.addCategory(newCat, 0);
              treeView.expandToLevel(treeView.getTree().getSelection()[0].getData(), TreeViewer.ALL_LEVELS);
              treeView.refresh();
            }
          }
        };
        fNewCategoryAction.setEnabled(!((REBook) treeView.getTree().getSelection()[0].getData()).getName().equals(REBook.DEFAULT_BOOK_NAME));
        mm2.add(fNewCategoryAction);
        mm2.add(new Separator());
        Action fRenameAction = new Action(Messages.getString("views.RELibraryView.context.book.actions.renameBook.name")) { //$NON-NLS-1$
          /*
           * (non-Javadoc)
           *
           * @see org.eclipse.jface.action.Action#run()
           */
          @Override
          public void run() {
            InputDialog dlg = new InputDialog(treeView.getControl().getShell(), Messages.getString("views.RELibraryView.context.book.actions.renameBook.dlg.title"), Messages.getString("views.RELibraryView.context.book.actions.renameBook.dlg.text"), //$NON-NLS-1$ //$NON-NLS-2$
                ((REBook) treeView.getTree().getSelection()[0].getData()).getName(), new IInputValidator() {

              @Override
              public String isValid(String newText) {
                if (newText == null || newText.trim().length() == 0) {
                  return Messages.getString("views.RELibraryView.context.book.actions.renameBook.dlg.message1"); //$NON-NLS-1$
                } else if (QuickRExPlugin.getDefault().reBookWithNameExists(newText)
                    && !newText.equals(((REBook) treeView.getTree().getSelection()[0].getData()).getName())) {
                  return Messages.getString("views.RELibraryView.context.book.actions.renameBook.dlg.message2"); //$NON-NLS-1$
                } else {
                  return null;
                }
              }

            });
            int retCode = dlg.open();
            if (Dialog.OK == retCode) {
              REBook selected = (REBook) treeView.getTree().getSelection()[0].getData();
              selected.setName(dlg.getValue());
              treeView.refresh();
            }
          }
        };
        fRenameAction.setEnabled(!((REBook) treeView.getTree().getSelection()[0].getData()).getName().equals(REBook.DEFAULT_BOOK_NAME));
        mm2.add(fRenameAction);
        Action fChangeLocAction = new Action(Messages.getString("views.RELibraryView.context.book.actions.moveBook.name")) { //$NON-NLS-1$
          /*
           * (non-Javadoc)
           *
           * @see org.eclipse.jface.action.Action#run()
           */
          @Override
          public void run() {
            FileDialog dialog = new FileDialog(treeView.getControl().getShell(), SWT.SAVE);
            dialog.setText(Messages.getString("views.RELibraryView.context.book.actions.moveBook.dlg.text")); //$NON-NLS-1$
            dialog.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$
            String newPath = dialog.open();
            if (newPath == null) {
              return;
            } else {
              IPath reLibFilePath = new Path(newPath);
              File reLibFile = reLibFilePath.toFile();
              if (reLibFile.exists()) {
                boolean proceed = MessageDialog.openQuestion(treeView.getControl().getShell(), Messages.getString("views.RELibraryView.context.book.actions.moveBook.dlg.warn.title"), //$NON-NLS-1$
                    Messages.getString("views.RELibraryView.context.book.actions.moveBook.dlg.warn.text")); //$NON-NLS-1$
                if (!proceed) {
                  return;
                } else {
                  REBook selected = (REBook) treeView.getTree().getSelection()[0].getData();
                  selected.setPath(newPath);
                  treeView.refresh();
                }
              } else {
                REBook selected = (REBook) treeView.getTree().getSelection()[0].getData();
                selected.setPath(newPath);
                treeView.refresh();
              }

            }
          }
        };
        fChangeLocAction.setEnabled(!((REBook) treeView.getTree().getSelection()[0].getData()).getName().equals(REBook.DEFAULT_BOOK_NAME));
        mm2.add(fChangeLocAction);
        mm2.add(new Separator());
        Action fDeleteAction = new Action(Messages.getString("views.RELibraryView.context.book.actions.deleteBook.name")) { //$NON-NLS-1$
          /*
           * (non-Javadoc)
           *
           * @see org.eclipse.jface.action.Action#run()
           */
          @Override
          public void run() {
            boolean proceed = MessageDialog.openQuestion(treeView.getControl().getShell(), Messages.getString("views.RELibraryView.context.book.actions.deleteBook.dlg.title"), //$NON-NLS-1$
                Messages.getString("views.RELibraryView.context.book.actions.deleteBook.dlg.text")); //$NON-NLS-1$
            if (proceed) {
              REBook selected = (REBook) treeView.getTree().getSelection()[0].getData();
              List<RECategory> categories = selected.getContents();
              if (categories != null) {
                for (RECategory selectedCat : categories) {
                  RELibraryEntry[] contents = selectedCat.getCategoryContents();
                  if (contents != null) {
                    for (RELibraryEntry content : contents) {
                      RELibraryEntryEditorInput inp = new RELibraryEntryEditorInput(content, false);
                      IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(inp);
                      if (editor != null) {
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editor, false);
                      }
                    }
                  }
                }
              }
              QuickRExPlugin.getDefault().removeREBook(selected);
              treeView.refresh();
            }
          }
        };
        fDeleteAction.setEnabled(!((REBook) treeView.getTree().getSelection()[0].getData()).getName().equals(REBook.DEFAULT_BOOK_NAME));
        mm2.add(fDeleteAction);
      }
    });
    treeView.getTree().setMenu(mm.createContextMenu(treeView.getTree()));
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.grabExcessHorizontalSpace = true;
    treeView.getControl().setLayoutData(gd);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
   */
  @Override
  public void setFocus() {
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(PropertyChangeEvent event) {
    if (event.getSource() instanceof RELibraryEntry && "title".equals(event.getProperty())) { //$NON-NLS-1$
      treeView.update(event.getSource(), new String[] { event.getProperty() });
    } else if (event.getSource() instanceof QuickRExPlugin) {
      treeView.refresh();
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#dispose()
   */
  @Override
  public void dispose() {
    QuickRExPlugin.getDefault().removeRELibraryListener(this);
    super.dispose();
  }

  /**
   * Expands the tree to the passed entry.
   *
   * @param entry the entry to expand to
   */
  public void expandToEntry(RELibraryEntry entry) {
    treeView.expandToLevel(entry, TreeViewer.ALL_LEVELS);
    treeView.setSelection(new StructuredSelection(entry), true);
  }

  /**
   * Returns the index of the passed item as a child of the passed
   * parent-item. Returns -1 if the passed item is not found among
   * the children of the passed parent-item
   *
   * @param parentItem the parent item
   * @param item the child to get the index for
   * @return the index for the child or -1
   */
  protected int getChildIndex(TreeItem parentItem, TreeItem item) {
    TreeItem[] children = parentItem.getItems();
    for (int i = 0; i < children.length; i++) {
      if (children[i].equals(item)) {
        return i;
      }
    }
    return -1;
  }
}