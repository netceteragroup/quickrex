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
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author bastian.bergerhoff
 */
public class EditorCategoryMappingXMLHandler extends DefaultHandler {

  private final Map<String, List<REEditorCategoryMapping>> mappings;

  private final List<String> categories;

  private REEditorCategoryMapping currentMapping;

  /**
   * The constructor.
   *
   * @param categoryMappings
   * @param categories
   */
  public EditorCategoryMappingXMLHandler(Map<String, List<REEditorCategoryMapping>> categoryMappings, List<String> categories) {
    this.mappings = categoryMappings;
    this.categories = categories;
  }

  @Override
  public void endElement(String uri, String localName, String qName) {
    if (REEditorCategoryMapping.INSTANCE_QNAME.equals(qName)) {
      List<REEditorCategoryMapping> currentContents = mappings.get(currentMapping.getCategory());
      if (currentContents == null) {
        currentContents = new ArrayList<>();
        categories.add(currentMapping.getCategory());
      }
      currentContents.add(currentMapping);
      mappings.put(currentMapping.getCategory(), currentContents);
    }
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    if (REEditorCategoryMapping.INSTANCE_QNAME.equals(qName)) {
      String proposalKey = attributes.getValue(REEditorCategoryMapping.PROPOSAL_KEY_ATTRIBUTE_QNAME);
      String category = attributes.getValue(REEditorCategoryMapping.CATEGORY_ATTRIBUTE_QNAME);
      currentMapping = new REEditorCategoryMapping(proposalKey, category);
    }
  }
}