package mmj.transforms;

import java.util.*;

import mmj.lang.*;
import mmj.lang.ParseTree.RPNStep;
import mmj.pa.ProofAsst;

/**
 * This class is a prototype of a class that increase the deduction level of a
 * theorem. <code> If we had:
 * 
 * h1::
 * hypothesis ... qed:: assertion
 * It will turn it into:
 * 
 * h1::
 * ( ph -> hypothesis )
 * ...
 * qed:: ( ph -> assertion )
 * 
 * <\code>
 * This transformation will be called here
 * "increasing the deduction level". Assertion form that is equal to the other
 * assertion but has form of ( ph -> assertion ), where ph can be any wff will
 * be called here "more deductive form"
 */
public class DeductionTransformation {

    final private ProofAsst proofAsst;
    final private Messages messages;

    public DeductionTransformation(final ProofAsst useProofAsst,
        final Messages useMessages)
    {
        proofAsst = useProofAsst;
        messages = useMessages;
    }

    /**
     * This function finds assertion with more deductive form.
     *
     * @param assrt - theorem of which more deductive form will be found
     * @param stmts - collection of statemets that will be searched
     * @return theorem with more deductive form or null if not found
     */
    public Assrt findMoreDeductive(final Assrt assrt,
        final Collection<Stmt> stmts)
    {
        if (assrt.getLabel().equals("wi"))
            return null;
        if (assrt.getLabel().equals("ax-mp"))
            for (final Stmt stmt : stmts)
                if (stmt.getLabel().equals("mpd")) {
                    final Assrt ret = (Assrt)stmt;
                    return ret;
                }
        Assrt match = null;
        if (assrt instanceof Theorem || assrt instanceof Axiom)
            for (final Stmt current : stmts)
                if (current instanceof Theorem || current instanceof Axiom) {
                    final Assrt compare = (Assrt)current;
                    if (isMoreDeductive(assrt, compare)) {
                        System.out.println("Theorem " + assrt.getLabel()
                            + " equals " + current.getLabel());
                        if (match == null)
                            match = compare;
                    }
                }
        return match;
    }

    /**
     * This function check's if one assertion has more deductive form than the
     * other.
     *
     * @param assrt1 - first assertion
     * @param assrt2 - assertion that can have more deductive form
     * @return true if it is more deductive, false if not.
     */
    private boolean isMoreDeductive(final Assrt assrt1, final Assrt assrt2) {

        if (assrt1.getExprParseTree() == null
            || assrt2.getExprParseTree() == null)
        {
            System.out.print("null at " + assrt1.getLabel());
            return false;
        }
        if (!isMoreDeductive(assrt1.getExprParseTree(),
            assrt2.getExprParseTree()))
            return false;
        final Vector<LogHyp> hyp1 = getHypothesis(assrt1);
        Vector<LogHyp> hyp2 = getHypothesis(assrt2);
        if (hyp1.size() != hyp2.size())
            return false;
        while (hyp1.size() != 0) {
            boolean match = false;
            final ParseTree tree1 = hyp1.get(0).getExprParseTree();
            for (int j = 0; j < hyp2.size(); j++)
                if (isMoreDeductive(tree1, hyp2.get(j).getExprParseTree())) {
                    hyp1.remove(0);
                    hyp2.remove(j);
                    match = true;
                    break;
                }
            if (!match)
                return false;
        }
        final Stmt antecedent = assrt2.getExprParseTree().getRoot().child[0].stmt;
        hyp2 = getHypothesis(assrt2);
        for (int i = 0; i < hyp2.size(); i++)
            if (hyp2.get(i).getExprParseTree().getRoot().child[0].stmt != antecedent)
                return false;
        if (checkVar(assrt1, assrt2) != null)
            return true;
        return false;
    }

