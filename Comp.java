
public class Comp {
	
	public static void main(String[] args) {
		String input = "var a = 4;\nif(a == 4) {\n  a = 3;\n}\nelse{\n  a = a + 1;\n}";
		
		System.out.printf("\nInput:\n%s\n\nGenerated Object Code:\n", input);
		
		Eval.parseTokens(Lexer.lexInputString(input));
		
		
		
	}
}