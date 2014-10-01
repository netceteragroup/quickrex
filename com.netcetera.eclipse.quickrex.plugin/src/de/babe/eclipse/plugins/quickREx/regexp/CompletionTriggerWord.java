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

/**
 * @author bastian.bergerhoff
 */
class CompletionTriggerWord extends CompletionTrigger {

  static final String INSTANCE_QNAME = "wordtrigger";  //$NON-NLS-1$
  static final String WORD_ATTRIBUTE_QNAME = "word"; //$NON-NLS-1$

  private final String word;
  private final String extension;
  private final String plainProposal;

  /**
   * @param p_word
   */
  CompletionTriggerWord(String p_word, String p_extendWith, String p_plainProposal) {
    this.word = p_word;
    this.extension = p_extendWith;
    this.plainProposal = p_plainProposal;
  }


  String getWord() {
    return this.word;
  }

  @Override
  String getPlainProposal() {
    return this.plainProposal;
  }

  String getExtension() {
    return this.extension;
  }

  @Override
  public boolean isMatchFor(String text) {
    for (int i = 0; i < this.word.length(); i++) {
      if (text.endsWith(this.word.substring(0, this.word.length() - i))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String getInsertString(String text) {
    String wordRemainder = null;
    for (int i = 0; i < this.word.length(); i++) {
      if (text.endsWith(this.word.substring(0, this.word.length() - i))) {
        wordRemainder = this.word.substring(this.word.length() - i);
        break;
      }
    }
    return wordRemainder + this.extension;
  }

  @Override
  public String getInsertString() {
    return this.getInsertString(this.text);
  }

  @Override
  public int compareTo(CompletionTrigger other) {
    // Always prefer WordCompletions unless the standard proposal starts with a \ and only wants to add one character
    if (other instanceof CompletionTriggerExpression) {
      String otherPlainProposal = other.getPlainProposal();
      if ((otherPlainProposal.startsWith("\\") //$NON-NLS-1$
            || otherPlainProposal.startsWith("(") //$NON-NLS-1$
            || otherPlainProposal.startsWith("[")) //$NON-NLS-1$
          && other.getInsertString().length() == 1) {
        return +1;
      } else {
        return -1;
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
