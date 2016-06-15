package com.vaadin.designimpl.converters;

import com.vaadin.data.util.converter.Converter;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.Locale;

public class PriorityStringConverter implements Converter<String, Report.Priority> {

  private static final long serialVersionUID = 7576587504674391794L;
  private static final char COUNTER_CHARACTER = '▌';

  @Override
  public Report.Priority convertToModel(final String value, final Class<? extends Report.Priority> targetType, final Locale locale) throws ConversionException {
    if(value == null)
    {
      return null;
    }

    final int ordinal = value.length() - 1;
    for (final Report.Priority priority : Report.Priority.values()) {
      if (priority.ordinal() == ordinal) {
        return priority;
      }
    }
    return null;
  }

  @Override
  public String convertToPresentation(final Report.Priority value, final Class<? extends String> targetType, final Locale locale) throws ConversionException {
    if(value == null)
    {
      return null;
    }

    final StringBuilder stringBuilder = new StringBuilder();

    for (int i = 0; i <= value.ordinal(); i++) {
      stringBuilder.append(COUNTER_CHARACTER);
    }

    return stringBuilder.toString();
  }

  @Override public Class<Report.Priority> getModelType() { return Report.Priority.class; }
  @Override public Class<String> getPresentationType() {
    return String.class;
  }
}
