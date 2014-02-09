package edu.colorado.csdms.wmt.client.data;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * A GWT JavaScript overlay (JSO) type that describes a WMT component model,
 * with "id", "name", "url", "provides" and "uses" attributes. Declares JSNI
 * methods to access these attributes from a JSON and modify them in memory.
 * 
 * @see <a
 *      href="http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html">http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html</a>
 * @author Mark Piper (mark.piper@colorado.edu)
 * 
 */
public class ComponentJSO extends JavaScriptObject {

  // Overlay types have protected, no-arg, constructors.
  protected ComponentJSO() {
  }

  /**
   * A JSNI method to access the "id" attribute of a WMT component model. This
   * attribute is always present, and is a string.
   */
  public final native String getId() /*-{
		return this.id;
  }-*/;

  /**
   * A JSNI method to access the "name" attribute of a WMT component model.
   * This attribute is always present, and is a string.
   */
  public final native String getName() /*-{
		return this.name;
  }-*/;

  /**
   * A JSNI method to access the "url" attribute of a WMT component model.
   * This attribute is always present, and is a string.
   */
  public final native String getURL() /*-{
		return this.url;
  }-*/;

  /**
   * A JSNI method to access the "provides" attribute of a WMT component
   * model. This attribute is a JsArray of PortJSO objects. It's always
   * present, but it may be empty.
   */
  public final native JsArray<PortJSO> getPortsProvided() /*-{
		return this.provides;
  }-*/;

  /**
   * A JSNI method to access the "uses" attribute of a WMT component model.
   * This attribute is a JsArray of PortJSO objects. It's always present, but
   * it may be empty.
   */
  public final native JsArray<PortJSO> getPortsUsed() /*-{
		return this.uses;
  }-*/;

  /**
   * A non-JSNI method for stringifying the attributes of a Component. Must be
   * final.
   */
  public final Vector<String> toStringVector() {

    Vector<String> retVal = new Vector<String>();
    retVal.add("id: " + getId());
    retVal.add("name: " + getName());
    retVal.add("url: " + getURL());
    for (int i = 0; i < getPortsProvided().length(); i++) {
      retVal.add("provides: " + getPortsProvided().get(i).toStringVector().toString());
    }
    for (int i = 0; i < getPortsUsed().length(); i++) {
      retVal.add("uses: " + getPortsUsed().get(i).toStringVector().toString());
    }
    return retVal;
  }
}
