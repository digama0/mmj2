# Installation instructions

## Brief install instructions

In brief:

- Install Java.
  Java version 11 and 8 are known to work, 11+ is recommended and easier
  to use (we provide a precompiled version).
  You only <i>need</i> the Java Runtime Environment (JRE), but
  unless you're short of disk space, you should probably install the
  full Java Development Kit (JDK).
  Beware: the license for Oracle's Java implementation changed in 2019.
  Make sure you use an OpenJDK implementation of Java,
  fully comply with Oracle's terms for free use of Oracle JDK, or
  pay Oracle their Oracle JDK fee and comply with those
  different legal terms.
- (Recommended) Install git and a good text editor.
- Put the mmj2 directory in its conventional place, which is
  C:\mmj2 for Windows and $HOME/mmj2 for anything else.
- (Optional) Compile mmj2. This isn't necessary if you use Java 11,
  because we provide a precompiled jar file.
- (Recommended) Install the Metamath database set.mm in its
  conventional place, which is C:\set.mm for Windows
  and $HOME/set.mm for anything else.
- (Recommended) Install Metamath-exe program in its
  conventional place, which is C:\metamath for Windows
  and $HOME/metamath for anything else.

Then follow the [QuickStart.html](QuickStart.html)

Below is more detail on how to do this for various systems.

## Full install instructions

First, <i>back up your data</i>.
We don't know of any problems, but everyone is human, there
are no guarantees with anything, and we expressly state there's no warranty.
In fact, you should have backups whether or not you ever use this software.

mmj2 requires Java to run.
Some major versions of Java are "long term support" (LTS) versions.
In particular, major versions 8 and 11 are long term support versions.
We recommend using version 11.
There are also various implementations of Java, in particular,
Oracle's Java implemenation and various OpenJDK implementations.

<b><i>BEWARE</i></b>: Oracle's implementation of Java
has historically been widely used, but
Oracle's Java license <i>radically</i> changed on April 16, 2019.
Oracle permits certain non-commercial uses at no cost,
but <i>many</i> uses require paying a fee - even some you might think
are non-commercial.
If you don't pay those fees to Oracle ahead-of-time,
you risk large fines for you and any company you work for,
and Oracle has a reputation for being litigious.
If you use the Oracle implementation, make <i>sure</i> all
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
make absolutely <i>certain</i> that you
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
The programs <tt>Notepad</tt> and <tt>Wordpad</tt> are <b>not</b>
good text editors.
There are many available; if you have no idea, you might try
[Notepad++](https://notepad-plus-plus.org/), which is small,
simple, and gets the job done.
Other options include [Atom](https://atom.io/),
[vim](https://www.vim.org/)
[Microsoft Visual Studio Code](https://code.visualstudio.com/).

#### Install mmj2

Install mmj2; we recommend putting it in "C:\mmj2" on Windows.
Click on the "start" button, type "command", press ENTER, and then type:

~~~~
cd c:\
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

Put the database set.mm in its recommended place, which is
C:\set.mm for Windows.

#### (Recommended) Install Metamath-exe
Install metamath-exe in its conventional place,
which is C:\metamath for Windows.

#### (Optional) Create a desktop icon

On the start menu type
`C:\mmj2\mmj2jar` to open that directory
and then right-click on the `mmj2.bat` file.
Create a shortcut, then drag that to the desktop.

Note: Cygwin users should create a shortcut on the file
`mmj2-cygwin.bat` instead.

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
    - Install the JDK version 11 with: <tt>sudo apt install openjdk-11-jdk</tt>
    - You can install multiple different Java versions, e.g.,
      version 8 JDK is package <tt>openjdk-8-jdk</tt>, and the current
      default Java version is package <tt>default-jdk</tt>.
      Replace "-jdk" with "-jre" if you're short of disk space and <i>only</i>
      want the Java Runtime Environment (JRE).
      If you have more than one version, you can select between them using
      <tt>sudo update-alternatives --config java</tt>


- On Fedora, install OpenJDK 11 as follows:
    - Install JDK version 11 with:
      <tt>sudo dnf install java-11-openjdk.x86_64</tt>
    - You can replace "11" with "1.8" or "latest" to get version 8 or the
      latest version (respectively).

An alternative way to install Java would be to install an
[Oracle Java release](https://www.oracle.com/java/technologies/javase-downloads.html); make absolutely <i>certain</i> that you
comply with Oracle's licenses if you do this.

#### Install git and a good text editor

Install git:

- On Debian and Ubuntu this is `sudo apt install git`
- On Fedora this is `sudo dnf install git`

Linux systems all come with at least one good text editor and
many alternatives.

#### Install mmj2

By convention mmj2 is placed in $HOME/mmj2 though can be
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

#### (Recommended) Install metamath-exe

You do not <i>have</i> to install the Metamath-exe
(C Metamath) implementation, but it is likely to be helpful.
The recommended location for it is "<tt>$HOME/metamath</tt>".

### MacOS

#### Install Java
An easy way to install Java on MacOS is to go to
[AdoptOpenJDK](https://adoptopenjdk.net/), who provide
prebuilt OpenJDK implementations for many systems.
There's also an implementation available via <tt>brew</tt>.

An alternative way to install Java would be to install an
[Oracle Java release](https://www.oracle.com/java/technologies/javase-downloads.html);
make absolutely <i>certain</i> that you
comply with Oracle's licenses if you do this
(the free one, in particular, has many restrictions on how you
can legally use it).

#### The rest

Now that you have Java installed,
follow the Linux/Unix/Cygwin instructions above.
