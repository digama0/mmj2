// toFol - Translate Metamath proofs to LF/FOL
// Mario Carneiro, 30-Jul-2016
//
// Macro type: RunParm
//
// Arguments: Assrt labels, with * wildcard
// This option translates all the listed theorems into an LF/FOL style proof
// (this version is tailored for MMT, but probably applies in other
// circumstances as well).
//
// Example invocation:
// RunMacro,toFol,*

var imports = new JavaImporter(
	Packages.mmj.lang,
	Packages.mmj.setmm,
	java.util.TreeSet,
	java.util.regex.Pattern);
with (imports) {
var setmm = new SetMMConstants(proofAsst); 
var folTranslator = new FolTranslator(proofAsst, setmm);

var toCheck = new TreeSet(MObj.SEQ);
var matches = new Array(args.length-1);
for (var i = 1; i < args.length; i++) {
	matches[i-1] = Pattern.compile(Pattern.quote(
        args[i].trim()).replace("*", "\\E.*\\Q"));
}
bigLoop: for each (var s in logicalSystem.stmtTbl.values()) {
    if (s instanceof Assrt && s.getTyp() == setmm.DED) {
        for each (var p in matches) {
            if (p.matcher(s.label).matches()) {
                toCheck.add(s);
            	continue bigLoop;
            }
        }
    }
}

for each (var s in toCheck) {
	log(s + ": " + folTranslator.translateAssrt(s).asMMT(0));
}

} // with(imports)
