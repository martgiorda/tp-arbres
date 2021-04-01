package Decision_Tree;

public class NodeTree {
/**
 * in this class we creat a nod of tree with diferents attribut 
 */
	String contenu ; 
	
public	NodeTree left ;
	public NodeTree right ; 
	
	
	
	

	
	public NodeTree(String contenu,
			NodeTree left, NodeTree right) {
		super();
		this.contenu = contenu;
	
		this.left = left;
		this.right = right;
	}
	
	
	public NodeTree() {
		//contructeur 
	}
	
	



	/**
	 * getters and setters 
	 */
	


}
