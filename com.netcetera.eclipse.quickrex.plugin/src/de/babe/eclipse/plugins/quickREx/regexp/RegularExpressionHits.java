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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CancellationException;

/**
 * @author bastian.bergerhoff, georg.sendt
 */
public class RegularExpressionHits {

  private List<Match> matchData;

  private int matchIndex;

  private Throwable throwable;

  private boolean globalMatch;

  private boolean canceled;

  /**
   * (Re)Initializes this instance with data from the passed Matcher.
   */
  public synchronized void init(String regExp, CharSequence testText, Collection<Flag> flags) {
    this.throwable = null;

    try {
      doInit(regExp, testText, flags);
    } catch (CancellationException e) {
      this.canceled = true;
    } catch (Throwable t) {
      this.throwable = t;
    }
  }

  public synchronized  boolean isGlobalMatch() {
    return this.globalMatch;
  }

  public synchronized Throwable getException() {
    return this.throwable;
  }

  public synchronized boolean containsException() {
    return this.throwable != null;
  }

  public synchronized boolean isCanceled() {
    return this.canceled;
  }

  /**
   * Returns <code>true</code> if and only if the Matcher used when last
   * calling.
   *
   * @see RegularExpressionHits#init(String, CharSequence, Collection) found at least one match
   *
   * @return <code>true</code> if this hit contains matches
   */
  public synchronized boolean containsMatches() {
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
  public synchronized boolean hasNextMatch() {
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
  public synchronized boolean hasPreviousMatch() {
    return this.matchIndex > 0;
  }

  /**
   * Returns the number of matches defined for this hit.
   *
   * @return the number of matches defined for this hit
   */
  public synchronized int getNumberOfMatches() {
    return matchData.size();
  }

  /**
   * Makes the next match the current one. Only call if
   *
   * @see RegularExpressionHits#hasNextMatch() returns <code>true</code>
   */
  public synchronized void toNextMatch() {
    this.matchIndex++;
  }

  /**
   * Makes the previous match the current one. Only call if
   *
   * @see RegularExpressionHits#hasPreviousMatch() returns <code>true</code>
   */
  public synchronized void toPreviousMatch() {
    this.matchIndex--;
  }

  /**
   * Returns the current match. Initially, the current match is the first one.
   *
   * @return the current match
   */
  public synchronized Match getCurrentMatch() {
    return matchData.get(this.matchIndex);
  }

  /**
   * Returns all Matches contained in this instance as array.
   *
   * @return all Matches contained in this instance as array
   */
  public synchronized Match[] getAllMatches() {
    return matchData.toArray(new Match[matchData.size()]);
  }

  /**
   * Resets this instance to an empty one containing no matches.
   */
  public synchronized void reset() {
    this.matchData = new ArrayList<>();
    this.matchIndex = -1;
    this.throwable = null;
    this.globalMatch = false;
    this.canceled = false;
  }

  private void doInit(String regExp, CharSequence testText, Collection<Flag> flags) {
    MatchSet matches = MatchSetFactory.createMatchSet(regExp, testText, flags);
    matchData = new ArrayList<>();
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
    this.globalMatch = matches.matches();
  }

}
