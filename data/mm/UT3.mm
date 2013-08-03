$(
set.mm - Version of 26-Jan-2006
cloned for UT3.mm for mmj2 unit test 3, 21-Mar-2006
$)
  $c ( $.
  $c ) $.
  $c -> $.
  $c -. $.
  $c wff $.
  $c |- $. 
  $v ph $. 
  $v ps $. 
  $v ch $. 
  $v th $.
  $v ta $.
  wph $f wff ph $.
  wps $f wff ps $.
  wch $f wff ch $.
  wth $f wff th $.
  wta $f wff ta $.
  wn $a wff -. ph $.
  wi $a wff ( ph -> ps ) $.
  ax-1 $a |- ( ph -> ( ps -> ph ) ) $.
  ax-2 $a |- ( ( ph -> ( ps -> ch ) ) -> ( ( ph -> ps ) -> ( ph -> ch ) ) ) $.
  ax-3 $a |- ( ( -. ph -> -. ps ) -> ( ps -> ph ) ) $.
  ${
    min $e |- ph $.
    maj $e |- ( ph -> ps ) $.
    ax-mp $a |- ps $.
  $}

  ${
    UT3001.1 $e |- ph $.
    UT3001 $p |- ( ps -> ph ) $=
      ( ) $.
    $( E-IO-0201 Compressed proof must have at least one block of 
       compressed data!  $)
  $}

  ${
    UT3002.1 $e |- ph $.
    UT3002 $p |- ( ps -> ph ) $=
      ( ) ? $.
    $( OK, this is a valid proof entry
    $)
  $}

  ${
    UT3003.1 $e |- ph $.
    UT3003 $p |- ( ps -> ph ) $=
      ( ) A $.
    $( OK, this is a valid proof entry, but it doesn't prove the theorem.
    $)
  $}

  ${
    UT3004.1 $e |- ph $.
    UT3004 $p |- ( ps -> ph ) $=
      ( wth ) D $.
    $( OK, this is a valid proof entry, but it doesn't prove the theorem.
       'D' refers to wth because there are 3 mandatory hyps preceding wth.
    $)

  $}

  ${
    UT3005.1 $e |- ph $.
    UT3005 $p |- ( ps -> ph ) $=
      ( RRRRRRRRRRRRRRRRRRRRRRRRRRR ) D $.
    $( E-LA-0101 Theorem : compressed proof contains invalid 
       statement label, not found in Statement Table. Label
       position within the compressed proof's parentheses = ...
       . Statement label = ... ";
    $)
  $}

  ${
    UT3006.1 $e |- ph $.
    UT3006 $p |- ( ps -> ph ) $=
      ( min maj ) D $.
    $( E-LA-0102 Theorem : compressed proof contains invalid 
       statement label in parenthesized portion of proof. The
       referenced statement is neither a VarHyp nor an Assrt
       type statement! Label position within the compressed
       proof's parentheses = .... Statement label = ...
    $)
  $}

  ${
    UT3007.1 $e |- ph $.
    UT3007 $p |- ( ps -> ph ) $=
      ( ax-1 wth ) D $.
    $( E-LA-0103 Theorem: compressed proof contains invalid 
       statement label in parenthesized portion of proof. The
       referenced statement is a VarHyp that occurs *after*
       one or more Assrt labels within the parentheses. Label
       position within the compressed proof's parentheses = ....
       Statement label = ...
    $)
  $}

  ${
    UT3008.1 $e |- ph $.
    UT3008 $p |- ( ps -> ph ) $=
      ( ax-1 wth ) $.
    $( E-LA-0104 Theorem : compressed proof contains no 
       compressed proof blocks following the parentheses! 
       Proof is empty?!
    $)
  $}

  ${
    UT3009.1 $e |- ph $.
    UT3009 $p |- ( ps -> ph ) $=
      ( $.
    $( ok, no right paren. bad boy.
       E-IO-0024 Invalid character in proof step. Token read = $. 
       Source Id: ... Line: 115 Column: 10
    $)
  $}

  ${
    UT3010.1 $e |- ph $.
    UT3010 $p |- ( ps -> ph ) $=
      ( wph wps wch wth wta 
        wph wps wch wth wta 
        wph wps wch wth wta
        wph wps wch wth wta
        wph wps wch wth wta
        wi ax-1 ax-mp ) A B A D C A B E F U $.
    $( E-LA-0105 Theorem: Premature end of proof. Final
       compressed proof block reached prior to end of
       compressed step number
    $)
  $}

  ${
    UT3011.1 $e |- ph $.
    UT3011 $p |- ( ps -> ph ) $=
      ( wch wth wta ax-1 ax-2 ax-3 wi wn  ) AZBZ*CZDZEZFZGZHZIZJZKZL $.
      $( E-LA-0107 Theorem UT3011: Compressed proof block 1, 
         character position 5 contains an invalid character. 
         Compressed proof block may contain only 'A', 'B'...'Z' 
         and '?'.
      $)
  $}

  ${
    UT3012.1 $e |- ph $.
    UT3012 $p |- ( ps -> ph ) $=
      ( wi ax-1 ax-mp ) A B A D C A B E F $.
      $( this one should work! $)
  $}

  ${
    UT3013.1 $e |- ph $.
    UT3013 $p |- ( ps -> ph ) $=
      ( wi ax-1 ax-mp ) U?UABADCABEF $.
      $( E-LA-0108 Theorem : Compressed proof block ..., 
         character position ... contains a '?' inside a
         compressed proof number (for example: 'U?' or 'U?U').
      $)
  $}

  ${
    UT3014.1 $e |- ph $.
    UT3014 $p |- ( ps -> ph ) $=
      ( wi ax-1 ax-mp ) U?ABADCABEF $.
      $( E-LA-0108 Theorem : Compressed proof block ..., 
         character position ... contains a '?' inside a
         compressed proof number (for example: 'U?' or 'U?U').
      $)
  $}

  ${
    UT3015.1 $e |- ph $.
    UT3015 $p |- ( ps -> ph ) $=
      ( wi ax-1 ax-mp ) UZUABADCABEF $.
      $( E-LA-0109 Theorem : Compressed proof block ...,
         character position ... contains a 'Z' (Repeated
         Subproof symbol) inside a compressed proof number
         (for example: 'UZ' or 'UZU').
      $)
  $}
 
  ${
    UT3016.1 $e |- ph $.
    UT3016 $p |- ( ps -> ph ) $=
      ( wi ax-1 ax-mp ) UZABADCABEF $.
      $( E-LA-0109 Theorem : Compressed proof block ...,
         character position ... contains a 'Z' (Repeated
         Subproof symbol) inside a compressed proof number
         (for example: 'UZ' or 'UZU').
      $)
  $}

  ${
    UT3017.1 $e |- ph $.
    UT3017 $p |- ( ps -> ph ) $=
      ( ) Z $.
      $( E-LA-0110 Theorem UT3017: Compressed proof block 1, 
         character position 1 contains a 'Z' (Repeated Subproof 
         symbol) at an invalid location, such as after another 
         'Z' or following a '?' 
      $)
  $}

  ${
    UT3018.1 $e |- ph $.
    UT3018 $p |- ( ps -> ph ) $=
      ( ) AZ $.
  $}

  ${
    UT3019.1 $e |- ph $.
    UT3019 $p |- ( ps -> ph ) $=
      ( ) AZA $.
  $}

  ${
    UT3020.1 $e |- ph $.
    UT3020 $p |- ( ps -> ph ) $=
      ( ) AZZ $.
  $}

  ${
    UT3021.1 $e |- ph $.
    UT3021 $p |- ( ps -> ph ) $=
      ( wth ) E $.
      $( E-LA-0111 Theorem UT3021: Compressed proof block 1, 
         character position 1 is invalid: comressed number 
         that points beyond the end of the Repeated Subproof 
      $)
  $} 

  ${
    UT3022.1 $e |- ph $.
    UT3022 $p |- ( ps -> ph ) $=
      ( wch wth wta ax-1 ax-2 ax-3 wi wn  ) GZHZIZJZKZL $.
      $( E-LA-0112 Theorem : Compressed proof block ...,
         character position is invalid: It appears that the
         comressed proof is corrupted -- subproof length points 
         outside the range of proof steps! The problem may be
         the result Of a handcoded compressed proof, or a bug
         in the proof compression logic that occurred at some
         earlier time. At any rate, this proof is bogus and 
         cannot be processed further!
      $)
  $}
 
  ${
    $( Premise for ~ a1i . $)
    a1i.1 $e |- ph $.
    $( Inference derived from axiom ~ ax-1 .  See ~ a1d for an explanation of
       our informal use of the terms "inference" and "deduction". $)
    a1i $p |- ( ps -> ph ) $=
      ( wi ax-1 ax-mp ) ABADCABEF $.
      $( [5-Aug-93] $)
  $}
