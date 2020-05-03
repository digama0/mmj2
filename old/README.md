
# README

    //********************************************************************/
    //* Copyright (C) 2005 thru 2011                                     */
    //* MEL O'CAT : x178g243 at yahoo.com                                */
    //* License terms: GNU General Public License Version 2              */
    //*                or any later version                              */
    //********************************************************************/
    //*4567890123456 (71-character line to adjust editor window) 23456789*/

### As Of 1-Nov-2011


For Info about the latest release features click on:

* [CHGLOG.TXT](CHGLOG.TXT)
* [mmj2CommandLineArguments.html](doc/mmj2CommandLineArguments.html)
* [mmj2\doc\GMFFDoc\*](doc/GMFFDoc)
* [mmj2.html](mmj2.html)

See also: [QuickStart.html][1] and [README.html][2]

* * *

*You can now support mmj2 development with $$$ donations via Paypal to siskiyousis at gmail.com!*

*And, for questions, bugs, enhancements, etc. contact Mel at X178G243 at yahoo.com*

* * *
    -----------------------------------------------------------------
    Quick Start CHEAT SHEET!!!  (see also QuickStart.html)
    -----------------------------------------------------------------   

       Quick Start!!!:
      
       Windows:  Double-click mmj2jar\mmj2.bat in Windows Explorer
      
       Mac OS-X: Double-click mmj2jar\MacMMJ2.command in Finder

       BUT...WAIT!...before running mmj2, edit, if needed:

       EDIT-->Windows    mmj2jar\RunParms.txt
              using      mmj2jar\mmj2.bat
              Notepad:   mmj2jar\mmj2PATutorial.bat

           -->Mac OS-X   mmj2jar\RunParms.txt
              using      mmj2jar\MacRunParmsPATutorial.txt                
              TextEdit:  mmj2jar\MacMMJ2.command
                         mmj2jar\MacMMJ2PATutorial.command
 
       (Double-click works well now because the new "mmj2 Fail Popup
       Window" *not only* provides start-up and "fail" error
       messages, but it also forces the Windows Command Prompt (Mac
       OS-X utilities terminal.app) window to stay open, which makes
       it possible to see all of the mmj2 output about the error.)

* * *

0. mmj2 The key idea of mmj2 and its Proof Assistant -- why you want it --&nbsp; is that it has a "GUI" Proof Assistant which is said to be much easier to use than Metamath.exe.

 mmj2 provides a set of tools designed for use with `Metamath.exe` and its associated utilities, such as `eimm.exe` (Import/Export mmj2 Proof Worksheet). In addition to the new and improved mmj2 Proof Assistant GUI -- by far, the most interesting and useful part of mmj2 -- the mmj2 software package provides capabilities such as validation of .mm files; proof verification; grammatical/syntax analysis of .mm files; printing; Text Mode Formula Formatting ("TMFF"), a novel "pretty printing" feature that may facilitate comprehension of complicated math and logic formulas written in Metamath's ASCII shorthand; and MUCH MORE...

1. Installation: Refer to [INSTALL.html][3] for instructions on installing and taking the next steps to run and use mmj2. Then visit or return to [mmj2.html][4] to see the other documentation, such as the [mmj2 Proof Assistant User Guide][5]. Once you have installed mmj2 be sure to try the interactive [mmj2 Proof Assistant Tutorial][6] (which ought to take about an hour.)

2. Requirements:


 * You should have a modern PC or Mac/Unix/Linux computer with at least 2GB of memory and a good, fast hard drive for a good experience using mmj2, which is a bit of a memory hog and is processor-intensive after start-up. mmj2, like Metamath.exe, loads an entire Metamath .mm database (file) into memory (and then does an unbelievable amount of computation!)
 * You will also need Sun Microsystem's Oracle's Java, either the Java Development Kit (JDK aka "J2SE") or the Java Runtime Environment (JRE), version 1.5 or higher.
 * Metamath itself, is technically speaking, optional, but in practice a necessity because mmj2 is an add-on, complementary toolkit for Metamath.
 * An excellent [text editor][7] capable of handling large files and Unix-style line endings (even in Windows) is a necessity. See [INSTALL.html][3] for additional info about this.
 * A file compression utility such as [7-Zip][8].

