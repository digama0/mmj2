// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchArgsLong.java

package mmj.search;

// Referenced classes of package mmj.search:
//            SearchArgsField, SearchOptionsConstants, SearchOptionsFieldAttr

public abstract class SearchArgsLong extends SearchArgsField {

    public SearchArgsLong(final int i, final String s) {
        super(i);
        set(s);
    }

    public SearchArgsLong(final int i) {
        this(i, SearchOptionsConstants.FIELD_ATTR[i].defaultText);
    }

    @Override
    public String get() {
        return Long.toString(longValue);
    }

    @Override
    public void set(final String s) {
        try {
            longValue = Long.parseLong(s);
        } catch (final NumberFormatException numberformatexception) {
            throw new IllegalArgumentException(

            SearchOptionsConstants.ERRMSG_ARG_INTEGER_TEXT_INVALID_1 + s
                + SearchOptionsConstants.ERRMSG_ARG_INTEGER_TEXT_INVALID_2
                + fieldId
                + SearchOptionsConstants.ERRMSG_ARG_INTEGER_TEXT_INVALID_3
                + SearchOptionsConstants.FIELD_ATTR[fieldId].label);
        }
    }

    public long getLong() {
        return longValue;
    }

    private long longValue;
}