import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/*
	Basic lexical analyzer that breaks a string down into tokens which are placed in a list. 
	
	Description of syntax/valid tokens:
	
		Reserved Keywords:
			var, if, while, true, false
	
		Literals:
			INT: any number of digits 0-9
			REAL: digits 0-9 followed by a '.' and another set of digits 0-9
			STRING: any characters between double quotes. no concat or escape characters yet
			
		Identifiers:
			must begin with a letter, followed by any number of alphanumeric characters or underscores
			
		Operators:
			all operators except for sizeof and -> taken from C, though I may not use them all or use them in the same way
			
		Punctuation:
			braces, brackets, parenthesis, and semicolons
			
		Comments:
			$ line comments begin with a dolla sign and end with a \n character
			I chose the $ because I don't use it for anything else
	
	
	
	The Token format has something left to be desired, but it will be improved in subsequent versions. 
*/

public class Lexer {
	
	// 4 is the ascii value for "EOT" (end of transmission)
	final static char EOF = 4;
	
	final static String[] reservedWords = {"if", "var", "while", "true", "false", "else", "print"};
	
	final static HashMap<Character, String> opMap;
	static {
		opMap = new HashMap<>();
		opMap.put('+', "PLUS");
		opMap.put('-', "MINUS");
		opMap.put('*', "STAR");
		opMap.put('/', "SLASH");
		opMap.put('%', "PERCENT");
		opMap.put('!', "BANG");
		opMap.put('=', "EQUALS");
		opMap.put('&', "AND");
		opMap.put('|', "PIPE");
		opMap.put('<', "LESS");
		opMap.put('>', "GREATER");
		opMap.put('~', "TILDE");
		opMap.put('^', "CARROT");
	}
	
		
	private static String input;

	// list of tokens 
	private static ArrayList<Token> tokenList;
	
	// current lookahead character
	private static char look;
	
	// current position in the input file/string
	private static int pos;
	
	private static int lineNumber;
	
	
	/* ~~~~~ Sample input strings ~~~~~ */
	static String sample = "var a = 123;\n var expression = (a + b) * 4 << thing;";
	
	static String optest = "+ - * / % ! = & | < > ~ ^ += -= *= /= %= != == &= |= <= >= ^= && || << >> ++ -- <<= >>=";
	
	static String commenttest = "var blah = 73;$  \nvar b = asdf;";
	
	static String errortest = "var line1 = 123;\nvar line2 = 123.123;\nvar line3 = 123.";
	
	static String errortest2 = "var line1 = 123;\nvar line2 = 123.123;\nvar line3 = .123";
	
	static String numTest = "var integer = 556;\nvar real = 5.56;";
	
	static String stringTest = "var string = \"I am a string\"";
	/* ~~~~~ ~~~~~~~~~~~~~~~~~~~~ ~~~~~ */
	
	
	public static void main(String[] args) {
		tokenList = new ArrayList<>();
		pos = 0;
		lineNumber = 0;
		input = stringTest;
		
		getChar();
		lexSourceCode();
		for(Token t : tokenList)
			System.out.println(t.toString());
		
	}
	
	public static List<Token> lexInputString(String source) {
		tokenList = new ArrayList<>();
		pos = 0;
		lineNumber = 0;
		input = source;
		
		getChar();
		lexSourceCode();
		
		return tokenList;
	}
	
	
	private static void getChar(){
		if(pos >= input.length()) {
			look = EOF;
		}
		else {
			pos++;
			look = input.charAt(pos - 1);
		}	
	}
	
	// peek ahead an extra character without changing look or advancing position
	private static char peek() {
		if(pos + 1 >= input.length()) {
			return EOF;
		}
		return input.charAt(pos + 1);
	}
	
	/* 
		main loop and entry point of lexical analysis
	*/
	public static void lexSourceCode(){
		
		while(look != EOF) {
			if(isDigit(look)) {
				getNum();
			}
			else if(isAlpha(look)) {
				getSymbol();
			}
			else if(isOp(look)) {
				getOp();
			}
			else if(isPunc(look)) {
				getPunctuation();
			}
			else if(look == '\"') {
				getString();
			}
			else if(look == '$') {
				
				// for now let's just have line comments that are initiated with a '$' and terminated with a newline
				while(look != '\n')
					getChar();
				lineNumber++;
				// this call to getChar is not strictly needed, since the lexer will ignore whitespace anyways, but it saves us an iteration
				getChar();
			}
			else if(Character.isWhitespace(look)){
				
				//simply skip over any whitespace we encounter, increment line number if it's a newline though
				if(look == '\n')
					lineNumber++;
				getChar();
			}
			else {
				error("Unrecognized start of token", lineNumber, "" + look);
			}
		}
	}
	
	
	/* ####################### TOKEN READING METHODS ####################### */
	
	/*
		Recognize and consume a string
	*/
	private static void getString() {
		getChar();
		
		StringBuilder string = new StringBuilder();
		
		while(look != '\"' && look != EOF){
			string.append(look);
			getChar();
		}
		if(look == EOF)
			error("Unclosed string literal beginning", lineNumber, "");
		
		getChar();
		
		tokenList.add(new Token("STRING", string.toString()));
	}
	
