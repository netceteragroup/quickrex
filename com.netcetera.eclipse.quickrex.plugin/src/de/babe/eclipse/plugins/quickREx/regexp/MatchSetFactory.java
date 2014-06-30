/*******************************************************************************
 * Copyright (c) 2005 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *     Andreas Studer - Contributions to handling global flags
 *     Georg Sendt - added JRegexp-related implementations
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.regexp;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import de.babe.eclipse.plugins.quickREx.regexp.jdk.JavaMatchSet;

/**
 * @author bastian.bergerhoff, andreas.studer, georg.sendt
 */
public final class MatchSetFactory {

  public static final int JAVA_FLAVOUR = 1;

  /**
   * Factory-Method to create a MatchSet for the passed details.
   *
   * @param flavour
   *          one of the Flavour-Flags defined in this class
   * @param regExp
   *          the regular expression
   * @param text
   *          the text to match against the reg.exp.
   * @param flags
   *          a Collection of flags to pass to the Compiler. This may contain more flags than are applicable to the requested flavour. In this case,
   *          only those flags wich are applicable are taken into account when creating the MatchSet
   * @return a MatchSet as requested
   */
  public static MatchSet createMatchSet(int flavour, String regExp, String text, Collection flags) {
    Vector flavourFlags = new Vector(flags);
    flavourFlags.retainAll(MatchSetFactory.getAllFlags());
    return new JavaMatchSet(regExp, text, flavourFlags);
  }

  /**
   * Gets all possible flags for a specific regex-flavour.
   *
   * @return A collection of the type de.babe.eclipse.plugins.quickREx.regexp.Flag
   * @see de.babe.eclipse.plugins.quickREx.regexp.Flag
   */
  public static List<Flag> getAllFlags() {
    return JavaMatchSet.getAllFlags();
  }

  /**
   * Returns a Collection of all flags supported by any of the regular-expression Compilers used by the plug-in.
   *
   * @return a Collection of all flags supported by any of the regular-expression Compilers used by the plug-in
   */
  public static List<Flag> getAllSupportedFlags() {
    return MatchSetFactory.getAllFlags();
  }

  /**
   * Helper-Method to get the maximum number of flags supported by any of the regular-expression Compilers used by the plug-in.
   *
   * @return the maximum number of flags
   */
  public static int getMaxFlagColumns() {
    return JavaMatchSet.getAllFlags().size();
  }
}