# Quick Start

First, install the software (See [README.md](README.md) and [INSTALL.md](INSTALL.md)). We'll assume you also have the Metamath set.mm database installed; by default we assume it's the file c:\set.mm\set.mm (Windows) or "$HOME/set.mm/set.mm" (everything else). We'll also assume that mmj2 is installed in C:\mmj2\ (Windows) or "$HOME/mmj2" (everything else). Then:

* Windows: Run mmj2\mmj2jar\mmj2.bat (e.g., double-click from Windows Explorer)
* MacOS: Run mmj2/mmj2jar/mmj2.command (e.g., double-click from Finder)
* Linux/Unix/Cygwin: Run mmj2/mmj2jar/mmj2 (e.g., double-click from your GUI desktop environment)

If you give the mmj2 command `-d DATABASE.mm` it will try to load database `DATABASE.mm`. Otherwise, it will try to load the last database it loaded, and if it's never loaded a database before, it will search various directories to find a set.mm database using various likely places. If you give a database name that does not exist in the current directory (e.g., `iset.mm`), it will search for a datbase with that name.

The mmj2 command normally lets you load and edit .mmp files; you can include the .mmp file on the command line to automatically open it. By default files are loaded and stored in your current directory.

You can also give mmj2 a command script. Add a `-f` option, followed by the filename of the run parameters script such as `RunParams.txt`.

Note: mmj2 takes a time to get started (circa 60 seconds on a slow machine loading the large database set.mm). Once it gets started and you've done your first unification it should generally be quite fast.

**Starting the tutorial**: mmj2 comes with an interactive tutorial. Use "File/Open New Proof File", if it asks if you want to save changes you probably should say "No", then go to the first tutorial file (this is probably `../PATutorial/Page101.mmp` depending on where you installed it) If you just want to watch it, you can watch a [Walkthrough of the tutorial in mmj2](https://www.youtube.com/watch?v=87mnU1ckbI0).

There are some sample run parameter scripts in the mmj2jar directory, though you may need to copy and edit them for your purposes. The `mmj2` command executes the underlying mmj2.jar JAR file that implements mmj2; if you need to control it directly, there is documentation on mmj2 JAR file [Command Line Arguments](doc/mmj2CommandLineArguments.html). The file [AnnotatedRunParams.txt](mmj2jar/AnnotatedRunParms.txt) explains the RunParams.txt file.
