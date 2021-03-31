package Decision_Tree;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;


public class DataSet {

	
	public DataSet() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * function that get data from file
	 * @param _path :path of file
	 * @return data 
	 */
	
	public static ArrayList<HashMap<String,Double>> getFileData(String _path){
		
		
		
		Path path=Paths.get(_path);

		String[] lignes;
		try {
			lignes = Files.readAllLines(path).toArray(new String [] {});
		} catch (IOException e) {
			
			lignes=null;
			
			e.printStackTrace();
		}
		
			
		
			// TODO Auto-generated catch block
		

		ArrayList<HashMap<String,Double>> all=new ArrayList<>();

		HashMap<String,Double> map2=new HashMap<>() ;
		map2.put("Iris-setosa",1.0);
		map2.put("Iris-versicolor",2.0);
		map2.put("Iris-virginica",3.0);

		for(int i=0;i<lignes.length;i++) {
			HashMap<String,Double> map = new HashMap<>();
			
			
			if(lignes[i].isEmpty())
				continue;
			String [] t=lignes[i].split(",");
		
			map.put("classification",map2.get(t[4]));
			map.put("petalWidth",Double.parseDouble(t[3]));
				
			map.put("sepalWidth",Double.parseDouble(t[1]));
			
			map.put("petalLength",Double.parseDouble(t[2]));
		
			map.put("sepalLength",Double.parseDouble(t[0]));
			
		
			
			all.add(map);
			
		}

		//System.out.println(all.get(58).get("irisClass"));


		return all;

	
			}
public static void buildTree(String _path,String varibaleType,String critere) {
	
	//read the file of data 
	
	 ArrayList<HashMap<String,Double>> fileData = getFileData(_path);
	 //condition d'arret :tous les noeud doievent etre pure =>entropie=0
	 String a=getCondition(fileData);
	 
	
	
	
	//return null;
	
}
public static String getCondition(ArrayList<HashMap<String,Double>> data) {
	/**
	 * calcule entropie pour la data 
	 * et renvoyer la condition correspandante 
	 */
	ArrayList<String> attributs=new ArrayList<>();
	for (Entry<String, Double> entry : data.get(0).entrySet()) {
		attributs.add(entry.getKey());
   
    }
	System.out.println(data.get(0));
	System.out.println(data.size());
	attributs.remove("classification");
	double median;
	double moyenne;
	System.out.println(attributs);
	
	for(String attribut:attributs) {
	
		List<Double> allAttributValues=data.stream().map(e -> e.get(attribut)).collect(Collectors.toList());
	
		System.out.println("element avant tri"+allAttributValues.get(0));
		Collections.sort(allAttributValues);
	
		System.out.println("element après tri"+allAttributValues.get(0));
		 median=median(allAttributValues);
		 //contruction of 2 table one contains ellements< median and the other contains elements >median
		 moyenne=mean(allAttributValues);
		 System.out.println("attribut pris:"+attribut);
		 
		
		System.out.println("median="+median);
		System.out.println("moyenne="+moyenne);
		
	}
	return null;
	
}

public static double mean(List<Double> m) {
	
	
    int taille = m.size();
    double somme=0;
    
    	
    	System.out.println(m.size());
    for(double s:m) {
    	somme=somme+s;
    }
    return somme/taille;
   
}


public static double median(List<Double> m) {
	
	
    int middle = m.size()/2;
    System.out.println("middle\t"+middle);
    System.out.println(m.size());
    if (m.size()%2 == 1) {
        return m.get(middle);
    } else {
        return ((m.get(middle-1) + m.get(middle) )/ 2.0);
    }
}



	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		/**
		 * upload the file
		 */
		

		
		buildTree("C:\\Users\\elake\\OneDrive\\Bureau\\M2Miage\\dataanalyse\\projet_graphe\\iris (1).csv","", "");
		
	}
}

