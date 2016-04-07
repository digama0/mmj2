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
 * Superclass of all MMJ messages, for both info and errors. Every MMJ message
 * has a code associated with it (see {@link ErrorCode}), which allows both for
 * efficient locating of the error by users or developers, as well as
 * customizable error suppression by type.
 * <p>
 * The primary difference between info messages and error messages is that an
 * info message generally throws this exception, while an info message will
 * merely create the exception object and pass it around. All of the message
 * output constructs work by creating members of this class at some point, and
 * produce messages headed by the error code, which has the format
 * {@code X-YY-1234}.
 * <p>
 * Since this class extends {@link Exception}, it is checked, as are all its
 * subclasses. In order to throw an unchecked exception, it is usually wrapped
 * in an {@link IllegalStateException} or {@link IllegalArgumentException}; the
 * first case is usually a programmer error, while the second might be more
 * generally used for validating input where checked exceptions are
 * inconvenient. In any case, the {@link #extract(Throwable)} class can be used
 * to get an embedded MMJException out of any exception.
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
        super(code.messageRaw(args));
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
        super(code.messageRaw(args), cause);
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
        return code.code() + " " + msg;
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
     * {@link ErrorCode#use()}); in the latter case the usage count for the
     * error is increased.
     *
     * @param t the throwable
     * @return true if this throwable is enabled
     */
    public static boolean use(final Throwable t) {
        final MMJException e = extract(t);
        return e == null || e.code.use();
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

    /**
     * An error context is an extra piece of information about the location of
     * the error, such as "Theorem X" or "Line 2". These decorate the error
     * message (before or after) and are usually added by specialized methods in
     * a class that wrap the exceptions that are thrown. The context list is
     * ordered, but only one of each type of context is allowed, so that there
     * is no danger of double-annotating the position of an error.
     *
     * @author Mario
     */
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