3. Limitations: mmj2 is still in "developmental" status, meaning that it is subject to change, bug fixes and continuing enhancements...at the whim of Mel L. O'Cat (If you don't like the changes you can clone mmj2 under the GNU GENERAL PUBLIC LICENSE and write your own code.) There is NO WARRANTY -- see [LICENSE.TXT][9] (GNU GENERAL PUBLIC LICENSE Version 2, June 1991). Use at your own risk. (In any case, you are strongly urged to always backup your data; to secure your machine from hackers and viruses, whether or not you use mmj2; to feed your PC only clean electricity from a battery-powered UPS; and ... to always remember that the only secure computer is one that is unplugged, re-packed in its original shipping container, and safely stacked in a secure warehouse. I recommend using a firewall such as [Norton][10] which allows you to specify which programs are allowed to access the internet -- and then to block Java from internet access...better yet, disconnect your computer from the internet while you are using mmj2 :-) Better safe than sorry!)

4. Opportunities: mmj2 was begun as an experimental study of Metamath, and development has proceed in stages, with each new enhancement package designed to be as modular and independent from the others as possible. These developmental stages -- from .mm database validation and printing, proof verification, grammatical analysis, "proof assistanting", Text Mode Formula Formatting, etc. -- have advanced the state of&nbsp; the art of Metamath programming by a small amount and provide a foundation for further work. mmj2 is now usable (and almost good enough to throw away and rewrite properly!) But there is still more work, more experimentation and more learning to be done.&nbsp;

mmj2's capabilities are invoked using "RunParm" commands that form a language, albeit without looping or "if" statements. Thus, it is easy to enhance and extend mmj2, even for "one offs": code a new program, a new "RunParm" command or two, and modify the mmj.util.BatchMMJ2.java program, and one can immediately gain programmatic access to a fully parsed, proof-verified Metamath database loaded into memory and ready for use. (Note: the [MMJ2 Service][11] Feature allows your program to call mmj2 or to be called by mmj2.)

