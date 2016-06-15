package com.vaadin.designimpl;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.design.ReportDetailsDesign;
import com.vaadin.designimpl.wrappers.Priority;
import com.vaadin.designimpl.wrappers.ReportDetailsWindow;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ClientConnector;
import com.vaadin.ui.*;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.*;

import java.io.OutputStream;
import java.util.*;

import static com.vaadin.designimpl.ReportsOverviewSelection.getSelectionCache;

public class ReportDetails extends ReportDetailsDesign {

  private static final long serialVersionUID = 5237059636913498559L;

  private BugrapRepository repository;
  private final Set<Report> reports = new LinkedHashSet<>();
  private Project project;

  public ReportDetails(final boolean isWindow) {
    priorityComboBox.setContainerDataSource(new BeanItemContainer<>(Priority.class, Arrays.asList(Priority.values())));
    typeComboBox.setContainerDataSource(new BeanItemContainer<>(Report.Type.class, Arrays.asList(Report.Type.values())));
    statusComboBox.setContainerDataSource(new BeanItemContainer<>(Report.Status.class, Arrays.asList(Report.Status.values())));

    commentSection.addStyleName("greyBackground");
    controlsContainer.addStyleName("greyBackground");
    controlsContainer.addStyleName("five-pixel-padding");

    artefactSummaryExtension.addStyleName("grey-font");

    detailsTextArea.setWordwrap(true);
    detailsTextArea.setRows(10);

    //settings for windowed mode
    detailsSplitPanel.setSplitPosition(isWindow ? 50 : 100, Unit.PERCENTAGE, false);
    detailsSplitPanel.setLocked(!isWindow);
    commentSection.setVisible(isWindow);

    //attach listeners
    revertButton.addClickListener(event -> setContext(new HashSet<>(reports), project));
    updateButton.addClickListener(event -> commitData());
    openInWindowButton.addClickListener(event -> new ReportDetailsWindow(reports, project, repository).show());

    updateButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

    attachmentsBar.addComponentAttachListener(event -> attachmentsBar.setVisible(attachmentsBar.getComponentCount() > 0));
    attachmentsBar.addComponentDetachListener(event -> attachmentsBar.setVisible(attachmentsBar.getComponentCount() > 0));

    doneButton.addClickListener((Button.ClickListener) event ->
    {
      final Report  report  = reports.iterator().next();
      saveTextComment(report, ReportDetails.this.commentTextArea.getValue());

      final Iterable<? extends ClientConnector> allChildrenIterable = getAllChildrenIterable(attachmentsBar);
      for (ClientConnector clientConnector : allChildrenIterable)
      {
        if(clientConnector instanceof Upload)
        {
          final Upload upload = (Upload) clientConnector;
          saveAttachmentComment(report, upload.getCaption(), (byte[]) upload.getData());
        }
      }
    });

    attachmentButton.addClickListener(new Button.ClickListener() {
      @Override
      public void buttonClick(Button.ClickEvent event)
      {
        new Upload("New attachment...", new Upload.Receiver() {
          @Override
          public OutputStream receiveUpload(String filename, String mimeType)
          {
            return null;
          }
        });
      }
    });
    cancelButton.addClickListener((Button.ClickListener) event -> {
      attachmentsBar.removeAllComponents();
      commentTextArea.clear();
    });
  }

  private void saveTextComment(Report report, String text)
  {
    final Comment comment = new Comment();
    comment.setType(Comment.Type.COMMENT);
    comment.setComment(text);
    saveCommentMetaData(report, comment);
  }

  private void saveAttachmentComment(Report report, String attachmentName, byte[] binaryData)
  {
    final Comment comment = new Comment();
    comment.setType(Comment.Type.COMMENT);
    comment.setAttachmentName(attachmentName);
    comment.setAttachment(binaryData);
    saveCommentMetaData(report, comment);
  }

  private void saveCommentMetaData(Report report, Comment comment)
  {
    comment.setAuthor(getSelectionCache().getUser());
    comment.setTimestamp(new Date());
    comment.setReport(report);
    repository.save(comment);
  }

