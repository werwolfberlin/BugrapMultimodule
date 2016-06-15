package com.vaadin.designimpl;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.design.ReportsOverviewDesign;
import com.vaadin.designimpl.wrappers.OverviewTableWrapper;
import com.vaadin.designimpl.wrappers.ReportDetailsWindow;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.gerald.ButtonGroup;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.*;


public class ReportsOverview extends ReportsOverviewDesign
{
  private static final long serialVersionUID = -8122886448258730412L;

  private static final String STATUS = "status";
  private static final String ASSIGNED = "assigned";
  private static final String VERSION = "version";

  private static final ProjectVersion ALL_VERSIONS_PROJECT_VERSION = new ProjectVersion();

  private static final AbstractProperty EMPTY_PROPERTY = new AbstractProperty() {
    private static final long serialVersionUID = 1297345636031493095L;
    @Override public Object getValue() { return new HashSet<>(); }
    @Override public void setValue(final Object newValue) throws ReadOnlyException { }
    @Override public Class getType() { return null; }
  };

  static {
    ALL_VERSIONS_PROJECT_VERSION.setVersion("All Versions");
  }

  private final ReportDetails reportDetails = new ReportDetails(false);
  private final SplitPanelListener splitPanelListener = new SplitPanelListener();

  private BugrapRepository           repository;
  private final OverviewTableWrapper artifactTableWrapper;
  private final PopupView            popup;
  private final CustomOptionPanel    customOptionPanel;
  private final ButtonGroup          buttonGroupOptions;
  private final ButtonGroup          buttonGroupAssignees;

  public ReportsOverview(final BugrapRepository repository) {
    super();

    this.repository = repository;

    final Set<Reporter> reporters = repository.findReporters();
    for (Reporter reporter : reporters)
    {
      System.out.println(reporter);
      if(reporter.getName().contains("manager"))
      {
        getSelectionCache().setUser(reporter);
      }
    }

    artifactTableWrapper = new OverviewTableWrapper(artifactTable);
    artifactTable.addItemClickListener((ItemClickEvent.ItemClickListener) event -> {
      if(event.isDoubleClick() && event.getButton() == MouseEventDetails.MouseButton.LEFT)
      {
        final Report reports = (Report)event.getItemId();
        final ReportDetailsWindow window = new ReportDetailsWindow(Collections.singleton(reports), getProject(), repository);
        window.addCloseListener(e -> updateArtifacts());
        window.show();
      }
    });

    reportDetails.setRepository(repository);
    reportDetails.addStyleName("top-line");
    reportDetails.addUpdateListener((Button.ClickListener) event -> updateArtifacts());
    splitPanel.addComponent(reportDetails);

    userButton.setCaption(getSelectionCache().getUser().getName());
    customOptionPanel = new CustomOptionPanel();
    customOptionPanel.addValueChangedListener(event -> {
      getSelectionCache().setStatus(customOptionPanel.getStatus());
      updateArtifacts();
    });

    buttonGroupOptions = new ButtonGroup(buttonStatusOpen, buttonStatusAll, buttonStatusCustom);
    popup = new PopupView(null, customOptionPanel);
    buttonStatusCustom.addClickListener((Button.ClickListener) event -> { if(event.getRelativeX() > 60) popup.setPopupVisible(true); });

    buttonGroupAssignees = new ButtonGroup(buttonAssigneesOnlyMe, buttonAssigneesEveryone);

    projectsComboBox.addStyleName("large-font");
    projectsComboBox.addStyleName("bold-font");
    updateProjects();
    setupProjectsComboBox();

    versionsComboBox.addStyleName("rounded-corners-left");
    versionsComboBox.addStyleName("rounded-corners-right");
    updateProjectVersions();
    setupProjectVersionsComboBox();

////    logoutButton;
////    reportBugButton;
////    requestFeatureButton;
////    manageProjectButton;
////    searchTextField;

    setupAssigneeOptions();
    setupStatusOptions();

    updateArtifacts();
    artifactTable.addValueChangeListener((Property.ValueChangeListener) this::updateEditPanel);
    splitPanel.addSplitPositionChangeListener(splitPanelListener);

//    artifactTable.addListener(new Listener() {
//      @Override
//      public void componentEvent(Event event)
//      {
//        System.out.println(event);
//      }
//    });

    artifactTable.addShortcutListener(new ShortcutListener("enter", ShortcutAction.KeyCode.ENTER, null) {
      @Override
      public void handleAction(Object sender, Object target)
      {
        if(target instanceof Table)
        {
          final Object value = ((Table) target).getValue();
          if(value instanceof Collection && ((Collection) value).size() == 1)
          {
            final Report              report   = (Report) ((Collection) value).iterator().next();
            final ReportDetailsWindow window = new ReportDetailsWindow(Collections.singleton(report), getProject(), repository);
            window.show();
          }
        }
        else
        {
          reportDetails.onEnterTyped();
        }
      }
    });
  }