    /**
     * This function return list of hypothesis used by an assertion.
     *
     * @param stmt - statement which hypothesis will be returned
     * @return hypothesis of given statement
     */
    private Vector<LogHyp> getHypothesis(final Stmt stmt) {

        final Vector<LogHyp> result = new Vector<LogHyp>();

        RPNStep[] node = null;
        if (stmt instanceof Theorem)
            node = ((Theorem)stmt).getProof();
        else
            return result;

        for (final RPNStep element : node)
            if (element != null) {
                if (element.stmt instanceof LogHyp)
                    result.add((LogHyp)element.stmt);
            }
            else {
                System.out.println("NAME:" + stmt.getLabel());
                return null;
            }

        final Vector<LogHyp> cleared = new Vector<LogHyp>();
        for (int i = 0; i < result.size(); i++) {
            boolean match = false;
            for (int j = 0; j < cleared.size(); j++)
                if (result.get(i) == cleared.get(j))
                    match = true;
            if (match)
                continue;
            cleared.add(result.get(i));
        }
        return cleared;
    }
    /**
     * This function makes more careful check if one assertion is more deductive
     * than the other. It looks which variables in assertion corresponds to
     * which variables in other assertion.
     *
     * @param assrt1 - assertion
     * @param assrt2 - assertion that has more deductive form
     * @return map where key is variables of assertion and values is variables
     *         of deductive assertion.
     */
    private Map<VarHyp, VarHyp> checkVar(final Assrt assrt1, final Assrt assrt2)
    {
        final Map<VarHyp, VarHyp> nameMap = new HashMap<VarHyp, VarHyp>();
        final Vector<Vector<ParseTree>> baseTrees = new Vector<Vector<ParseTree>>();
        final Vector<Vector<ParseTree>> deductionTrees = new Vector<Vector<ParseTree>>();

        baseTrees.add(new Vector<ParseTree>());
        deductionTrees.add(new Vector<ParseTree>());
        baseTrees.get(0).add(assrt1.getExprParseTree());
        deductionTrees.get(0).add(assrt2.getExprParseTree());

        final LogHyp baseHypsTemp[] = assrt1.getLogHypArray();
        final LogHyp deductionHypsTemp[] = assrt2.getLogHypArray();
        final Vector<ParseTree> baseHyps = new Vector<ParseTree>();
        final Vector<ParseTree> deductionHyps = new Vector<ParseTree>();

        if (baseHypsTemp.length != deductionHypsTemp.length)
            return null;

        for (int i = 0; i < baseHypsTemp.length; i++) {
            baseHyps.add(baseHypsTemp[i].getExprParseTree());
            deductionHyps.add(deductionHypsTemp[i].getExprParseTree());
        }

        boolean found = false;
        while (baseHyps.size() >= 1)
            for (int j = 0; j < deductionHyps.size(); j++) {
                found = false;
                if (isMoreDeductive(baseHyps.get(0), deductionHyps.get(j))) {
                    for (int k = 0; k < baseTrees.size(); k++)
                        if (areTreesEqual(baseHyps.get(0).getRoot(), baseTrees
                            .get(k).get(0).getRoot()))
                        {
                            found = true;
                            baseTrees.get(k).add(baseHyps.get(0));
                            deductionTrees.get(k).add(deductionHyps.get(j));
                            break;
                        }
                    if (!found) {
                        found = true;
                        final Vector<ParseTree> baseAddition = new Vector<ParseTree>();
                        baseAddition.add(baseHyps.get(0));

                        final Vector<ParseTree> deductionAddition = new Vector<ParseTree>();
                        deductionAddition.add(deductionHyps.get(j));

                        baseTrees.add(baseAddition);
                        deductionTrees.add(deductionAddition);
                    }
                    baseHyps.remove(0);
                    deductionHyps.remove(j);
                }
                if (found)
                    break;
            }

        for (int i = 0; i < baseTrees.size(); i++)
            if (baseTrees.get(i).size() == 1) {
                if (!fillNameMap(baseTrees.get(i).get(0), deductionTrees.get(i)
                    .get(0), nameMap))
                    return null;

                System.out.println("EQ:");
                debug(0, baseTrees.get(i).get(0).getRoot());
                debug(0, deductionTrees.get(i).get(0).getRoot());
                baseTrees.remove(i);
                deductionTrees.remove(i);
                i--;
            }

        for (int i = 0; i < nameMap.size(); i++)
            System.out.println("M:" + nameMap.values().toArray()[i] + " | "
                + nameMap.keySet().toArray()[i]);

        if (baseTrees.size() == 0)
            return nameMap;

        for (int i = 0; i < baseTrees.size(); i++)
            baseTrees.get(i).size();
        final Vector<Vector<Integer>> match = new Vector<Vector<Integer>>();
        for (int i = 0; i < baseTrees.size(); i++) {
            match.add(new Vector<Integer>());
            for (int j = 0; j < baseTrees.get(i).size(); j++)
                match.get(i).add(0);
        }
        for (int i = 0; i < deductionTrees.get(0).size(); i++) {
            System.out.println("[" + i + "]");
            debug(0, deductionTrees.get(0).get(i).getRoot());
            debug(0, baseTrees.get(0).get(i).getRoot());
        }
        boolean success = false;
        final Map<VarHyp, VarHyp> currentMap = new HashMap<VarHyp, VarHyp>();
        final Vector<Vector<ParseTree>> baseTreesCopy = new Vector<Vector<ParseTree>>();
        final Vector<Vector<ParseTree>> deductionTreesCopy = new Vector<Vector<ParseTree>>();
        while (true) {
            baseTreesCopy.clear();
            deductionTreesCopy.clear();
            for (int i = 0; i < baseTrees.size(); i++) {
                baseTreesCopy.add(new Vector<ParseTree>(baseTrees.get(i)));
                deductionTreesCopy.add(new Vector<ParseTree>(deductionTrees
                    .get(i)));
            }
            currentMap.clear();
            currentMap.putAll(nameMap);

            System.out.println("go");
            for (int i = 0; i < currentMap.size(); i++)
                System.out.println("M:" + currentMap.values().toArray()[i]
                    + " | " + currentMap.keySet().toArray()[i]);
            success = true;

            for (int checkGroupIndex = 0; checkGroupIndex < baseTrees.size(); checkGroupIndex++)
            {
                final int currentSize = baseTrees.get(checkGroupIndex).size();
                for (int checkEntryIndex = 0; checkEntryIndex < currentSize; checkEntryIndex++)
                {
                    for (int i = 0; i < deductionTreesCopy.get(0).size(); i++) {
                        System.out.println("[" + i + "]");
                        debug(0, deductionTreesCopy.get(0).get(i).getRoot());
                        debug(0, baseTreesCopy.get(0).get(i).getRoot());
                    }
                    success = fillNameMap(
                        baseTreesCopy.get(checkGroupIndex).get(checkEntryIndex),
                        deductionTreesCopy.get(checkGroupIndex).get(
                            match.get(checkGroupIndex).get(checkEntryIndex)),
                        currentMap);
                    deductionTreesCopy.get(checkGroupIndex).removeElementAt(
                        match.get(checkGroupIndex).get(checkEntryIndex));
                    System.out.print("\n");
                    if (!success)
                        break;
                }
                if (!success)
                    break;
            }
            if (success)
                return currentMap;

            System.out.println("do");
            int changeIndex = 0;
            int changeGroup = 0;
            while (true)
                if (match.get(changeGroup).get(changeIndex) >= match.get(
                    changeGroup).size()
                    - changeIndex - 1)
                {
                    if (changeGroup == baseTrees.size() - 1
                        && changeIndex == baseTrees.get(baseTrees.size() - 1)
                            .size() - 1)
                        return null;
                    if (changeIndex + 1 == match.get(changeGroup).size())
                        changeGroup++;
                    else {
                        match.get(changeGroup).set(changeIndex, 0);
                        changeIndex++;
                    }
                }
                else {
                    match.get(changeGroup).set(changeIndex,
                        match.get(changeGroup).get(changeIndex) + 1);
                    break;
                }
        }

    }
    /**
     * This function adds to map which variables in the assertion corresponds to
     * which variables in deduction assertion.
     *
     * @param base - assertion
     * @param deduction - more deductive assertion
     * @param nameMap - map with variables
     * @return true if 0 or more variables were added, false if some values
     *         stored in nameMap doesn't match.
     */
    private boolean fillNameMap(final ParseTree base,
        final ParseTree deduction, final Map<VarHyp, VarHyp> nameMap)
    {
        final ParseNode node2 = deduction.getRoot();
        final ParseNode node1 = base.getRoot();

        if (!node2.stmt.getLabel().equals("wi"))
            return false;
        final ParseNode leftChildNode = node2.getChild()[0];
        if (!(leftChildNode.stmt instanceof VarHyp))
            return false;

        return fillNameMap(node1, node2.getChild()[1], nameMap);
    }
    /**
     * This function adds to map which variables in the assertion corresponds to
     * which variables in deduction assertion. (Recursive)
     *
     * @param tree1 tree
     * @param tree2 tree that represents more deductive form
     * @param nameMap - map with variables
     * @return true if 0 or more variables were added, false if some values
     *         stored in nameMap doesn't match.
     */
    private boolean fillNameMap(final ParseNode tree1, final ParseNode tree2,
        final Map<VarHyp, VarHyp> nameMap)
    {
        if (tree1 == null || tree2 == null)
            return false;

        if (tree1.getChild().length != tree2.getChild().length)
            return false;

        System.out.print(tree1.getStmt().getLabel() + " ");

        if (tree1.stmt instanceof VarHyp) {
            if (tree2.stmt instanceof VarHyp) {
                final VarHyp hyp1 = (VarHyp)tree1.stmt;
                final VarHyp hyp2 = (VarHyp)tree2.stmt;
                final VarHyp get = nameMap.get(hyp1);
                if (get == null) {
                    System.out.print(hyp1.getLabel() + "(" + hyp2.getLabel()
                        + ") ");
                    nameMap.put(hyp1, hyp2);
                }
                else if (get != hyp2) {
                    System.out.print("(" + get.getLabel() + " vs "
                        + hyp2.getLabel() + ")");
                    return false;
                }
            }
            else
                return false;
        }
        else if (tree1.stmt != tree2.stmt)
            return false;

        for (int i = 0; i < tree1.getChild().length; i++)
            if (!fillNameMap(tree1.getChild()[i], tree2.getChild()[i], nameMap))
                return false;

        System.out.print("true");
        return true;
    }

