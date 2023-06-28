//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchArgsLong.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import mmj.pa.ErrorCode;

public abstract class SearchArgsLong extends SearchArgsField {

    public SearchArgsLong(final int id, final String value) {
        super(id);
        set(value);
    }

    public SearchArgsLong(final int id) {
        this(id, SearchOptionsConstants.FIELD_ATTR[id].defaultText);
    }

    @Override
    public String get() {
        return Long.toString(longValue);
    }

    @Override
    public void set(final String value) {
        try {
            longValue = Long.parseLong(value);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(ErrorCode.format(
                SearchOptionsConstants.ERRMSG_ARG_INTEGER_TEXT_INVALID, value,
                fieldId, SearchOptionsConstants.FIELD_ATTR[fieldId].label));
        }
    }

    public long getLong() {
        return longValue;
    }

    private long longValue;
}
