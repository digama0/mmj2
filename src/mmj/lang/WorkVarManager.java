//********************************************************************/
//* Copyright (C) 2007                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  WorkVarManager.java  0.01 08/01/2007
 *
 *  Aug-1-2007:
 *      --> new!
 *
 */

package mmj.lang;
import mmj.verify.Grammar;
import mmj.mmio.Statementizer;
import java.util.*;

/**
 *  WorkVarManager is a "helper" class that is used to
 *  define, declare, alloc and maintain state information
 *  about a set of Work Variables.
 */
public class WorkVarManager {

    private boolean areWorkVarsDeclared
                                  = false;

    // these are parallel array(lists), key is Type Code.
    private ArrayList       definedTypCdList;
    private ArrayList       definedWorkVarPrefixList;
    private ArrayList       definedNbrWorkVarsList;

    // these are parallel arrays, key is Type Code.
    private Cnst[]          declaredTypCd;
    private String[]        declaredWorkVarPrefix;
    private int[]           declaredNbrWorkVars;
    private WorkVar[][]     declaredWorkVar;
    private WorkVarHyp[][]  declaredWorkVarHyp;

    // allocated = "in use" / "active". the purpose
    // is to avoid unnecessary initializations, so
    // only allocated Work Variables get initialized.
    private boolean[][]     allocated;

    // prevAllocIndex array key is Type Code. Note
    // that the index value of work variable &W1 is 0, etc.
    private int[]           prevAllocIndex;

    // used for construction of WorkVar and WorkVarHyp.
    // the sequence numbers begin a -2**31 and are
    // increased by 1 for each new WorkVar and WorkVarHyp,
    // meaning that -- in terms of seq number -- they
    // are global (precede every input statement.)
    private int             seqNbrForMObj;

    /**
     *
     *  Sole constructor for WorkVarManager.
     *  <p>
     *  Does nothing except allocate the Arraylists
     *  to hold input definitions. Note therefore
     *  that the WorkVarManager object is not ready
     *  for use by ProofAsst or elsewhere until
     *  the Work Variables are actually declared,
     *  and a NullPointerException will result if
     *  access is attempted before then.
     *
     *  @param grammar Grammar object for loaded .mm file.
     */
    public WorkVarManager(Grammar grammar) {
        int len                   =
            grammar.getVarHypTypSet().size();

        definedTypCdList          = new ArrayList(len);
        definedWorkVarPrefixList  = new ArrayList(len);
        definedNbrWorkVarsList    = new ArrayList(len);
    }

    /**
     *  Returns true if the Work Variables have been
     *  declared.
     *  <p>
     *  If no Work Variable definitions are input and
     *  the Proof Assistant is instantiated prior to
     *  declaration of Work Variables, ProofAsstBoss.java
     *  will declare the Work Variables using the default
     *  definitions. Which is why we need this function...
     *  <p>
     *  @return true if Work Variables declared yet.
     *  <p>
     */
    public boolean areWorkVarsDeclared() {
        return areWorkVarsDeclared;
    }

