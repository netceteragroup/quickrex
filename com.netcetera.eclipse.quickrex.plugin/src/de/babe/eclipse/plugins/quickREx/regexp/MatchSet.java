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
 * Abstracts matches in a text.
 *
 * @author bastian.bergerhoff
 */
public interface MatchSet {

  /**
   * Returns <code>true</code> if and only if there is a next match
   * in this <code>MatchSet</code>. Acts like <code>next()</code> in
   * an enumeration in that it causes the whole instance state to be
   * centered around the next match.
   *
   * @return <code>true</code> if and only if there is a next match
   */
  boolean nextMatch();

  /**
   * Returns the start-offset of the current match.
   *
   * @return the start-offset of the current match
   */
  int start();

  /**
   * Returns the end-offset of the current match.
   *
   * @return the end-offset of the current match
   */
  int end();

  /**
   * Returns the number of groups in the current match.
   * 0 is returned if there are no groups - the match itself
   * does not count as a group.
   *
   * @return the number of groups in the current match
   */
  int groupCount();

  /**
   * Returns the String-contents of the group with the passed
   * index.
   *
   * @param groupIndex the index of the group
   * @return the String-contents of the group
   */
  String groupContents(int groupIndex);

  /**
   * Returns the start-offset of the group with the passed
   * index.
   *
   * @param groupIndex the index of the group
   * @return the start-offset of the group
   */
  int groupStart(int groupIndex);

  /**
   * Returns the end-offset of the group with the passed
   * index.
   *
   * @param groupIndex the index of the group
   * @return the end-offset of the group
   */
  int groupEnd(int groupIndex);

  /**
   * Returns <code>true</code> if and only if the pattern
   * matches the whole input.
   *
   * @return <code>true</code> if and only if the pattern
   * matches the whole input
   */
  boolean matches();
}
