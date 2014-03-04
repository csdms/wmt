package edu.colorado.csdms.wmt.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.colorado.csdms.wmt.client.data.Component;
import edu.colorado.csdms.wmt.client.data.Port;

/**
 * Tests for {@link Component}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentTest {
  
  private Component component;
  private String id;
  private String name;
  private String url;
  private Port[] provides;
  private Port[] uses;

  @Before
  public void setUp() throws Exception {
    id = "test";
    name = "Test";
    url = "www.foo.org";
    provides = new Port[] {new Port()};
    uses = new Port[] {new Port()};
    component = new Component(id, name, url, provides, uses);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testComponentZeroArgCtr() {
    Component component0 = new Component();
    assertNotNull(component0);
    assertNull(component0.getId());
    assertNull(component0.getName());
    assertNull(component0.getUrl());
    assertNull(component0.getProvidesPorts());
    assertNull(component0.getUsesPorts());    
  }

  @Test
  public void testComponentOneArgCtr() {
    Component component1 = new Component(name);
    assertNotNull(component1);
    assertNull(component1.getId());
    assertEquals(name, component1.getName());
    assertNull(component1.getUrl());
    assertNull(component1.getProvidesPorts());
    assertNull(component1.getUsesPorts());    
  }

  @Test
  public void testComponentJSOCtr() {
    // TODO Complete with GWTTestCase
  }

  @Test
  public void testComponentFiveArgCtr() {
    // Done in #setUp
  }

  @Test
  public void testGetId() {
    assertEquals(id, component.getId());
  }

  @Test
  public void testSetId() {
    String newId = "foo";
    component.setId(newId);
    assertEquals(newId, component.getId());
  }

  @Test
  public void testGetName() {
    assertEquals(name, component.getName());
  }

  @Test
  public void testSetName() {
    String newName = "Foo";
    component.setName(newName);
    assertEquals(newName, component.getName());
  }

  @Test
  public void testGetUrl() {
    assertEquals(url, component.getUrl());
  }

  @Test
  public void testSetUrl() {
    String newUrl = "www.colorado.edu";
    component.setUrl(newUrl);
    assertEquals(newUrl, component.getUrl());
  }

  @Test
  public void testGetProvidesPorts() {
    assertArrayEquals(provides, component.getProvidesPorts());
  }

  @Test
  public void testSetProvidesPorts() {
    Port[] newPorts = new Port[] {new Port()};
    component.setProvidesPorts(newPorts);
    assertArrayEquals(newPorts, component.getProvidesPorts());
  }

  @Test
  public void testGetUsesPorts() {
    assertArrayEquals(uses, component.getUsesPorts());
  }

  @Test
  public void testSetUsesPorts() {
    Port[] newPorts = new Port[] {new Port()};
    component.setUsesPorts(newPorts);
    assertArrayEquals(newPorts, component.getUsesPorts());
  }

  @Test
  public void testMakeInfoComponent() {
    Component info = Component.makeInfoComponent();
    assertNotNull(info);
  }
}
