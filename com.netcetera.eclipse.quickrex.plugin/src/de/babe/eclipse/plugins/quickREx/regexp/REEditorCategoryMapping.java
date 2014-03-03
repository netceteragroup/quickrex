/*******************************************************************************
 * Copyright (c) 2007 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.regexp;

/**
 * @author bastian.bergerhoff
 */
public class REEditorCategoryMapping {
  
  public final static String INSTANCE_QNAME = "categoryMapping";  //$NON-NLS-1$
  public final static String PROPOSAL_KEY_ATTRIBUTE_QNAME = "proposalKey"; //$NON-NLS-1$
  public final static String CATEGORY_ATTRIBUTE_QNAME = "category"; //$NON-NLS-1$

  private final String proposalKey;
  private final String category;
  
  /**
   * The constructor
   * 
   * @param p_proposalKey
   * @param p_category
   */
  public REEditorCategoryMapping(String p_proposalKey, String p_category) {
    this.proposalKey = p_proposalKey;
    this.category = p_category;
  }

  /**
   * Returns the Category (name) for the proposal as String
   * 
   * @return the category-name
   */
  public String getCategory() {
    return this.category;
  }
  
  /**
   * Returns the key of the proposal as String
   * 
   * @return the key of the proposal
   */
  public String getProposalKey() {
    return this.proposalKey;
  }
}
