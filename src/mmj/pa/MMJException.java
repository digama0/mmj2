//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * LangException.java  0.02 08/23/2005
 */

package mmj.pa;

import java.util.ArrayList;
import java.util.List;

/**
 * Thrown when Metamath source file has a non-fatal error such as a syntax
 * error.
 */
public class MMJException extends Exception {
    public ErrorCode code;
    public List<ErrorContext> ctxt = new ArrayList<>(0);

    /**
     * Constructor, {@code LangException} with error message.
     *
     * @param code error message.
     * @param args formatting arguments.
     */
    public MMJException(final ErrorCode code, final Object... args) {
        super(code.message(args));
        this.code = code;
    }

    /**
     * Constructor, {@code LangException} with error message and cause.
     *
     * @param cause The source exception, for stack tracing
     * @param code error message.
     * @param args formatting arguments.
     */
    public MMJException(final Throwable cause, final ErrorCode code,
        final Object... args)
    {
        super(code.message(args), cause);
        this.code = code;
    }

    public MMJException addContext(final ErrorContext ec) {
        if (ec != null && getContext(ec.getClass()) == null)
            ctxt.add(ec);
        return this;
    }

    public static <T extends MMJException> T addContext(final ErrorContext ec,
        final T e)
    {
        e.addContext(ec);
        return e;
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();
        for (final ErrorContext ec : ctxt)
            msg = ec.append(msg);
        return msg;
    }

    /**
     * Gets an {@link MMJException} in the cause of this throwable.
     *
     * @param t the throwable
     * @return an MMJException that caused it, or null
     */
    public static MMJException extract(final Throwable t) {
        return t == null ? null
            : t instanceof MMJException ? (MMJException)t
                : extract(t.getCause());
    }

    /**
     * Returns true if this Throwable is not caused by the MMJ system, or the
     * {@link MMJException} cause has an enabled error code (see
     * {@link ErrorCode#enabled}).
     *
     * @param t the throwable
     * @return true if this throwable is enabled
     */
    public static boolean isEnabled(final Throwable t) {
        final MMJException e = extract(t);
        return e == null || e.code.enabled;
    }

    protected void checkNS(final String ns) {
        if (!ns.equals(code.ns))
            throw new IllegalArgumentException(
                "Attempt to create exception with incorrect namespace " + ns,
                this);
    }

    @SuppressWarnings("unchecked")
    public <T extends ErrorContext> T getContext(final Class<T> clazz) {
        for (final ErrorContext ec : ctxt)
            if (clazz.isInstance(ec))
                return (T)ec;
        return null;
    }

    public interface ErrorContext {
        String append(String msg);
    }

    public static class FormatContext implements ErrorContext {
        String msg;
        Object[] args;

        public FormatContext(final String format, final Object... args) {
            msg = ErrorCode.format(format, this.args = args);
        }

        @Override
        public String toString() {
            return msg;
        }

        @Override
        public String append(final String msg) {
            return msg + " " + this.msg;
        }
    }
}
