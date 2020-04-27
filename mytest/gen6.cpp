/*
 * 6.mx is too big to record, so I only record how I generate it here. 
 * if define useSetN, this code will be 896KB. 
 * If I use n^2 algorithm to record my DCE analysis, I will certainly TLE. 
 * Fortunately, in my testbench, there is no such case, according to @peterzheng. 
 * Merciful TA!
 */
#include <iostream>
#include <string>
#define useSetN
const int setN = 20000;    

using namespace std;

void output(int n) {
    string caller = "test"  + to_string(n);
    string callee = "test" + to_string(n + 1);
    cout << "int " + caller + "() {\n    return " + callee + "();\n}\n";
}

int main() {
    int n;
    #ifndef useSetN
    cin >> n;
    #endif
    #ifdef useSetN
    n = setN;
    #endif
    cout << "int t;\n";
    cout << "int test" + to_string(n) + "() {\n    return t;\n}\n";
    for (int i = n - 1;i >= 0;--i) output(i);
    cout << "int main() {\n    t = getInt();\n    printInt(test0());\n}";
    return 0;
}