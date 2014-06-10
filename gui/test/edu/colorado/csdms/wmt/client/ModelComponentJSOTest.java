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

import edu.colorado.csdms.wmt.client.data.ModelComponentJSO;

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
  private String id;
  private String className;
  private boolean isDriver;

  /**
   * The module that sources this class. Must be present.
   */
  @Override
  public String getModuleName() {
    return "edu.colorado.csdms.wmt.WMT";
  }
  
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

  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    id = "avulsion";
    className = "Avulsion";
    isDriver = false;
    jso = testModelComponentJSO(id, className);
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
}
