
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
	
	public void store(String identifier);
	
	public void call(String identifier);
}