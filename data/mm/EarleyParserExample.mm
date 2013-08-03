$( mmEarleyParserExample $)
$c |- wff set class     $.
$c ( <-> = ) -. -> A. [ ] / $.

$v ph ps x y A B F    $.

wph $f  wff ph        $.
wps $f  wff ps        $.
vx  $f  set x         $.
vy  $f  set y         $.
cA  $f  class A       $.
cB  $f  class B       $.
cF  $f  class F       $.

wb         $a  wff ( ph <-> ps )       $.
wceq       $a  wff A = B               $.
cv         $a  class x                 $.
example    $p  |- ( ph <-> x = A )     $= ? $.

wn         $a  wff -. ph               $.
wi         $a  wff ( ph -> ps )        $.
example2   $p  |- ( -. -. ph -> ps )   $= ? $.
example3   $p  |- ( wff -> -. -. wff ) $= ? $.
example4   $p  |- ( ps -> -. -. ph  )  $= ? $.

wal           $a wff A. x ph  $.
${                              
   example5.1 $e |-  ph       $.
   example5   $a |-  A. x ph  $.
$}                              

wsb      $a wff [ y / x ] ph  $.
example6 $a |-  ( [ y / x ] [ x / y ] ph <->   
                [ y / x ] ph )               $.
