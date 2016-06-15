package com.vaadin.gerald.listeners;

public interface ValueChangeListener<S, V>
{
  void valueChanged(ValueChangeEvent<S, V> event);
}
