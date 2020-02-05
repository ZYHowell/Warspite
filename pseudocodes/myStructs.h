#ifndef InstTypeDef
#define InstTypeDef
//this is the stack frame
struct varInfo
{
    std::string type;
    int offset, size;
    varInfo():type(""), offset(0), size(){}
    varInfo(const std::string &n, int s, int of = 0):type(n), offset(of), size(s){}
    void setOffset(int of) { offset = of;}
};
struct scope
{
    bool top;   //the top level or not. {int a;{int a;}} vs. {int a;f();} void f(){int a;}
    int totOffset;
    std::map<std::string, varInfo> frame;   //frame[variable name] = variable type name; 
    scope(bool t = 0):top(t), frame(), totOffset(0){}
    inline bool count(std::string name) { return frame.count(name);}
    inline bool add_symbol(const std::string &name, const std::string &type, int size)
    {
        if (frame.count(name)) return false;
        frame[name] = varInfo(type, size);
        return true;
    }
    inline bool set_offset(const std::string &name) //this is done only until the some optimizations
    {
        if (frame.count(name)) return false;
        frame[name].offset = totOffset;
        totOffset += frame[name].size;
        return true;
    }
};
class stacks
{
    std::vector<scope> memory;
    scope globalMemory;
    std::map<std::string, int>  types;  //the length of each type
    int memSize;
public: 
    stacks():memSize(0), memory(), globalMemory(), types()
    {
        types["int"] = 4;
        types["bool"] = types["string"] = 1;
    }
    ~stacks(){}
    bool addType(std::string typeName, int typeSize)
    {
        if (types.count(typeName)) return false;
        types[typeName] = typeSize;
        return true;
    }
    void enter_scope(bool top)
    {
        memory.push_back(scope(top));
        memSize += 1;
    }
    bool exit_scope()
    {
        if (memSize) memory.pop_back();
        else return false;
        return true;
    }
    bool check_scope(const std::string &name)
    {
        if (memSize) {
            int now = memSize - 1;
            while(now >= 0) {
                if (memory[now].count(name)) return true;
                if (memory[now].top) break;
                --now;
            }
        } 
        return globalMemory.count(name);
    }
    bool add_symbol(const std::string &name, const std::string &type, int size = 1)
    {
        if (!types.count(type)) return false;
        if (memSize) return memory.back().add_symbol(name, type, size); //not add the offset now, set it later. 
        else return globalMemory.add_symbol(name, type, size);
    }
    varInfo find_symbol(const std::string &name)
    {
        if (memSize) {
            int now = memSize - 1;
            while(now >= 0) {
                if (memory[now].frame.count(name)) return memory[now].frame[name];
                if (memory[now].top) break;
                --now;
            }
        } 
        if (globalMemory.count(name)) return globalMemory.frame[name];
        else return varInfo();
    }
};
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