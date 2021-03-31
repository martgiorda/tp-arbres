package Decision_Tree;

public class NodeTree {
/**
 * in this class we creat a nod of tree with diferents attribut 
 */
	String attribut1 ; 
	String attribut2;
	String attribut3;
	String attribut4;
	String classe;
public	NodeTree left ;
	public NodeTree right ; 
	
	
	
	

	
	public NodeTree(String attribut1, String attribut2, String attribut3, String attribut4, String classe,
			NodeTree left, NodeTree right) {
		super();
		this.attribut1 = attribut1;
		this.attribut2 = attribut2;
		this.attribut3 = attribut3;
		this.attribut4 = attribut4;
		this.classe = classe;
		this.left = left;
		this.right = right;
	}

	/**
	 * getters and setters 
	 */
	

	public NodeTree getLeft() {
		return left;
	}


	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
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
