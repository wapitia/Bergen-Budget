grammar Sched;

import Countables, Dater;

// monthly examples:

//   MONTHLY DAY 1
//   MONTHLY DAYS 3, 13
//   MONTHLY FIRST WEEK DAYS MON, TUE
//   FIRST MON OF MONTH
//   FOURTH OF THE MONTH

// weekly examples:
//   BIWEEKLY SECOND FRIDAY
//   BIWEEKLY FIRST MONDAY AND SECOND WED FIRST WEEK FALLS ON 2017-12-12

schedule          :  dailySched
                  |  weeklySched
                  |  biweeklySched
                  |  monthlySched
                  |  quarterlySched
                  |  annuallySched
                  ;
                  
dailySched        :  DAILY 
                  ;
                  
weeklySched       :  WEEKLY
                  ;
                  
biweeklySched     :  BIWEEKLY domSpecs                               #biwSch1
                  ;
                  
quarterlySched    :  QUARTERLY
                  ;
                  
annuallySched     :  ANNUALLY 
                  ;
                  
monthlySched      :  MONTHLY domSpecs                                #monSch1
                  |  domSpecs OF THE? MONTH                          #monSch2   
                  |  ordDaysOfMonth OF THE? MONTH                    #monSch3
                  ;

// day-of-month schedule specifications
domSpecs          :  DAY daysOfMonthList                             #domSpecs1
                  |  weeksOfMonth WEEK? DAY? daysOfWeekList          #domSpecs2
                  ;

daysOfMonthList   :  (cardinal | monthnum) COMMA? AND? daysOfMonthList? 
                  ;
                  
weeksOfMonth      : weekOfMonth COMMA? AND? weeksOfMonth? 
                  ;
                  
weekOfMonth returns [Integer value] 
                  : d=ordinal { $value = $d.value; }
                  | LAST { $value = -1; }
                  ;

daysOfWeekList    : dayOfWeek COMMA? AND? daysOfWeekList? 
                  ;

dayOfWeek         : MON | TUE | WED | THU | FRI | SAT | SUN
                  ;
                  
//monthOfYear       : JAN | FEB | MAR | APR | MAY | JUN
//                  | JUL | AUG | SEP | OCT | NOV | DEC
//                  ;

ordDaysOfMonth    : ordDayOfMonth COMMA? AND? ordDaysOfMonth? 
                  ;

ordDayOfMonth     : ordinal;

// tokens
MONTHLY           : 'MONTHLY' ;
DAILY             : 'DAILY' ;
WEEKLY            : 'WEEKLY' ;
BIWEEKLY          : 'BIWEEKLY' | 'BI-WEEKLY' | 'BI WEEKLY' ;
QUARTERLY         : 'QUARTERLY' ;
ANNUALLY          : 'ANNUALLY' | 'YEARLY' ; 

OF                : 'OF' ;
THE               : 'THE' ;
MONTH             : 'MONTH' ;
COMMA             : ',' ;
AND               : 'AND' ;
DAY               : 'DAYS' | 'DAY' ;
WEEK              : 'WEEKS' | 'WEEK' ;


LAST              : 'LAST' ;

MON               : 'MON' | 'MONDAY' ;
TUE               : 'TUE' | 'TUES' | 'TUESDAY' ;
WED               : 'WED' | 'WEDNESDAY' ;
THU               : 'THU' | 'THUR' | 'THURS' | 'THURSDAY' ;
FRI               : 'FRI' | 'FRIDAY' ;
SAT               : 'SAT' | 'SATURDAY';
SUN               : 'SUN' | 'SUNDAY';
                  
//JAN               : 'JAN' ;
//FEB               : 'FEB' ;
//MAR               : 'MAR' ;
//APR               : 'APR' ;
//MAY               : 'MAY' ;
//JUN               : 'JUN' ;
//JUL               : 'JUL' ;
//AUG               : 'AUG' ;
//SEP               : 'SEP' ;
//OCT               : 'OCT' ;
//NOV               : 'NOV' ;
//DEC               : 'DEC' ;

                                    
// skip whitespace
WS  : [ \t\r\n\u000C]+ -> skip
    ;                  