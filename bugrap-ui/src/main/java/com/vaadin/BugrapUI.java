package com.vaadin;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.designimpl.ReportsOverview;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import org.vaadin.bugrap.domain.BugrapRepository;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Widgetset("com.vaadin.MyAppWidgetset")
@PreserveOnRefresh
public class BugrapUI extends UI
{
  private static final long serialVersionUID = -8297468279063773773L;

  private static final BugrapRepository REPOSITORY = new BugrapRepository("C:\\save\\IdeaProjects\\Bugrap\\db");

  @Override
  protected void init(final VaadinRequest vaadinRequest) {
    final ReportsOverview layout = new ReportsOverview(REPOSITORY);

    setContent(layout);
  }

  @WebServlet(urlPatterns = "/*", /*value = "/bugrap*//*", name = "Bugrap", */ name = "Bugrap", asyncSupported = true)
  @VaadinServletConfiguration(ui = BugrapUI.class, productionMode = false)
  public static class MyUIServlet extends VaadinServlet {
    private static final long serialVersionUID = 2955083134612615320L;
  }
}
