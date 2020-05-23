package Parser;
// Generated from Mx.g4 by ANTLR 4.7.2

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MxLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Bool=1, Int=2, Void=3, String=4, Null=5, If=6, Else=7, Continue=8, Break=9, 
		For=10, Return=11, Struct=12, Switch=13, While=14, This=15, True=16, False=17, 
		New=18, Class=19, LeftParen=20, RightParen=21, LeftBracket=22, RightBracket=23, 
		LeftBrace=24, RightBrace=25, Less=26, LessEqual=27, Greater=28, GreaterEqual=29, 
		LeftShift=30, RightShift=31, Plus=32, PlusPlus=33, Minus=34, MinusMinus=35, 
		Star=36, Div=37, Mod=38, And=39, Or=40, AndAnd=41, OrOr=42, Caret=43, 
		Not=44, Tilde=45, Question=46, Colon=47, Semi=48, Comma=49, Assign=50, 
		Equal=51, NotEqual=52, Dot=53, StringLiteral=54, Identifier=55, DecimalInteger=56, 
		Whitespace=57, Newline=58, BlockComment=59, LineComment=60;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Bool", "Int", "Void", "String", "Null", "If", "Else", "Continue", "Break", 
			"For", "Return", "Struct", "Switch", "While", "This", "True", "False", 
			"New", "Class", "LeftParen", "RightParen", "LeftBracket", "RightBracket", 
			"LeftBrace", "RightBrace", "Less", "LessEqual", "Greater", "GreaterEqual", 
			"LeftShift", "RightShift", "Plus", "PlusPlus", "Minus", "MinusMinus", 
			"Star", "Div", "Mod", "And", "Or", "AndAnd", "OrOr", "Caret", "Not", 
			"Tilde", "Question", "Colon", "Semi", "Comma", "Assign", "Equal", "NotEqual", 
			"Dot", "StringLiteral", "SChar", "Identifier", "DecimalInteger", "Whitespace", 
			"Newline", "BlockComment", "LineComment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'bool'", "'int'", "'void'", "'string'", "'null'", "'if'", "'else'", 
			"'continue'", "'break'", "'for'", "'return'", "'struct'", "'switch'", 
			"'while'", "'this'", "'true'", "'false'", "'new'", "'class'", "'('", 
			"')'", "'['", "']'", "'{'", "'}'", "'<'", "'<='", "'>'", "'>='", "'<<'", 
			"'>>'", "'+'", "'++'", "'-'", "'--'", "'*'", "'/'", "'%'", "'&'", "'|'", 
			"'&&'", "'||'", "'^'", "'!'", "'~'", "'?'", "':'", "';'", "','", "'='", 
			"'=='", "'!='", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Bool", "Int", "Void", "String", "Null", "If", "Else", "Continue", 
			"Break", "For", "Return", "Struct", "Switch", "While", "This", "True", 
			"False", "New", "Class", "LeftParen", "RightParen", "LeftBracket", "RightBracket", 
			"LeftBrace", "RightBrace", "Less", "LessEqual", "Greater", "GreaterEqual", 
			"LeftShift", "RightShift", "Plus", "PlusPlus", "Minus", "MinusMinus", 
			"Star", "Div", "Mod", "And", "Or", "AndAnd", "OrOr", "Caret", "Not", 
			"Tilde", "Question", "Colon", "Semi", "Comma", "Assign", "Equal", "NotEqual", 
			"Dot", "StringLiteral", "Identifier", "DecimalInteger", "Whitespace", 
			"Newline", "BlockComment", "LineComment"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public MxLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Mx.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2>\u0181\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3"+
		"\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3"+
		"\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20"+
		"\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\26"+
		"\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\34"+
		"\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3!\3!\3\"\3\"\3\"\3"+
		"#\3#\3$\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3*\3+\3+\3+\3,\3,"+
		"\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3"+
		"\64\3\65\3\65\3\65\3\66\3\66\3\67\3\67\7\67\u0138\n\67\f\67\16\67\u013b"+
		"\13\67\3\67\3\67\38\38\38\38\38\38\38\58\u0146\n8\39\39\79\u014a\n9\f"+
		"9\169\u014d\139\3:\3:\7:\u0151\n:\f:\16:\u0154\13:\3:\5:\u0157\n:\3;\6"+
		";\u015a\n;\r;\16;\u015b\3;\3;\3<\3<\5<\u0162\n<\3<\5<\u0165\n<\3<\3<\3"+
		"=\3=\3=\3=\7=\u016d\n=\f=\16=\u0170\13=\3=\3=\3=\3=\3=\3>\3>\3>\3>\7>"+
		"\u017b\n>\f>\16>\u017e\13>\3>\3>\3\u016e\2?\3\3\5\4\7\5\t\6\13\7\r\b\17"+
		"\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+"+
		"\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+"+
		"U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o\2q9s:u;w<y={>\3\2\t\6\2\f"+
		"\f\17\17$$^^\4\2C\\c|\6\2\62;C\\aac|\3\2\63;\3\2\62;\4\2\13\13\"\"\4\2"+
		"\f\f\17\17\2\u018b\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3"+
		"\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2"+
		"\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E"+
		"\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2"+
		"\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2"+
		"\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k"+
		"\3\2\2\2\2m\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2"+
		"\2\2\2{\3\2\2\2\3}\3\2\2\2\5\u0082\3\2\2\2\7\u0086\3\2\2\2\t\u008b\3\2"+
		"\2\2\13\u0092\3\2\2\2\r\u0097\3\2\2\2\17\u009a\3\2\2\2\21\u009f\3\2\2"+
		"\2\23\u00a8\3\2\2\2\25\u00ae\3\2\2\2\27\u00b2\3\2\2\2\31\u00b9\3\2\2\2"+
		"\33\u00c0\3\2\2\2\35\u00c7\3\2\2\2\37\u00cd\3\2\2\2!\u00d2\3\2\2\2#\u00d7"+
		"\3\2\2\2%\u00dd\3\2\2\2\'\u00e1\3\2\2\2)\u00e7\3\2\2\2+\u00e9\3\2\2\2"+
		"-\u00eb\3\2\2\2/\u00ed\3\2\2\2\61\u00ef\3\2\2\2\63\u00f1\3\2\2\2\65\u00f3"+
		"\3\2\2\2\67\u00f5\3\2\2\29\u00f8\3\2\2\2;\u00fa\3\2\2\2=\u00fd\3\2\2\2"+
		"?\u0100\3\2\2\2A\u0103\3\2\2\2C\u0105\3\2\2\2E\u0108\3\2\2\2G\u010a\3"+
		"\2\2\2I\u010d\3\2\2\2K\u010f\3\2\2\2M\u0111\3\2\2\2O\u0113\3\2\2\2Q\u0115"+
		"\3\2\2\2S\u0117\3\2\2\2U\u011a\3\2\2\2W\u011d\3\2\2\2Y\u011f\3\2\2\2["+
		"\u0121\3\2\2\2]\u0123\3\2\2\2_\u0125\3\2\2\2a\u0127\3\2\2\2c\u0129\3\2"+
		"\2\2e\u012b\3\2\2\2g\u012d\3\2\2\2i\u0130\3\2\2\2k\u0133\3\2\2\2m\u0135"+
		"\3\2\2\2o\u0145\3\2\2\2q\u0147\3\2\2\2s\u0156\3\2\2\2u\u0159\3\2\2\2w"+
		"\u0164\3\2\2\2y\u0168\3\2\2\2{\u0176\3\2\2\2}~\7d\2\2~\177\7q\2\2\177"+
		"\u0080\7q\2\2\u0080\u0081\7n\2\2\u0081\4\3\2\2\2\u0082\u0083\7k\2\2\u0083"+
		"\u0084\7p\2\2\u0084\u0085\7v\2\2\u0085\6\3\2\2\2\u0086\u0087\7x\2\2\u0087"+
		"\u0088\7q\2\2\u0088\u0089\7k\2\2\u0089\u008a\7f\2\2\u008a\b\3\2\2\2\u008b"+
		"\u008c\7u\2\2\u008c\u008d\7v\2\2\u008d\u008e\7t\2\2\u008e\u008f\7k\2\2"+
		"\u008f\u0090\7p\2\2\u0090\u0091\7i\2\2\u0091\n\3\2\2\2\u0092\u0093\7p"+
		"\2\2\u0093\u0094\7w\2\2\u0094\u0095\7n\2\2\u0095\u0096\7n\2\2\u0096\f"+
		"\3\2\2\2\u0097\u0098\7k\2\2\u0098\u0099\7h\2\2\u0099\16\3\2\2\2\u009a"+
		"\u009b\7g\2\2\u009b\u009c\7n\2\2\u009c\u009d\7u\2\2\u009d\u009e\7g\2\2"+
		"\u009e\20\3\2\2\2\u009f\u00a0\7e\2\2\u00a0\u00a1\7q\2\2\u00a1\u00a2\7"+
		"p\2\2\u00a2\u00a3\7v\2\2\u00a3\u00a4\7k\2\2\u00a4\u00a5\7p\2\2\u00a5\u00a6"+
		"\7w\2\2\u00a6\u00a7\7g\2\2\u00a7\22\3\2\2\2\u00a8\u00a9\7d\2\2\u00a9\u00aa"+
		"\7t\2\2\u00aa\u00ab\7g\2\2\u00ab\u00ac\7c\2\2\u00ac\u00ad\7m\2\2\u00ad"+
		"\24\3\2\2\2\u00ae\u00af\7h\2\2\u00af\u00b0\7q\2\2\u00b0\u00b1\7t\2\2\u00b1"+
		"\26\3\2\2\2\u00b2\u00b3\7t\2\2\u00b3\u00b4\7g\2\2\u00b4\u00b5\7v\2\2\u00b5"+
		"\u00b6\7w\2\2\u00b6\u00b7\7t\2\2\u00b7\u00b8\7p\2\2\u00b8\30\3\2\2\2\u00b9"+
		"\u00ba\7u\2\2\u00ba\u00bb\7v\2\2\u00bb\u00bc\7t\2\2\u00bc\u00bd\7w\2\2"+
		"\u00bd\u00be\7e\2\2\u00be\u00bf\7v\2\2\u00bf\32\3\2\2\2\u00c0\u00c1\7"+
		"u\2\2\u00c1\u00c2\7y\2\2\u00c2\u00c3\7k\2\2\u00c3\u00c4\7v\2\2\u00c4\u00c5"+
		"\7e\2\2\u00c5\u00c6\7j\2\2\u00c6\34\3\2\2\2\u00c7\u00c8\7y\2\2\u00c8\u00c9"+
		"\7j\2\2\u00c9\u00ca\7k\2\2\u00ca\u00cb\7n\2\2\u00cb\u00cc\7g\2\2\u00cc"+
		"\36\3\2\2\2\u00cd\u00ce\7v\2\2\u00ce\u00cf\7j\2\2\u00cf\u00d0\7k\2\2\u00d0"+
		"\u00d1\7u\2\2\u00d1 \3\2\2\2\u00d2\u00d3\7v\2\2\u00d3\u00d4\7t\2\2\u00d4"+
		"\u00d5\7w\2\2\u00d5\u00d6\7g\2\2\u00d6\"\3\2\2\2\u00d7\u00d8\7h\2\2\u00d8"+
		"\u00d9\7c\2\2\u00d9\u00da\7n\2\2\u00da\u00db\7u\2\2\u00db\u00dc\7g\2\2"+
		"\u00dc$\3\2\2\2\u00dd\u00de\7p\2\2\u00de\u00df\7g\2\2\u00df\u00e0\7y\2"+
		"\2\u00e0&\3\2\2\2\u00e1\u00e2\7e\2\2\u00e2\u00e3\7n\2\2\u00e3\u00e4\7"+
		"c\2\2\u00e4\u00e5\7u\2\2\u00e5\u00e6\7u\2\2\u00e6(\3\2\2\2\u00e7\u00e8"+
		"\7*\2\2\u00e8*\3\2\2\2\u00e9\u00ea\7+\2\2\u00ea,\3\2\2\2\u00eb\u00ec\7"+
		"]\2\2\u00ec.\3\2\2\2\u00ed\u00ee\7_\2\2\u00ee\60\3\2\2\2\u00ef\u00f0\7"+
		"}\2\2\u00f0\62\3\2\2\2\u00f1\u00f2\7\177\2\2\u00f2\64\3\2\2\2\u00f3\u00f4"+
		"\7>\2\2\u00f4\66\3\2\2\2\u00f5\u00f6\7>\2\2\u00f6\u00f7\7?\2\2\u00f78"+
		"\3\2\2\2\u00f8\u00f9\7@\2\2\u00f9:\3\2\2\2\u00fa\u00fb\7@\2\2\u00fb\u00fc"+
		"\7?\2\2\u00fc<\3\2\2\2\u00fd\u00fe\7>\2\2\u00fe\u00ff\7>\2\2\u00ff>\3"+
		"\2\2\2\u0100\u0101\7@\2\2\u0101\u0102\7@\2\2\u0102@\3\2\2\2\u0103\u0104"+
		"\7-\2\2\u0104B\3\2\2\2\u0105\u0106\7-\2\2\u0106\u0107\7-\2\2\u0107D\3"+
		"\2\2\2\u0108\u0109\7/\2\2\u0109F\3\2\2\2\u010a\u010b\7/\2\2\u010b\u010c"+
		"\7/\2\2\u010cH\3\2\2\2\u010d\u010e\7,\2\2\u010eJ\3\2\2\2\u010f\u0110\7"+
		"\61\2\2\u0110L\3\2\2\2\u0111\u0112\7\'\2\2\u0112N\3\2\2\2\u0113\u0114"+
		"\7(\2\2\u0114P\3\2\2\2\u0115\u0116\7~\2\2\u0116R\3\2\2\2\u0117\u0118\7"+
		"(\2\2\u0118\u0119\7(\2\2\u0119T\3\2\2\2\u011a\u011b\7~\2\2\u011b\u011c"+
		"\7~\2\2\u011cV\3\2\2\2\u011d\u011e\7`\2\2\u011eX\3\2\2\2\u011f\u0120\7"+
		"#\2\2\u0120Z\3\2\2\2\u0121\u0122\7\u0080\2\2\u0122\\\3\2\2\2\u0123\u0124"+
		"\7A\2\2\u0124^\3\2\2\2\u0125\u0126\7<\2\2\u0126`\3\2\2\2\u0127\u0128\7"+
		"=\2\2\u0128b\3\2\2\2\u0129\u012a\7.\2\2\u012ad\3\2\2\2\u012b\u012c\7?"+
		"\2\2\u012cf\3\2\2\2\u012d\u012e\7?\2\2\u012e\u012f\7?\2\2\u012fh\3\2\2"+
		"\2\u0130\u0131\7#\2\2\u0131\u0132\7?\2\2\u0132j\3\2\2\2\u0133\u0134\7"+
		"\60\2\2\u0134l\3\2\2\2\u0135\u0139\7$\2\2\u0136\u0138\5o8\2\u0137\u0136"+
		"\3\2\2\2\u0138\u013b\3\2\2\2\u0139\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a"+
		"\u013c\3\2\2\2\u013b\u0139\3\2\2\2\u013c\u013d\7$\2\2\u013dn\3\2\2\2\u013e"+
		"\u0146\n\2\2\2\u013f\u0140\7^\2\2\u0140\u0146\7p\2\2\u0141\u0142\7^\2"+
		"\2\u0142\u0146\7^\2\2\u0143\u0144\7^\2\2\u0144\u0146\7$\2\2\u0145\u013e"+
		"\3\2\2\2\u0145\u013f\3\2\2\2\u0145\u0141\3\2\2\2\u0145\u0143\3\2\2\2\u0146"+
		"p\3\2\2\2\u0147\u014b\t\3\2\2\u0148\u014a\t\4\2\2\u0149\u0148\3\2\2\2"+
		"\u014a\u014d\3\2\2\2\u014b\u0149\3\2\2\2\u014b\u014c\3\2\2\2\u014cr\3"+
		"\2\2\2\u014d\u014b\3\2\2\2\u014e\u0152\t\5\2\2\u014f\u0151\t\6\2\2\u0150"+
		"\u014f\3\2\2\2\u0151\u0154\3\2\2\2\u0152\u0150\3\2\2\2\u0152\u0153\3\2"+
		"\2\2\u0153\u0157\3\2\2\2\u0154\u0152\3\2\2\2\u0155\u0157\7\62\2\2\u0156"+
		"\u014e\3\2\2\2\u0156\u0155\3\2\2\2\u0157t\3\2\2\2\u0158\u015a\t\7\2\2"+
		"\u0159\u0158\3\2\2\2\u015a\u015b\3\2\2\2\u015b\u0159\3\2\2\2\u015b\u015c"+
		"\3\2\2\2\u015c\u015d\3\2\2\2\u015d\u015e\b;\2\2\u015ev\3\2\2\2\u015f\u0161"+
		"\7\17\2\2\u0160\u0162\7\f\2\2\u0161\u0160\3\2\2\2\u0161\u0162\3\2\2\2"+
		"\u0162\u0165\3\2\2\2\u0163\u0165\7\f\2\2\u0164\u015f\3\2\2\2\u0164\u0163"+
		"\3\2\2\2\u0165\u0166\3\2\2\2\u0166\u0167\b<\2\2\u0167x\3\2\2\2\u0168\u0169"+
		"\7\61\2\2\u0169\u016a\7,\2\2\u016a\u016e\3\2\2\2\u016b\u016d\13\2\2\2"+
		"\u016c\u016b\3\2\2\2\u016d\u0170\3\2\2\2\u016e\u016f\3\2\2\2\u016e\u016c"+
		"\3\2\2\2\u016f\u0171\3\2\2\2\u0170\u016e\3\2\2\2\u0171\u0172\7,\2\2\u0172"+
		"\u0173\7\61\2\2\u0173\u0174\3\2\2\2\u0174\u0175\b=\2\2\u0175z\3\2\2\2"+
		"\u0176\u0177\7\61\2\2\u0177\u0178\7\61\2\2\u0178\u017c\3\2\2\2\u0179\u017b"+
		"\n\b\2\2\u017a\u0179\3\2\2\2\u017b\u017e\3\2\2\2\u017c\u017a\3\2\2\2\u017c"+
		"\u017d\3\2\2\2\u017d\u017f\3\2\2\2\u017e\u017c\3\2\2\2\u017f\u0180\b>"+
		"\2\2\u0180|\3\2\2\2\r\2\u0139\u0145\u014b\u0152\u0156\u015b\u0161\u0164"+
		"\u016e\u017c\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}