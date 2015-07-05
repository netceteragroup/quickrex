package de.babe.eclipse.plugins.quickREx.regexp.jdk;

import java.util.concurrent.CancellationException;

public final class CancellableCharSequence implements CharSequence {

  private CharSequence delegate;
  private volatile boolean canceled;

  private CancellableCharSequence(CharSequence delegate) {
    this.delegate = delegate;
    this.canceled = false;
  }

  public static CancellableCharSequence wrap(CharSequence charSequence) {
    return new CancellableCharSequence(charSequence);
  }

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

  private void assertNotCanceled() {
    if (canceled) {
      throw new CancellationException("Operation canceled");
    }
  }
}