    /**
     * This function returns assertions that were used to proof this theorem.
     *
     * @param theorem - theorem that will be used to get assertions
     * @return vector with assertions or null if error.
     */
    private Vector<Assrt> getAssrt(final Theorem theorem) {
        final Vector<Assrt> result = new Vector<Assrt>();
        final RPNStep[] node = theorem.getProof();

        for (final RPNStep element : node)
            if (element.stmt instanceof Theorem)
                result.add((Assrt)element.stmt);
            else if (element.stmt instanceof Assrt)
                result.add((Assrt)element.stmt);

        final Vector<Assrt> cleared = new Vector<Assrt>();
        for (int i = 0; i < result.size(); i++) {
            boolean match = false;
            for (int j = 0; j < cleared.size(); j++)
                if (result.get(i) == cleared.get(j))
                    match = true;
            if (match)
                continue;
            cleared.add(result.get(i));
        }
        return cleared;
    }

    /**
     * This function checks if one tree represents more deductive form than the
     * other tree. BUT it doesn't care which variables are used.
     *
     * @param formula1 formula
     * @param formula2 more deductive formula
     * @return true if more deductive, false if not
     */
    private boolean isMoreDeductive(final ParseTree formula1,
        final ParseTree formula2)
    {
        final ParseNode node2 = formula2.getRoot();
        final ParseNode node1 = formula1.getRoot();

        if (!node2.stmt.getLabel().equals("wi"))
            return false;
        final ParseNode leftChildNode = node2.getChild()[0];
        if (!(leftChildNode.stmt instanceof VarHyp))
            return false;

        if (areTreesEqual(node1, node2.getChild()[1]))
            return true;

        return false;
    }

