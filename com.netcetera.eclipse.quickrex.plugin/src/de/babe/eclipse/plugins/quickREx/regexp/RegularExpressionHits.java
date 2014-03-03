/*******************************************************************************
 * Copyright (c) 2005 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *     Georg Sendt - added JRegex-Implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.regexp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;

import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;

/**
 * @author bastian.bergerhoff, georg.sendt
 */
public class RegularExpressionHits {

  private Vector matchData;

  private int matchIndex;

  private Throwable throwable;

  private boolean globalMatch;

  /**
   * (Re)Initializes this instance with data from the passed Matcher.
   * 
   * @param p_matcher
   *          the Matcher to use for initialization
   */
  public void init(String p_RegExp, String p_testText, Collection flags) {
    MatchSet matches = MatchSetFactory.createMatchSet(QuickRExPlugin.getDefault().getREFlavour(), p_RegExp, p_testText, flags);
    matchData = new Vector();
    while (matches.nextMatch()) {
      Match match = new Match(matches.start(), matches.end(), matches.groupContents(0));
      for (int g = 0; g < matches.groupCount(); g++) {
        match.addGroup(new Group(g + 1, matches.groupContents(g + 1), matches.groupStart(g + 1), matches.groupEnd(g + 1)));
      }
      matchData.add(match);
    }
    if (matchData.size() > 0) {
      this.matchIndex = 0;
    } else {
      this.matchIndex = -1;
    }
    this.throwable = null;
    this.globalMatch = matches.matches();
  }

  public boolean isGlobalMatch() {
    return this.globalMatch;
  }


  public void setException(Throwable throwable) {
    this.throwable = throwable;
  }

  public Throwable getException() {
    return this.throwable;
  }

  public boolean containsException() {
    return this.throwable != null;
  }


  /**
   * Returns <code>true</code> if and only if the Matcher used when last
   * calling
   * 
   * @see RegularExpressionHits#init(Matcher) found at least one match
   * 
   * @return <code>true</code> if this hit contains matches
   */
  public boolean containsMatches() {
    return matchData != null && matchData.size() > 0;
  }

  /**
   * Returns <code>true</code> if and only if this instance has a 'next
   * match'. Matches may be navigated by calling
   * 
   * @see RegularExpressionHits#toNextMatch() and
   * @see RegularExpressionHits#toPreviousMatch()
   * 
   * @return <code>true</code> if and only if this instance has a 'next match'
   */
  public boolean hasNextMatch() {
    return (this.matchIndex + 1) < matchData.size();
  }

  /**
   * Returns <code>true</code> if and only if this instance has a 'previous
   * match'. Matches may be navigated by calling
   * 
   * @see RegularExpressionHits#toNextMatch() and
   * @see RegularExpressionHits#toPreviousMatch()
   * 
   * @return <code>true</code> if and only if this instance has a 'previous
   *         match'
   */
  public boolean hasPreviousMatch() {
    return this.matchIndex > 0;
  }

  /**
   * Returns the number of matches defined for this hit
   * 
   * @return the number of matches defined for this hit
   */
  public int getNumberOfMatches() {
    return matchData.size();
  }

  /**
   * Makes the next match the current one. Only call if
   * 
   * @see RegularExpressionHits#hasNextMatch() returns <code>true</code>
   */
  public void toNextMatch() {
    this.matchIndex++;
  }

  /**
   * Makes the previous match the current one. Only call if
   * 
   * @see RegularExpressionHits#hasPreviousMatch() returns <code>true</code>
   */
  public void toPreviousMatch() {
    this.matchIndex--;
  }

  /**
   * Returns the current match. Initially, the current match is the first one.
   * 
   * @return the current match
   */
  public Match getCurrentMatch() {
    return (Match)matchData.get(this.matchIndex);
  }

  /**
   * Returns all Matches contained in this instance as array
   * 
   * @return all Matches contained in this instance as array
   */
  public Match[] getAllMatches() {
    return (Match[])matchData.toArray(new Match[matchData.size()]);
  }

  /**
   * Resets this instance to an empty one containing no matches
   */
  public void reset() {
    this.matchData = new Vector();
    this.matchIndex = -1;
    this.throwable = null;
  }

  /**
   * Returns all matches separated by CRs and LFs
   * 
   * @return all matches separated by CRs and LFs
   */
  public String grep() {
    try {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      Iterator iter = matchData.iterator();
      while (iter.hasNext()) {
        Match element = (Match)iter.next();
        pw.println(element.getText());
      }
      pw.close();
      return sw.toString();
    } catch (NullPointerException npe) {
      return ""; //$NON-NLS-1$
    }
  }
}
