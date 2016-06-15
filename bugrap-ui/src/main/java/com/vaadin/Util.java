package com.vaadin;

import com.vaadin.client.ApplicationConnection;
import org.vaadin.bugrap.domain.BugrapRepository;

/**
 * Created by Werwolf on 02.06.2016.
 */
public class Util {
  public static void main(String[] args) {

    new BugrapRepository("C:\\save\\IdeaProjects\\Bugrap\\db").populateWithTestData();
//    ApplicationConnection
  }
}
