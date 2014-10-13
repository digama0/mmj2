package mmj.oth;

import mmj.lang.*;

public class HOLLogicalSystem extends LogicalSystem {

    public HOLLogicalSystem() {
        super(OTConstants.HOL_PROVABLE_LOGIC_STMT_TYPE,
            OTConstants.HOL_LOGIC_STMT_TYPE, null, null, new SeqAssigner(),
            LangConstants.SYM_TBL_INITIAL_SIZE_DEFAULT,
            LangConstants.STMT_TBL_INITIAL_SIZE_DEFAULT, null, null);

    }

}
