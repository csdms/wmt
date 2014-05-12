/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 mcflugen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.Map;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.LabelJSO;
import edu.colorado.csdms.wmt.client.ui.handler.AuthenticationHandler;
import edu.colorado.csdms.wmt.client.ui.widgets.ComponentInfoDialogBox;
import edu.colorado.csdms.wmt.client.ui.widgets.LoginPanel;
import edu.colorado.csdms.wmt.client.ui.widgets.OpenDialogBox;

/**
 * Defines the initial layout of views (a perspective, in Eclipse parlance)
 * for a WMT instance in a browser window. The Perspective holds three views,
 * named North, West, and East. The top-level organizing panel for the
 * GUI is a DockLayoutPanel. Includes getters and setters for the UI elements
 * that are arrayed on the Perspective.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class Perspective extends DockLayoutPanel {

  private DataManager data;

  // Browser window dimensions (in px) used for setting up UI views.
  private Integer browserWindowWidth;

  // Primary UI panels.
  private ViewNorth viewNorth;
  private ViewWest viewWest;
  private ViewEast viewEast;

  // Secondary UI panels/widgets.
  private ScrollPanel scrollModel;
  private ScrollPanel scrollParameters;
  private LoginPanel loginPanel;
  private ModelActionPanel modelActionPanel;
  private ModelTree modelTree;
  private ParameterTable parameterTable;
  
  // Tertiary UI widgets!
  private ComponentInfoDialogBox componentInfoBox;
  private LabelsMenu labelsMenu;
  private OpenDialogBox openDialogBox;

  /**
   * Draws the panels and their children that compose the basic WMT GUI.
   */
  public Perspective(DataManager data) {

    super(Unit.PX);
    this.data = data;
    this.data.setPerspective(this);
    this.addStyleName("wmt-DockLayoutPanel");
    if (data.isDevelopmentMode()) {
      this.addStyleDependentName("devmode");
    }
    
    // Determine initial view sizes based on browser window dimensions.
    browserWindowWidth = Window.getClientWidth();
    Integer viewEastInitialWidth =
        (int) Math.round(Constants.VIEW_EAST_FRACTION * browserWindowWidth);
    Integer headerHeight = 50; // TODO diagnose from largest header elt

    // The Perspective has two children, a header in the north panel
    // and a SplitLayoutPanel below.
    viewNorth = new ViewNorth();
    this.addNorth(viewNorth, headerHeight);
    SplitLayoutPanel splitter = new SplitLayoutPanel(Constants.SPLITTER_SIZE);
    splitter.addStyleName("wmt-SplitLayoutPanel");
    this.add(splitter);

    // The SplitLayoutPanel defines panels which translate to the West
    // and East views of WMT.
    viewEast = new ViewEast();
    splitter.addEast(viewEast, viewEastInitialWidth);
    viewWest = new ViewWest();
    splitter.add(viewWest); // must be last
    
    // The ComponentInfoDialogBox floats above the Perspective.
    this.setComponentInfoBox(new ComponentInfoDialogBox());
  }

  /**
   * An inner class to define the header (North view) of the WMT GUI.
   */
  private class ViewNorth extends HorizontalPanel {
    
    /**
     * Makes the Header (North) view of the WMT GUI.
     */
    public ViewNorth() {

      this.setStyleName("wmt-NavBar");

      HTML title = new HTML("The CSDMS Web Modeling Tool");
      title.setStyleName("wmt-NavBarTitle");
      this.add(title);

      loginPanel = new LoginPanel();
      loginPanel.getSignInButton().addClickHandler(
          new AuthenticationHandler(data, loginPanel));
      this.add(loginPanel);
    }
  } // end ViewNorth

  /**
   * An inner class to define the West panel of the WMT client.
   */
  private class ViewWest extends TabLayoutPanel {

    /**
     * Makes the West view of the WMT client. It displays the model.
     */
    public ViewWest() {
      super(Constants.TAB_BAR_HEIGHT, Unit.PX);
      setModelPanel(new ScrollPanel());
      String tabTitle = data.tabPrefix("model") + "Model";
      this.add(scrollModel, tabTitle, true);
    }
  } // end ViewWest

  /**
   * An inner class to define the East panel of the WMT client.
   */
  private class ViewEast extends TabLayoutPanel {

    /**
     * Makes the East view of the WMT client. It displays the parameters of the
     * currently selected model.
     */
    public ViewEast() {
      super(Constants.TAB_BAR_HEIGHT, Unit.PX);
      setParametersPanel(new ScrollPanel());
      String tabTitle = data.tabPrefix("parameter") + "Parameters";
      this.add(scrollParameters, tabTitle, true);
    }
  } // end ViewEast

  public ScrollPanel getModelPanel() {
    return scrollModel;
  }

  public void setModelPanel(ScrollPanel scrollModel) {
    this.scrollModel = scrollModel;
  }

  /**
   * A convenience method for setting the tab title of the Model panel. If the
   * model isn't saved, prepend an asterisk to its name.
   */
  public void setModelPanelTitle() {
    String tabTitle = data.tabPrefix("model") + "Model";
    if (data.getModel().getName() != null) {
      String marker = data.modelIsSaved() ? "" : "*";
      tabTitle += " (" + marker + data.getModel().getName() + ")";
    } else {
      data.getModel().setName("Model " + data.saveAttempts.toString());
    }
    viewWest.setTabHTML(0, tabTitle);
  }

  public ComponentInfoDialogBox getComponentInfoBox() {
    return componentInfoBox;
  }

  public void setComponentInfoBox(ComponentInfoDialogBox componentInfoBox) {
    this.componentInfoBox = componentInfoBox;
  }

  /**
   * Returns a reference to the {@link ModelTree} used in a WMT session.
   */
  public ModelTree getModelTree() {
    return modelTree;
  }

  /**
   * Stores a reference to the {@link ModelTree} used in a WMT session.
   * 
   * @param modelTree the ModelTree instance
   */
  public void setModelTree(ModelTree modelTree) {
    this.modelTree = modelTree;
  }

  public ScrollPanel getParametersPanel() {
    return scrollParameters;
  }

  public void setParametersPanel(ScrollPanel scrollParameters) {
    this.scrollParameters = scrollParameters;
  }

  /**
   * A convenience method for setting the title of the Parameters panel.
   * 
   * @param componentId the id of the component whose parameters are displayed
   */
  public void setParameterPanelTitle(String componentId) {
    String tabTitle = data.tabPrefix("parameter") + "Parameters";
    if (componentId != null) {
      String componentName = data.getModelComponent(componentId).getName();
      tabTitle += " (" + componentName + ")";
    }
    viewEast.setTabHTML(0, tabTitle);
  }

  /**
   * Returns a reference to the {@link ParameterTable} used in the
   * "Parameters" tab of a WMT session.
   */
  public ParameterTable getParameterTable() {
    return parameterTable;
  }

  /**
   * Stores a reference to the {@link ParameterTable} used in the "Parameters"
   * tab of a WMT session.
   * 
   * @param parameterTable the parameterTable to set
   */
  public void setParameterTable(ParameterTable parameterTable) {
    this.parameterTable = parameterTable;
  }
  
  public TabLayoutPanel getViewEast() {
    return viewEast;
  }

  public TabLayoutPanel getViewWest() {
    return viewWest;
  }

  public ModelActionPanel getActionButtonPanel() {
    return modelActionPanel;
  }

  public void setActionButtonPanel(ModelActionPanel modelActionPanel) {
    this.modelActionPanel = modelActionPanel;
  }

  public LabelsMenu getLabelsMenu() {
    return labelsMenu;
  }

  public void setLabelsMenu(LabelsMenu labelsMenu) {
    this.labelsMenu = labelsMenu;
  }

  public OpenDialogBox getOpenDialogBox() {
    return openDialogBox;
  }

  public void setOpenDialogBox(OpenDialogBox openDialogBox) {
    this.openDialogBox = openDialogBox;
  }

  public LoginPanel getLoginPanel() {
    return loginPanel;
  }

  public void setLoginPanel(LoginPanel loginPanel) {
    this.loginPanel = loginPanel;
  }

  /**
   * Sets up the {@link ModelActionPanel} and the default starting
   * {@link ModelTree} in the "Model" tab, showing only the open port for the
   * driver of the model.
   */
  public void initializeModel() {
    modelTree = new ModelTree(data);
    modelActionPanel = new ModelActionPanel(data);
    modelActionPanel.setStyleName("wmt-ModelActionPanel");
    VerticalPanel panel = new VerticalPanel();
    panel.add(modelActionPanel);
    panel.add(modelTree);
    scrollModel.add(panel);
  }

  /**
   * Creates an empty ParameterTable to display in the "Parameters" tab.
   */
  public void initializeParameterTable() {
    parameterTable = new ParameterTable(data);
    scrollParameters.add(parameterTable);
  }

  /**
   * Resets WMT to an approximation of its startup state.
   */
  public void reset() {
    data.resetModelComponents();
    parameterTable.clearTable();
    modelTree.initializeTree();
    data.updateModelSaveState(false);
    ((ComponentSelectionMenu) modelTree.getDriverComponentCell()
        .getComponentMenu()).updateComponents();
    
    // Deselect all labels except for the owner label.
    for (Map.Entry<String, LabelJSO> entry : data.modelLabels.entrySet()) {
      try {
        entry.getValue().isSelected(entry.getKey().equals(data.security.getWmtUsername()));
      } catch (Exception e) {
        GWT.log(e.toString());
      }
    }
    labelsMenu.populateMenu();
  }
}