	/*
		Void function for recognizing essentially every operator. Makes use of the statically declared opMap HashMap to build the string ID of
		the given operator.
	*/
	private static void getOp() {
		StringBuilder tokenType = new StringBuilder();
		tokenType.append(opMap.get(look));
		
		// several operators can be followed by an '=' to form a different operator
		if(canBeFollowByEquals(look)) {
			
			getChar();
			if(look == '=') {
				
				tokenType.append("_EQUALS");
				getChar();
			}
			// other operators can be repeated to form different operators
			else if(canBeRepeated(look)) {	
			
				// NOTE: canBeRepeated is a subset of canBeFollowByEquals, so we call it in this scope instead
				char last = look;
				if(look == last) {
					
					tokenType.append("_");
					tokenType.append(opMap.get(look));
					getChar();
					
					// special case for <<= and >>= operators. I know they're uncommon but it feels wrong to exclude them since 
					// I've already included both the shift operators and all the other x_equals operators.
					if(last == '<' || last == '>') {
						if(look == '='){
							tokenType.append("_EQUALS");
							getChar();
						}
					}
				}
			}
		}
		// some operators fit neither of these critera but we still need to advance the lookahead character
		else
			getChar();
		
		// finally we add the token to the list
		tokenList.add(new Token(tokenType.toString()));
	}
	
	
	// this could really be done with hashmap, much like getOp. Oh well, maybe I'll change it later
	private static void getPunctuation() {
		
		switch(look) {
			case '[':
				tokenList.add(new Token("OPEN_BRACKET"));
				break;
			case ']':
				tokenList.add(new Token("CLOSE_BRACKET"));
				break;
			case '{':
				tokenList.add(new Token("OPEN_BRACE"));
				break;
			case '}':
				tokenList.add(new Token("CLOSE_BRACE"));
				break;
			case '(':
				tokenList.add(new Token("OPEN_PAREN"));
				break;
			case ')':
				tokenList.add(new Token("CLOSE_PAREN"));
				break;
			case ';':
				tokenList.add(new Token("SEMICOLON"));
		}
		getChar();
	}
	
	// this will also benefit from hashing when more reserved words are added
	private static void getSymbol() {
		
		StringBuilder token = new StringBuilder();
		
		do {
			token.append(look);
			getChar();
		} while(isSymbolChar(look));
		
		String keyword = token.toString();
		if(!checkAndAddReservedKeyWord(keyword)) {
			tokenList.add(new Token("IDENTIFIER", keyword));
		}
	}
	
	/* Determines if the passed string is a reserved keyword and adds it to the list of tokens if it is.
		Returns true if a keyword was found and added, false otherwise.
	*/ 
	private static boolean checkAndAddReservedKeyWord(String keyword) {
		for(String s : reservedWords) {
			if(keyword.equals(s)){
				tokenList.add(new Token(s.toUpperCase()));
				return true;
			}
		}
		return false;
	}
	
	/* recognizes and consumes a number (integer or real) and adds it to the tokenlist */
	private static void getNum() {
		StringBuilder token = new StringBuilder();
		
		do {
			token.append(look);
			getChar();
		} while (isDigit(look));
		
		// if we encounter a dot, we have a real (float/double) instead of an int
		if(look == '.') {
			
			token.append(".");
			
			// if the dot is followed by a digit, we process another set of digits
			if(isDigit(peek())) {
				
				getChar();
				do {
					token.append(look);
					getChar();
				} while (isDigit(look));
				tokenList.add(new Token("REAL", token.toString()));
			}
			else {
				error("Invalid token encountered", lineNumber, token.toString());
			}
		}
		else {
			tokenList.add(new Token("INT", token.toString()));
		}	
	}
	
	/* ####################### ---------------------- ####################### */
	
	
	/* Print an error and tell us what line it was on */
	private static void error(String message, int line, String token) {
		System.out.printf("%s at line %d: \"%s\"\nCompilation terminated.", message, line, token);
		System.exit(1);
	}
	
	
	/* Character catagorization methods */
	
	private static boolean isDigit(char c) { return Character.isDigit(c);}
	
	private static boolean isAlpha(char c) { return Character.isLetter(c); }
	
	private static boolean isSymbolChar(char c) { return isDigit(c) || isAlpha(c) || c =='_'; }
	
	private static boolean canBeFollowByEquals(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '=' ||c == '!' || 
			c == '&' || c == '|' || c == '<' || c == '>' || c == '^';
	}
	
	// returns true if the character is an operator that can be repeated to change it's meaning
	private static boolean canBeRepeated(char c) {
		return c == '+' || c == '-' || c == '&' || c == '|' || c == '<' || c == '>';
	}

	private static boolean isOp(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '=' ||c == '!' || 
			c == '&' || c == '|' || c == '<' || c == '>' || c == '^' || c == '~';
	}
	
	private static boolean isPunc(char c) {
		return c == '(' || c == ')' || c == ';' || c == '{' || c == '}' || c == '[' || c == ']';
	}
}