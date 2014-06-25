package mmj.transforms;

import mmj.lang.Messages;

public class TrOutput {
    public TrOutput(final Messages messages) {
        this.messages = messages;
    }

    Messages messages; // for debug reasons

    public void errorMessage(final String errorMessage, final Object... args) {
        messages.accumErrorMessage(errorMessage, args);
    }

    public void dbgMessage(final boolean print, final String infoMessage,
        final Object... args)
    {
        if (!print)
            return;

        messages.accumInfoMessage(infoMessage, args);
    }
}
