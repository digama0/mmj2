//********************************************************************/
//* Copyright (C) 2011  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  Paths.java.java  0.01 11/01/2011
 *
 *  Nov-01-2011 - Version 0.01
 *          --> new
 */

package mmj.util;

import java.io.*;

/**
 *  <code>Paths</code> holds the path-related command
 *  line arguments and provides several path-related
 *  services.
 */
public class Paths {

	private String 	mmj2PathArgument;
	private String 	metamathPathArgument;
	private String 	svcPathArgument;

	private File	mmj2Path;
	private File	metamathPath;
	private File	svcPath;

    /**
     *  Standard constructor.
     *
     *  @param mmj2PathArgument null or existing mmj2 directory.
     *  @param metamathPathArgument null or existing Metamath directory.
     *  @param svcPathArgument null or existing Svc directory.
     *
     *  @throws IllegalArgumentException if errors found.
     */
    public Paths(String mmj2PathArgument,
				 String metamathPathArgument,
				 String svcPathArgument)
				 	throws IllegalArgumentException,
				 	       IOException {

		this.mmj2PathArgument   = mmj2PathArgument;
		this.metamathPathArgument
								= metamathPathArgument;
		this.svcPathArgument   	= svcPathArgument;

		String s;

        // mmj2 path
		if (mmj2PathArgument == null) {
			s                   = null;
		}
		else {
			s                   =
				(new File(mmj2PathArgument)).
					getAbsolutePath();
		}

		System.out.print(
			UtilConstants.MMJ2_PATH_REPORT_LINE_1
			+ s);

		mmj2Path				=
			loadPathArgument(mmj2PathArgument,
							 UtilConstants.MMJ2_PATH_ARGUMENT_LITERAL);

		System.out.print(
			UtilConstants.PATH_REPORT_E_G_CAPTION_1
			+ buildMMJ2FilePath(
				UtilConstants.PATH_REPORT_EXAMPLE_FILE_NAME).
					getCanonicalPath()
			+ UtilConstants.PATH_REPORT_E_G_CAPTION_2
			+ "\n");

        // metamath path
		if (metamathPathArgument == null) {
			s                   = null;
		}
		else {
			s                   =
				(new File(metamathPathArgument)).
					getAbsolutePath();
		}

		System.out.print(
			UtilConstants.METAMATH_PATH_REPORT_LINE_2
			+ s);

		metamathPath			=
			loadPathArgument(metamathPathArgument,
							 UtilConstants.METAMATH_PATH_ARGUMENT_LITERAL);

		System.out.print(
			UtilConstants.PATH_REPORT_E_G_CAPTION_1
			+ buildMetamathFilePath(
				UtilConstants.PATH_REPORT_EXAMPLE_FILE_NAME).
					getCanonicalPath()
			+ UtilConstants.PATH_REPORT_E_G_CAPTION_2
			+ "\n");


        // svc path
		if (svcPathArgument == null) {
			s                   = null;
		}
		else {
			s                   =
				(new File(svcPathArgument)).
					getAbsolutePath();
		}

		System.out.print(
			UtilConstants.SVC_PATH_REPORT_LINE_3
			+ s);

		svcPath					=
			loadPathArgument(svcPathArgument,
							 UtilConstants.SVC_PATH_ARGUMENT_LITERAL);
		System.out.print(
			UtilConstants.PATH_REPORT_E_G_CAPTION_1
			+ buildSvcFilePath(
				UtilConstants.PATH_REPORT_EXAMPLE_FILE_NAME).
					getCanonicalPath()
			+ UtilConstants.PATH_REPORT_E_G_CAPTION_2
			+ "\n");
	}

