grammar Countables;

// 

countable returns [Integer value] 
               : m1=ordinal {$value = $m1.value; }  
               | m2=cardinal {$value = $m2.value; }
               ;
             
ordinal returns [Integer value] 
               : d=six_dig_ord 
                 { $value = $d.value; }  
               | m=three_dig_card DASH? MILLIONTH 
                 { $value = 1000000 * $m.value; }  
               | m1=three_dig_card DASH? MILLION COMMA? AND? d1=six_dig_ord 
                 { $value = 1000000 * $m1.value + $d1.value; }  
               ;

six_dig_ord returns [Integer value] 
               : d=three_dig_ord 
                 { $value = $d.value; }  
               | m=three_dig_card DASH? THOUSANDTH 
                 { $value = 1000 * $m.value; }  
               | m1=three_dig_card DASH? THOUSAND COMMA? AND? d1=three_dig_ord 
                 { $value = 1000 * $m1.value + $d1.value; }  
               ;

three_dig_ord returns [Integer value] 
                 // TWENTY-NINTH
               : m1=two_dig_ord 
                 { $value = $m1.value; }  
                 // THREE HUNDREDTH
               | m2=two_dig_card DASH? HUNDREDTH 
                 { $value = 100 * $m2.value; }  
                 // TWENTY-THREE-HUNDRED AND FOURTH
               | m3=two_dig_card DASH? HUNDRED COMMA? AND? d1=two_dig_ord 
                 { $value = 100 * $m3.value + $d1.value; }  
                 // FIVE SIXTY-SECOND
               | m4=ones_card d2=two_dig_ord 
                 { $value = 100 * $m4.value + $d2.value; }                 
               ;
               
two_dig_ord returns [Integer value] 
                 // THIRD
               : m1=ones_ord {$value = $m1.value; }
                 // 10TH
               | m2=tenth {$value = $m2.value; }
                 // FOURTEENTH
               | ma=teens_ord {$value = $ma.value; }
                 // SIXTIETH
               | m3=high_tens_ord {$value = $m3.value; }
                 // FOURTY-FIFTH
               | m4=high_tens_card DASH? d1=ones_ord 
                 {$value = $m4.value + $d1.value; }
                 // 63RD
               | m5=high_ones_card d2=ones_ord
                 {$value = 10 * $m5.value + $d2.value; }
               ;
               
ones_ord returns [Integer value] 
               : m1=first {$value = $m1.value; } 
               | m2=second {$value = $m2.value; } 
               | m3=third {$value = $m3.value; } 
               | mh=high_ones_ord  {$value = $mh.value; }
               ;
               
high_tens_ord returns [Integer value] 
               : m1=twentieth {$value = $m1.value; } 
               | m2=thirtieth {$value = $m2.value; } 
               | m3=fourtieth {$value = $m3.value; } 
               | m4=fiftieth  {$value = $m4.value; } 
               | m5=sixtieth {$value = $m5.value; } 
               | m6=seventieth {$value = $m6.value; } 
               | m7=eightieth {$value = $m7.value; } 
               | m8=nintieth {$value = $m8.value; } 
               ;
               
teens_ord returns [Integer value] 
               : m1=eleventh {$value = $m1.value; } 
               | m2=twelvth {$value = $m2.value; } 
               | m3=thirteenth {$value = $m3.value; } 
               | m4=fourteenth  {$value = $m4.value; } 
               | m5=fifteenth {$value = $m5.value; } 
               | m6=sixteenth {$value = $m6.value; } 
               | m7=seventeenth {$value = $m7.value; } 
               | m8=eighteenth {$value = $m8.value; } 
               | m9=nineteenth {$value = $m9.value; } 
               ;
                             
teens_ord2 returns [Integer value] 
               : m1=oneth {$value = $m1.value; } 
               | m2=twoth {$value = $m2.value; } 
               | m3=threeth {$value = $m3.value; } 
               | mh=high_ones_ord  {$value = $mh.value; }
               ;
               
high_ones_ord returns [Integer value]               
               : m4=fourth  {$value = $m4.value; } 
               | m5=fifth {$value = $m5.value; } 
               | m6=sixth {$value = $m6.value; } 
               | m7=seventh {$value = $m7.value; } 
               | m8=eighth {$value = $m8.value; } 
               | m9=ninth {$value = $m9.value; } 
               ;
               
cardinal returns [Integer value] 
               : m1=six_dig_card {$value = $m1.value; }  
               | m2=three_dig_card DASH? MILLION {$value = 1000000 * $m2.value; }  
               | m3=three_dig_card DASH? MILLION COMMA? AND? d1=six_dig_card 
                 {$value = 1000000 * $m3.value + $d1.value; }  
               ;
                                 
six_dig_card returns [Integer value] 
               : m1=three_dig_card {$value = $m1.value; }  
               | m2=three_dig_card DASH? THOUSAND {$value = 1000 * $m2.value; }  
               | m3=three_dig_card DASH? THOUSAND COMMA? AND? d1=three_dig_card 
                 {$value = 1000 * $m3.value + $d1.value; }  
               ;
                                 
