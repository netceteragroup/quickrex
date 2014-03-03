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

import java.util.Vector;


/**
 * @author bastian.bergerhoff
 */
public class Match {

	private final int start;
	private final int end;
  private final String text;
	private Vector groupData = new Vector();
	private int groupIndex = 0;

	/**
   * The constructor. Instances of this class are immutable.
   * 
	 * @param p_start the start-index of this match
	 * @param p_end the end-index of this match
	 */
	public Match(int p_start, int p_end, String p_text) {
		this.start = p_start;
		this.end = p_end;
    this.text = p_text;
	}
	
	/**
   * Returns the end-index of this match
   * 
	 * @return the end-index
	 */
	public int getEnd() {
		return this.end;
	}

	/**
   * Returns the start-index of this match
   * 
	 * @return the start-index
	 */
	public int getStart() {
		return this.start;
	}

  /**
   * Returns the textual contents of this match
   * 
   * @return the textual contents
   */
  public String getText() {
    return this.text;
  }

	/**
   * Returns <code>true</code> if and only if this match has a 'next group'.
   * Groups may be navigated by calling @see Match#toNextGroup() and
   * @see Match#toPreviousGroup()
   * 
	 * @return <code>true</code> if and only if this match has a 'next group'
	 */
	public boolean hasNextGroup() {
		return (this.groupIndex+1)<groupData.size();
	}

  /**
   * Returns <code>true</code> if and only if this match has a 'previous group'.
   * Groups may be navigated by calling @see Match#toNextGroup() and
   * @see Match#toPreviousGroup()
   * 
   * @return <code>true</code> if and only if this match has a 'previous group'
   */
	public boolean hasPreviousGroup() {
		return this.groupIndex>0;
	}

	/**
   * Returns the number of groups defined for this match
   * 
	 * @return the number of groups in this match
	 */
	public int getNumberOfGroups() {
		return groupData.size();
	}

	/**
	 * Makes the next group the current one. Only call if @see Match#hasNextGroup()
   * returns <code>true</code>
	 */
	public void toNextGroup() {
		this.groupIndex++;
	}

  /**
   * Makes the previous group the current one. Only call if @see Match#hasPreviousGroup()
   * returns <code>true</code>
   */
	public void toPreviousGroup() {
		this.groupIndex--;
	}

	/**
   * Returns the current group or <code>null</code>. Initially, the current group is
   * the one first added using @see Match#addGroup(Group).
   * 
	 * @return the current group or <code>null</code>
	 */
	public Group getCurrentGroup() {
		if (!groupData.isEmpty()) {
			return (Group)groupData.get(this.groupIndex);
		} else {
			return null;
		}
	}

	/**
   * Adds the passed group to the list of @see Group s this match holds
   * 
	 * @param p_group the Group to be added
	 */
	public void addGroup(Group p_group) {
    if (p_group.getText() != null) {
      this.groupData.add(p_group);
    }
	}
}