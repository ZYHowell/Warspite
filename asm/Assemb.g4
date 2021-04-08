grammar Assemb;

file: line* EOF;

line: inst | pseudoInst | directive | start;

inst: rtype | itype | stype | btype | utype | jtype | ltype;
start: Symbol ':';
rtype: Rop rd=Reg ',' src1=Reg ',' src2=Reg;
itype: Iop rd=Reg ',' src=Reg ',' im=imm;
stype: Sop value=Reg ',' offset=imm '(' addr=Reg ')';
btype: Bop src1=Reg ',' src2=Reg ',' label=Symbol;
utype: Uop rd=Reg ',' im=imm;
jtype: Jop rd=Reg ',' im=imm;
ltype: Lop rd=Reg ',' im=imm '(' rs=Reg ')';

pseudoInst
    : 'mv' rd=Reg ',' src=Reg                                                       #mv
    | 'li' rd=Reg ',' src=imm                                                       #li
    | 'ret'                                                                         #ret
    | 'j' label=Symbol                                                              #jp
    | Lop rd=Reg ',' src=Symbol                                                     #ld
    | 'la' rd=Reg ',' src=Symbol                                                    #la
    | 'call' Symbol                                                                 #call
    | Szop rd=Reg ',' src=Reg                                                       #sz
    | Bzop src=Reg ',' label=Symbol                                                 #bz
    ;

directive
    : '.section'? Section                   #section
    | '.globl' symbol=Symbol                #globl
    | '.type' symbol=Symbol ',' Type        #type
    | ('.p2align' | '.align') Integer       #align
    | '.word' Integer                       #word
    | ('.asciz' | '.string') StringLiteral  #asciz
    | '.file' name=StringLiteral            #filename
    ;

Section: '.text' | '.bss' | '.rodata';
Type: '@object' | '@function';

Rop: 'add' | 'sub' | 'slt' | 'xor' | 'or' | 'and'  | 'sll' | 'srl' | 'sra' | 'mul' | 'div' | 'rem';
Iop: 'addi' | 'slti' | 'xori' | 'ori' | 'andi'  | 'slli' | 'srli' | 'srai' | 'jalr';
Sop: 'sb' | 'sh' | 'sw';
Bop: 'beq' | 'bne' | 'blt' | 'bge';
Uop: 'auipc' | 'lui';
Jop: 'jal';
Lop: 'lb' | 'lh' | 'lw';
Szop: 'seqz' | 'snez' | 'sgtz' | 'sltz';
Bzop: 'beqz' | 'bnez' | 'bgtz' | 'bltz' | 'blez' | 'bgez';

Reg: 'zero' | 'ra' | 'sp' | 'gp' | 'tp' | 't0' | 't1' | 't2' |
     's0' | 's1' | 'a0' | 'a1' | 'a2' | 'a3' | 'a4' | 'a5' | 'a6' | 'a7' |
     's2' | 's3' | 's4' | 's5' | 's6' | 's7' | 's8' | 's9' | 's10' | 's11' |
     't3' | 't4' | 't5' | 't6';
Directive: '.section' | '.globl' | '.type' | '.p2align' | '.align' | '.word' | '.asciz' | '.string' | '.file';

Symbol: [a-zA-Z_.][a-zA-Z_.0-9$]*;

StringLiteral: '"' SChar* '"';
fragment
SChar
    : ~["\\\n\r]
    | '\\n'
    | '\\\\'
    | '\\"'
    ;

imm: Integer | relocation;
relocation: HL '(' Symbol ')';
HL: ('%hi' | '%lo');

Integer: DecimalInteger | ('-' DecimalInteger);
DecimalInteger: [1-9] [0-9]* | '0';

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

LineComment
    :   '#' ~[\r\n]*
        -> skip
    ;