three_dig_card returns [Integer value] 
                 // EIGHTY-ONE
               : m1=two_dig_card {$value = $m1.value; }  
                 // SIX HUNDRED
               | m2=two_dig_card DASH? HUNDRED {$value = 100 * $m2.value; }  
                 // FIFTY-SIX HUNDRED AND TWENTY FIVE
               | m3=two_dig_card DASH? HUNDRED COMMA? AND? d1=two_dig_card 
                 {$value = 100 * $m3.value + $d1.value; }  
                 // FOUR FOURTY-FOUR
               | m4=ones_card d2=two_dig_card 
                 {$value = 100 * $m4.value + $d2.value; }                 
               ;
                                 
two_dig_card returns [Integer value] 
                 // THREE
               : m1=ones_card {$value = $m1.value; }
                 // TEN
               | m2=ten {$value = $m2.value; }
                 // SIXTEEN
               | ma=teens_card {$value = $ma.value; }
                 // FIFTY
               | m3=high_tens_card {$value = $m3.value; }
                 // THIRTY-TWO
               | m4=high_tens_card DASH d1=ones_card 
                 {$value = $m4.value + $d1.value; }
                 // 78
               | m5=high_ones_card d2=ones_card
                 {$value = $m5.value + $d2.value; }
//               | m6=INT
//                 {$value = Integer.valueOf($m6.value)}
               ;
 
high_tens_card returns [Integer value] 
               : m2=twenty {$value = $m2.value; } 
               | m3=thirty {$value = $m3.value; } 
               | m4=fourty {$value = $m4.value; } 
               | m5=fifty  {$value = $m5.value; } 
               | m6=sixty {$value = $m6.value; } 
               | m7=seventy {$value = $m7.value; } 
               | m8=eighty {$value = $m8.value; } 
               | m9=ninety {$value = $m9.value; } 
               ;

teens_card returns [Integer value] 
               : m1=eleven {$value = $m1.value; } 
               | m2=twelve {$value = $m2.value; } 
               | m3=thirteen {$value = $m3.value; } 
               | m4=fourteen  {$value = $m4.value; } 
               | m5=fifteen {$value = $m5.value; } 
               | m6=sixteen {$value = $m6.value; } 
               | m7=seventeen {$value = $m7.value; } 
               | m8=eighteen {$value = $m8.value; } 
               | m9=nineteen {$value = $m9.value; } 
               ;
               
ones_card returns [Integer value] 
               : m1=one {$value = $m1.value; } 
               | mh=high_ones_card {$value = $mh.value; } 
               ;
                  
high_ones_card returns [Integer value] 
               : m2=two {$value = $m2.value; } 
               | m3=three {$value = $m3.value; } 
               | m4=four  {$value = $m4.value; } 
               | m5=five {$value = $m5.value; } 
               | m6=six {$value = $m6.value; } 
               | m7=seven {$value = $m7.value; } 
               | m8=eight {$value = $m8.value; } 
               | m9=nine {$value = $m9.value; } 
               ;
               

// BASE CARDINALS

one returns [Integer value] 
               : ('ONE' | '1'  | '01')  { $value = 1; } 
               ;
                  
two returns [Integer value] 
               : ('TWO' | '2'  | '02') {$value = 2; }
               ;
                  
three returns [Integer value] 
               : ('THREE' | '3'  | '03') {$value = 3; }
               ;
                  
four returns [Integer value] 
               : ('FOUR' | '4'  | '04') {$value = 4; }
               ;
                  
five returns [Integer value] 
               : ('FIVE' | '5'  | '05') {$value = 5; }
               ;
                  
six returns [Integer value] 
               : ('SIX' | '6'  | '06') {$value = 6; }
               ;
                  
seven returns [Integer value] 
               : ('SEVEN' | '7'  | '07') {$value = 7; }
               ;
                  
eight returns [Integer value] 
               : ('EIGHT' | '8'  | '08') {$value = 8; }
               ;
                  
nine returns [Integer value] 
               : ('NINE' | '9'  | '09') {$value = 9; }
               ;

ten returns [Integer value] 
               : ('TEN' | '10') {$value = 10; }
               ;
                  
eleven returns [Integer value] 
               : ('ELEVEN' | '11') {$value = 11; }
               ;
                  
twelve returns [Integer value] 
               : ('TWELVE' | '12') {$value = 12; }
               ;
                  
thirteen returns [Integer value] 
               : ('THIRTEEN' | '13') {$value = 13; }
               ;
                  
fourteen returns [Integer value] 
               : ('FOURTEEN' | '14') {$value = 14; }
               ;
                  
fifteen returns [Integer value] 
               : ('FIFTEEN' | '15') {$value = 15; }
               ;
                  
sixteen returns [Integer value] 
               : ('SIXTEEN' | '16') {$value = 16; }
               ;
                  
seventeen returns [Integer value] 
               : ('SEVENTEEN' | '17') {$value = 17; }
               ;
                  
eighteen returns [Integer value] 
               : ('EIGHTEEN' | '18') {$value = 18; }
               ;
                  
nineteen returns [Integer value] 
               : ('NINTEEN' | '19') {$value = 19; }
               ;

