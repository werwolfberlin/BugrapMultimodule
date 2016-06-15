package com.vaadin.designimpl;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.design.CustomOptionPanelDesign;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

class CustomOptionPanel extends CustomOptionPanelDesign
{
  private static final long serialVersionUID = -3472725862749249463L;
  private final Set<Property.ValueChangeListener> listeners =  Collections.synchronizedSet(new LinkedHashSet<>());

  CustomOptionPanel()
  {
    statusGroup.setContainerDataSource(new BeanItemContainer<>(Report.Status.class, Arrays.asList(Report.Status.values())));
    statusGroup.setMultiSelect(true);
    statusGroup.addValueChangeListener(this::fireValueChangedEvent);
  }

  void addValueChangedListener(final Property.ValueChangeListener listener)
  {
    listeners.add(listener);
  }

  public void removeValueChangedListener(final Property.ValueChangeListener listener)
  {
    listeners.remove(listener);
  }

  private void fireValueChangedEvent(final Property.ValueChangeEvent event)
  {
    for (final Property.ValueChangeListener listener : listeners) {
      listener.valueChange(event);
    }
  }

  public Set<Report.Status> getStatus()
  {
    //noinspection unchecked
    return (Set<Report.Status>) statusGroup.getValue();
  }
}