  private void commitData()
  {
    final Priority priority              = (Priority) priorityComboBox.getValue();
    final Report.Type     type           = (Report.Type) typeComboBox.getValue();
    final Report.Status   status         = (Report.Status) statusComboBox.getValue();
    final Reporter        reporter       = (Reporter) assignedToComboBox.getValue();
    final ProjectVersion  projectVersion = (ProjectVersion) versionComboBox.getValue();

    for (Report report : reports)
    {
      if(priority != null)       report.setPriority(priority.getPriority());
      if(type != null)           report.setType(type);
      if(status != null)         report.setStatus(status);
      if(reporter != null)       report.setAssigned(reporter);
      if(projectVersion != null) report.setVersion(projectVersion);

      repository.save(report);
    }

    setContext(new HashSet<>(reports), project);
    addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
    @Override
    public void handleAction(Object sender, Object target)
    {
      System.out.println(sender);
      System.out.println(target);
    }
  });
  }

  public void setRepository(final BugrapRepository repository) {
    this.repository = repository;
    assignedToComboBox.setContainerDataSource(new BeanItemContainer<>(Reporter.class, repository.findReporters()));
  }

  public void setContext(final Set values, final Project project) {
    this.project = project;
    reports.clear();

    final Set<ProjectVersion> projectVersions = repository.findProjectVersions(project);
    versionComboBox.setContainerDataSource(new BeanItemContainer<>(ProjectVersion.class, projectVersions));

    if(values.size() == 1)
    {
      setupSingleReport((Report)values.iterator().next());
    }

    if(values.size() > 1)
    {
      setupMassUpdate(values);
    }
  }

  private void setupMassUpdate(Set values)
  {
    openInWindowButton.setVisible(false);
    artefactSummary.setValue(values.size() + " reports selected");
    artefactSummaryExtension.setVisible(true);

    final Set<Priority>        priorities = new HashSet<>();
    final Set<Report.Type>     types      = new HashSet<>();
    final Set<Report.Status>   status     = new HashSet<>();
    final Set<Reporter>        assignees  = new HashSet<>();
    final Set<ProjectVersion>  versions   = new HashSet<>();

    for (Object value : values)
    {
      final Report report = (Report) value;
      reports.add(repository.getReportById(report.getId()));

      priorities.add(Priority.translatePriority(report.getPriority()));
      types.add(report.getType());
      status.add(report.getStatus());
      assignees.add(report.getAssigned());
      versions.add(report.getVersion());
    }

    priorityComboBox.setValue(  getPreselectedValue(priorities));
    typeComboBox.setValue(      getPreselectedValue(types));
    statusComboBox.setValue(    getPreselectedValue(status));
    assignedToComboBox.setValue(getPreselectedValue(assignees));
    versionComboBox.setValue(   getPreselectedValue(versions));
    setNullSelectionAllowed(true);

    detailsTextArea.setValue("");
  }

  private <T> T getPreselectedValue(final Collection<T> values)
  {
    return values.size() != 1 ? null : values.iterator().next();
  }

  private void setupSingleReport(final Report report)
  {
    openInWindowButton.setVisible(!commentSection.isVisible());
    artefactSummary.setValue(report.getSummary());
    artefactSummaryExtension.setVisible(false);

    reports.add(repository.getReportById(report.getId()));
    priorityComboBox.setValue(Priority.translatePriority(report.getPriority()));
    typeComboBox.setValue(report.getType());
    statusComboBox.setValue(report.getStatus());
    assignedToComboBox.setValue(report.getAssigned());
    versionComboBox.setValue(report.getVersion());
    setNullSelectionAllowed(false);

    detailsTextArea.setValue(report.getDescription());
  }

  private void setNullSelectionAllowed(final boolean nullSelectionAllowed)
  {
    priorityComboBox.setNullSelectionAllowed(nullSelectionAllowed);
    typeComboBox.setNullSelectionAllowed(nullSelectionAllowed);
    statusComboBox.setNullSelectionAllowed(nullSelectionAllowed);
    assignedToComboBox.setNullSelectionAllowed(true);
    versionComboBox.setNullSelectionAllowed(true);
  }

  public void addUpdateListener(Button.ClickListener clickListener)
  {
    doneButton.addClickListener(clickListener);
    updateButton.addClickListener(clickListener);
  }

  public void addCloseListener(Button.ClickListener clickListener)
  {
    doneButton.addClickListener(clickListener);
    cancelButton.addClickListener(clickListener);
  }

  void onEnterTyped()
  {
    updateButton.click();
  }
}
