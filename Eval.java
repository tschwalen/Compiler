
import java.util.ArrayList;
import java.util.List;

public class Eval {
	
	/*
		<expression> ::= <term> [<addop> <term>]*
		<term> ::= <factor>  [ <mulop> <factor ]*
		<factor> ::= <number> | (<expression>) | <variable>
		
	*/
	
	static Machine machine;

	static List<Token> tokenList;
	
	static int pos = 0;
	
	static Token lookTok;
	
	public static void main(String[] args) {
		

		tokenList = new ArrayList<>();
		tokenList.add(new Token("OPEN_PAREN"));
		tokenList.add(new Token("INT", "5"));
		tokenList.add(new Token("PLUS"));
		tokenList.add(new Token("INT", "6"));
		tokenList.add(new Token("CLOSE_PAREN"));
		tokenList.add(new Token("STAR"));
		tokenList.add(new Token("IDENTIFIER", "var2"));
		
		
		machine = new IA32_ATT();
		
		getTok();
		expression();
	}
	
	
	public static void parseTokens(List<Token> inputList) {
		tokenList = inputList;
		
		machine = new IA32_ATT();
		
		
		parseProgram();
		
		machine.print();

		// then tell machine to write the whole symbol table
		machine.writeSymbolTable();

		
	}
	
	/*******************************************************************/
	
	private static void parseProgram() {
		
		getTok();
		while(!lookTok.isType("EOF")) {
			statement();
		}
		
	}
	
	// <statement> ::= <assignment>
	private static void statement() {
		
		if(lookTok.isType("IDENTIFIER")) {
			assignment();
		}
		else if(lookTok.isType("IF")) {
			ifStatement();
		}
		else if(lookTok.isType("WHILE")) {
			whileStatement();
		}
		else if(lookTok.isType("VAR")) {
			declaration();
		}
		else if(lookTok.isType("PRINT")) {
			printStatement();
		}
		else {
			
			expected("Statement");
		}
	}

	// <printStatement> ::= PRINT ( <expression> ) ;
	private static void printStatement() {
		match("PRINT");
		match("OPEN_PAREN");
		expression();
		// now the result is in %rax
		machine.printFunc();
		match("CLOSE_PAREN");
		match("SEMICOLON");
	}


	// <declaration> ::= VAR <assignment>
	private static void declaration() {
		
		match("VAR");
		String id = lookTok.value;
		getTok();
		machine.addGlobalVarToTable(id);
		match("EQUALS");
		boolExpression();
		match("SEMICOLON");
		machine.store(id);

	}
	
	// <assignment> ::= IDENTIFIER = <bool-expression> ;
	private static void assignment() {
		
		String id = lookTok.value;
		getTok();
		match("EQUALS");
		boolExpression();
		match("SEMICOLON");
		machine.store(id);
	}
	
	
	// <while-statement> ::= WHILE ( <bool-expression> ) <block>
	private static void whileStatement() {
		
		match("WHILE");
		String L1 = machine.newLabel();
		String L2 = machine.newLabel();
		
		machine.postLabel(L1); // start loop here

		match("OPEN_PAREN");
		boolExpression();  // evaluate the expression
		match("CLOSE_PAREN");
		
		machine.jumpToLabelIfFalse(L2);  // if false, then bypass the block entirely
		block();
		machine.jumpToLabel(L1);
		machine.postLabel(L2);
	}
	
	
	// <if-statement> ::= IF ( <bool-expression> ) <block> [ELSE <block>]*
	private static void ifStatement() {
		match("IF");
		
		match("OPEN_PAREN");
		boolExpression();
		match("CLOSE_PAREN");
		
		String label_1 = machine.newLabel();
		String label_2 = label_1;
		
		/* if the expression evaluates to false (zero), then jump 
		 directly to the "else" block. */
		machine.jumpToLabelIfFalse(label_1);
		
		// otherwise execute the main block
		block();
		
		if(lookTok.isType("ELSE")) {
			match("ELSE");
			label_2 = machine.newLabel();
			machine.jumpToLabel(label_2);
			
			machine.postLabel(label_1);
			block();
		}
		
		machine.postLabel(label_2);
	}
	
	
	// <block> ::= { [<statement>]*}
	private static void block() {
		match("OPEN_BRACE");
		while(!lookTok.isType("CLOSE_BRACE"))
			statement();
		match("CLOSE_BRACE");
	}
	
	
	///// UTILITY METHODS
	
	private static void getTok() {
		if(pos >= tokenList.size()) {
			lookTok = new Token("EOF");
		}
		else {
			pos++;
			lookTok = tokenList.get(pos - 1);
		}
	}
	
	private static void match(String type) {
		if(lookTok.isType(type)) {
			getTok();
		}
		else {
			expected(type);
		}
	}
	
