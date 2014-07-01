/*******************************************************************************
 * Copyright (c) 2005, 2007 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation, all but:
 *     Georg Sendt - added JRegexp-related implementations
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.babe.eclipse.plugins.quickREx.regexp.CompletionProposalXMLHandler;
import de.babe.eclipse.plugins.quickREx.regexp.CompletionProposals;
import de.babe.eclipse.plugins.quickREx.regexp.EditorCategoryMappingXMLHandler;
import de.babe.eclipse.plugins.quickREx.regexp.Flag;
import de.babe.eclipse.plugins.quickREx.regexp.MatchSetFactory;
import de.babe.eclipse.plugins.quickREx.regexp.RECompletionProposal;
import de.babe.eclipse.plugins.quickREx.regexp.REEditorCategoryMapping;

/**
 * @author bastian.bergerhoff, georg.sendt
 */
public class QuickRExPlugin extends AbstractUIPlugin {

  private static QuickRExPlugin plugin;

  private ResourceBundle resourceBundle;

  private Map<String, List<RECompletionProposal>> jdkCatMappings;

  private List<String> jdkCategories;

  private CompletionProposals proposals;

  public static final String ID = "de.babe.eclipse.plugins.quickREx.QuickRExPlugin"; //$NON-NLS-1$

  public static final String EXPAND_NAVIGATION_SECTION = "de.babe.eclipse.plugins.quickREx.QuickRExPlugin.ExpandNavigationSection"; //$NON-NLS-1$

  private static final String LAST_SEARCH_SCOPE = "de.babe.eclipse.plugins.quickREx.QuickRExPlugin.LastSearchScope"; //$NON-NLS-1$

  private static final String LINK_RE_LIB_VIEW_WITH_EDITOR = "de.babe.eclipse.plugins.quickREx.QuickRExPlugin.LinkRELibViewWithEditor"; //$NON-NLS-1$

  private static final String JDK_PROPOSAL_FILE_NAME = "$nl$/jdkCompletion.xml"; //$NON-NLS-1$

  private static final String JDK_CATEGORIES_FILE_NAME = "$nl$/jdkCategories.xml"; //$NON-NLS-1$

