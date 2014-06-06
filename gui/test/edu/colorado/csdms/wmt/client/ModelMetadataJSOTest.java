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

import edu.colorado.csdms.wmt.client.data.ModelMetadataJSO;

/**
 * Tests for {@link ModelMetadataJSO}. JUnit integration is provided by
 * extending {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelMetadataJSOTest extends GWTTestCase {

  private ModelMetadataJSO jso;
  private String owner;
  private int id;
  private String name;

  /**
   * The module that sources this class. Must be present.
   */
  @Override
  public String getModuleName() {
     return "edu.colorado.csdms.wmt.WMT";
  }

  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link ModelMetadataJSO} object for testing.
   * 
   * @param owner
   * @param id
   * @param name
   * @return
   */
  private native ModelMetadataJSO testModelMetadataJSO(String owner, int id,
      String name) /*-{
		return {
			"owner" : owner,
			"id" : id,
			"name" : name
		};
  }-*/;  
  
  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    owner = "foo@bar.com";
    id = 42;
    name = "Test";
    jso = testModelMetadataJSO(owner, id, name);
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  /*
   * Test getting owner.
   */
  @Test
  public void testGetOwner() {
    assertEquals(owner, jso.getOwner());
  }

  /*
   * Test setting owner.
   */
  @Test
  public void testSetOwner() {
    String newOwner = "fu@baz.org";
    jso.setOwner(newOwner);
    assertEquals(newOwner, jso.getOwner());
  }

  /*
   * Test getting id.
   */
  @Test
  public void testGetId() {
    assertEquals(id, jso.getId());
  }

  /*
   * Test setting id.
   */
  @Test
  public void testSetId() {
    int newId = 17;
    jso.setId(newId);
    assertEquals(newId, jso.getId());
  }

  /*
   * Test getting name.
   */
  @Test
  public void testGetName() {
    assertEquals(name, jso.getName());
  }

  /*
   * Test setting name.
   */
  @Test
  public void testSetLabel() {
    String newName = "Tset";
    jso.setName(newName);
    assertEquals(newName, jso.getName());
  }

}
