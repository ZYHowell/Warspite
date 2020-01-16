#define MAXVARIABLES 1000
#define MAXLINES 10000

int LineNum, varNum;
enum InstType {Jump, Conditional, RR, RI, Move, Ret};
bool Liveliness[MAXVARIABLES][MAXLINES];

struct Line_t
{
    InstType type;
    int JDestO, JDestT;
    int Dest;
    int Source[2];
    int SourceNum;
};
Line_t Line[MAXLINES];

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
    for (int i = 0;i < Line[LineNow].SourceNum;++i) {
        for (int i = 0;i < LineNum;++i) visit[i] = false;
        Anal(LineNow, Line[LineNow].Source[i], visit, Liveliness);
    }
}
void LivelinessAnal()
{
    bool visit[MAXLINES];
    AnalAll(LineNum - 1, visit, Liveliness);
}
