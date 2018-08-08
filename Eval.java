
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
		
		getTok();
		statement();
		
	}
	
	/*******************************************************************/
	
	
	// <statement> ::= <assignment>
	private static void statement() {
		
		if(lookTok.isType("IDENTIFIER")) {
			assignment();
		}
		else {
			expected("Identifier");
		}
	}
	
	private static void assignment() {
		
		String id = lookTok.value;
		getTok();
		match("EQUALS");
		expression();
		machine.store(id);
	}
	
	
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
		
		if(lookTok.isType("PLUS")){
			machine.push();
			add();
		}
		else if(lookTok.isType("MINUS")) {
			machine.push();
			subtract();
		}
	}
	
	private static void term() {
		signedFactor();
		
		if(lookTok.isType("STAR")){
			machine.push();
			multiply();
		}
		else if(lookTok.isType("SLASH")) {
			machine.push();
			divide();
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