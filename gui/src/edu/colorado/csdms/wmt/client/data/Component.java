package edu.colorado.csdms.wmt.client.data;

/**
 * A class to model a component. It acts as a wrapper around a ComponentJSO
 * object, which is a read-only data object.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class Component {

  private String id;
  private String name;
  private String url; // use GWT URL instead?
  private Port[] provides;
  private Port[] uses;

  /**
   * Creates an empty Component. Attributes can be set on it later.
   */
  public Component() {
  }

  /**
   * Creates a Component with only a name and no other attributes (though the
   * other attributes can be set at a later point).
   * 
   * @param name a String giving the name for the Component
   */
  public Component(String name) {
    this.setName(name);
  }

  /**
   * Makes a Component the best way -- from an existing ComponentJSO object.
   * 
   * @param componentJso a ComponentJSO object read from a component JSON file
   */
  public Component(ComponentJSO componentJso) {

    this.setId(componentJso.getId());
    this.setName(componentJso.getName());
    this.setUrl(componentJso.getURL());

    Integer nProvidesPorts = componentJso.getPortsProvided().length();
    Port[] provides = new Port[nProvidesPorts];
    for (int i = 0; i < nProvidesPorts; i++) {
      provides[i] = new Port(componentJso.getPortsProvided().get(i));
    }
    this.setProvidesPorts(provides);

    Integer nUsesPorts = componentJso.getPortsUsed().length();
    Port[] uses = new Port[nUsesPorts];
    for (int i = 0; i < nUsesPorts; i++) {
      uses[i] = new Port(componentJso.getPortsUsed().get(i));
    }
    this.setUsesPorts(uses);
  }
  
  /**
   * Makes a Component the hard way -- by specifying up front all the
   * attributes that define it.
   * 
   * @param id
   * @param name
   * @param url
   * @param provides
   * @param uses
   */
  public Component(String id, String name, String url, Port[] provides,
      Port[] uses) {

    this.setId(id);
    this.setName(name);
    this.setUrl(url);
    this.setProvidesPorts(provides);
    this.setUsesPorts(uses);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Port[] getProvidesPorts() {
    return provides;
  }

  public void setProvidesPorts(Port[] provides) {
    this.provides = provides;
  }

  public Port[] getUsesPorts() {
    return uses;
  }

  public void setUsesPorts(Port[] uses) {
    this.uses = uses;
  }

  /**
   * A static utility method that makes a Component with informational text.
   */
  public static Component makeInfoComponent() {
    return new Component(
        "<i class='fa fa-plus-square fa-fw'></i> Drag component here");
  }
  
  /**
   * Examples and unit tests.
   * 
   * @param args
   */
  public static void main(String[] args) {

    // Zero-elt ctor.
    Component c = new Component();
    c.setName("HydroTrend");
    System.out.println(c.toString());
    System.out.println(c.getName());
    System.out.println(c.getUrl());

    // One-elt ctor.
    Component d = new Component("Avulsion");
    System.out.println(d.getName());

    // Five-elt ctor.
    Port elevation = new Port("elevation");
    Port river = new Port("river");
    Port waves = new Port("waves");
    Port[] provides = {elevation};
    Port[] uses = {river, waves};
    Component e =
        new Component("cem", "CEM", "http://csdms.colorado.edu/wiki/Model:CEM",
            provides, uses);
    System.out.println(e.getName());
    System.out.println(e.getUrl());
    for (int i = 0; i < uses.length; i++) {
      System.out.println(e.getUsesPorts()[i].getId());
    }
  }
}
