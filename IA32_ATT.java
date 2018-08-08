
public class IA32_ATT implements Machine {
	
	private int labelCount;
	
	public IA32_ATT() {
		labelCount = 0;
	}
	
	public void call(String identifier) {
		emitLine("call " + identifier);
	}
	
	public void clear() {
		emitLine("xorl   %eax, %eax");
	}
	
	public void negate() {
		emitLine("negl   %eax");
	}
	
	public void loadConstant(int n) {
		emitLine("movl   $" + n + ", %eax");
	}
	
	public void loadVariable(String identifier) {
		emitLine("movl   " + identifier + "(%eip), %eax");
	}
	
	public void push() {
		emitLine("pushl  %eax");
	}
	
	public void popAdd() {
		emitLine("popl   %ebx");
		emitLine("addl   %ebx, %eax");
	}
	
	public void popSub() {
		emitLine("popl   %ebx");
		emitLine("subl   %ebx, %eax");
		negate();
	}
	
	public void popMul(){
		emitLine("popl   %ebx");
		emitLine("imull  %ebx, %eax");
	}
	
	public void popDiv() {
		emitLine("popl   %ebx");
		emitLine("idivl  %eax, %ebx");
		emitLine("movl   %ebx, %eax");
	}
	
	public void popOr() {
		emitLine("popl   %edx");		// load left op into edx
		emitLine("movl   %eax, %ecx");  // load right op into ecx
		
		emitLine("xorl   %eax, %eax");  // zero out return register
		
		emitLine("testl  %edx, %edx");	// check if left op is nonzero
		emitLine("setne  %al");			
		emitLine("xorl   %edx, %edx");  
		
		emitLine("testl  %ecx, %ecx");
		emitLine("setne  %dl");
		
		emitLine("orl    %edx, %eax");
	}
	
	// this is logical xor instead of bitwise
	public void popXor() {
		emitLine("popl   %edx");		
		emitLine("movl   %eax, %ecx");  
		
		emitLine("xorl   %eax, %eax");  
		
		emitLine("testl  %edx, %edx");	
		emitLine("setne  %al");			
		emitLine("xorl   %edx, %edx");  
		
		emitLine("testl  %ecx, %ecx");
		emitLine("setne  %dl");
		
		emitLine("xorl   %edx, %eax");
	}
	
	// && the top of the stack pointer with eax
	// does not short circuit yet
	public void popAnd() {
		emitLine("popl   %edx");		
		emitLine("movl   %eax, %ecx");  
		
		emitLine("xorl   %eax, %eax");  
		
		emitLine("testl  %edx, %edx");	
		emitLine("setne  %al");			
		emitLine("xorl   %edx, %edx");  
		
		emitLine("testl  %ecx, %ecx");
		emitLine("setne  %dl");
		
		emitLine("andl   %edx, %eax");
	}
	
	// logically negate whatever is in the return register
	public void logNot() {
		emitLine("movl   %eax, %edx");
		emitLine("xorl   %eax, %eax");
		emitLine("testl  %edx, %edx");  // is edx nonzero?
		emitLine("sete   %al");    	   // if zero flag is raised , set return register to 1,
									   // otherwise set to 0
	}
	
	
	public void popEq() {
		
		emitLine("popl   %edx");
		emitLine("movl   %eax, %ecx");
		emitLine("xor    %eax, %eax");
		emitLine("testl  %edx, %ecx");
		emitLine("sete   %al");
	}
	
	public void popNotEq() {
		
		emitLine("popl   %edx");
		emitLine("movl   %eax, %ecx");
		emitLine("xor    %eax, %eax");
		emitLine("testl  %edx, %ecx");
		emitLine("setne  %al");
	}
	
	public void popLess() {
		emitLine("popl   %edx");
		emitLine("movl   %eax, %ecx");
		emitLine("xor    %eax, %eax");
		emitLine("cmpl   %edx, %ecx");
		emitLine("setl   %al");
	}
	
	public void popGreater() {
		emitLine("popl   %edx");
		emitLine("movl   %eax, %ecx");
		emitLine("xor    %eax, %eax");
		emitLine("cmpl   %edx, %ecx");
		emitLine("setg   %al");
	}
	
	public void testReturnValue() {
		emitLine("testl  %eax, %eax");
	}
	
	public void jumpToLabelIfFalse(String label) {
		testReturnValue();
		emitLine("je   " + label);
	}
	
	public void jumpToLabel(String label) {
		testReturnValue();
		emitLine("jmp " + label);
	}
	
	
	public void store(String identifier){
		emitLine("movl   %eax, " + identifier + "(%eip)");
	}
	
	public void emitLine(String s){
		System.out.printf("\t%s\n", s);
	}
	
	public String newLabel() {
		labelCount++;
		return "L" + (labelCount - 1);
	}
	
	public void postNewLabel() {
		System.out.println(newLabel() + ":");
	}
	
	public void postLabel(String s) {
		System.out.println(s + ":");
	}
}