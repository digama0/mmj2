package mmj.transforms;

import mmj.lang.Stmt;

class GeneralizedStmt {
    final Stmt stmt;
    final ConstSubst constSubst;
    final PropertyTemplate template;

    final int[] varIndexes;

    public GeneralizedStmt(final ConstSubst constSubst,
        final PropertyTemplate template, final int[] varIndexes,
        final Stmt stmt)
    {
        super();
        this.template = template;
        this.constSubst = constSubst;
        this.varIndexes = varIndexes;
        this.stmt = stmt;
        assert varIndexes.length == 2;
    }
}