import java.util.HashMap;
import java.util.List;

public class IA32_ATT implements Machine {
	
	private int labelCount;

	static HashMap<String, Symbol> globalTable;

	static List<HashMap<String, Symbol>> localTables;
	
	public IA32_ATT() {
		emitLine(".file	\"output.s\"");
		emitLine(".section	.rodata.str1.1,\"aMS\",@progbits,1");
		System.out.println(".LC0:");
		emitLine(".string	\"\\n%d\\n\"");
		emitLine(".text");
		emitLine(".globl  main");
		emitLine(".type	main, @function");
		System.out.println("main:");
		System.out.println(".LFP23:");
		emitLine(".cfi_startproc");
		emitLine("subq	$8, %rsp");
		emitLine(".cfi_def_cfa_offset 16");


		labelCount = 0;
		globalTable = new HashMap<>();
	}
	
	public void call(String identifier) {
		emitLine("call " + identifier);
	}
	
	public void clear() {
		emitLine("xorq   %rax, %rax");
	}
	
	public void negate() {
		emitLine("negq   %rax");
	}
	
	public void loadConstant(int n) {
		emitLine("movq   $" + n + ", %rax");
	}
	
	public void loadVariable(String identifier) {
		emitLine("movq   " + identifier + "(%rip), %rax");
	}
	
	public void push() {
		emitLine("pushq  %rax");
	}
	
	public void popAdd() {
		emitLine("popq   %rbx");
		emitLine("addq   %rbx, %rax");
	}
	
	public void popSub() {
		emitLine("popq   %rbx");
		emitLine("subq   %rbx, %rax");
		negate();
	}
	
	public void popMul(){
		emitLine("popq   %rbx");
		emitLine("imulq  %rbx, %rax");
	}
	
	public void popDiv() {
		emitLine("popq   %rbx");
		emitLine("idivq  %rax, %rbx");
		emitLine("movq   %rbx, %rax");
	}
	
	public void popOr() {
		emitLine("popq   %rdx");		// load left op into rdx
		emitLine("movq   %rax, %rcx");  // load right op into rcx
		
		emitLine("xorq   %rax, %rax");  // zero out return register
		
		emitLine("testq  %rdx, %rdx");	// check if left op is nonzero
		emitLine("setne  %al");			
		emitLine("xorq   %rdx, %rdx");  
		
		emitLine("testq  %rcx, %rcx");
		emitLine("setne  %dl");
		
		emitLine("orl    %rdx, %rax");
	}
	
	// this is logical xor instead of bitwise
	public void popXor() {
		emitLine("popq   %rdx");		
		emitLine("movq   %rax, %rcx");  
		
		emitLine("xorq   %rax, %rax");  
		
		emitLine("testq  %rdx, %rdx");	
		emitLine("setne  %al");			
		emitLine("xorq   %rdx, %rdx");  
		
		emitLine("testq  %rcx, %rcx");
		emitLine("setne  %dl");
		
		emitLine("xorq   %rdx, %rax");
	}
	
	// && the top of the stack pointer with rax
	// does not short circuit yet
	public void popAnd() {
		emitLine("popq   %rdx");		
		emitLine("movq   %rax, %rcx");  
		
		emitLine("xorq   %rax, %rax");  
		
		emitLine("testq  %rdx, %rdx");	
		emitLine("setne  %al");			
		emitLine("xorq   %rdx, %rdx");  
		
		emitLine("testq  %rcx, %rcx");
		emitLine("setne  %dl");
		
		emitLine("andl   %rdx, %rax");
	}
	
	// logically negate whatever is in the return register
	public void logNot() {
		emitLine("movq   %rax, %rdx");
		emitLine("xorq   %rax, %rax");
		emitLine("testq  %rdx, %rdx");  // is rdx nonzero?
		emitLine("sete   %al");    	   // if zero flag is raised , set return register to 1,
									   // otherwise set to 0
	}
	
	
	public void popEq() {
		
		emitLine("popq   %rdx");
		emitLine("movq   %rax, %rcx");
		emitLine("xorq   %rax, %rax");
		emitLine("testq  %rdx, %rcx");
		emitLine("sete   %al");
	}
	
	public void popNotEq() {
		
		emitLine("popq   %rdx");
		emitLine("movq   %rax, %rcx");
		emitLine("xorq   %rax, %rax");
		emitLine("testq  %rdx, %rcx");
		emitLine("setne  %al");
	}
	
	public void popLess() {
		emitLine("popq   %rdx");
		emitLine("movq   %rax, %rcx");
		emitLine("xorq   %rax, %rax");
		emitLine("cmpq   %rdx, %rcx");
		emitLine("setl   %al");
	}
	
	public void popGreater() {
		emitLine("popq   %rdx");
		emitLine("movq   %rax, %rcx");
		emitLine("xorq   %rax, %rax");
		emitLine("cmpq   %rdx, %rcx");
		emitLine("setg   %al");
	}
	
	public void testReturnValue() {
		emitLine("testq  %rax, %rax");
	}
	
	public void jumpToLabelIfFalse(String label) {
		testReturnValue();
		emitLine("jne   " + label);
	}

	public void jumpToLabelIfTrue(String label) {
		testReturnValue();
		emitLine("jne   " + label);
	}
	
	public void jumpToLabel(String label) {
		testReturnValue();
		emitLine("jmp " + label);
	}
	
	
	public void store(String identifier){
		emitLine("movq   %rax, " + identifier + "(%rip)");
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

	public void addGlobalVarToTable(String s){
		globalTable.put(s, new Symbol());
	}

	public void writeSymbolTable() {

		for(String id : globalTable.keySet()){
			System.out.printf("%s:\n", id);
			System.out.printf("\t.zero 8\n");
		}
	}

	public void print() {
		emitLine("movq   %rax, %rdx");
		emitLine("movq	$.LC0, %rsi");
		emitLine("movq	$1, %rdi");
		emitLine("movq	$0, %rax");
		emitLine("call	__printf_chk");
		emitLine("movq	$0, %rax");
		emitLine("addq	$8, %rsp");
		emitLine(".cfi_def_cfa_offset 8");
		emitLine("ret");
		emitLine(".cfi_endproc");
		System.out.println(".LFE23:");
		emitLine(".size	main, .-main");

		emitLine(".data");
		emitLine(".align 8");
	}
}
