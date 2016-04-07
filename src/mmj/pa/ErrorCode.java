package mmj.pa;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mmj.gmff.GMFFConstants;
import mmj.mmio.MMIOConstants;
import mmj.transforms.TrConstants;
import mmj.util.UtilConstants;
import mmj.verify.GrammarConstants;
import mmj.verify.ProofConstants;

/**
 * Error Codes used in mmj2
 * <p>
 * Each mmj message begins with a code, such as this:
 * <p>
 * <code>E-LA-0007</code>
 * <p>
 * where the format of the code is {@code X-YY-9999}<br>
 * <p>
 * <b>{@code X}</b> : error level
 * <ul>
 * <li>{@code E} = Error
 * <li>{@code I} = Information
 * <li>{@code W} = Warning
 * <li>{@code A} = Abort (processing terminates, usually a bug).
 * </ul>
 * <p>
 * <b>{@code YY}</b> : source code
 * <ul>
 * <li>{@code GM} = mmj.gmff package (see {@link GMFFConstants})
 * <li>{@code GR} = mmj.verify.Grammar and related code (see
 * {@link GrammarConstants})
 * <li>{@code IO} = mmj.mmio package (see {@link MMIOConstants})
 * <li>{@code LA} = mmj.lang package (see {@link GMFFConstants})
 * <li>{@code PA} = mmj.pa package (proof assistant) (see {@link PaConstants})
 * <li>{@code PR} = mmj.verify.VerifyProof and related code (see
 * {@link ProofConstants})
 * <li>{@code TL} = mmj.tl package (Theorem Loader).
 * <li>{@code TM} = mmj.tmff.AlignColumn and related code
 * <li>{@code UT} = mmj.util package. (see {@link UtilConstants})
 * <li>{@code TR} = mmj.transforms package (proof assistant) (see
 * {@link TrConstants})
 * </ul>
 * <p>
 * <b>{@code 9999}</b> : sequential number within the source code, 0001 through
 * 9999.
 */
public class ErrorCode {
    private static final Pattern PATT = Pattern
        .compile("([A-Z])-([A-Z]{2})-(\\d{4})");
    private static final Map<String, ErrorCode> codes = new HashMap<>();

    public final ErrorLevel level;
    public final String ns;
    public final int number;
    public final String messageRaw;
    public int usageMax = Integer.MAX_VALUE;
    public int usageCount = 0;

    public ErrorCode(final String code, final String message) {
        messageRaw = "".equals(message) ? null : message;
        final Matcher m = PATT.matcher(code);
        ErrorLevel l = null;
        if (m.matches()) {
            for (final ErrorLevel e : ErrorLevel.values())
                if (e.shortForm().equals(m.group(1))) {
                    l = e;
                    break;
                }
            ns = m.group(2);
            number = Integer.parseInt(m.group(3));
        }
        else {
            ns = null;
            number = 0;
        }
        if ((level = l) == null)
            throw new IllegalArgumentException(
                "Error code " + code + " in improper format");
        if (codes.put(code().substring(2), this) != null)
            throw new IllegalArgumentException(
                "Code " + code + " defined twice");
    }

    public String code() {
        return String.format("%s-%s-%04d", level.shortForm(), ns, number);
    }

    public String messageRaw(final Object... args) {
        return format(messageRaw, args);
    }

    public String message(final Object... args) {
        return code() + " " + messageRaw(args);
    }

    /**
     * Increments this error code's usage count.
     *
     * @return True if the error code has not exceeded its usage quota
     */
    public boolean use() {
        if (usageCount < usageMax) {
            usageCount++;
            return true;
        }
        return false;
    }

    /**
     * Similar to {@link #use()}, but does not increase the usage quota.
     *
     * @return True if the error code has not exceeded its quota
     */
    public boolean enabled() {
        return usageCount < usageMax;
    }

    public static String format(final String format, final Object... args) {
        if (args == null || args.length == 0)
            return format;
        if (format == null)
            return Arrays.toString(args);
        try {
            return String.format(format, args);
        } catch (final IllegalFormatException e) {
            return format;
        }
    }

    public static ErrorCode of(final String combined) {
        final String[] parse = combined.split(" ", 2);
        return new ErrorCode(parse[0], parse.length > 1 ? parse[1] : null);
    }

    public static ErrorCode of(final String code, final String message) {
        return new ErrorCode(code, message);
    }

    public static ErrorCode get(final String code) {
        return codes.get(code);
    }

    public enum ErrorLevel {
        Debug(false), Info(false), Warn(false), Error(true), Abort(true);

        public boolean error;

        private ErrorLevel(final boolean error) {
            this.error = error;
        }

        public String shortForm() {
            return toString().substring(0, 1);
        }
    }
}
