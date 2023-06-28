//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import mmj.lang.Stmt;

/**
 * Generalized statements are used to indicate constructions which could be
 * defined as simple statements and should have some property (usually, closure
 * property).
 * <p>
 * For example: construction ( A + B ) with property " _ e. CC".
 */
class GeneralizedStmt {
    /**
     * The syntax root. In the example it is "( A F B )" statement, "co" in
     * set.mm. Actually, mmj2 stores "co" arguments in the other order:
     * "[A, B, F]".
     */
    final Stmt stmt;

    /**
     * The constant subtrees. In the example it is (null, "+", null). Actually,
     * because of "co" arguments order the substitution will be [null, null, +]
     */
    final ConstSubst constSubst;

    /** The necessary property. In the example it is " _ e. CC". */
    final PropertyTemplate template;

    /**
     * The positions for variables. In the example because of "co" arguments
     * order the variable indexes will be [0, 1].
     */
    final int[] varIndexes;

    public GeneralizedStmt(final ConstSubst constSubst,
        final PropertyTemplate template, final int[] varIndexes, final Stmt stmt)
    {
        this.template = template;
        this.constSubst = constSubst;
        this.varIndexes = varIndexes;
        this.stmt = stmt;
        assert varIndexes.length == 2;
    }

    @Override
    public String toString() {
        return stmt.toString() + ":" + constSubst + " with " + template;
    }
}
