// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SearchOptionsScrnMapField.java

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

    public abstract void downloadToScrnMap(SearchArgs args,
        SearchMgr searchMgr);

    public abstract void setDefaultToCurrentValue();

    public abstract void resetToDefaultValue();

    public abstract String get();

    public abstract void set(String s);

    public abstract JLabel createJLabel();

    public abstract boolean requestFocusInWindow(boolean flag);
}