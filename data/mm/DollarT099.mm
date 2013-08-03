$( $t

/* The '$ t' (no space between '$' and 't') token indicates the beginning
    of the typesetting definition section, embedded in a Metamath
    comment.  There may only be one per source file, and the typesetting
    section ends with the end of the Metamath comment.  The typesetting
    section uses C-style comment delimiters.  Todo:  Allow multiple
    typesetting comments */

/* These are the LaTeX and HTML definitions in the order the tokens are
    introduced in $c or $v statements.  See HELP TEX or HELP HTML in the
    Metamath program. */


/******* Web page format settings *******/

/* Page title, home page link */
htmltitle "Metamath Proof Explorer";
htmlhome '<A HREF="mmset.html"><FONT SIZE=-2 FACE=sans-serif>' +
    '<IMG SRC="mm.gif" BORDER=0 ALT=' +
    '"Home" HEIGHT=32 WIDTH=32 ALIGN=MIDDLE>' +
    'Home</FONT></A>';
/* Optional file where bibliographic references are kept */
/* If specified, e.g. "mmset.html", Metamath will hyperlink all strings of the
   form "[rrr]" (where "rrr" has no whitespace) to "mmset.html#rrr" */
/* A warning will be given if the file "mmset.html" with the bibliographical
   references is not present.  It is read in order to check correctness of
   the references. */
htmlbibliography "mmset.html";

/* Page title, home page link */
/* These are the variables used for the Hilbert Space extension to
   set.mm. */
exthtmltitle "Hilbert Space Explorer";
exthtmlhome '<A HREF="mmhil.html"><FONT SIZE=-2 FACE=sans-serif>' +
    '<IMG SRC="atomic.gif" BORDER=0 ALT=' +
    '"Home" HEIGHT=32 WIDTH=32 ALIGN=MIDDLE>' +
    'Home</FONT></A>';
/* The variable "exthtmllabel" means that all states including
   and after this label should use the "ext..." variables. */
exthtmllabel "chil";
/* A warning will be given if the file with the bibliographical references
   is not present. */
exthtmlbibliography "mmhil.html";

/* Variable color key at the bottom of each proof table */
htmlvarcolor '<FONT COLOR="#0000FF">wff</FONT> '
    + '<FONT COLOR="#FF0000">set</FONT> '
    + '<FONT COLOR="#CC33CC">class</FONT>';

/* GIF and Unicode HTML directories - these are used for the GIF version to
   crosslink to the Unicode version and vice-versa */
htmldir "../mpegif/";
althtmldir "../mpeuni/";


/******* Symbol definitions *******/

htmldef "(" as "<IMG SRC='lp.gif' WIDTH=5 HEIGHT=19 ALT='(' ALIGN=TOP>";
  althtmldef "(" as "(";
  latexdef "(" as "(";
htmldef ")" as "<IMG SRC='rp.gif' WIDTH=5 HEIGHT=19 ALT=')' ALIGN=TOP>";
  althtmldef ")" as ")";
  latexdef ")" as ")";
htmldef "->" as
    " <IMG SRC='to.gif' WIDTH=15 HEIGHT=19 ALT='-&gt;' ALIGN=TOP> ";
  althtmldef "->" as ' &rarr; ';
  latexdef "->" as "\rightarrow";
htmldef "-." as "<IMG SRC='lnot.gif' WIDTH=10 HEIGHT=19 ALT='-.' ALIGN=TOP> ";
  althtmldef "-." as '&not; ';
  latexdef "-." as "\lnot";
htmldef "wff" as
    "<IMG SRC='_wff.gif' WIDTH=24 HEIGHT=19 ALT='wff' ALIGN=TOP> ";
  althtmldef "wff" as '<FONT COLOR="#808080">wff </FONT>'; /* was #00CC00 */
  latexdef "wff" as "{\rm wff}";
htmldef "|-" as
    "<IMG SRC='_vdash.gif' WIDTH=10 HEIGHT=19 ALT='|-' ALIGN=TOP> ";
  althtmldef "|-" as
    '<FONT COLOR="#808080" FACE=sans-serif>&#8866; </FONT>'; /* &vdash; */
    /* Without sans-serif, way too big in FF3 */
  latexdef "|-" as "\vdash";
htmldef "ph" as
    "<IMG SRC='_varphi.gif' WIDTH=11 HEIGHT=19 ALT='ph' ALIGN=TOP>";
  althtmldef "ph" as '<FONT COLOR="#0000FF"><I>&phi;</I></FONT>';
  latexdef "ph" as "\varphi";
htmldef "ps" as "<IMG SRC='_psi.gif' WIDTH=12 HEIGHT=19 ALT='ps' ALIGN=TOP>";
  althtmldef "ps" as '<FONT COLOR="#0000FF"><I>&psi;</I></FONT>';
  latexdef "ps" as "\psi";
htmldef "ch" as "<IMG SRC='_chi.gif' WIDTH=12 HEIGHT=19 ALT='ch' ALIGN=TOP>";
  althtmldef "ch" as '<FONT COLOR="#0000FF"><I>&chi;</I></FONT>';
  latexdef "ch" as "\chi";
htmldef "th" as "<IMG SRC='_theta.gif' WIDTH=8 HEIGHT=19 ALT='th' ALIGN=TOP>";
  althtmldef "th" as '<FONT COLOR="#0000FF"><I>&theta;</I></FONT>';
  latexdef "th" as "\theta";
htmldef "ta" as "<IMG SRC='_tau.gif' WIDTH=10 HEIGHT=19 ALT='ta' ALIGN=TOP>";
  althtmldef "ta" as '<FONT COLOR="#0000FF"><I>&tau;</I></FONT>';
  latexdef "ta" as "\tau";
htmldef "<->" as " <IMG SRC='leftrightarrow.gif' WIDTH=15 HEIGHT=19 " +
    "ALT='&lt;-&gt;' ALIGN=TOP> ";
  althtmldef "<->" as ' &harr; ';
  latexdef "<->" as "\leftrightarrow";
htmldef "\/" as " <IMG SRC='vee.gif' WIDTH=11 HEIGHT=19 ALT='\/' ALIGN=TOP> ";
  althtmldef "\/" as ' <FONT FACE=sans-serif>&#8897;</FONT> ' ;
    /* was &or; - changed to match font of &and; replacement */
  latexdef "\/" as "\vee";
htmldef "/\" as
    " <IMG SRC='wedge.gif' WIDTH=11 HEIGHT=19 ALT='/\' ALIGN=TOP> ";
  althtmldef "/\" as ' <FONT FACE=sans-serif>&#8896;</FONT> ';
    /* was &and; which is circle in Mozilla on WinXP Pro (but not Home) */
  latexdef "/\" as "\wedge";
htmldef "et" as "<IMG SRC='_eta.gif' WIDTH=9 HEIGHT=19 ALT='et' ALIGN=TOP>";
  althtmldef "et" as '<FONT COLOR="#0000FF"><I>&eta;</I></FONT>';
  latexdef "et" as "\eta";
htmldef "ze" as "<IMG SRC='_zeta.gif' WIDTH=9 HEIGHT=19 ALT='ze' ALIGN=TOP>";
  althtmldef "ze" as '<FONT COLOR="#0000FF"><I>&zeta;</I></FONT>';
  latexdef "ze" as "\zeta";
htmldef "-/\" as
    " <IMG SRC='barwedge.gif' WIDTH=9 HEIGHT=19 ALT='-/\' ALIGN=TOP> ";
  althtmldef "-/\" as ' <FONT FACE=sans-serif>&#8892;</FONT> ';
    /*althtmldef "-/\" as " &#8892; "; */ /* too-high font bug in FF */
    /* barwedge, U022BC, alias ISOAMSB barwed, ['nand'] */
  latexdef "-/\" as "\barwedge";
htmldef "A." as "<IMG SRC='forall.gif' WIDTH=10 HEIGHT=19 ALT='A.' ALIGN=TOP>";
  althtmldef "A." as '<FONT FACE=sans-serif>&forall;</FONT>'; /* &#8704; */
  latexdef "A." as "\forall";
htmldef "set" as
    "<IMG SRC='_set.gif' WIDTH=20 HEIGHT=19 ALT='set' ALIGN=TOP> ";
  althtmldef "set" as '<FONT COLOR="#808080">set </FONT>';
  latexdef "set" as "{\rm set}";
htmldef "x" as "<IMG SRC='_x.gif' WIDTH=10 HEIGHT=19 ALT='x' ALIGN=TOP>";
  althtmldef "x" as '<I><FONT COLOR="#FF0000">x</FONT></I>';
  latexdef "x" as "x";
htmldef "y" as "<IMG SRC='_y.gif' WIDTH=9 HEIGHT=19 ALT='y' ALIGN=TOP>";
  althtmldef "y" as '<I><FONT COLOR="#FF0000">y</FONT></I>';
  latexdef "y" as "y";
htmldef "z" as "<IMG SRC='_z.gif' WIDTH=9 HEIGHT=19 ALT='z' ALIGN=TOP>";
  althtmldef "z" as '<I><FONT COLOR="#FF0000">z</FONT></I>';
  latexdef "z" as "z";
htmldef "w" as "<IMG SRC='_w.gif' WIDTH=12 HEIGHT=19 ALT='w' ALIGN=TOP>";
  althtmldef "w" as '<I><FONT COLOR="#FF0000">w</FONT></I>';
  latexdef "w" as "w";
htmldef "v" as "<IMG SRC='_v.gif' WIDTH=9 HEIGHT=19 ALT='v' ALIGN=TOP>";
  althtmldef "v" as '<I><FONT COLOR="#FF0000">v</FONT></I>';
  latexdef "v" as "v";
htmldef "E." as "<IMG SRC='exists.gif' WIDTH=9 HEIGHT=19 ALT='E.' ALIGN=TOP>";
  althtmldef "E." as '<FONT FACE=sans-serif>&exist;</FONT>'; /* &#8707; */
    /* Without sans-serif, bad in Opera and way too big in FF3 */
  latexdef "E." as "\exists";
htmldef "=" as " <IMG SRC='eq.gif' WIDTH=12 HEIGHT=19 ALT='=' ALIGN=TOP> ";
  althtmldef "=" as ' = '; /* &equals; */
  latexdef "=" as "=";
htmldef "e." as " <IMG SRC='in.gif' WIDTH=10 HEIGHT=19 ALT='e.' ALIGN=TOP> ";
  althtmldef "e." as ' <FONT FACE=sans-serif>&isin;</FONT> ';
  latexdef "e." as "\in";
htmldef "[" as "<IMG SRC='lbrack.gif' WIDTH=5 HEIGHT=19 ALT='[' ALIGN=TOP>";
  althtmldef "[" as '['; /* &lsqb; */
  latexdef "[" as "[";
htmldef "/" as " <IMG SRC='solidus.gif' WIDTH=6 HEIGHT=19 ALT='/' ALIGN=TOP> ";
  althtmldef "/" as ' / '; /* &sol; */
  latexdef "/" as "/";
htmldef "]" as "<IMG SRC='rbrack.gif' WIDTH=5 HEIGHT=19 ALT=']' ALIGN=TOP>";
  althtmldef "]" as ']'; /* &rsqb; */
  latexdef "]" as "]";
htmldef "u" as "<IMG SRC='_u.gif' WIDTH=10 HEIGHT=19 ALT='u' ALIGN=TOP>";
  althtmldef "u" as '<I><FONT COLOR="#FF0000">u</FONT></I>';
  latexdef "u" as "u";
htmldef "f" as "<IMG SRC='_f.gif' WIDTH=9 HEIGHT=19 ALT='f' ALIGN=TOP>";
  althtmldef "f" as '<I><FONT COLOR="#FF0000">f</FONT></I>';
  latexdef "f" as "f";
htmldef "g" as "<IMG SRC='_g.gif' WIDTH=9 HEIGHT=19 ALT='g' ALIGN=TOP>";
  althtmldef "g" as '<I><FONT COLOR="#FF0000">g</FONT></I>';
  latexdef "g" as "g";
htmldef "E!" as "<IMG SRC='_e1.gif' WIDTH=12 HEIGHT=19 ALT='E!' ALIGN=TOP>";
  althtmldef "E!" as '<FONT FACE=sans-serif>&exist;!</FONT>';
  latexdef "E!" as "\exists{!}";
htmldef "E*" as "<IMG SRC='_em1.gif' WIDTH=15 HEIGHT=19 ALT='E*' ALIGN=TOP>";
  althtmldef "E*" as '<FONT FACE=sans-serif>&exist;*</FONT>';
  latexdef "E*" as "\exists^\ast";
htmldef "{" as "<IMG SRC='lbrace.gif' WIDTH=6 HEIGHT=19 ALT='{' ALIGN=TOP>";
  althtmldef "{" as '{'; /* &lcub; */
  latexdef "{" as "\{";
htmldef "|" as " <IMG SRC='vert.gif' WIDTH=3 HEIGHT=19 ALT='|' ALIGN=TOP> ";
  althtmldef "|" as ' <FONT FACE=sans-serif>&#8739;</FONT> '; /* &vertbar; */
  latexdef "|" as "|";
htmldef "}" as "<IMG SRC='rbrace.gif' WIDTH=6 HEIGHT=19 ALT='}' ALIGN=TOP>";
  althtmldef "}" as '}'; /* &rcub; */
  latexdef "}" as "\}";
htmldef "class" as
    "<IMG SRC='_class.gif' WIDTH=32 HEIGHT=19 ALT='class' ALIGN=TOP> ";
  althtmldef "class" as '<FONT COLOR="#808080">class </FONT>';
  latexdef "class" as "{\rm class}";
htmldef "A" as "<IMG SRC='_ca.gif' WIDTH=11 HEIGHT=19 ALT='A' ALIGN=TOP>";
  althtmldef "A" as '<I><FONT COLOR="#CC33CC">A</FONT></I>';
  latexdef "A" as "A";
htmldef "B" as "<IMG SRC='_cb.gif' WIDTH=12 HEIGHT=19 ALT='B' ALIGN=TOP>";
  althtmldef "B" as '<I><FONT COLOR="#CC33CC">B</FONT></I>';
  latexdef "B" as "B";
htmldef "C" as "<IMG SRC='_cc.gif' WIDTH=12 HEIGHT=19 ALT='C' ALIGN=TOP>";
  althtmldef "C" as '<I><FONT COLOR="#CC33CC">C</FONT></I>';
  latexdef "C" as "C";
htmldef "D" as "<IMG SRC='_cd.gif' WIDTH=12 HEIGHT=19 ALT='D' ALIGN=TOP>";
  althtmldef "D" as '<I><FONT COLOR="#CC33CC">D</FONT></I>';
  latexdef "D" as "D";
htmldef "P" as "<IMG SRC='_cp.gif' WIDTH=12 HEIGHT=19 ALT='P' ALIGN=TOP>";
  althtmldef "P" as '<I><FONT COLOR="#CC33CC">P</FONT></I>';
  latexdef "P" as "P";
htmldef "R" as "<IMG SRC='_cr.gif' WIDTH=12 HEIGHT=19 ALT='R' ALIGN=TOP>";
  althtmldef "R" as '<I><FONT COLOR="#CC33CC">R</FONT></I>';
  latexdef "R" as "R";
htmldef "S" as "<IMG SRC='_cs.gif' WIDTH=11 HEIGHT=19 ALT='S' ALIGN=TOP>";
  althtmldef "S" as '<I><FONT COLOR="#CC33CC">S</FONT></I>';
  latexdef "S" as "S";
htmldef "T" as "<IMG SRC='_ct.gif' WIDTH=12 HEIGHT=19 ALT='T' ALIGN=TOP>";
  althtmldef "T" as '<I><FONT COLOR="#CC33CC">T</FONT></I>';
  latexdef "T" as "T";
htmldef "=/=" as " <IMG SRC='ne.gif' WIDTH=12 HEIGHT=19 ALT='=/=' ALIGN=TOP> ";
  althtmldef "=/=" as ' &ne; ';
  latexdef "=/=" as "\ne";
htmldef "e/" as
    " <IMG SRC='notin.gif' WIDTH=10 HEIGHT=19 ALT='e/' ALIGN=TOP> ";
  althtmldef "e/" as ' <FONT FACE=sans-serif>&notin;</FONT> ';
  latexdef "e/" as "\notin";
htmldef "_V" as "<IMG SRC='rmcv.gif' WIDTH=10 HEIGHT=19 ALT='_V' ALIGN=TOP>";
  althtmldef "_V" as 'V';
  latexdef "_V" as "{\rm V}";
htmldef
    "[_" as "<IMG SRC='_ulbrack.gif' WIDTH=6 HEIGHT=19 ALT='[_' ALIGN=TOP>";
  althtmldef "[_" as '<U>[</U>'; /* &lsqb; */
  latexdef "[_" as "\underline{[}";
htmldef
    "]_" as "<IMG SRC='_urbrack.gif' WIDTH=5 HEIGHT=19 ALT=']_' ALIGN=TOP>";
  althtmldef "]_" as '<U>]</U>'; /* &rsqb; */
  latexdef "]_" as "\underline{]}";
htmldef "F" as "<IMG SRC='_cf.gif' WIDTH=13 HEIGHT=19 ALT='F' ALIGN=TOP>";
  althtmldef "F" as '<I><FONT COLOR="#CC33CC">F</FONT></I>';
  latexdef "F" as "F";
htmldef "G" as "<IMG SRC='_cg.gif' WIDTH=12 HEIGHT=19 ALT='G' ALIGN=TOP>";
  althtmldef "G" as '<I><FONT COLOR="#CC33CC">G</FONT></I>';
  latexdef "G" as "G";
htmldef "C_" as
    " <IMG SRC='subseteq.gif' WIDTH=12 HEIGHT=19 ALT='C=' ALIGN=TOP> ";
  althtmldef "C_" as ' <FONT FACE=sans-serif>&#8838;</FONT> '; /* &subE; */
  latexdef "C_" as "\subseteq";
htmldef "C." as
    " <IMG SRC='subset.gif' WIDTH=12 HEIGHT=19 ALT='C.' ALIGN=TOP> ";
  althtmldef "C." as ' <FONT FACE=sans-serif>&sub;</FONT> ';
  latexdef "C." as "\subset";
htmldef "\" as
    " <IMG SRC='setminus.gif' WIDTH=8 HEIGHT=19 ALT='\' ALIGN=TOP> ";
  althtmldef "\" as ' <FONT FACE=sans-serif>&#8726;</FONT> '; /* &setmn; */
  latexdef "\" as "\setminus";
htmldef "u." as " <IMG SRC='cup.gif' WIDTH=10 HEIGHT=19 ALT='u.' ALIGN=TOP> ";
  althtmldef "u." as ' &cup; ';
  latexdef "u." as "\cup";
htmldef "i^i" as
    " <IMG SRC='cap.gif' WIDTH=10 HEIGHT=19 ALT='i^i' ALIGN=TOP> ";
  althtmldef "i^i" as ' &cap; ';
  latexdef "i^i" as "\cap";
htmldef "(/)" as
    "<IMG SRC='varnothing.gif' WIDTH=11 HEIGHT=19 ALT='(/)' ALIGN=TOP>";
  althtmldef "(/)" as '<FONT FACE=sans-serif>&empty;</FONT>';
    /*althtmldef "(/)" as '&empty;';*/ /* =&#8709 */ /* bad in Opera */
    /*althtmldef "(/)" as '&#8960;';*/
  latexdef "(/)" as "\varnothing";
htmldef "if" as "<IMG SRC='_if.gif' WIDTH=11 HEIGHT=19 ALT='if' ALIGN=TOP>";
    /*htmldef "ded" as
    "<IMG SRC='_ded.gif' WIDTH=23 HEIGHT=19 ALT='ded' ALIGN=TOP>";*/
  althtmldef "if" as ' if';
    /*althtmldef "ded" as 'ded';*/
  latexdef "if" as "{\rm if}";
    /*latexdef "ded" as "{\rm ded}";*/
htmldef "," as "<IMG SRC='comma.gif' WIDTH=4 HEIGHT=19 ALT=',' ALIGN=TOP> ";
  althtmldef "," as ', ';
  latexdef "," as ",";
htmldef "si" as "<IMG SRC='_sigma.gif' WIDTH=10 HEIGHT=19 ALT='si' ALIGN=TOP>";
  althtmldef "si" as '<FONT COLOR="#0000FF"><I>&sigma;</I></FONT>';
  latexdef "si" as "\sigma";
htmldef "rh" as "<IMG SRC='_rho.gif' WIDTH=9 HEIGHT=19 ALT='rh' ALIGN=TOP>";
  althtmldef "rh" as '<FONT COLOR="#0000FF"><I>&rho;</I></FONT>';
  latexdef "rh" as "\rho";
htmldef "~P" as "<IMG SRC='scrp.gif' WIDTH=16 HEIGHT=19 ALT='~P' ALIGN=TOP>";
  althtmldef "~P" as '<FONT FACE=sans-serif>&weierp;</FONT>';
  latexdef "~P" as "{\cal P}";
htmldef "<." as
    "<IMG SRC='langle.gif' WIDTH=4 HEIGHT=19 ALT='&lt;.' ALIGN=TOP>";
  althtmldef "<." as
    "<IMG SRC='langle.gif' WIDTH=4 HEIGHT=19 ALT='&lt;.' ALIGN=TOP>";
    /* The Unicode below doesn't always work on Firefox and Chrome on Windows,
       so revert to the image above */
    /*althtmldef "<." as '<FONT FACE=sans-serif>&lang;</FONT>';*/ /* &#9001; */
  latexdef "<." as "\langle";
htmldef ">." as
    "<IMG SRC='rangle.gif' WIDTH=4 HEIGHT=19 ALT='&gt;.' ALIGN=TOP>";
  althtmldef ">." as
    "<IMG SRC='rangle.gif' WIDTH=4 HEIGHT=19 ALT='&gt;.' ALIGN=TOP>";
    /* The Unicode below doesn't always work on Firefox and Chrome on Windows,
       so revert to the image above */
    /*althtmldef ">." as '<FONT FACE=sans-serif>&rang;</FONT>';*/ /* &#9002; */
  latexdef ">." as "\rangle";
htmldef "U." as "<IMG SRC='bigcup.gif' WIDTH=13 HEIGHT=19 ALT='U.' ALIGN=TOP>";
  althtmldef "U." as '<FONT SIZE="+1">&cup;</FONT>'; /* &xcup; */
    /* xcup does not render, and #8899 renders as a small bold cup, on
       Mozilla 1.7.3 on Windows XP */
    /*althtmldef "U." as '&#8899;';*/ /* &xcup; */
  latexdef "U." as "\bigcup";
htmldef "|^|" as
    "<IMG SRC='bigcap.gif' WIDTH=13 HEIGHT=19 ALT='|^|' ALIGN=TOP>";
  althtmldef "|^|" as '<FONT SIZE="+1">&cap;</FONT>'; /* &xcap; */
    /*althtmldef "|^|" as '&#8898;';*/ /* &xcap; */
  latexdef "|^|" as "\bigcap";
htmldef "U_" as
    "<IMG SRC='_cupbar.gif' WIDTH=13 HEIGHT=19 ALT='U_' ALIGN=TOP>";
  althtmldef "U_" as '<U><FONT SIZE="+1">&cup;</FONT></U>'; /* &xcup; */
  latexdef "U_" as "\underline{\bigcup}";
htmldef "|^|_" as
    "<IMG SRC='_capbar.gif' WIDTH=13 HEIGHT=19 ALT='|^|_' ALIGN=TOP>";
  althtmldef "|^|_" as '<U><FONT SIZE="+1">&cap;</FONT></U>'; /* &xcap; */
  latexdef "|^|_" as "\underline{\bigcap}";
htmldef "Tr" as "<IMG SRC='_tr.gif' WIDTH=16 HEIGHT=19 ALT='Tr' ALIGN=TOP> ";
  althtmldef "Tr" as 'Tr ';
  latexdef "Tr" as "{\rm Tr}";
htmldef "_E" as "<IMG SRC='rmce.gif' WIDTH=9 HEIGHT=19 ALT='_E' ALIGN=TOP>";
  althtmldef "_E" as 'E';
  latexdef "_E" as "{\rm E}";
htmldef "_I" as "<IMG SRC='rmci.gif' WIDTH=4 HEIGHT=19 ALT='_I' ALIGN=TOP>";
  althtmldef "_I" as 'I';
  latexdef "_I" as "{\rm I}";
htmldef "Po" as " <IMG SRC='_po.gif' WIDTH=16 HEIGHT=19 ALT='Po' ALIGN=TOP> ";
  althtmldef "Po" as ' Po ';
  latexdef "Po" as "{\rm Po}";
htmldef "Or" as " <IMG SRC='_or.gif' WIDTH=18 HEIGHT=19 ALT='Or' ALIGN=TOP> ";
  althtmldef "Or" as ' Or ';
  latexdef "Or" as "{\rm Or}";
htmldef "sup" as "<IMG SRC='_sup.gif' WIDTH=22 HEIGHT=19 ALT='sup' ALIGN=TOP>";
  althtmldef "sup" as 'sup';
  latexdef "sup" as "{\rm sup}";
htmldef "Fr" as " <IMG SRC='_fr.gif' WIDTH=15 HEIGHT=19 ALT='Fr' ALIGN=TOP> ";
  althtmldef "Fr" as ' Fr ';
  latexdef "Fr" as "{\rm Fr}";
htmldef "We" as " <IMG SRC='_we.gif' WIDTH=21 HEIGHT=19 ALT='We' ALIGN=TOP> ";
  althtmldef "We" as ' We ';
  latexdef "We" as "{\rm We}";
htmldef "Ord" as
    "<IMG SRC='_ord.gif' WIDTH=26 HEIGHT=19 ALT='Ord' ALIGN=TOP> ";
  althtmldef "Ord" as 'Ord ';
  latexdef "Ord" as "{\rm Ord}";
htmldef "On" as "<IMG SRC='_on.gif' WIDTH=20 HEIGHT=19 ALT='On' ALIGN=TOP>";
  althtmldef "On" as 'On';
  latexdef "On" as "{\rm On}";
htmldef "Lim" as
    "<IMG SRC='_lim.gif' WIDTH=26 HEIGHT=19 ALT='Lim' ALIGN=TOP> ";
  althtmldef "Lim" as 'Lim ';
  latexdef "Lim" as "{\rm Lim}";
htmldef "suc" as
    "<IMG SRC='_suc.gif' WIDTH=22 HEIGHT=19 ALT='suc' ALIGN=TOP> ";
  althtmldef "suc" as 'suc ';
  latexdef "suc" as "{\rm suc}";
htmldef "om" as "<IMG SRC='omega.gif' WIDTH=11 HEIGHT=19 ALT='om' ALIGN=TOP>";
  althtmldef "om" as '&omega;';
  latexdef "om" as "\omega";
htmldef "X." as " <IMG SRC='times.gif' WIDTH=9 HEIGHT=19 ALT='X.' ALIGN=TOP> ";
  althtmldef "X." as ' &times; ';
  latexdef "X." as "\times";
htmldef "`'" as "<IMG SRC='_cnv.gif' WIDTH=10 HEIGHT=19 ALT=" + '"' + "`'" +
    '"' + " ALIGN=TOP>";
    /*htmldef "`'" as
      "<IMG SRC='smallsmile.gif' WIDTH=12 HEIGHT=19 ALT=" +
      '"' + "`'" + '"' + " ALIGN=TOP>";*/
  althtmldef "`'" as '<FONT SIZE="-1"><SUP>&#9697;</SUP></FONT>'; /* or 8995 */
  latexdef "`'" as "{}^{\smallsmile}";
htmldef "dom" as
    "<IMG SRC='_dom.gif' WIDTH=26 HEIGHT=19 ALT='dom' ALIGN=TOP> ";
  althtmldef "dom" as 'dom ';
  latexdef "dom" as "{\rm dom}";
htmldef "ran" as
    "<IMG SRC='_ran.gif' WIDTH=22 HEIGHT=19 ALT='ran' ALIGN=TOP> ";
  althtmldef "ran" as 'ran ';
  latexdef "ran" as "{\rm ran}";
htmldef "|`" as " <IMG SRC='restriction.gif' WIDTH=5 HEIGHT=19 ALT='|`'" +
    " ALIGN=TOP> ";
  althtmldef "|`" as ' <FONT FACE=sans-serif>&#8638;</FONT> '; /* &uharr; */
  latexdef "|`" as "\restriction";
htmldef '"' as "<IMG SRC='backquote.gif' WIDTH=7 HEIGHT=19 ALT='" + '"' +
    "' ALIGN=TOP>";
  althtmldef '"' as ' &#8220; ';
  latexdef '"' as "``";
htmldef "o." as " <IMG SRC='circ.gif' WIDTH=8 HEIGHT=19 ALT='o.' ALIGN=TOP> ";
  althtmldef "o." as ' <FONT FACE=sans-serif>&#8728;</FONT> ';
  latexdef "o." as "\circ";
htmldef "Rel" as
    "<IMG SRC='_rel.gif' WIDTH=22 HEIGHT=19 ALT='Rel' ALIGN=TOP> ";
  althtmldef "Rel" as 'Rel ';
  latexdef "Rel" as "{\rm Rel}";
htmldef
    "Fun" as "<IMG SRC='_fun.gif' WIDTH=25 HEIGHT=19 ALT='Fun' ALIGN=TOP> ";
  althtmldef "Fun" as 'Fun ';
  latexdef "Fun" as "{\rm Fun}";
htmldef "Fn" as " <IMG SRC='_fn.gif' WIDTH=17 HEIGHT=19 ALT='Fn' ALIGN=TOP> ";
  althtmldef "Fn" as ' Fn ';
  latexdef "Fn" as "{\rm Fn}";
htmldef ":" as "<IMG SRC='colon.gif' WIDTH=4 HEIGHT=19 ALT=':' ALIGN=TOP>";
  althtmldef ":" as ':';
  latexdef ":" as ":";
htmldef "-->" as
    "<IMG SRC='longrightarrow.gif' WIDTH=23 HEIGHT=19 ALT='--&gt;' ALIGN=TOP>";
  althtmldef "-->" as '&ndash;&rarr;';
    /* &#xAD;&#x2010;&ndash;&mdash;&minus; (possible symbols test) */
  latexdef "-->" as "\longrightarrow";
htmldef "-1-1->" as
    "<IMG SRC='onetoone.gif' WIDTH=23 HEIGHT=19 ALT='-1-1-&gt;' ALIGN=TOP>";
  althtmldef "-1-1->" as
    '&ndash;<FONT SIZE=-2 FACE=sans-serif>1-1</FONT>&rarr;';
  latexdef "-1-1->" as
    "\raisebox{.5ex}{${\textstyle{\:}_{\mbox{\footnotesize\rm 1" +
    "\tt -\rm 1}}}\atop{\textstyle{" +
    "\longrightarrow}\atop{\textstyle{}^{\mbox{\footnotesize\rm {\ }}}}}$}";
htmldef "-onto->" as
    "<IMG SRC='onto.gif' WIDTH=23 HEIGHT=19 ALT='-onto-&gt;' ALIGN=TOP>";
  althtmldef "-onto->" as
    '&ndash;<FONT SIZE=-2 FACE=sans-serif>onto</FONT>&rarr;';
  latexdef "-onto->" as
    "\raisebox{.5ex}{${\textstyle{\:}_{\mbox{\footnotesize\rm {\ }}}}" +
    "\atop{\textstyle{" +
    "\longrightarrow}\atop{\textstyle{}^{\mbox{\footnotesize\rm onto}}}}$}";
htmldef "-1-1-onto->" as "<IMG SRC='onetooneonto.gif' WIDTH=23 HEIGHT=19 " +
    "ALT='-1-1-onto-&gt;' ALIGN=TOP>";
  althtmldef "-1-1-onto->" as '&ndash;<FONT SIZE=-2 '
    + 'FACE=sans-serif>1-1</FONT>-<FONT SIZE=-2 '
    + 'FACE=sans-serif>onto</FONT>&rarr;';
  latexdef "-1-1-onto->" as
    "\raisebox{.5ex}{${\textstyle{\:}_{\mbox{\footnotesize\rm 1" +
    "\tt -\rm 1}}}\atop{\textstyle{" +
    "\longrightarrow}\atop{\textstyle{}^{\mbox{\footnotesize\rm onto}}}}$}";
htmldef "`" as "<IMG SRC='backtick.gif' WIDTH=4 HEIGHT=19 ALT='` ' ALIGN=TOP>";
    /* Above, IE7 _printing_ is corrupted by '`'; use '` ' which works */
  althtmldef "`" as ' &lsquo;';
  latexdef "`" as "`";
htmldef "Isom" as
    " <IMG SRC='_isom.gif' WIDTH=30 HEIGHT=19 ALT='Isom' ALIGN=TOP> ";
  althtmldef "Isom" as ' Isom ';
  latexdef "Isom" as "{\rm Isom}";
htmldef "X_" as
    "<IMG SRC='_bigtimes.gif' WIDTH=11 HEIGHT=19 ALT='X_' ALIGN=TOP>";
  althtmldef "X_" as '<FONT SIZE="+1" FACE=sans-serif>X</FONT>';
  latexdef "X_" as "\mbox{\large\boldmath$\times$}";
htmldef "|->" as " <IMG SRC='mapsto.gif' WIDTH=15 HEIGHT=19 ALT='|->'" +
    " ALIGN=TOP> ";
  althtmldef "|->" as ' <FONT FACE=sans-serif>&#8614;</FONT> ';
  latexdef "|->" as "\mapsto";
htmldef "1st" as "<IMG SRC='_1st.gif' WIDTH=15 HEIGHT=19 ALT='1st' ALIGN=TOP>";
  althtmldef "1st" as '1<SUP>st</SUP> ';
  latexdef "1st" as "1^{\rm st}";
htmldef "2nd" as "<IMG SRC='_2nd.gif' WIDTH=21 HEIGHT=19 ALT='2nd' ALIGN=TOP>";
  althtmldef "2nd" as '2<SUP>nd</SUP> ';
  latexdef "2nd" as "2^{\rm nd}";
htmldef "iota" as
    "<IMG SRC='iota.gif' WIDTH=6 HEIGHT=19 ALT='iota' ALIGN=TOP>";
  althtmldef "iota" as '&iota; ';
  latexdef "iota" as "\iota";
htmldef "h" as "<IMG SRC='_h.gif' WIDTH=10 HEIGHT=19 ALT='h' ALIGN=TOP>";
  althtmldef "h" as '<I><FONT COLOR="#FF0000">h</FONT></I>';
  latexdef "h" as "h";
htmldef "H" as "<IMG SRC='_ch.gif' WIDTH=14 HEIGHT=19 ALT='H' ALIGN=TOP>";
  althtmldef "H" as '<I><FONT COLOR="#CC33CC">H</FONT></I>';
  latexdef "H" as "H";
htmldef "rec" as "<IMG SRC='_rec.gif' WIDTH=21 HEIGHT=19 ALT='rec' ALIGN=TOP>";
  althtmldef "rec" as 'rec';
  latexdef "rec" as "{\rm rec}";
htmldef "^m" as
    " <IMG SRC='_hatm.gif' WIDTH=15 HEIGHT=19 ALT='^m' ALIGN=TOP> ";
  althtmldef "^m" as ' &uarr;<SUB><I>m</I></SUB> ';
  latexdef "^m" as "\uparrow_m";
htmldef "^pm" as
    " <IMG SRC='_hatpm.gif' WIDTH=21 HEIGHT=19 ALT='^pm' ALIGN=TOP> ";
  althtmldef "^pm" as ' &uarr;<SUB><I>pm</I></SUB> ';
  latexdef "^pm" as "\uparrow_{pm}";
htmldef "+o" as " <IMG SRC='_plo.gif' WIDTH=18 HEIGHT=19 ALT='+o' ALIGN=TOP> ";
  althtmldef "+o" as ' +<SUB><I>o</I></SUB> ';
  latexdef "+o" as "+_o";
htmldef ".o" as " <IMG SRC='_cdo.gif' WIDTH=10 HEIGHT=19 ALT='.o' ALIGN=TOP> ";
  althtmldef ".o" as ' &middot;<SUB><I>o</I></SUB> ';
  latexdef ".o" as "\cdot_o";
htmldef "^o" as
    " <IMG SRC='_hato.gif' WIDTH=11 HEIGHT=19 ALT='^o' ALIGN=TOP> ";
  althtmldef "^o" as ' &uarr;<SUB><I>o</I></SUB> ';
  latexdef "^o" as "\uparrow_o"; /*
  latexdef "^o" as "\hat{\ }_o"; */
htmldef "1o" as "<IMG SRC='_1o.gif' WIDTH=13 HEIGHT=19 ALT='1o' ALIGN=TOP>";
  althtmldef "1o" as '1<SUB><I>o</I></SUB>';
  latexdef "1o" as "1_o";
htmldef "2o" as "<IMG SRC='_2o.gif' WIDTH=14 HEIGHT=19 ALT='2o' ALIGN=TOP>";
  althtmldef "2o" as '2<SUB><I>o</I></SUB>';
  latexdef "2o" as "2_o";
htmldef "Er" as "<IMG SRC='_er.gif' WIDTH=16 HEIGHT=19 ALT='Er' ALIGN=TOP> ";
  althtmldef "Er" as 'Er ';
  latexdef "Er" as "{\rm Er}";
htmldef "/." as "<IMG SRC='diagup.gif' WIDTH=14 HEIGHT=19 ALT='/.' ALIGN=TOP>";
  althtmldef "/." as ' <B>/</B> ';
  latexdef "/." as "\diagup";
htmldef "Q" as "<IMG SRC='_cq.gif' WIDTH=12 HEIGHT=19 ALT='Q' ALIGN=TOP>";
  althtmldef "Q" as '<I><FONT COLOR="#CC33CC">Q</FONT></I>';
  latexdef "Q" as "Q";
htmldef "t" as "<IMG SRC='_t.gif' WIDTH=7 HEIGHT=19 ALT='t' ALIGN=TOP>";
  althtmldef "t" as '<I><FONT COLOR="#FF0000">t</FONT></I>';
  latexdef "t" as "t";
htmldef "s" as "<IMG SRC='_s.gif' WIDTH=7 HEIGHT=19 ALT='s' ALIGN=TOP>";
  althtmldef "s" as '<I><FONT COLOR="#FF0000">s</FONT></I>';
  latexdef "s" as "s";
htmldef "r" as "<IMG SRC='_r.gif' WIDTH=8 HEIGHT=19 ALT='r' ALIGN=TOP>";
  althtmldef "r" as '<I><FONT COLOR="#FF0000">r</FONT></I>';
  latexdef "r" as "r";
htmldef "a" as "<IMG SRC='_a.gif' WIDTH=9 HEIGHT=19 ALT='a' ALIGN=TOP>";
  althtmldef "a" as '<I><FONT COLOR="#FF0000">a</FONT></I>';
  latexdef "a" as "a";
htmldef "b" as "<IMG SRC='_b.gif' WIDTH=8 HEIGHT=19 ALT='b' ALIGN=TOP>";
  althtmldef "b" as '<I><FONT COLOR="#FF0000">b</FONT></I>';
  latexdef "b" as "b";
htmldef "c" as "<IMG SRC='_c.gif' WIDTH=7 HEIGHT=19 ALT='c' ALIGN=TOP>";
  althtmldef "c" as '<I><FONT COLOR="#FF0000">c</FONT></I>';
  latexdef "c" as "c";
htmldef "d" as "<IMG SRC='_d.gif' WIDTH=9 HEIGHT=19 ALT='d' ALIGN=TOP>";
  althtmldef "d" as '<I><FONT COLOR="#FF0000">d</FONT></I>';
  latexdef "d" as "d";
htmldef "e" as "<IMG SRC='_e.gif' WIDTH=8 HEIGHT=19 ALT='e' ALIGN=TOP>";
  althtmldef "e" as '<I><FONT COLOR="#FF0000">e</FONT></I>';
  latexdef "e" as "e";
htmldef "i" as "<IMG SRC='_i.gif' WIDTH=6 HEIGHT=19 ALT='i' ALIGN=TOP>";
  althtmldef "i" as '<I><FONT COLOR="#FF0000">i</FONT></I>';
  latexdef "i" as "i";
htmldef "j" as "<IMG SRC='_j.gif' WIDTH=7 HEIGHT=19 ALT='j' ALIGN=TOP>";
  althtmldef "j" as '<I><FONT COLOR="#FF0000">j</FONT></I>';
  latexdef "j" as "j";
htmldef "k" as "<IMG SRC='_k.gif' WIDTH=9 HEIGHT=19 ALT='k' ALIGN=TOP>";
  althtmldef "k" as '<I><FONT COLOR="#FF0000">k</FONT></I>';
  latexdef "k" as "k";
htmldef "m" as "<IMG SRC='_m.gif' WIDTH=14 HEIGHT=19 ALT='m' ALIGN=TOP>";
  althtmldef "m" as '<I><FONT COLOR="#FF0000">m</FONT></I>';
  latexdef "m" as "m";
htmldef "n" as "<IMG SRC='_n.gif' WIDTH=10 HEIGHT=19 ALT='n' ALIGN=TOP>";
  althtmldef "n" as '<I><FONT COLOR="#FF0000">n</FONT></I>';
  latexdef "n" as "n";
htmldef "o" as "<IMG SRC='_o.gif' WIDTH=8 HEIGHT=19 ALT='o' ALIGN=TOP>";
  althtmldef "o" as '<I><FONT COLOR="#FF0000">o</FONT></I>';
  latexdef "o" as "o";
htmldef "p" as "<IMG SRC='_p.gif' WIDTH=10 HEIGHT=19 ALT='p' ALIGN=TOP>";
  althtmldef "p" as '<I><FONT COLOR="#FF0000">p</FONT></I>';
  latexdef "p" as "p";
htmldef "q" as "<IMG SRC='_q.gif' WIDTH=8 HEIGHT=19 ALT='q' ALIGN=TOP>";
  althtmldef "q" as '<I><FONT COLOR="#FF0000">q</FONT></I>';
  latexdef "q" as "q";
htmldef "E" as "<IMG SRC='_ce.gif' WIDTH=13 HEIGHT=19 ALT='E' ALIGN=TOP>";
  althtmldef "E" as '<I><FONT COLOR="#CC33CC">E</FONT></I>';
  latexdef "E" as "E";
htmldef "I" as "<IMG SRC='_ci.gif' WIDTH=8 HEIGHT=19 ALT='I' ALIGN=TOP>";
  althtmldef "I" as '<I><FONT COLOR="#CC33CC">I</FONT></I>';
  latexdef "I" as "I";
htmldef "J" as "<IMG SRC='_cj.gif' WIDTH=10 HEIGHT=19 ALT='J' ALIGN=TOP>";
  althtmldef "J" as '<I><FONT COLOR="#CC33CC">J</FONT></I>';
  latexdef "J" as "J";
htmldef "K" as "<IMG SRC='_ck.gif' WIDTH=14 HEIGHT=19 ALT='K' ALIGN=TOP>";
  althtmldef "K" as '<I><FONT COLOR="#CC33CC">K</FONT></I>';
  latexdef "K" as "K";
htmldef "L" as "<IMG SRC='_cl.gif' WIDTH=10 HEIGHT=19 ALT='L' ALIGN=TOP>";
  althtmldef "L" as '<I><FONT COLOR="#CC33CC">L</FONT></I>';
  latexdef "L" as "L";
htmldef "M" as "<IMG SRC='_cm.gif' WIDTH=15 HEIGHT=19 ALT='M' ALIGN=TOP>";
  althtmldef "M" as '<I><FONT COLOR="#CC33CC">M</FONT></I>';
  latexdef "M" as "M";
htmldef "N" as "<IMG SRC='_cn.gif' WIDTH=14 HEIGHT=19 ALT='N' ALIGN=TOP>";
  althtmldef "N" as '<I><FONT COLOR="#CC33CC">N</FONT></I>';
  latexdef "N" as "N";
htmldef "O" as "<IMG SRC='_co.gif' WIDTH=12 HEIGHT=19 ALT='O' ALIGN=TOP>";
  althtmldef "O" as '<I><FONT COLOR="#CC33CC">O</FONT></I>';
  latexdef "O" as "O";
htmldef "U" as "<IMG SRC='_cu.gif' WIDTH=12 HEIGHT=19 ALT='U' ALIGN=TOP>";
  althtmldef "U" as '<I><FONT COLOR="#CC33CC">U</FONT></I>';
  latexdef "U" as "U";
htmldef "V" as "<IMG SRC='_cv.gif' WIDTH=12 HEIGHT=19 ALT='V' ALIGN=TOP>";
  althtmldef "V" as '<I><FONT COLOR="#CC33CC">V</FONT></I>';
  latexdef "V" as "V";
htmldef "W" as "<IMG SRC='_cw.gif' WIDTH=16 HEIGHT=19 ALT='W' ALIGN=TOP>";
  althtmldef "W" as '<I><FONT COLOR="#CC33CC">W</FONT></I>';
  latexdef "W" as "W";
htmldef "X" as "<IMG SRC='_cx.gif' WIDTH=13 HEIGHT=19 ALT='X' ALIGN=TOP>";
  althtmldef "X" as '<I><FONT COLOR="#CC33CC">X</FONT></I>';
  latexdef "X" as "X";
htmldef "Y" as "<IMG SRC='_cy.gif' WIDTH=12 HEIGHT=19 ALT='Y' ALIGN=TOP>";
  althtmldef "Y" as '<I><FONT COLOR="#CC33CC">Y</FONT></I>';
  latexdef "Y" as "Y";
htmldef "Z" as "<IMG SRC='_cz.gif' WIDTH=11 HEIGHT=19 ALT='Z' ALIGN=TOP>";
  althtmldef "Z" as '<I><FONT COLOR="#CC33CC">Z</FONT></I>';
  latexdef "Z" as "Z";
htmldef "~~" as
    " <IMG SRC='approx.gif' WIDTH=13 HEIGHT=19 ALT='~~' ALIGN=TOP> ";
  althtmldef "~~" as ' &#8776; '; /* &ap; */
  latexdef "~~" as "\approx";
htmldef "~<_" as
    " <IMG SRC='preccurlyeq.gif' WIDTH=11 HEIGHT=19 ALT='~&lt;_' ALIGN=TOP> ";
  althtmldef "~<_" as ' <FONT FACE=sans-serif>&#8828;</FONT> '; /* &prcue; */
  latexdef "~<_" as "\preccurlyeq";
htmldef "~<" as
    " <IMG SRC='prec.gif' WIDTH=11 HEIGHT=19 ALT='~&lt;' ALIGN=TOP> ";
  althtmldef "~<" as ' <FONT FACE=sans-serif>&#8826;</FONT> '; /* &pr; */
  latexdef "~<" as "\prec";
htmldef "Fin" as
    "<IMG SRC='_fin.gif' WIDTH=21 HEIGHT=19 ALT='Fin' ALIGN=TOP>";
  althtmldef "Fin" as 'Fin';
  latexdef "Fin" as "{\rm Fin}";
htmldef "card" as
    "<IMG SRC='_card.gif' WIDTH=30 HEIGHT=19 ALT='card' ALIGN=TOP>";
  althtmldef "card" as 'card';
  latexdef "card" as "{\rm card}";
htmldef "aleph" as
    "<IMG SRC='varaleph.gif' WIDTH=12 HEIGHT=19 ALT='aleph' ALIGN=TOP>";
  althtmldef "aleph" as '<FONT FACE=sans-serif>&#8501;</FONT>'; /* &aleph; */
  latexdef "aleph" as "\aleph";
    /* htmldef "Card" as
    "<IMG SRC='_ccard.gif' WIDTH=33 HEIGHT=19 ALT='Card' ALIGN=TOP>"; */ /*
  althtmldef "Card" as 'Card'; */ /*
  latexdef "Card" as "{\rm Card}"; */
htmldef "cf" as "<IMG SRC='__cf.gif' WIDTH=14 HEIGHT=19 ALT='cf' ALIGN=TOP>";
  althtmldef "cf" as 'cf';
  latexdef "cf" as "{\rm cf}";
htmldef "+c" as " <IMG SRC='_plc.gif' WIDTH=17 HEIGHT=19 ALT='+c' ALIGN=TOP> ";
  althtmldef "+c" as ' +<SUB><I>c</I></SUB> ';
  latexdef "+c" as "+_c";
htmldef "R1" as "<IMG SRC='_r1.gif' WIDTH=15 HEIGHT=19 ALT='R1' ALIGN=TOP>";
  althtmldef "R1" as '<I>R</I><SUB>1</SUB>';
  latexdef "R1" as "R_1";
htmldef "rank" as
    "<IMG SRC='_rank.gif' WIDTH=30 HEIGHT=19 ALT='rank' ALIGN=TOP>";
  althtmldef "rank" as 'rank';
  latexdef "rank" as "{\rm rank}";
htmldef "N." as "<IMG SRC='caln.gif' WIDTH=17 HEIGHT=19 ALT='N.' ALIGN=TOP>";
  althtmldef "N." as '<I><B>N</B></I>';
  latexdef "N." as "{\cal N}";
htmldef "+N" as " <IMG SRC='_pln.gif' WIDTH=22 HEIGHT=19 ALT='+N' ALIGN=TOP> ";
  althtmldef "+N" as ' +<I><SUB><B>N</B></SUB></I> ';
  latexdef "+N" as "+_{\cal N}";
htmldef ".N" as " <IMG SRC='_cdn.gif' WIDTH=14 HEIGHT=19 ALT='.N' ALIGN=TOP> ";
  althtmldef ".N" as ' &middot;<I><SUB><B>N</B></SUB></I> ';
  latexdef ".N" as "\cdot_{\cal N}";
htmldef "<N" as
    " <IMG SRC='_ltn.gif' WIDTH=21 HEIGHT=19 ALT='&lt;N' ALIGN=TOP> ";
  althtmldef "<N" as ' &lt;<I><SUB><B>N</B></SUB></I> ';
  latexdef "<N" as "<_{\cal N}";
htmldef "+pQ" as
    " <IMG SRC='_plpq.gif' WIDTH=28 HEIGHT=19 ALT='+pQ' ALIGN=TOP> ";
  althtmldef "+pQ" as ' +<I><SUB>p<B>Q</B></SUB></I> ';
  latexdef "+pQ" as "+_{p{\cal Q}}";
htmldef ".pQ" as
    " <IMG SRC='_cdpq.gif' WIDTH=19 HEIGHT=19 ALT='.pQ' ALIGN=TOP> ";
  althtmldef ".pQ" as ' &middot;<I><SUB>p<B>Q</B></SUB></I> ';
  latexdef ".pQ" as "\cdot_{p{\cal Q}}";
htmldef "~Q" as
    " <IMG SRC='_simq.gif' WIDTH=21 HEIGHT=19 ALT='~Q' ALIGN=TOP> ";
  althtmldef "~Q" as ' ~<I><SUB><B>Q</B></SUB></I> ';
  latexdef "~Q" as "\sim_{\cal Q}";
htmldef "Q." as "<IMG SRC='calq.gif' WIDTH=12 HEIGHT=19 ALT='Q.' ALIGN=TOP>";
  althtmldef "Q." as '<I><B>Q</B></I>';
  latexdef "Q." as "{\cal Q}";
htmldef "1Q" as "<IMG SRC='_1q.gif' WIDTH=16 HEIGHT=19 ALT='1Q' ALIGN=TOP>";
  althtmldef "1Q" as '1<I><SUB><B>Q</B></SUB></I>';
  latexdef "1Q" as "1_{\cal Q}";
htmldef "+Q" as " <IMG SRC='_plq.gif' WIDTH=21 HEIGHT=19 ALT='+Q' ALIGN=TOP> ";
  althtmldef "+Q" as ' +<I><SUB><B>Q</B></SUB></I> ';
  latexdef "+Q" as "+_{\cal Q}";
htmldef ".Q" as " <IMG SRC='_cdq.gif' WIDTH=13 HEIGHT=19 ALT='.Q' ALIGN=TOP> ";
  althtmldef ".Q" as ' &middot;<I><SUB><B>Q</B></SUB></I> ';
  latexdef ".Q" as "\cdot_{\cal Q}";
htmldef "*Q" as "<IMG SRC='_astq.gif' WIDTH=16 HEIGHT=19 ALT='*Q' ALIGN=TOP>";
  althtmldef "*Q" as '*<I><SUB><B>Q</B></SUB></I>';
  latexdef "*Q" as "\ast_{\cal Q}";
htmldef "<Q" as
    " <IMG SRC='_ltq.gif' WIDTH=20 HEIGHT=19 ALT='&lt;Q' ALIGN=TOP> ";
  althtmldef "<Q" as ' &lt;<I><SUB><B>Q</B></SUB></I> ';
  latexdef "<Q" as "<_{\cal Q}";
htmldef "P." as "<IMG SRC='calp.gif' WIDTH=13 HEIGHT=19 ALT='P.' ALIGN=TOP>";
  althtmldef "P." as '<I><B>P</B></I>';
  latexdef "P." as "{\cal P}";
htmldef "1P" as "<IMG SRC='_1p.gif' WIDTH=15 HEIGHT=19 ALT='1P' ALIGN=TOP>";
  althtmldef "1P" as '1<I><SUB><B>P</B></SUB></I>';
  latexdef "1P" as "1_{\cal P}";
htmldef "+P." as
    " <IMG SRC='_plp.gif' WIDTH=22 HEIGHT=19 ALT='+P.' ALIGN=TOP> ";
  althtmldef "+P." as ' +<I><SUB><B>P</B></SUB></I> ';
  latexdef "+P." as "+_{\cal P}";
htmldef ".P." as
    " <IMG SRC='_cdp.gif' WIDTH=13 HEIGHT=19 ALT='.P.' ALIGN=TOP> ";
  althtmldef ".P." as ' &middot;<I><SUB><B>P</B></SUB></I> ';
  latexdef ".P." as "\cdot_{\cal P}";
htmldef "<P" as
    " <IMG SRC='_ltp.gif' WIDTH=19 HEIGHT=19 ALT='&lt;P' ALIGN=TOP> ";
  althtmldef "<P" as '&lt;<I><SUB><B>P</B></SUB></I> ';
  latexdef "<P" as "<_{\cal P}";
htmldef "+pR" as
    " <IMG SRC='_plpr.gif' WIDTH=28 HEIGHT=19 ALT='+pR' ALIGN=TOP> ";
  althtmldef "+pR" as ' +<I><SUB>p<B>R</B></SUB></I> ';
  latexdef "+pR" as "+_{p{\cal R}}";
htmldef ".pR" as
    " <IMG SRC='_cdpr.gif' WIDTH=19 HEIGHT=19 ALT='.pR' ALIGN=TOP> ";
  althtmldef ".pR" as ' &middot;<I><SUB>p<B>R</B></SUB></I> ';
  latexdef ".pR" as "._{p{\cal R}}";
htmldef "~R" as
    " <IMG SRC='_simr.gif' WIDTH=23 HEIGHT=19 ALT='~R' ALIGN=TOP> ";
  althtmldef "~R" as ' ~<I><SUB><B>R</B></SUB></I> ';
  latexdef "~R" as "\sim_{\cal R}";
htmldef "R." as "<IMG SRC='calr.gif' WIDTH=15 HEIGHT=19 ALT='R.' ALIGN=TOP>";
  althtmldef "R." as '<I><B>R</B></I>';
  latexdef "R." as "{\cal R}";
htmldef "0R" as "<IMG SRC='_0r.gif' WIDTH=18 HEIGHT=19 ALT='0R' ALIGN=TOP>";
  althtmldef "0R" as '0<I><SUB><B>R</B></SUB></I>';
  latexdef "0R" as "0_{\cal R}";
htmldef "1R" as "<IMG SRC='_1r.gif' WIDTH=16 HEIGHT=19 ALT='1R' ALIGN=TOP>";
  althtmldef "1R" as '1<I><SUB><B>R</B></SUB></I>';
  latexdef "1R" as "1_{\cal R}";
htmldef "-1R" as "<IMG SRC='_m1r.gif' WIDTH=22 HEIGHT=19 ALT='-1R' ALIGN=TOP>";
  althtmldef "-1R" as '-1<I><SUB><B>R</B></SUB></I>';
  latexdef "-1R" as "-1_{\cal R}";
htmldef "+R" as " <IMG SRC='_plr.gif' WIDTH=23 HEIGHT=19 ALT='+R' ALIGN=TOP> ";
  althtmldef "+R" as ' +<I><SUB><B>R</B></SUB></I> ';
  latexdef "+R" as "+_{\cal R}";
htmldef ".R" as " <IMG SRC='_cdr.gif' WIDTH=14 HEIGHT=19 ALT='.R' ALIGN=TOP> ";
  althtmldef ".R" as ' &middot;<I><SUB><B>R</B></SUB></I> ';
  latexdef ".R" as "\cdot_{\cal R}";
htmldef "<R" as
    " <IMG SRC='_ltr.gif' WIDTH=20 HEIGHT=19 ALT='&lt;R' ALIGN=TOP> ";
  althtmldef "<R" as ' &lt;<I><SUB><B>R</B></SUB></I> ';
  latexdef "<R" as "<_{\cal R}";
htmldef "<RR" as
    " <IMG SRC='_ltbbr.gif' WIDTH=20 HEIGHT=19 ALT='&lt;R' ALIGN=TOP> ";
  althtmldef "<RR" as ' <FONT FACE=sans-serif>&lt;<SUB>&#8477;</SUB></FONT> ';
  latexdef "<RR" as "<_\mathbb{R}";
htmldef "CC" as "<IMG SRC='bbc.gif' WIDTH=12 HEIGHT=19 ALT='CC' ALIGN=TOP>";
  althtmldef "CC" as '<FONT FACE=sans-serif>&#8450;</FONT>';
  latexdef "CC" as "\mathbb{C}";
htmldef "RR" as "<IMG SRC='bbr.gif' WIDTH=13 HEIGHT=19 ALT='RR' ALIGN=TOP>";
  althtmldef "RR" as '<FONT FACE=sans-serif>&#8477;</FONT>';
  latexdef "RR" as "\mathbb{R}";
    /*latexdef "" as "_{10}";*/
    /*latexdef "" as "";*/
    /* suppress base 10 suffix */
htmldef "0" as "<IMG SRC='0.gif' WIDTH=8 HEIGHT=19 ALT='0' ALIGN=TOP>";
  althtmldef "0" as '0';
  latexdef "0" as "0";
htmldef "1" as "<IMG SRC='1.gif' WIDTH=7 HEIGHT=19 ALT='1' ALIGN=TOP>";
  althtmldef "1" as '1';
  latexdef "1" as "1";
htmldef "_i" as "<IMG SRC='rmi.gif' WIDTH=4 HEIGHT=19 ALT='_i' ALIGN=TOP>";
  althtmldef "_i" as 'i';
  latexdef "_i" as "{\rm i}";
htmldef "+" as " <IMG SRC='plus.gif' WIDTH=13 HEIGHT=19 ALT='+' ALIGN=TOP> ";
  althtmldef "+" as ' + ';
  latexdef "+" as "+";
htmldef "-" as " <IMG SRC='minus.gif' WIDTH=11 HEIGHT=19 ALT='-' ALIGN=TOP> ";
  althtmldef "-" as ' &minus; ';
  latexdef "-" as "-";
htmldef "-u" as
    "<IMG SRC='shortminus.gif' WIDTH=8 HEIGHT=19 ALT='-u' ALIGN=TOP>";
    /* use standard minus sign */
  althtmldef "-u" as '-';
  latexdef "-u" as "\textrm{-}"; /* short minus */
    /*latexdef "-u" as "-_u";*/
htmldef "x." as " <IMG SRC='cdot.gif' WIDTH=4 HEIGHT=19 ALT='x.' ALIGN=TOP> ";
  althtmldef "x." as ' &middot; '; /* what is &#xb7; ? */
  latexdef "x." as "\cdot";
htmldef "+oo" as " <IMG SRC='_pinf.gif' WIDTH=29 HEIGHT=19 ALT='+oo' " +
    "ALIGN=TOP>";
  althtmldef "+oo" as ' +&infin;';
  latexdef "+oo" as "+\infty";
htmldef "-oo" as " <IMG SRC='_minf.gif' WIDTH=24 HEIGHT=19 ALT='-oo' " +
    "ALIGN=TOP>";
  althtmldef "-oo" as ' -&infin;';
  latexdef "-oo" as "-\infty";
htmldef "RR*" as "<IMG SRC='_bbrast.gif' WIDTH=18 HEIGHT=19 ALT='RR*' " +
    "ALIGN=TOP>";
  althtmldef "RR*" as '<FONT FACE=sans-serif>&#8477;<SUP>*</SUP></FONT>';
  latexdef "RR*" as "\mathbb{R}^*";
htmldef "<" as " <IMG SRC='lt.gif' WIDTH=11 HEIGHT=19 ALT='&lt;' ALIGN=TOP> ";
  althtmldef "<" as ' &lt; ';
  latexdef "<" as "<";
htmldef "<_" as
    " <IMG SRC='le.gif' WIDTH=11 HEIGHT=19 ALT='&lt;_' ALIGN=TOP> ";
  althtmldef "<_" as ' &le; ';
  latexdef "<_" as "\le";
htmldef "2" as "<IMG SRC='2.gif' WIDTH=8 HEIGHT=19 ALT='2' ALIGN=TOP>";
  althtmldef "2" as '2';
  latexdef "2" as "2";
htmldef "3" as "<IMG SRC='3.gif' WIDTH=8 HEIGHT=19 ALT='3' ALIGN=TOP>";
  althtmldef "3" as '3';
  latexdef "3" as "3";
htmldef "4" as "<IMG SRC='4.gif' WIDTH=9 HEIGHT=19 ALT='4' ALIGN=TOP>";
  althtmldef "4" as '4';
  latexdef "4" as "4";
htmldef "5" as "<IMG SRC='5.gif' WIDTH=8 HEIGHT=19 ALT='5' ALIGN=TOP>";
  althtmldef "5" as '5';
  latexdef "5" as "5";
htmldef "6" as "<IMG SRC='6.gif' WIDTH=8 HEIGHT=19 ALT='6' ALIGN=TOP>";
  althtmldef "6" as '6';
  latexdef "6" as "6";
htmldef "7" as "<IMG SRC='7.gif' WIDTH=9 HEIGHT=19 ALT='7' ALIGN=TOP>";
  althtmldef "7" as '7';
  latexdef "7" as "7";
htmldef "8" as "<IMG SRC='8.gif' WIDTH=8 HEIGHT=19 ALT='8' ALIGN=TOP>";
  althtmldef "8" as '8';
  latexdef "8" as "8";
htmldef "9" as "<IMG SRC='9.gif' WIDTH=8 HEIGHT=19 ALT='9' ALIGN=TOP>";
  althtmldef "9" as '9';
  latexdef "9" as "9";
htmldef "10" as "<IMG SRC='_10.gif' WIDTH=14 HEIGHT=19 ALT='10' ALIGN=TOP>";
  althtmldef "10" as '10';
  latexdef "10" as "10";
htmldef "NN" as "<IMG SRC='bbn.gif' WIDTH=12 HEIGHT=19 ALT='NN' ALIGN=TOP>";
  althtmldef "NN" as '<FONT FACE=sans-serif>&#8469;</FONT>'; /* &Nopf; */
  latexdef "NN" as "\mathbb{N}";
htmldef "NN0" as
    "<IMG SRC='_bbn0.gif' WIDTH=19 HEIGHT=19 ALT='NN0' ALIGN=TOP>";
  althtmldef "NN0" as '<FONT FACE=sans-serif>&#8469;<SUB>0</SUB></FONT>';
  latexdef "NN0" as "\mathbb{N}_0";
htmldef "ZZ" as "<IMG SRC='bbz.gif' WIDTH=11 HEIGHT=19 ALT='ZZ' ALIGN=TOP>";
  althtmldef "ZZ" as '<FONT FACE=sans-serif>&#8484;</FONT>';
  latexdef "ZZ" as "\mathbb{Z}";
htmldef "QQ" as "<IMG SRC='bbq.gif' WIDTH=13 HEIGHT=19 ALT='QQ' ALIGN=TOP>";
  althtmldef "QQ" as '<FONT FACE=sans-serif>&#8474;</FONT>';
  latexdef "QQ" as "\mathbb{Q}";
htmldef "RR+" as "<IMG SRC='_bbrplus.gif' WIDTH=20 HEIGHT=19 ALT='RR+' " +
    "ALIGN=TOP>";
  althtmldef "RR+" as '<FONT FACE=sans-serif>&#8477;<SUP>+</SUP></FONT>';
  latexdef "RR+" as "\mathbb{R}^+";
htmldef "sqr" as "<IMG SRC='surd.gif' WIDTH=14 HEIGHT=19 ALT='sqr' ALIGN=TOP>";
  althtmldef "sqr" as '&radic;';
  latexdef "sqr" as "\surd";
htmldef "Re" as "<IMG SRC='re.gif' WIDTH=12 HEIGHT=19 ALT='Re' ALIGN=TOP>";
  althtmldef "Re" as '<FONT FACE=sans-serif>&real;</FONT>';
  latexdef "Re" as "\Re";
htmldef "Im" as "<IMG SRC='im.gif' WIDTH=12 HEIGHT=19 ALT='Im' ALIGN=TOP>";
  althtmldef "Im" as '<FONT FACE=sans-serif>&image;</FONT>';
  latexdef "Im" as "\Im";
htmldef "*" as "<IMG SRC='ast.gif' WIDTH=7 HEIGHT=19 ALT='*' ALIGN=TOP>";
  althtmldef "*" as '<FONT FACE=sans-serif>&lowast;</FONT>';
  latexdef "*" as "*";
htmldef "abs" as "<IMG SRC='_abs.gif' WIDTH=22 HEIGHT=19 ALT='abs' ALIGN=TOP>";
  althtmldef "abs" as 'abs';
  latexdef "abs" as "{\rm abs}";
htmldef "|_" as "<IMG SRC='lfloor.gif' WIDTH=6 HEIGHT=19 ALT='|_' " +
    "ALIGN=TOP>";
  althtmldef "|_" as '&#8970;';
  latexdef "|_" as "\lfloor";
htmldef "mod" as " <IMG SRC='_mod.gif' WIDTH=29 HEIGHT=19 ALT='mod' " +
    "ALIGN=TOP> ";
  althtmldef "mod" as ' mod ';
  latexdef "mod" as "{\rm mod}";
htmldef "==" as " <IMG SRC='equiv.gif' WIDTH=12 HEIGHT=19 ALT='==' " +
    "ALIGN=TOP> ";
  althtmldef "==" as "&equiv;"; /* 2263 */
  latexdef "==" as "\equiv";
htmldef "seq1" as " <IMG SRC='_seq1.gif' WIDTH=26 HEIGHT=19 ALT='seq1' " +
    "ALIGN=TOP> ";
  althtmldef "seq1" as 'seq<SUB>1</SUB>';
  latexdef "seq1" as "{\rm seq}_1";
htmldef "shift" as " <IMG SRC='_shift.gif' WIDTH=30 HEIGHT=19 ALT='shift' " +
    "ALIGN=TOP> ";
  althtmldef "shift" as 'shift';
  latexdef "shift" as "{\rm shift}";
htmldef "(,)" as "(,)"; /* 26-Dec-2006 nm This needs image. */
  althtmldef "(,)" as "(,)";
  latexdef "(,)" as "(,)";
htmldef "(,]" as "(,]"; /* 26-Dec-2006 nm This needs image. */
  althtmldef "(,]" as "(,]";
  latexdef "(,]" as "(,]";
htmldef "[,)" as "[,)"; /* 26-Dec-2006 nm This needs image. */
  althtmldef "[,)" as "[,)";
  latexdef "[,)" as "[,)";
htmldef "[,]" as "[,]"; /* 26-Dec-2006 nm This needs image. */
  althtmldef "[,]" as "[,]";
  latexdef "[,]" as "[,]";
htmldef "ZZ>=" as "<IMG SRC='_bbzge.gif' WIDTH=20 HEIGHT=19 ALT=" +
    "'ZZ>=' ALIGN=TOP>";
  althtmldef "ZZ>=" as "<FONT FACE=sans-serif>&#8484;<SUB>&ge;</SUB></FONT>";
  latexdef "ZZ>=" as "\mathbb{Z}_\ge";
htmldef "limsup" as
    "<IMG SRC='_limsup.gif' WIDTH=44 HEIGHT=19 ALT='limsup' ALIGN=TOP>";
  althtmldef "limsup" as 'lim sup';
  latexdef "limsup" as "\limsup";
htmldef "seq" as " <IMG SRC='_seq.gif' WIDTH=22 HEIGHT=19 ALT='seq' " +
    "ALIGN=TOP> ";
  althtmldef "seq" as 'seq';
  latexdef "seq" as "{\rm seq}";
htmldef "seq0" as " <IMG SRC='_seq0.gif' WIDTH=27 HEIGHT=19 ALT='seq0' " +
    "ALIGN=TOP> ";
  althtmldef "seq0" as 'seq<SUB>0</SUB>';
  latexdef "seq0" as "{\rm seq}_0";
htmldef "^" as "<IMG SRC='uparrow.gif' WIDTH=7 HEIGHT=19 ALT='^' ALIGN=TOP>";
  althtmldef "^" as '&uarr;';
  latexdef "^" as "\uparrow"; /*
  latexdef "^" as "\widehat{\ }"; */
htmldef "!" as "<IMG SRC='bang.gif' WIDTH=3 HEIGHT=19 ALT='!' ALIGN=TOP>";
  althtmldef "!" as '!';
  latexdef "!" as "{!}";
htmldef "_C" as " <IMG SRC='rmcc.gif' WIDTH=10 HEIGHT=19 ALT='_C' ALIGN=TOP> ";
  althtmldef "_C" as 'C';
  latexdef "_C" as "{\rm C}";
htmldef "~~>" as " <IMG SRC='rightsquigarrow.gif' WIDTH=15 HEIGHT=19 " +
    "ALT='~~&gt;' ALIGN=TOP> ";
  althtmldef "~~>" as ' <FONT FACE=sans-serif>&#8669;</FONT> ';
  latexdef "~~>" as '\rightsquigarrow';
htmldef "..." as "<IMG SRC='ldots.gif' WIDTH=18 HEIGHT=19 ALT=" +
    "'...' ALIGN=TOP>";
  althtmldef "..." as "...";
  latexdef "..." as "\ldots";
htmldef "sum_" as "<IMG SRC='csigma.gif' WIDTH=11 HEIGHT=19 ALT=" +
    "'sum_' ALIGN=TOP>";
  althtmldef "sum_" as "&Sigma;";
  latexdef "sum_" as "\Sigma";
htmldef "exp" as "<IMG SRC='_exp.gif' WIDTH=24 HEIGHT=19 ALT='exp' ALIGN=TOP>";
  althtmldef "exp" as "exp";
  latexdef "exp" as "\exp";
htmldef "_e" as "<IMG SRC='rme.gif' WIDTH=7 HEIGHT=19 ALT='_e' ALIGN=TOP>";
  althtmldef "_e" as "e";
  latexdef "_e" as "{\rm e}";
htmldef "sin" as "<IMG SRC='_sin.gif' WIDTH=19 HEIGHT=19 ALT='sin' ALIGN=TOP>";
  althtmldef "sin" as "sin";
  latexdef "sin" as "\sin";
htmldef "pi" as "<IMG SRC='pi.gif' WIDTH=10 HEIGHT=19 ALT='pi' ALIGN=TOP>";
  althtmldef "pi" as "<I>&pi;</I>";
  latexdef "pi" as "\pi";
htmldef "cos" as "<IMG SRC='_cos.gif' WIDTH=21 HEIGHT=19 ALT='cos' ALIGN=TOP>";
  althtmldef "cos" as "cos";
  latexdef "cos" as "\cos";
htmldef "log" as "<IMG SRC='_log.gif' WIDTH=20 HEIGHT=19 ALT='log' ALIGN=TOP>";
  althtmldef "log" as "log";
  latexdef "log" as "\log";
htmldef "-cn->" as "<IMG SRC='_cnmap.gif' WIDTH=23 HEIGHT=19 ALT=" +
    "'-cn->' ALIGN=TOP>";
  althtmldef "-cn->" as '&ndash;<FONT SIZE=-1 FACE=sans-serif>cn</FONT>&rarr;';
  latexdef "-cn->" as
    "\raisebox{.5ex}{${\textstyle{\:}_{\mbox{\footnotesize\rm cn" +
    "}}}\atop{\textstyle{" +
    "\longrightarrow}\atop{\textstyle{}^{\mbox{\footnotesize\rm {\ }}}}}$}";
htmldef "Top" as "Top"; /* 12-Jul-2006 nm These need images. */
  althtmldef "Top" as "Top";
  latexdef "Top" as "{\rm Top}";
htmldef "TopSp" as "TopSp";
  althtmldef "TopSp" as "TopSp";
  latexdef "TopSp" as "{\rm TopSp}";
htmldef "Bases" as "Bases";
  althtmldef "Bases" as "Bases";
  latexdef "Bases" as "{\rm Bases}";
htmldef "topGen" as "topGen";
  althtmldef "topGen" as "topGen";
  latexdef "topGen" as "{\rm topGen}";
/* htmldef "tX" as "tX";
  althtmldef "tX" as "tX";
  latexdef "tX" as "{\rm tX}"; */ /* Moved to main set.mm */
htmldef "int" as "int";
  althtmldef "int" as "int";
  latexdef "int" as "{\rm int}";
htmldef "cls" as "cls";
  althtmldef "cls" as "cls";
  latexdef "cls" as "{\rm cls}";
htmldef "Clsd" as "Clsd";
  althtmldef "Clsd" as "Clsd";
  latexdef "Clsd" as "{\rm Clsd}";
htmldef "nei" as "nei";
  althtmldef "nei" as "nei";
  latexdef "nei" as "{\rm nei}";
htmldef "limPt" as "limPt";
  althtmldef "limPt" as "limPt";
  latexdef "limPt" as "{\rm limPt}";
htmldef "Cn" as " Cn ";
  althtmldef "Cn" as " Cn ";
  latexdef "Cn" as "{\rm Cn}";
htmldef "CnP" as " CnP ";
  althtmldef "CnP" as " CnP ";
  latexdef "CnP" as "{\rm CnP}";
htmldef "Haus" as "Haus";
  althtmldef "Haus" as "Haus";
  latexdef "Haus" as "{\rm Haus}";
htmldef "Met" as "Met";
  althtmldef "Met" as "Met";
  latexdef "Met" as "{\rm Met}";
htmldef "MetSp" as "MetSp";
  althtmldef "MetSp" as "MetSp";
  latexdef "MetSp" as "{\rm MetSp}";
htmldef "ball" as " ball ";
  althtmldef "ball" as " ball ";
  latexdef "ball" as "{\rm ball}";
htmldef "Open" as "Open";
  althtmldef "Open" as "Open";
  latexdef "Open" as "{\rm Open}";
htmldef "~~>m" as "<IMG SRC='_squigm.gif' WIDTH=25 HEIGHT=19 " +
    "ALT='~~&gt;m' ALIGN=TOP>";
  althtmldef "~~>m" as '<FONT FACE=sans-serif>&#8669;<SUB>m</SUB></FONT>';
  latexdef "~~>m" as "\rightsquigarrow_{\rm m}";
htmldef "Cau" as "Cau"; /* 7-Sep-2006 nm These need images. */
  althtmldef "Cau" as "Cau";
  latexdef "Cau" as "{\rm Cau}";
htmldef "CMet" as "CMet";
  althtmldef "CMet" as "CMet";
  latexdef "CMet" as "{\rm CMet}";
htmldef "Grp" as "Grp";
  althtmldef "Grp" as "Grp";
  latexdef "Grp" as "{\rm Grp}";
htmldef "Id" as "Id";
  althtmldef "Id" as "Id";
  latexdef "Id" as "{\rm Id}";
htmldef "inv" as "inv";
  althtmldef "inv" as "inv";
  latexdef "inv" as "{\rm inv}";
htmldef "/g" as " <IMG SRC='_divg.gif' WIDTH=11 HEIGHT=19 ALT='/g' " +
    "ALIGN=TOP> ";
  althtmldef "/g" as " /<SUB><I>g</I></SUB> ";
  latexdef "/g" as "/_g";
htmldef "^g" as
    "<IMG SRC='_hatg.gif' WIDTH=11 HEIGHT=19 ALT='^g' ALIGN=TOP>";
  althtmldef "^g" as "&uarr;<SUB><I>g</I></SUB>";
  latexdef "^g" as "\uparrow_g";
htmldef "Abel" as "Abel";
  althtmldef "Abel" as "Abel";
  latexdef "Abel" as "{\rm Abel}";
htmldef "SubGrp" as "SubGrp";
  althtmldef "SubGrp" as "SubGrp";
  latexdef "SubGrp" as "{\rm SubGrp}";
htmldef "SymGrp" as "SymGrp";
  althtmldef "SymGrp" as "SymGrp";
  latexdef "SymGrp" as "{\rm SymGrp}";
htmldef "GrpHom" as " GrpHom ";
  althtmldef "GrpHom" as " GrpHom ";
  latexdef "GrpHom" as "{\rm GrpHom}";
htmldef "GrpIso" as " GrpIso ";
  althtmldef "GrpIso" as " GrpIso ";
  latexdef "GrpIso" as "{\rm GrpIso}";
htmldef "GrpAct" as "GrpAct";
  althtmldef "GrpAct" as "GrpAct";
  latexdef "GrpAct" as "{\rm GrpAct}";
htmldef "Ring" as "Ring";
  althtmldef "Ring" as "Ring";
  latexdef "Ring" as "{\rm Ring}";
htmldef "DivRing" as "DivRing";
  althtmldef "DivRing" as "DivRing";
  latexdef "DivRing" as "{\rm DivRing}";
htmldef "*-Fld" as "*-Fld";
  althtmldef "*-Fld" as "*-Fld";
  latexdef "*-Fld" as "\ast-{\rm Fld}";
htmldef "CVec" as "CVec";
  althtmldef "CVec" as "CVec";
  latexdef "CVec" as "{\rm CVec}";
htmldef "NrmCVec" as "NrmCVec";
  althtmldef "NrmCVec" as "NrmCVec";
  latexdef "NrmCVec" as "{\rm NrmCVec}";
htmldef "BaseSet" as "BaseSet";
  althtmldef "BaseSet" as "BaseSet";
  latexdef "BaseSet" as "{\rm BaseSet}";
htmldef "+v" as "<IMG SRC='_plv.gif' WIDTH=19 HEIGHT=19 ALT='+v' ALIGN=TOP>";
  althtmldef "+v" as ' +<SUB><I>v</I></SUB> ';
  latexdef "+v" as '+_v';
htmldef ".s" as "<IMG SRC='_cds.gif' WIDTH=9 HEIGHT=19 ALT='.s' ALIGN=TOP>";
  althtmldef ".s" as ' <B>&middot;</B><SUB><I>s</I></SUB> ';
  latexdef ".s" as '\cdot_s';
htmldef "0v" as "<IMG SRC='_0v.gif' WIDTH=14 HEIGHT=19 ALT='0v' ALIGN=TOP>";
  althtmldef "0v" as '0<SUB><I>v</I></SUB>';
  latexdef "0v" as '0_v';
htmldef "-v" as "<IMG SRC='_mv.gif' WIDTH=17 HEIGHT=19 ALT='-v' ALIGN=TOP>";
  althtmldef "-v" as ' &minus;<SUB><I>v</I></SUB> ';
  latexdef "-v" as '-_v';
htmldef ".i" as "<IMG SRC='_cdi.gif' WIDTH=7 HEIGHT=19 ALT='.i' ALIGN=TOP>";
  althtmldef ".i" as ' <B>&middot;</B><SUB><I>i</I></SUB> ';
  latexdef ".i" as '\cdot_i';
htmldef "norm" as "<IMG SRC='_norm.gif' WIDTH=32 HEIGHT=19 ALT='norm' " +
    "ALIGN=TOP>";
  althtmldef "norm" as 'norm';
  latexdef "norm" as '{\rm norm}';
    /* 24-Apr-2007 nm These need images. */
htmldef "IndMet" as "IndMet";
  althtmldef "IndMet" as "IndMet";
  latexdef "IndMet" as "{\rm IndMet}";
htmldef "SubSp" as "SubSp";
  althtmldef "SubSp" as "SubSp";
  latexdef "SubSp" as "{\rm SubSp}";
htmldef "normOp" as "normOp";
  althtmldef "normOp" as " normOp ";
  latexdef "normOp" as "{\rm normOp}";
htmldef "LnOp" as " LnOp ";
  althtmldef "LnOp" as " LnOp ";
  latexdef "LnOp" as "{\rm LnOp}";
htmldef "BLnOp" as " BLnOp ";
  althtmldef "BLnOp" as " BLnOp ";
  latexdef "BLnOp" as "{\rm BLnOp}";
htmldef "0op" as
    " <IMG SRC='_0op.gif' WIDTH=21 HEIGHT=19 ALT='0op' ALIGN=TOP> ";
  althtmldef "0op" as " 0<SUB>op</SUB> ";
  latexdef "0op" as "0_\mathrm{op}";
htmldef "adj" as "adj";
  althtmldef "adj" as "adj";
  latexdef "adj" as "{\rm adj}";
htmldef "HmOp" as "HmOp";
  althtmldef "HmOp" as "HmOp";
  latexdef "HmOp" as "{\rm HmOp}";
htmldef "CPreHil" as "CPreHil";
  althtmldef "CPreHil" as "CPreHil";
  latexdef "CPreHil" as "{\rm CPreHil}";
htmldef "CBan" as "CBan";
  althtmldef "CBan" as "CBan";
  latexdef "CBan" as "{\rm CBan}";
htmldef "CHil" as "CHil";
  althtmldef "CHil" as "CHil";
  latexdef "CHil" as "{\rm CHil}";
htmldef "Poset" as "Poset";
  althtmldef "Poset" as "Poset";
  latexdef "Poset" as "{\rm Poset}";
htmldef "supw" as " sup<SUB>w</SUB> ";
  althtmldef "supw" as " sup<SUB>w</SUB> ";
  latexdef "supw" as "{\rm sup}_{\rm w}";
htmldef "infw" as " inf<SUB>w</SUB> ";
  althtmldef "infw" as " inf<SUB>w</SUB> ";
  latexdef "infw" as "{\rm inf}_{\rm w}";
htmldef "Lat" as "Lat";
  althtmldef "Lat" as "Lat";
  latexdef "Lat" as "{\rm Lat}";
    /* Hilbert space */
htmldef "~H" as "<IMG SRC='scrh.gif' WIDTH=19 HEIGHT=19 ALT='~H' ALIGN=TOP>";
  althtmldef "~H" as ' <FONT FACE=sans-serif>&#8459;</FONT> ';
    /* 8459 is script H; 8460 is fraktur H */
  latexdef "~H" as '\mathfrak{H}';
htmldef "+h" as " <IMG SRC='_pvh.gif' WIDTH=18 HEIGHT=19 ALT='+h' ALIGN=TOP> ";
  althtmldef "+h" as ' +<SUB><I>h</I></SUB> ';
  latexdef "+h" as '+_h';
htmldef ".h" as " <IMG SRC='_cdh.gif' WIDTH=9 HEIGHT=19 ALT='.h' ALIGN=TOP> ";
  althtmldef ".h" as ' <B>&middot;</B><SUB><I>h</I></SUB> ';
  latexdef ".h" as '\cdot_h';
htmldef "0h" as "<IMG SRC='_0vh.gif' WIDTH=14 HEIGHT=19 ALT='0h' ALIGN=TOP>";
  althtmldef "0h" as '0<SUB><I>h</I></SUB>';
  latexdef "0h" as '0_h';
htmldef "-h" as " <IMG SRC='_mvh.gif' WIDTH=16 HEIGHT=19 ALT='-h' ALIGN=TOP> ";
  althtmldef "-h" as ' &minus;<SUB><I>h</I></SUB> ';
  latexdef "-h" as '-_h';
htmldef ".ih" as " <IMG SRC='_cdih.gif' WIDTH=13 HEIGHT=19 ALT='.ih' " +
    "ALIGN=TOP> ";
  althtmldef ".ih" as ' <B>&middot;</B><SUB><I>ih</I></SUB> ';
  latexdef ".ih" as '\cdot_{ih}';
htmldef "normh" as "<IMG SRC='_normh.gif' WIDTH=38 HEIGHT=19 ALT='normh' " +
    "ALIGN=TOP>";
  althtmldef "normh" as 'norm<SUB><I>h</I></SUB>';
  latexdef "normh" as '{\rm norm}_h';
htmldef "Cauchy" as "<IMG SRC='_cauchy.gif' WIDTH=47 HEIGHT=19 " +
    "ALT='Cauchy' ALIGN=TOP>";
  althtmldef "Cauchy" as 'Cauchy';
  latexdef "Cauchy" as '{\rm Cauchy}';
htmldef "~~>v" as " <IMG SRC='_squigv.gif' WIDTH=21 HEIGHT=19 " +
    "ALT='~~&gt;v' ALIGN=TOP> ";
  althtmldef "~~>v" as
    ' <FONT FACE=sans-serif>&#8669;<SUB><I>v</I></SUB></FONT> ';
  latexdef "~~>v" as '\rightsquigarrow_v';
htmldef "SH" as "<IMG SRC='_sh.gif' WIDTH=24 HEIGHT=19 ALT='SH' ALIGN=TOP>";
  althtmldef "SH" as
    ' <FONT FACE=sans-serif><I><B>S</B></I><SUB>&#8459;</SUB></FONT> ';
  latexdef "SH" as '\mathfrak{S}_\mathfrak{H}';
htmldef "CH" as "<IMG SRC='_scrch.gif' WIDTH=22 HEIGHT=19 ALT='CH' ALIGN=TOP>";
  althtmldef "CH" as
    ' <FONT FACE=sans-serif><I><B>C</B></I><SUB>&#8459;</SUB></FONT> ';
  latexdef "CH" as '\mathfrak{C}_\mathfrak{H}';
/*htmldef "-H" as
    "<IMG SRC='_lnoth.gif' WIDTH=23 HEIGHT=19 ALT='-H' ALIGN=TOP>";*/
  /*althtmldef "-H" as '&minus;<SUB>&#8459;</SUB>';*/
  /*latexdef "-H" as '-_\mathfrak{H}'; */
htmldef "_|_" as "<IMG SRC='perp.gif' WIDTH=11 HEIGHT=19 ALT='_|_' ALIGN=TOP>";
  althtmldef "_|_" as '<FONT FACE=sans-serif>&#8869;</FONT>'; /* &bottom; */
  latexdef "_|_" as '\perp';
htmldef "+H" as " <IMG SRC='_plh.gif' WIDTH=24 HEIGHT=19 ALT='+H' ALIGN=TOP> ";
  althtmldef "+H" as ' <FONT FACE=sans-serif>+<SUB>&#8459;</SUB></FONT> ';
  latexdef "+H" as '+_\mathfrak{H}';
htmldef "span" as
    "<IMG SRC='_span.gif' WIDTH=31 HEIGHT=19 ALT='span' ALIGN=TOP>";
  althtmldef "span" as 'span';
  latexdef "span" as '{\rm span}';
htmldef "vH" as
    " <IMG SRC='_veeh.gif' WIDTH=21 HEIGHT=19 ALT='vH' ALIGN=TOP> ";
  althtmldef "vH" as ' <FONT FACE=sans-serif>&or;<SUB>&#8459;</SUB></FONT> ';
  latexdef "vH" as '\vee_\mathfrak{H}';
htmldef "\/H" as
    " <IMG SRC='_bigveeh.gif' WIDTH=23 HEIGHT=19 ALT='\/H' ALIGN=TOP> ";
  althtmldef "\/H" as ' <FONT FACE=sans-serif><FONT SIZE="+1">&or;' +
    '</FONT><SUB>&#8459;</SUB></FONT> ';
  latexdef "\/H" as '\bigvee_\mathfrak{H}';
htmldef "0H" as "<IMG SRC='_0h.gif' WIDTH=20 HEIGHT=19 ALT='0H' ALIGN=TOP>";
  althtmldef "0H" as '<FONT FACE=sans-serif>0<SUB>&#8459;</SUB></FONT>';
  latexdef "0H" as '0_\mathfrak{H}';
htmldef "C_H" as
    " <IMG SRC='_cch.gif' WIDTH=23 HEIGHT=19 ALT='C_H' ALIGN=TOP> ";
  althtmldef "C_H" as
    ' <FONT FACE=sans-serif><I>C</I><SUB>&#8459;</SUB></FONT> ';
  latexdef "C_H" as 'C_\mathfrak{H}';
htmldef "proj" as 'proj'; /* Jan-18-2006 nm This doesn't have an image. */
  althtmldef "proj" as 'proj';
  latexdef "proj" as '{\rm proj}';
htmldef "+op" as
    " <IMG SRC='_plop.gif' WIDTH=25 HEIGHT=19 ALT='+op' ALIGN=TOP> ";
  althtmldef "+op" as ' +<SUB>op</SUB> ';
  latexdef "+op" as '+_\mathrm{op}';
htmldef ".op" as
    " <IMG SRC='_cdop.gif' WIDTH=16 HEIGHT=19 ALT='.op' ALIGN=TOP> ";
  althtmldef ".op" as ' <B>&middot;</B><SUB>op</SUB> ';
  latexdef ".op" as '\cdot_\mathrm{op}';
htmldef "-op" as
    " <IMG SRC='_mop.gif' WIDTH=23 HEIGHT=19 ALT='-op' ALIGN=TOP> ";
  althtmldef "-op" as ' &minus;<SUB>op</SUB> ';
  latexdef "-op" as '-_\mathrm{op}';
htmldef "+fn" as
    " <IMG SRC='_plfn.gif' WIDTH=24 HEIGHT=19 ALT='+fn' ALIGN=TOP> ";
  althtmldef "+fn" as ' +<SUB>fn</SUB> ';
  latexdef "+fn" as '+_\mathrm{fn}';
htmldef ".fn" as
    " <IMG SRC='_cdfn.gif' WIDTH=15 HEIGHT=19 ALT='.fn' ALIGN=TOP> ";
  althtmldef ".fn" as ' <B>&middot;</B><SUB>fn</SUB> ';
  latexdef ".fn" as '\cdot_\mathrm{fn}';
htmldef "Iop" as
    " <IMG SRC='_iop.gif' WIDTH=18 HEIGHT=19 ALT='Iop' ALIGN=TOP> ";
  althtmldef "Iop" as ' I<SUB>op</SUB> ';
  latexdef "Iop" as "I_\mathrm{op}";
htmldef "0hop" as "0<SUB>hop</SUB>"; /* These don't have images. */
  althtmldef "0hop" as ' 0<SUB>hop</SUB> ';
  latexdef "0hop" as "0_\mathrm{hop}";
htmldef "normop" as 'norm<SUB>op</SUB>';
  althtmldef "normop" as 'norm<SUB>op</SUB>';
  latexdef "normop" as '{\rm norm}_{\rm op}';
htmldef "ConOp" as 'ConOp';
  althtmldef "ConOp" as 'ConOp';
  latexdef "ConOp" as '{\rm ConOp}';
htmldef "LinOp" as 'LinOp';
  althtmldef "LinOp" as 'LinOp';
  latexdef "LinOp" as '{\rm LinOp}';
htmldef "BndLinOp" as 'BndLinOp';
  althtmldef "BndLinOp" as 'BndLinOp';
  latexdef "BndLinOp" as '{\rm BndLinOp}';
htmldef "UniOp" as 'UniOp';
  althtmldef "UniOp" as 'UniOp';
  latexdef "UniOp" as '{\rm UniOp}';
htmldef "HrmOp" as 'HrmOp';
  althtmldef "HrmOp" as 'HrmOp';
  latexdef "HrmOp" as '{\rm HrmOp}';
htmldef "normfn" as 'norm<SUB>fn</SUB>';
  althtmldef "normfn" as 'norm<SUB>fn</SUB>';
  latexdef "normfn" as '{\rm norm}_{\rm fn}';
htmldef "null" as 'null';
  althtmldef "null" as 'null';
  latexdef "null" as '{\rm null}';
htmldef "ConFn" as 'ConFn';
  althtmldef "ConFn" as 'ConFn';
  latexdef "ConFn" as '{\rm ConFn}';
htmldef "LinFn" as 'LinFn';
  althtmldef "LinFn" as 'LinFn';
  latexdef "LinFn" as '{\rm LinFn}';
htmldef "adjh" as 'adj<SUB>h</SUB>';
  althtmldef "adjh" as 'adj<SUB>h</SUB>';
  latexdef "adjh" as '{\rm adj}_h';
htmldef "bra" as 'bra';
  althtmldef "bra" as 'bra';
  latexdef "bra" as '{\rm bra}';
htmldef "ketbra" as ' ketbra ';
  althtmldef "ketbra" as ' ketbra ';
  latexdef "ketbra" as '{\rm ketbra}';
htmldef "<_op" as
    " <IMG SRC='_leop.gif' WIDTH=24 HEIGHT=19 ALT='<_op' ALIGN=TOP> ";
  althtmldef "<_op" as " &le;<SUB>op</SUB> ";
  latexdef "<_op" as '\le_{\rm op}';
htmldef "eigvec" as 'eigvec'; /* These don't have images. */
  althtmldef "eigvec" as 'eigvec';
  latexdef "eigvec" as '{\rm eigvec}';
htmldef "eigval" as 'eigval';
  althtmldef "eigval" as 'eigval';
  latexdef "eigval" as '{\rm eigval}';
htmldef "Lambda" as
    "<IMG SRC='clambda.gif' WIDTH=11 HEIGHT=19 ALT='Lambda' ALIGN=TOP>";
  althtmldef "Lambda" as 'Lambda';
  latexdef "Lambda" as '\Lambda';
htmldef "States" as
    "<IMG SRC='_states.gif' WIDTH=40 HEIGHT=19 ALT='States' ALIGN=TOP>";
  althtmldef "States" as 'States';
  latexdef "States" as '{\rm States}';
    /* These don't have images. */
htmldef "CHStates" as 'CHStates';
  althtmldef "CHStates" as 'CHStates';
  latexdef "CHStates" as '{\rm CHStates}';
htmldef "Atoms" as
    "<IMG SRC='_atoms.gif' WIDTH=40 HEIGHT=19 ALT='Atoms' ALIGN=TOP>";
  althtmldef "Atoms" as 'Atoms';
  latexdef "Atoms" as '{\rm Atoms}';
htmldef "<o" as
    " <IMG SRC='lessdot.gif' WIDTH=11 HEIGHT=19 ALT='&lt;o' ALIGN=TOP> ";
  althtmldef "<o" as ' <FONT FACE=sans-serif>&#8918;</FONT> ';
  latexdef "<o" as '\lessdot';
htmldef "MH" as " <IMG SRC='_mh.gif' WIDTH=27 HEIGHT=19 ALT='MH' ALIGN=TOP> ";
  althtmldef "MH" as
    ' <FONT FACE=sans-serif><I>M</I><SUB>&#8459;</SUB></FONT> ';
  latexdef "MH" as 'M_\mathfrak{H}';
htmldef "MH*" as
    " <IMG SRC='_mhast.gif' WIDTH=27 HEIGHT=19 ALT='MH*' ALIGN=TOP> ";
  althtmldef "MH*" as
    ' <FONT FACE=sans-serif><I>M</I><SUB>&#8459;</SUB><SUP>*</SUP></FONT> ';
  latexdef "MH*" as 'M_\mathfrak{H}^*';

/* stuff to be sorted into the right places above: */
htmldef "T." as " <IMG SRC='top.gif' WIDTH=11 HEIGHT=19 ALT='T.' ALIGN=TOP> ";
  althtmldef "T." as ' &#x22A4; ';
  latexdef "T." as "\top";
htmldef "l" as '<I><FONT COLOR="#FF0000">l</FONT></I>';
  althtmldef "l" as '<I><FONT COLOR="#FF0000">l</FONT></I>';
  latexdef "l" as "l";
htmldef "Homeo" as " Homeo ";
  althtmldef "Homeo" as " Homeo ";
  latexdef "Homeo" as "{\rm Homeo}";
htmldef "~=" as " ~= ";
  althtmldef "~=" as " ~= ";
  latexdef "~=" as "~=";
htmldef "subSp" as "subSp";
  althtmldef "subSp" as "subSp";
  latexdef "subSp" as "{\rm subSp}";
htmldef "fBas" as "fBas";
  althtmldef "fBas" as "fBas";
  latexdef "fBas" as "{\rm fBas}";
htmldef "filGen" as "filGen";
  althtmldef "filGen" as "filGen";
  latexdef "filGen" as "{\rm filGen}";
htmldef "Fil" as "Fil";
  althtmldef "Fil" as "Fil";
  latexdef "Fil" as "{\rm Fil}";
htmldef "fLim1" as "fLim1";
  althtmldef "fLim1" as "fLim1";
  latexdef "fLim1" as "{\rm fLim1}";
htmldef "FilMap" as " FilMap ";
  althtmldef "FilMap" as " FilMap ";
  latexdef "FilMap" as "{\rm FilMap}";
htmldef "fLimf" as " fLimf ";
  althtmldef "fLimf" as " fLimf ";
  latexdef "fLimf" as "{\rm fLimf}";
htmldef "fClus" as "fClus";
  althtmldef "fClus" as "fClus";
  latexdef "fClus" as "{\rm fClus}";
htmldef "fClusf" as " fClusf ";
  althtmldef "fClusf" as " fClusf ";
  latexdef "fClusf" as "{\rm fClusf}";
htmldef "Fre" as "Fre";
  althtmldef "Fre" as "Fre";
  latexdef "Fre" as "{\rm Fre}";
htmldef "Comp" as "Comp";
  althtmldef "Comp" as "Comp";
  latexdef "Comp" as "{\rm Comp}";
htmldef "Con" as "Con";
  althtmldef "Con" as "Con";
  latexdef "Con" as "{\rm Con}";
htmldef "Plig" as "Plig";
  althtmldef "Plig" as "Plig";
  latexdef "Plig" as "{\rm Plig}";
htmldef "tX" as " <IMG SRC='times.gif' WIDTH=9 HEIGHT=19 ALT='X.' ALIGN=TOP>" +
    "<IMG SRC='subt.gif' WIDTH=5 HEIGHT=19 ALT='t' ALIGN=TOP> ";
  althtmldef "tX" as " &times;<SUB>t</SUB> ";
  latexdef "tX" as "\times_t";
htmldef "Ass" as "Ass";
  althtmldef "Ass" as "Ass";
  latexdef "Ass" as "{\rm Ass}";
htmldef "ExId" as " ExId ";
  althtmldef "ExId" as " ExId ";
  latexdef "ExId" as "{\rm ExId}";
htmldef "Magma" as "Magma";
  althtmldef "Magma" as "Magma";
  latexdef "Magma" as "{\rm Magma}";
htmldef "SemiGrp" as "SemiGrp";
  althtmldef "SemiGrp" as "SemiGrp";
  latexdef "SemiGrp" as "{\rm SemiGrp}";
htmldef "Mnd" as "Mnd";
  althtmldef "Mnd" as "Mnd";
  latexdef "Mnd" as "{\rm Mnd}";
htmldef "Com2" as "Com2";
  althtmldef "Com2" as "Com2";
  latexdef "Com2" as "{\rm Com2}";
htmldef "Fld" as "Fld";
  althtmldef "Fld" as "Fld";
  latexdef "Fld" as "{\rm Fld}";


/* htmldef, althtmldef, latexdef for mathboxes */
/* Note the "Mathbox of" instead of "Mathbox for" to make searching easier. */

/* Mathbox of Jonathan Ben-Naim */
htmldef    "ph'" as "<IMG SRC='_bnj_phiPrime.gif'   WIDTH=15 HEIGHT=19" +
       " ALT=" + '"' + "ph'" + '"' + " ALIGN=TOP>";
htmldef    "ps'" as "<IMG SRC='_bnj_psiPrime.gif'   WIDTH=16 HEIGHT=19" +
       " ALT=" + '"' + "ps'" + '"' + " ALIGN=TOP>";
htmldef    "ch'" as "<IMG SRC='_bnj_chiPrime.gif'   WIDTH=16 HEIGHT=19" +
       " ALT=" + '"' + "ch'" + '"' + " ALIGN=TOP>";
htmldef    "th'" as "<IMG SRC='_bnj_thetaPrime.gif' WIDTH=12 HEIGHT=19" +
       " ALT=" + '"' + "th'" + '"' + " ALIGN=TOP>";
htmldef    "ta'" as "<IMG SRC='_bnj_tauPrime.gif'   WIDTH=14 HEIGHT=19" +
       " ALT=" + '"' + "ta'" + '"' + " ALIGN=TOP>";
htmldef    "et'" as "<IMG SRC='_bnj_etaPrime.gif'   WIDTH=13 HEIGHT=19" +
       " ALT=" + '"' + "et'" + '"' + " ALIGN=TOP>";
htmldef    "ze'" as "<IMG SRC='_bnj_zetaPrime.gif'  WIDTH=13 HEIGHT=19" +
       " ALT=" + '"' + "ze'" + '"' + " ALIGN=TOP>";
htmldef    "si'" as "<IMG SRC='_bnj_sigmaPrime.gif' WIDTH=14 HEIGHT=19" +
       " ALT=" + '"' + "si'" + '"' + " ALIGN=TOP>";
htmldef    "rh'" as "<IMG SRC='_bnj_rhoPrime.gif'   WIDTH=13 HEIGHT=19" +
       " ALT=" + '"' + "rh'" + '"' + " ALIGN=TOP>";
althtmldef "ph'" as '<FONT COLOR="#0000FF"><I>&phi;&prime;</I></FONT>';
althtmldef "ps'" as '<FONT COLOR="#0000FF"><I>&psi;&prime;</I></FONT>';
althtmldef "ch'" as '<FONT COLOR="#0000FF"><I>&chi;&prime;</I></FONT>';
althtmldef "th'" as '<FONT COLOR="#0000FF"><I>&theta;&prime;</I></FONT>';
althtmldef "ta'" as '<FONT COLOR="#0000FF"><I>&tau;&prime;</I></FONT>';
althtmldef "et'" as '<FONT COLOR="#0000FF"><I>&eta;&prime;</I></FONT>';
althtmldef "ze'" as '<FONT COLOR="#0000FF"><I>&zeta;&prime;</I></FONT>';
althtmldef "si'" as '<FONT COLOR="#0000FF"><I>&sigma;&prime;</I></FONT>';
althtmldef "rh'" as '<FONT COLOR="#0000FF"><I>&rho;&prime;</I></FONT>';
latexdef   "ph'" as "\varphi'";
latexdef   "ps'" as "\psi'";
latexdef   "ch'" as "\chi'";
latexdef   "th'" as "\theta'";
latexdef   "ta'" as "\tau'";
latexdef   "et'" as "\eta'";
latexdef   "ze'" as "\zeta'";
latexdef   "si'" as "\sigma'";
latexdef   "rh'" as "\rho'";
htmldef    'ph"' as "<IMG SRC='_bnj_phiPrimePrime.gif'   WIDTH=18 HEIGHT=19" +
      " ALT='ph" + '"' + "' ALIGN=TOP>";
htmldef    'ps"' as "<IMG SRC='_bnj_psiPrimePrime.gif'   WIDTH=19 HEIGHT=19" +
      " ALT='ps" + '"' + "' ALIGN=TOP>";
htmldef    'ch"' as "<IMG SRC='_bnj_chiPrimePrime.gif'   WIDTH=19 HEIGHT=19" +
      " ALT='ch" + '"' + "' ALIGN=TOP>";
htmldef    'th"' as "<IMG SRC='_bnj_thetaPrimePrime.gif' WIDTH=15 HEIGHT=19" +
      " ALT='th" + '"' + "' ALIGN=TOP>";
htmldef    'ta"' as "<IMG SRC='_bnj_tauPrimePrime.gif'   WIDTH=17 HEIGHT=19" +
      " ALT='ta" + '"' + "' ALIGN=TOP>";
htmldef    'et"' as "<IMG SRC='_bnj_etaPrimePrime.gif'   WIDTH=16 HEIGHT=19" +
      " ALT='et" + '"' + "' ALIGN=TOP>";
htmldef    'ze"' as "<IMG SRC='_bnj_zetaPrimePrime.gif'  WIDTH=16 HEIGHT=19" +
      " ALT='ze" + '"' + "' ALIGN=TOP>";
htmldef    'si"' as "<IMG SRC='_bnj_sigmaPrimePrime.gif' WIDTH=17 HEIGHT=19" +
      " ALT='si" + '"' + "' ALIGN=TOP>";
htmldef    'rh"' as "<IMG SRC='_bnj_rhoPrimePrime.gif'   WIDTH=16 HEIGHT=19" +
      " ALT='rh" + '"' + "' ALIGN=TOP>";
althtmldef 'ph"' as '<FONT COLOR="#0000FF"><I>&phi;&Prime;</I></FONT>';
althtmldef 'ps"' as '<FONT COLOR="#0000FF"><I>&psi;&Prime;</I></FONT>';
althtmldef 'ch"' as '<FONT COLOR="#0000FF"><I>&chi;&Prime;</I></FONT>';
althtmldef 'th"' as '<FONT COLOR="#0000FF"><I>&theta;&Prime;</I></FONT>';
althtmldef 'ta"' as '<FONT COLOR="#0000FF"><I>&tau;&Prime;</I></FONT>';
althtmldef 'et"' as '<FONT COLOR="#0000FF"><I>&eta;&Prime;</I></FONT>';
althtmldef 'ze"' as '<FONT COLOR="#0000FF"><I>&zeta;&Prime;</I></FONT>';
althtmldef 'si"' as '<FONT COLOR="#0000FF"><I>&sigma;&Prime;</I></FONT>';
althtmldef 'rh"' as '<FONT COLOR="#0000FF"><I>&rho;&Prime;</I></FONT>';
latexdef   'ph"' as "\varphi''";
latexdef   'ps"' as "\psi''";
latexdef   'ch"' as "\chi''";
latexdef   'th"' as "\theta''";
latexdef   'ta"' as "\tau''";
latexdef   'et"' as "\eta''";
latexdef   'ze"' as "\zeta''";
latexdef   'si"' as "\sigma''";
latexdef   'rh"' as "\rho''";
htmldef    "ph0"  as "<IMG SRC='_bnj_phi0.gif'   WIDTH=18 HEIGHT=19" +
    " ALT='ph0' ALIGN=TOP>";
htmldef    "ps0"  as "<IMG SRC='_bnj_psi0.gif'   WIDTH=19 HEIGHT=19" +
    " ALT='ps0' ALIGN=TOP>";
htmldef    "ch0_" as "<IMG SRC='_bnj_chi0.gif'   WIDTH=19 HEIGHT=19" +
    " ALT='ch0_' ALIGN=TOP>";
htmldef    "th0"  as "<IMG SRC='_bnj_theta0.gif' WIDTH=15 HEIGHT=19" +
    " ALT='th0' ALIGN=TOP>";
htmldef    "ta0"  as "<IMG SRC='_bnj_tau0.gif'   WIDTH=17 HEIGHT=19" +
    " ALT='ta0' ALIGN=TOP>";
htmldef    "et0"  as "<IMG SRC='_bnj_eta0.gif'   WIDTH=16 HEIGHT=19" +
    " ALT='et0' ALIGN=TOP>";
htmldef    "ze0"  as "<IMG SRC='_bnj_zeta0.gif'  WIDTH=16 HEIGHT=19" +
    " ALT='ze0' ALIGN=TOP>";
htmldef    "si0"  as "<IMG SRC='_bnj_sigma0.gif' WIDTH=17 HEIGHT=19" +
    " ALT='si0' ALIGN=TOP>";
htmldef    "rh0"  as "<IMG SRC='_bnj_rho0.gif'   WIDTH=16 HEIGHT=19" +
    " ALT='rh0' ALIGN=TOP>";
althtmldef "ph0"  as '<FONT COLOR="#0000FF"><I>&phi;<SUB>0</SUB></I></FONT>';
althtmldef "ps0"  as '<FONT COLOR="#0000FF"><I>&psi;<SUB>0</SUB></I></FONT>';
althtmldef "ch0_" as '<FONT COLOR="#0000FF"><I>&chi;<SUB>0</SUB></I></FONT>';
althtmldef "th0"  as '<FONT COLOR="#0000FF"><I>&theta;<SUB>0</SUB></I></FONT>';
althtmldef "ta0"  as '<FONT COLOR="#0000FF"><I>&tau;<SUB>0</SUB></I></FONT>';
althtmldef "et0"  as '<FONT COLOR="#0000FF"><I>&eta;<SUB>0</SUB></I></FONT>';
althtmldef "ze0"  as '<FONT COLOR="#0000FF"><I>&zeta;<SUB>0</SUB></I></FONT>';
althtmldef "si0"  as '<FONT COLOR="#0000FF"><I>&sigma;<SUB>0</SUB></I></FONT>';
althtmldef "rh0"  as '<FONT COLOR="#0000FF"><I>&rho;<SUB>0</SUB></I></FONT>';
latexdef   "ph0"  as "\varphi_0";
latexdef   "ps0"  as "\psi_0";
latexdef   "ch0_" as "\chi_0";
latexdef   "th0"  as "\theta_0";
latexdef   "ta0"  as "\tau_0";
latexdef   "et0"  as "\eta_0";
latexdef   "ze0"  as "\zeta_0";
latexdef   "si0"  as "\sigma_0";
latexdef   "rh0"  as "\rho_0";
htmldef    "ph1" as "<IMG SRC='_bnj_phi1.gif'   WIDTH=16 HEIGHT=19" +
    " ALT='ph1' ALIGN=TOP>";
htmldef    "ps1" as "<IMG SRC='_bnj_psi1.gif'   WIDTH=17 HEIGHT=19" +
    " ALT='ps1' ALIGN=TOP>";
htmldef    "ch1" as "<IMG SRC='_bnj_chi1.gif'   WIDTH=17 HEIGHT=19" +
    " ALT='ch1' ALIGN=TOP>";
htmldef    "th1" as "<IMG SRC='_bnj_theta1.gif' WIDTH=13 HEIGHT=19" +
    " ALT='th1' ALIGN=TOP>";
htmldef    "ta1" as "<IMG SRC='_bnj_tau1.gif'   WIDTH=15 HEIGHT=19" +
    " ALT='ta1' ALIGN=TOP>";
htmldef    "et1" as "<IMG SRC='_bnj_eta1.gif'   WIDTH=14 HEIGHT=19" +
    " ALT='et1' ALIGN=TOP>";
htmldef    "ze1" as "<IMG SRC='_bnj_zeta1.gif'  WIDTH=14 HEIGHT=19" +
    " ALT='ze1' ALIGN=TOP>";
htmldef    "si1" as "<IMG SRC='_bnj_sigma1.gif' WIDTH=15 HEIGHT=19" +
    " ALT='si1' ALIGN=TOP>";
htmldef    "rh1" as "<IMG SRC='_bnj_rho1.gif'   WIDTH=14 HEIGHT=19" +
    " ALT='rh1' ALIGN=TOP>";
althtmldef "ph1" as '<FONT COLOR="#0000FF"><I>&phi;<SUB>1</SUB></I></FONT>';
althtmldef "ps1" as '<FONT COLOR="#0000FF"><I>&psi;<SUB>1</SUB></I></FONT>';
althtmldef "ch1" as '<FONT COLOR="#0000FF"><I>&chi;<SUB>1</SUB></I></FONT>';
althtmldef "th1" as '<FONT COLOR="#0000FF"><I>&theta;<SUB>1</SUB></I></FONT>';
althtmldef "ta1" as '<FONT COLOR="#0000FF"><I>&tau;<SUB>1</SUB></I></FONT>';
althtmldef "et1" as '<FONT COLOR="#0000FF"><I>&eta;<SUB>1</SUB></I></FONT>';
althtmldef "ze1" as '<FONT COLOR="#0000FF"><I>&zeta;<SUB>1</SUB></I></FONT>';
althtmldef "si1" as '<FONT COLOR="#0000FF"><I>&sigma;<SUB>1</SUB></I></FONT>';
althtmldef "rh1" as '<FONT COLOR="#0000FF"><I>&rho;<SUB>1</SUB></I></FONT>';
latexdef   "ph1" as "\varphi_1";
latexdef   "ps1" as "\psi_1";
latexdef   "ch1" as "\chi_1";
latexdef   "th1" as "\theta_1";
latexdef   "ta1" as "\tau_1";
latexdef   "et1" as "\eta_1";
latexdef   "ze1" as "\zeta_1";
latexdef   "si1" as "\sigma_1";
latexdef   "rh1" as "\rho_1";
htmldef    "a'" as "<IMG SRC='_bnj_aPrime.gif' WIDTH=13 HEIGHT=19" +
    " ALT=" + '"' + "a'" + '"' + " ALIGN=TOP>";
htmldef    "b'" as "<IMG SRC='_bnj_bPrime.gif' WIDTH=12 HEIGHT=19" +
    " ALT=" + '"' + "b'" + '"' + " ALIGN=TOP>";
htmldef    "c'" as "<IMG SRC='_bnj_cPrime.gif' WIDTH=11 HEIGHT=19" +
    " ALT=" + '"' + "c'" + '"' + " ALIGN=TOP>";
htmldef    "d'" as "<IMG SRC='_bnj_dPrime.gif' WIDTH=13 HEIGHT=19" +
    " ALT=" + '"' + "d'" + '"' + " ALIGN=TOP>";
htmldef    "e'" as "<IMG SRC='_bnj_ePrime.gif' WIDTH=12 HEIGHT=19" +
    " ALT=" + '"' + "e'" + '"' + " ALIGN=TOP>";
htmldef    "f'" as "<IMG SRC='_bnj_fPrime.gif' WIDTH=13 HEIGHT=19" +
    " ALT=" + '"' + "f'" + '"' + " ALIGN=TOP>";
htmldef    "g'" as "<IMG SRC='_bnj_gPrime.gif' WIDTH=13 HEIGHT=19" +
    " ALT=" + '"' + "g'" + '"' + " ALIGN=TOP>";
htmldef    "h'" as "<IMG SRC='_bnj_hPrime.gif' WIDTH=14 HEIGHT=19" +
    " ALT=" + '"' + "h'" + '"' + " ALIGN=TOP>";
htmldef    "i'" as "<IMG SRC='_bnj_iPrime.gif' WIDTH=10 HEIGHT=19" +
    " ALT=" + '"' + "i'" + '"' + " ALIGN=TOP>";
htmldef    "j'" as "<IMG SRC='_bnj_jPrime.gif' WIDTH=11 HEIGHT=19" +
    " ALT=" + '"' + "j'" + '"' + " ALIGN=TOP>";
htmldef    "k'" as "<IMG SRC='_bnj_kPrime.gif' WIDTH=13 HEIGHT=19" +
    " ALT=" + '"' + "k'" + '"' + " ALIGN=TOP>";
htmldef    "l'" as "<IMG SRC='_bnj_lPrime.gif' WIDTH=9  HEIGHT=19" +
    " ALT=" + '"' + "l'" + '"' + " ALIGN=TOP>";
htmldef    "m'" as "<IMG SRC='_bnj_mPrime.gif' WIDTH=18 HEIGHT=19" +
    " ALT=" + '"' + "m'" + '"' + " ALIGN=TOP>";
htmldef    "n'" as "<IMG SRC='_bnj_nPrime.gif' WIDTH=14 HEIGHT=19" +
    " ALT=" + '"' + "n'" + '"' + " ALIGN=TOP>";
htmldef    "o'" as "<IMG SRC='_bnj_oPrime.gif' WIDTH=12 HEIGHT=19" +
    " ALT=" + '"' + "o'" + '"' + " ALIGN=TOP>";
htmldef    "p'" as "<IMG SRC='_bnj_pPrime.gif' WIDTH=14 HEIGHT=19" +
    " ALT=" + '"' + "p'" + '"' + " ALIGN=TOP>";
htmldef    "q'" as "<IMG SRC='_bnj_qPrime.gif' WIDTH=12 HEIGHT=19" +
    " ALT=" + '"' + "q'" + '"' + " ALIGN=TOP>";
htmldef    "r'" as "<IMG SRC='_bnj_rPrime.gif' WIDTH=12 HEIGHT=19" +
    " ALT=" + '"' + "r'" + '"' + " ALIGN=TOP>";
htmldef    "s'" as "<IMG SRC='_bnj_sPrime.gif' WIDTH=11 HEIGHT=19" +
    " ALT=" + '"' + "s'" + '"' + " ALIGN=TOP>";
htmldef    "t'" as "<IMG SRC='_bnj_tPrime.gif' WIDTH=11 HEIGHT=19" +
    " ALT=" + '"' + "t'" + '"' + " ALIGN=TOP>";
htmldef    "u'" as "<IMG SRC='_bnj_uPrime.gif' WIDTH=14 HEIGHT=19" +
    " ALT=" + '"' + "u'" + '"' + " ALIGN=TOP>";
htmldef    "v'" as "<IMG SRC='_bnj_vPrime.gif' WIDTH=13 HEIGHT=19" +
    " ALT=" + '"' + "v'" + '"' + " ALIGN=TOP>";
htmldef    "w'" as "<IMG SRC='_bnj_wPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "w'" + '"' + " ALIGN=TOP>";
htmldef    "x'" as "<IMG SRC='_bnj_xPrime.gif' WIDTH=14 HEIGHT=19" +
    " ALT=" + '"' + "x'" + '"' + " ALIGN=TOP>";
htmldef    "y'" as "<IMG SRC='_bnj_yPrime.gif' WIDTH=13 HEIGHT=19" +
    " ALT=" + '"' + "y'" + '"' + " ALIGN=TOP>";
htmldef    "z'" as "<IMG SRC='_bnj_zPrime.gif' WIDTH=13 HEIGHT=19" +
    " ALT=" + '"' + "z'" + '"' + " ALIGN=TOP>";
althtmldef "a'" as '<I><FONT COLOR="#FF0000">a&prime;</FONT></I>';
althtmldef "b'" as '<I><FONT COLOR="#FF0000">b&prime;</FONT></I>';
althtmldef "c'" as '<I><FONT COLOR="#FF0000">c&prime;</FONT></I>';
althtmldef "d'" as '<I><FONT COLOR="#FF0000">d&prime;</FONT></I>';
althtmldef "e'" as '<I><FONT COLOR="#FF0000">e&prime;</FONT></I>';
althtmldef "f'" as '<I><FONT COLOR="#FF0000">f&prime;</FONT></I>';
althtmldef "g'" as '<I><FONT COLOR="#FF0000">g&prime;</FONT></I>';
althtmldef "h'" as '<I><FONT COLOR="#FF0000">h&prime;</FONT></I>';
althtmldef "i'" as '<I><FONT COLOR="#FF0000">i&prime;</FONT></I>';
althtmldef "j'" as '<I><FONT COLOR="#FF0000">j&prime;</FONT></I>';
althtmldef "k'" as '<I><FONT COLOR="#FF0000">k&prime;</FONT></I>';
althtmldef "l'" as '<I><FONT COLOR="#FF0000">l&prime;</FONT></I>';
althtmldef "m'" as '<I><FONT COLOR="#FF0000">m&prime;</FONT></I>';
althtmldef "n'" as '<I><FONT COLOR="#FF0000">n&prime;</FONT></I>';
althtmldef "o'" as '<I><FONT COLOR="#FF0000">o&prime;</FONT></I>';
althtmldef "p'" as '<I><FONT COLOR="#FF0000">p&prime;</FONT></I>';
althtmldef "q'" as '<I><FONT COLOR="#FF0000">q&prime;</FONT></I>';
althtmldef "r'" as '<I><FONT COLOR="#FF0000">r&prime;</FONT></I>';
althtmldef "s'" as '<I><FONT COLOR="#FF0000">s&prime;</FONT></I>';
althtmldef "t'" as '<I><FONT COLOR="#FF0000">t&prime;</FONT></I>';
althtmldef "u'" as '<I><FONT COLOR="#FF0000">u&prime;</FONT></I>';
althtmldef "v'" as '<I><FONT COLOR="#FF0000">v&prime;</FONT></I>';
althtmldef "w'" as '<I><FONT COLOR="#FF0000">w&prime;</FONT></I>';
althtmldef "x'" as '<I><FONT COLOR="#FF0000">x&prime;</FONT></I>';
althtmldef "y'" as '<I><FONT COLOR="#FF0000">y&prime;</FONT></I>';
althtmldef "z'" as '<I><FONT COLOR="#FF0000">z&prime;</FONT></I>';
latexdef   "a'" as "a'";
latexdef   "b'" as "b'";
latexdef   "c'" as "c'";
latexdef   "d'" as "d'";
latexdef   "e'" as "e'";
latexdef   "f'" as "f'";
latexdef   "g'" as "g'";
latexdef   "h'" as "h'";
latexdef   "i'" as "i'";
latexdef   "j'" as "j'";
latexdef   "k'" as "k'";
latexdef   "l'" as "l'";
latexdef   "m'" as "m'";
latexdef   "n'" as "n'";
latexdef   "o'" as "o'";
latexdef   "p'" as "p'";
latexdef   "q'" as "q'";
latexdef   "r'" as "r'";
latexdef   "s'" as "s'";
latexdef   "t'" as "t'";
latexdef   "u'" as "u'";
latexdef   "v'" as "v'";
latexdef   "w'" as "w'";
latexdef   "x'" as "x'";
latexdef   "y'" as "y'";
latexdef   "z'" as "z'";
htmldef    'a"' as "<IMG SRC='_bnj_aPrimePrime.gif' WIDTH=16 HEIGHT=19 " +
      " ALT='a" + '"' + "' ALIGN=TOP>";
htmldef    'b"' as "<IMG SRC='_bnj_bPrimePrime.gif' WIDTH=15 HEIGHT=19 " +
      " ALT='b" + '"' + "' ALIGN=TOP>";
htmldef    'c"' as "<IMG SRC='_bnj_cPrimePrime.gif' WIDTH=14 HEIGHT=19 " +
      " ALT='c" + '"' + "' ALIGN=TOP>";
htmldef    'd"' as "<IMG SRC='_bnj_dPrimePrime.gif' WIDTH=16 HEIGHT=19 " +
      " ALT='d" + '"' + "' ALIGN=TOP>";
htmldef    'e"' as "<IMG SRC='_bnj_ePrimePrime.gif' WIDTH=15 HEIGHT=19 " +
      " ALT='e" + '"' + "' ALIGN=TOP>";
htmldef    'f"' as "<IMG SRC='_bnj_fPrimePrime.gif' WIDTH=16 HEIGHT=19 " +
      " ALT='f" + '"' + "' ALIGN=TOP>";
htmldef    'g"' as "<IMG SRC='_bnj_gPrimePrime.gif' WIDTH=16 HEIGHT=19 " +
      " ALT='g" + '"' + "' ALIGN=TOP>";
htmldef    'h"' as "<IMG SRC='_bnj_hPrimePrime.gif' WIDTH=17 HEIGHT=19 " +
      " ALT='h" + '"' + "'ALIGN=TOP>";
htmldef    'i"' as "<IMG SRC='_bnj_iPrimePrime.gif' WIDTH=13 HEIGHT=19 " +
      " ALT='i" + '"' + "' ALIGN=TOP>";
htmldef    'j"' as "<IMG SRC='_bnj_jPrimePrime.gif' WIDTH=14 HEIGHT=19 " +
      " ALT='j" + '"' + "' ALIGN=TOP>";
htmldef    'k"' as "<IMG SRC='_bnj_kPrimePrime.gif' WIDTH=16 HEIGHT=19 " +
      " ALT='k" + '"' + "' ALIGN=TOP>";
htmldef    'l"' as "<IMG SRC='_bnj_lPrimePrime.gif' WIDTH=12 HEIGHT=19 " +
      " ALT='l" + '"' + "'ALIGN=TOP>";
htmldef    'm"' as "<IMG SRC='_bnj_mPrimePrime.gif' WIDTH=21 HEIGHT=19 " +
      " ALT='m" + '"' + "' ALIGN=TOP>";
htmldef    'n"' as "<IMG SRC='_bnj_nPrimePrime.gif' WIDTH=17 HEIGHT=19 " +
      " ALT='n" + '"' + "' ALIGN=TOP>";
htmldef    'o"' as "<IMG SRC='_bnj_oPrimePrime.gif' WIDTH=15 HEIGHT=19 " +
      " ALT='o" + '"' + "'ALIGN=TOP>";
htmldef    'p"' as "<IMG SRC='_bnj_pPrimePrime.gif' WIDTH=17 HEIGHT=19 " +
      " ALT='p" + '"' + "'ALIGN=TOP>";
htmldef    'q"' as "<IMG SRC='_bnj_qPrimePrime.gif' WIDTH=15 HEIGHT=19 " +
      " ALT='q" + '"' + "'ALIGN=TOP>";
htmldef    'r"' as "<IMG SRC='_bnj_rPrimePrime.gif' WIDTH=15 HEIGHT=19 " +
      " ALT='r" + '"' + "'ALIGN=TOP>";
htmldef    's"' as "<IMG SRC='_bnj_sPrimePrime.gif' WIDTH=14 HEIGHT=19 " +
      " ALT='s" + '"' + "'ALIGN=TOP>";
htmldef    't"' as "<IMG SRC='_bnj_tPrimePrime.gif' WIDTH=14 HEIGHT=19 " +
      " ALT='t" + '"' + "'ALIGN=TOP>";
htmldef    'u"' as "<IMG SRC='_bnj_uPrimePrime.gif' WIDTH=17 HEIGHT=19 " +
      " ALT='u" + '"' + "'ALIGN=TOP>";
htmldef    'v"' as "<IMG SRC='_bnj_vPrimePrime.gif' WIDTH=16 HEIGHT=19 " +
      " ALT='v" + '"' + "'ALIGN=TOP>";
htmldef    'w"' as "<IMG SRC='_bnj_wPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='w" + '"' + "'ALIGN=TOP>";
htmldef    'x"' as "<IMG SRC='_bnj_xPrimePrime.gif' WIDTH=17 HEIGHT=19 " +
      " ALT='x" + '"' + "'ALIGN=TOP>";
htmldef    'y"' as "<IMG SRC='_bnj_yPrimePrime.gif' WIDTH=16 HEIGHT=19 " +
      " ALT='y" + '"' + "'ALIGN=TOP>";
htmldef    'z"' as "<IMG SRC='_bnj_zPrimePrime.gif' WIDTH=16 HEIGHT=19 " +
      " ALT='z" + '"' + "'ALIGN=TOP>";
althtmldef 'a"' as '<I><FONT COLOR="#FF0000">a&Prime;</FONT></I>';
althtmldef 'b"' as '<I><FONT COLOR="#FF0000">b&Prime;</FONT></I>';
althtmldef 'c"' as '<I><FONT COLOR="#FF0000">c&Prime;</FONT></I>';
althtmldef 'd"' as '<I><FONT COLOR="#FF0000">d&Prime;</FONT></I>';
althtmldef 'e"' as '<I><FONT COLOR="#FF0000">e&Prime;</FONT></I>';
althtmldef 'f"' as '<I><FONT COLOR="#FF0000">f&Prime;</FONT></I>';
althtmldef 'g"' as '<I><FONT COLOR="#FF0000">g&Prime;</FONT></I>';
althtmldef 'h"' as '<I><FONT COLOR="#FF0000">h&Prime;</FONT></I>';
althtmldef 'i"' as '<I><FONT COLOR="#FF0000">i&Prime;</FONT></I>';
althtmldef 'j"' as '<I><FONT COLOR="#FF0000">j&Prime;</FONT></I>';
althtmldef 'k"' as '<I><FONT COLOR="#FF0000">k&Prime;</FONT></I>';
althtmldef 'l"' as '<I><FONT COLOR="#FF0000">l&Prime;</FONT></I>';
althtmldef 'm"' as '<I><FONT COLOR="#FF0000">m&Prime;</FONT></I>';
althtmldef 'n"' as '<I><FONT COLOR="#FF0000">n&Prime;</FONT></I>';
althtmldef 'o"' as '<I><FONT COLOR="#FF0000">o&Prime;</FONT></I>';
althtmldef 'p"' as '<I><FONT COLOR="#FF0000">p&Prime;</FONT></I>';
althtmldef 'q"' as '<I><FONT COLOR="#FF0000">q&Prime;</FONT></I>';
althtmldef 'r"' as '<I><FONT COLOR="#FF0000">r&Prime;</FONT></I>';
althtmldef 's"' as '<I><FONT COLOR="#FF0000">s&Prime;</FONT></I>';
althtmldef 't"' as '<I><FONT COLOR="#FF0000">t&Prime;</FONT></I>';
althtmldef 'u"' as '<I><FONT COLOR="#FF0000">u&Prime;</FONT></I>';
althtmldef 'v"' as '<I><FONT COLOR="#FF0000">v&Prime;</FONT></I>';
althtmldef 'w"' as '<I><FONT COLOR="#FF0000">w&Prime;</FONT></I>';
althtmldef 'x"' as '<I><FONT COLOR="#FF0000">x&Prime;</FONT></I>';
althtmldef 'y"' as '<I><FONT COLOR="#FF0000">y&Prime;</FONT></I>';
althtmldef 'z"' as '<I><FONT COLOR="#FF0000">z&Prime;</FONT></I>';
latexdef   'a"' as "a''";
latexdef   'b"' as "b''";
latexdef   'c"' as "c''";
latexdef   'd"' as "d''";
latexdef   'e"' as "e''";
latexdef   'f"' as "f''";
latexdef   'g"' as "g''";
latexdef   'h"' as "h''";
latexdef   'i"' as "i''";
latexdef   'j"' as "j''";
latexdef   'k"' as "k''";
latexdef   'l"' as "l''";
latexdef   'm"' as "m''";
latexdef   'n"' as "n''";
latexdef   'o"' as "o''";
latexdef   'p"' as "p''";
latexdef   'q"' as "q''";
latexdef   'r"' as "r''";
latexdef   's"' as "s''";
latexdef   't"' as "t''";
latexdef   'u"' as "u''";
latexdef   'v"' as "v''";
latexdef   'w"' as "w''";
latexdef   'x"' as "x''";
latexdef   'y"' as "y''";
latexdef   'z"' as "z''";
htmldef    "a0"  as "<IMG SRC='_bnj_a0.gif' WIDTH=16 HEIGHT=19" +
    " ALT='a0' ALIGN=TOP>";
htmldef    "b0"  as "<IMG SRC='_bnj_b0.gif' WIDTH=15 HEIGHT=19" +
    " ALT='b0' ALIGN=TOP>";
htmldef    "c0_" as "<IMG SRC='_bnj_c0.gif' WIDTH=14 HEIGHT=19" +
    " ALT='c0_' ALIGN=TOP>";
htmldef    "d0"  as "<IMG SRC='_bnj_d0.gif' WIDTH=16 HEIGHT=19" +
    " ALT='d0' ALIGN=TOP>";
htmldef    "e0"  as "<IMG SRC='_bnj_e0.gif' WIDTH=15 HEIGHT=19" +
    " ALT='e0' ALIGN=TOP>";
htmldef    "f0_" as "<IMG SRC='_bnj_f0.gif' WIDTH=16 HEIGHT=19" +
    " ALT='f0_' ALIGN=TOP>";
htmldef    "g0"  as "<IMG SRC='_bnj_g0.gif' WIDTH=16 HEIGHT=19" +
    " ALT='g0' ALIGN=TOP>";
htmldef    "h0"  as "<IMG SRC='_bnj_h0.gif' WIDTH=17 HEIGHT=19" +
    " ALT='h0' ALIGN=TOP>";
htmldef    "i0"  as "<IMG SRC='_bnj_i0.gif' WIDTH=13 HEIGHT=19" +
    " ALT='i0' ALIGN=TOP>";
htmldef    "j0"  as "<IMG SRC='_bnj_j0.gif' WIDTH=14 HEIGHT=19" +
    " ALT='j0' ALIGN=TOP>";
htmldef    "k0"  as "<IMG SRC='_bnj_k0.gif' WIDTH=16 HEIGHT=19" +
    " ALT='k0' ALIGN=TOP>";
htmldef    "l0"  as "<IMG SRC='_bnj_l0.gif' WIDTH=12 HEIGHT=19" +
    " ALT='l0' ALIGN=TOP>";
htmldef    "m0"  as "<IMG SRC='_bnj_m0.gif' WIDTH=21 HEIGHT=19" +
    " ALT='m0' ALIGN=TOP>";
htmldef    "n0_" as "<IMG SRC='_bnj_n0.gif' WIDTH=17 HEIGHT=19" +
    " ALT='n0_' ALIGN=TOP>";
htmldef    "o0"  as "<IMG SRC='_bnj_o0.gif' WIDTH=15 HEIGHT=19" +
    " ALT='o0' ALIGN=TOP>";
htmldef    "p0"  as "<IMG SRC='_bnj_p0.gif' WIDTH=17 HEIGHT=19" +
    " ALT='p0' ALIGN=TOP>";
htmldef    "q0"  as "<IMG SRC='_bnj_q0.gif' WIDTH=15 HEIGHT=19" +
    " ALT='q0' ALIGN=TOP>";
htmldef    "r0"  as "<IMG SRC='_bnj_r0.gif' WIDTH=15 HEIGHT=19" +
    " ALT='r0' ALIGN=TOP>";
htmldef    "s0"  as "<IMG SRC='_bnj_s0.gif' WIDTH=14 HEIGHT=19" +
    " ALT='s0' ALIGN=TOP>";
htmldef    "t0"  as "<IMG SRC='_bnj_t0.gif' WIDTH=14 HEIGHT=19" +
    " ALT='t0' ALIGN=TOP>";
htmldef    "u0"  as "<IMG SRC='_bnj_u0.gif' WIDTH=17 HEIGHT=19" +
    " ALT='u0' ALIGN=TOP>";
htmldef    "v0"  as "<IMG SRC='_bnj_v0.gif' WIDTH=16 HEIGHT=19" +
    " ALT='v0' ALIGN=TOP>";
htmldef    "w0"  as "<IMG SRC='_bnj_w0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='w0' ALIGN=TOP>";
htmldef    "x0"  as "<IMG SRC='_bnj_x0.gif' WIDTH=17 HEIGHT=19" +
    " ALT='x0' ALIGN=TOP>";
htmldef    "y0"  as "<IMG SRC='_bnj_y0.gif' WIDTH=16 HEIGHT=19" +
    " ALT='y0' ALIGN=TOP>";
htmldef    "z0"  as "<IMG SRC='_bnj_z0.gif' WIDTH=16 HEIGHT=19" +
    " ALT='z0' ALIGN=TOP>";
althtmldef "a0"  as '<I><FONT COLOR="#FF0000">a<SUB>0</SUB></FONT></I>';
althtmldef "b0"  as '<I><FONT COLOR="#FF0000">b<SUB>0</SUB></FONT></I>';
althtmldef "c0_" as '<I><FONT COLOR="#FF0000">c<SUB>0</SUB></FONT></I>';
althtmldef "d0"  as '<I><FONT COLOR="#FF0000">d<SUB>0</SUB></FONT></I>';
althtmldef "e0"  as '<I><FONT COLOR="#FF0000">e<SUB>0</SUB></FONT></I>';
althtmldef "f0_" as '<I><FONT COLOR="#FF0000">f<SUB>0</SUB></FONT></I>';
althtmldef "g0"  as '<I><FONT COLOR="#FF0000">g<SUB>0</SUB></FONT></I>';
althtmldef "h0"  as '<I><FONT COLOR="#FF0000">h<SUB>0</SUB></FONT></I>';
althtmldef "i0"  as '<I><FONT COLOR="#FF0000">i<SUB>0</SUB></FONT></I>';
althtmldef "j0"  as '<I><FONT COLOR="#FF0000">j<SUB>0</SUB></FONT></I>';
althtmldef "k0"  as '<I><FONT COLOR="#FF0000">k<SUB>0</SUB></FONT></I>';
althtmldef "l0"  as '<I><FONT COLOR="#FF0000">l<SUB>0</SUB></FONT></I>';
althtmldef "m0"  as '<I><FONT COLOR="#FF0000">m<SUB>0</SUB></FONT></I>';
althtmldef "n0_" as '<I><FONT COLOR="#FF0000">n<SUB>0</SUB></FONT></I>';
althtmldef "o0"  as '<I><FONT COLOR="#FF0000">o<SUB>0</SUB></FONT></I>';
althtmldef "p0"  as '<I><FONT COLOR="#FF0000">p<SUB>0</SUB></FONT></I>';
althtmldef "q0"  as '<I><FONT COLOR="#FF0000">q<SUB>0</SUB></FONT></I>';
althtmldef "r0"  as '<I><FONT COLOR="#FF0000">r<SUB>0</SUB></FONT></I>';
althtmldef "s0"  as '<I><FONT COLOR="#FF0000">s<SUB>0</SUB></FONT></I>';
althtmldef "t0"  as '<I><FONT COLOR="#FF0000">t<SUB>0</SUB></FONT></I>';
althtmldef "u0"  as '<I><FONT COLOR="#FF0000">u<SUB>0</SUB></FONT></I>';
althtmldef "v0"  as '<I><FONT COLOR="#FF0000">v<SUB>0</SUB></FONT></I>';
althtmldef "w0"  as '<I><FONT COLOR="#FF0000">w<SUB>0</SUB></FONT></I>';
althtmldef "x0"  as '<I><FONT COLOR="#FF0000">x<SUB>0</SUB></FONT></I>';
althtmldef "y0"  as '<I><FONT COLOR="#FF0000">y<SUB>0</SUB></FONT></I>';
althtmldef "z0"  as '<I><FONT COLOR="#FF0000">z<SUB>0</SUB></FONT></I>';
latexdef   "a0"  as "a_0";
latexdef   "b0"  as "b_0";
latexdef   "c0_" as "c_0";
latexdef   "d0"  as "d_0";
latexdef   "e0"  as "e_0";
latexdef   "f0_" as "f_0";
latexdef   "g0"  as "g_0";
latexdef   "h0"  as "h_0";
latexdef   "i0"  as "i_0";
latexdef   "j0"  as "j_0";
latexdef   "k0"  as "k_0";
latexdef   "l0"  as "l_0";
latexdef   "m0"  as "m_0";
latexdef   "n0_" as "n_0";
latexdef   "o0"  as "o_0";
latexdef   "p0"  as "p_0";
latexdef   "q0"  as "q_0";
latexdef   "r0"  as "r_0";
latexdef   "s0"  as "s_0";
latexdef   "t0"  as "t_0";
latexdef   "u0"  as "u_0";
latexdef   "v0"  as "v_0";
latexdef   "w0"  as "w_0";
latexdef   "x0"  as "x_0";
latexdef   "y0"  as "y_0";
latexdef   "z0"  as "z_0";
htmldef    "a1"  as "<IMG SRC='_bnj_a1.gif' WIDTH=14 HEIGHT=19" +
    " ALT='a1' ALIGN=TOP>";
htmldef    "b1"  as "<IMG SRC='_bnj_b1.gif' WIDTH=13 HEIGHT=19" +
    " ALT='b1' ALIGN=TOP>";
htmldef    "c1_" as "<IMG SRC='_bnj_c1.gif' WIDTH=12 HEIGHT=19" +
    " ALT='c1_' ALIGN=TOP>";
htmldef    "d1"  as "<IMG SRC='_bnj_d1.gif' WIDTH=14 HEIGHT=19" +
    " ALT='d1' ALIGN=TOP>";
htmldef    "e1"  as "<IMG SRC='_bnj_e1.gif' WIDTH=13 HEIGHT=19" +
    " ALT='e1' ALIGN=TOP>";
htmldef    "f1"  as "<IMG SRC='_bnj_f1.gif' WIDTH=14 HEIGHT=19" +
    " ALT='f1' ALIGN=TOP>";
htmldef    "g1"  as "<IMG SRC='_bnj_g1.gif' WIDTH=14 HEIGHT=19" +
    " ALT='g1' ALIGN=TOP>";
htmldef    "h1"  as "<IMG SRC='_bnj_h1.gif' WIDTH=15 HEIGHT=19" +
    " ALT='h1' ALIGN=TOP>";
htmldef    "i1"  as "<IMG SRC='_bnj_i1.gif' WIDTH=11 HEIGHT=19" +
    " ALT='i1' ALIGN=TOP>";
htmldef    "j1"  as "<IMG SRC='_bnj_j1.gif' WIDTH=12 HEIGHT=19" +
    " ALT='j1' ALIGN=TOP>";
htmldef    "k1"  as "<IMG SRC='_bnj_k1.gif' WIDTH=14 HEIGHT=19" +
    " ALT='k1' ALIGN=TOP>";
htmldef    "l1"  as "<IMG SRC='_bnj_l1.gif' WIDTH=10 HEIGHT=19" +
    " ALT='l1' ALIGN=TOP>";
htmldef    "m1"  as "<IMG SRC='_bnj_m1.gif' WIDTH=19 HEIGHT=19" +
    " ALT='m1' ALIGN=TOP>";
htmldef    "n1"  as "<IMG SRC='_bnj_n1.gif' WIDTH=15 HEIGHT=19" +
    " ALT='n1' ALIGN=TOP>";
htmldef    "o1"  as "<IMG SRC='_bnj_o1.gif' WIDTH=13 HEIGHT=19" +
    " ALT='o1' ALIGN=TOP>";
htmldef    "p1"  as "<IMG SRC='_bnj_p1.gif' WIDTH=15 HEIGHT=19" +
    " ALT='p1' ALIGN=TOP>";
htmldef    "q1"  as "<IMG SRC='_bnj_q1.gif' WIDTH=13 HEIGHT=19" +
    " ALT='q1' ALIGN=TOP>";
htmldef    "r1"  as "<IMG SRC='_bnj_r1.gif' WIDTH=13 HEIGHT=19" +
    " ALT='r1' ALIGN=TOP>";
htmldef    "s1"  as "<IMG SRC='_bnj_s1.gif' WIDTH=12 HEIGHT=19" +
    " ALT='s1' ALIGN=TOP>";
htmldef    "t1"  as "<IMG SRC='_bnj_t1.gif' WIDTH=12 HEIGHT=19" +
    " ALT='t1' ALIGN=TOP>";
htmldef    "u1"  as "<IMG SRC='_bnj_u1.gif' WIDTH=15 HEIGHT=19" +
    " ALT='u1' ALIGN=TOP>";
htmldef    "v1"  as "<IMG SRC='_bnj_v1.gif' WIDTH=14 HEIGHT=19" +
    " ALT='v1' ALIGN=TOP>";
htmldef    "w1"  as "<IMG SRC='_bnj_w1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='w1' ALIGN=TOP>";
htmldef    "x1"  as "<IMG SRC='_bnj_x1.gif' WIDTH=15 HEIGHT=19" +
    " ALT='x1' ALIGN=TOP>";
htmldef    "y1"  as "<IMG SRC='_bnj_y1.gif' WIDTH=14 HEIGHT=19" +
    " ALT='y1' ALIGN=TOP>";
htmldef    "z1"  as "<IMG SRC='_bnj_z1.gif' WIDTH=14 HEIGHT=19" +
    " ALT='z1' ALIGN=TOP>";
althtmldef "a1"  as '<I><FONT COLOR="#FF0000">a<SUB>1</SUB></FONT></I>';
althtmldef "b1"  as '<I><FONT COLOR="#FF0000">b<SUB>1</SUB></FONT></I>';
althtmldef "c1_" as '<I><FONT COLOR="#FF0000">c<SUB>1</SUB></FONT></I>';
althtmldef "d1"  as '<I><FONT COLOR="#FF0000">d<SUB>1</SUB></FONT></I>';
althtmldef "e1"  as '<I><FONT COLOR="#FF0000">e<SUB>1</SUB></FONT></I>';
althtmldef "f1"  as '<I><FONT COLOR="#FF0000">f<SUB>1</SUB></FONT></I>';
althtmldef "g1"  as '<I><FONT COLOR="#FF0000">g<SUB>1</SUB></FONT></I>';
althtmldef "h1"  as '<I><FONT COLOR="#FF0000">h<SUB>1</SUB></FONT></I>';
althtmldef "i1"  as '<I><FONT COLOR="#FF0000">i<SUB>1</SUB></FONT></I>';
althtmldef "j1"  as '<I><FONT COLOR="#FF0000">j<SUB>1</SUB></FONT></I>';
althtmldef "k1"  as '<I><FONT COLOR="#FF0000">k<SUB>1</SUB></FONT></I>';
althtmldef "l1"  as '<I><FONT COLOR="#FF0000">l<SUB>1</SUB></FONT></I>';
althtmldef "m1"  as '<I><FONT COLOR="#FF0000">m<SUB>1</SUB></FONT></I>';
althtmldef "n1"  as '<I><FONT COLOR="#FF0000">n<SUB>1</SUB></FONT></I>';
althtmldef "o1"  as '<I><FONT COLOR="#FF0000">o<SUB>1</SUB></FONT></I>';
althtmldef "p1"  as '<I><FONT COLOR="#FF0000">p<SUB>1</SUB></FONT></I>';
althtmldef "q1"  as '<I><FONT COLOR="#FF0000">q<SUB>1</SUB></FONT></I>';
althtmldef "r1"  as '<I><FONT COLOR="#FF0000">r<SUB>1</SUB></FONT></I>';
althtmldef "s1"  as '<I><FONT COLOR="#FF0000">s<SUB>1</SUB></FONT></I>';
althtmldef "t1"  as '<I><FONT COLOR="#FF0000">t<SUB>1</SUB></FONT></I>';
althtmldef "u1"  as '<I><FONT COLOR="#FF0000">u<SUB>1</SUB></FONT></I>';
althtmldef "v1"  as '<I><FONT COLOR="#FF0000">v<SUB>1</SUB></FONT></I>';
althtmldef "w1"  as '<I><FONT COLOR="#FF0000">w<SUB>1</SUB></FONT></I>';
althtmldef "x1"  as '<I><FONT COLOR="#FF0000">x<SUB>1</SUB></FONT></I>';
althtmldef "y1"  as '<I><FONT COLOR="#FF0000">y<SUB>1</SUB></FONT></I>';
althtmldef "z1"  as '<I><FONT COLOR="#FF0000">z<SUB>1</SUB></FONT></I>';
latexdef   "a1"  as "a_1";
latexdef   "b1"  as "b_1";
latexdef   "c1_" as "c_1";
latexdef   "d1"  as "d_1";
latexdef   "e1"  as "e_1";
latexdef   "f1"  as "f_1";
latexdef   "g1"  as "g_1";
latexdef   "h1"  as "h_1";
latexdef   "i1"  as "i_1";
latexdef   "j1"  as "j_1";
latexdef   "k1"  as "k_1";
latexdef   "l1"  as "l_1";
latexdef   "m1"  as "m_1";
latexdef   "n1"  as "n_1";
latexdef   "o1"  as "o_1";
latexdef   "p1"  as "p_1";
latexdef   "q1"  as "q_1";
latexdef   "r1"  as "r_1";
latexdef   "s1"  as "s_1";
latexdef   "t1"  as "t_1";
latexdef   "u1"  as "u_1";
latexdef   "v1"  as "v_1";
latexdef   "w1"  as "w_1";
latexdef   "x1"  as "x_1";
latexdef   "y1"  as "y_1";
latexdef   "z1"  as "z_1";
htmldef    "A'" as "<IMG SRC='_bnj_caPrime.gif' WIDTH=15 HEIGHT=19" +
    " ALT=" + '"' + "A'" + '"' + " ALIGN=TOP>";
htmldef    "B'" as "<IMG SRC='_bnj_cbPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "B'" + '"' + " ALIGN=TOP>";
htmldef    "C'" as "<IMG SRC='_bnj_ccPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "C'" + '"' + " ALIGN=TOP>";
htmldef    "D'" as "<IMG SRC='_bnj_cdPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "D'" + '"' + " ALIGN=TOP>";
htmldef    "E'" as "<IMG SRC='_bnj_cePrime.gif' WIDTH=17 HEIGHT=19" +
    " ALT=" + '"' + "E'" + '"' + " ALIGN=TOP>";
htmldef    "F'" as "<IMG SRC='_bnj_cfPrime.gif' WIDTH=17 HEIGHT=19" +
    " ALT=" + '"' + "F'" + '"' + " ALIGN=TOP>";
htmldef    "G'" as "<IMG SRC='_bnj_cgPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "G'" + '"' + " ALIGN=TOP>";
htmldef    "H'" as "<IMG SRC='_bnj_chPrime.gif' WIDTH=18 HEIGHT=19" +
    " ALT=" + '"' + "H'" + '"' + " ALIGN=TOP>";
htmldef    "I'" as "<IMG SRC='_bnj_ciPrime.gif' WIDTH=12 HEIGHT=19" +
    " ALT=" + '"' + "I'" + '"' + " ALIGN=TOP>";
htmldef    "J'" as "<IMG SRC='_bnj_cjPrime.gif' WIDTH=14 HEIGHT=19" +
    " ALT=" + '"' + "J'" + '"' + " ALIGN=TOP>";
htmldef    "K'" as "<IMG SRC='_bnj_ckPrime.gif' WIDTH=18 HEIGHT=19" +
    " ALT=" + '"' + "K'" + '"' + " ALIGN=TOP>";
htmldef    "L'" as "<IMG SRC='_bnj_clPrime.gif' WIDTH=14 HEIGHT=19" +
    " ALT=" + '"' + "L'" + '"' + " ALIGN=TOP>";
htmldef    "M'" as "<IMG SRC='_bnj_cmPrime.gif' WIDTH=19 HEIGHT=19" +
    " ALT=" + '"' + "M'" + '"' + " ALIGN=TOP>";
htmldef    "N'" as "<IMG SRC='_bnj_cnPrime.gif' WIDTH=18 HEIGHT=19" +
    " ALT=" + '"' + "N'" + '"' + " ALIGN=TOP>";
htmldef    "O'" as "<IMG SRC='_bnj_coPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "O'" + '"' + " ALIGN=TOP>";
htmldef    "P'" as "<IMG SRC='_bnj_cpPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "P'" + '"' + " ALIGN=TOP>";
htmldef    "Q'" as "<IMG SRC='_bnj_cqPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "Q'" + '"' + " ALIGN=TOP>";
htmldef    "R'" as "<IMG SRC='_bnj_crPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "R'" + '"' + " ALIGN=TOP>";
htmldef    "S'" as "<IMG SRC='_bnj_csPrime.gif' WIDTH=15 HEIGHT=19" +
    " ALT=" + '"' + "S'" + '"' + " ALIGN=TOP>";
htmldef    "T'" as "<IMG SRC='_bnj_ctPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "T'" + '"' + " ALIGN=TOP>";
htmldef    "U'" as "<IMG SRC='_bnj_cuPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "U'" + '"' + " ALIGN=TOP>";
htmldef    "V'" as "<IMG SRC='_bnj_cvPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "V'" + '"' + " ALIGN=TOP>";
htmldef    "W'" as "<IMG SRC='_bnj_cwPrime.gif' WIDTH=20 HEIGHT=19" +
    " ALT=" + '"' + "W'" + '"' + " ALIGN=TOP>";
htmldef    "X'" as "<IMG SRC='_bnj_cxPrime.gif' WIDTH=17 HEIGHT=19" +
    " ALT=" + '"' + "X'" + '"' + " ALIGN=TOP>";
htmldef    "Y'" as "<IMG SRC='_bnj_cyPrime.gif' WIDTH=16 HEIGHT=19" +
    " ALT=" + '"' + "Y'" + '"' + " ALIGN=TOP>";
htmldef    "Z'" as "<IMG SRC='_bnj_czPrime.gif' WIDTH=15 HEIGHT=19" +
    " ALT=" + '"' + "Z'" + '"' + " ALIGN=TOP>";
althtmldef "A'" as '<I><FONT COLOR="#CC33CC">A&prime;</FONT></I>';
althtmldef "B'" as '<I><FONT COLOR="#CC33CC">B&prime;</FONT></I>';
althtmldef "C'" as '<I><FONT COLOR="#CC33CC">C&prime;</FONT></I>';
althtmldef "D'" as '<I><FONT COLOR="#CC33CC">D&prime;</FONT></I>';
althtmldef "E'" as '<I><FONT COLOR="#CC33CC">E&prime;</FONT></I>';
althtmldef "F'" as '<I><FONT COLOR="#CC33CC">F&prime;</FONT></I>';
althtmldef "G'" as '<I><FONT COLOR="#CC33CC">G&prime;</FONT></I>';
althtmldef "H'" as '<I><FONT COLOR="#CC33CC">H&prime;</FONT></I>';
althtmldef "I'" as '<I><FONT COLOR="#CC33CC">I&prime;</FONT></I>';
althtmldef "J'" as '<I><FONT COLOR="#CC33CC">J&prime;</FONT></I>';
althtmldef "K'" as '<I><FONT COLOR="#CC33CC">K&prime;</FONT></I>';
althtmldef "L'" as '<I><FONT COLOR="#CC33CC">L&prime;</FONT></I>';
althtmldef "M'" as '<I><FONT COLOR="#CC33CC">M&prime;</FONT></I>';
althtmldef "N'" as '<I><FONT COLOR="#CC33CC">N&prime;</FONT></I>';
althtmldef "O'" as '<I><FONT COLOR="#CC33CC">O&prime;</FONT></I>';
althtmldef "P'" as '<I><FONT COLOR="#CC33CC">P&prime;</FONT></I>';
althtmldef "Q'" as '<I><FONT COLOR="#CC33CC">Q&prime;</FONT></I>';
althtmldef "R'" as '<I><FONT COLOR="#CC33CC">R&prime;</FONT></I>';
althtmldef "S'" as '<I><FONT COLOR="#CC33CC">S&prime;</FONT></I>';
althtmldef "T'" as '<I><FONT COLOR="#CC33CC">T&prime;</FONT></I>';
althtmldef "U'" as '<I><FONT COLOR="#CC33CC">U&prime;</FONT></I>';
althtmldef "V'" as '<I><FONT COLOR="#CC33CC">V&prime;</FONT></I>';
althtmldef "W'" as '<I><FONT COLOR="#CC33CC">W&prime;</FONT></I>';
althtmldef "X'" as '<I><FONT COLOR="#CC33CC">X&prime;</FONT></I>';
althtmldef "Y'" as '<I><FONT COLOR="#CC33CC">Y&prime;</FONT></I>';
althtmldef "Z'" as '<I><FONT COLOR="#CC33CC">Z&prime;</FONT></I>';
latexdef   "A'" as "A'";
latexdef   "B'" as "B'";
latexdef   "C'" as "C'";
latexdef   "D'" as "D'";
latexdef   "E'" as "E'";
latexdef   "F'" as "F'";
latexdef   "G'" as "G'";
latexdef   "H'" as "H'";
latexdef   "I'" as "I'";
latexdef   "J'" as "J'";
latexdef   "K'" as "K'";
latexdef   "L'" as "L'";
latexdef   "M'" as "M'";
latexdef   "N'" as "N'";
latexdef   "O'" as "O'";
latexdef   "P'" as "P'";
latexdef   "Q'" as "Q'";
latexdef   "R'" as "R'";
latexdef   "S'" as "S'";
latexdef   "T'" as "T'";
latexdef   "U'" as "U'";
latexdef   "V'" as "V'";
latexdef   "W'" as "W'";
latexdef   "X'" as "X'";
latexdef   "Y'" as "Y'";
latexdef   "Z'" as "Z'";
htmldef    'A"' as "<IMG SRC='_bnj_caPrimePrime.gif' WIDTH=18 HEIGHT=19 " +
      " ALT='A" + '"' + "'ALIGN=TOP>";
htmldef    'B"' as "<IMG SRC='_bnj_cbPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='B" + '"' + "'ALIGN=TOP>";
htmldef    'C"' as "<IMG SRC='_bnj_ccPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='C" + '"' + "'ALIGN=TOP>";
htmldef    'D"' as "<IMG SRC='_bnj_cdPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='D" + '"' + "'ALIGN=TOP>";
htmldef    'E"' as "<IMG SRC='_bnj_cePrimePrime.gif' WIDTH=20 HEIGHT=19 " +
      " ALT='E" + '"' + "'ALIGN=TOP>";
htmldef    'F"' as "<IMG SRC='_bnj_cfPrimePrime.gif' WIDTH=20 HEIGHT=19 " +
      " ALT='F" + '"' + "'ALIGN=TOP>";
htmldef    'G"' as "<IMG SRC='_bnj_cgPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='G" + '"' + "'ALIGN=TOP>";
htmldef    'H"' as "<IMG SRC='_bnj_chPrimePrime.gif' WIDTH=21 HEIGHT=19 " +
      " ALT='H" + '"' + "'ALIGN=TOP>";
htmldef    'I"' as "<IMG SRC='_bnj_ciPrimePrime.gif' WIDTH=15 HEIGHT=19 " +
      " ALT='I" + '"' + "'ALIGN=TOP>";
htmldef    'J"' as "<IMG SRC='_bnj_cjPrimePrime.gif' WIDTH=17 HEIGHT=19 " +
      " ALT='J" + '"' + "'ALIGN=TOP>";
htmldef    'K"' as "<IMG SRC='_bnj_ckPrimePrime.gif' WIDTH=21 HEIGHT=19 " +
      " ALT='K" + '"' + "'ALIGN=TOP>";
htmldef    'L"' as "<IMG SRC='_bnj_clPrimePrime.gif' WIDTH=17 HEIGHT=19 " +
      " ALT='L" + '"' + "'ALIGN=TOP>";
htmldef    'M"' as "<IMG SRC='_bnj_cmPrimePrime.gif' WIDTH=22 HEIGHT=19 " +
      " ALT='M" + '"' + "'ALIGN=TOP>";
htmldef    'N"' as "<IMG SRC='_bnj_cnPrimePrime.gif' WIDTH=21 HEIGHT=19 " +
      " ALT='N" + '"' + "'ALIGN=TOP>";
htmldef    'O"' as "<IMG SRC='_bnj_coPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='O" + '"' + "'ALIGN=TOP>";
htmldef    'P"' as "<IMG SRC='_bnj_cpPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='P" + '"' + "'ALIGN=TOP>";
htmldef    'Q"' as "<IMG SRC='_bnj_cqPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='Q" + '"' + "'ALIGN=TOP>";
htmldef    'R"' as "<IMG SRC='_bnj_crPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='R" + '"' + "'ALIGN=TOP>";
htmldef    'S"' as "<IMG SRC='_bnj_csPrimePrime.gif' WIDTH=18 HEIGHT=19 " +
      " ALT='S" + '"' + "'ALIGN=TOP>";
htmldef    'T"' as "<IMG SRC='_bnj_ctPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='T" + '"' + "'ALIGN=TOP>";
htmldef    'U"' as "<IMG SRC='_bnj_cuPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='U" + '"' + "'ALIGN=TOP>";
htmldef    'V"' as "<IMG SRC='_bnj_cvPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='V" + '"' + "'ALIGN=TOP>";
htmldef    'W"' as "<IMG SRC='_bnj_cwPrimePrime.gif' WIDTH=23 HEIGHT=19 " +
      " ALT='W" + '"' + "'ALIGN=TOP>";
htmldef    'X"' as "<IMG SRC='_bnj_cxPrimePrime.gif' WIDTH=20 HEIGHT=19 " +
      " ALT='X" + '"' + "'ALIGN=TOP>";
htmldef    'Y"' as "<IMG SRC='_bnj_cyPrimePrime.gif' WIDTH=19 HEIGHT=19 " +
      " ALT='Y" + '"' + "'ALIGN=TOP>";
htmldef    'Z"' as "<IMG SRC='_bnj_czPrimePrime.gif' WIDTH=18 HEIGHT=19 " +
      " ALT='Z" + '"' + "'ALIGN=TOP>";
althtmldef 'A"' as '<I><FONT COLOR="#CC33CC">A&Prime;</FONT></I>';
althtmldef 'B"' as '<I><FONT COLOR="#CC33CC">B&Prime;</FONT></I>';
althtmldef 'C"' as '<I><FONT COLOR="#CC33CC">C&Prime;</FONT></I>';
althtmldef 'D"' as '<I><FONT COLOR="#CC33CC">D&Prime;</FONT></I>';
althtmldef 'E"' as '<I><FONT COLOR="#CC33CC">E&Prime;</FONT></I>';
althtmldef 'F"' as '<I><FONT COLOR="#CC33CC">F&Prime;</FONT></I>';
althtmldef 'G"' as '<I><FONT COLOR="#CC33CC">G&Prime;</FONT></I>';
althtmldef 'H"' as '<I><FONT COLOR="#CC33CC">H&Prime;</FONT></I>';
althtmldef 'I"' as '<I><FONT COLOR="#CC33CC">I&Prime;</FONT></I>';
althtmldef 'J"' as '<I><FONT COLOR="#CC33CC">J&Prime;</FONT></I>';
althtmldef 'K"' as '<I><FONT COLOR="#CC33CC">K&Prime;</FONT></I>';
althtmldef 'L"' as '<I><FONT COLOR="#CC33CC">L&Prime;</FONT></I>';
althtmldef 'M"' as '<I><FONT COLOR="#CC33CC">M&Prime;</FONT></I>';
althtmldef 'N"' as '<I><FONT COLOR="#CC33CC">N&Prime;</FONT></I>';
althtmldef 'O"' as '<I><FONT COLOR="#CC33CC">O&Prime;</FONT></I>';
althtmldef 'P"' as '<I><FONT COLOR="#CC33CC">P&Prime;</FONT></I>';
althtmldef 'Q"' as '<I><FONT COLOR="#CC33CC">Q&Prime;</FONT></I>';
althtmldef 'R"' as '<I><FONT COLOR="#CC33CC">R&Prime;</FONT></I>';
althtmldef 'S"' as '<I><FONT COLOR="#CC33CC">S&Prime;</FONT></I>';
althtmldef 'T"' as '<I><FONT COLOR="#CC33CC">T&Prime;</FONT></I>';
althtmldef 'U"' as '<I><FONT COLOR="#CC33CC">U&Prime;</FONT></I>';
althtmldef 'V"' as '<I><FONT COLOR="#CC33CC">V&Prime;</FONT></I>';
althtmldef 'W"' as '<I><FONT COLOR="#CC33CC">W&Prime;</FONT></I>';
althtmldef 'X"' as '<I><FONT COLOR="#CC33CC">X&Prime;</FONT></I>';
althtmldef 'Y"' as '<I><FONT COLOR="#CC33CC">Y&Prime;</FONT></I>';
althtmldef 'Z"' as '<I><FONT COLOR="#CC33CC">Z&Prime;</FONT></I>';
latexdef   'A"' as "A''";
latexdef   'B"' as "B''";
latexdef   'C"' as "C''";
latexdef   'D"' as "D''";
latexdef   'E"' as "E''";
latexdef   'F"' as "F''";
latexdef   'G"' as "G''";
latexdef   'H"' as "H''";
latexdef   'I"' as "I''";
latexdef   'J"' as "J''";
latexdef   'K"' as "K''";
latexdef   'L"' as "L''";
latexdef   'M"' as "M''";
latexdef   'N"' as "N''";
latexdef   'O"' as "O''";
latexdef   'P"' as "P''";
latexdef   'Q"' as "Q''";
latexdef   'R"' as "R''";
latexdef   'S"' as "S''";
latexdef   'T"' as "T''";
latexdef   'U"' as "U''";
latexdef   'V"' as "V''";
latexdef   'W"' as "W''";
latexdef   'X"' as "X''";
latexdef   'Y"' as "Y''";
latexdef   'Z"' as "Z''";
htmldef    "A0" as "<IMG SRC='_bnj_ca0.gif' WIDTH=18 HEIGHT=19" +
    " ALT='A0' ALIGN=TOP>";
htmldef    "B0" as "<IMG SRC='_bnj_cb0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='B0' ALIGN=TOP>";
htmldef    "C0" as "<IMG SRC='_bnj_cc0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='C0' ALIGN=TOP>";
htmldef    "D0" as "<IMG SRC='_bnj_cd0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='D0' ALIGN=TOP>";
htmldef    "E0" as "<IMG SRC='_bnj_ce0.gif' WIDTH=20 HEIGHT=19" +
    " ALT='E0' ALIGN=TOP>";
htmldef    "F0" as "<IMG SRC='_bnj_cf0.gif' WIDTH=20 HEIGHT=19" +
    " ALT='F0' ALIGN=TOP>";
htmldef    "G0" as "<IMG SRC='_bnj_cg0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='G0' ALIGN=TOP>";
htmldef    "H0" as "<IMG SRC='_bnj_ch0.gif' WIDTH=21 HEIGHT=19" +
    " ALT='H0' ALIGN=TOP>";
htmldef    "I0" as "<IMG SRC='_bnj_ci0.gif' WIDTH=15 HEIGHT=19" +
    " ALT='I0' ALIGN=TOP>";
htmldef    "J0" as "<IMG SRC='_bnj_cj0.gif' WIDTH=17 HEIGHT=19" +
    " ALT='J0' ALIGN=TOP>";
htmldef    "K0" as "<IMG SRC='_bnj_ck0.gif' WIDTH=21 HEIGHT=19" +
    " ALT='K0' ALIGN=TOP>";
htmldef    "L0" as "<IMG SRC='_bnj_cl0.gif' WIDTH=17 HEIGHT=19" +
    " ALT='L0' ALIGN=TOP>";
htmldef    "M0" as "<IMG SRC='_bnj_cm0.gif' WIDTH=22 HEIGHT=19" +
    " ALT='M0' ALIGN=TOP>";
htmldef    "N0" as "<IMG SRC='_bnj_cn0.gif' WIDTH=21 HEIGHT=19" +
    " ALT='N0' ALIGN=TOP>";
htmldef    "O0" as "<IMG SRC='_bnj_co0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='O0' ALIGN=TOP>";
htmldef    "P0" as "<IMG SRC='_bnj_cp0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='P0' ALIGN=TOP>";
htmldef    "Q0" as "<IMG SRC='_bnj_cq0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='Q0' ALIGN=TOP>";
htmldef    "R0" as "<IMG SRC='_bnj_cr0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='R0' ALIGN=TOP>";
htmldef    "S0" as "<IMG SRC='_bnj_cs0.gif' WIDTH=18 HEIGHT=19" +
    " ALT='S0' ALIGN=TOP>";
htmldef    "T0" as "<IMG SRC='_bnj_ct0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='T0' ALIGN=TOP>";
htmldef    "U0" as "<IMG SRC='_bnj_cu0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='U0' ALIGN=TOP>";
htmldef    "V0" as "<IMG SRC='_bnj_cv0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='V0' ALIGN=TOP>";
htmldef    "W0" as "<IMG SRC='_bnj_cw0.gif' WIDTH=23 HEIGHT=19" +
    " ALT='W0' ALIGN=TOP>";
htmldef    "X0" as "<IMG SRC='_bnj_cx0.gif' WIDTH=20 HEIGHT=19" +
    " ALT='X0' ALIGN=TOP>";
htmldef    "Y0" as "<IMG SRC='_bnj_cy0.gif' WIDTH=19 HEIGHT=19" +
    " ALT='Y0' ALIGN=TOP>";
htmldef    "Z0" as "<IMG SRC='_bnj_cz0.gif' WIDTH=18 HEIGHT=19" +
    " ALT='Z0' ALIGN=TOP>";
althtmldef "A0" as '<I><FONT COLOR="#CC33CC">A<SUB>0</SUB></FONT></I>';
althtmldef "B0" as '<I><FONT COLOR="#CC33CC">B<SUB>0</SUB></FONT></I>';
althtmldef "C0" as '<I><FONT COLOR="#CC33CC">C<SUB>0</SUB></FONT></I>';
althtmldef "D0" as '<I><FONT COLOR="#CC33CC">D<SUB>0</SUB></FONT></I>';
althtmldef "E0" as '<I><FONT COLOR="#CC33CC">E<SUB>0</SUB></FONT></I>';
althtmldef "F0" as '<I><FONT COLOR="#CC33CC">F<SUB>0</SUB></FONT></I>';
althtmldef "G0" as '<I><FONT COLOR="#CC33CC">G<SUB>0</SUB></FONT></I>';
althtmldef "H0" as '<I><FONT COLOR="#CC33CC">H<SUB>0</SUB></FONT></I>';
althtmldef "I0" as '<I><FONT COLOR="#CC33CC">I<SUB>0</SUB></FONT></I>';
althtmldef "J0" as '<I><FONT COLOR="#CC33CC">J<SUB>0</SUB></FONT></I>';
althtmldef "K0" as '<I><FONT COLOR="#CC33CC">K<SUB>0</SUB></FONT></I>';
althtmldef "L0" as '<I><FONT COLOR="#CC33CC">L<SUB>0</SUB></FONT></I>';
althtmldef "M0" as '<I><FONT COLOR="#CC33CC">M<SUB>0</SUB></FONT></I>';
althtmldef "N0" as '<I><FONT COLOR="#CC33CC">N<SUB>0</SUB></FONT></I>';
althtmldef "O0" as '<I><FONT COLOR="#CC33CC">O<SUB>0</SUB></FONT></I>';
althtmldef "P0" as '<I><FONT COLOR="#CC33CC">P<SUB>0</SUB></FONT></I>';
althtmldef "Q0" as '<I><FONT COLOR="#CC33CC">Q<SUB>0</SUB></FONT></I>';
althtmldef "R0" as '<I><FONT COLOR="#CC33CC">R<SUB>0</SUB></FONT></I>';
althtmldef "S0" as '<I><FONT COLOR="#CC33CC">S<SUB>0</SUB></FONT></I>';
althtmldef "T0" as '<I><FONT COLOR="#CC33CC">T<SUB>0</SUB></FONT></I>';
althtmldef "U0" as '<I><FONT COLOR="#CC33CC">U<SUB>0</SUB></FONT></I>';
althtmldef "V0" as '<I><FONT COLOR="#CC33CC">V<SUB>0</SUB></FONT></I>';
althtmldef "W0" as '<I><FONT COLOR="#CC33CC">W<SUB>0</SUB></FONT></I>';
althtmldef "X0" as '<I><FONT COLOR="#CC33CC">X<SUB>0</SUB></FONT></I>';
althtmldef "Y0" as '<I><FONT COLOR="#CC33CC">Y<SUB>0</SUB></FONT></I>';
althtmldef "Z0" as '<I><FONT COLOR="#CC33CC">Z<SUB>0</SUB></FONT></I>';
latexdef   "A0" as "A_0";
latexdef   "B0" as "B_0";
latexdef   "C0" as "C_0";
latexdef   "D0" as "D_0";
latexdef   "E0" as "E_0";
latexdef   "F0" as "F_0";
latexdef   "G0" as "G_0";
latexdef   "H0" as "H_0";
latexdef   "I0" as "I_0";
latexdef   "J0" as "J_0";
latexdef   "K0" as "K_0";
latexdef   "L0" as "L_0";
latexdef   "M0" as "M_0";
latexdef   "N0" as "N_0";
latexdef   "O0" as "O_0";
latexdef   "P0" as "P_0";
latexdef   "Q0" as "Q_0";
latexdef   "R0" as "R_0";
latexdef   "S0" as "S_0";
latexdef   "T0" as "T_0";
latexdef   "U0" as "U_0";
latexdef   "V0" as "V_0";
latexdef   "W0" as "W_0";
latexdef   "X0" as "X_0";
latexdef   "Y0" as "Y_0";
latexdef   "Z0" as "Z_0";
htmldef    "A1_"  as "<IMG SRC='_bnj_ca1.gif' WIDTH=16 HEIGHT=19" +
    " ALT='A1' ALIGN=TOP>";
htmldef    "B1_"  as "<IMG SRC='_bnj_cb1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='B1' ALIGN=TOP>";
htmldef    "C1_"  as "<IMG SRC='_bnj_cc1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='C1' ALIGN=TOP>";
htmldef    "D1_"  as "<IMG SRC='_bnj_cd1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='D1' ALIGN=TOP>";
htmldef    "E1"  as "<IMG SRC='_bnj_ce1.gif' WIDTH=18 HEIGHT=19" +
    " ALT='E1' ALIGN=TOP>";
htmldef    "F1_"  as "<IMG SRC='_bnj_cf1.gif' WIDTH=18 HEIGHT=19" +
    " ALT='F1' ALIGN=TOP>";
htmldef    "G1_"  as "<IMG SRC='_bnj_cg1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='G1' ALIGN=TOP>";
htmldef    "H1_"  as "<IMG SRC='_bnj_ch1.gif' WIDTH=19 HEIGHT=19" +
    " ALT='H1' ALIGN=TOP>";
htmldef    "I1_"  as "<IMG SRC='_bnj_ci1.gif' WIDTH=13 HEIGHT=19" +
    " ALT='I1' ALIGN=TOP>";
htmldef    "J1"  as "<IMG SRC='_bnj_cj1.gif' WIDTH=15 HEIGHT=19" +
    " ALT='J1' ALIGN=TOP>";
htmldef    "K1"  as "<IMG SRC='_bnj_ck1.gif' WIDTH=19 HEIGHT=19" +
    " ALT='K1' ALIGN=TOP>";
htmldef    "L1_"  as "<IMG SRC='_bnj_cl1.gif' WIDTH=15 HEIGHT=19" +
    " ALT='L1' ALIGN=TOP>";
htmldef    "M1_"  as "<IMG SRC='_bnj_cm1.gif' WIDTH=20 HEIGHT=19" +
    " ALT='M1' ALIGN=TOP>";
htmldef    "N1"  as "<IMG SRC='_bnj_cn1.gif' WIDTH=19 HEIGHT=19" +
    " ALT='N1' ALIGN=TOP>";
htmldef    "O1_"  as "<IMG SRC='_bnj_co1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='O1' ALIGN=TOP>";
htmldef    "P1"  as "<IMG SRC='_bnj_cp1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='P1' ALIGN=TOP>";
htmldef    "Q1"  as "<IMG SRC='_bnj_cq1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='Q1' ALIGN=TOP>";
htmldef    "R1_" as "<IMG SRC='_bnj_cr1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='R1_' ALIGN=TOP>";
htmldef    "S1_"  as "<IMG SRC='_bnj_cs1.gif' WIDTH=16 HEIGHT=19" +
    " ALT='S1' ALIGN=TOP>";
htmldef    "T1"  as "<IMG SRC='_bnj_ct1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='T1' ALIGN=TOP>";
htmldef    "U1"  as "<IMG SRC='_bnj_cu1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='U1' ALIGN=TOP>";
htmldef    "V1_"  as "<IMG SRC='_bnj_cv1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='V1' ALIGN=TOP>";
htmldef    "W1"  as "<IMG SRC='_bnj_cw1.gif' WIDTH=21 HEIGHT=19" +
    " ALT='W1' ALIGN=TOP>";
htmldef    "X1"  as "<IMG SRC='_bnj_cx1.gif' WIDTH=18 HEIGHT=19" +
    " ALT='X1' ALIGN=TOP>";
htmldef    "Y1"  as "<IMG SRC='_bnj_cy1.gif' WIDTH=17 HEIGHT=19" +
    " ALT='Y1' ALIGN=TOP>";
htmldef    "Z1"  as "<IMG SRC='_bnj_cz1.gif' WIDTH=16 HEIGHT=19" +
    " ALT='Z1' ALIGN=TOP>";
althtmldef "A1_"  as '<I><FONT COLOR="#CC33CC">A<SUB>1</SUB></FONT></I>';
althtmldef "B1_"  as '<I><FONT COLOR="#CC33CC">B<SUB>1</SUB></FONT></I>';
althtmldef "C1_"  as '<I><FONT COLOR="#CC33CC">C<SUB>1</SUB></FONT></I>';
althtmldef "D1_"  as '<I><FONT COLOR="#CC33CC">D<SUB>1</SUB></FONT></I>';
althtmldef "E1"  as '<I><FONT COLOR="#CC33CC">E<SUB>1</SUB></FONT></I>';
althtmldef "F1_"  as '<I><FONT COLOR="#CC33CC">F<SUB>1</SUB></FONT></I>';
althtmldef "G1_"  as '<I><FONT COLOR="#CC33CC">G<SUB>1</SUB></FONT></I>';
althtmldef "H1_"  as '<I><FONT COLOR="#CC33CC">H<SUB>1</SUB></FONT></I>';
althtmldef "I1_"  as '<I><FONT COLOR="#CC33CC">I<SUB>1</SUB></FONT></I>';
althtmldef "J1"  as '<I><FONT COLOR="#CC33CC">J<SUB>1</SUB></FONT></I>';
althtmldef "K1"  as '<I><FONT COLOR="#CC33CC">K<SUB>1</SUB></FONT></I>';
althtmldef "L1_"  as '<I><FONT COLOR="#CC33CC">L<SUB>1</SUB></FONT></I>';
althtmldef "M1_"  as '<I><FONT COLOR="#CC33CC">M<SUB>1</SUB></FONT></I>';
althtmldef "N1"  as '<I><FONT COLOR="#CC33CC">N<SUB>1</SUB></FONT></I>';
althtmldef "O1_"  as '<I><FONT COLOR="#CC33CC">O<SUB>1</SUB></FONT></I>';
althtmldef "P1"  as '<I><FONT COLOR="#CC33CC">P<SUB>1</SUB></FONT></I>';
althtmldef "Q1"  as '<I><FONT COLOR="#CC33CC">Q<SUB>1</SUB></FONT></I>';
althtmldef "R1_" as '<I><FONT COLOR="#CC33CC">R<SUB>1</SUB></FONT></I>';
althtmldef "S1_"  as '<I><FONT COLOR="#CC33CC">S<SUB>1</SUB></FONT></I>';
althtmldef "T1"  as '<I><FONT COLOR="#CC33CC">T<SUB>1</SUB></FONT></I>';
althtmldef "U1"  as '<I><FONT COLOR="#CC33CC">U<SUB>1</SUB></FONT></I>';
althtmldef "V1_"  as '<I><FONT COLOR="#CC33CC">V<SUB>1</SUB></FONT></I>';
althtmldef "W1"  as '<I><FONT COLOR="#CC33CC">W<SUB>1</SUB></FONT></I>';
althtmldef "X1"  as '<I><FONT COLOR="#CC33CC">X<SUB>1</SUB></FONT></I>';
althtmldef "Y1"  as '<I><FONT COLOR="#CC33CC">Y<SUB>1</SUB></FONT></I>';
althtmldef "Z1"  as '<I><FONT COLOR="#CC33CC">Z<SUB>1</SUB></FONT></I>';
latexdef   "A1_"  as "A_1";
latexdef   "B1_"  as "B_1";
latexdef   "C1_"  as "C_1";
latexdef   "D1_"  as "D_1";
latexdef   "E1"  as "E_1";
latexdef   "F1_"  as "F_1";
latexdef   "G1_"  as "G_1";
latexdef   "H1_"  as "H_1";
latexdef   "I1_"  as "I_1";
latexdef   "J1"  as "J_1";
latexdef   "K1"  as "K_1";
latexdef   "L1_"  as "L_1";
latexdef   "M1_"  as "M_1";
latexdef   "N1"  as "N_1";
latexdef   "O1_"  as "O_1";
latexdef   "P1"  as "P_1";
latexdef   "Q1"  as "Q_1";
latexdef   "R1_" as "R_1";
latexdef   "S1_"  as "S_1";
latexdef   "T1"  as "T_1";
latexdef   "U1"  as "U_1";
latexdef   "V1_"  as "V_1";
latexdef   "W1"  as "W_1";
latexdef   "X1"  as "X_1";
latexdef   "Y1"  as "Y_1";
latexdef   "Z1"  as "Z_1";
htmldef "_fns" as " fns ";
  althtmldef "_fns" as " fns ";
  latexdef "_fns" as "{\rm fns\;}";
htmldef "_pred" as " pred";
  althtmldef "_pred" as " pred";
  latexdef "_pred" as "{\rm pred}";
htmldef "_Se" as " Se ";
  althtmldef "_Se" as " Se ";
  latexdef "_Se" as "{\rm \;Se\;}";
htmldef "_FrSe" as " FrSe ";
  althtmldef "_FrSe" as " FrSe ";
  latexdef "_FrSe" as "{\rm \;FrSe\;}";
htmldef "_trCl" as " trCl";
  althtmldef "_trCl" as " trCl";
  latexdef "_trCl" as "{\rm trCl}";
htmldef "_TrFo" as " TrFo";
  althtmldef "_TrFo" as " TrFo";
  latexdef "_TrFo" as "{\rm TrFo}";
/* End of Jonathan Ben-Naim's mathbox */

/* Mathbox of Paul Chapman */
htmldef "||" as "<IMG SRC='parallel.gif' WIDTH=5 HEIGHT=19 ALT='||'" +
    " ALIGN=TOP>";
  althtmldef "||" as ' &#8741; ';
  latexdef "||" as "\parallel";
htmldef "gcd" as " <IMG SRC='_gcd.gif' WIDTH=23 HEIGHT=19 ALT='gcd'" +
    " ALIGN=TOP> ";
  althtmldef "gcd" as " gcd ";
  latexdef "gcd" as "\,{\rm gcd}\,";
htmldef "#" as "#";
  althtmldef "#" as "#";
  latexdef "#" as "#";
htmldef "Prime" as "Prime";
  althtmldef "Prime" as "Prime";
  latexdef "Prime" as "{\rm Prime}";

/* Mathbox of Scott Fenton */
htmldef "Pred" as "Pred";
  althtmldef "Pred" as "Pred";
  latexdef "Pred" as "{\rm Pred}";
htmldef "Trcl" as "Trcl";
  althtmldef "Trcl" as "Trcl";
  latexdef "Trcl" as "{\rm Trcl}";
htmldef "No" as '<FONT FACE=sans-serif> No </FONT>';
  althtmldef "No" as '<FONT FACE=sans-serif> No </FONT>';
  latexdef "No" as "{\rm No}";
htmldef "bday" as '<FONT FACE=sans-serif> bday </FONT>';
  althtmldef "bday" as '<FONT FACE=sans-serif> bday </FONT>';
  latexdef "bday" as "{\rm bday}";
htmldef "<s" as " &lt;s ";
  althtmldef "<s" as " &lt;s ";
  latexdef "<s" as "{<_s}";

/* Mathbox of Jeff Hankins */
htmldef "1stc" as "<IMG SRC='_1stc.gif' WIDTH=25 HEIGHT=19 ALT='1stc'" +
    " ALIGN=TOP>";
  althtmldef "1stc" as "1<SUP>st</SUP>&omega;";
  latexdef "1stc" as "1^{\rm st}\omega";
htmldef "2ndc" as "<IMG SRC='_2ndc.gif' WIDTH=31 HEIGHT=19 ALT='2ndc'" +
    " ALIGN=TOP>";
  althtmldef "2ndc" as "2<SUP>nd</SUP>&omega;";
  latexdef "2ndc" as "2^{\rm nd}\omega";
htmldef "Ref" as "Ref";
  althtmldef "Ref" as "Ref";
  latexdef "Ref" as "{\rm Ref}";
htmldef "UFil" as "UFil";
  althtmldef "UFil" as "UFil";
  latexdef "UFil" as "{\rm UFil}";
htmldef "Dir" as "Dir";
  althtmldef "Dir" as "Dir";
  latexdef "Dir" as "{\rm Dir}";
htmldef "tail" as "tail";
  althtmldef "tail" as "tail";
  latexdef "tail" as "{\rm tail}";
htmldef "Fne" as "Fne";
  althtmldef "Fne" as "Fne";
  latexdef "Fne" as "{\rm Fne}";
htmldef "PtFin" as "PtFin";
  althtmldef "PtFin" as "PtFin";
  latexdef "PtFin" as "{\rm PtFin}";
htmldef "LocFin" as "LocFin";
  althtmldef "LocFin" as "LocFin";
  latexdef "LocFin" as "{\rm LocFin}";
htmldef "Kol2" as "Kol2";
  althtmldef "Kol2" as "Kol2";
  latexdef "Kol2" as "{\rm Kol2}";
htmldef "Reg" as "Reg";
  althtmldef "Reg" as "Reg";
  latexdef "Reg" as "{\rm Reg}";
htmldef "Nrm" as "Nrm";
  althtmldef "Nrm" as "Nrm";
  latexdef "Nrm" as "{\rm Nrm}";


/* Mathbox of Anthony Hart */
htmldef "F." as " <IMG SRC='perp.gif' WIDTH=11 HEIGHT=19 ALT='F.' ALIGN=TOP> ";
  althtmldef "F." as ' &perp; ';
  latexdef "F." as "\bot";


/* Mathbox of Jeff Hoffman */
htmldef "gcdOLD" as "gcdOLD";
  althtmldef "gcdOLD" as "gcdOLD";
  latexdef "gcdOLD" as "{\rm gcdOLD}";


/* Mathbox of FL */
htmldef "A1" as '<I><FONT COLOR="#CC33CC">A<SUB>1</SUB></FONT></I>';
  althtmldef "A1" as '<I><FONT COLOR="#CC33CC">A<SUB>1</SUB></FONT></I>';
  latexdef "A1" as "A_1";
htmldef "A2" as '<I><FONT COLOR="#CC33CC">A<SUB>2</SUB></FONT></I>';
  althtmldef "A2" as '<I><FONT COLOR="#CC33CC">A<SUB>2</SUB></FONT></I>';
  latexdef "A2" as "A_2";
htmldef "B1" as '<I><FONT COLOR="#CC33CC">B<SUB>1</SUB></FONT></I>';
  althtmldef "B1" as '<I><FONT COLOR="#CC33CC">B<SUB>1</SUB></FONT></I>';
  latexdef "B1" as "B_1";
htmldef "B2" as '<I><FONT COLOR="#CC33CC">B<SUB>2</SUB></FONT></I>';
  althtmldef "B2" as '<I><FONT COLOR="#CC33CC">B<SUB>2</SUB></FONT></I>';
  latexdef "B2" as "B_2";
htmldef "C1" as '<I><FONT COLOR="#CC33CC">C<SUB>1</SUB></FONT></I>';
  althtmldef "C1" as '<I><FONT COLOR="#CC33CC">C<SUB>1</SUB></FONT></I>';
  latexdef "C1" as "C_1";
htmldef "C2" as '<I><FONT COLOR="#CC33CC">C<SUB>2</SUB></FONT></I>';
  althtmldef "C2" as '<I><FONT COLOR="#CC33CC">C<SUB>2</SUB></FONT></I>';
  latexdef "C2" as "C_2";
htmldef "D1" as '<I><FONT COLOR="#CC33CC">D<SUB>1</SUB></FONT></I>';
  althtmldef "D1" as '<I><FONT COLOR="#CC33CC">D<SUB>1</SUB></FONT></I>';
  latexdef "D1" as "D_1";
htmldef "D2" as '<I><FONT COLOR="#CC33CC">D<SUB>2</SUB></FONT></I>';
  althtmldef "D2" as '<I><FONT COLOR="#CC33CC">D<SUB>2</SUB></FONT></I>';
  latexdef "D2" as "D_2";
htmldef "F1" as '<I><FONT COLOR="#CC33CC">F<SUB>1</SUB></FONT></I>';
  althtmldef "F1" as '<I><FONT COLOR="#CC33CC">F<SUB>1</SUB></FONT></I>';
  latexdef "F1" as "F_1";
htmldef "F2" as '<I><FONT COLOR="#CC33CC">F<SUB>2</SUB></FONT></I>';
  althtmldef "F2" as '<I><FONT COLOR="#CC33CC">F<SUB>2</SUB></FONT></I>';
  latexdef "F2" as "F_2";
htmldef "G1" as '<I><FONT COLOR="#CC33CC">G<SUB>1</SUB></FONT></I>';
  althtmldef "G1" as '<I><FONT COLOR="#CC33CC">G<SUB>1</SUB></FONT></I>';
  latexdef "G1" as "G_1";
htmldef "G2" as '<I><FONT COLOR="#CC33CC">G<SUB>2</SUB></FONT></I>';
  althtmldef "G2" as '<I><FONT COLOR="#CC33CC">G<SUB>2</SUB></FONT></I>';
  latexdef "G2" as "G_2";
htmldef "H1" as '<I><FONT COLOR="#CC33CC">H<SUB>1</SUB></FONT></I>';
  althtmldef "H1" as '<I><FONT COLOR="#CC33CC">H<SUB>1</SUB></FONT></I>';
  latexdef "H1" as "H_1";
htmldef "H2" as '<I><FONT COLOR="#CC33CC">H<SUB>2</SUB></FONT></I>';
  althtmldef "H2" as '<I><FONT COLOR="#CC33CC">H<SUB>2</SUB></FONT></I>';
  latexdef "H2" as "H_2";
htmldef "I1" as '<I><FONT COLOR="#CC33CC">I<SUB>1</SUB></FONT></I>';
  althtmldef "I1" as '<I><FONT COLOR="#CC33CC">I<SUB>1</SUB></FONT></I>';
  latexdef "I1" as "I_1";
htmldef "I2" as '<I><FONT COLOR="#CC33CC">I<SUB>2</SUB></FONT></I>';
  althtmldef "I2" as '<I><FONT COLOR="#CC33CC">I<SUB>2</SUB></FONT></I>';
  latexdef "I2" as "I_2";
htmldef "L1" as '<I><FONT COLOR="#CC33CC">L<SUB>1</SUB></FONT></I>';
  althtmldef "L1" as '<I><FONT COLOR="#CC33CC">L<SUB>1</SUB></FONT></I>';
  latexdef "L1" as "L_1";
htmldef "L2" as '<I><FONT COLOR="#CC33CC">L<SUB>2</SUB></FONT></I>';
  althtmldef "L2" as '<I><FONT COLOR="#CC33CC">L<SUB>2</SUB></FONT></I>';
  latexdef "L2" as "L_2";
htmldef "M1" as '<I><FONT COLOR="#CC33CC">M<SUB>1</SUB></FONT></I>';
  althtmldef "M1" as '<I><FONT COLOR="#CC33CC">M<SUB>1</SUB></FONT></I>';
  latexdef "M1" as "M_1";
htmldef "M2" as '<I><FONT COLOR="#CC33CC">M<SUB>2</SUB></FONT></I>';
  althtmldef "M2" as '<I><FONT COLOR="#CC33CC">M<SUB>2</SUB></FONT></I>';
  latexdef "M2" as "M_2";
htmldef "O1" as '<I><FONT COLOR="#CC33CC">O<SUB>1</SUB></FONT></I>';
  althtmldef "O1" as '<I><FONT COLOR="#CC33CC">O<SUB>1</SUB></FONT></I>';
  latexdef "O1" as "O_1";
htmldef "O2" as '<I><FONT COLOR="#CC33CC">O<SUB>2</SUB></FONT></I>';
  althtmldef "O2" as '<I><FONT COLOR="#CC33CC">O<SUB>2</SUB></FONT></I>';
  latexdef "O2" as "O_2";
htmldef "Ro1" as '<I><FONT COLOR="#CC33CC">Ro<SUB>1</SUB></FONT></I>';
  althtmldef "Ro1" as '<I><FONT COLOR="#CC33CC">Ro<SUB>1</SUB></FONT></I>';
  latexdef "Ro1" as "Ro_1";
htmldef "Ro2" as '<I><FONT COLOR="#CC33CC">Ro<SUB>2</SUB></FONT></I>';
  althtmldef "Ro2" as '<I><FONT COLOR="#CC33CC">Ro<SUB>2</SUB></FONT></I>';
  latexdef "Ro2" as "Ro_2";
htmldef "S1" as '<I><FONT COLOR="#CC33CC">S<SUB>1</SUB></FONT></I>';
  althtmldef "S1" as '<I><FONT COLOR="#CC33CC">S<SUB>1</SUB></FONT></I>';
  latexdef "S1" as "S_1";
htmldef "S2" as '<I><FONT COLOR="#CC33CC">S<SUB>2</SUB></FONT></I>';
  althtmldef "S2" as '<I><FONT COLOR="#CC33CC">S<SUB>2</SUB></FONT></I>';
  latexdef "S2" as "S_2";
htmldef "V1" as '<I><FONT COLOR="#CC33CC">V<SUB>1</SUB></FONT></I>';
  althtmldef "V1" as '<I><FONT COLOR="#CC33CC">V<SUB>1</SUB></FONT></I>';
  latexdef "V1" as "V_1";
htmldef "V2" as '<I><FONT COLOR="#CC33CC">V<SUB>2</SUB></FONT></I>';
  althtmldef "V2" as '<I><FONT COLOR="#CC33CC">V<SUB>2</SUB></FONT></I>';
  latexdef "V2" as "V_2";
htmldef "V3" as '<I><FONT COLOR="#CC33CC">V<SUB>3</SUB></FONT></I>';
  althtmldef "V3" as '<I><FONT COLOR="#CC33CC">V<SUB>2</SUB></FONT></I>';
  latexdef "V3" as "V_3";

htmldef "+t" as '<I><FONT COLOR="#CC33CC">+<SUB>t</SUB> </FONT></I>';
  althtmldef "+t" as '<I><FONT COLOR="#CC33CC">+<SUB>t</SUB> </FONT></I>';
  latexdef "+t" as "+_t";
htmldef "-t" as '<I><FONT COLOR="#CC33CC">-<SUB>t</SUB> </FONT></I>';
  althtmldef "-t" as '<I><FONT COLOR="#CC33CC">-<SUB>t</SUB> </FONT></I>';
  latexdef "-t" as "-_t";
htmldef "~t" as '<I><FONT COLOR="#CC33CC">~<SUB>t</SUB> </FONT></I>';
  althtmldef "~t" as '<I><FONT COLOR="#CC33CC">~<SUB>t</SUB> </FONT></I>';
  latexdef "~t" as "~_t";
htmldef "0t" as '<I><FONT COLOR="#CC33CC">0<SUB>t</SUB> </FONT></I>';
  althtmldef "0t" as '<I><FONT COLOR="#CC33CC">0<SUB>t</SUB> </FONT></I>';
  latexdef "0t" as "0_t";
htmldef "1t" as '<I><FONT COLOR="#CC33CC">1<SUB>t</SUB> </FONT></I>';
  althtmldef "1t" as '<I><FONT COLOR="#CC33CC">1<SUB>t</SUB> </FONT></I>';
  latexdef "1t" as "1_t";
htmldef ".t" as '<I><FONT COLOR="#CC33CC">.<SUB>t</SUB> </FONT></I>';
  althtmldef ".t" as '<I><FONT COLOR="#CC33CC">.<SUB>t</SUB> </FONT></I>';
  latexdef ".t" as "._t";
htmldef "+w" as '<I><FONT COLOR="#CC33CC">+<SUB>w</SUB> </FONT></I>';
  althtmldef "+w" as '<I><FONT COLOR="#CC33CC">+<SUB>w</SUB> </FONT></I>';
  latexdef "+w" as "+_w";
htmldef "-w" as '<I><FONT COLOR="#CC33CC">-<SUB>w</SUB> </FONT></I>';
  althtmldef "-w" as '<I><FONT COLOR="#CC33CC">-<SUB>w</SUB> </FONT></I>';
  latexdef "-w" as "-_w";
htmldef "0w" as '<I><FONT COLOR="#CC33CC">0<SUB>w</SUB> </FONT></I>';
  althtmldef "0w" as '<I><FONT COLOR="#CC33CC">0<SUB>w</SUB> </FONT></I>';
  latexdef "0w" as "0_w";
htmldef "~w" as '<I><FONT COLOR="#CC33CC">~<SUB>w</SUB> </FONT></I>';
  althtmldef "~w" as '<I><FONT COLOR="#CC33CC">~<SUB>w</SUB> </FONT></I>';
  latexdef "~w" as "~_w";
htmldef ".w" as '<I><FONT COLOR="#CC33CC">.<SUB>w</SUB> </FONT></I>';
  althtmldef ".w" as '<I><FONT COLOR="#CC33CC">.<SUB>w</SUB> </FONT></I>';
  latexdef ".w" as "._w";

htmldef "RVec" as " RVec ";
  althtmldef "RVec" as " RVec ";
  latexdef "RVec" as "{\rm RVec}";
htmldef "RAffSp" as " RAffSp ";
  althtmldef "RAffSp" as " RAffSp ";
  latexdef "RAffSp" as "{\rm RAffSp}";
htmldef "Toset" as " Toset ";
  althtmldef "Toset" as " Toset ";
  latexdef "Toset" as "{\rm Toset}";
htmldef "fi" as " fi ";
  althtmldef "fi" as " fi ";
  latexdef "fi" as "{\rm fi}";
htmldef "Alg" as " Alg ";
  althtmldef "Alg" as " Alg ";
  latexdef "Alg" as "{\rm Alg}";
htmldef "Cat" as " Cat ";
  althtmldef "Cat" as " Cat ";
  latexdef "Cat" as "{\rm Cat}";
htmldef "dom_" as "<U>dom</U>";
  althtmldef "dom_" as "<U>dom</U>";
  latexdef "dom_" as "\underline{\rm dom}";
htmldef "cod_" as "<U>cod</U>";
  althtmldef "cod_" as "<U>cod</U>";
  latexdef "cod_" as "\underline{\rm cod}";
htmldef "id_" as "<U>id</U>";
  althtmldef "id_" as "<U>id</U>";
  latexdef "id_" as "\underline{\rm id}";
htmldef "o_" as "<U>o</U>";
  althtmldef "o_" as "<U>o</U>";
  latexdef "o_" as "\underline{\rm o}";
htmldef "Epi" as " Epi ";
  althtmldef "Epi" as " Epi ";
  latexdef "Epi" as "{\rm Epi}";
htmldef "Monic" as " Monic ";
  althtmldef "Monic" as " Monic ";
  latexdef "Monic" as "{\rm Monic}";
htmldef "Iso" as " Iso ";
  althtmldef "Iso" as " Iso ";
  latexdef "Iso" as "{\rm Iso}";
htmldef "Ded" as " Ded ";
  althtmldef "Ded" as " Ded ";
  latexdef "Ded" as "{\rm Ded}";
htmldef "hom" as " hom ";
  althtmldef "hom" as " hom ";
  latexdef "hom" as "{\rm hom}";
htmldef "Dgra" as " Dgra ";
  althtmldef "Dgra" as " Dgra ";
  latexdef "Dgra" as "{\rm Dgra}";
htmldef "Func" as " Func ";
  althtmldef "Func" as " Func ";
  latexdef "Func" as "{\rm Func}";
htmldef "cinv" as " cinv ";
  althtmldef "cinv" as " cinv ";
  latexdef "cinv" as "{\rm cinv}";
htmldef "Isofunc" as " Isofunc ";
  althtmldef "Isofunc" as " Isofunc ";
  latexdef "Isofunc" as "{\rm Isofunc}";
htmldef "Tofld" as " Tofld ";
  althtmldef "Tofld" as " Tofld ";
  latexdef "Tofld" as "{\rm Tofld}";
htmldef "zeroDiv" as " zeroDiv ";
  althtmldef "zeroDiv" as " zeroDiv ";
  latexdef "zeroDiv" as "{\rm zeroDiv}";
htmldef "Tarski" as " Tarski ";
  althtmldef "Tarski" as " Tarski ";
  latexdef "Tarski" as "{\rm Tarski}";
htmldef "cur1" as "cur1";
  althtmldef "cur1" as "cur1";
  latexdef "cur1" as "{\rm cur1}";
htmldef "cur2" as "cur2";
  althtmldef "cur2" as "cur2";
  latexdef "cur2" as "{\rm cur2}";
htmldef "Plibg" as "Plibg";
  althtmldef "Plibg" as "Plibg";
  latexdef "Plibg" as "{\rm Plibg}";
htmldef "Plibg0" as "Plibg0";
  althtmldef "Plibg0" as "Plibg0";
  latexdef "Plibg0" as "{\rm Plibg0}";
htmldef "Plibg1" as "Plibg1";
  althtmldef "Plibg1" as "Plibg1";
  latexdef "Plibg1" as "{\rm Plibg1}";
htmldef "Plibg2" as "Plibg2";
  althtmldef "Plibg2" as "Plibg2";
  latexdef "Plibg2" as "{\rm Plibg2}";
htmldef "Plibg3" as "Plibg3";
  althtmldef "Plibg3" as "Plibg3";
  latexdef "Plibg3" as "{\rm Plibg3}";
htmldef "Plibg4a" as "Plibg4a";
  althtmldef "Plibg4a" as "Plibg4a";
  latexdef "Plibg4a" as "{\rm Plibg4a}";
htmldef "Plibg4b" as "Plibg4b";
  althtmldef "Plibg4b" as "Plibg4b";
  latexdef "Plibg4b" as "{\rm Plibg4b}";
htmldef "Com1" as "Com1";
  althtmldef "Com1" as "Com1";
  latexdef "Com1" as "{\rm Com1}";
htmldef "mxl" as "mxl";
  althtmldef "mxl" as "mxl";
  latexdef "mxl" as "{\rm mxl}";
htmldef "ge" as "ge";
  althtmldef "ge" as "ge";
  latexdef "ge" as "{\rm ge}";
htmldef "Preset" as " Preset ";
  althtmldef "Preset" as " Preset ";
  latexdef "Preset" as "{\rm Preset}";
htmldef "Vec" as "Vec";
  althtmldef "Vec" as "Vec";
  latexdef "Vec" as "{\rm Vec}";
htmldef "+m" as "+m";
  althtmldef "+m" as "+m";
  latexdef "+m" as "+_{m}";
htmldef "xm" as "xm";
  althtmldef "xm" as "xm";
  latexdef "xm" as "x_{m}";
htmldef ".m" as ".m";
  althtmldef ".m" as ".m";
  latexdef ".m" as "._{m}";
htmldef "SubCat" as " SubCat ";
  althtmldef "SubCat" as " SubCat ";
  latexdef "SubCat" as "{\rm SubCat}";
htmldef "t+" as "t+";
  althtmldef "t+" as "t+";
  latexdef "t+" as "{\rm t}+";
htmldef "t*" as "t*";
  althtmldef "t*" as "t*";
  latexdef "t*" as "{\rm t}*";
htmldef "pr" as "pr";
  althtmldef "pr" as "pr";
  latexdef "pr" as "{\rm pr}";
htmldef "prj" as "pr";
  althtmldef "prj" as "prj";
  latexdef "prj" as "{\rm prj}";
htmldef "LatAlg" as "LatAlg";
  althtmldef "LatAlg" as "LatAlg";
  latexdef "LatAlg" as "{\rm LatAlg}";
htmldef "^md" as "^md";
  althtmldef "^md" as "^md";
  latexdef "^md" as " md ";
htmldef "prod_" as "<IMG SRC='cpi.gif' WIDTH=11 HEIGHT=19 ALT=" +
    "'prod_' ALIGN=TOP>";
  althtmldef "prod_" as "prod_";
  latexdef "prod_" as "\Pi";
htmldef "cset" as "cset";
  althtmldef "cset" as "cset";
  latexdef "cset" as "{\rm cset}";
htmldef "TopGrp" as "TopGrp";
  althtmldef "TopGrp" as "TopGrp";
  latexdef "TopGrp" as "{\rm TopGrp}";
htmldef "(+)" as
          "<IMG SRC='oplus.gif' WIDTH=13 HEIGHT=19 ALT='(+)' ALIGN=TOP>";
  althtmldef "(+)" as "(+)";
  latexdef "(+)" as "\oplus";
htmldef "SubVec" as "SubVec";
  althtmldef "SubVec" as "SubVec";
  latexdef "SubVec" as "{\rm SubVec}";
htmldef "tarskiMap" as "tarskiMap";
  althtmldef "tarskiMap" as "tarskiMap";
  latexdef "tarskiMap" as "{\rm tarskiMap}";
htmldef "Uni" as "Uni";
  althtmldef "Uni" as "Uni";
  latexdef "Uni" as "{\rm Uni}";
htmldef "[.]" as
          "<IMG SRC='wbox.gif' WIDTH=11 HEIGHT=19 ALT='[.]' ALIGN=TOP>";
  althtmldef "[.]" as "[.]";
  latexdef "[.]" as "\Box";
htmldef "<>" as
          "<IMG SRC='wdiamond.gif' WIDTH=13 HEIGHT=19 ALT='<>' ALIGN=TOP>";
  althtmldef "<>" as "<>";
  latexdef "<>" as "\Diamond";
htmldef "()" as
          "<IMG SRC='circ.gif' WIDTH=8 HEIGHT=19 ALT='()' ALIGN=TOP>";
  althtmldef "()" as "()";
  latexdef "()" as "\bigcirc";
htmldef "str" as " str ";
  althtmldef "str" as " str ";
  latexdef "str" as "{\rm str}";
htmldef "until" as "until";
  althtmldef "until" as "until";
  latexdef "until" as "{\rm until}";
htmldef "tar" as "tar";
  althtmldef "tar" as "tar";
  latexdef "tar" as "{\rm tar}";
htmldef "Free" as "Free";
  althtmldef "Free" as "Free";
  latexdef "Free" as "{\rm Free}";
htmldef "Str" as "Str";
  althtmldef "Str" as "Str";
  latexdef "Str" as "{\rm Str}";
htmldef "Strx" as "Strx";
  althtmldef "Strx" as "Strx";
  latexdef "Strx" as "{\rm Strx}";
htmldef "len" as "len";
  althtmldef "len" as "len";
  latexdef "len" as "{\rm len}";
htmldef "conc" as "conc";
  althtmldef "conc" as "conc";
  latexdef "conc" as "{\rm conc}";
htmldef "tr" as "tr";
  althtmldef "tr" as "tr";
  latexdef "tr" as "{\rm tr}";
htmldef "seg" as "seg";
  althtmldef "seg" as "seg";
  latexdef "seg" as "{\rm seg}";
htmldef "line" as "line";
  althtmldef "line" as "line";
  latexdef "line" as "{\rm line}";
htmldef "ub" as " ub ";
  althtmldef "ub" as " ub ";
  latexdef "ub" as "{\rm ub}";
/*
htmldef "filr" as " filr ";
  althtmldef "filr" as " filr ";
  latexdef "filr" as "{\rm filr}";
*/
/* End of mathbox of FL */


/* Mathbox of Jeff Madsen */
htmldef "II" as "<IMG SRC='bbi.gif' WIDTH=7 HEIGHT=19 ALT='II' ALIGN=TOP>";
  althtmldef "II" as "II";
  latexdef "II" as "{\rm II}";
htmldef "~~>t" as "<IMG SRC='rightsquigarrow.gif' WIDTH=15 HEIGHT=19 ALT=" +
    "'~~>' ALIGN=TOP><IMG SRC='subt.gif' WIDTH=5 HEIGHT=19 ALT='t' ALIGN=TOP>";
  althtmldef "~~>t" as "<FONT FACE=sans-serif>&#8669;</FONT>t";
  latexdef "~~>t" as "\rightsquigarrow_t}";
htmldef "TotBnd" as "TotBnd";
  althtmldef "TotBnd" as "TotBnd";
  latexdef "TotBnd" as "{\rm TotBnd}";
htmldef "Bnd" as "Bnd";
  althtmldef "Bnd" as "Bnd";
  latexdef "Bnd" as "{\rm Bnd}";
htmldef "Ismty" as "Ismty";
  althtmldef "Ismty" as "Ismty";
  latexdef "Ismty" as "{\rm Ismty}";
htmldef "Rn" as "<IMG SRC='bbr.gif' WIDTH=13 HEIGHT=19 ALT='RR' ALIGN=TOP>" +
    "<IMG SRC='supn.gif' WIDTH=6 HEIGHT=19 ALT='n' ALIGN=TOP>";
  althtmldef "Rn" as "<FONT FACE=sans-serif>&#8477;</FONT><I><SUP>n</SUP></I>";
  latexdef "Rn" as "\mathbb{R}^n";
htmldef "PHtpy" as "PHtpy";
  althtmldef "PHtpy" as "PHtpy";
  latexdef "PHtpy" as "{\rm PHtpy}";
htmldef "~=ph" as "<IMG SRC='simeq.gif' WIDTH=13 HEIGHT=19 ALT='~='" +
    "ALIGN=TOP><IMG SRC='subp.gif' WIDTH=7 HEIGHT=19 ALT='p' ALIGN=TOP>" +
    "<IMG SRC='subh.gif' WIDTH=6 HEIGHT=19 ALT='h' ALIGN=TOP>";
  althtmldef "~=ph" as "~=ph";
  latexdef "~=ph" as "\simeq_{ph}";
htmldef "pi1b" as "<IMG SRC='pi.gif' WIDTH=10 HEIGHT=19 ALT='pi' ALIGN=TOP>" +
    "<IMG SRC='sub1.gif' WIDTH=4 HEIGHT=19 ALT='1' ALIGN=TOP>" +
    "<IMG SRC='subb.gif' WIDTH=6 HEIGHT=19 ALT='b' ALIGN=TOP>";
  althtmldef "pi1b" as "<I>&pi;<SUB>1b</SUB></I>";
  latexdef "pi1b" as "\pi_{1b}";
htmldef "*p" as "<IMG SRC='ast.gif' WIDTH=7 HEIGHT=19 ALT='&#42;' ALIGN=TOP>" +
    "<IMG SRC='subp.gif' WIDTH=7 HEIGHT=19 ALT='p' ALIGN=TOP>";
  althtmldef "*p" as "&#42;<I><SUB>p</SUB></I>";
  latexdef "*p" as "*_p";
htmldef "pi1" as "<IMG SRC='pi.gif' WIDTH=10 HEIGHT=19 ALT='pi' ALIGN=TOP>" +
    "<IMG SRC='sub1.gif' WIDTH=4 HEIGHT=19 ALT='1' ALIGN=TOP>";
  althtmldef "pi1" as "<I>&pi;<SUB>1</SUB></I>";
  latexdef "pi1" as "\pi_1";
htmldef "RngHom" as " RngHom ";
  althtmldef "RngHom" as " RngHom ";
  latexdef "RngHom" as "{\rm RngHom}";
htmldef "RngIso" as " RngIso ";
  althtmldef "RngIso" as " RngIso ";
  latexdef "RngIso" as "{\rm RngIso}";
htmldef "~=r" as "<IMG SRC='simeq.gif' WIDTH=13 HEIGHT=19 ALT='~='" +
    "ALIGN=TOP><IMG SRC='subr.gif' WIDTH=5 HEIGHT=19 ALT='r' ALIGN=TOP>";
  althtmldef "~=r" as "~=<SUB>r</SUB>";
  latexdef "~=r" as "\simeq_r";
htmldef "CRing" as "CRing";
  althtmldef "CRing" as "CRing";
  latexdef "CRing" as "{\rm CRing}";
htmldef "Idl" as "Idl";
  althtmldef "Idl" as "Idl";
  latexdef "Idl" as "{\rm Idl}";
htmldef "PrIdl" as "PrIdl";
  althtmldef "PrIdl" as "PrIdl";
  latexdef "PrIdl" as "{\rm PrIdl}";
htmldef "MaxIdl" as "MaxIdl";
  althtmldef "MaxIdl" as "MaxIdl";
  latexdef "MaxIdl" as "{\rm MaxIdl}";
htmldef "PrRing" as "PrRing";
  althtmldef "PrRing" as "PrRing";
  latexdef "PrRing" as "{\rm PrRing}";
htmldef "Dmn" as "Dmn";
  althtmldef "Dmn" as "Dmn";
  latexdef "Dmn" as "{\rm Dmn}";
htmldef "IdlGen" as " IdlGen ";
  althtmldef "IdlGen" as " IdlGen ";
  latexdef "IdlGen" as "{\rm IdlGen}";


/* Mathbox of Rodolfo Medina */
htmldef "Prt" as "Prt ";
  althtmldef "Prt" as "Prt ";
  latexdef "Prt" as "{\rm Prt}";


/* Mathbox of Steve Rodriguez */
htmldef "HypGrph" as "HypGrph";
  althtmldef "HypGrph" as "HypGrph";
  latexdef "HypGrph" as "{\rm HypGrph}";
htmldef "PsGrph" as "PsGrph";
  althtmldef "PsGrph" as "PsGrph";
  latexdef "PsGrph" as "{\rm PsGrph}";
htmldef "SmpGrph" as "SmpGrph";
  althtmldef "SmpGrph" as "SmpGrph";
  latexdef "SmpGrph" as "{\rm SmpGrph}";


/* Mathbox of Alan Sare */
htmldef "->.." as ' &#9658 ';
  althtmldef "->.." as ' &#9658; ';
  latexdef "->.." as "\Longrightarrow";
htmldef "->..." as ' , ';
  althtmldef "->..." as ' , ';
  latexdef "->..." as " , ";
htmldef "[." as ' . ';
  althtmldef "[." as ' . ';
  latexdef "[." as ".";
htmldef "(." as ' . ';
  althtmldef "(." as ' . ';
  latexdef "(." as ".";
htmldef "]." as ' . ';
  althtmldef "]." as ' . ';
  latexdef "]." as ".";
htmldef ")." as ' . ';
  althtmldef ")." as ' . ';
  latexdef ")." as ".";
htmldef "." as ' . ';
  althtmldef "." as ' . ';
  latexdef "." as ".";
htmldef "la" as '<FONT COLOR="#0000FF"><I>&lambda;</I></FONT>';
  althtmldef "la" as '<FONT COLOR="#0000FF"><I>&lambda;</I></FONT>';
  latexdef "la" as "\lambda";
htmldef "ka" as '<FONT COLOR="#0000FF"><I>&kappa;</I></FONT>';
  althtmldef "ka" as '<FONT COLOR="#0000FF"><I>&kappa;</I></FONT>';
  latexdef "ka" as "\kappa";
htmldef "mu" as "<IMG SRC='_mu.gif' WIDTH=10 HEIGHT=19 ALT='mu' ALIGN=TOP>";
  althtmldef "mu" as '<FONT COLOR="#0000FF"><I>&mu;</I></FONT>';
  latexdef "mu" as "\mu";


/* End of typesetting definition section */
$)
 
