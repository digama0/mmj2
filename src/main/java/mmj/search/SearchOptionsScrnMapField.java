//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * {{file}}.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Font;

import javax.swing.JLabel;

// Referenced classes of package mmj.search:
//            SearchArgs, SearchMgr

public interface SearchOptionsScrnMapField {

    public abstract int getFieldId();

    public abstract void positionCursor(int i);

    public abstract void setSearchOptionsFont(Font font);

    public abstract void setEnabled(boolean flag);

    public abstract void uploadFromScrnMap(SearchArgs args);

    public abstract void downloadToScrnMap(SearchArgs args, SearchMgr searchMgr);

    public abstract void setDefaultToCurrentValue();

    public abstract void resetToDefaultValue();

    public abstract String get();

    public abstract void set(String s);

    public abstract JLabel createJLabel();

    public abstract boolean requestFocusInWindow(boolean flag);
}
