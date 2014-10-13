package mmj.oth;

import java.util.List;
import java.util.Set;

public interface Type {
    Set<VarType> getTypeVars();

    Type subst(List<List<List<Object>>> subst);
}