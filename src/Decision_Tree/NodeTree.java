package Decision_Tree;

public class NodeTree {
/**
 * in this class we creat a nod of tree with diferents attribut 
 */
	int value ; 
public	NodeTree left ;
	public NodeTree right ; 
	
	public NodeTree(int value, NodeTree left,NodeTree right) {
		// TODO Auto-generated constructor stub
	this.value=value;
	this.left=left;
	this.right=right;
	}
	
	/**
	 * getters and setters 
	 */

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public NodeTree getLeft() {
		return left;
	}

	public void setLeft(NodeTree left) {
		this.left = left;
	}

	public NodeTree getRight() {
		return right;
	}

	public void setRight(NodeTree right) {
		this.right = right;
	}
	
	
	
	
	
	
	

}
