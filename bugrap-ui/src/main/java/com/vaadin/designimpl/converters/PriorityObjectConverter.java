package com.vaadin.designimpl.converters;

import com.vaadin.data.util.converter.Converter;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.Locale;

public class PriorityObjectConverter implements Converter<Object, Report.Priority> {
  private static final long serialVersionUID = -3963674278755968016L;
  private static final PriorityStringConverter priorityStringConverter = new PriorityStringConverter();

  @Override
  public Report.Priority convertToModel(final Object value, final Class<? extends Report.Priority> targetType, final Locale locale) throws ConversionException {
    return priorityStringConverter.convertToModel(value == null ? null : value.toString(), targetType, locale);
  }

  @Override
  public Object convertToPresentation(final Report.Priority value, final Class<?> targetType, final Locale locale) throws ConversionException {
    return priorityStringConverter.convertToPresentation(value, String.class, locale);
  }

  @Override
  public Class<Report.Priority> getModelType() {
    return Report.Priority.class;
  }

  @Override
  public Class<Object> getPresentationType() {
    return Object.class;
  }
}
