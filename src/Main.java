import org.antlr.runtime.debug.*;
import org.antlr.v4.parse.ANTLRParser;


public class Main {
    public static void main(String[] args) {

        /*
         * step1: parse;
         * step2: ASTBuilder;
         * step3: globalScope init;
         * step4: symbolCollect;
            (function, class and methods in class, in order to support forwarding reference)
         * step5: functionType generate
         * step6: semantic Analysis
         * step7: print opt
         */
    }
}