/*******************************************************************************
 * Copyright (c) 2005 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Andreas Studer - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.regexp.jdk;

import java.util.regex.Pattern;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.regexp.Flag;

/**
 * Class JavaFlag. This represents all flags for the Java Regex implementation.
 *
 * @author Andreas Studer
 * @version 1.0
 * @since 2.1
 */
final class JavaFlag extends Flag {

  static final Flag JDK_CANON_EQ = new JavaFlag(
      "de.babe.eclipse.plugins.quickREx.regexp.jdk.CANON_EQ", Pattern.CANON_EQ, Messages.getString("regexp.jdk.JavaFlag.canon_eq"), //$NON-NLS-1$ //$NON-NLS-2$
      Messages.getString("regexp.jdk.JavaFlag.canon_eq.description")); //$NON-NLS-1$

  static final Flag JDK_UNICODE_CASE = new JavaFlag(
      "de.babe.eclipse.plugins.quickREx.regexp.jdk.UNICODE_CASE", Pattern.UNICODE_CASE, Messages.getString("regexp.jdk.JavaFlag.unicode_case"), //$NON-NLS-1$ //$NON-NLS-2$
      Messages.getString("regexp.jdk.JavaFlag.unicode_case.description")); //$NON-NLS-1$

  static final Flag JDK_DOTALL = new JavaFlag(
      "de.babe.eclipse.plugins.quickREx.regexp.jdk.DOTALL", Pattern.DOTALL, Messages.getString("regexp.jdk.JavaFlag.dotall"), //$NON-NLS-1$ //$NON-NLS-2$
      Messages.getString("regexp.jdk.JavaFlag.dotall.description")); //$NON-NLS-1$

  static final Flag JDK_MULTILINE = new JavaFlag(
      "de.babe.eclipse.plugins.quickREx.regexp.jdk.MULTILINE", Pattern.MULTILINE, Messages.getString("regexp.jdk.JavaFlag.multiline"), //$NON-NLS-1$ //$NON-NLS-2$
      Messages.getString("regexp.jdk.JavaFlag.multiline.description")); //$NON-NLS-1$

  static final Flag JDK_COMMENTS = new JavaFlag(
      "de.babe.eclipse.plugins.quickREx.regexp.jdk.COMMENTS", Pattern.COMMENTS, Messages.getString("regexp.jdk.JavaFlag.comments"), //$NON-NLS-1$ //$NON-NLS-2$
      Messages.getString("regexp.jdk.JavaFlag.comments.description")); //$NON-NLS-1$

  static final Flag JDK_CASE_INSENSITIVE = new JavaFlag(
      "de.babe.eclipse.plugins.quickREx.regexp.jdk.CASE_INSENSITIVE", Pattern.CASE_INSENSITIVE, Messages.getString("regexp.jdk.JavaFlag.case_insensitive"), //$NON-NLS-1$ //$NON-NLS-2$
      Messages.getString("regexp.jdk.JavaFlag.case_insensitive.description")); //$NON-NLS-1$

  static final Flag JDK_UNIX_LINES = new JavaFlag(
      "de.babe.eclipse.plugins.quickREx.regexp.jdk.UNIX_LINES", Pattern.UNIX_LINES, Messages.getString("regexp.jdk.JavaFlag.unix_lines"), //$NON-NLS-1$ //$NON-NLS-2$
      Messages.getString("regexp.jdk.JavaFlag.unix_lines.description")); //$NON-NLS-1$


  private JavaFlag(String code, int flag, String name, String description) {
    super(code, flag, name, description);
  }
}