	/**
	 *  Builds a <code>File</code> object relative to the
	 *  <code>mmj2Path</code>.
	 *  <p>
	 *  If the input fileName designates an absolute path
	 *  then the output <code>File</code> is not relative to
	 *  <code>mmj2Path</code> or the Current Path.
	 *  <p>
	 *  Otherwise, if <code>mmj2Path</code> is null then the
	 *  output <code>File</code> is relative to the Current Path.
     *  <p>
     *  @param pathName name of file or directory.
     *  @return File object relative to mmj2Path unless
     *          input pathName is absolute or mmj2Path
     *          is null, in which case the output File
     *          is relative to the current path.
	 */
	public File buildMMJ2FilePath(String pathName) {

		File file               = new File(pathName);

		if (getMMJ2Path() == null
				||
		    file.isAbsolute()) {
		}
		else {
			file                =
				new File(
					getMMJ2Path(),
			        pathName);
		}

		return file;
	}

	/**
	 *  Builds a <code>File</code> object relative to the
	 *  <code>metamathPath</code>.
	 *  <p>
	 *  If the input fileName designates an absolute path
	 *  then the output <code>File</code> is not relative to
	 *  <code>metamathPath</code> or the Current Path.
	 *  <p>
	 *  Otherwise, if <code>metamathPath</code> is null then the
	 *  output <code>File</code> is relative to the Current Path.
     *  <p>
     *  @param pathName name of file or directory.
     *  @return File object relative to metamathPath unless
     *          input pathName is absolute or metamathPath
     *          is null, in which case the output File
     *          is relative to the current path.
	 */
	public File buildMetamathFilePath(String pathName) {

		File file               = new File(pathName);

		if (getMetamathPath() == null
		    	||
		    file.isAbsolute()) {
		}
		else {
			file                =
				new File(
					getMetamathPath(),
			        pathName);
		}

		return file;
	}

	/**
	 *  Builds a <code>File</code> object relative to the
	 *  <code>svcPath</code>.
	 *  <p>
	 *  If the input fileName designates an absolute path
	 *  then the output <code>File</code> is not relative to
	 *  <code>svcPath</code> or the Current Path.
	 *  <p>
	 *  Otherwise, if <code>svcPath</code> is null then the
	 *  output <code>File</code> is relative to the Current Path.
     *  <p>
     *  @param pathName name of file or directory.
     *  @return File object relative to svcPath unless
     *          input pathName is absolute or svcPath
     *          is null, in which case the output File
     *          is relative to the current path.
	 */
	public File buildSvcFilePath(String pathName) {

		File file               = new File(pathName);

		if (getSvcPath() == null
				||
			file.isAbsolute()) {
		}
		else {
			file                =
				new File(
					getSvcPath(),
			        pathName);
		}

		return file;
	}

	/**
	 *  Gets the mmj2Path.
	 *  <p>
	 *  @return The mmj2Path File object.
	 */
	 public File getMMJ2Path() {
		 return mmj2Path;
	 }

	/**
	 *  Gets the metamathPath.
	 *  <p>
	 *  @return The mmj2Path File object.
	 */
	 public File getMetamathPath() {
		 return metamathPath;
	 }

	/**
	 *  Gets the svcPath.
	 *  <p>
	 *  @return The mmj2Path File object.
	 */
	 public File getSvcPath() {
		 return svcPath;
	 }

	private File loadPathArgument(String pathArgument,
	                              String pathArgumentLiteral) {

		if (pathArgument == null) {
			return null;
		}

		File path               = new File(pathArgument);

		try {
			if (path.exists() &&
				(!path.isFile())) {
				return path;
			}
			else {
				throw new IllegalArgumentException(
					UtilConstants.ERRMSG_PATH_INVALID_1
					+ pathArgumentLiteral
					+ UtilConstants.ERRMSG_PATH_INVALID_2
					+ path.getAbsolutePath()
					+ UtilConstants.
						ERRMSG_COMMAND_LINE_ARGUMENTS_FORMAT);
			}
		}
		catch (SecurityException e) {
				throw new IllegalArgumentException(
					UtilConstants.ERRMSG_PATH_SECURITY_ERROR_1
					+ pathArgumentLiteral
					+ UtilConstants.ERRMSG_PATH_SECURITY_ERROR_2
					+ path.getAbsolutePath()
					+ UtilConstants.
						ERRMSG_COMMAND_LINE_ARGUMENTS_FORMAT);
		}
	}
}
