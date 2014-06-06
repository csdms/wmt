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

import com.google.gwt.junit.client.GWTTestCase;

import edu.colorado.csdms.wmt.client.data.ParameterJSO;
import edu.colorado.csdms.wmt.client.data.ValueJSO;

/**
 * Tests for {@link ParameterJSO}. JUnit integration is provided by extending
 * {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ParameterJSOTest extends GWTTestCase {

  private ParameterJSO jso;
  private String key;
  private String name;
  private String description;
  private ValueJSO value;

  /**
   * The module that sources this class. Must be present.
   */
  @Override
  public String getModuleName() {
     return "edu.colorado.csdms.wmt.WMT";
  }

  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link ParameterJSO} object for testing.
   *
   * @param key
   * @param name
   * @param description
   */
  private native ParameterJSO testParameterJSO(String key, String name,
      String description, ValueJSO value) /*-{
		return {
			"key" : key,
			"name" : name,
			"description" : description,
			"value" : value
		};
  }-*/;

  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link ValueJSO} object for testing.
   */
  private native ValueJSO testValueJSO() /*-{
		return {
			"default" : 500,
			"range" : {
				"max" : 2147483647,
				"min" : 0
			},
			"type" : "int"
		};
  }-*/;
  
  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    key = "number_of_rows";
    name = "Number of rows";
    description = "Number of rows in the computational grid";
    value = testValueJSO();
    jso = testParameterJSO(key, name, description, value);
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  /*
   * Test getting key.
   */
  @Test
  public void testGetKey() {
    assertEquals(key, jso.getKey());
  }

  /*
   * Test getting name.
   */
  @Test
  public void testGetName() {
    assertEquals(name, jso.getName());
  }

  /*
   * Test getting description.
   */
  @Test
  public void testGetDescription() {
    assertEquals(description, jso.getDescription());
  }
  
  /*
   * Test getting value.
   */
  @Test
  public void testGetValue() {
    assertSame(value, jso.getValue());
  }
}
