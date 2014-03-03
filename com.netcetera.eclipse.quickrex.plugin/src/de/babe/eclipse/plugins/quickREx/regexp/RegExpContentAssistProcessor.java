/*******************************************************************************
 * Copyright (c) 2005, 2007 Bastian Bergerhoff and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Bastian Bergerhoff - initial API and implementation
 *     Georg Sendt - added JRegexp-related implementations
 *******************************************************************************/
package de.babe.eclipse.plugins.quickREx.regexp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.contentassist.IContentAssistSubjectControl;
import org.eclipse.jface.contentassist.ISubjectControlContentAssistProcessor;
import org.eclipse.jface.contentassist.SubjectControlContextInformationValidator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import de.babe.eclipse.plugins.quickREx.QuickRExPlugin;

/**
 * @author bastian.bergerhoff, georg.sendt
 */
public class RegExpContentAssistProcessor implements ISubjectControlContentAssistProcessor {

  /**
   * The available proposals.
   */
  private final static CompletionProposals proposals = new CompletionProposals();

  static {
    initializeProposals();
  }

  private static void initializeProposals() {
    QuickRExPlugin.getDefault().initCompletionProposals(proposals);
  }

  /**
   * The context information validator.
   */
  private IContextInformationValidator fValidator = new SubjectControlContextInformationValidator(this);

  /*
   * @see IContentAssistProcessor#computeCompletionProposals(ITextViewer, int)
   */
  @Override
  public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
    return computeCompletionProposals((IContentAssistSubjectControl)null, documentOffset);
  }

  /*
   * @see IContentAssistProcessor#computeContextInformation(ITextViewer, int)
   */
  @Override
  public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
    return computeContextInformation((IContentAssistSubjectControl)null, documentOffset);
  }

  /*
   * @see IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
   */
  @Override
  public char[] getCompletionProposalAutoActivationCharacters() {
    return new char[] { '\\', '[', '(' };
  }

  /*
   * @see IContentAssistProcessor#getContextInformationAutoActivationCharacters()
   */
  @Override
  public char[] getContextInformationAutoActivationCharacters() {
    return new char[] {};
  }

  /*
   * @see IContentAssistProcessor#getContextInformationValidator()
   */
  @Override
  public IContextInformationValidator getContextInformationValidator() {
    return fValidator;
  }

  /*
   * @see IContentAssistProcessor#getErrorMessage()
   */
  @Override
  public String getErrorMessage() {
    return null;
  }

  /*
   * @see ISubjectControlContentAssistProcessor#computeCompletionProposals(IContentAssistSubjectControl, int)
   */
  @Override
  public ICompletionProposal[] computeCompletionProposals(IContentAssistSubjectControl contentAssistSubjectControl, int documentOffset) {
    Set results = new TreeSet(new RECompletionProposalComparator());
    Iterator iter = proposals.getKeys(MatchSetFactory.JAVA_FLAVOUR).iterator();
    while (iter.hasNext()) {
      addProposal((String)iter.next(), contentAssistSubjectControl, documentOffset, results);
    }

    List proposals = new ArrayList(results.size());

    Iterator propIt = results.iterator();

    while (propIt.hasNext()) {
      RECompletionProposal proposal = (RECompletionProposal)propIt.next();

      String proposalKey = proposal.getKey();
      String displayString = proposal.getDisplayString();
      String additionalInfo = proposal.getAdditionalInfo();
      IContextInformation info = createContextInformation(proposalKey);

      int relativeOffset = proposal.getInsertString().length();

      proposals.add(new CompletionProposal(proposal.getInsertString(), documentOffset, 0, Math.max(0, relativeOffset), null, displayString, info,
          additionalInfo));
    }

    return (ICompletionProposal[])proposals.toArray(new ICompletionProposal[results.size()]);
  }

  /*
   * @see ISubjectControlContentAssistProcessor#computeContextInformation(IContentAssistSubjectControl, int)
   */
  @Override
  public IContextInformation[] computeContextInformation(IContentAssistSubjectControl contentAssistSubjectControl, int documentOffset) {
    return null;
  }

  private void addProposal(String proposalKey, IContentAssistSubjectControl contentAssistSubjectControl, int documentOffset, Set results) {
    RECompletionProposal proposal = proposals.getProposal(MatchSetFactory.JAVA_FLAVOUR, proposalKey);

    try {
      String text = null;
      text = contentAssistSubjectControl.getDocument().get(0, documentOffset);

      proposal.setText(text);

      if (proposal.isMatch()) {
        results.add(proposal);
      }
    } catch (BadLocationException ble) {
      // nop
    }
  }

  private IContextInformation createContextInformation(String proposalKey) {
    return new ContextInformation(null, "contextDisplayString", "informationDisplayString"); //$NON-NLS-1$ //$NON-NLS-2$
  }
}