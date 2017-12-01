grammar Dater;

// PARSER

opendaterange    : ON? TheDayOf? d=datespec            # SD
                 | BETWEEN TheDayOf? d1=datespec AND TheDayOf? d2=datespec # ODR
                 | AFTER TheDayOf? d=datespec                    # AFT
                 | ON OR AFTER TheDayOf? d=datespec              # OOA
                 | BEFORE TheDayOf? d=datespec                   # BEF
                 | ON OR BEFORE TheDayOf? d=datespec             # OOB
                 ;
    
datespec         : yy=yearnum DASH mm=monthspec DASH dd=dayofmonth     # CDATE1
                 | mm=monthspec SLASH dd=dayofmonth SLASH yy=yearnum   # CDATE2
                 | mo=monthname dd=dayofmonth COMMA? yy=yearnum        # CDATE3
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

TheDayOf         : THE? (DAY | DATE)? OF?
                 ;

BETWEEN          : 'BETWEEN';
AND              : 'AND';
DASH             : '-';
ON               : 'ON';
OR               : 'OR';
BEFORE           : 'BEFORE';
AFTER            : 'AFTER';
SLASH            : '/' ;
THE              : 'THE' ;
DAY              : 'DAY' ;
DATE             : 'DATE' ;
OF               : 'OF' ;
COMMA            : ',' ;

fragment DIGIT   : [0-9] ;

INT         : DIGIT+ ;

    // Whitespace is ignored

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