    /**
     *  Define the Work Variables to be used for a given
     *  grammatical type code.
     *  <p>
     *  The definitions are stored separately from the
     *  tables constructed by "Declare". This allows
     *  mixing of definitions and default values for
     *  Work Variables.
     *
     *  @param grammar Grammar object for loaded .mm file.
     *  @param grammaticalTypCdIn String holding the Cnst
     *                 used as a Type Code (e.g. "wff").
     *  @param workVarPrefixIn String holding the prefix to
     *                 be used with a numeric suffix to name
     *                 Work Variables of a given Type.
     *  @param nbrWorkVarsIn number of Work Variables to declare
     *                 for this Type Code.
     *
     *  @throws VerifyException if individual field values are
     *                 invalid.
     */
    public void defineWorkVarType(Grammar       grammar,
                                  String        grammaticalTypCdIn,
                                  String        workVarPrefixIn,
                                  int           nbrWorkVarsIn)
                    throws VerifyException {

        Cnst    typ               =
            editGrammaticalTypCd(grammar,
                                 grammaticalTypCdIn);

        String  prefix            =
            editWorkVarPrefix(workVarPrefixIn);

        Integer     nbrWorkVars   =
            editNbrWorkVars(nbrWorkVarsIn);

        int loc                   =
            definedTypCdList.indexOf(typ);
        if (loc == -1) {
            definedTypCdList.add(typ);
            definedWorkVarPrefixList.add(prefix);
            definedNbrWorkVarsList.add(nbrWorkVars);
        }
        else {
            definedTypCdList.set(loc,
                                 typ);
            definedWorkVarPrefixList.set(loc,
                                         prefix);
            definedNbrWorkVarsList.set(loc,
                                       nbrWorkVars);
        }
    }

    /**
     *  Declare the Work Variables that have been defined.
     *  <p>
     *  The input definitions and default values for each
     *  grammatical Type Code are used to create the Work
     *  Variable objects and finalize the WorkVarManager
     *  object for actual use.
     *  <p>
     *  Note that final validation checks are performed to
     *  ensure that there are no duplicate Var or VarHyp
     *  names, either among the Work Variable objects or
     *  the other symbols and statements defined in the
     *  LogicalSystem for the input .mm file. This means
     *  checking for duplicate Work Var prefixes as well
     *  as checking each WorkVar and WorkVarHyp name
     *  against the LogicalSystem's Symbol and Statement
     *  tables.
     *  <p>
     *  @param grammar Grammar object for loaded .mm file.
     *  @param logicalSystem LogicalSystem for loaded .mm file.
     *
     *  @throws VerifyException if duplicate Var or VarHyp
     *                 names result or if there is a conflict
     *                 with the Symbol and Statement table
     *                 namespaces.
     */
    public void declareWorkVars(Grammar         grammar,
                                LogicalSystem   logicalSystem)
                    throws VerifyException {

        seqNbrForMObj             =
            LangConstants.STARTING_WORK_VAR_SEQ_NBR_FOR_MOBJ;

        int len                   =
            grammar.getVarHypTypSet().size();

        declaredTypCd             = new Cnst[len];
        declaredWorkVarPrefix     = new String[len];
        declaredNbrWorkVars       = new int[len];
        declaredWorkVar           = new WorkVar[len][];
        declaredWorkVarHyp        = new WorkVarHyp[len][];

        prevAllocIndex            = new int[len];
        allocated                 = new boolean[len][];

        Cnst     defTyp;
        int      loc;
        int      n;
        String   labelId;
        Sym[]    symArray         = new Sym[2]; //for formula
        Iterator iterator         =
                        grammar.getVarHypTypSet().iterator();
        int      i                = 0;
        while (iterator.hasNext()) {
            defTyp                = (Cnst)iterator.next();

            // WARNING: updating the Cnst here...
            defTyp.workVarTypIndex
                                  = i;
            declaredTypCd[i]      = defTyp;

            loc                   =
                definedTypCdList.indexOf(defTyp);
            if (loc == -1) {
                declaredWorkVarPrefix[i]
                                  =
                    LangConstants.WORK_VAR_DEFAULT_PREFIX_PREFIX
                    + defTyp.getId().substring(0,1).toUpperCase();

                declaredNbrWorkVars[i]
                                  =
                    LangConstants.WORK_VAR_DEFAULT_NBR_FOR_TYP_CD;
            }
            else {
                declaredWorkVarPrefix[i]  =
                    (String)definedWorkVarPrefixList.
                        get(loc);

                declaredNbrWorkVars[i]    =
                    ((Integer)definedNbrWorkVarsList.
                        get(loc)).intValue();
            }

            n                     = declaredNbrWorkVars[i];
            declaredWorkVar[i]    = new WorkVar[n];
            declaredWorkVarHyp[i] = new WorkVarHyp[n];
            allocated[i]          = new boolean[n];
            prevAllocIndex[i]     = -1;

            symArray[0]           = defTyp;
            for (int j = 0; j < n; j++) {

                labelId               =      // (e.g. "&W1")
                    declaredWorkVarPrefix[i] +
                    Integer.toString(j + 1);

                declaredWorkVar[i][j] =
                    new WorkVar(
                        seqNbrForMObj++,
                        labelId,
                        j);                  // workVarIndex

                symArray[1]           = declaredWorkVar[i][j];

                declaredWorkVarHyp[i][j]
                                      =
                    new WorkVarHyp(
                        seqNbrForMObj++,
                        labelId,
                        new VarHypFormula(2,
                                          symArray),
                        j);
            }

            ++i;
        }

        checkForDuplicateWorkVarPrefixes();
        checkWorkVarNamespaceUniqueness(logicalSystem);

        areWorkVarsDeclared       = true;

    }

