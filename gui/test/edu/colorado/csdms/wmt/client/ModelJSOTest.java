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

import com.google.gwt.core.client.JsArray;
import com.google.gwt.junit.client.GWTTestCase;

import edu.colorado.csdms.wmt.client.data.ModelComponentJSO;
import edu.colorado.csdms.wmt.client.data.ModelJSO;

/**
 * Tests for {@link ModelJSO}. JUnit integration is provided by extending
 * {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelJSOTest extends GWTTestCase {

  private ModelJSO jso;
  private ModelJSO jsoFull;
  private String name;
  private ModelComponentJSO component;
  private int nComponents;
  private JsArray<ModelComponentJSO> componentArray;
  
  /**
   * The module that sources this class. Must be present.
   */
  @Override
  public String getModuleName() {
    return "edu.colorado.csdms.wmt.WMT";
  }

  /**
   * A JSNI method that defines a {@link ModelComponentJSO} object for testing.
   */
  private native ModelComponentJSO makeModelComponentJSO() /*-{
		return {
      "id":"cem",
      "class":"CEM",
      "driver":true,
      "parameters":{
        "simulation_name":"CEM",
        "number_of_rows":"4200",
        "number_of_columns":"4200",
        "row_spacing":"150",
        "column_spacing":"150",
        "output_format":"vtk"
      },
      "connect":{
        "river":"river@avulsion",
        "waves":"waves@waves"
      }
		};
  }-*/;

  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link ModelJSO} object for testing.
   */
  private native ModelJSO testModelJSO(String name) /*-{
		return {
		  "name" : name
		};
  }-*/;
  
  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link ModelJSO} object for testing.
   */
  private native ModelJSO testModelJSO(String name,
      JsArray<ModelComponentJSO> componentArray) /*-{
		return {
			"name" : name,
			"model" : componentArray
		};
  }-*/;

  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    name = "My Excellent Model";
    component = makeModelComponentJSO();
    nComponents = 1;
    jso = testModelJSO(name);
    componentArray = JsArray.createArray().cast();
    componentArray.push(component);
    jsoFull = testModelJSO(name, componentArray);
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  /*
   * Test getting the model name.
   */
  @Test
  public void testGetName() {
    assertEquals(name, jso.getName());
  }

  /*
   * Test setting the model name.
   */
  @Test
  public void testSetName() {
    String newName = "My Even More Excellent Model";
    jso.setName(newName);
    assertEquals(newName, jso.getName());
  }

  /*
   * Test counting the number of components. If there are none, the count should
   * be zero.
   */
  @Test
  public void testCountZeroComponents() {
    int zero = 0;
    assertEquals(zero, jso.nComponents());
  }

  /*
   * Test counting the number of components.
   */
  @Test
  public void testCountComponents() {
    assertEquals(nComponents, jsoFull.nComponents());
  }
  
  /*
   * Test getting components. If components aren't defined, the result should be
   * null.
   */
  @Test
  public void testGetZeroComponents() {
    assertNull(jso.getComponents());
  }

  /*
   * Test getting components.
   */
  @Test
  public void testGetComponents() {
    assertEquals(componentArray, jsoFull.getComponents());
  }

  /*
   * Test getting a single component.
   */
  @Test
  public void testGetSingleComponent() {
    assertEquals(component, jsoFull.getComponents().get(0));
  }

  /*
   * Test setting and getting components. It would be preferable to have a 
   * distinct test case for only setting components.
   */
  @Test
  public void testSetAndGetComponents() {
    jso.setComponents(componentArray);
    assertEquals(componentArray, jso.getComponents());
  }

  /*
   * Test setting components to null and retrieving.
   */
  @Test
  public void testSetNullComponents() {
    jso.setComponents(null);
    assertNull(jso.getComponents());
  }
  
}
