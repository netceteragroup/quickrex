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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author bastian.bergerhoff
 */
public class CompletionProposals {

  private HashMap keysMap;
  private HashMap proposalsMap;
  
  /**
   * The constructor
   */
  public CompletionProposals() {
    keysMap = new HashMap();
    proposalsMap = new HashMap();
  }
  
  /**
   * Returns a list of completion-proposal-keys for the passed flavour (one of the constants 
   * defined in MatchSetFactory).
   * 
   * @param p_flavour the flavour
   * @return an ArrayList of keys for the flavour
   */
  public ArrayList getKeys(int p_flavour) {
    return (ArrayList)keysMap.get(new Integer(p_flavour));
  }

  /**
   * Returns the proposal for the passed flavour with the passed key
   * 
   * @param p_flavour the flavour to look for
   * @param p_proposalKey the key for the proposal
   * @return the RECompletionProposal
   */
  public RECompletionProposal getProposal(int p_flavour, String p_proposalKey) {
    return (RECompletionProposal)((HashMap)proposalsMap.get(new Integer(p_flavour))).get(p_proposalKey);
  }

  /**
   * Sets the keys-list (as passed) for the passed flavour
   * 
   * @param p_flavour the flavour
   * @param p_keys the list of keys
   */
  public void setKeys(int p_flavour, ArrayList p_keys) {
    keysMap.put(new Integer(p_flavour), p_keys);
  }

  /**
   * Sets the proposals-map (as passed) for the passed flavour
   * 
   * @param p_flavour the flavour
   * @param p_proposals the HashMap with proposals (String-keys mapped to RECompletionProposal-instances)
   */
  public void setProposals(int p_flavour, HashMap p_proposals) {
    proposalsMap.put(new Integer(p_flavour), p_proposals);
  }

  /**
   * Copies key-lists and proposal-maps to the passed instance
   * 
   * @param p_proposals the instance to initialize
   */
  public void copyValuesTo(CompletionProposals p_proposals) {
    p_proposals.keysMap = this.keysMap;
    p_proposals.proposalsMap = this.proposalsMap;
  }
}
