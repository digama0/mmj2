$( monkey3.mm $) 

  $c ( AND ) $. 
  $c monkey SpeakNoEvil HearNoEvil SeeNoEvil $.
  $c ARE IN THE container barrel Barrel $.

  $v A $.
  $v B $.
  $v C $.
  $v D $.
  $v E $.
  
  x       $f monkey    A $.
  y       $f monkey    B $.
  z       $f monkey    C $.

  xxx     $f container D $.
  yyy     $f barrel    E $.
  
  nospeak $a monkey SpeakNoEvil $.
  nohear  $a monkey HearNoEvil  $.
  nosee   $a monkey SeeNoEvil   $.

  barrelisacontainer $a container E $.
  Barrel             $a barrel Barrel $.
    
  3monkeysinthecontainer $a ( A AND B AND C ) ARE IN THE D $.

  3BadMonkeysInTheBarrel $p ( SpeakNoEvil AND HearNoEvil AND SeeNoEvil )
                               ARE IN THE Barrel
                         $=
                              nospeak
                              nohear
                              nosee 
                              Barrel
                              barrelisacontainer
                              3monkeysinthecontainer
                         $. 
  
