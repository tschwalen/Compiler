
public class IA32_ATT implements Machine {
	
	public IA32_ATT() {}
	
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
	
	public void store(String identifier){
		emitLine("movl   %eax, " + identifier + "(%eip)");
	}
	
	public void emitLine(String s){
		System.out.printf("\t%s\n", s);
	}
}