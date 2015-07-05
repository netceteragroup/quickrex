/*******************************************************************************
 * Copyright (c) 2005, 2007 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation, all but:
 *     Andreas Studer - Contributions to handling global flags
 *     Georg Sendt - Contributions to threaded evaluation, implementation of
 *                   JRegex-Flavour
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.contentassist.ComboContentAssistSubjectAdapter;
import org.eclipse.jface.contentassist.SubjectControlContentAssistant;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contentassist.ContentAssistHandler;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.PluginImageRegistry;
import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;
import de.babe.eclipse.plugins.quickREx.StringUtils;
import de.babe.eclipse.plugins.quickREx.actions.JCopyAction;
import de.babe.eclipse.plugins.quickREx.regexp.Flag;
import de.babe.eclipse.plugins.quickREx.regexp.Match;
import de.babe.eclipse.plugins.quickREx.regexp.MatchSetFactory;
import de.babe.eclipse.plugins.quickREx.regexp.RegExpContentAssistProcessor;
import de.babe.eclipse.plugins.quickREx.regexp.RegularExpressionHits;

/**
 * @author bastian.bergerhoff, andreas.studer, georg.sendt
 */
public class QuickRExView extends ViewPart {

  public static final String ID = "de.babe.eclipse.plugins.quickREx.views.QuickRExView"; //$NON-NLS-1$

  private Combo regExpCombo;

  private StyledText testText;

  private Label globalMatch;

  private Label matches;

  private Label groups;

  private Button previousButton;

  private Button nextButton;

  private Button previousGroupButton;

  private Button nextGroupButton;

  private RegularExpressionHits hits = new RegularExpressionHits();

  private static final String MATCH_BG_COLOR_KEY = "de.babe.eclipse.plugins.QuickREx.matchBgColor"; //$NON-NLS-1$

  private static final String MATCH_FG_COLOR_KEY = "de.babe.eclipse.plugins.QuickREx.matchFgColor"; //$NON-NLS-1$

  private static final String CURRENT_MATCH_BG_COLOR_KEY = "de.babe.eclipse.plugins.QuickREx.currentMatchBgColor"; //$NON-NLS-1$

  private static final String CURRENT_MATCH_FG_COLOR_KEY = "de.babe.eclipse.plugins.QuickREx.currentMatchFgColor"; //$NON-NLS-1$

  private static final String NOT_EVALUATED_BG_COLOR_KEY = "de.babe.eclipse.plugins.QuickREx.notEvaluatedBgColor"; //$NON-NLS-1$

  private static final String EDITOR_FONT_KEY = "de.babe.eclipse.plugins.QuickREx.textfontDefinition"; //$NON-NLS-1$

  private SubjectControlContentAssistant regExpContentAssistant;

  private Collection<Flag> currentFlags = new ArrayList<>();

  private Action jcopyAction;

  private Point lastRESelection = new Point(0, 0);

  /**
   * The constructor.
   */
  public QuickRExView() {
  }

  @Override
  public void createPartControl(Composite parent) {
    createViewContents(parent);
    makeActions();
    contributeToActionBars();
    initializeCurrentFlags();
  }

  private void initializeCurrentFlags() {
    for (Flag element : MatchSetFactory.getAllSupportedFlags()) {
      if (QuickRExPlugin.getDefault().isFlagSaved(element)) {
        currentFlags.add(element);
      }
    }
  }

  private void createViewContents(Composite parent) {
    FormToolkit tk = new FormToolkit(parent.getDisplay());
    Form form = tk.createForm(parent);
    GridLayout layout = new GridLayout();

    layout.numColumns = 4;

    form.getBody().setLayout(layout);

    createFirstRow(tk, form);

    createSecondRow(tk, form);

    createNavigationSection(tk, form);

    createFlagsSection(tk, form);
  }

