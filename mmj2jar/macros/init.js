messages = batchFramework.outputBoss.messages;
grammar = batchFramework.grammarBoss.grammar;
proofAsst = batchFramework.proofAsstBoss.proofAsst;
logicalSystem = batchFramework.logicalSystemBoss.logicalSystem;
verifyProofs = batchFramework.verifyProofBoss.verifyProofs;
proofAsstPreferences = batchFramework.proofAsstBoss.proofAsstPreferences;
function runMacro(name) {
	batchFramework.macroBoss.runMacro(name)
}
function debug(obj) {
	print(JSON.stringify(obj, null, 4));
}