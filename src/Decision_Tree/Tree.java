package Decision_Tree;
import java.util.ArrayList;
import java.util.HashMap;
public class Tree {

	NodeTree root ;
	int nbr_noeud;
	ArrayList<NodeTree> tree = new ArrayList<NodeTree>();
	
	public Tree() {
		// TODO Auto-generated constructor stub
		
		
	}
	
	public Tree(NodeTree root) {
		// TODO Auto-generated constructor stub
		this.root=root;
		
		tree.add(root);
		
	}
	
	
		
		
		
		
		
		
		
		
		
		
	}
	/**
	 * method to construct tree from number of node 
	 * @param i number of node in the tree
	 * @param root value of root
	 * @return buildtree
	 */
public Tree buidTree(int n,NodeTree root) {
	/**
	 * creat of tree
	 */
	Tree tr=new Tree(root,n);
	
	for( int i=1;i<n;i++) {
		NodeTree newNode=new NodeTree(i, null,null);
		tr.addNode(newNode, tr.root);
	}
	return tr ;
}

	

}