  private void createNavigationSection(FormToolkit tk, final Form form) {
    GridData gd;
    final Section section = tk.createSection(form.getBody(), Section.TWISTIE);
    gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.grabExcessHorizontalSpace = true;
    gd.horizontalSpan = 5;
    section.setLayoutData(gd);
    section.setText(Messages.getString("views.QuickRExView.global.navigation")); //$NON-NLS-1$
    tk.createCompositeSeparator(section);
    section.addExpansionListener(new ExpansionAdapter() {
      @Override
      public void expansionStateChanged(ExpansionEvent e) {
        QuickRExPlugin.getDefault().getPreferenceStore().setValue(QuickRExPlugin.EXPAND_NAVIGATION_SECTION, section.isExpanded());
        form.redraw();
      }
    });
    if (QuickRExPlugin.getDefault().getPreferenceStore().contains(QuickRExPlugin.EXPAND_NAVIGATION_SECTION)) {
      section.setExpanded(QuickRExPlugin.getDefault().getPreferenceStore().getBoolean(QuickRExPlugin.EXPAND_NAVIGATION_SECTION));
    } else {
      QuickRExPlugin.getDefault().getPreferenceStore().setValue(QuickRExPlugin.EXPAND_NAVIGATION_SECTION, true);
      QuickRExPlugin.getDefault().getPreferenceStore().setDefault(QuickRExPlugin.EXPAND_NAVIGATION_SECTION, true);
      section.setExpanded(true);
    }
    Composite client = tk.createComposite(section);
    GridLayout layout = new GridLayout();
    layout.numColumns = 4;
    client.setLayout(layout);
    gd = new GridData();
    gd.horizontalSpan = 2;
    gd.grabExcessHorizontalSpace = true;
    client.setLayoutData(gd);

    createThirdRow(tk, client);

    createFourthRow(tk, client);

    createFifthRow(tk, client);

    section.setClient(client);

  }

  private void createFlagsSection(FormToolkit tk, final Form form) {
    GridData gd;
    Section section = tk.createSection(form.getBody(), Section.DESCRIPTION | Section.TWISTIE);
    gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.grabExcessHorizontalSpace = true;
    gd.horizontalSpan = 5;
    section.setLayoutData(gd);
    section.setText(Messages.getString("views.QuickRExView.global.flags")); //$NON-NLS-1$
    tk.createCompositeSeparator(section);
    section.addExpansionListener(new ExpansionAdapter() {
      @Override
      public void expansionStateChanged(ExpansionEvent e) {
        form.redraw();
      }
    });
    Composite client = tk.createComposite(section);
    GridLayout layout = new GridLayout();
    layout.numColumns = MatchSetFactory.getMaxFlagColumns() + 1;
    client.setLayout(layout);
    gd = new GridData();
    gd.horizontalSpan = 2;
    gd.grabExcessHorizontalSpace = true;
    client.setLayoutData(gd);

    createFlagFlavourSection(tk, client, layout); //$NON-NLS-1$

    section.setClient(client);
  }