    /**
     * This function checks if one tree represents more deductive form than the
     * other tree. BUT it doesn't care which variables are used. (Recursive)
     *
     * @param tree1 formula
     * @param tree2 more deductive formula
     * @return true if more deductive, false if not
     */
    private boolean areTreesEqual(final ParseNode tree1, final ParseNode tree2)
    {
        if (tree1 == null || tree2 == null)
            return false;

        if (tree1.getChild().length != tree2.getChild().length)
            return false;
        if (tree1.stmt instanceof VarHyp) {
            if (tree2.stmt instanceof VarHyp) {
                if (tree1.stmt.getLabel().equals(tree2.stmt.getLabel()))
                    return true;
                return true;
            }
            else
                return false;
        }
        else if (tree1.stmt != tree2.stmt)
            return false;

        for (int i = 0; i < tree1.getChild().length; i++)
            if (!areTreesEqual(tree1.getChild()[i], tree2.getChild()[i]))
                return false;

        return true;
    }

    /**
     * This function checks if one tree represents more deductive form than the
     * other tree. IT DOES care which variables are used.
     *
     * @param formula1 formula
     * @param formula2 more deductive formula
     * @param nameMap map generated with checkVars function
     * @return true if more deductive, false if not
     */
    private boolean isMoreDeductive(final ParseTree formula1,
        final ParseTree formula2, final Map<VarHyp, VarHyp> nameMap)
    {
        formula1.getRoot();
        final ParseNode node2 = formula2.getRoot();
        final ParseNode node1 = formula1.getRoot();

        if (!node2.stmt.getLabel().equals("wi"))
            return false;
        final ParseNode leftChildNode = node2.getChild()[0];
        if (!(leftChildNode.stmt instanceof VarHyp))
            return false;

        if (areTreesEqual(node1, node2.getChild()[1], nameMap))
            return true;

        return false;
    }

