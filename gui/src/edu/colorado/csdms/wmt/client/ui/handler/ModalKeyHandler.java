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
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

/**
 * Calls a {@link ClickHandler} when the <code>Enter</code> or <code>Esc</code>
 * key is pressed.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModalKeyHandler implements KeyDownHandler {

  private ClickHandler okHandler;
  private ClickHandler cancelHandler;

  /**
   * Creates a new {@link ModalKeyHandler}.
   * 
   * @param okHandler the handler mapped to the "OK" button
   * @param cancelHandler the handler mapped to the "Cancel" button
   */
  public ModalKeyHandler(ClickHandler okHandler, ClickHandler cancelHandler) {
    this.okHandler = okHandler;
    this.cancelHandler = cancelHandler;
  }

  @Override
  public void onKeyDown(KeyDownEvent event) {
    Integer keyCode = event.getNativeKeyCode();
    if (keyCode == KeyCodes.KEY_ESCAPE) {
      cancelHandler.onClick(null);
    } else if (keyCode == KeyCodes.KEY_ENTER) {
      okHandler.onClick(null);
    }
  }
}
