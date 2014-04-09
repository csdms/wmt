/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.security;

/**
 * A class for working with usernames and passwords in WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class Security {

  private String hpccHostname;
  private String hpccUsername; // for the HPCC where the model is run
  private String hpccPassword;
  private String wmtUsername;  // for logging into WMT
  private String wmtPassword;
  
  /**
   * Initializes the Security object used in a WMT session.
   */
  public Security() {
  }
  
  /**
   * Returns the hostname of the machine where the user wants the model to be
   * run.
   */
  public String getHpccHostname() {
    return hpccHostname;
  }

  /**
   * Stores the hostname of the machine where the user wants the model to be
   * run.
   * 
   * @param hpccHostname
   */
  public void setHpccHostname(String hostname) {
    this.hpccHostname = hostname;
  }

  /**
   * Returns the user's username for the host on which the model is to be run.
   */
  public String getHpccUsername() {
    return hpccUsername;
  }

  /**
   * Stores the user's username for the host on which the model is to be run.
   * 
   * @param hpccUsername
   */
  public void setHpccUsername(String username) {
    this.hpccUsername = username;
  }

  /**
   * Returns the user's password for the host on which the model is to be run.
   */
  public String getHpccPassword() {
    return hpccPassword;
  }

  /**
   * Stores the user's password for the host on which the model is to be run.
   * 
   * @param hpccPassword
   */
  public void setHpccPassword(String password) {
    this.hpccPassword = password;
  }

  /**
   * Returns the user's login name for the WMT client.
   */
  public String getWmtUsername() {
    return wmtUsername;
  }

  /**
   * Stores the user's login name for the WMT client.
   * 
   * @param wmtUsername
   */
  public void setWmtUsername(String wmtUsername) {
    this.wmtUsername = wmtUsername;
  }

  /**
   * Returns the user's password for the WMT client.
   */
  public String getWmtPassword() {
    return wmtPassword;
  }

  /**
   * Stores the user's password for the WMT client.
   * 
   * @param wmtPassword
   */
  public void setWmtPassword(String wmtPassword) {
    this.wmtPassword = wmtPassword;
  }
}
