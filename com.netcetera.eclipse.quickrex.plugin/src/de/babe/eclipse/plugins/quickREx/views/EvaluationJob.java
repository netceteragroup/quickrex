package de.babe.eclipse.plugins.quickREx.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import de.babe.eclipse.plugins.quickREx.Messages;
import de.babe.eclipse.plugins.quickREx.regexp.Flag;
import de.babe.eclipse.plugins.quickREx.regexp.RegularExpressionHits;
import de.babe.eclipse.plugins.quickREx.regexp.jdk.CancellableCharSequence;

/**
 * A {@link Job} that evaluates a regular expression.
 */
final class EvaluationJob extends Job {

  private final RegularExpressionHits hits;
  private final Runnable viewUpdateCallback;
  private final Collection<Flag> flags;
  private volatile CancellableCharSequence testText;
  private volatile String regexp;

  EvaluationJob(RegularExpressionHits hits, Runnable viewUpdateCallback) {
    super(Messages.getString("views.QuickRExView.evaluationJob.name"));

    this.hits = hits;
    this.viewUpdateCallback = viewUpdateCallback;
    this.testText = CancellableCharSequence.wrap("");
    this.regexp = "";
    this.flags = Collections.synchronizedList(new ArrayList<Flag>());
  }

  // This method must not be called within the thread executing this job
  void reset(String testText, String regexp, Collection<Flag> flags) {
    this.flags.clear();
    this.flags.addAll(flags);
    this.testText = CancellableCharSequence.wrap(testText);
    this.regexp = regexp;
  }

  @Override
  protected IStatus run(IProgressMonitor monitor) {
    hits.init(regexp, testText, flags);
    Display.getDefault().syncExec(viewUpdateCallback);

    return Status.OK_STATUS;
  }

  @Override
  protected void canceling() {
    this.testText.cancel();
  }
}