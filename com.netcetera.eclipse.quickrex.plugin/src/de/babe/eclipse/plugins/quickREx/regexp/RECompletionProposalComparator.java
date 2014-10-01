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

import java.util.Comparator;

/**
 * @author bastian.bergerhoff
 */
class RECompletionProposalComparator implements Comparator<RECompletionProposal> {

  @Override
  public int compare(RECompletionProposal prop1, RECompletionProposal prop2) {
    return prop1.compareTo(prop2);
  }
}