5. Shortcomings of mmj2:


 * It is not a "prover".
 * It does not update Metamath .mm databases.
 * Its grammatical analysis module does minimal checking for grammatical ambiguity (this section is stubbed out -- very difficult, requiring lots of work in special cases because the general case is impossible to prove :).
 * It is a single-user system, not designed for concurrent access throughout (but you can run more than one instance of mmj2's Proof Assistant GUI simultaneously.)
 * All messages are output in English.
 * All output is left-to-right.
 * Support for Unicode or non-ASCII input/output is minimal.
 * Metamath RPN-format proofs are only output in the Metamath uncompressed format.
 * Severe data validation errors encountered during start-up, such as a Metamath .mm database parse error produce error messages on the operating system Command Prompt window, as well as the mmj2 Fail Popup Window -- and when you click "ok", processing terminates immediately. Unfortunately, some users will find these messages hard to decipher.
 * A redesign could make better use of the various Metamath software "objects". To a great extent the "objects" are treated mostly as information hiders and as a way to package program functions written "old school" style.
 * The mmj2 object hierarchy -- `Stmt <- Hyp < VarHyp`, etc. --&nbsp; has proven to be resilient and extensible. However, it is clear the Axiom should be subclassed with SyntaxAxiom (and perhaps Definition!), which could easily be accomplished if we knew the grammatical Type Codes at file load time. Also, Proof should be an object in its own right. (Other design decisions are dubious and/or debatable.)
 * `mmj.verify.GRForest.java` and `GRNode.java` can be replaced with a Java `TreeSet` (they are no longer needed except for duplicate checking now that the Earley Parse algorithm is in use.) Plus` mmj.verify.BottomUpParser.java` can be eliminated without loss.
 * Unification in the mmj2 Proof Assistant depends upon there being a single, unique syntax tree for each valid expression. Grammatical productions intended to support things like "x + y + z" in an unambiguous manner may possibly be defined away using "Syntax Proofs", but little work has been done in this area beyond dealing with weq/wceq, etc. in set.mm for the mmj2 Proof Assistant project (see [BasicsOfSyntaxAxiomsAndTypes.html][12]).

6. Next Steps, Possible Enhancements, and Experimental Topics:

 * Enhance the mmj2 Service Feature by adding a Ctrl key on the mmj2 Proof Assistant GUI screen to invoke the current mmj2 Service program (plug-in) program, passing the proof worksheet as input. Upon return from the invoked program, the mmj2 Proof Assistant GUI would unify the (possibly) modified proof worksheet and display the results. The would be one way to connect mmj2 and a user-written step "prover" (or other useful proof modifier).
 * ?

7. Acknowledgements:
  * Norman Megill, Legendary Inventor of [Metamath](http://www.metamath.org)
  * [Raph Levien](http://www.levien.com/), Inventor of `mmverify.py` and [Ghilbert](http://ghilbert.org/)
  * [Robert M. Solovay](http://math.berkeley.edu/~solovay/), Author of `peano.mm`
  * Dick Grune and Ceriel J.H. Jacobs, Authors of ["Parsing Techniques - A Practical Guide"](http://www.cs.vu.nl/~dick/PTAPG.html)
  * Aaron Krowne and Joe Corneli, [PlanetMath](http://planetmath.org/), the original sponsors of mmj2
  * Herbert B. Enderton, Author of "A Mathematical Introduction to Logic" (1972, Academic Press)
  * [http://www.math.ucla.edu/~hbe/amil/index.html](http://www.math.ucla.edu/~hbe/amil/index.html)
  * Richmond H. Thomason, Author of&nbsp; "Symbolic Logic, An Introduction" (1970, Macmillan)
  * Professor Vann McGee, [MIT Open Courseware, Logic I][16]
  * Michael Beeson, Explainer Extraordinaire of [Unification](http://michaelbeeson.com/research/otter-lambda/index.php?include=Unification)
  * Alin Suciu, [Yet Another Efficient Unification Algorithm](http://arxiv.org/pdf/cs/0603080)
  * F. Line, Paul Chapman and many others who provided analysis, feedback, ideas, review and/or testing.
  * Sci-Fi Inspiration: Greg Egan, "[Diaspora][17]"; Charles Stross, "[Accelerando][18]"; Bruce Sterling, "[Distraction][19]"; Neal Stephenson, "[Snow Crash][20]"; and Robert Heinlein, "[Gulf][21]".
  * Everyone else I forgot to list. Thanks!

   [1]: QuickStart.html
   [2]: README.html
   [3]: INSTALL.html
   [4]: mmj2.html
   [5]: doc/PAUserGuide/Start.html
   [6]: mmj2jar/mmj2PATutorial.bat
   [7]: http://www.textpad.com/
   [8]: http://www.7-zip.org/
   [9]: LICENSE.TXT
   [10]: http://us.norton.com/internet-security/
   [11]: doc/mmj2service
   [12]: http://microfox.com/doc/BasicsOfSyntaxAxiomsAndTypes.html
   [13]: http://math.berkeley.edu/~solovay/
   [14]: http://www.cs.vu.nl/~dick/PTAPG.html
   [15]: http://www.math.ucla.edu/~hbe/amil/index.html
   [16]: http://ocw.mit.edu/OcwWeb/Linguistics-and-Philosophy/24-241Logic-IFall2002/CourseHome/index.htm
   [17]: http://gregegan.customer.netspace.net.au/DIASPORA/DIASPORA.html
   [18]: http://www.antipope.org/charlie/blog-static/fiction/accelerando/accelerando-intro.html
   [19]: http://www.amazon.com/Distraction-Bruce-Sterling/dp/0553576399
   [20]: http://www.amazon.com/Snow-Crash-Bantam-Spectra-Book/dp/0553380958
   [21]: http://www.amazon.com/Assignment-Eternity-Robert-Heinlein/dp/0671578650
