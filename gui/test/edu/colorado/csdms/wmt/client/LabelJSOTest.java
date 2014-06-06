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

import edu.colorado.csdms.wmt.client.data.LabelJSO;

/**
 * Tests for {@link LabelJSO}. JUnit integration is provided by extending
 * {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LabelJSOTest extends GWTTestCase {

  private LabelJSO jso;
  private String owner;
  private String tag;
  private int id;
  private boolean selected;
  
  /**
   * The module that sources this class. Must be present.
   */
  @Override
  public String getModuleName() {
     return "edu.colorado.csdms.wmt.WMT";
  }

  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link LabelJSO} object for testing.
   * <p>
   * Note that "selected" member isn't present.
   * 
   * @param owner
   * @param tag
   * @param id
   * @return
   */
  private native LabelJSO testLabelJSO(String owner, String tag, int id) /*-{
		return {
		  "owner": owner,
		  "tag": tag,
		  "id": id
		  };
  }-*/;  
  
  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    owner = "foo@bar.com";
    tag = "Test";
    id = 42;
    selected = true; // initially unused
    jso = testLabelJSO(owner, tag, id);
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
   * Test getting tag/label.
   */
  @Test
  public void testGetLabel() {
    assertEquals(tag, jso.getLabel());
  }

  /*
   * Test setting tag/label.
   */
  @Test
  public void testSetLabel() {
    String newLabel = "hoopy frood";
    jso.setLabel(newLabel);
    assertEquals(newLabel, jso.getLabel());
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
   * Test for an unset "selected" member -- the return from LabelJSO#isSelected
   * should be false. This is the default state, since "selected" isn't included
   * in a LabelJSO from the server; it's added later in the UI code.
   */
  @Test
  public void testGetSelectedUninitialized() {
    assertFalse(jso.isSelected());
  }

  /*
   * Test getting/setting "selected" member.
   */
  @Test
  public void testSetSelected() {
    jso.isSelected(selected);
    assertTrue(jso.isSelected());
  }
}
