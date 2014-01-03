//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * EraseWffsPreprocessRequest.java  0.01 03/01/2008
 *
 * Version 0.01:
 * ==> New.
 */

package mmj.pa;

import java.io.*;

/**
 * EraseWffsPreprocessRequest implements a user request for an erasure operation
 * on non-QED Derivation Step formulas in a proof text area.
 */
public class EraseWffsPreprocessRequest extends PreprocessRequest {

    String iString;
    LineNumberReader r;
    StringWriter w;

    /**
     * Constructor for EraseWffsPreprocessRequest
     */
    public EraseWffsPreprocessRequest() {
        super();
    }

    /**
     * Erases formulas on non-QED Derivation Steps which contain a Ref label.
     * <p>
     * Non-qed Derivation Steps begin in column 1 with a digit, and if the Ref
     * is present then there must be two colons (":") in the first token and the
     * first token must not end with a ":" (otherwise the Proof Worksheet will
     * error out anyway.)
     * 
     * @param proofTextArea Proof Worksheet text string
     * @return proofTextArea modified by editing operation
     */
    @Override
    public String doIt(final String proofTextArea) throws ProofAsstException {
        String firstToken;
        try {
            r = new LineNumberReader(new StringReader(proofTextArea));
            w = new StringWriter(proofTextArea.length());

            iString = r.readLine();
            while (iString != null) {
                if (iString.length() > 0
                    && Character.isDigit(iString.charAt(0)))
                {
                    firstToken = getFirstTokenIfHasRefLabel(iString);
                    if (firstToken != null) {
                        eraseWff(firstToken);
                        continue;
                    }
                }
                copyProofWorkStmt();
            }
            return w.toString();
        } catch (final IOException e) {
            throw new ProofAsstException(PaConstants.ERRMSG_ERASE_WFFS_ERROR,
                e.getMessage());
        }
    }

    private String getFirstTokenIfHasRefLabel(final String s) {
        final StringBuilder sb = new StringBuilder();
        int cntColons = 0;
        int i = 0;
        char c;
        while (true) {
            if (i >= s.length())
                break;
            c = s.charAt(i++);
            if (c == PaConstants.FIELD_DELIMITER_COLON)
                cntColons++;
            else if (Character.isWhitespace(c))
                break;
            sb.append(c);
        }

        if (cntColons != PaConstants.MAX_FIELD_DELIMITER_COLONS
            || sb.charAt(sb.length() - 1) == PaConstants.FIELD_DELIMITER_COLON)
            return null;
        return sb.toString();
    }

    private void copyProofWorkStmt() throws IOException {

        while (true) {
            w.write(iString);
            w.write("\n");
            iString = r.readLine();
            if (iString == null)
                break;
            if (iString.length() == 0
                || Character.isWhitespace(iString.charAt(0)))
                continue;
            break;
        }
    }

    private void eraseWff(final String firstToken) throws IOException {

        w.write(firstToken);
        w.write("\n");

        while (true) {
            iString = r.readLine();
            if (iString == null)
                break;
            if (iString.length() == 0
                || Character.isWhitespace(iString.charAt(0)))
                continue;
            break;
        }
    }
}
