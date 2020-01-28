#ifndef InstTypeDef
#define InstTypeDef
//for all variables, the -1 means there is no such variable
//this is for the assembly code
enum AssInstType {Jump, Conditional, RR, RI, Move, Ret};
struct AssLine_t
{
    AssInstType type;
    int JDestO, JDestT;
    int Dest;
    int Source[2];
    int CalType;        //this is already the opcode
};
//this is for the blocked and SSA code, the only difference is the later has attribute "version". 
enum SSAInstType {Jump, Conditional, RR, RI, Return};
struct SSALine_t
{
    SSAInstType type;
    int JDestT, JDestF;
    ParPair Dest;
    ParPair Source[2];
    int CalType;
};
//this is for each variable of the SSA
struct ParPair
{
    int varNum;
    int verNum;
};

//the expression and cfgLine, many bugs obviously. 
enum ExprType {Constant, Variable, BinOp, Mem};
struct Expr_t
{
    ExprType type;
    int value;
    int opcode;
    std::shared_ptr<Expr_t> child[2];
};

enum CFGLineType {Jump, CJump, Move, EXE};
struct CFGLine_t
{
    CFGLineType type;
    int DestVar;
    Expr_t expr[2];
};

using label_t = int;
using version_t = int;
using varNum_t = int;
struct CFGNode_t
{
    int label; 
    std::vector<std::shared_ptr<CFGLine_t>> commands;
    std::vector<std::shared_ptr<CFGNode_t>> doms;
    std::map<varNum_t, std::map<label_t, version_t>> phis;  //phi[variable][label] = version
    void phiInsert(int varNum, int bn, int vn) {
        phis[varNum][bn] = vn;
    }
};
#endif