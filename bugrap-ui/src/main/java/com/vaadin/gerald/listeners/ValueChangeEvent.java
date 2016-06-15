package com.vaadin.gerald.listeners;

public class ValueChangeEvent<S, V>
{
  private final S source;
  private final V oldValue;
  private final V newValue;

  public ValueChangeEvent(final S source, final V oldValue, final V newValue)
  {
    this.source = source;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public S getSource()
  {
    return source;
  }

  public V getOldValue()
  {
    return oldValue;
  }

  public V getNewValue()
  {
    return newValue;
  }
}
