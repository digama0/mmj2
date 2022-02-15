# Quick Start

First, install the software (See [README.md](README.md) and
[INSTALL.html](INSTALL.html)). We'll assume you also have the Metamath
set.mm database installed; by default we assume it's in the default
directory c:\\set.mm\\ (Windows) or "$HOME/set.mm/" (everything else).
We'll also assume that mmj2 is installed in C:\\mmj2\\ (Windows) or
"$HOME/mmj2" (everything else). Then:

-   Windows: Run mmj2\\mmj2jar\\mmj2.bat (e.g., double-click from
    Windows Explorer)
-   MacOS: Run mmj2/mmj2jar/MacMMJ2.command (e.g., double-click from
    Finder)
-   Linux/Unix: Run mmj2/mmj2jar/mmj2.sh (e.g., double-click from your
    GUI desktop environment)
-   Cygwin: Run mmj2/mmj2jar/mmj2-cygwin.bat (e.g., double-click from
    Windows Explorer)

These files will try to load the Metamath database set.mm, by default
from the directory c:\\set.mm on Windows and "$HOME/set.mm". You can
change this, by either setting the environment variable
METAMATH\_DB\_DIR to the directory you want to use, or can copy that
file and edit it as you wish.

BUT...WAIT!...before running mmj2, you might want to copy that file
somewhere and edit it, or edit one the file mmj2/mmj2jar/RunParams.txt
which defines the commands that mmj2 runs on startup.

Note: mmj2 takes a time to get started (circa 60 seconds on a slow
machine loading the large database set.mm). Once it gets started and
you've done your first unification it should generally be quite fast.

**Starting the tutorial**: mmj2 comes with an interactive tutorial. Use
"File/Open New Proof File", if it asks if you want to save changes you
probably should say "No", then go to the first tutorial file (this is
probably ../PATutorial/Page101.mmp depending on where you installed it)
If you just want to watch it, you can watch a [Walkthrough of the
tutorial in mmj2](https://www.youtube.com/watch?v=87mnU1ckbI0).

There are some other sample starter files in the mmj2jar directory,
though you may need to copy and edit them for your purposes. There is
documentation on mmj2 [Command Line
Arguments](doc/mmj2CommandLineArguments.html). The file
[AnnotatedRunParams.txt](mmj2jar/AnnotatedRunParms.txt) explains the
RunParams.txt file.
