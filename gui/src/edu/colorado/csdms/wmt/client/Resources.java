/**
 * <License>
 */
package edu.colorado.csdms.wmt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * Creates a new {@link ClientBundle} to define custom CSS rules for WMT.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideClientBundle.html#CssResource
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public interface Resources extends ClientBundle {

  public static final Resources INSTANCE = GWT.create(Resources.class);
  
  @Source("WMT.css")
  @CssResource.NotStrict
  CssResource css();
}
