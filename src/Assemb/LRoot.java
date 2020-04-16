package Assemb;

import Assemb.LOperand.GReg;
import Assemb.LOperand.PhyReg;
import MIR.IRoperand.Register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class LRoot {
    public static ArrayList<String> phyRegName = new ArrayList<>(Arrays.asList(
            "zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0", "s1",
            "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
            "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"));
    private static ArrayList<Integer> saveStatus = new ArrayList<>(Arrays.asList(
            0, 1, 0, 0, 0, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1 ,1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1));
    //      0,ra,sp,gp,tp,t0,t1-2 ,s0,s1,a0-1 ,a2-7             ,s2-11                        ,t3-6
    //      sp is specially considered here: it is unable to be used by others
    private HashSet<LFn> functions = new HashSet<>(), builtinFunctions = new HashSet<>();
    private ArrayList<PhyReg> phyRegs = new ArrayList<>();
    private ArrayList<PhyReg> callerSaveRegs = new ArrayList<>(), calleeSaveRegs = new ArrayList<>();
    private HashMap<GReg, String> strings = new HashMap<>();
    private HashSet<GReg> globalRegs = new HashSet<>();

    public LRoot() {
        for (int i = 0; i < 32; ++i) {
            phyRegs.add(new PhyReg(phyRegName.get(i)));
            switch (saveStatus.get(i)){
                case 1: callerSaveRegs.add(phyRegs.get(i));break;
                case 2: calleeSaveRegs.add(phyRegs.get(i));break;
            }
        }
    }

    public void addBuiltinFunction(LFn fn) {
        builtinFunctions.add(fn);
    }
    public ArrayList<PhyReg> callerSave() {
        return callerSaveRegs;
    }
    public ArrayList<PhyReg> calleeSave() {
        return calleeSaveRegs;
    }
    public void addString(GReg reg, String str) {
        strings.put(reg, str);
    }
    public void addGlobalReg(GReg reg) {
        globalRegs.add(reg);
    }
    public PhyReg getPhyReg(int i) {
        return phyRegs.get(i);
    }
    public HashSet<LFn> functions() {
        return functions;
    }
    public void addFunction(LFn fn) {
        functions.add(fn);
    }
}
