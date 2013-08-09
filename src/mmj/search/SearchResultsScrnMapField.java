// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SearchResultsScrnMapField.java

package mmj.search;

import java.awt.Font;

public interface SearchResultsScrnMapField {

    public abstract int getFieldId();

    public abstract void positionCursor(int i);

    public abstract void setSearchResultsFont(Font font);

    public abstract void setEnabled(boolean flag);
}