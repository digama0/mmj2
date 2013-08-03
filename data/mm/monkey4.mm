$( monkey4.mm $) 

  $c ( AND ) $. 
  $c monkey SpeakNoEvil HearNoEvil SeeNoEvil $.
  $c ARE IN THE container barrel Barrel $.
  $c things thing $.

  $v A $.
  $v B $.
  $v C $.
  $v D $.
  $v E $.
  $v F $.
  $v G $.
  $v H $.
  $v I $.

  mA        $f monkey    A $.
  mB        $f monkey    B $.
  mC        $f monkey    C $. 
  cD        $f container D $.
  bE        $f barrel    E $.
  tF        $f things    F $.
  tG        $f thing     G $.
  tH        $f thing     H $.
  tI        $f thing     I $.

  barrelisacontainer $a container E $.
  Barrel             $a barrel    Barrel $.
  monkeyisathing     $a thing     A $.         
 
  Nospeak $a monkey SpeakNoEvil $.
  Nohear  $a monkey HearNoEvil  $.
  Nosee   $a monkey SeeNoEvil   $.

  3things $a things G AND H AND I $.
  
  thingsinthecontainer $a ( F ) ARE IN THE D $.

  3BadMonkeysInTheBarrel $p ( SpeakNoEvil AND HearNoEvil AND SeeNoEvil )
                              ARE IN THE Barrel
       $=
                    Barrel               $( $a barrel    Barrel $.     $)
                barrelisacontainer       $( $a container E $.          $)
                        Nospeak          $( $a monkey SpeakNoEvil $.   $)
                    monkeyisathing       $( $a thing     A $.          $)
                        Nohear           $( $a monkey HearNoEvil  $.   $)
                    monkeyisathing       $( $a thing     A $.          $)
                        Nosee            $( $a monkey SeeNoEvil   $.   $)
                    monkeyisathing       $( $a thing     A $.          $)
                3things                  $( $a things G AND H AND I $. $)
            thingsinthecontainer         $( $a ( F ) ARE IN THE D $.   $)
       $. 
  
$( NOTICE how easy it would be to simulate a logic theorem
   by prefacing the statements in "thingsinthecontainer" 
   and "3BadMonkeysInTheBarrel" with the "|=" turnstile constant.
   We can avoid doing that in this case because the statements
   already begin with a constant -- the constant "(") -- thus
   satisfying the basic rule of metamath that every labelled
   statement begin with a constant. $) 
