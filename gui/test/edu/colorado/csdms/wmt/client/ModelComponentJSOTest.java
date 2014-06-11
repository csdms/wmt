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

import edu.colorado.csdms.wmt.client.data.ModelComponentConnectionsJSO;
import edu.colorado.csdms.wmt.client.data.ModelComponentJSO;
import edu.colorado.csdms.wmt.client.data.ModelComponentParametersJSO;

/**
 * Tests for {@link ModelComponentJSO}. JUnit integration is provided
 * by extending {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelComponentJSOTest extends GWTTestCase {

  private ModelComponentJSO jso;
  private ModelComponentJSO jsoFull;
  private String id;
  private String className;
  private boolean isDriver;
  private ModelComponentParametersJSO parameters;
  private ModelComponentConnectionsJSO connections;
  private int nParameters;
  private int nConnections;
  
  /**
   * The module that sources this class. Must be present.
   */
  @Override
  public String getModuleName() {
    return "edu.colorado.csdms.wmt.WMT";
  }

  /**
   * A JSNI method that defines a {@link ModelComponentParametersJSO} object for
   * testing.
   */
  private native ModelComponentParametersJSO makeModelComponentParametersJSO() /*-{
		return {
			"simulation_name" : "Avulsion",
			"number_of_rows" : "2100",
			"number_of_columns" : "2100",
			"row_spacing" : "300",
			"column_spacing" : "300",
			"output_format" : "vtk"
		};
  }-*/;
  
  /**
   * A JSNI method that defines a {@link ModelComponentConnectionsJSO} object
   * for testing.
   */
  private native ModelComponentConnectionsJSO makeModelComponentConnectionsJSO() /*-{
		return {
			"discharge":null,
      "elevation":"elevation@cem"
		};
  }-*/;

  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link ModelComponentJSO} object for testing.
   */
  private native ModelComponentJSO testModelComponentJSO(String id,
      String className) /*-{
		var modelComponent = {id:id};
		modelComponent["class"] = className;
		return modelComponent;
  }-*/;
  
  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link ModelComponentJSO} object for testing.
   */
  private native ModelComponentJSO testModelComponentJSO(String id,
      String className, ModelComponentParametersJSO parameters,
      ModelComponentConnectionsJSO connections) /*-{
		var modelComponent = {
			"id" : id,
			"parameters" : parameters,
			"connect" : connections
		};
		modelComponent["class"] = className;
		return modelComponent;
  }-*/;

  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    id = "avulsion";
    className = "Avulsion";
    isDriver = false;
    parameters = makeModelComponentParametersJSO();
    nParameters = parameters.getKeys().length();
    connections = makeModelComponentConnectionsJSO();
    nConnections = connections.getPortIds().length();
    jso = testModelComponentJSO(id, className);
    jsoFull = testModelComponentJSO(id, className, parameters, connections);
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  /*
   * Test getting the id.
   */
  @Test
  public void testGetId() {
    assertEquals(id, jso.getId());
  }

  /*
   * Test setting the id.
   */
  @Test
  public void testSetId() {
    String newId = "avulsion_1";
    jso.setId(newId);
    assertEquals(newId, jso.getId());
  }

  /*
   * Test getting the class name.
   */
  @Test
  public void testGetClassName() {
    assertEquals(className, jso.getClassName());
  }

  /*
   * Test setting the class name.
   */
  @Test
  public void testSetClassName() {
    String newClassName = "Avulsion (standalone)";
    jso.setClassName(newClassName);
    assertEquals(newClassName, jso.getClassName());
  }

  /*
   * Test whether this is the driver of the model. If "driver" is undefined, 
   * false should be returned.
   */
  @Test
  public void testIsDriver() {
    assertFalse(jso.isDriver());
  }

  /*
   * Test setting this component as the driver of the model.
   */
  @Test
  public void testSetDriver() {
    jso.setDriver();
    assertTrue(jso.isDriver());
  }
  
  /*
   * Test counting the number of component parameters. If parameters aren't 
   * defined, the count should be zero.
   */
  @Test
  public void testCountZeroParameters() {
    int zero = 0;
    assertEquals(zero, jso.nParameters());
  }

  /*
   * Test counting the number of component parameters.
   */
  @Test
  public void testCountParameters() {
    assertEquals(nParameters, jsoFull.nParameters());
  }
  
  /*
   * Test getting parameters. If parameters aren't defined, the result should be
   * null.
   */
  @Test
  public void testGetZeroParameters() {
    assertNull(jso.getParameters());
  }

  /*
   * Test getting parameters.
   */
  @Test
  public void testGetParameters() {
    assertEquals(parameters, jsoFull.getParameters());
  }

  /*
   * Test setting and getting parameters. It would be preferable to have a 
   * distinct test case for only setting parameters.
   */
  @Test
  public void testSetAndGetParameters() {
    jso.setParameters(parameters);
    assertEquals(parameters, jso.getParameters());
  }

  /*
   * Test setting parameters to null and retrieving.
   */
  @Test
  public void testSetNullParameters() {
    jso.setParameters(null);
    assertNull(jso.getParameters());
  }
  
  /*
   * Test counting the number of component connections. If connections aren't 
   * defined, the count should be zero.
   */
  @Test
  public void testCountZeroConnections() {
    int zero = 0;
    assertEquals(zero, jso.nConnections());
  }

  /*
   * Test counting the number of component connections.
   */
  @Test
  public void testCountConnections() {
    assertEquals(nConnections, jsoFull.nConnections());
  }
  
  /*
   * Test getting connections. If connections aren't defined, the result should
   * be null.
   */
  @Test
  public void testGetZeroConnections() {
    assertNull(jso.getConnections());
  }

  /*
   * Test getting connections.
   */
  @Test
  public void testGetConnections() {
    assertEquals(connections, jsoFull.getConnections());
  }

  /*
   * Test setting and getting connections. It would be preferable to have a 
   * distinct test case for only setting connections.
   */
  @Test
  public void testSetAndGetConnections() {
    jso.setConnections(connections);
    assertEquals(connections, jso.getConnections());
  }

  /*
   * Test setting connections to null and retrieving.
   */
  @Test
  public void testSetNullConnections() {
    jso.setConnections(null);
    assertNull(jso.getConnections());
  }
  
}
