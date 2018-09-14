import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.io.IOException;


public class Comp {
	
	public static void main(String[] args) {
		String input = "var a = 4;\nif(a != 4) {\n  a = 3;\n}\nelse{\n  a = a + 1;\n}";
		
		String input2 = "var my_var = 12;\nvar other_var = 17;\nmy_var = (15 * other_var) - (other_var + 6);";

		//System.out.printf("\nInput:\n%s\n\nGenerated Object Code:\n", input2);
		
		if(args.length != 0) {
			try {
				input = readFile(args[0], Charset.forName("UTF-8"));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		Eval.parseTokens(Lexer.lexInputString(input));
	}

	static String readFile(String path, Charset encoding) throws IOException {
  		byte[] encoded = Files.readAllBytes(Paths.get(path));
  		return new String(encoded, encoding);
	}
}