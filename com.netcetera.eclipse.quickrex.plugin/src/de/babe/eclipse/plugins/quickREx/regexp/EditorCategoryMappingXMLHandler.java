/*******************************************************************************
 * Copyright (c) 2007 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.regexp;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author bastian.bergerhoff
 */
public class EditorCategoryMappingXMLHandler extends DefaultHandler {

  private final HashMap mappings;

  private final ArrayList categories;

  private REEditorCategoryMapping currentMapping;

  /**
   * The constructor
   * 
   * @param p_categoryMappings
   * @param p_categories
   */
  public EditorCategoryMappingXMLHandler(HashMap p_categoryMappings, ArrayList p_categories) {
    this.mappings = p_categoryMappings;
    this.categories = p_categories;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String uri, String localName, String qName) {
    if (REEditorCategoryMapping.INSTANCE_QNAME.equals(qName)) {
      ArrayList currentContents = (ArrayList)mappings.get(currentMapping.getCategory());
      if (currentContents == null) {
        currentContents = new ArrayList();
        categories.add(currentMapping.getCategory());
      }
      currentContents.add(currentMapping);
      mappings.put(currentMapping.getCategory(), currentContents);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    if (REEditorCategoryMapping.INSTANCE_QNAME.equals(qName)) {
      String proposalKey = attributes.getValue(REEditorCategoryMapping.PROPOSAL_KEY_ATTRIBUTE_QNAME);
      String category = attributes.getValue(REEditorCategoryMapping.CATEGORY_ATTRIBUTE_QNAME);
      currentMapping = new REEditorCategoryMapping(proposalKey, category);
    }
  }
}