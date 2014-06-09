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

  private static final String[] ITEMS = {"foo", "bar", "baz"};
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
    exchange_items = JsArrayString.createArray().cast();
    for (String item : ITEMS) {
      exchange_items.push(item);
    }
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
