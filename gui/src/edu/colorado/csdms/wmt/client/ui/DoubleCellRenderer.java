/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;

/**
 * A localized renderer based on {@link NumberFormat#getDecimalFormat}.
 */
public class DoubleCellRenderer extends AbstractRenderer<Double> {

  private static DoubleCellRenderer INSTANCE;

  /**
   * Returns the instance.
   */
  public static Renderer<Double> instance() {
    if (INSTANCE == null) {
      INSTANCE = new DoubleCellRenderer();
    }
    return INSTANCE;
  }

  protected DoubleCellRenderer() {
  }

  public String render(Double object) {
    if (object == null) {
      return "";
    }
    return NumberFormat.getFormat("#,##0.0#####").format(object);
  }
}