  private void updateSplitPosition(final AbstractSplitPanel.SplitPositionChangeEvent event) {
    final float splitPosition = event.getSplitPosition();
    final Unit splitPositionUnit = event.getSplitPositionUnit();
    getSelectionCache().setSplitPosition(splitPosition, splitPositionUnit, splitPanel.isSplitPositionReversed());
  }

  private void setupStatusOptions() {
    optionsLayout.addComponent(popup);

    buttonGroupOptions.setSelectedButton(getSelectedStatus());
    buttonGroupOptions.addValueChangedListener(event -> {
      getSelectionCache().setStatus(getStatus());
      popup.setEnabled(event.getNewValue() == buttonStatusCustom);
      updateArtifacts();
    });

    popup.setEnabled(buttonGroupOptions.getSelectedButton() == buttonStatusCustom);
    updateEditPanel((Property.ValueChangeEvent) () -> EMPTY_PROPERTY);
  }

  private Button getSelectedStatus() {
    final Set<Report.Status> status = getSelectionCache().getStatus(Collections.singleton(Report.Status.OPEN));
    if(status.size() == 1 && status.iterator().next() == Report.Status.OPEN)
    {
      return buttonStatusOpen;
    }

    else if (status.size() == Report.Status.values().length)
    {
      return buttonStatusAll;
    }

    return buttonStatusCustom;
  }

  private void setupAssigneeOptions() {
    final Collection<Button> assignees = buttonGroupAssignees.getButtons();
    final Button defaultAssignee = assignees == null || assignees.isEmpty() ? buttonAssigneesOnlyMe : buttonGroupAssignees.getButtons().iterator().next();
    buttonGroupAssignees.setSelectedButton(getSelectionCache().getAssignee(defaultAssignee));
    buttonGroupAssignees.addValueChangedListener(event ->
    {
      getSelectionCache().setAssignee(event.getNewValue());
      updateArtifacts();
    });
  }

  private void setupProjectVersionsComboBox() {
    versionsComboBox.setNullSelectionAllowed(false);
    versionsComboBox.setImmediate(true);
    versionsComboBox.addValueChangeListener((Property.ValueChangeListener) event ->
    {
      getSelectionCache().setSelectedProjectVersion(getProject(), getProjectVersion());
      updateArtifacts();

      if(event.getProperty().getValue() == ALL_VERSIONS_PROJECT_VERSION)
      {
        artifactTable.sort(new Object[]{"version", "priority"}, new boolean[]{true, false});
      }
    });
  }

  private void setupProjectsComboBox() {
    projectsComboBox.setNullSelectionAllowed(false);
    projectsComboBox.setFilteringMode(FilteringMode.CONTAINS);
    projectsComboBox.setImmediate(true);
    projectsComboBox.addValueChangeListener((Property.ValueChangeListener) event ->
    {
      getSelectionCache().setProject(getProject());
      updateProjectVersions();
    });
  }

  private ReportsOverviewSelection getSelectionCache() { return ReportsOverviewSelection.getSelectionCache(); }
  private Project getProject() { return (Project) projectsComboBox.getValue(); }
  private ProjectVersion getProjectVersion() { return (ProjectVersion) versionsComboBox.getValue(); }

  private Set<Report.Status> getStatus() {
    final Button value = buttonGroupOptions.getSelectedButton();
    if(buttonStatusOpen == value)
    {
      return EnumSet.of(Report.Status.OPEN);
    }
    else if (buttonStatusAll == value)
    {
      return EnumSet.allOf(Report.Status.class);
    }
    return customOptionPanel.getStatus();
  }

  private Reporter getAssignee() {
    final Button value = buttonGroupAssignees.getSelectedButton();
    return value == null || buttonAssigneesEveryone == value ? null : getSelectionCache().getUser();
  }

