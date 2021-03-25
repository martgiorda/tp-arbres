package Decision_Tree;
import java.util.*;


public class App_tree {

	
	


	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int val_racine;
		int n_noeud;
		NodeTree n ;
		
		
		
		//saisie
		Scanner sc =new Scanner(System.in);
		System.out.println("entrer la valeur de racine");
		val_racine=sc.nextInt();
		NodeTree root=new NodeTree(val_racine, null, null);
		System.out.println("entrer le nombre de noeud ");
		n_noeud=sc.nextInt();
		Tree tr=new Tree().buidTree(n_noeud, root);
		//sc.close();
		for(int i=1;i<n_noeud;i++) {
		
		System.out.println("entrer la valeur du"+i+" noeud");
		 n=new NodeTree(sc.nextInt(), null, null);
		 tr.addNode(n, root);
		
		}
		 sc.close();
		/**
		 * affichage
		 */
	
		
		for(int i=1;i<tr.tree.size();i++) {
			
			System.out.println("noeud numero:"+i+"="+tr.tree.get(i).getValue());
		}
		
		
		
	}

}
