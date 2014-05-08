/**
 * <License>
 */
package edu.colorado.csdms.wmt.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

import edu.colorado.csdms.wmt.client.data.ComponentJSO;

/**
 * Tests for {@link ComponentJSO}. JUnit integration is provided by extending
 * {@link GWTTestCase}.
 * 
 * @see http://www.gwtproject.org/doc/latest/DevGuideTesting.html
 * @see http://www.gwtproject.org/doc/latest/tutorial/JUnit.html
 * @see http://blog.danielwellman.com/2008/08/testing-json-parsing-using-javascript-overlay-types-in-gwt-15.html
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentJSOTest extends GWTTestCase {

  private ComponentJSO componentJSO;
  private String id;
  private String componentClass;
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
   * {@link ComponentJSO} object for testing.
   * 
   * @param id
   * @param componentClass
   * @param name
   */
  private native ComponentJSO testComponentJSO(String id, String componentClass,
      String name) /*-{
		return {
			"id" : id,
			"class" : componentClass,
			"name" : name
		}
  }-*/;  
  
  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    id = "child";
    componentClass = "Child";
    name = "Child_0";
    componentJSO = testComponentJSO(id, componentClass, name);
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  @Test
  public void testGetId() {
    assertEquals(id, componentJSO.getId());
  }

  @Test
  public void testGetComponentClass() {
    assertEquals(componentClass, componentJSO.getComponentClass());
  }
  
  @Test
  public void testGetName() {
    assertEquals(name, componentJSO.getName());
  }
}
