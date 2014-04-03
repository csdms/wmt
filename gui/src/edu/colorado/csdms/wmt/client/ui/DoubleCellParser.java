/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.text.ParseException;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.text.shared.Parser;

/**
 * A localized parser based on {@link NumberFormat#getDecimalFormat}.
 */
public class DoubleCellParser implements Parser<Double> {

  private static DoubleCellParser INSTANCE;

  /**
   * Returns the instance of the no-op renderer.
   */
  public static Parser<Double> instance() {
    if (INSTANCE == null) {
      INSTANCE = new DoubleCellParser();
    }
    return INSTANCE;
  }

  protected DoubleCellParser() {
  }

  public Double parse(CharSequence object) throws ParseException {
    if ("".equals(object.toString())) {
      return null;
    }

    try {
      return NumberFormat.getDecimalFormat().parse(object.toString());
    } catch (NumberFormatException e) {
      throw new ParseException(e.getMessage(), 0);
    }
  }
}
