/*******************************************************************************
 * Copyright (c) 2005, 2007 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.regexp;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author bastian.bergerhoff
 */
public class RECompletionProposal {
  
  public final static String INSTANCE_QNAME = "proposal";  //$NON-NLS-1$
  public final static String KEY_ATTRIBUTE_QNAME = "key"; //$NON-NLS-1$
  public final static String PLAIN_VALUE_ATTRIBUTE_QNAME = "value"; //$NON-NLS-1$
  public final static String ALLOW_PLAIN_ATTRIBUTE_QNAME = "allowPlain"; //$NON-NLS-1$
  public static final String DISPLAY_STRING_ATTRIBUTE_QNAME = "displayString"; //$NON-NLS-1$
  public static final String ADD_INFO_ATTRIBUTE_QNAME = "additionalInfo"; //$NON-NLS-1$

  private final String key;
  private final String plainProposal;
  private final Vector triggers;
  private final boolean allowPlain;
  private String text;
  private String displayString;
  private String additionalInfo;
  
  public RECompletionProposal(String p_proposalKey, String p_plainProposal) {
    this(p_proposalKey, p_plainProposal, true);
  }

  public RECompletionProposal(String p_proposalKey, String p_plainProposal, boolean p_allowPlain) {
    this.key = p_proposalKey;
    this.plainProposal = p_plainProposal;
    this.triggers = new Vector();
    this.allowPlain = p_allowPlain;
  }

  public void addTriggerExpression(String p_regExp, String p_proposal) {
    this.triggers.add(0,new CompletionTriggerExpression(p_regExp, p_proposal, this.plainProposal));
  }

  public void addTriggerWord(String p_word, String p_extendWith) {
    this.triggers.add(0,new CompletionTriggerWord(p_word, p_extendWith, this.plainProposal));    
  }

  /**
   * @return Returns the additionalInfo.
   */
  public String getAdditionalInfo() {
    return this.additionalInfo;
  }
  
  /**
   * @param additionalInfo The additionalInfo to set.
   */
  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }
  
  /**
   * @return Returns the displayString.
   */
  public String getDisplayString() {
    return this.displayString;
  }
  
  /**
   * @param displayString The displayString to set.
   */
  public void setDisplayString(String displayString) {
    this.displayString = displayString;
  }

  private boolean isPlainMatch() {
    return isMatch() && getMatchingTrigger() == null;
  }

  public String getInsertString() {
    if (this.text == null || this.text.length() == 0) {
      return this.plainProposal;
    }
    for (Iterator triggerIt = triggers.iterator(); triggerIt.hasNext();) {
      CompletionTrigger trigger = (CompletionTrigger)triggerIt.next();
      if (trigger.isMatchFor(this.text)) {
        return trigger.getInsertString(this.text);
      }
    }
    return this.plainProposal;
  }

  public String getInsertString(String p_text) {
    if (p_text == null || p_text.length() == 0) {
      return this.plainProposal;
    }
    for (Iterator triggerIt = triggers.iterator(); triggerIt.hasNext();) {
      CompletionTrigger trigger = (CompletionTrigger)triggerIt.next();
      if (trigger.isMatchFor(p_text)) {
        return trigger.getInsertString(p_text);
      }
    }
    return this.plainProposal;
  }

  public boolean isMatch() {
    if (this.text == null || this.text.length() == 0) {
      return this.allowPlain;
    } else {
      for (Iterator triggerIt = triggers.iterator(); triggerIt.hasNext();) {
        CompletionTrigger trigger = (CompletionTrigger)triggerIt.next();
        if (trigger.isMatchFor(this.text)) {
          // Found a matching trigger
          return true;
        }
      }
      // Found no matching trigger, but may nevertheless be ok
      return this.allowPlain;
    }
  }

  public boolean isMatchFor(String p_text) {
    if (p_text == null || p_text.length() == 0) {
      return this.allowPlain;
    } else {
      for (Iterator triggerIt = triggers.iterator(); triggerIt.hasNext();) {
        CompletionTrigger trigger = (CompletionTrigger)triggerIt.next();
        if (trigger.isMatchFor(p_text)) {
          // Found a matching trigger
          return true;
        }
      }
      // Found no matching trigger, but may nevertheless be ok
      return this.allowPlain;
    }
  }

  public void setText(String p_text) {
    this.text = p_text;
  }

  public String getKey() {
    return this.key;
  }

  /*package*/ String getPlainProposal() {
    return this.plainProposal;
  }

  /**
   * Returns -1 if this proposal is preferrable to the passed proposal, 0 if they are equal
   * and +1 if the passed proposal is preferrable to this one.
   * 
   * @param p_other the RECompletionProposal this should be compared to
   * @return -1, 0 or +1 depending on the passed proposal
   */
  public int compareTo(RECompletionProposal p_other) {
    // Always prefer anything to a plain proposal
    if (this.text == null || this.text.length() == 0) {
      return +1;
    }
    // If this is no match or a plain match (not an actual one), prefer the other one
    if (!this.isMatch() || this.isPlainMatch()) {
      return +1;
    } 
    // If this is a match but the other is not or is a plain match (not an actual one), prefer this one
    if (!p_other.isMatch() || p_other.isPlainMatch()) {
      return -1;
    }
    // Both match, let the triggers decide
    return this.getMatchingTrigger().compareTo(p_other.getMatchingTrigger());
  }


  private CompletionTrigger getMatchingTrigger() {
    for (Iterator triggerIt = triggers.iterator(); triggerIt.hasNext();) {
      CompletionTrigger trigger = (CompletionTrigger)triggerIt.next();
      if (trigger.isMatchFor(this.text)) {
        trigger.setText(this.text);
        return trigger;
      }
    }
    return null;
  }
}
