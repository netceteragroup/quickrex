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

  public static final String INSTANCE_QNAME = "retrigger";  //$NON-NLS-1$
  public static final String REG_EXP_ATTRIBUTE_QNAME = "re"; //$NON-NLS-1$

  private final String regExp;
  private final Pattern pattern;
  private final String proposal;
  private final String plainProposal;

  /**
   * @param regExp
   * @param proposal
   */
  CompletionTriggerExpression(String regExp, String proposal, String plainProposal) {
    this.regExp = regExp;
    this.proposal = proposal;
    this.pattern = Pattern.compile(this.regExp);
    this.plainProposal = plainProposal;
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

  @Override
  public boolean isMatchFor(String text) {
    return this.pattern.matcher(text).matches();
  }

  @Override
  public String getInsertString(String text) {
    return this.proposal;
  }

  @Override
  public String getInsertString() {
    return this.getInsertString(this.text);
  }

  @Override
  public int compareTo(CompletionTrigger other) {
    // Always prefer WordCompletions
    if (other instanceof CompletionTriggerWord) {
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
    int otherLength = other.getInsertString().length();
    if (thisLength < otherLength) {
      return -1;
    } else {
      return +1;
    }
  }
}
