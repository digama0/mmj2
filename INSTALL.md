# Installation instructions

## Brief install instructions

In brief:

- Install Java.
  Java version 11+ is recommended and easier
  to use (we provide a precompiled version).
  You only *need* the Java Runtime Environment (JRE), but
  unless you're short of disk space, you should probably install the
  full Java Development Kit (JDK).
  Beware: the license for Oracle's Java implementation changed in 2019.
  Make sure you use an OpenJDK implementation of Java,
  fully comply with Oracle's terms for free use of Oracle JDK, or
  pay Oracle their Oracle JDK fee and comply with those
  different legal terms.
- (Recommended) Install git and a good text editor.
- Install mmj2 somewhere. A common location for a local single-user install
  is C:\mmj2 for Windows and $HOME/mmj2 for anything else,
  but you can put it anywhere you want.
- (Optional) Compile mmj2. This isn't necessary if you use Java 11,
  because we provide a precompiled jar file.
- (Recommended) Install the Metamath database file set.mm somewhere.
  A common location for a local single-user install
  is C:\set.mm\set.mm for Windows and $HOME/set.mm/set.mm for anything else,
  but you can put it anywhere you want (e.g., C:\metamath\set.mm on Windows).
  The reason for the doubled "set.mm" is that the "set.mm" repository
  contains several databases, including the set.mm database.
- (Recommended) Install Metamath-exe program in its
  conventional place, which is C:\metamath for Windows
  and $HOME/metamath for anything else.

Then follow the [Quick Start guide](quickStart.md).

Below is more detail on how to do this for various systems.

## Full install instructions

First, *back up your data*.
We don't know of any problems, but everyone is human, there
are no guarantees with anything, and we expressly state there's no warranty.
In fact, you should have backups whether or not you ever use this software.

mmj2 requires Java to run.
Some major versions of Java are "long term support" (LTS) versions.
In particular, major versions 8 and 11 are long term support versions.
We recommend using version 11.
There are also various implementations of Java, in particular,
Oracle's Java implemenation and various OpenJDK implementations.

**BEWARE**: Oracle's implementation of Java
has historically been widely used, but
Oracle's Java license *radically* changed on April 16, 2019.
Oracle permits certain non-commercial uses at no cost,
but *many* uses require paying a fee - even some you might think
are non-commercial.
If you don't pay those fees to Oracle ahead-of-time,
you risk large fines for you and any company you work for,
and Oracle has a reputation for being litigious.
If you use the Oracle implementation, make *sure* all
your uses meet Oracle's license terms.
If you're not sure you'll meet Oracle's terms, consult your lawyer.
An alternative is to use one of the various "openjdk" implementations
of Java, which work fine and is what we suggest below.

The text below shows how to install mmj2 for various systems:
Windows, Linux/Unix/Cygwin, and MacOS (MacOS is really a kind of Unix).
Java includes the "Swing" class which is used to implement a portable GUI.

### Windows

These are the instructions for a conventional Windows install.
You can also install Cygwin on Windows and use mmj2; if you do that,
follow the Windows instructions for installing Java, then switch to
the Linux/Unix/Cygwin instructions.

#### Install Java

First, install Java.
Tehcnically you only need a Java Runtime Environment (JRE)
implementation, but unless you're short of space it doesn't hurt to have a
full Java Development Kit (JDK) since the JDK includes a JRE.

Legally the safest thing to do is install an implementation
of OpenJDK.
We recommend JDK version 11 (at least) to run.
There are many ways to get an OpenJDK implementation on Windows,
here are two:

