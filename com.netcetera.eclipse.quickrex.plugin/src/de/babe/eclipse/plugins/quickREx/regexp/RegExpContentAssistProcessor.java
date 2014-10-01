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
   * The available PROPOSALS.
   */
  private static final CompletionProposals PROPOSALS = new CompletionProposals();

  static {
    initializeProposals();
  }

  private static void initializeProposals() {
    QuickRExPlugin.getDefault().initCompletionProposals(PROPOSALS);
  }

  /**
   * The context information validator.
   */
  private IContextInformationValidator fValidator = new SubjectControlContextInformationValidator(this);

  @Override
  public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
    return computeCompletionProposals((IContentAssistSubjectControl) null, documentOffset);
  }

  @Override
  public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
    return computeContextInformation((IContentAssistSubjectControl) null, documentOffset);
  }

  @Override
  public char[] getCompletionProposalAutoActivationCharacters() {
    return new char[] { '\\', '[', '(' };
  }

  @Override
  public char[] getContextInformationAutoActivationCharacters() {
    return new char[] {};
  }

  @Override
  public IContextInformationValidator getContextInformationValidator() {
    return fValidator;
  }

  @Override
  public String getErrorMessage() {
    return null;
  }

  @Override
  public ICompletionProposal[] computeCompletionProposals(IContentAssistSubjectControl contentAssistSubjectControl, int documentOffset) {
    Set<RECompletionProposal> results = new TreeSet<>(new RECompletionProposalComparator());
    for (String proposalKey : PROPOSALS.getKeys()) {
      addProposal(proposalKey, contentAssistSubjectControl, documentOffset, results);
    }

    List<CompletionProposal> proposals = new ArrayList<>(results.size());

    for (RECompletionProposal proposal : results) {

      String displayString = proposal.getDisplayString();
      String additionalInfo = proposal.getAdditionalInfo();
      IContextInformation info = createContextInformation();

      int relativeOffset = proposal.getInsertString().length();

      CompletionProposal completionProposal = new CompletionProposal(
          proposal.getInsertString(), documentOffset, 0, Math.max(0, relativeOffset), null, displayString, info, additionalInfo);
      proposals.add(completionProposal);
    }

    return proposals.toArray(new ICompletionProposal[results.size()]);
  }

  @Override
  public IContextInformation[] computeContextInformation(IContentAssistSubjectControl contentAssistSubjectControl, int documentOffset) {
    return null;
  }

  private void addProposal(String proposalKey, IContentAssistSubjectControl contentAssistSubjectControl, int documentOffset, Set<RECompletionProposal> results) {
    RECompletionProposal proposal = PROPOSALS.getProposal(proposalKey);

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

  private IContextInformation createContextInformation() {
    return new ContextInformation(null, "contextDisplayString", "informationDisplayString"); //$NON-NLS-1$ //$NON-NLS-2$
  }
}