    /**
     *  Validates an input string as being a valid Type Code
     *  for Work Variables.
     *
     *  @param grammar Grammar object for loaded .mm file.
     *  @param grammaticalTypCdIn String Type Code (Cnst)
     *  @return Cnst Type Code.
     *
     *  @throws VerifyException if input Type Code invalid.
     */
    public Cnst editGrammaticalTypCd(Grammar   grammar,
                                     String    grammaticalTypCdIn)
                    throws VerifyException {
        String s;
        Cnst   typ;
        if (grammaticalTypCdIn != null) {
            s                     = grammaticalTypCdIn.trim();
            if (s.length() > 0) {
                Iterator i        =
                    grammar.getVarHypTypSet().iterator();
                while (i.hasNext()) {
                    typ       = (Cnst)i.next();
                    if (typ.getId().equals(s)) {
                        return typ;
                    }
                }
            }
        }
        else {
            s                     = new String("");
        }

        throw new VerifyException(
            LangConstants.ERRMSG_DEFINE_WORK_VAR_TYPE_BAD_1
            + s
            + LangConstants.ERRMSG_DEFINE_WORK_VAR_TYPE_BAD_2);

    }

    /**
     *  Validates an input string as being a valid WorkVar
     *  Prefix.
     *  <p>
     *  Duplicate checking is not done here, just checking
     *  to make sure that the input prefix characters are
     *  valid Metamath "math symbols".
     *  <p>
     *  @param workVarPrefixIn String of Metamath math symbols.
     *  @return workVarPrefix string, trimmed.
     *
     *  @throws VerifyException if workVarPrefixIn has non
     *          math symbols or is null or empty.
     */
    public String editWorkVarPrefix(String workVarPrefixIn)
                    throws VerifyException {
        String s;
        if (workVarPrefixIn != null) {
            s                     = workVarPrefixIn.trim();
            if (s.length() > 0) {
                if (Statementizer.isValidMathSymbol(s)) {
                    return s;
                }
            }
        }
        else {
            s                     = new String("");
        }

        throw new VerifyException(
            LangConstants.ERRMSG_DEFINE_WORK_VAR_PREFIX_BAD_1
            + s
            + LangConstants.ERRMSG_DEFINE_WORK_VAR_PREFIX_BAD_2);
    }

