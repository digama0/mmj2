if (executionMode == ExecutionMode.WORKSHEET_PARSE) {
	proofWorksheet = macroStmt.proofWorksheet;
	messages = proofWorksheet.messages;
	argsRaw = macroStmt.stmtText.toString();
	function log() {
		messages.accumInfoMessage(
			Array.prototype.slice.call(arguments).join(" "),[]);
	}
}
