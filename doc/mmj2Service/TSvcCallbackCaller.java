import java.io.*;
import java.util.*;
import mmj.mmio.*;
import mmj.lang.*;
import mmj.pa.*;
import mmj.util.*;
import mmj.verify.*;
import mmj.tmff.*;
import mmj.svc.*;
import mmj.tl.*;

/**
 *  Test mmj2 SvcCallback as "callee"
 */
public class TSvcCallbackCaller implements SvcCallback {
    public TSvcCallbackCaller() {
    }

    public void go(Messages              messages,
                   OutputBoss            outputBoss,
                   LogicalSystem         logicalSystem,
                   VerifyProofs          verifyProofs,
                   Grammar               grammar,
                   WorkVarManager        workVarManager,
                   ProofAsstPreferences  proofAsstPreferences,
                   ProofAsst             proofAsst,
                   TlPreferences         tlPreferences,
                   TheoremLoader         theoremLoader,
                   File                  svcFolder,
                   Map                   svcArgs) {

        System.out.println(
            "Hello world, I am TSvcCallbackCaller.java");

        System.out.println(
            "My home svcFolder is "
            + svcFolder.getAbsolutePath());

        System.out.println(
            "Here are the Map.Entry elements in my svcArgs:");

        Iterator i                =
            svcArgs.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry m           = (Map.Entry)i.next();
            System.out.println(
                "key="
                + m.getKey()
                + " value="
                + m.getValue());
        }

        System.out.println("Sample of unification via ProofAsst");
        try {
            ProofWorksheet w      =
                theoremLoader.
                    getUnifiedProofWorksheet(
                        PaConstants.SAMPLE_PROOF_TEXT,
                        proofAsst,
                        "TSvcCallbackCaller sample");
            System.out.println("Returned Proof Text area");
            System.out.println(w.getOutputProofText());
            System.out.println("Messages follow:");
            System.out.println(w.getOutputMessageText());
        }
        catch (TheoremLoaderException e) {
            System.out.println("Errors found in proof text:");
            System.out.println(e.getMessage());
        }

        System.out.println("Bye!");
    }

    /**
     *  Main function interfacing to Java environment,
     *  running the BatchMMJ2 functions.
     *
     * @param args see class description.
     */
    public static void main(String[] args) {

        TSvcCallbackCaller tSvcCallbackCaller
                                  =
            new TSvcCallbackCaller();

        BatchMMJ2 batchMMJ2       = new BatchMMJ2();

        int returnCode            =
            batchMMJ2.generateSvcCallback(args,
                                          tSvcCallbackCaller);
        if (returnCode != 0) {
            System.exit(returnCode);
        }
    }


}