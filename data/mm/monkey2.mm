$( monkey2.mm $) 

  $c ( AND ) $. 
  $c monkey SpeakNoEvil HearNoEvil SeeNoEvil $.
  $c ARE IN THE Barrel $.

  $v A $.
  $v B $.
  $v C $.
  
  x       $f monkey A $.
  y       $f monkey B $.
  z       $f monkey C $.
  
  nospeak $a monkey SpeakNoEvil $.
  nohear  $a monkey HearNoEvil  $.
  nosee   $a monkey SeeNoEvil   $.

  3things $a ( A AND B AND C ) ARE IN THE Barrel $.
 
  3BadMonkeys $p ( SpeakNoEvil AND HearNoEvil AND SeeNoEvil ) 
                 ARE IN THE Barrel
              $=   nospeak
                   nohear
                   nosee
                   3things 
              $.   

  3GoodMonkeys $p ( SpeakNoEvil AND HearNoEvil AND SeeNoEvil )
                  ARE IN THE Barrel
              $=
                   nospeak
                   nohear
                   nosee
                   3things
              $.
