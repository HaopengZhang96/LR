package bean;

public class Derivation {
	private String left;
	private String right;
	public String getLeft() {
		return left;
	}
	public void setLeft(String left) {
		this.left = left;
	}
	public String getRight() {
		return right;
	}
	public void setRight(String right) {
		this.right = right;
	}
	@Override
	public boolean equals(Object otherObject){
		if(this == otherObject) return true;
		if(otherObject ==null) return false;
		if(getClass()!=otherObject.getClass())
			return false;
		Derivation other=(Derivation) otherObject;
		return left.equals(other.left)&&right.equals(other.right);
	}
}
