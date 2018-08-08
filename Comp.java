
public class Comp {
	
	public static void main(String[] args) {
		String input = "if(a == 4) { a = 4; } else { a = a + 1; }";
		
		System.out.printf("\nInput:\n%s\n\nGenerated Object Code:\n", input);
		
		Eval.parseTokens(Lexer.lexInputString(input));
		
		
		
	}
}