  /**
   * Creates a line of flags. This is a helper for the Method createFlagSection.
   * @param tk The FormToolkit to use
   * @param client The Composite Client
   * @param layout The GridLayout to use
   *
   * @see de.babe.eclipse.plugins.quickREx.regexp.MatchSetFactory
   */
  private void createFlagFlavourSection(FormToolkit tk, Composite client, GridLayout layout) {
    int nButtons = 1;
    for (final Flag element : MatchSetFactory.getAllFlags()) {
      nButtons++;
      final Button checkButton = tk.createButton(client, element.getName(), SWT.CHECK);
      GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
      gd.grabExcessHorizontalSpace = false;
      checkButton.setLayoutData(gd);
      checkButton.setToolTipText(element.getDescription());
      checkButton.setSelection(QuickRExPlugin.getDefault().isFlagSaved(element));
      checkButton.addSelectionListener(new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          if (checkButton.getSelection()) {
            currentFlags.add(element);
          } else {
            currentFlags.remove(element);
          }
          updateEvaluation();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {
        }
      });
    }
    while (nButtons < layout.numColumns) {
      nButtons++;
      Label fillLabel = tk.createLabel(client, ""); //$NON-NLS-1$
    }
  }

  private void createFifthRow(FormToolkit tk, Composite client) {
    GridData gd;
    // Fourth row...
    Label groupsLabel = tk.createLabel(client, Messages.getString("views.QuickRExView.fifthrow.label")); //$NON-NLS-1$
    gd = new GridData();
    gd.grabExcessHorizontalSpace = false;
    gd.horizontalAlignment = SWT.END;
    groupsLabel.setLayoutData(gd);
    previousGroupButton = tk.createButton(client, Messages.getString("views.QuickRExView.fifthrow.prev"), SWT.PUSH); //$NON-NLS-1$
    gd = new GridData();
    gd.grabExcessHorizontalSpace = false;
    previousGroupButton.setLayoutData(gd);
    previousGroupButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        handlePreviousGroupButtonPressed();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent event) {
      }
    });
    previousGroupButton.setEnabled(false);
    nextGroupButton = tk.createButton(client, Messages.getString("views.QuickRExView.fifthrow.next"), SWT.PUSH); //$NON-NLS-1$
    gd = new GridData(GridData.VERTICAL_ALIGN_END);
    gd.grabExcessHorizontalSpace = false;
    nextGroupButton.setLayoutData(gd);
    nextGroupButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        handleNextGroupButtonPressed();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent event) {
      }
    });
    nextGroupButton.setEnabled(false);
    groups = tk.createLabel(client, ""); //$NON-NLS-1$
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
    gd.grabExcessHorizontalSpace = true;
    groups.setLayoutData(gd);
  }

  private void createFourthRow(FormToolkit tk, Composite client) {
    GridData gd;
    // Third row...
    Label regExpResult = tk.createLabel(client, Messages.getString("views.QuickRExView.fourthrow.label")); //$NON-NLS-1$
    gd = new GridData();
    gd.grabExcessHorizontalSpace = false;
    gd.horizontalAlignment = SWT.END;
    regExpResult.setLayoutData(gd);
    previousButton = tk.createButton(client, Messages.getString("views.QuickRExView.fourthrow.prev"), SWT.PUSH); //$NON-NLS-1$
    gd = new GridData();
    gd.grabExcessHorizontalSpace = false;
    previousButton.setLayoutData(gd);
    previousButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        handlePreviousButtonPressed();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent event) {
      }
    });
    previousButton.setEnabled(false);
    nextButton = tk.createButton(client, Messages.getString("views.QuickRExView.fourthrow.next"), SWT.PUSH); //$NON-NLS-1$
    gd = new GridData(GridData.VERTICAL_ALIGN_END);
    gd.grabExcessHorizontalSpace = false;
    nextButton.setLayoutData(gd);
    nextButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        handleNextButtonPressed();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent event) {
      }
    });
    nextButton.setEnabled(false);
    matches = tk.createLabel(client, Messages.getString("views.QuickRExView.fourthrow.message")); //$NON-NLS-1$
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
    gd.grabExcessHorizontalSpace = true;
    matches.setLayoutData(gd);
  }

  private void createThirdRow(FormToolkit tk, Composite client) {
    GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
    gd.grabExcessHorizontalSpace = false;
    globalMatch = tk.createLabel(client, ""); //$NON-NLS-1$
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
    gd.grabExcessHorizontalSpace = true;
    gd.horizontalSpan = 4;
    globalMatch.setLayoutData(gd);
  }

  private void createSecondRow(FormToolkit tk, Form form) {
    GridData gd;
    // Second row
    Label testTextEnter = tk.createLabel(form.getBody(), Messages.getString("views.QuickRExView.secondrow.label")); //$NON-NLS-1$
    gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_END);
    gd.grabExcessHorizontalSpace = false;
    testTextEnter.setLayoutData(gd);
    testText = new StyledText(form.getBody(), SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    testText.setFont(JFaceResources.getFont(EDITOR_FONT_KEY));
    gd = new GridData(GridData.FILL_BOTH);
    gd.grabExcessHorizontalSpace = true;
    gd.grabExcessVerticalSpace = true;

    gd.horizontalSpan = 3;
    testText.setLayoutData(gd);
    testText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent event) {
        handleTestTextModified();
      }
    });
    tk.adapt(testText, true, true);
  }

  private void createFirstRow(FormToolkit tk, Form form) {
    GridData gd;
    // First row...
    Label regExpEnter = tk.createLabel(form.getBody(), Messages.getString("views.QuickRExView.firstrow.label")); //$NON-NLS-1$
    gd = new GridData();
    gd.horizontalAlignment = GridData.END;
    gd.grabExcessHorizontalSpace = false;
    regExpEnter.setLayoutData(gd);
    regExpCombo = new Combo(form.getBody(), SWT.DROP_DOWN);
    regExpCombo.setItems(new String[0]);
    regExpCombo.setFont(JFaceResources.getFont(EDITOR_FONT_KEY));
    gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
    gd.horizontalSpan = 2;
    gd.grabExcessHorizontalSpace = true;
    regExpCombo.setLayoutData(gd);
    regExpCombo.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent event) {
        handleRegExpModified();
      }
    });
    regExpCombo.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent event) {
        // This is a hack to keep the Previous- and Next-Buttons from generating
        // selections in the component...
        regExpCombo.clearSelection();
      }

      @Override
      public void focusLost(FocusEvent event) {
        // This is a hack to keep the Previous- and Next-Buttons from generating
        // selections in the component...
        regExpCombo.clearSelection();
      }
    });
    regExpCombo.addKeyListener(new KeyListener() {
      @Override
      public void keyPressed(KeyEvent e) {
        lastRESelection = regExpCombo.getSelection();
      }

      @Override
      public void keyReleased(KeyEvent e) {
        lastRESelection = regExpCombo.getSelection();
      } });
    regExpCombo.addMouseListener(new MouseListener() {
      @Override
      public void mouseDoubleClick(MouseEvent e) {
      }

      @Override
      public void mouseDown(MouseEvent e) {
        lastRESelection = regExpCombo.getSelection();
      }

      @Override
      public void mouseUp(MouseEvent e) {
      } });
    tk.adapt(regExpCombo, true, true);

    Button stopButton = tk.createButton(form.getBody(), "", SWT.PUSH);
    gd = new GridData();
    gd.horizontalAlignment = GridData.END;
    gd.horizontalSpan = 1;
    gd.grabExcessHorizontalSpace = false;
    stopButton.setLayoutData(gd);
    PluginImageRegistry imageRegistry = (PluginImageRegistry) QuickRExPlugin.getDefault().getImageRegistry();
    stopButton.setImage(imageRegistry.getDescriptor(PluginImageRegistry.IMG_STOP).createImage());
    stopButton.setToolTipText(Messages.getString("views.QuickRExView.stopButton.tooltip"));
    stopButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        handleStopButtonPressed();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {}
    });

    createRegExpContentAssist();
  }

  private void handleStopButtonPressed() {

  }

  private void createRegExpContentAssist() {
    regExpContentAssistant = new SubjectControlContentAssistant();
    regExpContentAssistant.enableAutoActivation(false);
    regExpContentAssistant.enableAutoInsert(true);
    regExpContentAssistant.setContentAssistProcessor(new RegExpContentAssistProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
    regExpContentAssistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
    regExpContentAssistant.setRestoreCompletionProposalSize(QuickRExPlugin.getDefault().getDialogSettings()); //$NON-NLS-1$
    regExpContentAssistant.setInformationControlCreator(new IInformationControlCreator() {
      @Override
      public IInformationControl createInformationControl(Shell parent) {
        return new DefaultInformationControl(parent);
      }
    });
    regExpContentAssistant.install(new ComboContentAssistSubjectAdapter(regExpCombo));
    ContentAssistHandler.createHandlerForCombo(regExpCombo, regExpContentAssistant);
  }

  private void makeActions() {
    jcopyAction = new JCopyAction();
  }

  private void contributeToActionBars() {
    IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
    fillToolBar(toolbar);
  }

  private void fillToolBar(IToolBarManager manager) {
    manager.add(jcopyAction);
  }

  private void redrawFourthLine() {
    nextButton.redraw();
    previousButton.redraw();
    matches.redraw();
  }

  private void redrawFifthLine() {
    nextGroupButton.redraw();
    previousGroupButton.redraw();
    groups.redraw();
  }

  private ITextEditor getActiveEditor() {
    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof ITextEditor) {
      return (ITextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    } else {
      return null;
    }
  }

  /**
   * The handle-method for copying to the current editor (escaping for Java).
   */
  public void handleCopyButtonPressed() {
    copyToEditor(StringUtils.escapeForJava(regExpCombo.getText()));
  }

  private void copyToEditor(String string) {
    try {
      int currentOffset = ((ITextSelection) getActiveEditor().getSelectionProvider().getSelection()).getOffset();
      int currentLength = ((ITextSelection) getActiveEditor().getSelectionProvider().getSelection()).getLength();
      getActiveEditor().getDocumentProvider().getDocument(getActiveEditor().getEditorInput()).replace(currentOffset, currentLength, string);
      getActiveEditor().getSelectionProvider().setSelection(new TextSelection(currentOffset, string.length()));
    } catch (Throwable t) {
      // nop...
    }
  }

  private void handleNextGroupButtonPressed() {
    hits.getCurrentMatch().toNextGroup();
    groups.setText(escapeMnemonic(Messages.getString("views.QuickRExView.result.group", new Object[] { hits.getCurrentMatch().getNumberOfGroups(), //$NON-NLS-1$
        fetchGroupID(), hits.getCurrentMatch().getCurrentGroup().getText() })));
    nextGroupButton.setEnabled(hits.getCurrentMatch().hasNextGroup());
    previousGroupButton.setEnabled(hits.getCurrentMatch().hasPreviousGroup());
    updateMatchView(hits.getCurrentMatch());
  }

  private void handlePreviousGroupButtonPressed() {
    hits.getCurrentMatch().toPreviousGroup();
    groups.setText(escapeMnemonic(Messages.getString("views.QuickRExView.result.group", new Object[] { hits.getCurrentMatch().getNumberOfGroups(), //$NON-NLS-1$
        fetchGroupID(), hits.getCurrentMatch().getCurrentGroup().getText() })));
    nextGroupButton.setEnabled(hits.getCurrentMatch().hasNextGroup());
    previousGroupButton.setEnabled(hits.getCurrentMatch().hasPreviousGroup());
    updateMatchView(hits.getCurrentMatch());
  }

  private void handleNextButtonPressed() {
    hits.toNextMatch();
    Match match = hits.getCurrentMatch();
    matches.setText(Messages.getString("views.QuickRExView.result.match", new Object[] { hits.getNumberOfMatches(), //$NON-NLS-1$
        match.getStart(), match.getEnd() }));
    updateMatchView(match);
    nextButton.setEnabled(hits.hasNextMatch());
    previousButton.setEnabled(hits.hasPreviousMatch());
    if (hits.getCurrentMatch().getNumberOfGroups() > 0) {
      groups.setText(escapeMnemonic(Messages.getString("views.QuickRExView.result.group", new Object[] { hits.getCurrentMatch().getNumberOfGroups(), //$NON-NLS-1$
          fetchGroupID(), hits.getCurrentMatch().getCurrentGroup().getText() })));
    } else {
      groups.setText(Messages.getString("views.QuickRExView.result.group.none")); //$NON-NLS-1$
    }
    nextGroupButton.setEnabled(hits.getCurrentMatch().hasNextGroup());
    previousGroupButton.setEnabled(hits.getCurrentMatch().hasPreviousGroup());
  }

  private void handlePreviousButtonPressed() {
    hits.toPreviousMatch();
    Match match = hits.getCurrentMatch();
    matches.setText(Messages.getString("views.QuickRExView.result.match", new Object[] { hits.getNumberOfMatches(), //$NON-NLS-1$
        match.getStart(), match.getEnd() }));
    updateMatchView(match);
    nextButton.setEnabled(hits.hasNextMatch());
    previousButton.setEnabled(hits.hasPreviousMatch());
    if (hits.getCurrentMatch().getNumberOfGroups() > 0) {
      groups.setText(escapeMnemonic(Messages.getString("views.QuickRExView.result.group", new Object[] { hits.getCurrentMatch().getNumberOfGroups(), //$NON-NLS-1$
          fetchGroupID(), hits.getCurrentMatch().getCurrentGroup().getText() })));
    } else {
      groups.setText(Messages.getString("views.QuickRExView.result.group.none")); //$NON-NLS-1$
    }
    nextGroupButton.setEnabled(hits.getCurrentMatch().hasNextGroup());
    previousGroupButton.setEnabled(hits.getCurrentMatch().hasPreviousGroup());
  }

  private void handleTestTextModified() {
    updateEvaluation();
  }

  private void handleRegExpModified() {
    updateEvaluation();
  }

  private void updateMatchView(Match match) {
    updateMatchView(match, true);
  }

  private void updateMatchView(Match match, boolean evaluated) {
    testText.setStyleRange(new StyleRange(0, testText.getText().length(), null, null));
    if (!evaluated) {
      testText.setBackground(JFaceResources.getColorRegistry().get(NOT_EVALUATED_BG_COLOR_KEY));
    } else {
      testText.setBackground(null);
    }
    if (hits.getAllMatches() != null && hits.getAllMatches().length > 0) {
      testText.setStyleRanges(getStyleRanges(hits.getAllMatches()));
    }
    if (match != null) {
      testText.setStyleRange(new StyleRange(match.getStart(), match.getEnd() - match.getStart(), JFaceResources.getColorRegistry().get(
          CURRENT_MATCH_FG_COLOR_KEY), JFaceResources.getColorRegistry().get(CURRENT_MATCH_BG_COLOR_KEY), SWT.NORMAL));
      if (match.getCurrentGroup() != null && match.getCurrentGroup().getStart() >= 0) {
        testText.setStyleRange(new StyleRange(match.getCurrentGroup().getStart(), match.getCurrentGroup().getEnd()
            - match.getCurrentGroup().getStart(), JFaceResources.getColorRegistry().get(CURRENT_MATCH_FG_COLOR_KEY), JFaceResources
            .getColorRegistry().get(CURRENT_MATCH_BG_COLOR_KEY), SWT.BOLD));
      }
      // scroll horizontally if needed
      testText.setTopIndex(testText.getLineAtOffset(match.getStart()));
    }
  }

  private StyleRange[] getStyleRanges(Match[] matches) {
    StyleRange[] ranges = new StyleRange[matches.length];
    for (int i = 0; i < matches.length; i++) {
      int start = matches[i].getStart();
      int length = matches[i].getEnd() - start;
      Color foreground = JFaceResources.getColorRegistry().get(MATCH_FG_COLOR_KEY);
      Color background = JFaceResources.getColorRegistry().get(MATCH_BG_COLOR_KEY);
      ranges[i] = new StyleRange(start, length, foreground, background);
    }
    return ranges;
  }

  private void updateEvaluation() {
    Point selection = regExpCombo.getSelection();
    evaluate();
    regExpCombo.setSelection(selection);
  }

  private void evaluate() {
    if (regExpCombo.getText() != null && testText.getText() != null) {
      matches.setForeground(null);
      matches.setText(""); //$NON-NLS-1$
      groups.setText(""); //$NON-NLS-1$

      final String sRegExpCombo = regExpCombo.getText();
      final String sTestText = testText.getText();

      new Job("QuickREx Evaluation") {
        @Override
        protected IStatus run(IProgressMonitor monitor) {
          try {
            hits.init(sRegExpCombo, sTestText, currentFlags);
          } catch (Throwable throwable) {
            hits.setException(throwable);
          }


          Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
              // TODO Auto-generated method stub
              if (hits.containsException()) {
                Throwable t = hits.getException();
                if (t instanceof PatternSyntaxException) {
                  matches.setText(Messages.getString("views.QuickRExView.result.match.illegalPattern", new Object[]{StringUtils.firstLine(t.getMessage())})); //$NON-NLS-1$
                } else {
                  String msg = t.getMessage();
                  if (msg == null) {
                    msg = t.toString();
                  }
                  matches.setText(Messages.getString("views.QuickRExView.result.match.parserException", new Object[]{msg})); //$NON-NLS-1$
                }

                matches.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
                hits.reset();
                updateMatchView(null);
                regExpCombo.setFocus();
                groups.setText(""); //$NON-NLS-1$
                globalMatch.setText(""); //$NON-NLS-1$
                nextButton.setEnabled(false);
                previousButton.setEnabled(false);
                nextGroupButton.setEnabled(false);
                previousGroupButton.setEnabled(false);
              } else if (hits.containsMatches()) {
                Match match = hits.getCurrentMatch();
                updateMatchView(match);
                matches.setText(Messages.getString("views.QuickRExView.result.match", new Object[] { hits.getNumberOfMatches(), //$NON-NLS-1$
                    match.getStart(), match.getEnd() }));
                globalMatch.setText(Messages.getString("views.QuickRExView.result.globalMatch", new Object[] { hits.isGlobalMatch()})); //$NON-NLS-1$
                nextButton.setEnabled(hits.hasNextMatch());
                previousButton.setEnabled(hits.hasPreviousMatch());
                if (hits.getCurrentMatch().getNumberOfGroups() > 0) {
                  groups.setText(escapeMnemonic(Messages.getString("views.QuickRExView.result.group", new Object[] { hits.getCurrentMatch().getNumberOfGroups(), //$NON-NLS-1$
                      fetchGroupID(), hits.getCurrentMatch().getCurrentGroup().getText() })));
                } else {
                  groups.setText(Messages.getString("views.QuickRExView.result.group.none")); //$NON-NLS-1$
                }
                nextGroupButton.setEnabled(hits.getCurrentMatch().hasNextGroup());
                previousGroupButton.setEnabled(hits.getCurrentMatch().hasPreviousGroup());
              } else {
                updateMatchView(null);
                matches.setText(Messages.getString("views.QuickRExView.result.match.none")); //$NON-NLS-1$
                groups.setText(""); //$NON-NLS-1$
                globalMatch.setText(Messages.getString("views.QuickRExView.result.globalMatch", new Object[] { hits.isGlobalMatch()})); //$NON-NLS-1$
                nextButton.setEnabled(false);
                previousButton.setEnabled(false);
                nextGroupButton.setEnabled(false);
                previousGroupButton.setEnabled(false);
              }
            }
          });

          return Status.OK_STATUS;
        }
      }.schedule();

      redrawFourthLine();
      redrawFifthLine();
    }
  }

  private String escapeMnemonic(String string) {
    return string.replaceAll("&", "&&");
  }

  private String fetchGroupID() {
    int index = hits.getCurrentMatch().getCurrentGroup().getIndex();
    return Integer.toString(index);
  }

  @Override
  public void setFocus() {
  }

  @Override
  public void dispose() {
    QuickRExPlugin.getDefault().saveSelectedFlagValues(currentFlags);
    super.dispose();
  }

  /**
   * Set the current RE to the passed String.
   *
   * @param re the String to use
   */
  public void setRegularExpression(String re) {
    regExpCombo.setText(re);
  }

  /**
   * Set the current test-text to the passed String.
   *
   * @param text the String to use
   */
  public void setTestText(String text) {
    testText.setText(text);
  }

  /**
   * Returns the current RE.
   *
   * @return the current RE
   */
  public String getRegularExpression() {
    return regExpCombo.getText();
  }

  /**
   * Returns the last selection in the RE-Combo.
   *
   * @return the last selection
   */
  public Point getLastComboSelection() {
    return lastRESelection;
  }
}