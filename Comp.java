
public class Comp {
	
	public static void main(String[] args) {
		String input = "var1 = 5";
		
		Eval.parseTokens(Lexer.lexInputString(input));
		
	}
}