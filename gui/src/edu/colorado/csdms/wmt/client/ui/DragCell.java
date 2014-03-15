/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.TreeItem;

import edu.colorado.csdms.wmt.client.data.Component;
import edu.colorado.csdms.wmt.client.data.ComponentJSO;
import edu.colorado.csdms.wmt.client.data.Port;

/**
 * A draggable (using native GWT DnD) widget that displays a text item with a
 * grabby handle (made with CSS) on the left. Used to populate a
 * {@link ComponentList}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DragCell extends Grid implements DragStartHandler,
    MouseOverHandler, MouseOutHandler {

  private ComponentJSO componentJSO;
  private String label;
  private String id;
  private HTML grabCell;
  private HTML textCell;
  private Boolean sensitive;
  private ComponentInfoDialogBox componentInfoDialogBox;

  /**
   * Makes a DragCell for the given component.
   * 
   * @param componentJSO the ComponentJSO object representing the component
   */
  public DragCell(ComponentJSO componentJSO) {

    // A DragCell is a Grid with one row and two columns. And it's draggable.
    super(1, 2);
    this.getElement().setDraggable(Element.DRAGGABLE_TRUE);

    this.componentJSO = componentJSO;

    // The name doesn't really do anything, though it is what GWT native DnD
    // shows on drag. The id is key -- it's what is picked up in the ModelTree
    // on drop. Use the id to figure out what model is being dragged, whether
    // it satisfies the uses port and what ports it provides.
    this.label = componentJSO.getName();
    this.id = componentJSO.getId();

    // Font Awesome! Also tried fa-arrow-right.
    this.grabCell = new HTML("<i class='fa fa-plus-square fa-fw'></i>");
    this.textCell = new HTML(label);

    this.setWidget(0, 0, grabCell);
    this.setWidget(0, 1, textCell);

    grabCell.setStyleName("wmt-GrabCell");
    textCell.setStyleName("wmt-TextCell");

    // Set the pointy-hand cursor when over a DragCell.
    this.getElement().getStyle().setCursor(Cursor.POINTER);

    // Set a tooltip on the component.
    // setTooltip(componentJSO);

    // Associate event handlers.
    addDomHandler(this, DragStartEvent.getType());
    grabCell.addClickHandler(new GrabCellClickHandler());
    addDomHandler(this, MouseOverEvent.getType());
    addDomHandler(this, MouseOutEvent.getType());
  }

  /**
   * The drag start event handler. The component id is the data carried in the
   * draggable element.
   */
  @Override
  public void onDragStart(DragStartEvent event) {
    event.setData("text", this.id);
  }

  /**
   * Displays the {@link ComponentInfoDialogBox} when the mouse moves over the
   * {@link DragCell}.
   */
  @Override
  public void onMouseOver(MouseOverEvent event) {
    componentInfoDialogBox = new ComponentInfoDialogBox(componentJSO);
    final Integer x = event.getClientX();
    final Integer y = event.getClientY();
    componentInfoDialogBox.setPopupPositionAndShow(new PositionCallback() {
      @Override
      public void setPosition(int offsetWidth, int offsetHeight) {
        Integer nudge = 5; // px
        componentInfoDialogBox.setPopupPosition(x + nudge, y);
      }
    });
  }

  /**
   * Closes the component info dialog box when the mouse leaves the
   * {@link DragCell}.
   */
  @Override
  public void onMouseOut(MouseOutEvent event) {
    componentInfoDialogBox.hide();
  }

  /**
   * A worker that queries the component to get information to display in the
   * tooltip.
   * 
   * @param componentJSO the ComponentJSO object representing the component
   */
  public void setTooltip(ComponentJSO componentJSO) {
    String text = "Provides: ";
    Integer nPortsProvided = componentJSO.getPortsProvided().length();
    if (nPortsProvided == 0) {
      text += "none";
    } else {
      for (int i = 0; i < componentJSO.getPortsProvided().length(); i++) {
        text += componentJSO.getPortsProvided().get(i).getId();
      }
    }
    setTitle(text);
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public HTML getGrabCell() {
    return grabCell;
  }

  public void setGrabCell(HTML grabCell) {
    this.grabCell = grabCell;
  }

  public HTML getTextCell() {
    return this.textCell;
  }

  public void setTextCell(HTML textCell) {
    this.textCell = textCell;
  }

  public Boolean getSensitive() {
    return sensitive;
  }

  /**
   * Sets the sensitivity of the DragCell with CSS style and draggability.
   * 
   * @todo Setting draggability is not working.
   * @param sensitive
   */
  public void setSensitive(Boolean sensitive) {

    this.sensitive = sensitive;
    this.getGrabCell().setStyleDependentName("notallowed", !this.sensitive);
    this.getTextCell().setStyleDependentName("notallowed", !this.sensitive);

    // TODO Not a huge deal, but this is not working.
    String isDraggable =
        this.sensitive ? Element.DRAGGABLE_TRUE : Element.DRAGGABLE_FALSE;
    this.getElement().setDraggable(isDraggable);
  }

  /**
   * Handles click on the grabby handle button in a DragCell. Attempt to match
   * the selected component with an open "uses" port in the ModelTree.
   */
  public class GrabCellClickHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {

      // Get the parent (the ComponentList) to get access to the DataManager,
      // which will be needed below.
      ComponentList parent = (ComponentList) DragCell.this.getParent();

      // The selected component.
      Component component =
          new Component(parent.data.getComponent(DragCell.this.id));

      // Find what "uses" ports in the ModelTree are currently open. If there
      // are none, short-circuit the method.
      List<ModelCell> openCells =
          parent.data.getModelTree().findOpenModelCells();
      if (openCells.size() == 0) {
        return;
      }

      // In the special case of the driver being the only open "port", add
      // the selected component and short-circuit the method.
      if (openCells.get(0).getPortCell().getPort().getId().matches("driver")) {
        TreeItem target = openCells.get(0).getParentTreeItem();
        parent.data.getModelTree().addComponent(component, target);
        target.setState(true);
        return;
      }

      // Since this is a convenience, take the first provides port of the
      // selected component.
      Port port = component.getProvidesPorts()[0];
      // GWT.log("Port provided: " + port.getId());

      // Try to match a uses port with a provides port of the component.
      for (int i = 0; i < openCells.size(); i++) {
        ModelCell cell = openCells.get(i);
        // GWT.log("Open port: " + cell.getPortCell().getPort().getId());
        if (port.getId().matches(cell.getPortCell().getPort().getId())) {
          // GWT.log("Port match!");
          TreeItem target = cell.getParentTreeItem();
          parent.data.getModelTree().addComponent(component, target);
          target.setState(true);
          break;
        }
      }
    }
  }
}
