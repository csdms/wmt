/**
 * <License>
 */
package edu.colorado.csdms.wmt;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.colorado.csdms.wmt.client.data.Port;

/**
 * Tests for {@link Port}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class PortTest {

  private Port port;
  private String id;
  
  @Before
  public void setUp() throws Exception {
    id = "test";
    port = new Port(id, true);
  }

  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link edu.colorado.mpiper.client.Port#Port()}.
   */
  @Test
  public void testPortZeroArgCtr() {
    Port port0 = new Port();
    assertNotNull(port0);
    assertNull(port0.getId());
    assertFalse(port0.isRequired());
  }

  /**
   * Test method for {@link edu.colorado.mpiper.client.Port#Port(java.lang.String)}.
   */
  @Test
  public void testPortOneArgCtr() {
    Port port1 = new Port(id);
    assertNotNull(port1);
    assertEquals(id, port1.getId());
    assertFalse(port1.isRequired());
  }

  /**
   * Test method for {@link edu.colorado.mpiper.client.Port#getId()}.
   */
  @Test
  public void testGetId() {
    assertEquals(id, port.getId());
  }

  /**
   * Test method for {@link edu.colorado.mpiper.client.Port#setId(java.lang.String)}.
   */
  @Test
  public void testSetId() {
    port.setId("foo");
    assertEquals("foo", port.getId());
  }

  /**
   * Test method for {@link edu.colorado.mpiper.client.Port#isRequired()}.
   */
  @Test
  public void testIsRequired() {
    assertTrue(port.isRequired());
  }

  /**
   * Test method for {@link edu.colorado.mpiper.client.Port#isRequired(java.lang.Boolean)}.
   */
  @Test
  public void testIsRequiredBoolean() {
    port.isRequired(false);
    assertFalse(port.isRequired());
  }

}
