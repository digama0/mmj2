ExecutionMode = Packages.mmj.pa.MacroManager.ExecutionMode;
CallbackType = Packages.mmj.pa.MacroManager.CallbackType;
messages = batchFramework.outputBoss.messages;
grammar = batchFramework.grammarBoss.grammar;
proofAsst = batchFramework.proofAsstBoss.proofAsst;
logicalSystem = batchFramework.logicalSystemBoss.logicalSystem;
verifyProofs = batchFramework.verifyProofBoss.verifyProofs;
proofAsstPreferences = batchFramework.proofAsstBoss.proofAsstPreferences;
macroManager = batchFramework.macroBoss.macroManager;
log = print;
function runMacro(name) {
	macroManager.runMacro(name)
}
function eval(code) {
	macroManager.evalRaw(code)
}
(function() {
	var callbacks = [];
	post = function(type, f) {
		var index = type.ordinal();
		if (index in callbacks)
			callbacks[index].push(f);
		else {
			callbacks[index] = [f];
			macroManager.setCallback(type, function() {
				if (index in callbacks) {
					var list = callbacks[index];
					while (list.length > 0)
						(list.shift())();
				}
			});
		}
	}
	function reset() {
		callbacks = [];
		post(CallbackType.BEFORE_PARSE, reset);
	};
	reset();
})();
macroManager.setPrepMacro("prep");
post(CallbackType.BUILD_GUI, function() {
	proofAsstGUI = proofAsst.proofAsstGUI;
	function log() {
		messages.accumInfoMessage(
			Array.prototype.slice.call(arguments).join(" "),[]);
	}
});
function setKeyCommand(key, f) {
	post(CallbackType.BUILD_GUI, function() {
		with (new JavaImporter(Packages.javax.swing)) {
			var window = proofAsstGUI.mainFrame.getContentPane();
			window.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(key), key);
			window.getActionMap().put(key,
				new (Java.extend(AbstractAction, f))());
		}
	});
}
function unify() {
	proofAsstGUI.unificationAction(false, false,
		null, null, null).actionPerformed(null);
}
////////////////////////////////////////////////////////
// USER SPACE: perform extra custom initializations here
////////////////////////////////////////////////////////
