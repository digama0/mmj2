package mmj.transforms;

import java.util.*;

import mmj.lang.*;
import mmj.lang.ParseTree.RPNStep;

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

    /**
     * This function finds assertion with more deductive form.
     * 
     * @param assrt - theorem of which more deductive form will be found
     * @param stmts - iterator of StmtTbl that will be searched
     * @return theorem with more deductive form or null if not found
     */
    public Assrt findMoreDeductive(final Assrt assrt, final Iterator<Stmt> stmts)
    {
        if (assrt.getLabel().equals("wi"))
            return null;
        if (assrt.getLabel().equals("ax-mp"))
            while (stmts.hasNext()) {
                final Stmt next = stmts.next();
                if (next.getLabel().equals("mpd")) {
                    final Assrt ret = (Assrt)next;
                    return ret;
                }
            }
        Assrt match = null;
        if (assrt instanceof Theorem || assrt instanceof Axiom)
            while (stmts.hasNext()) {
                final Stmt current = stmts.next();
                if (current instanceof Theorem || current instanceof Axiom) {
                    final Assrt compare = (Assrt)current;
                    if (isMoreDeductive(assrt, compare)) {
                        System.out.println("Theorem " + assrt.getLabel()
                            + " equals " + current.getLabel());
                        if (match == null)
                            match = compare;
                    }
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
    boolean isMoreDeductive(final Assrt assrt1, final Assrt assrt2) {

        if (assrt1.getExprParseTree() == null
            || assrt2.getExprParseTree() == null)
        {
            System.out.print("null at " + assrt1.getLabel());
            return false;
        }
        if (isMoreDeductive(assrt1.getExprParseTree(),
            assrt2.getExprParseTree()) == false)
            return false;
        final Vector<LogHyp> hyp1 = getHypothesis(assrt1);
        final Vector<LogHyp> hyp2 = getHypothesis(assrt2);
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
            if (match == false)
                return false;
        }
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
    Vector<LogHyp> getHypothesis(final Stmt stmt) {

        final Vector<LogHyp> result = new Vector<LogHyp>();

        RPNStep[] node = null;
        if (stmt instanceof Theorem)
            node = ((Theorem)stmt).getProof();
        else
            return result;

        for (final RPNStep element : node)
            if (element.stmt instanceof LogHyp)
                result.add((LogHyp)element.stmt);

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
    Map<VarHyp, VarHyp> checkVar(final Assrt assrt1, final Assrt assrt2) {
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
        for (int i = 0; i < baseHypsTemp.length; i++) {
            baseHyps.add(baseHypsTemp[i].getExprParseTree());
            deductionHyps.add(deductionHypsTemp[i].getExprParseTree());
        }

        boolean found = false;
        while (baseHyps.size() >= 1)
            for (int j = 0; j < deductionHyps.size(); j++) {
                found = false;
                if (isMoreDeductive(baseHyps.get(0), deductionHyps.get(j)) == true)
                {
                    for (int k = 0; k < baseTrees.size(); k++)
                        if (areTreesEqual(baseHyps.get(0).getRoot(), baseTrees
                            .get(k).get(0).getRoot()) == true)
                        {
                            found = true;
                            baseTrees.get(k).add(baseHyps.get(0));
                            deductionTrees.get(k).add(deductionHyps.get(j));
                            break;
                        }
                    if (found == false) {
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
                if (found == true)
                    break;
            }

        for (int i = 0; i < baseTrees.size(); i++)
            if (baseTrees.get(i).size() == 1) {
                fillNameMap(baseTrees.get(i).get(0),
                    deductionTrees.get(i).get(0), nameMap);
                baseTrees.remove(i);
                deductionTrees.remove(i);
                i--;
            }

        if (baseTrees.size() == 0)
            return nameMap;

        int length = 0;
        for (int i = 0; i < baseTrees.size(); i++)
            length += baseTrees.get(i).size();
        final Vector<Vector<Integer>> match = new Vector<Vector<Integer>>();
        for (int i = 0; i < baseTrees.size(); i++) {
            match.add(new Vector<Integer>());
            for (int j = 0; j < baseTrees.get(i).size(); j++)
                match.get(i).add(0);
        }

        int changeIndex = 0;
        int changeGroup = 0;
        int currentIndex = 0;
        int groupIndex = 0;
        int iterator = 0;
        boolean success = false;
        final Map<VarHyp, VarHyp> currentMap = new HashMap<VarHyp, VarHyp>();
        if (assrt1.getLabel().equals("syl") && assrt2.getLabel().equals("syld"))
            System.out.println("smth");

        while (true) {
            currentMap.clear();
            currentMap.putAll(nameMap);

            currentIndex = 0;
            groupIndex = 0;
            iterator = 0;
            while (iterator < length) {
                success = true;
                final boolean result = fillNameMap(baseTrees.get(groupIndex)
                    .get(currentIndex),
                    deductionTrees.get(groupIndex).get(currentIndex),
                    currentMap);
                if (result == false) {
                    success = false;
                    break;
                }

                currentIndex++;
                if (currentIndex == baseTrees.get(groupIndex).size()) {
                    currentIndex = 0;
                    groupIndex++;
                }
                iterator++;
            }

            changeIndex = 0;
            changeGroup = 0;
            while (true) {
                int num = match.get(changeGroup).get(changeIndex);
                num++;
                if (num == match.get(changeGroup).size()) {
                    match.get(changeGroup).set(changeIndex, 0);
                    changeIndex++;
                    if (changeIndex == baseTrees.get(changeGroup).size()) {
                        changeIndex = 0;
                        groupIndex++;
                        if (groupIndex >= baseTrees.size())
                            return null;
                    }
                    continue;
                }
                else {
                    match.get(changeGroup).set(changeIndex, num);
                    break;
                }
            }

            if (success == false)
                continue;
            return currentMap;
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
    boolean fillNameMap(final ParseTree base, final ParseTree deduction,
        final Map<VarHyp, VarHyp> nameMap)
    {
        final ParseNode node2 = deduction.getRoot();
        final ParseNode node1 = base.getRoot();

        if (node2.stmt.getLabel().equals("wi") == false)
            return false;
        final ParseNode leftChildNode = node2.getChild()[0];
        if (leftChildNode.stmt instanceof VarHyp == false)
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
    boolean fillNameMap(final ParseNode tree1, final ParseNode tree2,
        final Map<VarHyp, VarHyp> nameMap)
    {
        if (tree1 == null || tree2 == null)
            return false;

        if (tree1.getChild().length != tree2.getChild().length)
            return false;
        if (tree1.stmt instanceof VarHyp) {
            if (tree2.stmt instanceof VarHyp) {
                final VarHyp hyp1 = (VarHyp)tree1.stmt;
                final VarHyp hyp2 = (VarHyp)tree2.stmt;
                final VarHyp get = nameMap.get(hyp1);
                if (get == null)
                    nameMap.put(hyp1, hyp2);
                else if (get != hyp2)
                    return false;
                return true;
            }
            else
                return false;
        }
        else if (tree1.stmt != tree2.stmt)
            return false;

        for (int i = 0; i < tree1.getChild().length; i++)
            if (fillNameMap(tree1.getChild()[i], tree2.getChild()[i], nameMap) == false)
                return false;

        return true;
    }

    /**
     * This function returns assertions that were used to proof this theorem.
     * 
     * @param theorem - theorem that will be used to get assertions
     * @return vector with assertions or null if error.
     */
    Vector<Assrt> getAssrt(final Theorem theorem) {
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
    boolean isMoreDeductive(final ParseTree formula1, final ParseTree formula2)
    {
        formula1.getRoot();
        final ParseNode node2 = formula2.getRoot();
        final ParseNode node1 = formula1.getRoot();

        if (node2.stmt.getLabel().equals("wi") == false)
            return false;
        final ParseNode leftChildNode = node2.getChild()[0];
        if (leftChildNode.stmt instanceof VarHyp == false)
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
    boolean areTreesEqual(final ParseNode tree1, final ParseNode tree2) {
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
            if (areTreesEqual(tree1.getChild()[i], tree2.getChild()[i]) == false)
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
    boolean isMoreDeductive(final ParseTree formula1, final ParseTree formula2,
        final Map<VarHyp, VarHyp> nameMap)
    {
        formula1.getRoot();
        final ParseNode node2 = formula2.getRoot();
        final ParseNode node1 = formula1.getRoot();

        if (node2.stmt.getLabel().equals("wi") == false)
            return false;
        final ParseNode leftChildNode = node2.getChild()[0];
        if (leftChildNode.stmt instanceof VarHyp == false)
            return false;

        System.out.println("X");
        debug(1, node1);
        System.out.println("Y");
        debug(1, node2);
        if (areTreesEqual(node1, node2.getChild()[1], nameMap) == true)
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
    boolean areTreesEqual(final ParseNode tree1, final ParseNode tree2,
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
            if (areTreesEqual(tree1.getChild()[i], tree2.getChild()[i], nameMap) == false)
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
    Vector<Integer> getOrder(final Map<VarHyp, VarHyp> map, final Assrt base,
        final Assrt deduction)
    {
        final Vector<Integer> result = new Vector<Integer>();
        final Hyp baseHyps[] = base.getMandFrame().hypArray;
        final Hyp deductionHyps[] = deduction.getMandFrame().hypArray;
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
                        deductionHyps[j].getExprParseTree(), map) == true)
                    {
                        result.add(j);
                        break;
                    }
                }
            }
        final VarHyp left = (VarHyp)deduction.getExprParseTree().getRoot()
            .getChild()[0].stmt;
        int leftIndex = 0;
        for (int i = 0; i < deductionHyps.length; i++)
            if (deductionHyps[i] == left) {
                result.insertElementAt(-1, i);
                leftIndex = i;
                break;
            }
        for (int i = leftIndex + 1; i < deductionHyps.length; i++)
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
     */
    void fillProofList(final Vector<String> proof,
        final Map<Assrt, Assrt> thrm, final Map<LogHyp, LogHyp> hyp,
        final ParseNode node)
    {

        boolean customFill = false;
        if (node.stmt instanceof Theorem) {
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
                    fillProofList(proof, thrm, hyp, node.child[order.get(i)]);
            customFill = true;
        }

        if (node.stmt instanceof Axiom
            && node.stmt.getLabel().equals("wi") == false
            && node.stmt.getLabel().equals("wn") == false)
        {
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
                    fillProofList(proof, thrm, hyp, node.child[order.get(i)]);
            customFill = true;
        }
        if (customFill == false)
            for (final ParseNode element : node.child)
                fillProofList(proof, thrm, hyp, element);

        if (node.stmt instanceof Theorem) {
            System.out.println("Adding:" + thrm.get(node.stmt).getLabel()
                + " in replacement for Theorem:" + node.stmt);
            proof.add(thrm.get(node.stmt).getLabel());
        }
        else if (node.stmt instanceof LogHyp) {
            System.out.println("Adding:" + hyp.get(node.stmt).getLabel()
                + " in replacement for LogHyp:" + node.stmt);
            proof.add(hyp.get(node.stmt).getLabel());
        }
        else if (node.stmt instanceof Axiom) {
            if (node.stmt.getLabel().equals("wi"))
                proof.add("wi");
            else if (node.stmt.getLabel().equals("wn"))
                proof.add("wn");
            else
                proof.add(thrm.get(node.stmt).getLabel());
        }
        else
            proof.add(node.stmt.getLabel());
    }

    /**
     * this function has debbuging use only
     */
    void debug(final int indent, final ParseNode node) {
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
     * @param node
     */
    void findAndFixNullChild(final ParseNode node) {
        if (node.child == null) {
            System.out.println("FOUND NULL CHILD!");
            node.child = new ParseNode[0];
        }
        for (final ParseNode element : node.child)
            findAndFixNullChild(element);
    }

    /**
     * this function has debbuging use only
     */
    Stmt findByName(final Map<String, Stmt> map, final String name) {
        final Iterator<Stmt> iterator = map.values().iterator();
        while (iterator.hasNext()) {
            final Stmt next = iterator.next();
            if (next.getLabel().equals(name) == true)
                return next;
        }
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
    Theorem createMoreDeductive(final Axiom axiom,
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
            for (final VarHyp axiomMandVarHyp : axiomMandVarHyps) {
                System.out.println("Mandatory var:"
                    + axiomMandVarHyp.getLabel());
                proof.add(axiomMandVarHyp.getLabel());
            }

            proof.add(axiom.getLabel());

            proof.add("a1i");

            assrt.add("(");
            assrt.add("ka");
            assrt.add("->");
            for (int j = 1; j < axiom.getFormula().getSym().length; j++)
                assrt.add(axiom.getFormula().getSym()[j].toString());
            assrt.add(")");

            try {
                final Theorem result = logicalSystem.addTheorem(
                    axiom.getLabel() + "_d", 100, axiom.getTyp().getId(),
                    assrt, proof, messages);

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

                final VarHyp varHypTestArray[] = result.getMandVarHypArray();
                for (final VarHyp element : varHypTestArray) {
                    System.out.println("Mandatory var:" + element.getLabel());
                    proof.add(element.getLabel());
                }

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
        final Map<String, Stmt> stmts = logicalSystem.getStmtTbl();

        final Vector<Assrt> usedTheorems = getAssrt(theorem);
        final HashMap<Assrt, Assrt> deductiveTheoremMap = new HashMap<Assrt, Assrt>();
        for (int i = 0; i < usedTheorems.size(); i++) {
            if (usedTheorems.get(i).getLabel().equals("wi")
                || usedTheorems.get(i).getLabel().equals("wn"))
                continue;
            Assrt moreDeductive = findMoreDeductive(usedTheorems.get(i), stmts
                .values().iterator());
            if (moreDeductive == null) {
                System.out.println("Cant find:" + usedTheorems.get(i));
                if (usedTheorems.get(i) instanceof Axiom) {
                    System.out.println("Everything is based on axiom: "
                        + usedTheorems.get(i).getLabel());
                    moreDeductive = createMoreDeductive(
                        (Axiom)usedTheorems.get(i), logicalSystem, messages);
                    if (moreDeductive == null)
                        return null;
                }
                else {
                    System.out.println("Creating more deductive:"
                        + usedTheorems.get(i).getLabel());
                    moreDeductive = createMoreDeductive(
                        (Theorem)usedTheorems.get(i), logicalSystem, messages);
                }
                if (moreDeductive == null) {
                    System.out.println("Cant get more deductive for "
                        + usedTheorems.get(i).getLabel());
                    return null;
                }
            }
            deductiveTheoremMap.put(usedTheorems.get(i), moreDeductive);
        }

        final Vector<LogHyp> hyps = getHypothesis(theorem);
        final HashMap<LogHyp, LogHyp> deductiveHypMap = new HashMap<LogHyp, LogHyp>();
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
        fillProofList(proof, deductiveTheoremMap, deductiveHypMap, node);
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

        try {
            final Theorem result = logicalSystem.addTheorem(theorem.getLabel()
                + "_d", 100, theorem.getTyp().getId(), assrt, proof, messages);

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

            return result;
        } catch (final LangException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Fail");
            return null;
        }

    }

}
