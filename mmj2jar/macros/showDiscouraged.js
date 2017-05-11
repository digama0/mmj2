// showDiscouraged - Write list of discouraged theorems and their uses
// Mario Carneiro, 30-Jul-2016
//
// Macro type: RunParm
//
// Argument: Output file name, default "discouraged", no extension.
//
// Example invocation:
// RunMacro,showDiscouraged,discouraged

(function(){
var imports = new JavaImporter(
	Packages.mmj.lang,
	java.util,
	java.io);
with (imports) {
var filePath = proofAsstPreferences.store.getMMJ2Path();
var fileNameParm = args.length > 1 ? args[1] : 'discouraged';

var file = new File(fileNameParm);
if (filePath && !file.isAbsolute())
    file = new File(filePath, fileNameParm);

if (file.isDirectory()) {
    messages.accumErrorMessage('E-MA-0002 showDiscouraged: "%s" is not a file', file.getAbsolutePath());
    return;
}
var lines = new TreeSet();
var stmts = new TreeSet(MObj.SEQ);
stmts.addAll(logicalSystem.stmtTbl.values());
function addLine(str) {printStream.println(str);}
var useDisc = new TreeMap(MObj.SEQ);
var useListTemp = new HashSet();
for each (var s in stmts) {
	if (s.description && s.descriptionForSearch.contains('(New usage is discouraged.)'))
		useDisc.put(s, new ArrayList());
	if (s instanceof Theorem) {
		if (s.description && s.descriptionForSearch.contains('(Proof modification is discouraged.)'))
			lines.add('Proof modification of "'+s.label+'" is discouraged ('+s.proof.length+' steps).');
		useListTemp.clear();
		for each (var rpn in s.proof)
			if (rpn && rpn.stmt && useDisc.containsKey(rpn.stmt))
				useListTemp.add(rpn.stmt);
		for each (var used in useListTemp)
			useDisc.get(used).add(s);
	}
}
for each (var entry in useDisc.entrySet()) {
	lines.add('New usage of "'+entry.key.label+'" is discouraged ('+entry.value.length+' uses).');
	for each (var stmt in entry.value)
		lines.add('"'+entry.key.label+'" is used by "'+stmt.label+'".');
}

var printStream = new PrintStream(file);
for each (var line in lines)
	printStream.println(line);
printStream.close();
log("I-MA-0001 Show discouraged output to "+file);

} // with(imports)
})()