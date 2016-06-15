package com.vaadin.designimpl;

import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.io.Serializable;
import java.util.*;

class ReportsOverviewSelection implements Serializable {
  private static final long serialVersionUID = -3706787855419120506L;

  private Reporter user;
  private Project project;
  private Map<Project, ProjectVersion> version = new HashMap<>();
  private Button assignee;
  private Set<Report.Status> status = new HashSet<>();
  private SplitPanelPosition splitPanelPosition = new SplitPanelPosition();

  Reporter getUser() { return user; }
  Project getProject() { return project; }
  Map<Project, ProjectVersion> getVersion() { return version; }
  Button getAssignee(final Button defaultValue) { return assignee == null ? defaultValue : assignee; }
  Set<Report.Status> getStatus(final Set<Report.Status> defaultValue) { return status.isEmpty() ? defaultValue : status; }
  SplitPanelPosition getSplitPanelPosition(){return splitPanelPosition;}

  void setUser(final Reporter user) { this.user = user; }
  void setProject(final Project project) { this.project = project; }
  void setVersion(final Map<Project, ProjectVersion> version) { this.version = version; }
  void setAssignee(final Button assignee) { this.assignee = assignee; }
  void setStatus(final Set<Report.Status> status) { this.status.clear(); this.status.addAll(status); }
  void setSplitPosition(final Float splitPosition, final Sizeable.Unit splitPositionUnit, final boolean isReverse)
  {
    splitPanelPosition.setSplitPosition(splitPosition);
    splitPanelPosition.setSplitPositionUnit(splitPositionUnit);
    splitPanelPosition.setReverse(isReverse);
  }


  static ReportsOverviewSelection getSelectionCache() {
    final Object selectionCache = VaadinSession.getCurrent().getAttribute("selectionCache");
    if (selectionCache != null) {
      //noinspection unchecked
      return (ReportsOverviewSelection) selectionCache;
    }

    final ReportsOverviewSelection selCache = new ReportsOverviewSelection();
    VaadinSession.getCurrent().setAttribute("selectionCache", selCache);
    return selCache;
  }

  Project getSelectedProject(final Set<Project> projects)
  {
    if(projects == null)
    {
      return null;
    }

    final Project selectedProject = getProject();
    if(selectedProject == null)
    {
      return projects.isEmpty() ? null : projects.iterator().next();
    }

    for (final Project project : projects) {
      if(selectedProject.compareTo(project) == 0)
      {
        return project;
      }
    }

    return projects.isEmpty() ? null : projects.iterator().next();
  }

  ProjectVersion getSelectedProjectVersion(final Set<ProjectVersion> projectVersions,
                                           final Project project,
                                           final ProjectVersion defaultValue)
  {
    final Map<Project, ProjectVersion> selectionCache = getSelectionCache().getVersion();
    final ProjectVersion selectedProjectVersion = selectionCache.get(project);
    if(selectedProjectVersion == null)
    {
      return defaultValue;
    }

    for (final ProjectVersion projectVersion : projectVersions) {
      if(projectVersion.compareTo(selectedProjectVersion) == 0)
      {
        return projectVersion;
      }
    }

    return defaultValue;
  }

  void setSelectedProjectVersion(final Project project, final ProjectVersion projectVersion)
  {
    if(projectVersion != null)
    {
      version.put(project, projectVersion);
    }
  }

  static class SplitPanelPosition implements Serializable
  {
    public SplitPanelPosition()
    {
    }

    public SplitPanelPosition(Float splitPosition, Sizeable.Unit splitPositionUnit, boolean isReverse)
    {
      this.splitPosition = splitPosition;
      this.splitPositionUnit = splitPositionUnit;
      this.isReverse = isReverse;
    }

    private Float         splitPosition     = null;
    private Sizeable.Unit splitPositionUnit = Sizeable.Unit.PIXELS;
    private boolean       isReverse         =true;

    public boolean isValid()
    {
      return splitPosition != null && splitPositionUnit != null;
    }

    public Float getSplitPosition()
    {
      return splitPosition;
    }

    public void setSplitPosition(Float splitPosition)
    {
      this.splitPosition = splitPosition;
    }

    public Sizeable.Unit getSplitPositionUnit()
    {
      return splitPositionUnit;
    }

    public void setSplitPositionUnit(Sizeable.Unit splitPositionUnit)
    {
      this.splitPositionUnit = splitPositionUnit;
    }

    public boolean isReverse()
    {
      return isReverse;
    }

    public void setReverse(boolean reverse)
    {
      isReverse = reverse;
    }
  }
}