    /**
     * This function checks if one tree represents more deductive form than the
     * other tree. IT DOES care which variables are used. (Recursive)
     *
     * @param tree1 formula
     * @param tree2 more deductive formula
     * @param nameMap map generated with checkVars function
     * @return true if more deductive, false if not
     */
    private boolean areTreesEqual(final ParseNode tree1, final ParseNode tree2,
        final Map<VarHyp, VarHyp> nameMap)
    {
        if (tree1 == null || tree2 == null)
            return false;
        if (tree1.getChild().length != tree2.getChild().length)
            return false;
        if (tree1.stmt instanceof VarHyp) {
            if (tree2.stmt instanceof VarHyp) {
                if (nameMap.get(tree1.stmt) == tree2.stmt) {
                    System.out.println("Look:" + tree1.stmt.getLabel()
                        + " in map:" + nameMap.get(tree1.stmt).getLabel()
                        + " eq:" + tree2.stmt.getLabel());
                    return true;
                }
                else
                    return false;
            }
            else
                return false;
        }
        else if (tree1.stmt != tree2.stmt)
            return false;

        for (int i = 0; i < tree1.getChild().length; i++)
            if (!areTreesEqual(tree1.getChild()[i], tree2.getChild()[i],
                nameMap))
                return false;

        return true;
    }

