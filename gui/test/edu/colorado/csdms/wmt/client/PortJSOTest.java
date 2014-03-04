package edu.colorado.csdms.wmt.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

import edu.colorado.csdms.wmt.client.data.PortJSO;

/**
 * Tests for {@link PortJSO}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class PortJSOTest extends GWTTestCase {

  private PortJSO portJSO;
  private String id;
  private Boolean required;
  
  @Override
  public String getModuleName() {
     return "edu.colorado.csdms.wmt.WMT";
  }
  
  // A JSNI method that defines a fixture for the tests.
  private native PortJSO testPortJSO(String id, boolean required) /*-{ 
    return {"required":required, "id":id} 
  }-*/;  
  
  @Before
  @Override
  protected void gwtSetUp() throws Exception {
    id = "test";
    required = true;
    portJSO = testPortJSO(id, required);
  }

  @After
  @Override
  protected void gwtTearDown() throws Exception {
  }

  @Test
  public void testGetId() {
    assertEquals(id, portJSO.getId());
  }
  
  @Test
  public void testSetId() {
    String newId = "foo";
    portJSO.setId(newId);
    assertEquals(newId, portJSO.getId());
  }
  
  @Test
  public void testIsRequired() {
    assertTrue(portJSO.isRequired());
  }

  @Test
  public void testSetIsRequired() {
    Boolean isRequired = false;
    portJSO.isRequired(isRequired);
//    assertFalse(portJSO.isRequired());
  }
}
