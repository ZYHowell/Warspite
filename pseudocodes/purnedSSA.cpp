#include "includes.h"
//this is to transform the first IR to its SSA form. 
/*
 * From CFG to pseudo-SSA(all expression preserved). 
 * Then insert phi function to make it a purned SSA. 
 * Then unfold all expressions(introduce temp var) and renaming(multi-version var). 
 */

/*
 * Each node of the CFG contains: 
 * A label;
 * A vector of commands, in which expressions are folded; 
 *   Only the last command is and should be a jump, conditional or not. 
 * A vector of lines to show the graph; 
 * A map of phi functions. 
 */
int blockNum, varNum;

int varVersion[MAXVARIABLES];
bool Defined[MAXVARIABLES];

int CFGnodeNum;
std::vector<std::shared_ptr<CFGNode_t>> LabelOrder;
int CFGroot;
std::vector<std::vector<int>> precursor;
//the cfg is generated, and its root is the beginning of main
//the precursor[i] contains labels of precursor nodes of the node(label i)

inline int GetVer(ParPair &it, bool isDest = 0)
{
    bool newVer = isDest && Defined[it.varNum];
    Defined[it.varNum] = 1;
    it.verNum = varVersion[it.varNum] = varVersion[it.varNum] + newVer;
    return it.verNum;
}
inline void GetSourceVer(SSALine_t &it)
{
    for (int j = 0;j < 2 && it.Source[j].varNum != -1;++j)
        GetVer(it.Source[j]);
}


/*
 * Lengauer-Tarjan Algorithm
 */
std::vector<int> DFSorder, DFSindex;
std::vector<int> Fathers, sdom, idom, UnionRoot, MinVer;
std::vector<std::vector<int>> bucket;
int tot;
int cnt = 0;
//generate the DFS order first
void DFS(int it)
{
    if (DFSorder[it]) return;
    DFSindex.push_back(it);
    sdom[it] = DFSorder[it] = ++tot;
    for (auto son : LabelOrder[it]->doms) 
        DFS(son->label), Fathers[son->label] = it;
}
void DFSorderGen(int CFGroot)
{
    Fathers.resize(CFGnodeNum);Fathers.clear();
    DFSorder.resize(CFGnodeNum);DFSorder.clear();
    DFSindex.resize(CFGnodeNum + 1);DFSindex.clear();
    sdom.resize(CFGnodeNum);sdom.clear();

    for (int i = 0;i < CFGnodeNum;++i)
        UnionRoot[i] = MinVer[i] = i;

    tot = 0;
    DFS(CFGroot), Fathers[CFGroot] = -1;
}

int FindUnionRoot(int it)
{
    if (UnionRoot[it] == it) return it;
    int ret = FindUnionRoot(UnionRoot[it]);
    if (sdom[MinVer[UnionRoot[it]]] < sdom[MinVer[it]]) MinVer[it] = MinVer[UnionRoot[it]];
    return UnionRoot[it] = ret;
}
inline int eval(int it)
{
    FindUnionRoot(it);
    return MinVer[it];
}

void idomGen(int CFGroot)
{
    int tmp;

    idom.resize(CFGnodeNum);idom.clear();

    for (int i = tot;i > 1;--i) {
        tmp = DFSindex[i];
        for (int pre : precursor[tmp])
            //if (DFSorder[pre]) //no need since the graph is connected. 
            sdom[tmp] = std::min(sdom[tmp], sdom[eval(pre)]);
        //cal idom by the way
        bucket[DFSindex[sdom[tmp]]].push_back(tmp);
        int tmpfather = Fathers[tmp];
        UnionRoot[tmp] = tmpfather;
        for (int buk : bucket[tmpfather]) {
            int u = eval(buk);
            idom[buk] = sdom[u] == sdom[buk] ? tmpfather : u;
        }
        bucket[tmpfather].clear();
    }
    for (int i = 2;i <= tot;++i) {
        tmp = DFSindex[i];
        idom[tmp] = (idom[tmp] == DFSindex[sdom[tmp]]) ? idom[tmp] : idom[idom[tmp]];
    }
}
/*
 * generate the dom boundary by idom
 */

/*
 * using dom boundary to insert phi function here. 
 * for all nodes: 
 *   for all statements: 
 *     if it has a dest(also called def)
 *       goto each dom frontier and check add such phi function
 */