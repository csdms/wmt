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

import edu.colorado.csdms.wmt.client.data.ModelComponentConnectionsJSO;

/**
 * Tests for {@link ModelComponentConnectionsJSO}. JUnit integration is provided
 * by extending {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelComponentConnectionsJSOTest extends GWTTestCase {

  private static final String[] PORTS = {
      "river", "waves", "discharge", "elevation"};
  private static final String[] COMPONENTS = {
      "avulsion", "waves", null, "cem"};
  private static final int N_PORTS = PORTS.length;
  private String[] connections = new String[N_PORTS];
  private ModelComponentConnectionsJSO jso;
  private JsArrayString portsJso;
  private JsArrayString connectionsJso;

  /**
   * The module that sources this class. Must be present.
   */
  @Override
  public String getModuleName() {
    return "edu.colorado.csdms.wmt.WMT";
  }

  /**
   * A helper that converts a Java String array to a native JsArrayString.
   * 
   * @param input a String array
   * @return a JsArrayString
   * @see http://stackoverflow.com/a/22168025
   */
  public static JsArrayString toJsArray(String[] input) {
    JsArrayString jsArrayString = JsArrayString.createArray().cast();
    for (String s : input) {
      jsArrayString.push(s);
    }
    return jsArrayString;
  }
  
  /**
   * A helper for making the array of connections.
   */
  public void makeConnections() {
    for (int i = 0; i < PORTS.length; i++) {
      connections[i] =
          (COMPONENTS[i] != null) ? PORTS[i] + "@" + COMPONENTS[i] : null;
    }
  }
  
  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link ModelComponentConnectionsJSO} object for testing.
   */
  private native ModelComponentConnectionsJSO testModelComponentConnectionsJSO(
      JsArrayString keys, JsArrayString values) /*-{
    var connections = {};
		for (var i = 0; i < keys.length; i++) {
			connections[keys[i]] = values[i]; 
		}
		return connections;
  }-*/;

  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    makeConnections();
    portsJso = toJsArray(PORTS);
    connectionsJso = toJsArray(connections);
    jso = testModelComponentConnectionsJSO(portsJso, connectionsJso);
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  /*
   * Test the number of connections through the port ids.
   */
  @Test
  public void testNumberOfPorts() {
    assertEquals(N_PORTS, jso.getPortIds().length());
  }

  /*
   * Test getting all port ids. There's no assertArrayEquals in GWTTestCase, so
   * compare "join"-ed Strings.
   */
  @Test
  public void testGetPortIds() {
    assertEquals(portsJso.join(), jso.getPortIds().join());
  }

  /*
   * Test getting all connections. There's no assertArrayEquals in GWTTestCase,
   * so compare "join"-ed Strings.
   */
  @Test
  public void testGetConnections() {
    assertEquals(toJsArray(COMPONENTS).join(), jso.getConnections().join());
  }

  /*
   * Test getting a single component connection, given a port.
   */
  @Test
  public void testGetSingleConnection() {
    Integer index = 1; // arbitrary, but between 0 and N_PORTS.
    String portId = PORTS[index];
    String componentId = COMPONENTS[index];
    assertEquals(componentId, jso.getConnection(portId));
  }
  
  /*
   * Test adding a new connection. This may not be wholly independent from
   * #testGetSingleConnection.
   */
  @Test
  public void testAddConnection() {
    String portId = "foo";
    String componentId = "bar";
    jso.addConnection(portId, componentId);
    assertEquals(componentId, jso.getConnection(portId));
  }
  
  /*
   * Test adding and getting a null connection. (Should break this into two
   * tests.)
   */
  @Test
  public void testAddNullConnection() {
    String portId = "baz";
    String componentId = null;
    jso.addConnection(portId, componentId);
    assertNull(jso.getConnection(portId));
  }
}
