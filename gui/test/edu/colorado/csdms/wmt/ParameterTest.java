/**
 * 
 */
package edu.colorado.csdms.wmt;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.colorado.csdms.wmt.client.data.Parameter;
import edu.colorado.csdms.wmt.client.data.Value;

/**
 * Tests for {@link Parameter}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ParameterTest {

  private Parameter parameter;
  private String key;
  private String name;
  private String description;
  private Value value;
  
  @Before
  public void setUp() throws Exception {
    parameter = new Parameter();
    key = "key";
    parameter.setKey(key);
    name = "Name";
    parameter.setName(name);
    description = "Parameter description";
    parameter.setDescription(description);
    value = new Value();
    parameter.setValue(value);
  }

  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link edu.colorado.csdms.wmt.client.data.Parameter#Parameter()}.
   */
  @Test
  public void testParameterZeroArgCtr() {
    Parameter parameter0 = new Parameter();
    assertNotNull(parameter0);
    assertNull(parameter0.getKey());
    assertNull(parameter0.getName());
    assertNull(parameter0.getDescription());
    assertNull(parameter0.getValue());
  }

  /**
   * Test method for {@link edu.colorado.csdms.wmt.client.data.Parameter#getKey()}.
   */
  @Test
  public void testGetKey() {
    assertEquals(key, parameter.getKey());
  }

  /**
   * Test method for {@link edu.colorado.csdms.wmt.client.data.Parameter#setKey(java.lang.String)}.
   */
  @Test
  public void testSetKey() {
    String newKey = "foo";
    parameter.setKey(newKey);
    assertEquals(newKey, parameter.getKey());
    parameter.setKey(newKey.toUpperCase()); // should be lowercased
    assertEquals(newKey, parameter.getKey());
  }

  /**
   * Test method for {@link edu.colorado.csdms.wmt.client.data.Parameter#getName()}.
   */
  @Test
  public void testGetName() {
    assertEquals(name, parameter.getName());
  }

  /**
   * Test method for
   * {@link edu.colorado.csdms.wmt.client.data.Parameter#setName(java.lang.String)}
   * .
   */
  @Test
  public void testSetName() {
    String newName = "Foo";
    parameter.setName(newName);
    assertEquals(newName, parameter.getName());
  }

  /**
   * Test method for {@link edu.colorado.csdms.wmt.client.data.Parameter#getDescription()}.
   */
  @Test
  public void testGetDescription() {
    assertEquals(description, parameter.getDescription());
  }

  /**
   * Test method for {@link edu.colorado.csdms.wmt.client.data.Parameter#setDescription(java.lang.String)}.
   */
  @Test
  public void testSetDescription() {
    String newDescription = "Foo bar baz";
    parameter.setDescription(newDescription);
    assertEquals(newDescription, parameter.getDescription());
  }

  /**
   * Test method for {@link edu.colorado.csdms.wmt.client.data.Parameter#getValue()}.
   */
  @Test
  public void testGetValue() {
    assertEquals(value, parameter.getValue());
  }

  /**
   * Test method for {@link edu.colorado.csdms.wmt.client.data.Parameter#setValue(edu.colorado.csdms.wmt.client.data.Value)}.
   */
  @Test
  public void testSetValue() {
    Value newValue = new Value();
    parameter.setValue(newValue);
    assertEquals(newValue, parameter.getValue());
  }
}