    /**
     * Assertion's mandatory hypothesis can have different order than the
     * mandatory hypothesis of more deductive assertion has. This function
     * returns which mandatory hypothesis of assertion corresponds to which
     * mandatory hypothesis of more deductive assertion.
     *
     * @param map map created with checkVars function
     * @param base assertion
     * @param deduction more deductive assertion
     * @return vector with RPN order If mandatory hypothesis of assertion has
     *         index P and mandatory hypothesis of more deductive assertion has
     *         index Q then element of vector with index Q will contain P. If
     *         element Q is an Variable Hypothesis of antecedent of more
     *         deductive assertion then element of vector with index Q will
     *         contain -1
     */
    private Vector<Integer> getOrder(final Map<VarHyp, VarHyp> map,
        final Assrt base, final Assrt deduction)
    {
        final Vector<Integer> result = new Vector<Integer>();
        final Hyp baseHyps[] = base.getMandFrame().hypArray;
        final Hyp deductionHyps[] = deduction.getMandFrame().hypArray;
        System.out.println("BASE:" + base.getLabel() + "|" + base.getFormula()
            + " DED:" + deduction.getLabel() + "|" + deduction.getFormula());
        for (final Hyp baseHyp2 : baseHyps)
            System.out.println("B:" + baseHyp2.getLabel());

        for (final Hyp deductionHyp : deductionHyps)
            System.out.println("d:" + deductionHyp.getLabel());

        for (int i = 0; i < map.values().size(); i++)
            System.out.println("h:" + map.values().toArray()[i]);

        for (final Hyp baseHyp : baseHyps)
            for (int j = 0; j < deductionHyps.length; j++) {
                if (baseHyp instanceof VarHyp) {
                    final VarHyp work = (VarHyp)baseHyp;
                    if (map.get(work) == deductionHyps[j]) {
                        result.add(j);
                        break;
                    }
                }
                if (baseHyp instanceof LogHyp) {
                    final LogHyp work = (LogHyp)baseHyp;
                    if (isMoreDeductive(work.getExprParseTree(),
                        deductionHyps[j].getExprParseTree(), map))
                    {
                        result.add(j);
                        break;
                    }
                }
            }
        for (int i = 0; i < result.size(); i++)
            System.out.println("r:" + result.get(i));

        final VarHyp left = (VarHyp)deduction.getExprParseTree().getRoot()
            .getChild()[0].stmt;
        int leftIndex = 0;
        for (int i = 0; i < deductionHyps.length; i++)
            if (deductionHyps[i] == left) {
                result.insertElementAt(-1, i);
                leftIndex = i;
                break;
            }
        for (int i = leftIndex + 1; i < result.size(); i++)
            result.set(i, result.get(i) - 1);
        System.out.println("Theorem:" + base.getLabel());
        int j = 0;
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i) >= 0)
                System.out.println(baseHyps[j].getLabel() + " " + result.get(i)
                    + " " + deductionHyps[result.get(i)]);
            else {
                System.out.println("atcedent" + " " + result.get(i) + " "
                    + "atcedecent");
                j--;
            }
            j++;
        }
        return result;
    }
    /**
     * This function adds theorem labels to proof list that will proof more
     * deductive form of given theorem. (Recursive)
     *
     * @param proof - vector with strings of proof
     * @param thrm - map that maps assertions to their more deductive equivalent
     * @param hyp - map that maps hypothesis to their more deductive equivalent
     * @param node - parse node (usually the root of the tree)
     * @param isNodeVarHyp - is current node a Variable hypotheses
     */
    private void fillProofList(final Vector<String> proof,
        final Map<Assrt, Assrt> thrm, final Map<LogHyp, LogHyp> hyp,
        final ParseNode node, final boolean isNodeVarHyp)
    {
        System.out.println("Entering:" + node.stmt.getLabel());
        boolean customFill = false;

        if (node.stmt instanceof LogHyp) {
            proof.add(node.stmt.getLabel() + "_d");
            return;
        }
        if (node.stmt instanceof Theorem && !isNodeVarHyp) {
            final Theorem current = (Theorem)node.stmt;
            final Hyp hyps[] = current.getMandFrame().hypArray;
            final boolean isVarHyp[] = new boolean[hyps.length];
            for (int i = 0; i < hyps.length; i++)
                if (hyps[i] instanceof VarHyp)
                    isVarHyp[i] = true;
                else
                    isVarHyp[i] = false;
            Assrt moreDeductive = thrm.get(current);
            if (moreDeductive == null)
                moreDeductive = findMoreDeductive(current, proofAsst
                    .getLogicalSystem().getStmtTbl().values());
            if (moreDeductive == null)
                moreDeductive = createMoreDeductive(current,
                    proofAsst.getLogicalSystem(), messages);
            if (moreDeductive == null) {
                System.out.println("Coudn't create more deductive for:"
                    + current.getLabel());
                return;
            }
            thrm.put(current, moreDeductive);
            final Map<VarHyp, VarHyp> map = checkVar((Assrt)node.stmt,
                thrm.get(node.stmt));
            if (map == null)
                System.out.println("NOOOPE");
            final Vector<Integer> order = getOrder(map, (Assrt)node.stmt,
                thrm.get(node.stmt));
            for (int i = 0; i < order.size(); i++)
                if (order.get(i) == -1)
                    proof.add("wka");
                else
                    fillProofList(proof, thrm, hyp, node.child[order.get(i)],
                        isVarHyp[order.get(i)]);
            customFill = true;
            proof.add(moreDeductive.getLabel());
        }

        if (node.stmt instanceof Axiom && !isNodeVarHyp
            && !node.stmt.getLabel().equals("wi")
            && !node.stmt.getLabel().equals("wn"))
        {
            final Axiom current = (Axiom)node.stmt;
            final Hyp hyps[] = current.getMandFrame().hypArray;
            final boolean isVarHyp[] = new boolean[hyps.length];
            for (int i = 0; i < hyps.length; i++)
                if (hyps[i] instanceof VarHyp)
                    isVarHyp[i] = true;
                else
                    isVarHyp[i] = false;
            Assrt moreDeductive = thrm.get(current);
            if (moreDeductive == null)
                moreDeductive = findMoreDeductive(current, proofAsst
                    .getLogicalSystem().getStmtTbl().values());
            if (moreDeductive == null)
                moreDeductive = createMoreDeductiveAxiom(current,
                    proofAsst.getLogicalSystem(), messages);
            if (moreDeductive == null) {
                System.out.println("Coudn't create more deductive for:"
                    + current.getLabel());
                return;
            }
            thrm.put(current, moreDeductive);

            final Map<VarHyp, VarHyp> map = checkVar((Assrt)node.stmt,
                thrm.get(node.stmt));
            if (map == null)
                System.out.println("NOOOPE");
            final Vector<Integer> order = getOrder(map, (Assrt)node.stmt,
                thrm.get(node.stmt));
            for (int i = 0; i < order.size(); i++)
                if (order.get(i) == -1)
                    proof.add("wka");
                else
                    fillProofList(proof, thrm, hyp, node.child[order.get(i)],
                        isVarHyp[order.get(i)]);
            customFill = true;
            proof.add(moreDeductive.getLabel());
        }
        if (!customFill) {
            for (final ParseNode element : node.child)
                fillProofList(proof, thrm, hyp, element, true);
            proof.add(node.stmt.getLabel());
        }
    }
    /**
     * this function has debbuging use only
     *
     * @param indent number of '-' symbols in the start of printed string
     * @param node node to be printed
     */
    private void debug(final int indent, final ParseNode node) {
        for (int i = 0; i < indent; i++)
            System.out.print("-");
        System.out.print(":" + node.stmt.getLabel());
        System.out.print("\n");
        if (node.child == null)
            return;
        for (final ParseNode element : node.child)
            debug(indent + 1, element);

    }

    /**
     * this function has debbuging use only
     *
     * @param node parse tree node
     */
    private void findAndFixNullChild(final ParseNode node) {
        if (node.child == null) {
            System.out.println("FOUND NULL CHILD!");
            node.child = new ParseNode[0];
        }
        for (final ParseNode element : node.child)
            findAndFixNullChild(element);
    }

    /**
     * this function has debbuging use only
     *
     * @param map a map from names to statements
     * @param name searched name of statement
     * @return found statement or null
     */
    public Stmt findByName(final Map<String, Stmt> map, final String name) {
        // TODO: this function is used from other files but has comment
        // 'debbuging use only'

        // TODO: Why not just use "return map.get(name);"
        for (final Stmt stmt : map.values())
            if (stmt.getLabel().equals(name))
                return stmt;
        return null;
    }

    /**
     * this function creates more deductive theorem of the given axiom
     *
     * @param axiom - axiom of which the more deductive theorem will be created
     * @param logicalSystem - logical system where this theorem is stored
     * @param messages - messages to display errors
     * @return more deductive axiom
     */
    private Theorem createMoreDeductiveAxiom(final Axiom axiom,
        final LogicalSystem logicalSystem, final Messages messages)
    {
        final Vector<LogHyp> logHyps = getHypothesis(axiom);
        System.out.println("Size of hypothesis:" + logHyps.size());
        if (logHyps.size() == 0) {
            final Vector<String> proof = new Vector<String>();
            final Vector<String> assrt = new Vector<String>();

            final RPNStep axiomStatement[] = axiom.getExprRPN();
            for (final RPNStep element : axiomStatement)
                proof.add(element.stmt.getLabel());

            proof.add("wka");
            final VarHyp axiomMandVarHyps[] = axiom.getMandVarHypArray();
            for (final VarHyp axiomMandVarHyp : axiomMandVarHyps)
                proof.add(axiomMandVarHyp.getLabel());

            proof.add(axiom.getLabel());

            proof.add("a1i");

            for (int i = 0; i < proof.size(); i++)
                System.out.println("PROOF:" + proof.get(i));

            assrt.add("(");
            assrt.add("ka");
            assrt.add("->");
            for (int j = 1; j < axiom.getFormula().getSym().length; j++)
                assrt.add(axiom.getFormula().getSym()[j].toString());
            assrt.add(")");

            try {
                logicalSystem.beginScope();
                final Theorem result = logicalSystem.addTheorem(
                    axiom.getLabel() + "_d", 5, axiom.getTyp().getId(), assrt,
                    proof, messages);

                if (result == null)
                    System.out.println("Was unable to create axiom-theorem");
                final Stmt wi = findByName(logicalSystem.getStmtTbl(), "wi");
                final Stmt wka = findByName(logicalSystem.getStmtTbl(), "wka");

                final ParseNode exprRoot = new ParseNode();
                exprRoot.stmt = wi;
                exprRoot.child = new ParseNode[2];
                exprRoot.child[0] = new ParseNode(wka);
                exprRoot.child[0].child = new ParseNode[0];
                final ParseTree tree = axiom.getExprParseTree();
                final ParseNode node = tree.getRoot();
                exprRoot.child[1] = node.deepClone();

                findAndFixNullChild(exprRoot);

                result.setExprParseTree(new ParseTree(exprRoot));
                findAndFixNullChild(exprRoot);

                System.out.println("Successful creating axiom replacer");

                logicalSystem.endScope();
                return result;

            } catch (final LangException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Failure with exception");
                return null;
            }
        }
        System.out.println("Axiom:" + axiom.getLabel()
            + " has hypothesis. Can't create.");
        return null;
    }

    /**
     * This function creates more deductive theorem
     *
     * @param theorem - subject of transformation
     * @param logicalSystem - logical system where theorem is stored
     * @param messages - messages to display errors
     * @return more deductive theorem
     */
    public Theorem createMoreDeductive(final Theorem theorem,
        final LogicalSystem logicalSystem, final Messages messages)
    {
        final Stmt wi = findByName(logicalSystem.getStmtTbl(), "wi");

        final Stmt wka = findByName(logicalSystem.getStmtTbl(), "wka");

        final HashMap<Assrt, Assrt> deductiveTheoremMap = new HashMap<Assrt, Assrt>();
        getAssrt(theorem);

        final Vector<LogHyp> hyps = getHypothesis(theorem);
        final HashMap<LogHyp, LogHyp> deductiveHypMap = new HashMap<LogHyp, LogHyp>();

        final RPNStep steps[] = theorem.getProof();
        final ParseTree tree = new ParseTree(steps);
        final ParseNode node = tree.getRoot();
        final Vector<String> proof = new Vector<String>();
        final Vector<String> assrt = new Vector<String>();
        /*
         * for (final RPNStep step : steps) { if (step.stmt instanceof Theorem)
         * p    roof.add(deductiveTheoremMap.get(step.stmt).getLabel()); if
         * (step.stmt instanceof LogHyp)
         * proof.add(deductiveHypMap.get(step.stmt).getLabel()); else
         * proof.add(step.stmt.toString()); }
         */
        /*  if ( theorem.getLabel().equals("con4d") == true )*/
        fillProofList(proof, deductiveTheoremMap, deductiveHypMap, node, false);
        /*  else {
                proof.add("wka");      // ka
                proof.add("wph");      // ph
                proof.add("wps");      // !ps -> !ch
                proof.add("wn");       //
                proof.add("wch");      //
                proof.add("wn");       //
                proof.add("wi");       //
                proof.add("wch");      // ch -> ps
                proof.add("wps");      //
                proof.add("wi");       //
                proof.add("con4d.1_d");// ( ka -> ( ph -> ( !ps -> !ch ) ) )
                proof.add("wps");      //
                proof.add("wch");      //
                proof.add("wka");      //
                proof.add("ax-3_d");   // ( ka -> ( ( !ps -> !ch ) -> ( ch -> ps ) ) )
                proof.add("syld");     //
            }*/
        for (int i = 0; i < proof.size(); i++)
            System.out.println("T:" + proof.get(i));

        assrt.add("(");
        assrt.add("ka");
        assrt.add("->");
        for (int j = 1; j < theorem.getFormula().getSym().length; j++)
            assrt.add(theorem.getFormula().getSym()[j].toString());
        assrt.add(")");

        logicalSystem.beginScope();

        for (int i = 0; i < hyps.size(); i++) {
            final Vector<String> symbols = new Vector<String>();
            symbols.add("(");
            symbols.add("ka");
            symbols.add("->");
            for (int j = 1; j < hyps.get(i).getFormula().getSym().length; j++)
                symbols.add(hyps.get(i).getFormula().getSym()[j].toString());
            symbols.add(")");
            try {
                deductiveHypMap.put(
                    hyps.get(i),
                    logicalSystem.addLogHyp(hyps.get(i).getLabel() + "_d", hyps
                        .get(i).getTyp().toString(), symbols));
                final LogHyp logHyp = deductiveHypMap.get(hyps.get(i));
                final ParseNode exprRoot = new ParseNode();
                exprRoot.stmt = wi;
                exprRoot.child = new ParseNode[2];
                exprRoot.child[0] = new ParseNode(wka);
                exprRoot.child[0].child = new ParseNode[0];
                exprRoot.child[1] = hyps.get(i).getExprParseTree().getRoot()
                    .deepClone();

                findAndFixNullChild(exprRoot);
                logHyp.setExprParseTree(new ParseTree(exprRoot));

                findAndFixNullChild(exprRoot);

            } catch (final LangException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Error by some lang expression");
                return null;
            }
        }

        try {
            final Theorem result = logicalSystem.addTheorem(theorem.getLabel()
                + "_d", 5, theorem.getTyp().getId(), assrt, proof, messages);

            if (result == null) {
                System.out
                    .println("Some error on creating theorem. Result is NULL");
                return null;
            }
            final ParseNode exprRoot = new ParseNode();

            exprRoot.setStmt(wi);

            if (wi == null || wka == null)
                System.out.println("SHEIT NO SHIT WAS DONE!");
            final ParseNode[] children = new ParseNode[2];
            children[0] = new ParseNode(wka);
            children[0].child = new ParseNode[0];

            children[1] = new ParseTree(theorem.getExprParseTree().getRoot())
                .getRoot();

            exprRoot.child = children;

            findAndFixNullChild(exprRoot);
            result.setExprParseTree(new ParseTree(exprRoot));

            findAndFixNullChild(exprRoot);

            logicalSystem.endScope();

            return result;
        } catch (final LangException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Fail");
            return null;
        }

    }

}
