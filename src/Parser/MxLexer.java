package Parser;
// Generated from Mx.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MxLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Bool=1, Int=2, Void=3, String=4, Null=5, If=6, Else=7, Const=8, Continue=9, 
		Break=10, For=11, Return=12, Struct=13, Switch=14, While=15, This=16, 
		True=17, False=18, New=19, Class=20, LeftParen=21, RightParen=22, LeftBracket=23, 
		RightBracket=24, LeftBrace=25, RightBrace=26, Less=27, LessEqual=28, Greater=29, 
		GreaterEqual=30, LeftShift=31, RightShift=32, Plus=33, PlusPlus=34, Minus=35, 
		MinusMinus=36, Star=37, Div=38, Mod=39, And=40, Or=41, AndAnd=42, OrOr=43, 
		Caret=44, Not=45, Tilde=46, Question=47, Colon=48, Semi=49, Comma=50, 
		Assign=51, Equal=52, NotEqual=53, Dot=54, StringLiteral=55, Identifier=56, 
		DecimalInteger=57, Whitespace=58, Newline=59, BlockComment=60, LineComment=61;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"Bool", "Int", "Void", "String", "Null", "If", "Else", "Const", "Continue", 
			"Break", "For", "Return", "Struct", "Switch", "While", "This", "True", 
			"False", "New", "Class", "LeftParen", "RightParen", "LeftBracket", "RightBracket", 
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
			"'const'", "'continue'", "'break'", "'for'", "'return'", "'struct'", 
			"'switch'", "'while'", "'this'", "'true'", "'false'", "'new'", "'class'", 
			"'('", "')'", "'['", "']'", "'{'", "'}'", "'<'", "'<='", "'>'", "'>='", 
			"'<<'", "'>>'", "'+'", "'++'", "'-'", "'--'", "'*'", "'/'", "'%'", "'&'", 
			"'|'", "'&&'", "'||'", "'^'", "'!'", "'~'", "'?'", "':'", "';'", "','", 
			"'='", "'=='", "'!='", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Bool", "Int", "Void", "String", "Null", "If", "Else", "Const", 
			"Continue", "Break", "For", "Return", "Struct", "Switch", "While", "This", 
			"True", "False", "New", "Class", "LeftParen", "RightParen", "LeftBracket", 
			"RightBracket", "LeftBrace", "RightBrace", "Less", "LessEqual", "Greater", 
			"GreaterEqual", "LeftShift", "RightShift", "Plus", "PlusPlus", "Minus", 
			"MinusMinus", "Star", "Div", "Mod", "And", "Or", "AndAnd", "OrOr", "Caret", 
			"Not", "Tilde", "Question", "Colon", "Semi", "Comma", "Assign", "Equal", 
			"NotEqual", "Dot", "StringLiteral", "Identifier", "DecimalInteger", "Whitespace", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2?\u0189\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b"+
		"\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3"+
		"\17\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3"+
		"\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3"+
		"\25\3\25\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3"+
		"\32\3\33\3\33\3\34\3\34\3\35\3\35\3\35\3\36\3\36\3\37\3\37\3\37\3 \3 "+
		"\3 \3!\3!\3!\3\"\3\"\3#\3#\3#\3$\3$\3%\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3"+
		")\3*\3*\3+\3+\3+\3,\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3"+
		"\62\3\63\3\63\3\64\3\64\3\65\3\65\3\65\3\66\3\66\3\66\3\67\3\67\38\38"+
		"\78\u0140\n8\f8\168\u0143\138\38\38\39\39\39\39\39\39\39\59\u014e\n9\3"+
		":\3:\7:\u0152\n:\f:\16:\u0155\13:\3;\3;\7;\u0159\n;\f;\16;\u015c\13;\3"+
		";\5;\u015f\n;\3<\6<\u0162\n<\r<\16<\u0163\3<\3<\3=\3=\5=\u016a\n=\3=\5"+
		"=\u016d\n=\3=\3=\3>\3>\3>\3>\7>\u0175\n>\f>\16>\u0178\13>\3>\3>\3>\3>"+
		"\3>\3?\3?\3?\3?\7?\u0183\n?\f?\16?\u0186\13?\3?\3?\3\u0176\2@\3\3\5\4"+
		"\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22"+
		"#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C"+
		"#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q\2s:u;"+
		"w<y={>}?\3\2\t\6\2\f\f\17\17$$^^\5\2C\\aac|\6\2\62;C\\aac|\3\2\63;\3\2"+
		"\62;\4\2\13\13\"\"\4\2\f\f\17\17\2\u0193\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3"+
		"\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2"+
		"\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35"+
		"\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)"+
		"\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2"+
		"\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2"+
		"A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3"+
		"\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2"+
		"\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2"+
		"g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2s\3\2\2\2\2u\3"+
		"\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\3\177\3\2\2\2\5\u0084"+
		"\3\2\2\2\7\u0088\3\2\2\2\t\u008d\3\2\2\2\13\u0094\3\2\2\2\r\u0099\3\2"+
		"\2\2\17\u009c\3\2\2\2\21\u00a1\3\2\2\2\23\u00a7\3\2\2\2\25\u00b0\3\2\2"+
		"\2\27\u00b6\3\2\2\2\31\u00ba\3\2\2\2\33\u00c1\3\2\2\2\35\u00c8\3\2\2\2"+
		"\37\u00cf\3\2\2\2!\u00d5\3\2\2\2#\u00da\3\2\2\2%\u00df\3\2\2\2\'\u00e5"+
		"\3\2\2\2)\u00e9\3\2\2\2+\u00ef\3\2\2\2-\u00f1\3\2\2\2/\u00f3\3\2\2\2\61"+
		"\u00f5\3\2\2\2\63\u00f7\3\2\2\2\65\u00f9\3\2\2\2\67\u00fb\3\2\2\29\u00fd"+
		"\3\2\2\2;\u0100\3\2\2\2=\u0102\3\2\2\2?\u0105\3\2\2\2A\u0108\3\2\2\2C"+
		"\u010b\3\2\2\2E\u010d\3\2\2\2G\u0110\3\2\2\2I\u0112\3\2\2\2K\u0115\3\2"+
		"\2\2M\u0117\3\2\2\2O\u0119\3\2\2\2Q\u011b\3\2\2\2S\u011d\3\2\2\2U\u011f"+
		"\3\2\2\2W\u0122\3\2\2\2Y\u0125\3\2\2\2[\u0127\3\2\2\2]\u0129\3\2\2\2_"+
		"\u012b\3\2\2\2a\u012d\3\2\2\2c\u012f\3\2\2\2e\u0131\3\2\2\2g\u0133\3\2"+
		"\2\2i\u0135\3\2\2\2k\u0138\3\2\2\2m\u013b\3\2\2\2o\u013d\3\2\2\2q\u014d"+
		"\3\2\2\2s\u014f\3\2\2\2u\u015e\3\2\2\2w\u0161\3\2\2\2y\u016c\3\2\2\2{"+
		"\u0170\3\2\2\2}\u017e\3\2\2\2\177\u0080\7d\2\2\u0080\u0081\7q\2\2\u0081"+
		"\u0082\7q\2\2\u0082\u0083\7n\2\2\u0083\4\3\2\2\2\u0084\u0085\7k\2\2\u0085"+
		"\u0086\7p\2\2\u0086\u0087\7v\2\2\u0087\6\3\2\2\2\u0088\u0089\7x\2\2\u0089"+
		"\u008a\7q\2\2\u008a\u008b\7k\2\2\u008b\u008c\7f\2\2\u008c\b\3\2\2\2\u008d"+
		"\u008e\7u\2\2\u008e\u008f\7v\2\2\u008f\u0090\7t\2\2\u0090\u0091\7k\2\2"+
		"\u0091\u0092\7p\2\2\u0092\u0093\7i\2\2\u0093\n\3\2\2\2\u0094\u0095\7p"+
		"\2\2\u0095\u0096\7w\2\2\u0096\u0097\7n\2\2\u0097\u0098\7n\2\2\u0098\f"+
		"\3\2\2\2\u0099\u009a\7k\2\2\u009a\u009b\7h\2\2\u009b\16\3\2\2\2\u009c"+
		"\u009d\7g\2\2\u009d\u009e\7n\2\2\u009e\u009f\7u\2\2\u009f\u00a0\7g\2\2"+
		"\u00a0\20\3\2\2\2\u00a1\u00a2\7e\2\2\u00a2\u00a3\7q\2\2\u00a3\u00a4\7"+
		"p\2\2\u00a4\u00a5\7u\2\2\u00a5\u00a6\7v\2\2\u00a6\22\3\2\2\2\u00a7\u00a8"+
		"\7e\2\2\u00a8\u00a9\7q\2\2\u00a9\u00aa\7p\2\2\u00aa\u00ab\7v\2\2\u00ab"+
		"\u00ac\7k\2\2\u00ac\u00ad\7p\2\2\u00ad\u00ae\7w\2\2\u00ae\u00af\7g\2\2"+
		"\u00af\24\3\2\2\2\u00b0\u00b1\7d\2\2\u00b1\u00b2\7t\2\2\u00b2\u00b3\7"+
		"g\2\2\u00b3\u00b4\7c\2\2\u00b4\u00b5\7m\2\2\u00b5\26\3\2\2\2\u00b6\u00b7"+
		"\7h\2\2\u00b7\u00b8\7q\2\2\u00b8\u00b9\7t\2\2\u00b9\30\3\2\2\2\u00ba\u00bb"+
		"\7t\2\2\u00bb\u00bc\7g\2\2\u00bc\u00bd\7v\2\2\u00bd\u00be\7w\2\2\u00be"+
		"\u00bf\7t\2\2\u00bf\u00c0\7p\2\2\u00c0\32\3\2\2\2\u00c1\u00c2\7u\2\2\u00c2"+
		"\u00c3\7v\2\2\u00c3\u00c4\7t\2\2\u00c4\u00c5\7w\2\2\u00c5\u00c6\7e\2\2"+
		"\u00c6\u00c7\7v\2\2\u00c7\34\3\2\2\2\u00c8\u00c9\7u\2\2\u00c9\u00ca\7"+
		"y\2\2\u00ca\u00cb\7k\2\2\u00cb\u00cc\7v\2\2\u00cc\u00cd\7e\2\2\u00cd\u00ce"+
		"\7j\2\2\u00ce\36\3\2\2\2\u00cf\u00d0\7y\2\2\u00d0\u00d1\7j\2\2\u00d1\u00d2"+
		"\7k\2\2\u00d2\u00d3\7n\2\2\u00d3\u00d4\7g\2\2\u00d4 \3\2\2\2\u00d5\u00d6"+
		"\7v\2\2\u00d6\u00d7\7j\2\2\u00d7\u00d8\7k\2\2\u00d8\u00d9\7u\2\2\u00d9"+
		"\"\3\2\2\2\u00da\u00db\7v\2\2\u00db\u00dc\7t\2\2\u00dc\u00dd\7w\2\2\u00dd"+
		"\u00de\7g\2\2\u00de$\3\2\2\2\u00df\u00e0\7h\2\2\u00e0\u00e1\7c\2\2\u00e1"+
		"\u00e2\7n\2\2\u00e2\u00e3\7u\2\2\u00e3\u00e4\7g\2\2\u00e4&\3\2\2\2\u00e5"+
		"\u00e6\7p\2\2\u00e6\u00e7\7g\2\2\u00e7\u00e8\7y\2\2\u00e8(\3\2\2\2\u00e9"+
		"\u00ea\7e\2\2\u00ea\u00eb\7n\2\2\u00eb\u00ec\7c\2\2\u00ec\u00ed\7u\2\2"+
		"\u00ed\u00ee\7u\2\2\u00ee*\3\2\2\2\u00ef\u00f0\7*\2\2\u00f0,\3\2\2\2\u00f1"+
		"\u00f2\7+\2\2\u00f2.\3\2\2\2\u00f3\u00f4\7]\2\2\u00f4\60\3\2\2\2\u00f5"+
		"\u00f6\7_\2\2\u00f6\62\3\2\2\2\u00f7\u00f8\7}\2\2\u00f8\64\3\2\2\2\u00f9"+
		"\u00fa\7\177\2\2\u00fa\66\3\2\2\2\u00fb\u00fc\7>\2\2\u00fc8\3\2\2\2\u00fd"+
		"\u00fe\7>\2\2\u00fe\u00ff\7?\2\2\u00ff:\3\2\2\2\u0100\u0101\7@\2\2\u0101"+
		"<\3\2\2\2\u0102\u0103\7@\2\2\u0103\u0104\7?\2\2\u0104>\3\2\2\2\u0105\u0106"+
		"\7>\2\2\u0106\u0107\7>\2\2\u0107@\3\2\2\2\u0108\u0109\7@\2\2\u0109\u010a"+
		"\7@\2\2\u010aB\3\2\2\2\u010b\u010c\7-\2\2\u010cD\3\2\2\2\u010d\u010e\7"+
		"-\2\2\u010e\u010f\7-\2\2\u010fF\3\2\2\2\u0110\u0111\7/\2\2\u0111H\3\2"+
		"\2\2\u0112\u0113\7/\2\2\u0113\u0114\7/\2\2\u0114J\3\2\2\2\u0115\u0116"+
		"\7,\2\2\u0116L\3\2\2\2\u0117\u0118\7\61\2\2\u0118N\3\2\2\2\u0119\u011a"+
		"\7\'\2\2\u011aP\3\2\2\2\u011b\u011c\7(\2\2\u011cR\3\2\2\2\u011d\u011e"+
		"\7~\2\2\u011eT\3\2\2\2\u011f\u0120\7(\2\2\u0120\u0121\7(\2\2\u0121V\3"+
		"\2\2\2\u0122\u0123\7~\2\2\u0123\u0124\7~\2\2\u0124X\3\2\2\2\u0125\u0126"+
		"\7`\2\2\u0126Z\3\2\2\2\u0127\u0128\7#\2\2\u0128\\\3\2\2\2\u0129\u012a"+
		"\7\u0080\2\2\u012a^\3\2\2\2\u012b\u012c\7A\2\2\u012c`\3\2\2\2\u012d\u012e"+
		"\7<\2\2\u012eb\3\2\2\2\u012f\u0130\7=\2\2\u0130d\3\2\2\2\u0131\u0132\7"+
		".\2\2\u0132f\3\2\2\2\u0133\u0134\7?\2\2\u0134h\3\2\2\2\u0135\u0136\7?"+
		"\2\2\u0136\u0137\7?\2\2\u0137j\3\2\2\2\u0138\u0139\7#\2\2\u0139\u013a"+
		"\7?\2\2\u013al\3\2\2\2\u013b\u013c\7\60\2\2\u013cn\3\2\2\2\u013d\u0141"+
		"\7$\2\2\u013e\u0140\5q9\2\u013f\u013e\3\2\2\2\u0140\u0143\3\2\2\2\u0141"+
		"\u013f\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0144\3\2\2\2\u0143\u0141\3\2"+
		"\2\2\u0144\u0145\7$\2\2\u0145p\3\2\2\2\u0146\u014e\n\2\2\2\u0147\u0148"+
		"\7^\2\2\u0148\u014e\7p\2\2\u0149\u014a\7^\2\2\u014a\u014e\7^\2\2\u014b"+
		"\u014c\7^\2\2\u014c\u014e\7$\2\2\u014d\u0146\3\2\2\2\u014d\u0147\3\2\2"+
		"\2\u014d\u0149\3\2\2\2\u014d\u014b\3\2\2\2\u014er\3\2\2\2\u014f\u0153"+
		"\t\3\2\2\u0150\u0152\t\4\2\2\u0151\u0150\3\2\2\2\u0152\u0155\3\2\2\2\u0153"+
		"\u0151\3\2\2\2\u0153\u0154\3\2\2\2\u0154t\3\2\2\2\u0155\u0153\3\2\2\2"+
		"\u0156\u015a\t\5\2\2\u0157\u0159\t\6\2\2\u0158\u0157\3\2\2\2\u0159\u015c"+
		"\3\2\2\2\u015a\u0158\3\2\2\2\u015a\u015b\3\2\2\2\u015b\u015f\3\2\2\2\u015c"+
		"\u015a\3\2\2\2\u015d\u015f\7\62\2\2\u015e\u0156\3\2\2\2\u015e\u015d\3"+
		"\2\2\2\u015fv\3\2\2\2\u0160\u0162\t\7\2\2\u0161\u0160\3\2\2\2\u0162\u0163"+
		"\3\2\2\2\u0163\u0161\3\2\2\2\u0163\u0164\3\2\2\2\u0164\u0165\3\2\2\2\u0165"+
		"\u0166\b<\2\2\u0166x\3\2\2\2\u0167\u0169\7\17\2\2\u0168\u016a\7\f\2\2"+
		"\u0169\u0168\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u016d\3\2\2\2\u016b\u016d"+
		"\7\f\2\2\u016c\u0167\3\2\2\2\u016c\u016b\3\2\2\2\u016d\u016e\3\2\2\2\u016e"+
		"\u016f\b=\2\2\u016fz\3\2\2\2\u0170\u0171\7\61\2\2\u0171\u0172\7,\2\2\u0172"+
		"\u0176\3\2\2\2\u0173\u0175\13\2\2\2\u0174\u0173\3\2\2\2\u0175\u0178\3"+
		"\2\2\2\u0176\u0177\3\2\2\2\u0176\u0174\3\2\2\2\u0177\u0179\3\2\2\2\u0178"+
		"\u0176\3\2\2\2\u0179\u017a\7,\2\2\u017a\u017b\7\61\2\2\u017b\u017c\3\2"+
		"\2\2\u017c\u017d\b>\2\2\u017d|\3\2\2\2\u017e\u017f\7\61\2\2\u017f\u0180"+
		"\7\61\2\2\u0180\u0184\3\2\2\2\u0181\u0183\n\b\2\2\u0182\u0181\3\2\2\2"+
		"\u0183\u0186\3\2\2\2\u0184\u0182\3\2\2\2\u0184\u0185\3\2\2\2\u0185\u0187"+
		"\3\2\2\2\u0186\u0184\3\2\2\2\u0187\u0188\b?\2\2\u0188~\3\2\2\2\r\2\u0141"+
		"\u014d\u0153\u015a\u015e\u0163\u0169\u016c\u0176\u0184\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}