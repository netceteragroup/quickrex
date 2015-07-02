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
abstract class CompletionTrigger {

  static final String COMPLETION_VALUE_ATTRIBUTE_QNAME = "completion"; //$NON-NLS-1$

  protected String text;

  public abstract boolean isMatchFor(String text);

  public abstract String getInsertString(String text);

  public abstract String getInsertString();

  public abstract int compareTo(CompletionTrigger other);

  abstract String getPlainProposal();

  /**
   * @param text
   */
  public void setText(String text) {
    this.text = text;
  }
}
