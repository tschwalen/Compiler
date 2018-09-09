
public interface Machine {
	
	public void clear();
	
	public void negate();
	
	public void loadConstant(int n);
	
	public void loadVariable(String identifier);
	
	public void push();
	
	public void popAdd();
	
	public void popSub();
	
	public void popMul();
	
	public void popDiv();
	
	public void popOr();
	
	public void popXor();
	
	public void popAnd();
	
	public void logNot();
	
	public void popEq();
	
	public void popNotEq();
	
	public void popLess();
	
	public void popGreater();
	
	public void store(String identifier);
	
	public void call(String identifier);
	
	public void testReturnValue();
	
	public void jumpToLabelIfFalse(String label);
	
	public void jumpToLabel(String label);
	
	public String newLabel();
	
	public void postNewLabel();
	
	public void postLabel(String s);

	public void addGlobalVarToTable(String s);

	public void writeSymbolTable();
}