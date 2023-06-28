//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * Chapter.java  0.01 08/01/2008
 *
 * Aug-1-2008:
 *     --> new!
 */

package mmj.lang;

import mmj.pa.ErrorCode;

/**
 * Chapter is a baby class that provides a way to provide a title for a grouping
 * of Sections.
 * <p>
 * See BookManager for more details.
 */
public class Chapter {

    private final int chapterNbr;
    private final String chapterTitle;

    private Section firstSection;
    private Section lastSection;

    private int minMObjSeq = 0;
    private int maxMObjSeq = 0;

    /**
     * Sole constructor for Chapter.
     * 
     * @param chapterNbr is assigned by BookManager.
     * @param chapterTitle is the extracted descriptive title from the input
     *            Metamath database or the default title (must be at least an
     *            empty String!)
     */
    public Chapter(final int chapterNbr, final String chapterTitle) {
        this.chapterNbr = chapterNbr;
        this.chapterTitle = chapterTitle;
    }

    /**
     * Records the presence of a new Section with a Chapter.
     * 
     * @param section The new Section in the Chapter.
     */
    public void storeNewSection(final Section section) {
        if (firstSection == null)
            firstSection = section;
        lastSection = section;
    }

    /**
     * Returns the Chapter Number.
     * 
     * @return chapterNbr for the Chapter.
     */
    public int getChapterNbr() {
        return chapterNbr;
    }

    /**
     * Returns the Chapter Title
     * 
     * @return chapterTitle for the Chapter.
     */
    public String getChapterTitle() {
        return chapterTitle;
    }

    /**
     * Returns the first Section within the Chapter.
     * 
     * @return first Section within the Chapter.
     */
    public Section getFirstSection() {
        return firstSection;
    }

    /**
     * Returns the last Section within the Chapter.
     * <p>
     * Note: this may be the same as the first Section.
     * 
     * @return last Section within the Chapter.
     */
    public Section getLastSection() {
        return lastSection;
    }

    public int getMinMObjSeq() {
        return minMObjSeq;
    }

    public int getMaxMObjSeq() {
        return maxMObjSeq;
    }

    public void setMinMObjSeq(final int minMObjSeq) {
        this.minMObjSeq = minMObjSeq;
    }

    public void setMaxMObjSeq(final int maxMObjSeq) {
        this.maxMObjSeq = maxMObjSeq;
    }

    /**
     * Test code for creating diagnostics.
     * 
     * @return String of information about the Chapter formatted into a single
     *         line.
     */
    @Override
    public String toString() {
        return ErrorCode.format(LangConstants.CHAPTER_TOSTRING_LITERAL,
            getChapterNbr(), getChapterTitle(), getFirstSection()
                .getSectionNbr(), getLastSection().getSectionNbr());
    }
}
