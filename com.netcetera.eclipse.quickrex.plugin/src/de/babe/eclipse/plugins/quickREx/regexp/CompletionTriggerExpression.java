/*******************************************************************************
 * Copyright (c) 2005 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.regexp;

import java.util.regex.Pattern;

/**
 * @author bastian.bergerhoff
 */
class CompletionTriggerExpression extends CompletionTrigger {

  public final static String INSTANCE_QNAME = "retrigger";  //$NON-NLS-1$
  public final static String REG_EXP_ATTRIBUTE_QNAME = "re"; //$NON-NLS-1$

  private final String regExp;
  private final Pattern pattern;
  private final String proposal;
  private final String plainProposal;

  /**
   * @param p_regExp
   * @param p_proposal
   */
  CompletionTriggerExpression(String p_regExp, String p_proposal, String p_plainProposal) {
    this.regExp = p_regExp;
    this.proposal = p_proposal;
    this.pattern = Pattern.compile(this.regExp);
    this.plainProposal = p_plainProposal;
  }

  /*package*/ String getRegExp() {
    return this.regExp;
  }

  /*package*/ @Override
  String getPlainProposal() {
    return this.plainProposal;
  }

  /*package*/ String getProposal() {
    return this.proposal;
  }

  /* (non-Javadoc)
   * @see de.babe.eclipse.plugins.quickREx.regexp.CompletionTrigger#isMatchFor(java.lang.String)
   */
  @Override
  public boolean isMatchFor(String text) {
    return this.pattern.matcher(text).matches();
  }

  /* (non-Javadoc)
   * @see de.babe.eclipse.plugins.quickREx.regexp.CompletionTrigger#getInsertString(java.lang.String)
   */
  @Override
  public String getInsertString(String text) {
    return this.proposal;
  }

  /* (non-Javadoc)
   * @see de.babe.eclipse.plugins.quickREx.regexp.CompletionTrigger#getInsertString()
   */
  @Override
  public String getInsertString() {
    return this.getInsertString(this.text);
  }

  /* (non-Javadoc)
   * @see de.babe.eclipse.plugins.quickREx.regexp.CompletionTrigger#compareTo(de.babe.eclipse.plugins.quickREx.regexp.CompletionTrigger)
   */
  @Override
  public int compareTo(CompletionTrigger p_other) {
    // Always prefer WordCompletions
    if (p_other instanceof CompletionTriggerWord) {
      if ((this.getPlainProposal().startsWith("\\") //$NON-NLS-1$
            || this.getPlainProposal().startsWith("[") //$NON-NLS-1$
            || this.getPlainProposal().startsWith("("))
          && this.getInsertString().length() == 1) { //$NON-NLS-1$
        return -1;
      } else {
        return +1;
      }
    }
    // The shorter the String to insert, the better...
    int thisLength = this.getInsertString().length();
    int otherLength = p_other.getInsertString().length();
    if (thisLength < otherLength) {
      return -1;
    } else {
      return +1;
    }
  }
}
