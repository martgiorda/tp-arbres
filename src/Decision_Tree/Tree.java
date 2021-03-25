package Decision_Tree;
import java.util.ArrayList;
public class Tree {

	NodeTree root ;
	int nbr_noeud;
	ArrayList<NodeTree> tree = new ArrayList<NodeTree>();
	
	public Tree() {
		// TODO Auto-generated constructor stub
		
		
	}
	
	public Tree(NodeTree root,int nbr_noeud) {
		// TODO Auto-generated constructor stub
		this.root=root;
		this.nbr_noeud=nbr_noeud;
		tree.add(root);
		
	}
	
	public void addNode(NodeTree newNode,NodeTree rootExplore) {
		/**
		 * in this example i want to build binary tree with numeric values ,
		 * to do that i will test every time if the new node that i want to add is lower or higher
		 * if it is lower i will put it in the left side else i will add it in the right one 
		 */
		if (rootExplore==null)
			return ;
		if(newNode.getValue()>=rootExplore.getValue()) {
			
			if(rootExplore.getRight()==null) {
				rootExplore.setRight(rootExplore.getRight());
			tree.add(newNode);}
			/**
			 * if the next node is not empty we  backtrack the tree until find the empty one 
			 */
			else addNode(newNode, rootExplore.getRight());
			
		}
		if(newNode.getValue()<=rootExplore.getValue()) {
			if(rootExplore.getLeft()==null) {
				rootExplore.setRight(rootExplore.getLeft());
				tree.add(newNode);}
			else addNode(newNode, rootExplore.getLeft());
			
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
