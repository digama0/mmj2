//********************************************************************/
//* Copyright (C) 2005, 2006, 2008                                   */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * MObj.java  0.05 08/01/2008
 *
 * Version 0.03:
 *     --> Added tempObject boolean for dummy/temp MObj's
 *         required in ProofAssistant.
 *
 * Nov-01-2006: Version 0.04 --
 *     --> added description string (derived from input
 *         Metamath comments)
 *
 * Aug-01-2008: Version 0.05 --
 *     --> added chapterNbr, sectionNbr and sectionMObjNbr,
 *         plus Comparator SECTION_AND_MOBJ_NBR.
 *         See mmj.lang.BookManager.java for more info.

 */

package mmj.lang;

import java.util.Comparator;

/**
 * MObj (Metamath Object) is root of Sym and Stmt.
 * <p>
 * Originally coded without a common root, but eventually bowed to the
 * inevitable. The ability to hold an array/collection of Syms and Stmts is
 * helpful, and eventually may become an absolute necessity -- if and when a
 * LogicalSystem can be output, we will need to store Begin and End Scope
 * statements, and perhaps Include Files and Comments.
 * 
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public abstract class MObj {

    /**
     * Provides an ordering of Metamath objects.
     * <p>
     * The concept of database sequence is key for Metamath. Statements can only
     * refer to statements previously defined -- no forward references and no
     * self references are allowed. Also, the sequence of hypotheses is by
     * MObj.seq, as is the ordering of items on the Proof Work Stack.
     * <p>
     * Metamath allows variables to be "active" or "inactive", based on the
     * scope level in which they are referenced by {@code VarHyp}s. This allows
     * for an unlimited number of {@code VarHyp}s with a limited set of of
     * {@code Var}s, but it complicates things elsewhere! Even the grammatical
     * parser has to deal with non-terminal symbols that change depending on
     * context (@see mmj.verify.Grammar).
     */
    protected final int seq;

    /**
     * isTempObject flag denotes a dummy or temporary object that is not stored
     * in the Sym table or Stmt table and has the lifespan of a single
     * transaction.
     */
    protected boolean isTempObject = false;

    /**
     * description is derived from Metamath comments (initially... and may be
     * present only for Theorems.)
     */
    protected String description = null;

    /**
     * chapterNbr assigned by BookManager which is optionally enabled.
     * <p>
     * chapterNbr is zero if the MObj has not been assigned to a Chapter.
     * Chapter numbers are assigned from 1 by 1 within the set of loaded
     * Metamath input files (see mmj.lang.BookManager.java for more info.)
     */
    protected int chapterNbr = 0;

    /**
     * sectionNbr assigned by BookManager which is optionally enabled.
     * <p>
     * sectionNbr is zero if the MObj has not been assigned to a Section.
     * Section numbers are assigned from 1 by 1, and correspond to
     * "sub-sections" in a Metamath input file (see set.mm), except that each
     * input Metamath "sub-section" is broken down into 4 mmj2 Sections: 1: Sym
     * (Cnst and Var), 2: VarHyp, 3: Syntax and 4: Logic (logic includes logic
     * axioms, theorems and logical hypotheses). These categories are assigned
     * in sequence so that a "logic" section number is therefore always
     * divisible by 4, syntax by 3, and so on (see mmj.lang.BookManager.java for
     * more info.)
     */
    protected int sectionNbr = 0;

    /**
     * sectionMObjNbr is assigned by BookManager which is optionally enabled.
     * <p>
     * sectionMObjNbr is zero if the MObj has not been assigned to a Section.
     * sectionMObj numbers are assigned from 1 by 1 within Section (see
     * mmj.lang.BookManager.java for more info.)
     */
    protected int sectionMObjNbr = 0;

    /**
     * Construct MObj with sequence number.
     * 
     * @param inSeq the sequence number
     */
    protected MObj(final int inSeq) {
        seq = inSeq;
    }

    /**
     * Note: an extensive set of tests showed that direct access to .seq is an
     * infintesimal bit faster than getSeq() :) So public access to MObj.seq was
     * revoked!
     * 
     * @return seq number.
     */
    public int getSeq() {
        return seq;
    }

    /**
     * Returns tempObject boolean.
     * 
     * @return tempObject boolean flag denoting dummy/temp objects.
     */
    public boolean getIsTempObject() {
        return isTempObject;
    }

    /**
     * Sets tempObject boolean value.
     * 
     * @param isTempObject boolean flag denoting dummy/temp objects.
     */
    public void setIsTempObject(final boolean isTempObject) {
        this.isTempObject = isTempObject;
    }

    /**
     * Converts to MObj to String.
     * 
     * @return returns MObj string;
     */
    @Override
    public String toString() {
        return Integer.toString(seq);
    }

    /**
     * Computes hashcode for this MObj
     * 
     * @return hashcode for the MObj
     */
    @Override
    public int hashCode() {
        return seq;
    }

    /**
     * Compare for equality with another MObj.
     * <p>
     * Equal if and only if the MObj sequence numbers are equal. and the obj to
     * be compared to this object is not null and is a MObj as well.
     * 
     * @return returns true if equal, otherwise false.
     */
    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof MObj && seq == ((MObj)obj).seq;
    }

    /**
     * SEQ sequences by MObj.seq.
     */
    public static final Comparator<MObj> SEQ = new Comparator<MObj>() {
        public int compare(final MObj o1, final MObj o2) {
            return o1.seq - o2.seq;
        }
    };

    /**
     * SECTION_AND_MOBJ_NBR sequences by sectionNbr and sectionMObjNbr;
     */
    public static final Comparator<MObj> SECTION_AND_MOBJ_NBR = new Comparator<MObj>()
    {
        public int compare(final MObj o1, final MObj o2) {
            return o1.sectionNbr == o2.sectionNbr ? o1.sectionMObjNbr
                - o2.sectionMObjNbr : o1.sectionNbr - o2.sectionNbr;
        }
    };

    /**
     * Returns description text derived from Metamath comments.
     * <p>
     * This contains the complete Metamath comment statement except for the $(
     * and $) delimiter tokens.
     * <p>
     * 
     * @return description text derived from Metamath comments.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return a version of the description where all whitespace is collapsed to
     *         a single space
     */
    public String getDescriptionForSearch() {
        final StringBuilder str = new StringBuilder(description.length());
        boolean space = false;
        for (int i = 0; i < description.length(); i++) {
            final char c = description.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!space) {
                    str.append(' ');
                    space = true;
                }
            }
            else {
                str.append(c);
                space = false;
            }
        }

        return str.toString();
    }

    /*
     * Sets description text derived from Metamath comments.
     * <p>
     * This contains the complete Metamath comment statement
     * except for the $( and $) delimiter tokens.
     * <p>
     * @param description description text derived from Metamath comments.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Return chapterNbr;
     * 
     * @return chapter number for the MObj.
     */
    public int getChapterNbr() {
        return chapterNbr;
    }

    /**
     * Updates chapterNbr;
     * 
     * @param chapterNbr for the MObj.
     */
    public void setChapterNbr(final int chapterNbr) {
        this.chapterNbr = chapterNbr;
    }

    public int getOrigSectionNbr() {
        return (sectionNbr - 1) / LangConstants.SECTION_NBR_CATEGORIES + 1;
    }

    /**
     * Return sectionNbr;
     * 
     * @return section number for the MObj.
     */
    public int getSectionNbr() {
        return sectionNbr;
    }

    /**
     * Updates sectionNbr;
     * 
     * @param sectionNbr for the MObj.
     */
    public void setSectionNbr(final int sectionNbr) {
        this.sectionNbr = sectionNbr;
    }

    /**
     * Return sectionMObjNbr;
     * 
     * @return sectionMObjNbr for the MObj.
     */
    public int getSectionMObjNbr() {
        return sectionMObjNbr;
    }

    /**
     * Updates sectionMObjNbr;
     * 
     * @param sectionMObjNbr for the MObj.
     */
    public void setSectionMObjNbr(final int sectionMObjNbr) {
        this.sectionMObjNbr = sectionMObjNbr;
    }

}
