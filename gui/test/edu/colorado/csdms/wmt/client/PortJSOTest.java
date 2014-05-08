/**
 * <License>
 */
package edu.colorado.csdms.wmt.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.junit.client.GWTTestCase;

import edu.colorado.csdms.wmt.client.data.PortJSO;

/**
 * Tests for {@link PortJSO}. JUnit integration is provided by extending
 * {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @see http://blog.danielwellman.com/2008/08/testing-json-parsing-using-javascript-overlay-types-in-gwt-15.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class PortJSOTest extends GWTTestCase {

  private PortJSO portJSO;
  private String id;
  private boolean required;
  private JsArrayString exchange_items;
  
  /**
   * The module that sources this class. Must be present.
   */
  @Override
  public String getModuleName() {
     return "edu.colorado.csdms.wmt.WMT";
  }
  
  /**
   * A JSNI method that defines a fixture for the tests. Returns a PortJSO
   * object for testing.
   * 
   * @param id
   * @param required
   * @param exchange_items
   */
  private native PortJSO testPortJSO(String id, boolean required,
      JsArrayString exchange_items) /*-{
		return {
			"required" : required,
			"id" : id,
			"exchange_items" : exchange_items
		}
  }-*/;  
  
  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    id = "test";
    required = true;
    exchange_items = (JsArrayString) JsArrayString.createArray();
    exchange_items.push("foo");
    exchange_items.push("bar");
    exchange_items.push("baz");
    portJSO = testPortJSO(id, required, exchange_items);
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  @Test
  public void testGetId() {
    assertEquals(id, portJSO.getId());
  }
  
  @Test
  public void testSetId() {
    String newId = "foo";
    portJSO.setId(newId);
    assertEquals(newId, portJSO.getId());
  }
  
  @Test
  public void testIsRequired() {
    assertTrue(portJSO.isRequired());
  }

  @Test
  public void testSetIsRequired() {
    boolean isRequired = false;
    portJSO.isRequired(isRequired);
    assertFalse(portJSO.isRequired());
  }

  @Test
  public void testGetExchangeItems() {
    assertEquals(exchange_items, portJSO.getExchangeItems());
  }
}
