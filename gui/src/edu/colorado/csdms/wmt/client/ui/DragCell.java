/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;

import edu.colorado.csdms.wmt.client.data.ComponentJSO;

/**
 * A draggable (using native GWT DnD) widget that displays a text item with a
 * grabby handle (made with CSS) on the left. Used to populate a
 * {@link ComponentList}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DragCell extends Grid implements DragStartHandler {

  private String label;
  private String id;
  private HTML grabCell;
  private HTML textCell;
  private Boolean sensitive;

  /**
   * Makes a DragCell for the given component.
   * 
   * @param componentJSO the ComponentJSO object representing the component
   */
  public DragCell(ComponentJSO componentJSO) {

    // A DragCell is a Grid with one row and two columns.
    super(1, 2);
        
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
    setTooltip(componentJSO);

    this.getElement().setDraggable(Element.DRAGGABLE_TRUE);
    addDomHandler(this, DragStartEvent.getType());
  }

  /**
   * The drag start event handler. The component id is the data carried 
   * in the draggable element.
   */
  @Override
  public void onDragStart(DragStartEvent event) {
    event.setData("text", this.id);
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
    
    // TODO Not a big deal, but not working.
    String isDraggable =
          this.sensitive ? Element.DRAGGABLE_TRUE : Element.DRAGGABLE_FALSE;
    this.getElement().setDraggable(isDraggable);
  }
}
