(function(){
var Provers = mmj.transforms.Provers;
var UseWhenPossible = Provers.UseWhenPossible;
var forbiddenAssrts = new java.util.HashSet();
function P(s) new mmj.lang.ParseNode(getStmt(s), Java.to(
        Array.prototype.slice.call(arguments, 1), "mmj.lang.ParseNode[]"));
function useWhenPossible() {
    for each (s in arguments) {
        trManager.provers.add(new UseWhenPossible(getStmt(s)));
        forbiddenAssrts.add(s);
    }
}
function useWhenPossibleExt(s, f) {
    trManager.provers.add(new UseWhenPossible(getStmt(s), f));
    forbiddenAssrts.add(s);
}
function arrayProver(f, o) {
    var array = new Provers.ArrayProver(f);
    trManager.provers.add(array);
    for (s in o) {
        var f = o[s];
        array.addProver(f ? new UseWhenPossible(getStmt(s), f) :
            new UseWhenPossible(getStmt(s)));
        forbiddenAssrts.add(s);
    }
    return array;
}
var patterns = new java.util.HashMap();
function patternMatch(patt, node) {
    var s = node.stmt.getTyp().getId() + " " + patt;
    var pattern = patterns.get(s);
    if (!pattern)
        patterns.put(s, pattern =
            new mmj.transforms.Pattern(proofAsst, s));
    return pattern.match(node);
}

// This is a list of assertions in set.mm that can be used with UseWhenPossible,
// but are nevertheless "too aggressive" to be used as a general simplification
// tactic (for example a1i, which removes antecedents), or has the same
// conclusion as another assert in the database, forcing the computer to make a
// decision if not resolved by excluding all of them or all but the most common.
var badAssrtList = [
"a1i imim2i imim3i a1d a2d a1ii a1dd pm2.21d pm2.21i pm2.24d pm2.24i pm5.74d",
"anim1i anim2i pm5.32i pm5.32ri ori ord orci olci orcd olcd imori biantru",
"biantrur ancli ancri impac pm4.71rd pm5.32d pm5.32rd pm5.32da pm5.74da imbi2i",
"imdistand imdistanda orcanai intnan intnanr intnand intnanrd bianfi bianfd",
"3mix1i 3mix2i 3mix3i 3exp 3expa 3expb 3expia 3expib 3exp1 3expd 3exp2 exp5o",
"exp516 exp520 3ori 3impexpbicomi hbth nex a4s a4sd a5i dral1 dral1-o dral2",
"dral2-o exdistrf sb6x hbs1f alimd albid exbid moani euf eubid eximd hbexd",
"exlimd nexd mo4f ralbidva reximdva rexbidva ralbiia rexbiia 2rexbiia 2ralbiia",
"2ralbidva rexbida 2rexbidva 2ralbida alrimiv alrimdv rspec rgenw",
"rgen2w ralimiaa ralimia ralimdva ralimdaa ralrimivw r19.21bi rspec2 rspec3",
"reximia reximdvai rexlimivw rexlimdvw reubidv reubiia abbid rabbidva cla4egv",
"rcla4 rcla4e ceqsexg sbcth sbc5 sbc6 sbcieg sbcbidv sbcbidgv sbcth2 csbeq2d",
"sseli sselda elin2 ifeq1da ifeq2da elpw elpr elsnc elsnc2 snssd iineq2d ssbrd",
"ssbri opabbid mpteq2i mpteq2dv mpteq2da inex1 inex2 elpw2 opelopabga elsuc",
"elsuc2 onssneli onssnel2i ralxpf rexxpf releldmi relelrni relbrcnv fconst6",
"ffvelrni fvclex oprabbid fovcl ndmov dfoprab4 riotabidv dominf ac6s4 ac6s5",
"ac9s dominfac sumeq2d sumeq2sdv lbprc ubprc p0cl p0mnl p1cl p1mxl ubtr2 lbtr2",
"lubfun lubeq lubdm glbfun glbeq glbdm joinfun joineq joindm meetfun meeteq",
"meetdm odutos 0chn 0ach 0gpos subljcl sublmcl nhfun nheq nhdm imhfun imheq",
"imhdm subgcl subgsubcl symggrp frgpgrp oppgsubg pwsrng pwscrng opprrng",
"opprsubg subrgacl subrgmcl pwslmod opprnzr opprdomn psr1crng psr1assa psr1tos",
"ply1crng ply1assa psr1rng ply1rng psr1lmod psr1sca psr1sca2 ply1lmod ply1sca",
"ply1sca2 xrsdsreval remetdval ply1nz ply1domn dvlog dvlog2 opidon opidon2",
"ismndo1 ismndo2 sheli cheli ococi pjococi shne0i chne0i pjcompi pjvi",
"nmbdoplbi nmbdfnlbi shatomici hatomici embantd jad 2th 2thd 2false 2falsed",
"jctil jctir jctild jctird im2anan9 bi2anan9 alimd eximd nexd albid exbid hbim",
"a12study eubid mo4f eqeqan12d eqeqan12rd abbid ralbida rexbida ralbid rexbid",
"ralimdaa reximdai ceqsalv ceqsexv rcla4 rcla4e ceqsexg sbcbid csbeq2d iineq2d",
"breqan12d breqan12rd mpteq2da opth opabex oveqan12d oveqan12rd ac6s sumeq12dv",
"lbf oimmxl ohmlb ohmlb2 lubsn2 joinidm2 iseqv4 eqvmxl lubsn joinidm atnlt",
"latjex latjeq latjcl latjidm latjass latj12 latj32 latjrot latj4 latjjdi",
"latjjdir clatlubdm clatlub clatlubcl lubf op0cl grpcl grpplusf grprcan rngcl",
"rngidcl rng0cl rngacl 1unit unitmulcl unitinvcl unitnegcl irredn0 srng1",
"lmodvacl lmodass lmod0vcl lmodvsubcl lmodcom lmod4 lss1 lssvsubcl lidlacl",
"lidl0 lidl1 lpi1 lpiss tgpcn rerest xrrest metdcn2 nvgf nvgcl 0lno shseli",
"shunssji shsleji shjcomi shub1i shjcli omlsi pjpj0i chjcomi ccase2 alrimd",
"exlimd 2ralbida opabbid opelopaba hbfvd axcc4 ac6 readdcani oimlb oimlub",
"oimjoin atcvr0 pltnle latoimj latabs2 isdlat dlatmjdi dlatmjdir planh0",
"oplecon3b oplecon1b opoc1 opltcon3b olj01 olj02 olj11 olj12 hlatexch2",
"atexchcvr grprid grppncan ghmlin mulgdi rngridm dvrcl lmodvpncan ocvlss",
"ngprcan nmmtri rngo0rid rngo0lid nvdi nvpncan nvtri lnof shlej1i shlej2i",
"shlubi chjassi ralxpf rexxpf oprabbid zorn3 opmoc opnoncon oldmm1 oldmm2",
"oldmm3 oldmm4 dvrcan1 lspsntri sspg sspgval sspmval sspm sspival sspi lnoadd",
"fh1i fh3i hl0lt1",

"jaao anim12ii anim12i im2anan9r anim12dan rcla4dv impbid21d r19.21bi anim12ci",
"rcla4edv bi2anan9r ineqan12d 3anassrs wunfv wunop evlfcl"
].join(" ").split(" ");

for each (var s in badAssrtList)
    forbiddenAssrts.add(s);
var badAssrtsRE = /OLD$|ALT$|lem\d*$|^bnj|^cbv|ad.*ant/;
var mathbox = getStmt("mathbox").getSeq();
trManager.buildUWPProvers(assrtList, function(assrt)
        assrt.getSeq() < mathbox &&
        !badAssrtsRE.test(assrt.getLabel()) &&
        !forbiddenAssrts.contains(assrt.getLabel()));

///////////////////////
// Arithmetic engine //
///////////////////////
function arithEval(root) {
    var label = root.stmt.getLabel(), groups;
    var typ = root.stmt.getTyp().getId();
    if (typ.equals("class")) {
        if (groups = patternMatch("; A B", root))
            return 10 * arithEval(groups.get("A")) + arithEval(groups.get("B"));
        if (groups = patternMatch("( A + B )", root))
            return arithEval(groups.get("A")) + arithEval(groups.get("B"));
        if (groups = patternMatch("( A x. B )", root))
            return arithEval(groups.get("A")) * arithEval(groups.get("B"));
        if (groups = patternMatch("( A ^ B )", root))
            return Math.pow(arithEval(groups.get("A")), arithEval(groups.get("B")));
        return label.equals("cc0") ? 0 :
        	label.match(/c\d+/) ? +label.substring(1) : NaN;
    }
    return typ.equals("wff") && (groups = patternMatch("A < B", root)) ?
        arithEval(groups.get("A")) < arithEval(groups.get("B")) : NaN;
}

//strict = 0 or omitted: allow 10 - aT(10) = 10, aT(100) = ; 10 0
//       = 1: no 10 at bottom level - aT(10) = ; 1 0, aT(100) = ; 10 0
//       = 2: no 10s at all - aT(10) = ; 1 0, aT(100) = ; ; 1 0 0
function asTerm(n, strict) isNaN(n) ? null : (strict ? n < 10 : n <= 10) ?
    P(n==0?'cc0':'c'+n) : P("cdc", asTerm(n/10|0, n<2?0:2), asTerm(n%10));
function normalize(root, strict) asTerm(arithEval(root), strict);
function isNormalized(root, strict) root.isDeepDup(normalize(root, strict));

useWhenPossibleExt("nn0cni", // A e. NN0 => A e. CC
        function (info, root, r) !isNaN(arithEval(r.get("A"))));

useWhenPossible("declt"); // B < C => ; A B < ; A C
useWhenPossibleExt("decltc", // C < 10, A < B => ; A C < ; B D
        function (info, root, r) arithEval(r.get("C")) < 10);
useWhenPossibleExt("declti", function (info, root, r) // C < 10 => C < ; A B
        arithEval(r.get("A")) > 0 && arithEval(r.get("C")) < 10);
arrayProver(function (info, root) {
    var groups = patternMatch("A < B", root);
    if (!groups) return null;
    var a = groups.get("A"), b = groups.get("B");
    var na = arithEval(a), nb = arithEval(b);
    if (!(na < nb)) return null;
    return isNormalized(a, 1) ? isNormalized(b) ? null : "breqtrri" :
        isNormalized(b) ? "eqbrtri" : "3brtr4i";
}, {
    // A R B, C = B => A R C
    breqtrri: function (info, root, r) {
        r.set("B", normalize(r.get("C")));
        return true;
    },
    // A = B, B R C => A R C
    eqbrtri: function (info, root, r) {
        r.set("B", normalize(r.get("A"), 1));
        return true;
    },
    // A R B, C = A, D = B => C R D
    "3brtr4i": function (info, root, r) {
        r.set("A", normalize(r.get("C"), 1));
        r.set("B", normalize(r.get("D")));
        return true;
    }
});

useWhenPossible("dec0h", "dec0u");
function addcomli(info, root, r) {
	// ( A + B ) = C => ( B + A ) = C
    var a = r.get("A"), b = r.get("B");
    
	return arithEval(a) < 10 && arithEval(b) < arithEval(a) &&
			isNormalized(a) && isNormalized(b);
}
useWhenPossibleExt("addcomli", addcomli);
useWhenPossibleExt("mulcomli", addcomli);
useWhenPossibleExt("eqcomi", function (info, root, r) // A = B => B = A
    arithEval(r.get("A")) <= 10 && patternMatch("; C D", r.get("B")));
useWhenPossibleExt("eqtr4i", function (info, root, r) {
    // A = B, C = B => A = C
    var c = r.get("C");
    if (arithEval(r.get("A")) != arithEval(c)) return false;
    return !r.set("B", normalize(c)).isDeepDup(c);
});

useWhenPossibleExt("decma", function (info, root, r) {
    // M = ; A B, N = ; C D, ( ( A x. P ) + C ) = E, 
    // ( ( B x. P ) + D ) = F => ( ( M x. P ) + N ) = ; E F
    var m = arithEval(r.get("M")), n = arithEval(r.get("N")),
        p = arithEval(r.get("P"));
    if (!((m >= 10 || n >= 10) && (m%10)*p+n%10 < 10)
    		|| m == 1 || p == 1 || n == 0) return false;
    r.set("A", asTerm(m/10|0)); r.set("B", asTerm(m%10));
    r.set("C", asTerm(n/10|0)); r.set("D", asTerm(n%10));
    return true;
});
function decmac(info, root, r) {
    // M = ; A B, N = ; C D, ( ( A x. P ) + ( C + G ) ) = E, 
    // ( ( B x. P ) + D ) = ; G F => ( ( M x. P ) + N ) = ; E F
    var m = arithEval(r.get("M")), n = arithEval(r.get("N")),
        p = arithEval(r.get("P"));
    if (!(m >= 10 || n >= 10)
    		|| m == 1 || p == 1 || n == 0) return false;
    var gf = (m%10)*p+n%10;
    if (isNaN(gf)) return false;
    r.set("A", asTerm(m/10|0)); r.set("B", asTerm(m%10));
    r.set("C", asTerm(n/10|0)); r.set("D", asTerm(n%10));
    r.set("G", asTerm(gf/10|0)); r.set("F", asTerm(gf%10));
    return true;
};
useWhenPossibleExt("decmac", decmac);
useWhenPossibleExt("decma2c", decmac);

useWhenPossibleExt("decsucc", function (info, root, r) {
    // ( A + 1 ) = B, N = ; A 9 => ( N + 1 ) = ; B 0
    var b = arithEval(r.get("B"));
    if (!(b > 0)) return false;
    r.set("A", asTerm(b-1));
    return true;
});
useWhenPossibleExt("decsuc", function (info, root, r) {
    // ( B + 1 ) = C, N = ; A B => ( N + 1 ) = ; A C
    var c = arithEval(r.get("C"));
    if (!(c > 0)) return false;
    r.set("B", asTerm(c-1));
    return true;
});

function decadd(info, root, r) {
    var m = arithEval(r.get("M")), n = arithEval(r.get("N"));
    r.set("A", asTerm(m/10|0)); r.set("B", asTerm(m%10));
	r.set("C", asTerm(n/10|0)); r.set("D", asTerm(n%10));
	return true;
}
function decaddi(info, root, r) {
    var m = arithEval(r.get("M"));
	r.set("A", asTerm(m/10|0)); r.set("B", asTerm(m%10));
	return true;
}
arrayProver(function (info, root) {
    var groups = patternMatch("( M + N ) = ; E F", root);
    if (!groups) return null;
    var m = arithEval(groups.get("M")), n = arithEval(groups.get("N")),
        f = m%10+n%10;
    if (!(m >= 10 || n >= 10)) return null;
    return n < 10 ?
		(f < 10 ? "decaddi" : f > 10 ? "decaddci" : f == 10 ? "decaddci2" : null) :
    	f < 10 ? "decadd" : f > 10 ? "decaddc" : f == 10 ? "decaddc2" : null;
    r.set("A", asTerm(m/10|0)); r.set("B", asTerm(m%10));
    r.set("C", asTerm(n/10|0)); r.set("D", asTerm(n%10));
    return true;
    var a = groups.get("A"), b = groups.get("B");
    var na = arithEval(a), nb = arithEval(b);
    if (!(na < nb)) return null;
    return isNormalized(a, 1) ? isNormalized(b) ? null : "breqtrri" :
        isNormalized(b) ? "eqbrtri" : "3brtr4i";
}, {decadd: decadd, decaddc: decadd, decaddc2: decadd,
	decaddi: decaddi, decaddci: decaddi, decaddci2: decaddi});

function decmulc(info, root, r) {
    // N = ; A B, ( ( A x. P ) + E ) = C, 
    // ( B x. P ) = ; E D => ( N x. P ) = ; C D
    var n = arithEval(r.get("N")), p = arithEval(r.get("P"));
    if (!(n >= 10)) return false;
    var e = ((n%10)*p)/10|0;
    if (isNaN(e)) return false;
    r.set("A", asTerm(n/10|0)); r.set("B", asTerm(n%10));
    r.set("E", asTerm(e));
    return true;
};
useWhenPossibleExt("decmul1c", decmulc);
useWhenPossibleExt("decmul2c", decmulc);

useWhenPossible("numexp0", "numexp1", "sqvali");
arrayProver(function (info, root) {
    var groups = patternMatch("( A ^ B ) = C", root);
    if (!groups || !isNormalized(groups.get("C"))) return null;
    var m = arithEval(groups.get("A")), n = arithEval(groups.get("B"));
    if (isNaN(m) || !(n >= 2)) return null;
    return n == 2 ? "eqtri" : n%2 == 0 ? "numexp2x" : "numexpp1";
}, {
    // A = B, B = C => A = C
    eqtri: function (info, root, r) {
    	var a = r.get("A").child[0];
        r.set("B", P("co", a, a, P("cmul")));
        return true;
    },
    // ( 2 x. M ) = N, ( A ^ M ) = D, ( D x. D ) = C => ( A ^ N ) = C
    numexp2x: function (info, root, r) {
    	var m = arithEval(r.get("N"))/2|0;
        r.set("M", asTerm(m));
        r.set("D", asTerm(Math.pow(arithEval(r.get("A")), m)));
        return true;
    },
    // ( M + 1 ) = N, ( ( A ^ M ) x. A ) = C => ( A ^ N ) = C
    "numexpp1": function (info, root, r) {
        r.set("M", asTerm(arithEval(r.get("N"))-1));
        return true;
    }
});

useWhenPossibleExt("eqtri", function (info, root, r) {
    // A = B, B = C => A = C
    var a = r.get("A");
    if (!a.stmt.label.equals("co")) return false;
    var d = a.child[0], e = a.child[1];
    if (isNaN(arithEval(d)) || isNaN(arithEval(e))) return false;
    var nd = normalize(d), ne = normalize(e);
    if (d.isDeepDup(nd) && e.isDeepDup(ne)) return false;
    r.set("B", P("co", nd, ne, a.child[2]));
    return true;
});

/////////////////////////
// Structure detection //
/////////////////////////

/**
 * This function looks for steps of the form |- A = ( Const ` B ) and returns
 * the substitutions for A, B, and F = Const
 */ 
function getStructure(root) {
	var groups = patternMatch("A = ( F ` B )", root);
	if (!groups) return null;
	var sym = groups.get("F").stmt.getFormula().getSym();
	return sym.length == 2 && sym[1] instanceof mmj.lang.Cnst &&
			groups.get("A").stmt instanceof mmj.lang.VarHyp &&
			groups.get("B").stmt instanceof mmj.lang.VarHyp ? groups : null;
}

var activeStructure = null;

function Maxifier() {
	var elementList = {}, modeMap = {}, maxStr = null, maxCount = 0;
	return {
		get el() {
			return maxStr == null ? null : elementList[maxStr];
		},
		add: function(root) {
			var groups = getStructure(root);
			if (groups == null) return;
			var b = groups.get("B");
			var str = b.stmt.getVar().getId();
			if (!modeMap[str]) {
				elementList[str] = {base: b, groups: {}};
				modeMap[str] = 1;
			} else modeMap[str]++;
			var f = groups.get("F");
			elementList[str].groups[f.stmt.getFormula().getSym()[1]] =
				{fv: root.child[1], val: groups.get("A")};
			if (modeMap[str] > maxCount) {
				maxStr = str;
				maxCount = modeMap[str];
			}
		}
	}
}

post(CallbackType.AFTER_PARSE, function() {
	var max = Maxifier();
	for each (var step in proofWorksheet.proofWorkStmtList)
		if (step instanceof mmj.pa.HypothesisStep)
			max.add(step.formulaParseTree.getRoot());
	activeStructure = max.el;
	debug(activeStructure);
	return true;
});

proofAsst.proofUnifier.postUnifyHook = function(d, assrt, assrtSubst) {
	if (activeStructure == null) return;
	var max = Maxifier();
	var hypArray = assrt.getMandFrame().hypArray;
	var vars = {};
	for (var i=0; i<hypArray.length; i++)
		if (hypArray[i] instanceof mmj.lang.VarHyp)
			vars[hypArray[i].getVar().getId()] = i;
		else
			max.add(hypArray[i].getExprParseTree().getRoot());
	if (max.el == null) return;
	var i = vars[max.el.base.stmt.getVar().getId()];
	var hyp = assrtSubst[i].stmt;
	if (hyp instanceof mmj.lang.WorkVarHyp)
		assrtSubst[i] = hyp.paSubst = activeStructure.base;
	else return;
	for (var key in max.el.groups) {
		var o = max.el.groups[key];
		i = vars[o.val.stmt.getVar().getId()];
		hyp = assrtSubst[i].stmt;
		if (hyp instanceof mmj.lang.WorkVarHyp)
			assrtSubst[i] = hyp.paSubst = key in activeStructure.groups ?
				activeStructure.groups[key].val : o.fv;
	}
};

})()