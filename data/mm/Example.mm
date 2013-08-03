$( Example.mm cloned from Metamath's set.mm $)

          $c (                                       $.  
          $c )                                       $. 
          $c ->                                      $. 
          $c wff                                     $. 
          $c |-                                      $. 
 
          $v ph                                      $. 
          $v ps                                      $. 
          $v ch                                      $. 

  wph     $f wff ph                                  $.
  wps     $f wff ps                                  $.
  wch     $f wff ch                                  $.

  wi      $a wff ( ph -> ps )                        $.

  ax-1    $a |-  ( ph -> ( ps -> ph ) )              $.

  ${
    min   $e |-  ph                                  $.
    maj   $e |-  ( ph -> ps )                        $.
    ax-mp $a |-  ps                                  $.
  $}

  ${
    a1i.1 $e |-  ph                                  $.
    a1i   $p |-  ( ps -> ph )
          $= wph wps wph wi a1i.1 wph wps ax-1 ax-mp $.
  $}
