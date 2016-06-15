package com.vaadin.designimpl.wrappers;

import org.vaadin.bugrap.domain.entities.Report;

public enum Priority
{
  TRIVIAL ("▌", Report.Priority.TRIVIAL),
  MINOR   ("▌▌", Report.Priority.MINOR),
  NORMAL  ("▌▌▌", Report.Priority.NORMAL),
  MAJOR   ("▌▌▌▌", Report.Priority.MAJOR),
  CRITICAL("▌▌▌▌▌", Report.Priority.CRITICAL),
  BLOCKER ("▌▌▌▌▌▌", Report.Priority.BLOCKER);

  private String          representation;
  private Report.Priority priority;

  Priority(String representation, Report.Priority priority)
  {

    this.representation = representation;
    this.priority = priority;
  }

  public Report.Priority getPriority()
  {
    return priority;
  }

  public static Priority translatePriority(Report.Priority prio)
  {
    return valueOf(prio.name());
  }

  @Override
  public String toString()
  {
    return representation;
  }
}
