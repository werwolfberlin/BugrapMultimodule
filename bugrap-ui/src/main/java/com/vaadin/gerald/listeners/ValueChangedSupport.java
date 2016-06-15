package com.vaadin.gerald.listeners;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ValueChangedSupport<S, V>
{
  private final Set<ValueChangeListener<S, V>> listeners = Collections.synchronizedSet(new LinkedHashSet<>());

  public void addValueChangedListener(final ValueChangeListener<S, V> listener)    { listeners.add(listener); }

  public void removeValueChangedListener(final ValueChangeListener<S, V> listener) { listeners.remove(listener); }

  protected synchronized void fireValueChanged(S source, V oldValue, V value)
  {
    final ValueChangeEvent<S, V> event = new ValueChangeEvent<>(source, oldValue, value);

    for (ValueChangeListener<S, V> listener : listeners)
    {
      listener.valueChanged(event);
    }
  }
}
