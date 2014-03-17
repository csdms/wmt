package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataURL;

/**
 * Supplies a Grid with links for viewing or downloading the input
 * configuration files generated by the parameters set for a component in a
 * model.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ViewInputFilesPanel extends FlexTable {

  private DataManager data;
  private String componentId;

  /**
   * Creates a new ViewInputFilesPanel with links built for the current state
   * of a model component parameter.
   * 
   * @param data the DataManager object for the WMT session
   * @param componentId the id of the model component, a String
   */
  public ViewInputFilesPanel(DataManager data, String componentId) {

    this.data = data;
    this.componentId = componentId;
    this.getElement().getStyle().setMarginTop(2, Unit.EM);

    String baseUrl = "https://csdms.colorado.edu/wmt-server/";
    String currentUrl = baseUrl + "current/";
    String defaultUrl = baseUrl + "default/";

    HTML viewCurrentHtml =
        new HTML("<a href='" + currentUrl + "html'>HTML</a>");
    HTML viewCurrentText =
        new HTML("<a href='" + currentUrl + "text'>text</a>");
    HTML viewCurrentJson =
        new HTML("<a href='" + currentUrl + "json'>JSON</a>");

    HTML viewDefaultHtml =
        new HTML("<a href='" + defaultUrl + "html'>HTML</a>");
    HTML viewDefaultText =
        new HTML("<a href='" + defaultUrl + "text'>text</a>");
    HTML viewDefaultJson =
        new HTML("<a href='" + defaultUrl + "json'>JSON</a>");

    HTML viewTitle = new HTML("View input files");
    viewTitle.setTitle("View the model configuration files "
        + "generated using these parameter values");

    this.setWidget(0, 0, viewTitle);
    this.getFlexCellFormatter().setColSpan(0, 0, 6);
    this.getCellFormatter().setHorizontalAlignment(0, 0,
        HasHorizontalAlignment.ALIGN_CENTER);

    this.setWidget(1, 0, new HTML("current:"));
    this.setWidget(1, 1, viewCurrentHtml);
    this.setWidget(1, 2, new HTML("|"));
    this.setWidget(1, 3, viewCurrentText);
    this.setWidget(1, 4, new HTML("|"));
    this.setWidget(1, 5, viewCurrentJson);

    this.setWidget(2, 0, new HTML("default:"));
    this.setWidget(2, 1, viewDefaultHtml);
    this.setWidget(2, 2, new HTML("|"));
    this.setWidget(2, 3, viewDefaultText);
    this.setWidget(2, 4, new HTML("|"));
    this.setWidget(2, 5, viewDefaultJson);

    for (int j = 0; j < this.getCellCount(1); j++) {
      this.getCellFormatter().getElement(1, j).getStyle().setPaddingLeft(5,
          Unit.PX);
      this.getCellFormatter().getElement(2, j).getStyle().setPaddingLeft(5,
          Unit.PX);
    }

    viewDefaultHtml.addClickHandler(new InterceptClickHandler("html", true));
    viewDefaultText.addClickHandler(new InterceptClickHandler("text", true));
    viewDefaultJson.addClickHandler(new InterceptClickHandler("json", true));
    viewCurrentHtml.addClickHandler(new InterceptClickHandler("html", false));
    viewCurrentText.addClickHandler(new InterceptClickHandler("text", false));
    viewCurrentJson.addClickHandler(new InterceptClickHandler("json", false));
  }

  /**
   * Intercepts the click on the link in the HTML, text or JSON cell and
   * directs it to open in another tab/window.
   */
  public class InterceptClickHandler implements ClickHandler {

    private String type;
    private Boolean useDefault;

    public InterceptClickHandler(String type, Boolean useDefault) {
      this.type = type;
      this.useDefault = useDefault;
    }

    @Override
    public void onClick(ClickEvent event) {
      event.preventDefault();
      if (!useDefault && !data.modelIsSaved()) {
        Window.alert("Model must be saved to view current input files.");
        return;
      }
      String url = DataURL.formatComponent(data, componentId, type, useDefault);
      Window.open(url, "_blank", null);
    }
  }
}
