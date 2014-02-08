/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

/**
 * A class to model a parameter for a WMT component. It acts as a wrapper
 * around a ParameterJSO object, which is a read-only data object.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class Parameter {

  private String key; // always lowercase
  private String name;
  private String description;
  private Value value;
  
  /**
   * Creates an empty Parameter. Attributes can be set on it afterward.
   */
  public Parameter() {
  }

  /**
   * Creates a new Parameter from a read-only ParameterJSO object.
   * 
   * @param parameterJSO a ParameterJSO object read from a parameter JSON file
   */
  public Parameter(ParameterJSO parameterJSO) {
    setKey(parameterJSO.getKey());
    setName(parameterJSO.getName());
    setDescription(parameterJSO.getDescription());
    this.value = new Value(parameterJSO.getValue());
  }

  /**
   * Gets the key -- a variable_case identifier -- for the Parameter.
   * 
   * @return the key, as a String
   */
  public String getKey() {
    return key;
  }

  /**
   * Sets the key -- a variable_case identifier -- for the Parameter.
   * 
   * @param key the key to set, as a String
   */
  public void setKey(String key) {
    this.key = key.toLowerCase();
  }

  /**
   * Gets the human-readable name of the Parameter.
   * 
   * @return the name, as a String
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the human-readable name of the Parameter.
   * 
   * @param name the name to set, as a String
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the long description of the Parameter.
   * 
   * @return the description, as a String
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the long description of the Parameter.
   * 
   * @param description the description to set, as a String
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the Value object associated with the Parameter.
   * 
   * @return a Value object
   */
  public Value getValue() {
    return value;
  }

  /**
   * Sets the Value object associated with the Parameter.
   * 
   * @param value a Value object to set
   */
  public void setValue(Value value) {
    this.value = value;
  }
  
  /**
   * Examples and unit tests.
   */
  public static void main() {
    
    // Zero-elt ctr.
    Parameter p0 = new Parameter();
    p0.setKey("simulation_name");
    System.out.println(p0.getKey());
  }
}
