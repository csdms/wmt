package edu.colorado.csdms.wmt.client.data;

/**
 * A class to model a port in the WMT GUI. It acts as a wrapper around the data
 * in a PortJSO object.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class Port {

  private String id; // always lowercase
  private Boolean required = false;

  /**
   * Creates an empty Port. Attributes can be set on it later.
   */
  public Port() {
  }

  /**
   * Creates a Port with the given id. It's set as optional, by default.
   * 
   * @param id a variable_case String giving the id for the Port
   */
  public Port(String id) {
    this.setId(id);
  }

  /**
   * Creates a Port from a PortJSO object.
   * 
   * @param portJso a PortJSO object read from a component JSON file
   */
  public Port(PortJSO portJso) {
    this.setId(portJso.getId());
    this.isRequired(portJso.isRequired());
  }

  /**
   * Creates a Port with the given id and required/optional attribute.
   * 
   * @param id a variable_case String giving the id for the Port
   * @param required whether the Port is required or optional
   */
  public Port(String id, Boolean required) {
    this.setId(id);
    this.isRequired(required);
  }

  /**
   * Returns the id of the Port as a String.
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id of the Port. Always lowercase.
   * 
   * @param id a variable_case String giving the id for the Port
   */
  public void setId(String id) {
    this.id = id.toLowerCase();
  }

  /**
   * Returns the required/optional setting for the Port. (required = true)
   * 
   * @return
   */
  public Boolean isRequired() {
    return required;
  }

  /**
   * Sets whether the Port is required (true) or optional (false).
   * 
   * @param required
   */
  public void isRequired(Boolean required) {
    this.required = required;
  }

  /**
   * Examples and unit tests.
   * 
   * @param args
   */
  public static void main(String[] args) {

    // Zero-elt ctor.
    Port p0 = new Port();
    p0.setId("discharge");
    System.out.println(p0.toString());
    System.out.println(p0.getId());

    // One-elt ctor.
    Port p1 = new Port("waves");
    System.out.println(p1.getId());
    System.out.println(p1.isRequired().toString());

    // Two-elt ctor.
    Port p2 = new Port("elevation", true);
    System.out.println(p2.getId());
    System.out.println(p2.isRequired().toString());
  }
}
