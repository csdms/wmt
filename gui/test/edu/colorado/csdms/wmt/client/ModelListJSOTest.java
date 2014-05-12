/**
 * <License>
 */
package edu.colorado.csdms.wmt.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.junit.client.GWTTestCase;

import edu.colorado.csdms.wmt.client.data.ModelListJSO;

/**
 * Tests for {@link ModelListJSO}. JUnit integration is provided by extending
 * {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelListJSOTest extends GWTTestCase {

  private ModelListJSO jso;
  private String name;
  private int id;
  private JsArray<ModelListJSO> jsoList;

  /**
   * The module that sources this class. Must be present.
   */
  @Override
  public String getModuleName() {
    return "edu.colorado.csdms.wmt.WMT";
  }

  /**
   * A JSNI method that defines a fixture for the tests. Returns a
   * {@link ModelListJSO} object for testing.
   * 
   * @param ids
   */
  private native ModelListJSO testModelListJSO(String name, int id) /*-{
		return {
			"name" : name,
			"id" : id
		};
  }-*/;

  @SuppressWarnings("unchecked")
  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    name = "HydroTrend and Friends";
    id = 5;
    jso = testModelListJSO(name, id);
    
    jsoList = (JsArray<ModelListJSO>) ModelListJSO.createObject();
    jsoList.setLength(3);
    jsoList.push(jso);
    jsoList.push(testModelListJSO("foo", 42));
    jsoList.push(testModelListJSO("bar", 1));
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  // Test getting the name of the model.
  @Test
  public void testGetName() {
    assertEquals(name, jso.getName());
  }

  // Test setting the name of the model.
  @Test
  public void testSetName() {
    String newName = "Foo";
    jso.setName(newName);
    assertEquals(newName, jso.getName());
  }

  // Test getting the id of the model.
  @Test
  public void testGetId() {
    assertEquals(id, jso.getModelId());
  }
  
  // Test the length of the array.
  @Test
  public void testLength() {
    int arrayLength = 3;
    assertEquals(arrayLength, jsoList.length());
  }

  // Test whether a single model can be retrieved.
//  @Test
//  public void testGetSingleId() {
//    int index = 0;
//    assertEquals(jso, jsoList.get(index));
//  }
}
