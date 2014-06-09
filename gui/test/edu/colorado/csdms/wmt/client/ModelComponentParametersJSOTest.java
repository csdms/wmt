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

import edu.colorado.csdms.wmt.client.data.ModelComponentParametersJSO;

/**
 * Tests for {@link ModelComponentParametersJSO}. JUnit integration is provided
 * by extending {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelComponentParametersJSOTest extends GWTTestCase {

  private static final String[] keys = {
      "simulation_name", "number_of_rows", "number_of_columns", "row_spacing",
      "column_spacing", "output_format"};
  private static final String[] values = {
      "CEM", "4200", "2100", "150", "300", "vtk"};
  private static final int N_PARAMETERS = keys.length;
  private ModelComponentParametersJSO jso;
  private JsArrayString keysJso;
  private JsArrayString valuesJso;

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
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link ModelComponentParametersJSO} object for testing.
   */
  private native ModelComponentParametersJSO testModelComponentParametersJSO(
      JsArrayString keys, JsArrayString values) /*-{
    var parameters = {};
		for (var i = 0; i < keys.length; i++) {
			parameters[keys[i]] = values[i]; 
		}
		return parameters;
  }-*/;

  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    keysJso = toJsArray(keys);
    valuesJso = toJsArray(values);
    jso = testModelComponentParametersJSO(keysJso, valuesJso);
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  // Test the number of parameters through the keys.
  @Test
  public void testNumberOfKeys() {
    assertEquals(N_PARAMETERS, jso.getKeys().length());
  }

  // Test the number of parameters through the values.
  @Test
  public void testNumberOfValues() {
    assertEquals(N_PARAMETERS, jso.getValues().length());
  }

}