	private static void expected(String type) {
		System.out.printf("Expected type: %s, Encountered: %s\n", type, lookTok.type);
		System.exit(0);
	}
	
	/************************************************************/
	
	// Boolean things begin here
	
	private static void boolExpression() {
		
		boolTerm();
		
		while(lookTok.isType("PIPE_PIPE") || lookTok.isType("CARROT")) {
			machine.push();
			if(lookTok.isType("PIPE_PIPE")) {
				boolOr();
			}
			else if(lookTok.isType("CARROT")) {
				boolXor();
			}
		}
	}
	
	private static void boolTerm() {
		notFactor();
		
		while(lookTok.isType("AND_AND")) {
			machine.push();
			match("AND_AND");
			notFactor();
			// jack says to put a pop here but I'm not sure
		}
	}
	
	private static void notFactor() {
		if(lookTok.isType("BANG")) {
			match("BANG");
			boolFactor();
			machine.logNot();
		}
		else {
			boolFactor();
		}
	}
	
	private static void boolFactor() {
		if(lookTok.isType("TRUE")) {
			machine.loadConstant(1);
		}
		else if(lookTok.isType("FALSE")) {
			machine.loadConstant(0);
		}
		else {
			relation();
		}
	}
	
	private static void relation() {
		expression();
		
		
		if(lookTok.isType("EQUALS_EQUALS")) {
			machine.push();
			equals();
		}
		else if(lookTok.isType("BANG_EQUALS")) {
			machine.push();
			notEquals();
		}
		else if(lookTok.isType("LESS")) {
			machine.push();
			less();
		}
		else if(lookTok.isType("GREATER")) {
			machine.push();
			greater();
		}
	}
	
	private static void equals() {
		match("EQUALS_EQUALS");
		expression();
		machine.popEq();
	}
	
	private static void notEquals() {
		match("BANG_EQUALS");
		expression();
		machine.popNotEq();
	}
	
	private static void less() {
		match("LESS");
		expression();
		machine.popLess();
	}
	
	private static void greater() {
		match("GREATER");
		expression();
		machine.popGreater();
	}
	
	
	private static void boolOr() {
		match("PIPE_PIPE"); // i'll go ahead and use the double or, since I may implement bitwise ops later
		boolTerm();
		machine.popOr();
	}
	
	private static void boolXor() {
		match("CARROT");
		boolTerm();
		machine.popXor();
	}
	
	
	// boolean things end here
	
	private static void ident() {
		String name = lookTok.value;
		getTok();
		
		if(lookTok.isType("OPEN_PAREN")) {
			match("OPEN_PAREN");
			match("CLOSE_PAREN");
			machine.call(name);
		}
		else {
			machine.loadVariable(name);
		}
		
	}
	
	private static void factor() {
		if(lookTok.isType("OPEN_PAREN")) {
			match("OPEN_PAREN");
			expression();
			match("CLOSE_PAREN");
		}
		else if (lookTok.isType("IDENTIFIER")) {
			ident();
		}
		else if (lookTok.isType("INT")){
			
			int val = Integer.parseInt(lookTok.value);
			match("INT");
			machine.loadConstant(val);
		}
		else if (lookTok.isType("TRUE")) {
			machine.loadConstant(1);
		}
		else if (lookTok.isType("FALSE")) {
			machine.loadConstant(0);
		}
		else
			expected("factor");
	}
	
	
	private static void signedFactor() {
		if(lookTok.isType("PLUS")) {
			getTok();
		}
		if(lookTok.isType("MINUS")) {
			
			getTok();
			if(lookTok.isType("INT")) {
				int val = Integer.parseInt(lookTok.value);
				match("INT");
				machine.loadConstant(-val);
			}
			else {
				factor();
				machine.negate();
			}
		}
		else
			factor();
	}
	
	private static void expression() {
		
		term();
		while(lookTok.isType("PLUS") || lookTok.isType("MINUS")) {
			if(lookTok.isType("PLUS")){
				machine.push();
				add();
			}
			else if(lookTok.isType("MINUS")) {
				machine.push();
				subtract();
			}
		}
	}
	
	private static void term() {
		signedFactor();
		while(lookTok.isType("STAR") || lookTok.isType("SLASH")) {
			if(lookTok.isType("STAR")){
				machine.push();
				multiply();
			}
			else if(lookTok.isType("SLASH")) {
				machine.push();
				divide();
			}
		}
	}
	
	
	
	
	private static void divide() {
		
		match("SLASH");
		factor();
		machine.popDiv();
	}
	
	private static void multiply() {
		
		match("STAR");
		factor();
		machine.popMul();
	}
	
	private static void add() {
		
		match("PLUS");
		term();
		machine.popAdd();
	}
	
	private static void subtract() {
		match("MINUS");
		term();
		machine.popSub();
	}
	
}