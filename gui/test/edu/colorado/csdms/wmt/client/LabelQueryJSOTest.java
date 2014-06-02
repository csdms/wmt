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

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.junit.client.GWTTestCase;

import edu.colorado.csdms.wmt.client.data.LabelQueryJSO;

/**
 * Tests for {@link LabelQueryJSO}. JUnit integration is provided by
 * extending {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @see http
 *      ://blog.danielwellman.com/2008/08/testing-json-parsing-using-javascript
 *      -overlay-types-in-gwt-15.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LabelQueryJSOTest extends GWTTestCase {

  private LabelQueryJSO jso;
  private JsArrayInteger ids;
  
  /**
   * The module that sources this class. Must be present.
   */
  @Override
  public String getModuleName() {
     return "edu.colorado.csdms.wmt.WMT";
  }
  
  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link LabelQueryJSO} object for testing.
   * 
   * @param ids
   */
  private native LabelQueryJSO testLabelModelQueryJSO(JsArrayInteger ids) /*-{
		return ids;
  }-*/;  
  
  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    ids = (JsArrayInteger) JsArrayInteger.createObject();
    ids.setLength(3);
    ids.push(4);
    ids.push(3);
    ids.push(12);
    jso = testLabelModelQueryJSO(ids);
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  // Test the length of the array.
  @Test
  public void testLength() {
    int arrayLength = 3;
    assertEquals(arrayLength, jso.getIds().length());
  }

  // Test whether all ids can be accessed.
  @Test
  public void testGetIds() {
    assertEquals(ids, jso.getIds());
  }
  
  // Test whether a single id can be accessed.
  @Test
  public void testGetSingleId() {
    int index = 0;
    assertEquals(ids.get(index), jso.getIds().get(index));
  }
}
