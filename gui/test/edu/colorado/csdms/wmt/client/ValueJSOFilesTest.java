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

import edu.colorado.csdms.wmt.client.data.ValueJSO;

/**
 * Tests for {@link ValueJSO} of type "file". JUnit integration is provided by
 * extending {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ValueJSOFilesTest extends GWTTestCase {

  private static final String[] FILENAMES = {
      "danube.hyps", "eel.hyps", "ganges.hyps", "lanyang.hyps", "po.hyps",
      "waipaoa.hyps"};
  private static final int ARRAY_LENGTH = FILENAMES.length;
  private ValueJSO jso;
  private String type = "files";
  private String defaultValue;
  private JsArrayString files;

  /**
   * The module that sources this class. Must be present.
   */
  @Override
  public String getModuleName() {
    return "edu.colorado.csdms.wmt.WMT";
  }

  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link ValueJSO} object for testing.
   * 
   * @param defaultValue
   * @param files
   * @param type
   */
  private native ValueJSO testValueJSO(String defaultValue,
      JsArrayString files, String type) /*-{
		return {
			"default" : defaultValue,
			"files" : files,
			"type" : type
		};
  }-*/;

  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    files = JsArrayString.createObject().cast();
    files.setLength(ARRAY_LENGTH);
    for (String file : FILENAMES) {
      files.push(file);
    }
    defaultValue = FILENAMES[0];
    jso = testValueJSO(defaultValue, files, type);
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  /*
   * Test getting type.
   */
  @Test
  public void testGetType() {
    assertEquals(type, jso.getType());
  }

  /*
   * Test the length of the files array.
   */
  @Test
  public void testLength() {
    assertEquals(ARRAY_LENGTH, jso.getFiles().length());
  }

  /*
   * Test getting default.
   */
  @Test
  public void testGetDefault() {
    assertEquals(defaultValue, jso.getDefault());
  }

  /*
   * Test setting default.
   */
  @Test
  public void testSetDefault() {
    String newDefault = "lanyang.hyps";
    jso.setDefault(newDefault);
    assertEquals(newDefault, jso.getDefault());
  }

  /*
   * Test getting units. Should return null if not present.
   */
  @Test
  public void testGetUnits() {
    assertNull(jso.getUnits());
  }

  /*
   * Test getting min. Should return null if not present.
   */
  @Test
  public void testGetMin() {
    assertNull(jso.getMin());
  }

  /*
   * Test getting max. Should return null if not present.
   */
  @Test
  public void testGetMax() {
    assertNull(jso.getMax());
  }

  /*
   * Test getting choices. Should return null if not present.
   */
  @Test
  public void testGetChoices() {
    assertNull(jso.getChoices());
  }

  /*
   * Test getting files.
   */
  @Test
  public void testGetFiles() {
    assertEquals(files, jso.getFiles());
  }

  /*
   * Test setting files.
   */
  @Test
  public void testSetFiles() {
    files.push("foo.hyps");
    jso.setFiles(files);
    assertEquals(files, jso.getFiles());
  }
}
