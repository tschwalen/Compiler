



public class Symbol {


	int stackOffSet;
	boolean global;



	public Symbol(boolean global){
		this.global = global;
		stackOffSet = 0;
	}	

	public Symbol(int stackOffSet) {
		this.stackOffSet = stackOffSet;
	}
	
}