    /**
     *  Validates an input string specifying the number of
     *  WorkVar objects to be declared for a specific Type Code.
     *  <p>
     *  Basically this is just a range check: greater than
     *  some number (9) and less than some other number (999).
     *  <p>
     *  @param nbrWorkVarsIn Number of WorkVar objects for a
     *                       given Type Code.
     *  @return nbrWorkVars input int converted to Integer
     *                       (for storage in ArrayList).
     *
     *  @throws VerifyException if nbrWorkVarsIn out of range.
     */
    public Integer editNbrWorkVars(int nbrWorkVarsIn)
                    throws VerifyException {
        if (nbrWorkVarsIn <
            LangConstants.NBR_WORK_VARS_FOR_TYPE_MIN
                ||
            nbrWorkVarsIn >
            LangConstants.NBR_WORK_VARS_FOR_TYPE_MAX) {
            throw new VerifyException(
                LangConstants.ERRMSG_DEFINE_WORK_VAR_NBR_BAD_1
                + nbrWorkVarsIn
                + LangConstants.ERRMSG_DEFINE_WORK_VAR_NBR_BAD_2
                + LangConstants.NBR_WORK_VARS_FOR_TYPE_MIN
                + LangConstants.ERRMSG_DEFINE_WORK_VAR_NBR_BAD_3
                + LangConstants.NBR_WORK_VARS_FOR_TYPE_MAX
                );
        }

        return Integer.valueOf(nbrWorkVarsIn);
    }

    /**
     *  Returns true if input Work Var is allocated.
     *  <p>
     *  Note: this is a weird function, since how could
     *        an unallocated WorkVar be input to see
     *        whether or not it is allocates? BUT...
     *        consult StepUnifier.finalizeAndLoadAssrtSubst()
     *        for explanatory remarks...
     *
     *  @return true if input Work Var is allocated.
     */
    public boolean isAllocated(WorkVar workVar) {
        return allocated[
                workVar.getActiveVarHyp().getTyp().workVarTypIndex][
                workVar.workVarIndex];

    }

    /**
     *  Deallocates Work Vars that have updates and
     *  eliminates chains of WorkVar updates so that
     *  a Work Var update subtree contains only
     *  non-updated Work Vars.
     *  <p>
     *  Processing is by Type Code, in reverse order
     *  by Work Var number within Type Code so that
     *  subsequent alloc() calls begin with the
     *  lowest deallocated Work Var numbers.
     *  <p>
     *  Example: WorkVar "A" where = B and B = C
     *  results in update clone of A.paSubst: A = C.
     */
    public void resolveWorkVarUpdates() {

        ParseNode holdParseNode;

        for (int i = declaredWorkVar.length - 1;
             i >= 0;
             i--) {

            for (int j = declaredWorkVar[i].length - 1;
                 j >= 0;
                 j--) {

                if (!allocated[i][j]) {
                    continue;
                }

                if ((holdParseNode = declaredWorkVarHyp[i][j].paSubst)
                    == null) {
                    continue;
                }

                if (holdParseNode.hasUpdatedWorkVar()) {

                    declaredWorkVarHyp[i][j].paSubst
                                  =
                        holdParseNode.
                            cloneResolvingUpdatedWorkVars();
                }

                dealloc(i,
                        j);
            }
        }
    }

    /**
     *  Allocates the WorkVar specified by the input
     *  token if necessary assuming it is a valid
     *  WorkVar token string.
     *  <p>
     *  If the WorkVar is already allocated it just
     *  returns the WorkVar instance.
     *  <p>
     *  The input String must consist of a prefix
     *  which matches one of the specified WorkVar
     *  prefixes, followed by a positive integer
     *  within the range of WorkVar numbers specified
     *  for the Type Code corresponding to the
     *  prefix.
     *  <p>
     *  @param workVarIn token specifying a WorkVar (id)
     *  @return allocated WorkVar or null.
     *  @throws IllegalArgument exception if workVarIn
     *                 is null or an empty String.
     */
    public WorkVar alloc(String workVarIn) {

        if (workVarIn == null ||
            workVarIn.length() == 0) {
            throw new IllegalArgumentException(
                LangConstants.ERRMSG_BOGUS_WORK_VAR_IN_ALLOC_1);
        }

        String  suffix;
        int     suffixNbr;

        loop: for (int i = 0;
                   i < declaredWorkVarPrefix.length;
                   i++) {

            if (!workVarIn.startsWith(
                    declaredWorkVarPrefix[i])) {
                continue loop;
            }

            suffix                =
                workVarIn.substring(
                    declaredWorkVarPrefix[i].length());

            if (suffix.length() == 0
                ||
                suffix.charAt(0) == '-') {
                continue loop;
            }

            try {
                suffixNbr         = Integer.parseInt(suffix);
            }
            catch (NumberFormatException e) {
                continue loop;
            }

            if (suffixNbr < 1
                ||
                suffixNbr > declaredWorkVar[i].length) {
                continue loop;
            }

            return alloc(i,
                         --suffixNbr);
        }

        return null;
    }

