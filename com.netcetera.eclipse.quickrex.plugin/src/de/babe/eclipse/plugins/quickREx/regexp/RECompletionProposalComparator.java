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
public class RECompletionProposalComparator implements Comparator {

  public RECompletionProposalComparator() {
  }

  /* (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  public int compare(Object arg0, Object arg1) {
    RECompletionProposal prop1 = null;
    RECompletionProposal prop2 = null;
    try {
      prop1 = (RECompletionProposal)arg0;
      prop2 = (RECompletionProposal)arg1;
    } catch (ClassCastException cce) {
      return 0;
    }
    return prop1.compareTo(prop2);
  }
}
