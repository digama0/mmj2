//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import mmj.lang.Messages;
import mmj.pa.ErrorCode;

public class TrOutput {
    public TrOutput(final Messages messages) {
        this.messages = messages;
    }

    Messages messages; // for debug reasons

    public void errorMessage(final ErrorCode errorMessage,
        final Object... args)
    {
        messages.accumMessage(errorMessage, args);
    }

    public void dbgMessage(final boolean print, final ErrorCode infoMessage,
        final Object... args)
    {
        if (!print)
            return;

        messages.accumMessage(infoMessage, args);
        // System.out.format(infoMessage + "\n", args);
    }
}