    /**
     *  Allocates a given WorkVar.
     *  <p>
     *  It is hard to envision using this function...
     *  <p>
     *  @param workVar to be allocated.
     *  @return allocated WorkVar.
     */
    public WorkVar alloc(WorkVar workVar) {

        int i                     =
            workVar.getActiveVarHyp().getTyp().workVarTypIndex;
        int j                     =
            workVar.workVarIndex;
        return alloc(i,
                     j);
    }

    /**
     *  Allocates a new WorkVar for the input Type Code
     *  and returns the corresponding WorkVarHyp.
     *  <p>
     *  @param typ Cnst Type Code of WorkVar to be allocated.
     *  @return allocated WorkVar's WorkVarHyp.
     *  @throws VerifyException if no remaining WorkVar
     *          objects are available for use with the given
     *          Type Code.
     */
    public WorkVarHyp allocWorkVarHyp(Cnst typ)
                        throws VerifyException {
        return (WorkVarHyp)(alloc(typ).getActiveVarHyp());
    }

    /**
     *  Allocates a new WorkVar for the input Type Code.
     *  <p>
     *  @param typ Cnst Type Code of WorkVar to be allocated.
     *  @return allocated WorkVar.
     *  @throws VerifyException if no remaining WorkVar
     *          objects are available for use with the given
     *          Type Code.
     */
    public WorkVar alloc(Cnst typ)
                        throws VerifyException {

        int i                     = typ.workVarTypIndex;

        for (int j = prevAllocIndex[i] + 1;
             j < allocated[i].length;
             j++) {
            if (!allocated[i][j]) {
                return alloc(i,
                             j);
            }
        }

        for (int j = 0; j <= prevAllocIndex[i]; j++) {
            if (!allocated[i][j]) {
                return alloc(i,
                             j);
            }
        }

        throw new VerifyException(
            LangConstants.ERRMSG_TOO_FEW_WORK_VAR_FOR_TYP_1
            + typ.getId()
            + LangConstants.ERRMSG_TOO_FEW_WORK_VAR_FOR_TYP_2
            + declaredNbrWorkVars[i]
            + LangConstants.ERRMSG_TOO_FEW_WORK_VAR_FOR_TYP_3
            );
    }

    private WorkVar alloc(int i,
                          int j) {

        if (!allocated[i][j]) {
            allocated[i][j]       = true;
            declaredWorkVar[i][j].getActiveVarHyp().paSubst
                                  = null;
        }
        prevAllocIndex[i]         = j;
        return declaredWorkVar[i][j];
    }

    /**
     *  Deallocates all Work Variables.
     */
    public void deallocAll() {

        int len                   = allocated.length;
        allocated                 = new boolean[len][];

        for (int i = 0; i < declaredNbrWorkVars.length; i++) {
            allocated[i]          =
                new boolean[declaredNbrWorkVars[i]];
            prevAllocIndex[i]     = -1;
        }
    }

    /**
     *  Deallocates a WorkVar given a WorkVarHyp
     *  <p>
     *  @param varHyp workVarHyp to be deallocated.
     */
    public void dealloc(VarHyp varHyp) {

        dealloc(varHyp.getTyp().workVarTypIndex,
                ((WorkVar)varHyp.getVar()).workVarIndex);
    }



