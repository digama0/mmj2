//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * Section.java  0.01 08/01/2008
 *
 * Aug-1-2008:
 *     --> new!
 */

package mmj.lang;

import mmj.pa.ErrorCode;

/**
 * Section is a rudimentary class containing information for BookManager about
 * the grouping of statements in a Chapter within a Metamath database.
 * <p>
 * See BookManager for more information.
 */
public class Section {

    private final Chapter sectionChapter;
    private final int sectionNbr;
    private String sectionTitle;
    private int lastMObjNbr;
    private int minMObjSeq;
    private int maxMObjSeq;

    /**
     * Sole constructor for Section..
     * 
     * @param sectionChapter the Chapter to which the Section belongs.
     * @param sectionNbr is assigned by BookManager.
     * @param sectionTitle is the extracted descriptive title from the input
     *            Metamath database or the default title (must be at least an
     *            empty String!)
     */
    public Section(final Chapter sectionChapter, final int sectionNbr,
        final String sectionTitle)
    {

        this.sectionChapter = sectionChapter;
        this.sectionNbr = sectionNbr;
        this.sectionTitle = sectionTitle;

        sectionChapter.storeNewSection(this);
    }

    /**
     * Assigns an MObj to a Chapter and Section and computes the MObj
     * SectionMObjNbr.
     * <p>
     * This function is intended for use by LogicalSystem and it is this
     * function which actually updates the MOBj with the computed
     * SectionMOBjNbr.
     * <p>
     * Note: the MObj is assigned a new sectionMObjNbr only if MObj has not
     * already been assigned one. The reason this is necessary even with updates
     * performed during the initial load of the input .mm file is that a
     * Metamath Var can be declared in multiple locations within the file. These
     * multiple declarations occur within separate Metamath Scopes and outside
     * of the scope the Var is considered to be "inactive", so subsequent
     * re-declarations are considered to be re-activations. The bottom line is
     * that only the first declaration is assigned a sectionMObjNbr.
     * 
     * @param mObj the MObj to be assigned to a Chapter and Section and updated
     *            with SectionMObjNbr.
     * @return true only if the operation is completed successfully, meaning
     *         that the MObj has a zero sectionMObjNbr prior to the update.
     */
    public boolean assignChapterSectionNbrs(final MObj mObj) {
        final int n = mObj.getSectionMObjNbr();
        if (n != 0)
            return false;
        mObj.setSectionMObjNbr(++lastMObjNbr);
        mObj.setChapterNbr(sectionChapter.getChapterNbr());
        mObj.setSectionNbr(sectionNbr);
        final int j = mObj.getSeq();
        if (minMObjSeq == 0 || j < minMObjSeq)
            setMinMObjSeq(j);
        if (maxMObjSeq == 0 || j > maxMObjSeq)
            setMaxMObjSeq(j);
        final int min = sectionChapter.getMinMObjSeq();
        if (min == 0 || j < min)
            sectionChapter.setMinMObjSeq(j);
        final int max = sectionChapter.getMaxMObjSeq();
        if (max == 0 || j > max)
            sectionChapter.setMaxMObjSeq(j);
        return true;
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
     * Returns the Chapter to which the Section is assigned.
     * 
     * @return the Chapter to which the Section is assigned.
     */
    public Chapter getSectionChapter() {
        return sectionChapter;
    }

    /**
     * Returns the sectionNbr for the Section.
     * 
     * @return the sectionNbr for the Section.
     */
    public int getSectionNbr() {
        return sectionNbr;
    }

    /**
     * Returns the sectionTitle for the Section.
     * 
     * @return the sectionTitle for the Section.
     */
    public String getSectionTitle() {
        return sectionTitle;
    }

    /**
     * Sets the value of the sectionTitle for the Section.
     * <p>
     * The title must be, at least, an empty String.
     * 
     * @param sectionTitle Description or Title of the Section.
     */
    public void setSectionTitle(final String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    /**
     * Returns the last MObj number within the section.
     * <p>
     * The highest MObj number is the same as the last because additions are
     * made only at the end of a Section and new MObj numbers are generated from
     * 1 by 1 within each Section.
     * 
     * @return final MObj number within the Section.
     */
    public int getLastMObjNbr() {
        return lastMObjNbr;
    }

    /**
     * Test code for creating diagnostics.
     * 
     * @return String of information about the Section formatted into a single
     *         line.
     */
    @Override
    public String toString() {
        return ErrorCode.format(LangConstants.SECTION_TOSTRING_LITERAL,
            sectionChapter.getChapterNbr(), getSectionNbr(),
            getSectionCategoryDisplayCaption(), getSectionTitle(),
            getLastMObjNbr());
    }

    /**
     * Returns a string caption for the Section category code.
     * <p>
     * See LangConstants.SECTION_DISPLAY_CAPTION.
     * 
     * @return caption for the Section category code.
     */
    public String getSectionCategoryDisplayCaption() {
        return LangConstants.SECTION_DISPLAY_CAPTION[getSectionCategoryCd()];
    }

    /**
     * Returns the Section Category Code.
     * <p>
     * See LangConstants.SECTION_NBR_CATEGORIES.
     * 
     * @return the Section Category Code.
     */
    public int getSectionCategoryCd() {
        return Section.getSectionCategoryCd(sectionNbr);
    }

    /**
     * Returns the Section Category Code for a Section number.
     * <p>
     * See LangConstants.SECTION_NBR_CATEGORIES.
     * 
     * @param s section number
     * @return the Section Category Code.
     */
    public static int getSectionCategoryCd(final int s) {

        final int n = s % LangConstants.SECTION_NBR_CATEGORIES;

        if (n == 0)
            return LangConstants.SECTION_NBR_CATEGORIES;
        else
            return n;
    }
}