  /**
   * The constructor.
   */
  public QuickRExPlugin() {
    super();
    plugin = this;
    try {
      resourceBundle = ResourceBundle.getBundle("de.babe.eclipse.plugins.quickREx.QuickRExPluginResources"); //$NON-NLS-1$
    } catch (MissingResourceException x) {
      resourceBundle = null;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#createImageRegistry()
   */
  @Override
  protected ImageRegistry createImageRegistry() {
    return new PluginImageRegistry(this);
  }

  /**
   * This method is called upon plug-in activation.
   */
  @Override
  public void start(BundleContext p_context) throws Exception {
    super.start(p_context);
    initProposals();
    prepareRegexpCategories();
  }

  private void prepareRegexpCategories() {

    jdkCategories = new ArrayList<>();
    Map<String, List<REEditorCategoryMapping>> editorMappings = new HashMap<>();
    initCategoriesFromFile(editorMappings, jdkCategories);
    jdkCatMappings = addProposalsToMappings(jdkCategories, editorMappings);

  }

  private Map<String, List<RECompletionProposal>> addProposalsToMappings(List<String> p_categories, Map<String, List<REEditorCategoryMapping>> p_catMappings) {
    Map<String, List<RECompletionProposal>> result = new HashMap<>(p_catMappings.size());
    for (String category : p_categories) {
      List<REEditorCategoryMapping> proposalKeys = p_catMappings.get(category);
      List<RECompletionProposal> proposalsForCat = new ArrayList<>();
      for (REEditorCategoryMapping element : proposalKeys) {
        String currentKey = element.getProposalKey();
        proposalsForCat.add(proposals.getProposal(currentKey));
      }
      result.put(category, proposalsForCat);
    }
    return result;
  }

  /**
   * Returns the shared instance.
   */
  public static QuickRExPlugin getDefault() {
    return plugin;
  }

  /**
   * Returns the plugin's resource bundle.
   */
  public ResourceBundle getResourceBundle() {
    return resourceBundle;
  }

  /**
   * Returns <code>true</code> if and only if currently the Reg. Exp. Lib.-View is linked with the RE-Entry-editor.
   *
   * @return <code>true</code> if and only if currently the Reg. Exp. Lib.-View is linked with the RE-Entry-editor
   */
  public boolean isLinkRELibViewWithEditor() {
    return getPreferenceStore().getBoolean(LINK_RE_LIB_VIEW_WITH_EDITOR);
  }

  /**
   * Set (and store) the flag governing if the Reg. Exp. Lib.-View is linked with the RE-Entry-editor.
   *
   * @param p_flag the state of the flag to set and store
   */
  public void setLinkRELibViewWithEditor(boolean p_flag) {
    getPreferenceStore().setValue(LINK_RE_LIB_VIEW_WITH_EDITOR, p_flag);
  }


  /**
   * Initializes the passed structure with the completion proposals defined in XML-files.
   * In case the files have already been parsed, the information is only copied to the
   * passed instance. If the files were not parsed yet, this is done now...
   *
   * @param p_proposals the structure to initialize
   */
  public void initCompletionProposals(CompletionProposals p_proposals) {
    if (this.proposals == null) {
      initProposals();
    }
    proposals.copyValuesTo(p_proposals);
  }

  private synchronized void initProposals() {
    proposals = new CompletionProposals();

    Map<String, RECompletionProposal> jdkProposals = new HashMap<>();
    List<String> jdkKeys = new ArrayList<>();
    initCompletionsFromFile(jdkProposals, jdkKeys);
    proposals.setKeys(jdkKeys);
    proposals.setProposals(jdkProposals);
  }

  private void initCompletionsFromFile(Map<String, RECompletionProposal> p_proposals, List<String> p_keys) {
    String filepath = JDK_PROPOSAL_FILE_NAME;
    String errorMsgKey = "QuickRExPlugin.error.message7"; //$NON-NLS-1$
    try (InputStream propFileStream = FileLocator.openStream(getBundle(), new Path(filepath), true)) {
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(propFileStream, new CompletionProposalXMLHandler(p_proposals, p_keys));
    } catch (Exception ex) {
      // nop, to be save
      IStatus status = new Status(IStatus.WARNING, QuickRExPlugin.ID, 3, Messages.getString(errorMsgKey), ex); //$NON-NLS-1$
      getLog().log(status);
    }
  }

  public void initCategoriesFromFile(Map<String, List<REEditorCategoryMapping>> p_mappings, List<String> p_categories) {
    String filepath = JDK_CATEGORIES_FILE_NAME;
    String errorMsgKey = "QuickRExPlugin.error.readerror.jdk.categories"; //$NON-NLS-1$
    try (InputStream propFileStream = FileLocator.openStream(getBundle(), new Path(filepath), true)) {
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(propFileStream, new EditorCategoryMappingXMLHandler(p_mappings, p_categories));
    } catch (Exception ex) {
      // nop, to be save
      IStatus status = new Status(IStatus.WARNING, QuickRExPlugin.ID, 3, Messages.getString(errorMsgKey), ex); //$NON-NLS-1$
      getLog().log(status);
    }
  }

  /**
   * Saves the values of all flags to the PreferenceStore, where any flag contained in the passed collection is saved as 'set', any flag known to the
   * MatchSetFactory but not contained in the passed Collection is saved as 'not set'.
   *
   * @param p_flags
   *          a Collection holding the actually set flags
   */
  public void saveSelectedFlagValues(Collection<? extends Flag> p_flags) {
    for (Flag element : MatchSetFactory.getAllSupportedFlags()) {
      getPreferenceStore().setValue(element.getCode(), p_flags.contains(element));
    }
  }

  /**
   * Returns <code>true</code> if and only if the passed Flag is saved as 'set' in the PreferenceStore.
   *
   * @param p_flag
   *          the flag to check for
   * @return the state for the flag in the store (set: true, not set: false)
   */
  public boolean isFlagSaved(Flag p_flag) {
    return getPreferenceStore().getBoolean(p_flag.getCode());
  }

  /**
   * Returns the scope used for the last search of the Reg. Exp. Library.
   *
   * @return the scope used
   */
  public int getLastSearchScope() {
    return getPreferenceStore().getInt(LAST_SEARCH_SCOPE);
  }

  /**
   * Set (and store) the scope used for the last search of the Reg. Exp. Library.
   *
   * @param p_scope the scope to set and store
   */
  public void setLastSearchScope(int p_scope) {
    getPreferenceStore().setValue(LAST_SEARCH_SCOPE, p_scope);
  }

  /**
   * Returns an ArrayList of Categories (in fact, Category-names) defined for the
   * passed RE-Flavour.
   *
   * @return an ArrayList of category-names (Strings) or null if the flavour is unkown
   */
  public List<String> getRECategories() {
    return jdkCategories;
  }

  /**
   * Returns an HashMap of Expressions mapped to Categories defined for the
   * passed RE-Flavour.
   *
   * @return a HashMap containing category-names as keys and ArrayListe of RECompletionProposal-
   *          instances as Objects.
   */
  public Map<String, List<RECompletionProposal>> getREMappings() {
    return jdkCatMappings;
  }
}