/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.GWT;

/**
 * A class that defines, as public static members, constants used in the WMT
 * client.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class Constants {

  // This switch toggles API development and public mode.
  public static final Boolean USE_API_DEV_MODE = true;

  // By same-origin policy, must use HTTPS consistently across all URLs.
  public static final String BASE_URL = "https://csdms.colorado.edu/";
  public static final String API_URL = BASE_URL + "wmt-server/";
  public static final String API_DEV_URL = BASE_URL + "wmt/api-dev/";
  public static final String LOCAL_URL = GWT.getHostPageBaseURL();

  // API URLs for login status.
  public static final String LOGIN_PATH = "account/login";
  public static final String LOGOUT_PATH = "account/logout";
  public static final String USERNAME_PATH = "account/username";

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

  // Error messages used in DataTransfer.
  public static String REQUEST_ERR_MSG = "Failed to send the request: ";
  public static String RESPONSE_ERR_MSG = "No match found in the response.";

  public static String CLOSE_MSG =
      "Any unsaved model data will be lost if this page"
          + " is reloaded or closed.";

  // Number of tries to fetch a component; a magic number.
  public static Integer RETRY_ATTEMPTS = 3;

  // Fractional size of viewEast in Perspective.
  public static Double VIEW_EAST_FRACTION = 0.50;

  // Width (in px) of splitter grabby bar.
  public static Integer SPLITTER_SIZE = 5;

  // Height (in px) of tab bars.
  public static Double TAB_BAR_HEIGHT = 40.0;

  // Standard dimensions for PopupPanels.
  public static String MENU_WIDTH = "200px"; // arbitrary, aesthetic
  public static String MENU_HEIGHT = "20em";

  // The default text displayed in the driver ComponentCell.
  public static String DRIVER = "driver";

  // The default model name.
  public static String DEFAULT_MODEL = "Model 0";

  // The number of characters to display in a ComponentCell.
  public static Integer TRIM = 10;

  // Unicode representation.
  public static String ELLIPSIS = "\u2026";

  public static String ALL_COMPONENTS = "__all_components";

  // FontAwesome icons.
  public static String SIGN_IN = "<i class='fa fa-sign-in'></i> Sign In";
  public static String SIGN_OUT = "<i class='fa fa-sign-out'></i> Sign Out";
  public static String FA_OPEN = "<i class='fa fa-folder-open-o fa-fw'></i> ";
  public static String FA_SAVE = "<i class='fa fa-floppy-o fa-fw'></i> ";
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

  // Tooltip text strings.
  public static String MODEL_OPEN = "Open an existing model.";
  public static String MODEL_SAVE = "Save the current model to the server.";
  public static String MODEL_RUN = "Run the current model on a HPCC.";
  public static String MODEL_MORE = "See more options...";
  public static String MODEL_SAVE_AS = "Save a model with a new name.";
  public static String MODEL_LABELS = "Manage the labels set on a model.";
  public static String MODEL_DELETE = "Delete a model from server.";
  public static String MODEL_RUN_STATUS = "Get the status of model run.";
  public static String MODEL_HELP = "View the help documents on using WMT.";
  public static String COMPONENT_SHOW =
      "View and edit this component's parameters.";
  public static String COMPONENT_INFO =
      "View information about this component.";
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
