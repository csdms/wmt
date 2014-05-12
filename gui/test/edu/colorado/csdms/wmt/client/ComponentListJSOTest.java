/**
 * <License>
 */
package edu.colorado.csdms.wmt.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.junit.client.GWTTestCase;

import edu.colorado.csdms.wmt.client.data.ComponentListJSO;

/**
 * Tests for {@link ComponentListJSO}. JUnit integration is provided by
 * extending {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @see http://blog.danielwellman.com/2008/08/testing-json-parsing-using-javascript-overlay-types-in-gwt-15.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentListJSOTest extends GWTTestCase {

  private ComponentListJSO jso;
  private JsArrayString ids;
  
  /**
   * The module that sources this class. Must be present.
   */
  @Override
  public String getModuleName() {
     return "edu.colorado.csdms.wmt.WMT";
  }
  
  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link ComponentListJSO} object for testing.
   * 
   * @param ids
   */
  private native ComponentListJSO testComponentListJSO(JsArrayString ids) /*-{
		return ids;
  }-*/;  
  
  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    ids = (JsArrayString) JsArrayString.createObject();
    ids.setLength(3);
    ids.push("avulsion");
    ids.push("cem");
    ids.push("hydrotrend");
    jso = testComponentListJSO(ids);
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  // Test the length of the array.
  @Test
  public void testLength() {
    int arrayLength = 3;
    assertEquals(arrayLength, jso.getComponents().length());
  }

  // Test whether all ids can be retrieved.
  @Test
  public void testGetIds() {
    assertEquals(ids, jso.getComponents());
  }

  // Test whether a single id can be retrieved.
  @Test
  public void testGetSingleId() {
    int index = 0;
    assertEquals(ids.get(index), jso.getComponents().get(index));
  }
}
