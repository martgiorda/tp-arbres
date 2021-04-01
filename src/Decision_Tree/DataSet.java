package Decision_Tree;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.lang.Math.*;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

//import jdk.javadoc.internal.doclets.formats.html.AllClassesIndexWriter;


public class DataSet {

	static String [] arbre;


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




		return all;


	}
	public static void buildTree(String _path,String varibaleType,String critere) {

		//read the file of data 

		//arbre =tableau


		ArrayList<HashMap<String,Double>> fileData = getFileData(_path);
		arbre=new String [1000];

		addNode(fileData,0) ;
		//return null;
		System.out.println(Arrays.asList(arbre));
		
		displayChilds(0,1);
		
	}
	
	public static void displayChilds(int index, int offset) {
		
		/*
		if (index == 0) {
			System.out.print("root :"+arbre[index]);
		}
		else {
			System.out.print("index"+index+":"+arbre[index]);
		}
		*/
		
		System.out.print(arbre[index]);
		
		if ((index*2) +1 <= 1000) {
			if (arbre[(index*2) +1] != null) {
				System.out.println();
				for (int i=0;i<offset;i++) {
					System.out.print("\t");
				}
				
				displayChilds((index*2) +1, offset+1);
			}	
			if (arbre[(index*2) +2] != null) {
				System.out.println();
				for (int i=0;i<offset;i++) {
					System.out.print("\t");
				}
				
				displayChilds((index*2) +2, offset+1);
				
			}	
		}
		
		
	}
	
	public static String getCondition(ArrayList<HashMap<String,Double>> data) {
		/**
		 * calcule entropie pour la data 
		 * et renvoyer la condition correspandante 
		 */
		double median;

		double gain= -1;

		String maxCondition="";
		
		double maxGain= -1;
		
		String condition=null;


		ArrayList<String> attributs=new ArrayList<>();
		for (Entry<String, Double> entry : data.get(0).entrySet()) {
			attributs.add(entry.getKey());

		}

		attributs.remove("classification");

		for(String attribut:attributs) {
			ArrayList<HashMap<String,Double>> filsGauche = new ArrayList<>();
			ArrayList<HashMap<String,Double>> filsDroit =  new ArrayList<>();

			List<Double> allAttributValues=data.stream().map(e -> e.get(attribut)).collect(Collectors.toList());


			Collections.sort(allAttributValues);

			
			
			median=median(allAttributValues);
			//median=mean(allAttributValues);
			
			
			condition=attribut+"<="+median;

			

			for(HashMap<String, Double>dataLigne:data) {

				
				if(dataLigne.get(attribut)<=median) {
					filsGauche.add(dataLigne);

				}

				else {
					filsDroit.add(dataLigne);

				}


			}


			gain=getGain(data,filsGauche,filsDroit);
			
			if( gain>maxGain) {
				
				
				
				maxGain=gain;
				maxCondition=condition;
				
			

			}
		}
		
		
		return maxCondition;

	}

	public static double mean(List<Double> m) {


		int taille = m.size();
		double somme=0;


		//System.out.println(m.size());
		for(double s:m) {
			somme=somme+s;
		}
		return somme/taille;

	}
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static double getEntropie(ArrayList<HashMap<String,Double>> data) {

		double entropie =-1;
		double pk=0;
		int tailleData=data.size();

		HashMap<Double, Integer>classificationRef=new HashMap<>();
		ArrayList<Double> allClassification=new ArrayList<>();

		for(HashMap<String,Double>element:data) {


			if(classificationRef.get(element.get("classification"))!=null) {

				classificationRef.put(element.get("classification"),classificationRef.get(element.get("classification"))+1);


			}
			else {

				//identification d'une nouvelle classe 
				classificationRef.put(element.get("classification"),1);

				allClassification.add(element.get("classification"));
			}
		}

		for (double classification : allClassification) {

			//pk=classification/tailleData;
			
			pk=(double) (classificationRef.get(classification))/tailleData;
			
			/*System.out.println("classificationRef.get : "+classificationRef.get(classification));
			System.out.println("taille : "+tailleData);
			System.out.println(pk);*/
				
			entropie -= pk*Math.log(pk);
 
		}


		return entropie ;

	}
	public static double getGain(ArrayList<HashMap<String,Double>> data,ArrayList<HashMap<String,Double>>filsGauche,ArrayList<HashMap<String,Double>>filsDroit) {

		return getEntropie(data)-(filsGauche.size()*getEntropie(filsGauche)/data.size())-(filsDroit.size()*getEntropie(filsDroit)/data.size());



	}


	public static double median(List<Double> m) {


		int middle = m.size()/2;
		//System.out.println("middle\t"+middle);
		// System.out.println(m.size());
		if (m.size()%2 == 1) {
			return m.get(middle);
		} else {
			return ((m.get(middle-1) + m.get(middle) )/ 2.0);
		}
	}


	public static void addNode(ArrayList<HashMap<String,Double>> data,int indexcurrent) {
		//System.out.println(data.size()+" indexcurrent= "+indexcurrent);

		if(data.size()==0)
			return;
		ArrayList<HashMap<String,Double>> donneesGauche = new ArrayList<>();
		ArrayList<HashMap<String,Double>> donneesDroite =  new ArrayList<>();
		//condition d'arret :tous les noeud doievent etre pure =>entropie=0

		String a=getCondition(data);
		
		arbre[indexcurrent]=a;
		
		if (data.size()==1) {
			return;
		}
		
		
		HashMap<Double,String> mapClass=new HashMap<>() ;
		mapClass.put(1.0,"Iris-setosa");
		mapClass.put(2.0,"Iris-versicolor");
		mapClass.put(3.0,"Iris-virginica");


		String attribut =a.split("<=")[0];
		double value=Double.parseDouble(a.split("<=")[1]);
		

		
		for(HashMap<String,Double> ligne:data) {
			
			
			
			if(ligne.get(attribut) <= value) {
				donneesGauche.add(ligne);
				

			}

			else {
				donneesDroite.add(ligne);
				
			}
		}
		
		double differentClassGauche = donneesGauche.stream().map(e -> e.get("classification")).distinct().count();
		double differentClassDroite = donneesDroite.stream().map(e -> e.get("classification")).distinct().count();
		
		if (differentClassGauche == 1) {
			
			arbre[(2*indexcurrent)+1]=mapClass.get(donneesGauche.get(0).get("classification"));
			
		}
		else {
			
			addNode(donneesGauche, (indexcurrent*2)+1);
		}
		
		if (differentClassDroite == 1) {	
			
			
			arbre[(2*indexcurrent)+2]=mapClass.get(donneesDroite.get(0).get("classification"));
			
		}
		else {
			
			addNode(donneesDroite, (indexcurrent*2)+2);
		}

	}





	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		/**
		 * upload the file
		 */



		buildTree("D:\\Téléchargements\\iris.data","", "");

	}
}

