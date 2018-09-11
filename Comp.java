
public class Comp {
	
	public static void main(String[] args) {
		String input = "var a = 4;\nif(a != 4) {\n  a = 3;\n}\nelse{\n  a = a + 1;\n}";
		
		String input2 = "var my_var = 12;\nvar other_var = 17;\nmy_var = (15 * other_var) - (other_var + 6);";

		//System.out.printf("\nInput:\n%s\n\nGenerated Object Code:\n", input2);
		
		Eval.parseTokens(Lexer.lexInputString(input));
		
		
		
	}
}