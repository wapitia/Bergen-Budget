grammar Dater;

// PARSER

opendaterange    : d=datespec                          # SD
                 | BETWEEN d1=datespec AND d2=datespec # ODR
                 | AFTER d=datespec                    # AFT
                 | ON OR AFTER d=datespec              # OOA
                 | BEFORE d=datespec                   # BEF
                 | ON OR BEFORE d=datespec             # OOB
                 ;
    
datespec         : yy=yearnum DASH mm=monthspec DASH dd=dayofmonth     # CDATE1
                 | mm=monthspec SLASH dd=dayofmonth SLASH yy=yearnum   # CDATE2
                 ;

yearnum          : INT ;


monthspec        : monthnum    # MONO
                 | monthname   # MONAME
                 ;

dayofmonth       : INT;

monthnum         : INT;

monthname        : jan | feb | mar | apr | may | jun | jul  | aug  | sep | oct | nov | dec;

jan              : 'JANUARY' | 'JAN' ;
feb              : 'FEBRUARY' | 'FEB' ;
mar              : 'MARCH' | 'MAR' ;
apr              : 'APRIL' | 'APR' ;
may              : 'MAY' ;
jun              : 'JUNE' | 'JUN' ;
jul              : 'JULY' | 'JUL' ;
aug              : 'AUGUST' | 'AUG';
sep              : 'SEPTEMBER' | 'SEP' ;
oct              : 'OCTOBER' | 'OCT';
nov              : 'NOVEMBER' | 'NOV';
dec              : 'DECEMBER' | 'DEC';

// LEXER

BETWEEN          : 'BETWEEN';
AND              : 'AND';
DASH             : '-';
ON               : 'ON';
OR               : 'OR';
BEFORE           : 'BEFORE';
AFTER            : 'AFTER';
SLASH            : '/' ;

fragment DIGIT   : [0-9] ;

INT         : DIGIT+ ;

    // Whitespace is ignored

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

