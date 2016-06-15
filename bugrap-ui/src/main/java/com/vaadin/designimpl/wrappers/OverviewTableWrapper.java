package com.vaadin.designimpl.wrappers;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.designimpl.converters.PriorityStringConverter;
import com.vaadin.designimpl.converters.StringToElapsedTimeConverter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Table;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.*;

public class OverviewTableWrapper{

  private static final String STATUS = "status";
  private static final String ASSIGNED = "assigned";
  private static final List<String> VISIBLE_FIELDS = Lists.newArrayList("version", "priority", STATUS, "type", "summary", ASSIGNED, "timestamp", "reportedTimestamp");
  private final BeanItemContainer<Report> tableDataSource = new BeanItemContainer<>(Report.class);
  private Table table;

  public OverviewTableWrapper(final Table table) {
    this.table = table;
    table.setContainerDataSource(tableDataSource);
    table.setColumnCollapsingAllowed(true);

    table.setColumnHeader("version", "VERSION");
    table.setColumnHeader("priority", "PRIORITY");
    table.setColumnHeader(STATUS, "STATUS");
    table.setColumnHeader("type", "TYPE");
    table.setColumnHeader("summary", "SUMMARY");
    table.setColumnHeader(ASSIGNED, "ASSIGNED TO");
    table.setColumnHeader("timestamp", "LAST MODIFIED");
    table.setColumnHeader("reportedTimestamp", "REPORTED");

    table.setVisibleColumns(VISIBLE_FIELDS.toArray());
    table.sort(new Object[]{"priority"}, new boolean[]{false});

    table.setImmediate(true);
    table.setSelectable(true);
    table.setMultiSelect(true);
    table.setMultiSelectMode(MultiSelectMode.SIMPLE);
    table.setConverter("priority", new PriorityStringConverter());
    table.setConverter("timestamp", new StringToElapsedTimeConverter());
    table.setConverter("reportedTimestamp", new StringToElapsedTimeConverter());
  }

  public void setRows(final Set<Report> reports) {
    tableDataSource.removeAllItems();
    tableDataSource.addAll(reports);
    table.sort();
  }

}
