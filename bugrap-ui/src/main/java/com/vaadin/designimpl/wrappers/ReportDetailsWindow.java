package com.vaadin.designimpl.wrappers;

import com.vaadin.designimpl.ReportDetails;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.Set;

public class ReportDetailsWindow extends Window
{
  public ReportDetailsWindow(final Set<Report> reports, final Project project, final BugrapRepository repository)
  {
    final ReportDetails windowContent = new ReportDetails(true);
    windowContent.setRepository(repository);
    windowContent.setContext(reports, project);
    windowContent.addCloseListener(event -> closeWindow(ReportDetailsWindow.this));
    windowContent.addUpdateListener((Button.ClickListener) event ->
    {
      if("Update".equals(event.getButton().getCaption())) return;
      setCaption(getWindowCaption(repository, project, reports.iterator().next()));
    });

    setContent(windowContent);
    setCaption(getWindowCaption(repository, project, reports.iterator().next()));
    setWidth(800, Sizeable.Unit.PIXELS);
    setHeight(600, Sizeable.Unit.PIXELS);
    setWindowMode(WindowMode.MAXIMIZED);
    center();
    setResizable(false);
    setClosable(false);
}

  public void show() {UI.getCurrent().addWindow(this);}

  private String getWindowCaption(final BugrapRepository repository, final Project project, final Report report)
  {
    final ProjectVersion ver = repository.getReportById(report.getId()).getVersion();
    return project.toString() + " > " + (ver == null ? "All versions" : ver);
  }

  private void closeWindow(Window window)
  {
    UI.getCurrent().removeWindow(window);
    window.close();
  }
}
