/*
 * Copyright (C) 2015 by Netcetera AG.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of Netcetera AG, Switzerland.
 * The program(s) may be used and/or copied only with the written permission of Netcetera AG or
 * in accordance with the terms and conditions stipulated in the agreement/contract under which 
 * the program(s) have been supplied.
 */
package de.babe.eclipse.plugins.quickREx.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import de.babe.eclipse.plugins.quickREx.regexp.Flag;
import de.babe.eclipse.plugins.quickREx.regexp.RegularExpressionHits;
import de.babe.eclipse.plugins.quickREx.regexp.jdk.CancellableCharSequence;

final class EvaluationJob extends Job {

  private final RegularExpressionHits hits;
  private final Runnable viewUpdateCallback;
  private final Collection<Flag> flags;
  private volatile CancellableCharSequence testText;
  private volatile String regexp;

  public EvaluationJob(RegularExpressionHits hits, Runnable viewUpdateCallback) {
    super("QuickREx Evaluation");

    this.hits = hits;
    this.viewUpdateCallback = viewUpdateCallback;
    this.testText = CancellableCharSequence.wrap("");
    this.regexp = "";
    this.flags = Collections.synchronizedList(new ArrayList<Flag>());
  }

  // This method must not be called within the thread executing this job
  void reset(String testText, String regexp, Collection<Flag> flags) {
    cancel();
    try {
      join();
    } catch (InterruptedException e) {
      // NOP
    }
    this.flags.clear();
    this.flags.addAll(flags);
    this.testText = CancellableCharSequence.wrap(testText);
    this.regexp = regexp;
  }

  @Override
  protected IStatus run(IProgressMonitor monitor) {
    try {
      hits.init(regexp, testText, flags);
    } catch (Throwable throwable) {
      hits.setException(throwable);
    }

    Display.getDefault().syncExec(viewUpdateCallback);

    return Status.OK_STATUS;
  }

  @Override
  protected void canceling() {
    this.testText.cancel();
  }
}