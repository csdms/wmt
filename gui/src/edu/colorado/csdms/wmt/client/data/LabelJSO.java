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
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * A GWT JavaScript overlay (JSO) type that describes the JSON returned on a
 * HTTP GET call to <a
 * href="http://csdms.colorado.edu/wmt/api/tag/list">tag/list</a>. Declares
 * JSNI methods to access attributes.
 * <p>
 * Note that an instance of LabelJSO can represent a single label or a JsArray
 * of labels.
 * 
 * @see <a
 *      href="http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html">http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html</a>
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LabelJSO extends JavaScriptObject {
  
  // Overlay types always have protected, zero-arg constructors.
  protected LabelJSO() {
  }

  /**
   * Gets the owner of a label, a String. This is a JSNI method.
   */
  public final native String getOwner() /*-{
		return this.owner;
  }-*/;

  /**
   * Gets the text of the label, a String. This is a JSNI method.
   */
  public final native String getLabel() /*-{
		return this.tag;
  }-*/;

  /**
   * Sets the text of the label, a String. This is a JSNI method.
   */
  public final native void setLabel(String label) /*-{
		this.tag = label;
  }-*/;

  /**
   * A JSNI method to get the id of the label, an int. 
   */
  public final native int getId() /*-{
		return this.id;
  }-*/;    

  /**
   * Sets the id of the label, an int. This is a JSNI method.
   */
  public final native void setId(int id) /*-{
		this.id = id;
  }-*/;

  /**
   * Gets a JsArray of labels. This is a JSNI method.
   */
  public final native JsArray<LabelJSO> getLabels() /*-{
		return this;
  }-*/;
  
  /**
   * Returns the state of the label button; true = selected. This is a JSNI
   * method.
   */
  public final native boolean isSelected() /*-{
    return (typeof this.selected != 'undefined') ? this.selected : false;
  }-*/;
  
  /**
   * Stores the state of the label button. This is a JSNI method. (It actually
   * adds a "selected" object to the JSON.)
   * 
   * @param selected a value of true indicates button is selected
   */
  public final native void isSelected(boolean selected) /*-{
    this.selected = selected;
  }-*/;
}
