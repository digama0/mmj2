//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ScopeDef.java  0.02 08/23/2005
 */

package mmj.lang;

import java.util.ArrayList;
import java.util.List;

/**
 * This is just a simple data structure used to hold the items local to a
 * "scope" level in Metamath. Scopes can be nested to any depth.
 * mmj.lang.LogicalSystem maintains a stack (List) of ScopeDefs.
 */
public class ScopeDef {
    /**
     * List of "active" Var's within a scope level.
     */
    List<Var> scopeVar = new ArrayList<>();

    /**
     * List of "active" VarHyp's within a scope level.
     */
    List<VarHyp> scopeVarHyp = new ArrayList<>();

    /**
     * List of "active" LogHyp's within a scope level.
     */
    List<LogHyp> scopeLogHyp = new ArrayList<>();

    /**
     * List of "active" DjVar's within a scope level.
     */
    List<DjVars> scopeDjVars = new ArrayList<>();
}