    /**
     *  Deallocates a WorkVar.
     *  <p>
     *  @param workVar to be deallocated.
     */
    public void dealloc(WorkVar workVar) {

        dealloc(workVar.getActiveVarHyp().getTyp().workVarTypIndex,
                workVar.workVarIndex);
    }


    /*
     *  Note: prevAllocIndex set to the variable PRIOR TO
     *        the one we are deallocating -- so that the
     *        next allocation will reuse this one.
     */
    private void dealloc(int i,
                         int j) {
        allocated[i][j]           = false;
        setPrevAllocNbr(i,
                        j);  // j instead of j + 1
    }

    /**
     *  Sets the value of prevAllocIndex for a Type Code.
     *  <p>
     *  In other words, if we input '1' for &W1, then the
     *  next allocation will be &W2.
     *  <p>
     *  If the input prevAllocNbr is invalid a default
     *  setting of zero is used instead.
     *  <p>
     *  @param typ Cnst Type Code of prevAllocIndex to set.
     *  @param prevAllocNbr is one greater than prevAllocIndex
     */
    public void setPrevAllocNbr(Cnst typ,
                                int  prevAllocNbr) {
        setPrevAllocNbr(typ.workVarTypIndex,
                        prevAllocNbr);
    }

    private void setPrevAllocNbr(int typIndex,
                                 int prevAllocNbr) {
        if (prevAllocNbr < 0
            ||
            prevAllocNbr > allocated[typIndex].length) {
            prevAllocNbr          = 0;
        }
        prevAllocIndex[typIndex]  = prevAllocNbr - 1;
    }

    /**
     *  Checks to make sure that the array of defined plus
     *  default WorkVar prefix Strings contains no duplicates.
     *
     *  @throws VerifyException if duplicate WorkVar prefixes
     *                          specified.
     */
    private void checkForDuplicateWorkVarPrefixes()
                    throws VerifyException {

        for (int i = 0;
             i < declaredWorkVarPrefix.length - 1;
             i++) {
            for (int j = i + 1;
                 j < declaredWorkVarPrefix.length;
                 j++) {
                if (!declaredWorkVarPrefix[i].equals(
                        declaredWorkVarPrefix[j])) {
                    continue;
                }
                throw new VerifyException(
                    LangConstants.ERRMSG_DEFINE_WORK_VAR_PFX_DUP_1
                        + declaredWorkVarPrefix[i]
                        + LangConstants.
                            ERRMSG_DEFINE_WORK_VAR_PFX_DUP_2
                        + declaredTypCd[i].getId()
                        + LangConstants.
                            ERRMSG_DEFINE_WORK_VAR_PFX_DUP_3
                        + declaredTypCd[j].getId()
                        );
            }
        }
    }

    /**
     *  Checks to make sure that the array of WorkVar objects
     *  is disjoint with the Symbol and Statement tables.
     *
     *  @throws VerifyException if a WorkVar is found with the
     *                          same Id as a Var or Stmt in the
     *                          LogicalSystem for the input file.
     */
    private void checkWorkVarNamespaceUniqueness(
                                        LogicalSystem logicalSystem)
                    throws VerifyException {

        Map     symTbl            = logicalSystem.getSymTbl();
        Map     stmtTbl           = logicalSystem.getStmtTbl();
        Object  o;
        String  s;
        for (int i = 0; i < declaredWorkVar.length; i++) {
            for (int j = 0; j < declaredWorkVar[i].length; j++) {
                s                 = declaredWorkVar[i][j].getId();
                o                 = symTbl.get(s);
                if (o == null) {
                    o             = stmtTbl.get(s);
                    if (o == null) {
                        continue;
                    }
                }
                throw new VerifyException(
                    LangConstants.ERRMSG_DEFINE_WORK_VAR_DUP_1
                        + s
                        );
            }
        }
    }
}
