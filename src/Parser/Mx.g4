grammar Mx;

program : (varDef | funcDef | classDef)* EOF;

classDef : Class Identifier '{' (varDef | funcDef)* '}';
funcDef : type Identifier '(' paramList? ')' suite ';';
varDef : type singleVarDef (',' singleVarDef)* ';';

singleVarDef : Identifier ('=' expression)?;

paramList : param (',' param)*;
param : type Identifier;

basicType : Bool | Int | String;
type 
    : (Identifier | basicType) ('[' ']')* 
    | Void
    ;

suite : '{' statement* '}';

statement
    : suite                                                 #block
    | varDef                                                #vardef
    | If '(' expression ')' statement (Else statement)?     #ifStmt
    | For '(' init=expression? ';' cond=expression? ';'
                incr=expression? ')' statement              #forStmt
    | While '(' expression ')' statement                    #whileStmt
    | Return expression? ';'                                #returnStmt
    | Break ';'                                             #breakStmt
    | Continue ';'                                          #continueStmt
    | expression ';'                                        #pureExprStmt
    | ';'                                                   #emptyStmt
    ;

expressionList
    : expression (',' expression)*
    ;

expression
    : primary                                               #atomExpr
    | expression '.' Identifier                             #memberExpr
    | <assoc=right> 'new' creator                           #newExpr
    | expression '[' expression ']'                         #subscript
    | expression '(' expressionList? ')'                    #arglist
    | expression op=('++' | '--')                           #suffixExpr
    | <assoc=right> op=('+' | '-' | '++' | '--') expression #prefixExpr
    | <assoc=right> op=('~' | '!' ) expression              #prefixExpr
    | expression op=('*' | '/' | '%') expression            #binaryExpr
    | expression op=('+' | '-') expression                  #binaryExpr
    | expression op=('<<' | '>>') expression                #binaryExpr
    | expression op=('<' | '>' | '>=' | '<=') expression    #binaryExpr
    | expression op=('==' | '!=' ) expression               #binaryExpr
    | expression op='&' expression                          #binaryExpr
    | expression op='^' expression                          #binaryExpr
    | expression op='|' expression                          #binaryExpr
    | expression '&&' expression                            #binaryExpr
    | expression '||' expression                            #binaryExpr
    | <assoc=right> expression '=' expression               #binaryExpr
    ;

primary
    : '(' expression ')' 
    | This 
    | Identifier 
    | literal 
    ;

literal
    : DecimalInteger 
    | StringLiteral 
    | boolValue=(True | False) 
    | Null 
    ;

creator
    : (basicType | Identifier) ('[' expression ']')+ ('[' ']')* 
    | (basicType | Identifier) '(' ')'
    | (basicType | Identifier) 
    ;

Bool : 'bool';
Int : 'int';
Void : 'void';
String : 'string';
Null: 'null';
If : 'if';
Else : 'else';
Const : 'const';
Continue : 'continue';
Break : 'break';
For : 'for';
Return : 'return';
Struct : 'struct';
Switch : 'switch';
While : 'while';
This : 'this';
True : 'true';
False : 'false';
New : 'new';
Class : 'class';

LeftParen : '(';
RightParen : ')';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';

Less : '<';
LessEqual : '<=';
Greater : '>';
GreaterEqual : '>=';
LeftShift : '<<';
RightShift : '>>';

Plus : '+';
PlusPlus : '++';
Minus : '-';
MinusMinus : '--';
Star : '*';
Div : '/';
Mod : '%';

And : '&';
Or : '|';
AndAnd : '&&';
OrOr : '||';
Caret : '^';
Not : '!';
Tilde : '~';

Question : '?';
Colon : ':';
Semi : ';';
Comma : ',';

Assign : '=';
Equal : '==';
NotEqual : '!=';

Dot : '.';

StringLiteral
    : '"' SChar* '"'
    ;

fragment
SChar
    : ~["\\\n\r]
    | '\\n'
    | '\\\\'
    | '\\"'
    ;

Identifier
    : [a-zA-Z_] [a-zA-Z_0-9]*
    ;

DecimalInteger
    : [1-9] [0-9]*
    | '0'
    ;

Whitespace
    :   [ \t]+
        -> skip
    ;

Newline
    :   (   '\r' '\n'?
        |   '\n'
        )
        -> skip
    ;

BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;