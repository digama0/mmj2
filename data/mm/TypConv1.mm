$( TypConv1.mm --
 *      HYPOTHETICAL:
 *      =====================================================
 *      A2 ISA A1 (LABEL = A2A1, STMT= "A2A1 $a A1 A2var $.")
 *      A3 ISA A2 (LABEL = A3A2, STMT= "A3A2 $a A2 A3var $.")
 *      A3 ISA A1 (LABEL = A3A1, STMT= "A3A1 $a A1 A3var $.")
 *      A3 ISA B2 (LABEL = A3B2, STMT= "A3B2 $a B2 A3var $.")
 *      B2 ISA B  (LABEL = B2B,  STMT= "B2B  $a B  B2var $.")
 *      B3 ISA B2 (LABEL = B3B2, STMT= "B3B2 $a B2 B3var $.")
 *
 *      Cnst A1 has convTo.length   = 0 (empty)
 *                  confFrom[0]     = {A2, A2A1}
 *                          [1]     = {A3, A3A1}
 *
 *      Cnst A2 has convTo[0]       = {A1, A2A1}
 *                  convFrom[0]     = {A3, A3A2}
 *
 *      Cnst A3 has convTo[0]       = {A1, A3A1}
 *                        [1]       = {A2, A3A2}
 *                        [2]       = {B,  A3B2:B2B}
 *                        [3]       = {B2, A3B2}
 *                  convFrom.length = 0 (empty)
 *
 *      Cnst B  has convTo.length   = 0 (empty)
 *                  convFrom[0]     = {A3, A3B2:B2B}
 *                          [1]     = {B2, B2B}
 *                          [2]     = {B3, B3B3:B2B}
 *
 *      Cnst B2 has convTo[0]       = {B, B2B}
 *                  convFrom[0]     = {A3, A3B2}
 *                          [1]     = {B3, B2B3}
 *
 *      Cnst B3 has convTo[0]       = {B, B3B2:B2B}
 *                  convTo[1]       = {B2, B3B2}
 *                  convFrom.length = 0 (empty).
 *
$)

$c A1 A2 A3 B B2 B3 |- $.

$v A1var A2var A3var Bvar B2var B3var $.

A1varh $f A1 A1var $.
A2varh $f A2 A2var $.
A3varh $f A3 A3var $.
Bvarh  $f B  Bvar  $.
B2varh $f B2 B2var $.
B3varh $f B3 B3var $.


A2A1 $a A1 A2var $.
A3A2 $a A2 A3var $.
A3A1 $a A1 A3var $.
A3B2 $a B2 A3var $.
B2B  $a B  B2var $.
B3B2 $a B2 B3var $.

proof $p |- A1 $= ? $.