- [AdoptOpenJDK](https://adoptopenjdk.net/) provides
  prebuilt OpenJDK implementations for many systems.
- [Red Hat provides an implementation of OpenJDK for Windows](https://developers.redhat.com/openjdk-install/);
  you can go there to get one.

An alternative way to install Java would be to install an
[Oracle Java release](https://www.oracle.com/java/technologies/javase-downloads.html);
make absolutely *certain* that you
comply with Oracle's licenses if you do this
(the free one, in particular, has many restrictions on how you
can legally use it).

#### (Recommended) Install git and a good text editor

The "git" program is a very widely used version control system
that supports global collaborative development of software and
software-like artifacts.
You can get a Windows version from the
[Git SCM downloads](https://git-scm.com/downloads).

You will almost certainly need a good text editor that can handle
large files, and preferably one that can handle POSIX standard line endings
(LF instead of Windows' CRLF).
The programs `Notepad` and `Wordpad` are **not**
good text editors.
There are many available; if you have no idea, you might try
[Notepad++](https://notepad-plus-plus.org/), which is small,
simple, and gets the job done.
Other options include [Atom](https://atom.io/),
[vim](https://www.vim.org/)
[Microsoft Visual Studio Code](https://code.visualstudio.com/).

#### Install mmj2

Install mmj2; a common place is "C:\mmj2" on Windows, but it can be anywhere.
Click on the "start" button, type "command", press ENTER.
First, `cd` to whatever directory you want to contain mmj2. E.g.:

~~~~
cd c:\
~~~~

Then use `git` to create a directory with its current state:

~~~
git clone https://github.com/digama0/mmj2.git
cd mmj2
~~~~

#### (Optional) Compile mmj2

You do not have to compile mmj2, because we provide a
precompiled jar file.
If you want to recompile it, here's what you do.

First, if you directly downloaded the mmj2 git repository, as recommended, run
the following from the top-level "mmj2" directory:

~~~~
git submodule init
git submodule update
~~~~

Now to actually compile it in Windows, presuming it's in its conventional
C:\mmj2 location, run:

~~~~
C:\mmj2\compile\windows\CompMMJ.bat
~~~~

This creates the `c:\mmj2\mmj2jar\mmj2jar.jar`
jar file, in addition to creating the various mmj2 class files.

You should also compile the Javadoc for mmj2.
Use the Command Prompt window to execute the following command:
`C:\mmj2\doc\windows\BuildDoc.bat`

#### (Recommended) Install the Metamath database set.mm

You don't have to use the set.mm database with mmj2, but the mmj2 tutorial
uses it, and many mmj2 users will want it.

Install the database file set.mm. A common place is
C:\set.mm\set.mm for Windows. You can do this with:

~~~~
cd \
git clone https://github.com/metamath/set.mm.git
~~~~

Note that the first `cd` line chooses where the directory you want to contain
the directory of databases; use a different value if you want it
somwhere else.

#### (Optional) Install Metamath-exe

You can install metamath-exe.
A conventional place on Windows is C:\metamath.
See [Downloads](http://us.metamath.org/#downloads) for how to do this.

#### (Optional) Create a desktop icon

On the start menu type
`C:\mmj2\mmj2jar` to open that directory
and then right-click on the `mmj2.bat` file.
Create a shortcut, then drag that to the desktop.

Note: Cygwin users should create a shortcut on the shell file
`mmj2` instead.

### Unix/Linux

#### Install Java

First, install Java.
Tehcnically you only need a Java Runtime Environment (JRE)
implementation, but unless you're short of space it doesn't hurt to have a
full Java Development Kit (JDK) since the JDK includes a JRE.
Almost all widely-used systems have Java easily available as
an installable package.
For example:

- On Ubuntu and Debian, install OpenJDK 11 as follows:
    - Install the JDK version 11 with: `sudo apt install openjdk-11-jdk`
    - You can install multiple different Java versions, e.g.,
      version 8 JDK is package `openjdk-8-jdk`, and the current
      default Java version is package `default-jdk`.
      Replace "-jdk" with "-jre" if you're short of disk space and *only*
      want the Java Runtime Environment (JRE).
      If you have more than one version, you can select between them using
      `sudo update-alternatives --config java`

- On Fedora, install OpenJDK 11 as follows:
    - Install JDK version 11 with:
      `sudo dnf install java-11-openjdk.x86_64`
    - You can replace "11" with "1.8" or "latest" to get version 8 or the
      latest version (respectively).

An alternative way to install Java would be to install an
[Oracle Java release](https://www.oracle.com/java/technologies/javase-downloads.html); make absolutely *certain* that you
comply with Oracle's licenses if you do this.

#### Install git and a good text editor

Install git:

- On Debian and Ubuntu this is `sudo apt install git`
- On Fedora this is `sudo dnf install git`

Linux systems all come with at least one good text editor and
many alternatives.

#### Install mmj2

Install mmj2; a common place is "$HOME/mmj2" on everything but Windows,
but it can be
placed anywhere. One way to do this is with these Terminal commands
to download it using git:

~~~~
cd
git clone https://github.com/digama0/mmj2.git
cd mmj2
~~~~

You can also download its current release using your web browser and going to
<https://github.com/digama0/mmj2>

#### (Optional) Compile mmj2

You do not have to compile mmj2, because we provide a
precompiled jar file.
If you want to recompile it, here's how.

First, if you directly downloaded the mmj2 git repository, as recommended, run
the following from the top-level "mmj2" directory to get the
submodules and compile it:

~~~~
git submodule init
git submodule update
compile/posix_compile
~~~~

This is created in the current directory.
You can move it to its final loation with:

~~~~
mv mmj2.jar mmj2jar/
~~~~

#### (Recommended) Install set.mm

You don't have to use the set.mm database with mmj2, but the mmj2 tutorial
uses it, and many mmj2 users will want it.

Install the database file set.mm. A common place is
`$HOME/set.mm/set.mm` but it can be anywhere. You can do this with:

~~~~
cd
git clone https://github.com/metamath/set.mm.git
~~~~

#### (Optional) Install Metamath-exe

You do not *have* to install the Metamath-exe
(C Metamath) implementation, but it is likely to be helpful.
The recommended location for it is "`$HOME/metamath`".

See [Downloads](http://us.metamath.org/#downloads) for how to do this.

### MacOS

#### Install Java
An easy way to install Java on MacOS is to go to
[AdoptOpenJDK](https://adoptopenjdk.net/), who provide
prebuilt OpenJDK implementations for many systems.
There's also an implementation available via `brew`.

An alternative way to install Java would be to install an
[Oracle Java release](https://www.oracle.com/java/technologies/javase-downloads.html);
make absolutely *certain* that you
comply with Oracle's licenses if you do this
(the free one, in particular, has many restrictions on how you
can legally use it).

#### The rest

Now that you have Java installed,
follow the Linux/Unix/Cygwin instructions above.
