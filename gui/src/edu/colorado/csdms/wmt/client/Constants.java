/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 mcflugen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.colorado.csdms.wmt.client;

import com.google.gwt.core.client.GWT;

import edu.colorado.csdms.wmt.client.control.DataManager;

/**
 * A class that defines, as public static members, constants used in the WMT
 * client.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class Constants {

  // The CSDMS website, email, and wiki articles.
  public static final String CSDMS_HOME = "http://csdms.colorado.edu/";
  public static final String WMT_HELP = CSDMS_HOME + "wiki/WMT_help";
  public static final String WMT_TUTORIAL = CSDMS_HOME + "wiki/WMT_tutorial";
  public static final String CSDMS_EMAIL = "CSDMSsupport@colorado.edu";
  public static final String WMT_TITLE = "<h2>WMT</h2><p><b>The CSDMS Web"
      + " Modeling Tool</b></br>Version: " + DataManager.VERSION + "</p>";

  // This switch toggles API development and public mode.
  public static final Boolean USE_API_DEV_MODE = true;

  // By same-origin policy, must use HTTPS consistently across all URLs.
  public static final String BASE_URL = "https://csdms.colorado.edu/";
  public static final String API_URL = BASE_URL + "wmt-server/";
  public static final String API_DEV_URL = BASE_URL + "wmt/api-dev/";
  public static final String LOCAL_URL = GWT.getHostPageBaseURL();

  // API URLs for user login, logout, and status. Also, a cookie.
  public static final String NEW_USER_LOGIN_PATH = "account/new";
  public static final String LOGIN_PATH = "account/login";
  public static final String LOGOUT_PATH = "account/logout";
  public static final String USERNAME_PATH = "account/username";
  public static final String USERNAME_COOKIE = "__WMT_username";
  public static final Long COOKIE_DURATION = (long) (1000*60*60*24*14); // 2 wks

  // API URLs for labels.
  public static final String LABELS_LIST_PATH = "tag/list";
  public static final String LABELS_NEW_PATH = "tag/new";
  public static final String LABELS_DELETE_PATH = "tag/delete/";
  public static final String LABELS_MODEL_ADD_PATH = "tag/model/add";
  public static final String LABELS_MODEL_QUERY_PATH = "tag/model/query";
  public static final String LABELS_MODEL_GET_PATH = "tag/model/";
  
  // API URLs for components.
  public static final String COMPONENTS_LIST_PATH = "components/list";
  public static final String COMPONENTS_SHOW_PATH = "components/show/";

  // API URLs for models.
  public static final String MODELS_LIST_PATH = "models/list";
  public static final String MODELS_OPEN_PATH = "models/open/";
  public static final String MODELS_SHOW_PATH = "models/show/";
  public static final String MODELS_NEW_PATH = "models/new";
  public static final String MODELS_EDIT_PATH = "models/edit/";
  public static final String MODELS_DELETE_PATH = "models/delete/";
  public static final String MODELS_UPLOAD_PATH = "models/upload";

  // API URLs for running a model.
  public static final String RUN_NEW_PATH = "run/new";
  public static final String RUN_SHOW_PATH = "run/show";
  public static final String RUN_STAGE_PATH = "run/stage";
  public static final String RUN_LAUNCH_PATH = "run/launch";

  // Error and warning messages.
  public static String REQUEST_ERR_MSG = "Failed to send the request: ";
  public static String RESPONSE_ERR_MSG = "No match found in the response.";
  public static String CLOSE_MSG =
      "Any unsaved model data will be lost if this page"
          + " is reloaded or closed.";
  public static String LOGIN_ERR = "Please sign in with an email address"
      + " and a password.";
  public static String PASSWORD_ERR = "This email address is registered,"
      + " but the password is not valid.\n\nIf you've forgotten your password,"
      + " please contact CSDMS support (CSDMSsupport@colorado.edu)"
      + " for assistance.";
  public static String ADD_LABEL_ERR = "This label already exists."
      + " Please choose a different name.";
  public static String DELETE_LABEL_ERR = "This label cannot be deleted because"
      + " it is not owned by the current user.";
  public static String SIGN_IN_MSG = 
      WMT_TITLE + "<p>Please sign in to use WMT.</p>"
      + "<p>If this is your first time using WMT, create a<br>"
      + "sign in with an email address and a password.</p>"
      + "<p>For assistance, email <a href='mailto:" + Constants.CSDMS_EMAIL 
      + "'>" + Constants.CSDMS_EMAIL + "</a>.</p>";

  // Number of tries to fetch a component; a magic number.
  public static Integer RETRY_ATTEMPTS = 3;

  // Fractional size of viewEast in Perspective.
  public static Double VIEW_EAST_FRACTION = 0.60;

  // Width (in px) of splitter grabby bar.
  public static Integer SPLITTER_SIZE = 5;

  // Height (in px) of tab bars.
  public static Double TAB_BAR_HEIGHT = 40.0;

  // Standard dimensions for PopupPanels.
  public static String MENU_WIDTH = "220px"; // arbitrary, aesthetic
  public static String MENU_HEIGHT = "20em";

  // The default text displayed in the driver ComponentCell.
  public static String DRIVER = "driver";

  // The default model name and id.
  public static String DEFAULT_MODEL_NAME = "Model 0";
  public static Integer DEFAULT_MODEL_ID = -1;

  // The number of characters to display in a ComponentCell.
  public static Integer TRIM = 10;

  // Unicode representation.
  public static String ELLIPSIS = "\u2026";

  public static String ALL_COMPONENTS = "__all_components";

  // FontAwesome icons.
  public static String SIGN_IN = "<i class='fa fa-sign-in'></i> Sign In";
  public static String SIGN_OUT = "<i class='fa fa-sign-out'></i> Sign Out";
  public static String FA_OK = "<i class='fa fa-check'></i> ";
  public static String FA_CANCEL = "<i class='fa fa-ban'></i> ";
  public static String FA_OPEN = "<i class='fa fa-folder-open-o fa-fw'></i> ";
  public static String FA_SAVE = "<i class='fa fa-floppy-o fa-fw'></i> ";
  public static String FA_COPY = "<i class='fa fa-copy fa-fw'></i> ";
  public static String FA_DELETE = "<i class='fa fa-trash-o fa-fw'></i> ";
  public static String FA_RUN = "<i class='fa fa-play fa-fw'></i> ";
  public static String FA_STATUS = "<i class='fa fa-info fa-fw'></i> ";
  public static String FA_HELP = "<i class='fa fa-question fa-fw'></i> ";
  public static String FA_TAGS = "<i class='fa fa-tags fa-fw'></i> ";
  public static String FA_SELECT = "<i class='fa fa-plus'></i> ";
  public static String FA_ACTION = "<i class='fa fa-chevron-down'></i> ";
  public static String FA_WRENCH = "<i class='fa fa-wrench fa-fw'></i> ";
  public static String FA_CLOBBER = "<i class='fa fa-times fa-fw'></i> ";
  public static String FA_MORE = " <i class='fa fa-caret-down'></i>";
  public static String FA_COG = "<i class='fa fa-cog fa-fw'></i> ";
  public static String FA_COGS = "<i class='fa fa-cogs fa-fw'></i> ";
  public static String FA_USER = "<i class='fa fa-user fa-fw'></i> ";
  public static String FA_BEER = "<i class='fa fa-beer'></i> ";

  // Tooltip text strings.
  public static String MODEL_OPEN = "Open an existing model.";
  public static String MODEL_SAVE = "Save the current model to the server.";
  public static String MODEL_RUN = "Run the current model on a HPCC.";
  public static String MODEL_MORE = "See more options...";
  public static String MODEL_SAVE_AS = "Save a model with a new name.";
  public static String MODEL_DUPLICATE = "Make a duplicate of this model.";
  public static String MODEL_LABELS = "Manage the labels set on a model.";
  public static String MODEL_DELETE = "Delete a model from server.";
  public static String MODEL_RUN_STATUS = "Get the status of model run.";
  public static String MODEL_HELP = "View the help documents on using WMT.";
  public static String COMPONENT_SHOW =
      "View and edit this component's parameters.";
  public static String COMPONENT_INFO =
      "View information about this component.";
  public static String COMPONENT_INFO_1 =
      "View information about a component.";  
  public static String COMPONENT_DELETE =
      "Delete this component from the model.";
  public static String PARAMETER_RESET = "Reset all parameters for this"
      + " component to their default values.";
  public static String PARAMETER_VIEW_FILE = "View the input files for this"
      + " model component.";
  public static String PARAMETER_VIEW_CURRENT = "View the input files"
      + " generated by the current parameter values. The model must be saved.";
  public static String PARAMETER_VIEW_DEFAULT = "View the input files"
      + " generated by the default parameter values for this component.";

  // Questions
  public static final String QUESTION_START = "Are you sure you want to ";
  public static String QUESTION_SIGN_OUT = QUESTION_START
      + "sign out from WMT?";
  public static String QUESTION_PARAMETER_RESET = QUESTION_START
      + "reset all parameters to their default values?";

  protected Constants() {
  }
}
