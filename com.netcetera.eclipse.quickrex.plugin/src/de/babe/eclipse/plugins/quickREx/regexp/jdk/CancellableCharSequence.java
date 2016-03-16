package de.babe.eclipse.plugins.quickREx.regexp.jdk;

import java.util.concurrent.CancellationException;

/**
 * A {@link Character} that throws {@link CancellationException}
 * after {@link #cancel()} has been called.
 */
public final class CancellableCharSequence implements CharSequence {

  private CharSequence delegate;
  private volatile boolean canceled;

  private CancellableCharSequence(CharSequence delegate) {
    this.delegate = delegate;
    this.canceled = false;
  }

  /**
   * Factory method.
   *
   * @param charSequence the char sequence to wrap
   * @return the wrapped char sequence
   */
  public static CancellableCharSequence wrap(CharSequence charSequence) {
    return new CancellableCharSequence(charSequence);
  }

  /**
   * Marks this object as cancelled. After this all interface methods throw
   * a {@link CancellationException}.
   */
  public void cancel() {
    this.canceled = true;
  }

  @Override
  public int length() {
    assertNotCanceled();
    return delegate.length();
  }

  @Override
  public char charAt(int index) {
    assertNotCanceled();
    return delegate.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    assertNotCanceled();
    return delegate.subSequence(start, end);
  }

  @Override
  public String toString() {
    assertNotCanceled();
    return delegate.toString();
  }

  private void assertNotCanceled() {
    if (canceled) {
      throw new CancellationException("Operation canceled");
    }
  }
}
