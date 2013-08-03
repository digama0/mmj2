//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  RunParmFile.java.java  0.04 11/01/2011
 *
 *  Nov-1-2006: Version 0.03:
 *          --> Fixed bug involving "" input parms.
 *  Nov-01-2011 - Version 0.04
 *          --> Modified for mmj2 Paths Enhancement
 */

package mmj.util;

import java.io.*;

/**
 *  RunParmFile reads lines designed to be parsed by
 *  DelimitedTextParser and returns RunParmArrayEntry
 *  objects.
 *  <p>
 *  Comment lines are identified in RunParmFileArrayEntry
 *  and this class knows nothing whatsoever about mmj
 *  or Metamath.
 *  <p>
 *  Coincidentally (ha), the "String[] args" parameter
 *  for the constructor is the same as the BatchMMJ2
 *  command line parms.
 */
public class RunParmFile {

    private   String              runParmFileNameArgument;

	private   File                runParmFile;

    private   BufferedReader      runParmFileReader;

    private   String              inputLine
    							= null;

    private   int                 lineCount
    							= 0;

    private   boolean             eofReached
    							= false;

    private   char                delimiter
    							=
    	UtilConstants.RUNPARM_FIELD_DELIMITER_DEFAULT;

    private   char                quoter
    							=
        UtilConstants.RUNPARM_FIELD_QUOTER_DEFAULT;

	/**
	 *  Dumps the absolute path names to System.out for
	 *  testing purposes.
	 */
	public static void displayArgumentOptionReport(
						Paths  paths,
		               	String runParmFileNameArgument)
		               	    throws IOException {

		File   f;
		String absolutePath     = null;

		if (runParmFileNameArgument != null) {
			f                   =
				paths.
					buildMMJ2FilePath(
						runParmFileNameArgument);
			if (f != null) {
				absolutePath    = f.getAbsolutePath();
			}
		}

		System.out.println(
			UtilConstants.RUN_PARM_FILE_REPORT_LINE_1
			+ absolutePath
//			+ UtilConstants.RUN_PARM_FILE_REPORT_LINE_2
			);
	}

    /**
     *  Construct using <code>Paths</code> object and
     *  runParmFileName argument.
     *  <p>
     *  @param paths mmj2 Paths object.
     *  @param runParmFileNameArgument file name of RunParm file.
     *
     *  @throws IllegalArgumentException see UtilConstants.
     */
    public RunParmFile(Paths  paths,
		               String runParmFileNameArgument)
                throws  IOException,
                        IllegalArgumentException {

		this.runParmFileNameArgument
		                        = runParmFileNameArgument;

		try {
			runParmFile             =
				paths.
					buildMMJ2FilePath(
						runParmFileNameArgument);
		}
		catch (NullPointerException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_FILE_NOT_FOUND_1
                + runParmFileNameArgument
                + UtilConstants.ERRMSG_RUNPARM_FILE_NOT_FOUND_2
                + e.getMessage());
        }

		try {
			runParmFileReader 	=
				new BufferedReader(
					new InputStreamReader(
						new FileInputStream(
							runParmFile)));
        }
        catch(FileNotFoundException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_FILE_NOT_FOUND_1
                + runParmFile.getAbsolutePath()
                + UtilConstants.ERRMSG_RUNPARM_FILE_NOT_FOUND_2
                + e.getMessage());
        }
        catch(SecurityException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_FILE_NOT_FOUND_1
                + runParmFile.getAbsolutePath()
                + UtilConstants.ERRMSG_RUNPARM_FILE_NOT_FOUND_2
                + e.getMessage());
        }

		inputLine               = runParmFileReader.readLine();
        if (inputLine == null) {
            throw new IllegalStateException(
                    UtilConstants.ERRMSG_RUNPARM_FILE_EMPTY
                    + runParmFile.getAbsolutePath());
        }

        lineCount = 1;


    }

    /**
     *  Checks to see if another line of input is available.
     *
     *  @return true if another line of input is available.
     */
    public boolean hasNext() {
        if (inputLine == null) {
            return false;
        }
        return true;
    }


    /**
     *  Returns next line of RunParmFile formatted as
     *  a fully parsed RunParmArrayEntry object.
     *
     *  @return RunParmArrayEntry object.
     *
     *  @throws IllegalStateException if called after EOF.
     *  @throws IllegalArgumentException if parsing problem.
     *  @throws IOException if I/O error on RunParmFile.
     */
    public RunParmArrayEntry next()
            throws  IOException,
                    IllegalArgumentException,
                    IllegalStateException {

        RunParmArrayEntry ae    = null;

        if (inputLine == null) {
            if (eofReached) {
                throw new IllegalStateException(
                    UtilConstants.ERRMSG_RUNPARM_NEXT_AFTER_EOF
                    + runParmFile.getAbsolutePath());
            }
            eofReached          = true;
        }
        else {
            ++lineCount;

            ae                  =
            	new RunParmArrayEntry(
                	new DelimitedTextParser(inputLine,
				                            delimiter,
										    quoter));

            inputLine           = runParmFileReader.readLine();
        }

        return ae;
    }

    /**
     *  Close RunParmFile.
     */
    public void close() throws IOException {
        runParmFileReader.close();
        return;
    }


    /**
     *  Returns the canonical path name of the RunParmFile.
     *  <p>
     *  @return canonical path of RunParmFile or empty string
     *          if RunParmFile is null.
     */
	public String getAbsolutePath() {
		String s                = new String("");
		if (runParmFile != null) {
			s                   = runParmFile.getAbsolutePath();
		}
		return s;
	}
}
