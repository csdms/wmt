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
 * @see http://stackoverflow.com/a/7402103/1563298
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public interface Resources extends ClientBundle {

  public static final Resources INSTANCE = GWT.create(Resources.class);
  
  @Source("WMT.css")
  @CssResource.NotStrict
  CssResource css();
}
