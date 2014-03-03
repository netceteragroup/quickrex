/*******************************************************************************
 * Copyright (c) 2005, 2007 Bastian Bergerhoff and others
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
public class CompletionProposalXMLHandler extends DefaultHandler {

  private final HashMap proposals;

  private final ArrayList keys;

  private RECompletionProposal currentProposal;

  private CompletionTriggerExpression currentTriggerExpression;

  private CompletionTriggerWord currentTriggerWord;

  /**
   * @param p_proposals
   * @param p_keys
   */
  public CompletionProposalXMLHandler(HashMap p_proposals, ArrayList p_keys) {
    this.proposals = p_proposals;
    this.keys = p_keys;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  public void endElement(String uri, String localName, String qName) {
    if (CompletionTriggerExpression.INSTANCE_QNAME.equals(qName)) {
      currentProposal.addTriggerExpression(currentTriggerExpression.getRegExp(), currentTriggerExpression.getProposal());
      currentTriggerExpression = null;
    } else if (CompletionTriggerWord.INSTANCE_QNAME.equals(qName)) {
      currentProposal.addTriggerWord(currentTriggerWord.getWord(), currentTriggerWord.getExtension());
      currentTriggerWord = null;
    } else if (RECompletionProposal.INSTANCE_QNAME.equals(qName)) {
      proposals.put(currentProposal.getKey(), currentProposal);
      keys.add(currentProposal.getKey());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    if (RECompletionProposal.INSTANCE_QNAME.equals(qName)) {
      String key = attributes.getValue(RECompletionProposal.KEY_ATTRIBUTE_QNAME);
      String plainProposal = attributes.getValue(RECompletionProposal.PLAIN_VALUE_ATTRIBUTE_QNAME);
      boolean allowPlain = "true".equals(attributes.getValue(RECompletionProposal.ALLOW_PLAIN_ATTRIBUTE_QNAME)); //$NON-NLS-1$
      currentProposal = new RECompletionProposal(key, plainProposal, allowPlain);
      currentProposal.setDisplayString(attributes.getValue(RECompletionProposal.DISPLAY_STRING_ATTRIBUTE_QNAME));
      currentProposal.setAdditionalInfo(attributes.getValue(RECompletionProposal.ADD_INFO_ATTRIBUTE_QNAME));
    } else if (CompletionTriggerExpression.INSTANCE_QNAME.equals(qName)) {
      String regExp = attributes.getValue(CompletionTriggerExpression.REG_EXP_ATTRIBUTE_QNAME);
      String proposal = attributes.getValue(CompletionTriggerExpression.COMPLETION_VALUE_ATTRIBUTE_QNAME);
      currentTriggerExpression = new CompletionTriggerExpression(regExp, proposal, currentProposal.getPlainProposal());
    } else if (CompletionTriggerWord.INSTANCE_QNAME.equals(qName)) {
      String word = attributes.getValue(CompletionTriggerWord.WORD_ATTRIBUTE_QNAME);
      String extension = attributes.getValue(CompletionTriggerWord.COMPLETION_VALUE_ATTRIBUTE_QNAME);
      currentTriggerWord = new CompletionTriggerWord(word, extension, currentProposal.getPlainProposal());
    }
  }
}