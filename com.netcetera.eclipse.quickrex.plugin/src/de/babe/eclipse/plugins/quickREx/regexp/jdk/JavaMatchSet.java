/*******************************************************************************
 * Copyright (c) 2005 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *     Andreas Studer - Contributions to handling global FLAGS
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.regexp.jdk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.babe.eclipse.plugins.quickREx.regexp.Flag;
import de.babe.eclipse.plugins.quickREx.regexp.MatchSet;

/**
 * MatchSet using JDK-regular expressions.
 *
 * @author bastian.bergerhoff, andreas.studer
 */
public class JavaMatchSet implements MatchSet {

  private final Pattern pattern;
  private final Matcher matcher;

  private static final List<Flag> FLAGS = new ArrayList<>();

  static {
    FLAGS.add(JavaFlag.JDK_CANON_EQ);
    FLAGS.add(JavaFlag.JDK_CASE_INSENSITIVE);
    FLAGS.add(JavaFlag.JDK_COMMENTS);
    FLAGS.add(JavaFlag.JDK_DOTALL);
    FLAGS.add(JavaFlag.JDK_MULTILINE);
    FLAGS.add(JavaFlag.JDK_UNICODE_CASE);
    FLAGS.add(JavaFlag.JDK_UNIX_LINES);
  }

  /**
   * Returns a Collection of all Compiler-Flags the JDK-implementation
   * knows about.
   *
   * @return a Collection of all Compiler-Flags the JDK-implementation
   * knows about
   */
  public static List<Flag> getAllFlags() {
    return Collections.unmodifiableList(FLAGS);
  }

  /**
   * The constructor - uses JDK-regular expressions
   * to evaluate the passed regular expression against
   * the passed text.
   *
   * @param regExp the regular expression
   * @param text the text to evaluate regExp against
   * @param FLAGS a Collection of Flags to pass to the Compiler
   */
  public JavaMatchSet(String regExp, String text, Collection flags) {
    int iFlags = 0;
    for (Iterator iter = flags.iterator(); iter.hasNext();) {
      Flag element = (Flag)iter.next();
      iFlags = iFlags | element.getFlag();
    }
    pattern = Pattern.compile(regExp, iFlags);
    matcher = pattern.matcher(text);
  }

  /* (non-Javadoc)
   * @see de.babe.eclipse.plugins.quickREx.regexp.MatchSet#nextMatch()
   */
  @Override
  public boolean nextMatch() {
    return matcher.find();
  }

  /* (non-Javadoc)
   * @see de.babe.eclipse.plugins.quickREx.regexp.MatchSet#start()
   */
  @Override
  public int start() {
    return matcher.start();
  }

  /* (non-Javadoc)
   * @see de.babe.eclipse.plugins.quickREx.regexp.MatchSet#end()
   */
  @Override
  public int end() {
    return matcher.end();
  }

  /* (non-Javadoc)
   * @see de.babe.eclipse.plugins.quickREx.regexp.MatchSet#groupCount()
   */
  @Override
  public int groupCount() {
    return matcher.groupCount();
  }

  /* (non-Javadoc)
   * @see de.babe.eclipse.plugins.quickREx.regexp.MatchSet#groupContents(int)
   */
  @Override
  public String groupContents(int groupIndex) {
    return matcher.group(groupIndex);
  }

  /* (non-Javadoc)
   * @see de.babe.eclipse.plugins.quickREx.regexp.MatchSet#groupStart(int)
   */
  @Override
  public int groupStart(int groupIndex) {
    return matcher.start(groupIndex);
  }

  /* (non-Javadoc)
   * @see de.babe.eclipse.plugins.quickREx.regexp.MatchSet#groupEnd(int)
   */
  @Override
  public int groupEnd(int groupIndex) {
    return matcher.end(groupIndex);
  }

  @Override
  public boolean matches() {
    return matcher.matches();
  }
}
