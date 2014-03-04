package edu.colorado.csdms.wmt.client;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.colorado.csdms.wmt.client.data.Value;

/**
 * Tests for {@link Value}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ValueTest {

  private Value value;
  private String type;
  private String units;
  private String defaultValue;
  private String minValue;
  private String maxValue;
//  private String[] choices;
  
  @Before
  public void setUp() throws Exception {
    type = "test";
    units = "test";
    defaultValue = "test";
    minValue = "test";
    maxValue = "test";
//    choices = new String[] {"test"};
    value = new Value();
    value.setType(type);
    value.setUnits(units);
    value.setDefaultValue(defaultValue);
    value.setMinValue(minValue);
    value.setMaxValue(maxValue);
//    value.setChoices(choices);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testValueZeroArgCtr() {
    Value value0 = new Value();
    assertNotNull(value0);
    assertNull(value0.getType());
    assertNull(value0.getUnits());
    assertNull(value0.getDefaultValue());
    assertNull(value0.getMinValue());
    assertNull(value0.getMaxValue());
    assertNull(value0.getChoices());    
  }

  @Test
  public void testValueJSOCtr() {
    // TODO Complete with GWTTestCase
  }

  @Test
  public void testGetType() {
    assertEquals(type, value.getType());
  }

  @Test
  public void testSetType() {
    String newType = "foo";
    value.setType(newType);
    assertEquals(newType, value.getType());
  }

  @Test
  public void testGetUnits() {
    assertEquals(units, value.getUnits());
  }

  @Test
  public void testSetUnits() {
    String newUnits = "foo";
    value.setUnits(newUnits);
    assertEquals(newUnits, value.getUnits());
  }

  @Test
  public void testGetDefaultValue() {
    assertEquals(defaultValue, value.getDefaultValue());
  }

  @Test
  public void testSetDefaultValue() {
    String newDefault = "foo";
    value.setDefaultValue(newDefault);
    assertEquals(newDefault, value.getDefaultValue());
  }

  @Test
  public void testGetMinValue() {
    assertEquals(minValue, value.getMinValue());
  }

  @Test
  public void testSetMinValue() {
    String newMin = "foo";
    value.setMinValue(newMin);
    assertEquals(newMin, value.getMinValue());
  }

  @Test
  public void testGetMaxValue() {
    assertEquals(maxValue, value.getMaxValue());
  }

  @Test
  public void testSetMaxValue() {
    String newMax = "foo";
    value.setMaxValue(newMax);
    assertEquals(newMax, value.getMaxValue());
  }

  @Test
  public void testGetChoices() {
    // TODO
  }

  @Test
  public void testSetChoices() {
    // TODO
  }

}
