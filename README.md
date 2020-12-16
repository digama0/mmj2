# README for mmj2

mmj2 is a proof assistant for the Metamath language.
Metamath is a language that lets you express mathematical axioms and theorems.
The proof assistant includes a GUI for creating proofs,
proof verification tools, and grammatical/syntax analysis.

mmj2 was originally developed by Mel O'Cat 2005-2011.
This version has been further modified by Mario Carneiro and David A. Wheeler.
This program is released under the terms of the
GNU General Public License Version 2 or any later version
(SPDX license identifier GPL-2.0-or-later); see [LICENSE.TXT](LICENSE.TXT).

## Installation

Refer to [INSTALL.md](INSTALL.md) for instructions on how to install mmj2.
This includes installing a Java runtime if you don't already have one
(since mmj2 requires a Java runtime).

## Interactive Tutorial

Once you have installed mmj2, if you've not used mmj2 before,
be sure to try the [interactive tutorial](mmj2jar/PATutorial) (which ought to take about an hour).

## Quick Start

See [Quick Start](quickstart.md) if you want to quickly get running.

## More information

Once you've installed mmj2, the following pages may be useful:

* [mmj2 Proof Assistant User Guide](doc/PAUserGuide/Start.html)
* [General information about mmj2](mmj2.html)
* [mmj2 Command Line Arguments](doc/mmj2CommandLineArguments.html)

## Contributing

If you want to contribute to this version of mmj2,
please submit issues, pull requests, etc., to:
<https://github.com/digama0/mmj2>

MMj2 was initially developed by Mel O'Cat.
If you wish to provide financial support or send questions to Mel O'Cat, see
[mel-contact.md](mel-contact.md).

## Limitations

* mmj2 takes a time to get started (circa 60 seconds on a slow machine
  loading the large database set.mm).
  Java programs are slow to start up in general, and like Metamath.exe,
  when a database is selected, it loads the entire Metamath .mm database
  into memory and does computations on that.
  The first unification can also take several seconds.
  Once it gets started and you've done your first unification it should
  generally be quite fast.
* As noted in the license, there is NO WARRANTY.
* It has a few minor automation mechanisms, but it is fundamentally
  designed to help a human create proofs, not replace humans.
* It does *not* update Metamath .mm databases. Once you complete a proof,
  you have to use another tool (like a text editor) to put the proof
  into database. Writing a proof takes far more time copying it into a
  database, so this has not been seen as a serious problem.
* It is a single-user system, not designed for concurrent access
  throughout (but you can run more than one instance of mmj2's Proof
  Assistant GUI simultaneously.)
* All messages are output in English.
* All output is left-to-right.
* Support for Unicode or non-ASCII input/output is minimal.
  (Note that the metamath specification itself requires ASCII input.)