  private void updateEditPanel(final Property.ValueChangeEvent event) {
    final Set values = (Set)artifactTable.getValue();

    if(values == null || values.isEmpty())
    {
      setSplitPanelPosition(100f, Unit.PERCENTAGE, false, true);
      splitPanel.setLocked(true);
    }

    else if(values.size() > 1)
    {
      reportDetails.setContext(values, getProject());
      setSplitPanelPosition(120f, Unit.PIXELS, true, true);
      splitPanel.setLocked(true);
    }

    else
    {
      reportDetails.setContext(values, getProject());

      final ReportsOverviewSelection.SplitPanelPosition splitPanelPosition = getSelectionCache().getSplitPanelPosition();
      setSplitPanelPosition(splitPanelPosition.isValid() ? splitPanelPosition
                                                         : new ReportsOverviewSelection.SplitPanelPosition(350f, Unit.PIXELS, true), false);
      splitPanel.setLocked(false);
    }
  }

  private void setSplitPanelPosition(ReportsOverviewSelection.SplitPanelPosition splitPanelPosition, final boolean muted)
  {
    setSplitPanelPosition(splitPanelPosition.getSplitPosition(), splitPanelPosition.getSplitPositionUnit(), splitPanelPosition.isReverse(), muted);
  }

  private void setSplitPanelPosition(float splitPosition, Unit splitPositionUnit, boolean reverse, final boolean muted)
  {
    if(muted) splitPanel.removeSplitPositionChangeListener(splitPanelListener);
    splitPanel.setSplitPosition(splitPosition, splitPositionUnit, reverse);
    if(muted) splitPanel.addSplitPositionChangeListener(splitPanelListener);
  }

  private void updateArtifacts() {
    final Set<Report.Status> status = getStatus();
    final Reporter assignee = getAssignee();
    final Project project = getProject();
    final ProjectVersion projectVersion = getProjectVersion();

    // create a query for the reports
    final BugrapRepository.ReportsQuery query = new BugrapRepository.ReportsQuery();
    query.project = project;
    query.projectVersion = projectVersion == ALL_VERSIONS_PROJECT_VERSION ? null : projectVersion;
    query.reportAssignee = assignee;
    query.reportStatuses = status;
    final Set<Report> reports = repository.findReports(query);

    //adjust visible fields depending on selected options
    artifactTable.setColumnCollapsed(VERSION, projectVersion != ALL_VERSIONS_PROJECT_VERSION);
    artifactTable.setColumnCollapsed(STATUS, status.size() == 1);
    artifactTable.setColumnCollapsed(ASSIGNED, assignee != null);

    //set the reports result
    artifactTableWrapper.setRows(reports);
  }

  private void updateProjectVersions() {
    final Set<ProjectVersion> projectVersions = new TreeSet<>();
    final Project project = getProject();
    projectVersions.addAll(repository.findProjectVersions(project));
    versionsComboBox.removeAllItems();
    if(projectVersions.size() != 1)
    {
      versionsComboBox.addItem(ALL_VERSIONS_PROJECT_VERSION);
    }
    versionsComboBox.addItems(projectVersions);

    versionsComboBox.setValue(getSelectionCache().getSelectedProjectVersion(projectVersions, project, ALL_VERSIONS_PROJECT_VERSION));

    if(versionsComboBox.getValue() == ALL_VERSIONS_PROJECT_VERSION)
    {
      artifactTable.sort(new Object[]{"version", "priority"}, new boolean[]{true, false});
    }

    updateArtifacts();
  }

  private void updateProjects() {
    final Reporter user = getSelectionCache().getUser();

    final Set<Project> repoProjects = repository.findProjects();

    final Set<Project> projects = new TreeSet<>();
    projects.addAll(repoProjects);
    for (Project project : repoProjects)
    {
      if(user.isAdmin() || project.getManager().equals(user) || project.getDevelopers().contains(user))
      {
        projects.add(project);
      }
    }

    projectsComboBox.removeAllItems();
    projectsComboBox.addItems(projects);


    projectsComboBox.setValue(getSelectionCache().getSelectedProject(projects));
  }

  private class SplitPanelListener implements AbstractSplitPanel.SplitPositionChangeListener
  {
    @Override
    public void onSplitPositionChanged(AbstractSplitPanel.SplitPositionChangeEvent event)
    {
      ReportsOverview.this.updateSplitPosition(event);
    }
  }
}
