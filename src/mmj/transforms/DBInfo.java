//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

public abstract class DBInfo {
    protected boolean dbg;

    /** This field is true if this object was initialized */
    private boolean isInit = false;

    /** For the debug and error output */
    protected TrOutput output;

    protected DBInfo(final TrOutput output, final boolean dbg) {
        this.output = output;
        this.dbg = dbg;
        isInit = true;
    }

    public boolean isInit() {
        return isInit;
    }
}
