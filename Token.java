public class Token {
	
	String type;
	String value;
	
	
	
	public Token(String type, String value){
		this.type = type;
		this.value = value;
	}
	
	public Token(String type){
		this.type = type;
		value = "_nonvalue_";
	}
	
	public boolean isType(String otherType){
		return type.toUpperCase().equals(otherType.toUpperCase());
	}
	
	
	public String toString() {
		return "[Type : " + type + ", Value : " + value + "]";
	}
}