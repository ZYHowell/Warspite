import AST.rootNode;
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
        InputStream input = new FileInputStream("test.mx");

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

            new IRBuilder(gScope, irRoot).visit(ASTRoot);
            new IRPrinter(true).run(irRoot);
            //optim order: mem2reg-inline-(ADCE-SCCP-CFGSimplify)
        } catch (error er) {
            System.err.println(er.toString());
            throw new RuntimeException();
        }
    }
}