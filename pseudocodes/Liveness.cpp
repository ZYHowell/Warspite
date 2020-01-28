#include "includes.h"
//this is a easy liveness analysis before the register allocation

int LineNum, varNum;
bool Liveliness[MAXVARIABLES][MAXLINES];
AssLine_t Line[MAXLINES];

inline void Anal(int LineNow, 
                 int VarNow, 
                 bool *visit, 
                 bool Liveliness[][MAXLINES])
{
    if (LineNow < 0) return;
    if (Liveliness[VarNow][LineNow]) return;
    if (Line[LineNow].Dest == VarNow) return;
    visit[LineNow] = true;
    Liveliness[VarNow][LineNow] = true;
    if (Line[LineNow].type == Jump) {
        Anal(Line[LineNow].JDestO, VarNow, visit, Liveliness);
    }
    else if (Line[LineNow].type == Conditional) {
        Anal(Line[LineNow].JDestO, VarNow, visit, Liveliness);
        Anal(Line[LineNow].JDestT, VarNow, visit, Liveliness);
    }
    else {
        Anal(LineNow - 1, VarNow, visit, Liveliness);
    }
}
inline void AnalAll(int LineNow, bool *visit, bool Liveliness[][MAXLINES])
{
    for (int i = 0;i < 2 && Line[LineNow].Source[i] != -1;+i) {
        for (int i = 0;i < LineNum;++i) visit[i] = false;
        Anal(LineNow, Line[LineNow].Source[i], visit, Liveliness);
    }
}
void LivelinessAnal()
{
    bool visit[MAXLINES];
    AnalAll(LineNum - 1, visit, Liveliness);
}
