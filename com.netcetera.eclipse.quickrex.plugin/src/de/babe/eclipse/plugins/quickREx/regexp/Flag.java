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

import java.util.HashMap;

public class Flag {

  protected static HashMap flags = new HashMap();

  private final String code;
  private final int flag;
  private final String name;
  private final String description;

  public String getCode() {
    return this.code;
  }

  public String getDescription() {
    return this.description;
  }

  public int getFlag() {
    return this.flag;
  }

  public String getName() {
    return this.name;
  }

  protected Flag(String code, int flag, String name, String description) {
    this.code = code;
    this.flag = flag;
    this.name = name;
    this.description = description;
  }
}