twenty returns [Integer value] 
               : ('TWENTY' | '20') {$value = 20; }
               ;
                  
thirty returns [Integer value] 
               : ('THIRTY' | '30') {$value = 30; }
               ;
                  
fourty returns [Integer value] 
               : ('FOURTY' | 'FORTY' | '40') {$value = 40; }
               ;
                  
fifty returns [Integer value] 
               : ('FIFTY' | '50') {$value = 50; }
               ;
                  
sixty returns [Integer value] 
               : ('SIXTY' | '60') {$value = 60; }
               ;
                  
seventy returns [Integer value] 
               : ('SEVENTY' | '70') {$value = 70; }
               ;
                  
eighty returns [Integer value] 
               : ('EIGHTY' | '80') {$value = 80; }
               ;
                  
ninety returns [Integer value] 
               : ('NINETY' | 'NINTY' | '90') {$value = 90; }
               ;

// BASE ORDINALS

oneth returns [Integer value] 
               : ('1TH') {$value = 1; }
               ;
                  
twoth returns [Integer value] 
               : ('2TH') {$value = 2; }
               ;
                  
threeth returns [Integer value] 
               : ('3TH') {$value = 3; }
               ;

first returns [Integer value] 
               : ('FIRST' | '1ST'  | '01ST') {$value = 1; }
               ;
                  
second returns [Integer value] 
               : ('SECOND' | '2ND'  | '02ND') {$value = 2; }
               ;
                  
third returns [Integer value] 
               : ('THIRD' | '3RD'  | '03RD') {$value = 3; }
               ;
                  
fourth returns [Integer value] 
               : ('FOURTH' | '4TH') {$value = 4; }
               ;
                  
fifth returns [Integer value] 
               : ('FIFTH' | '5TH') {$value = 5; }
               ;
                  
sixth returns [Integer value] 
               : ('SIXTH' | '6TH') {$value = 6; }
               ;
                  
seventh returns [Integer value] 
               : ('SEVENTH' | '7TH') {$value = 7; }
               ;
                  
eighth returns [Integer value] 
               : ('EIGHTH' | '8TH') {$value = 8; }
               ;
                  
ninth returns [Integer value] 
               : ('NINTH' | '9TH') {$value = 9; }
               ;

tenth returns [Integer value] 
               : ('TENTH' | '10TH') {$value = 10; }
               ;
                  
eleventh returns [Integer value] 
               : ('ELEVENTH' | '11TH') {$value = 11; }
               ;
                  
twelvth returns [Integer value] 
               : ('TWELVTH' | 'TWELVETH' | 'TWELFTH' | '12TH') {$value = 12; }
               ;
                  
thirteenth returns [Integer value] 
               : ('THIRTEENTH' | '13TH') {$value = 13; }
               ;
                  
fourteenth returns [Integer value] 
               : ('FOURTEENTH' | '14TH') {$value = 14; }
               ;
                  
fifteenth returns [Integer value] 
               : ('FIFTEENTH' | '15TH') {$value = 15; }
               ;
                  
sixteenth returns [Integer value] 
               : ('SIXTEENTH' | '16TH') {$value = 16; }
               ;
                  
seventeenth returns [Integer value] 
               : ('SEVENTEENTH' | '17TH') {$value = 17; }
               ;
                  
eighteenth returns [Integer value] 
               : ('EIGHTEENTH' | '18TH') {$value = 18; }
               ;
                  
nineteenth returns [Integer value] 
               : ('NINTEENTH' | '19TH') {$value = 19; }
               ;

twentieth returns [Integer value] 
               : ('TWENTIETH' | '20TH') {$value = 20; }
               ;
                  
thirtieth returns [Integer value] 
               : ('THIRTIETH' | '30TH') {$value = 30; }
               ;
                  
fourtieth returns [Integer value] 
               : ('FOURTIETH' | 'FORTIETH' | '40TH') {$value = 40; }
               ;
                  
fiftieth returns [Integer value] 
               : ('FIFTIETH' | '50TH') {$value = 50; }
               ;
                  
sixtieth returns [Integer value] 
               : ('SIXTIETH' | '60TH') {$value = 60; }
               ;
                  
seventieth returns [Integer value] 
               : ('SEVENTIETH' | '70TH') {$value = 70; }
               ;
                  
eightieth returns [Integer value] 
               : ('EIGHTIETH' | '80TH') {$value = 80; }
               ;
                  
nintieth returns [Integer value] 
               : ('NINTIETH' | 'NINETIETH' | '90TH') {$value = 90; }
               ;

// LEXER
                  
HUNDRED        : 'HUNDRED' ;               
HUNDREDTH      : 'HUNDREDTH' ;
THOUSAND       : 'THOUSAND' ;               
THOUSANDTH     : 'THOUSANDTH' ;               
MILLION        : 'MILLION' ;               
MILLIONTH      : 'MILLIONTH' ;               
AND            : 'AND' ;
DASH           : '-' ;
COMMA          : ',' ;
                                    
// skip whitespace
WS  : [ \t\r\n\u000C]+ -> skip
    ;                  