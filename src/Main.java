import AST.rootNode;
import Assemb.LRoot;
import BackEnd.*;
import FrontEnd.*;
import Optim.*;
import MIR.Root;
import Parser.MxErrorListener;
import Parser.MxLexer;
import Parser.MxParser;
import Util.error.error;
import Util.scope.globalScope;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;


public class Main {
    public static void main(String[] args) throws Exception{
        boolean doCodeGen = true, doOptimization = false;//, inTest = false;
        String name = null;

        if (args.length > 0) {
            for (String arg : args) {
                switch (arg) {
                    case "-opt": doOptimization = true;break;
                    case "-semantic": doCodeGen = false;break;
                    //case "-test": inTest = true;break;
                    default: break;
                }
                if (arg.length() > 5 && arg.substring(0, 5).equals("-dir=")){
                    String sub = arg.substring(5, arg.length() - 5);
                    if (name == null) name = sub;
                    else System.err.println("multiple dir name: " + name + " and " + sub);
                }
            }
        }
        PrintStream pst = new PrintStream("output.s");
        PrintStream IRPst = new PrintStream("out.ll");
        if (name == null) name = "test.mx";
        InputStream input = new FileInputStream(name);
        try {
            rootNode ASTRoot;
            Root irRoot = new Root();
            globalScope gScope = new globalScope();

            MxLexer lexer = new MxLexer(CharStreams.fromStream(input));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new MxErrorListener());
            MxParser parser = new MxParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new MxErrorListener());
            ParseTree parseTreeRoot = parser.program();
            ASTBuilder astBuilder = new ASTBuilder();
            ASTRoot = (rootNode)astBuilder.visit(parseTreeRoot);
            new SymbolCollector(gScope, irRoot).visit(ASTRoot);
            new TypeFilter(gScope).visit(ASTRoot);
            new SemanticChecker(gScope, irRoot).visit(ASTRoot);
            //new SideEffectBuilder(gScope).visit(ASTRoot);
            //new HIRDCE(gScope).visit(ASTRoot);

            if (doCodeGen) {
                new IRBuilder(gScope, irRoot).visit(ASTRoot);
                //new IRPrinter(false, IRPst).run(irRoot);
                new Mem2Reg(irRoot).run();
                //optim order: inline-(ADCE-SCCP-CFGSimplify)
                if (doOptimization) new Optimization(irRoot).run();
                // new IRPrinter(false, IRPst).run(irRoot);
                new PhiResolve(irRoot).run();

                LRoot lRoot = new InstSelection(irRoot).run();
                // new AsmPrinter(lRoot, new PrintStream("debug.s")).run();
                new RegAlloc(lRoot).run();
                new AsmPrinter(lRoot, pst).run();
            }
        } catch (error er) {
            System.err.println(er.toString());
            throw new RuntimeException();
        }
    }
}