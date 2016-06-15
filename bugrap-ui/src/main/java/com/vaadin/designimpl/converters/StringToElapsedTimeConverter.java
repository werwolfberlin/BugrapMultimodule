package com.vaadin.designimpl.converters;

import com.vaadin.data.util.converter.Converter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class StringToElapsedTimeConverter implements Converter<String, Date> {
  private static final long serialVersionUID = -7626370287602929085L;

  @Override
  public Date convertToModel(final String value, final Class<? extends Date> targetType, final Locale locale) throws ConversionException {
    return null;
  }

  @Override
  public String convertToPresentation(final Date value, final Class<? extends String> targetType, final Locale locale) throws ConversionException {

    final Calendar now = new GregorianCalendar();
    now.setTime(new Date());
    final GregorianCalendar then = new GregorianCalendar();
    then.setTime(value);

    final int years = getTimeDiff(now, then, Calendar.YEAR);
    if (years > 0) {
      return years == 1 ? "1 year ago" : years + " years ago";
    }

    final int months = getTimeDiff(now, then, Calendar.MONTH);
    if (months > 0) {
      return months == 1 ? "1 month ago" : months + " months ago";
    }

    final int weeks = getTimeDiff(now, then, Calendar.WEEK_OF_YEAR);
    if (weeks > 0) {
      return weeks == 1 ? "1 week ago" : weeks + " weeks ago";
    }

    final int days = getTimeDiff(now, then, Calendar.DAY_OF_YEAR);
    if (days > 0) {
      return days == 1 ? "1 day ago" : days + " days ago";
    }

    final int hours = getTimeDiff(now, then, Calendar.HOUR);
    if (hours > 0) {
      return hours == 1 ? "1 hour ago" : hours + " hours ago";
    }

    final int minutes = getTimeDiff(now, then, Calendar.MINUTE);
    if (minutes > 0) {
      return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
    }

    return "a moment ago";
  }

  private int getTimeDiff(final Calendar now, final GregorianCalendar then, final int unit) {
    return now.get(unit) - then.get(unit);
  }

  @Override
  public Class<Date> getModelType() {
    return Date.class;
  }

  @Override
  public Class<String> getPresentationType() {
    return String.class;
  }
}
