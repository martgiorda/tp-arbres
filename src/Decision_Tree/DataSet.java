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
	static HashMap<Double,String> mapClass;
	static int classAttributeIndex;
	static String attributeToPredict;

	public DataSet() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * function that get data from file
	 * @param _path :path of file
	 * @return data 
	 */


	public static ArrayList<HashMap<String,Double>> getFileData(String _path, String attributeToPredict, String allAttributes){


		Path path=Paths.get(_path);
		
		ArrayList<String> lignes = new ArrayList<String>();
		
		try {
			lignes = (ArrayList<String>) Files.readAllLines(path);
		} catch (IOException e) {

			lignes=null;

			e.printStackTrace();
		}


		ArrayList<HashMap<String,Double>> all=new ArrayList<>();
		
		
		String [] split = allAttributes.split(",");
		
		for (int i=0; i<split.length ; i++)  {
			
			if (split[i].equals(attributeToPredict)) {
				classAttributeIndex = i;
			}
			
		}
		
		List<String> allClass = lignes.stream().filter(ligne -> !ligne.isEmpty()).map(ligne -> ligne.split(",")[classAttributeIndex]).distinct().sorted().collect(Collectors.toList());
		
		HashMap<String,Double> map2=new HashMap<>() ;
		mapClass=new HashMap<>() ;
		
		for (double i=1.0 ; i<=allClass.size() ; i++) {
			map2.put(allClass.get((int )i-1),i);
			mapClass.put(i,allClass.get((int )i-1));
		}
		
		
		
		
		List<String> allAttributesIndex = new ArrayList<>(Arrays.asList(allAttributes.split(",")));
		
		for(String ligne : lignes) {
			HashMap<String,Double> map = new HashMap<>();

			if(ligne.isEmpty())
				continue;
			
			String [] t = ligne.split(",");
			
			for (int i=0 ; i< allAttributesIndex.size() ; i++) {
				
				if (allAttributesIndex.get(i).equals(attributeToPredict)) {
					
					map.put(allAttributesIndex.get(i),map2.get(t[i]));
				}
				else {
					map.put(allAttributesIndex.get(i),Double.parseDouble(t[i]));
				}
				
			}

			all.add(map);

		}




		return all;


	}
	
	public static String [] buildTree(ArrayList<HashMap<String,Double>> fileData,String varibaleType,String critere) {

		//read the file of data 

		//arbre =tableau

		long startTime = System.nanoTime();    
		// ... the code being measured ...    
		
		arbre=new String [100000000];

		addNode(fileData,0) ;
		
		long timeToCalculate = System.nanoTime() - startTime;
		
		//return null;
		System.out.println("Time to calculate : "+timeToCalculate/1000/1000 + " ms" + " ou "+timeToCalculate/1000/1000/1000+ " s");
		
		//displayChildren(0,1);
		
		long timeToDisplay = System.nanoTime() - startTime;
		
		//return null;
		//System.out.println("");
		
		//System.out.println("Time after display : "+timeToDisplay/1000/1000+ " ms");
		
		return arbre;
		
	}
	
	public static void displayChildren(int index, int offset) {
		
		
		System.out.print(arbre[index]);
		
		if ((index*2) +1 <= 1000) {
			if (arbre[(index*2) +1] != null) {
				System.out.println();
				for (int i=0;i<offset;i++) {
					System.out.print("\t");
				}
				
				displayChildren((index*2) +1, offset+1);
			}	
			if (arbre[(index*2) +2] != null) {
				System.out.println();
				for (int i=0;i<offset;i++) {
					System.out.print("\t");
				}
				
				displayChildren((index*2) +2, offset+1);
				
			}	
		}
		
		
	}

	public static int getProfondeur(int index) {
		
		
		if (arbre[index] != null) {
			
			return 1 +Math.max(getProfondeur(index*2 + 1),getProfondeur(index*2 + 2));
		}
		
		else {
			return 0;
		}
			
		
	}
	
	public static String getCondition(ArrayList<HashMap<String,Double>> data) {
		/**
		 * calcule entropie pour la data 
		 * et renvoyer la condition correspandante 
		 */
		double median;

		double gain;

		String maxCondition="";
		
		double maxGain= 0;
		
		String condition=null;
		
		ArrayList<HashMap<String,Double>> filsGauche;
		ArrayList<HashMap<String,Double>> filsDroit;
		
		ArrayList<String> attributs=new ArrayList<>();
		for (Entry<String, Double> entry : data.get(0).entrySet()) {
			attributs.add(entry.getKey());

		}

		attributs.remove(attributeToPredict);
		
		//System.out.println("---");

		for(String attribut:attributs) {
			
			List<Double> allAttributValues=data.stream().map(e -> e.get(attribut)).collect(Collectors.toList());

			Collections.sort(allAttributValues);
			
			//System.out.println(allAttributValues.stream().distinct().collect(Collectors.toList()));
			
			for (double divideValue : allAttributValues.stream().distinct().collect(Collectors.toList())) {
				
				filsGauche = new ArrayList<>();
				filsDroit =  new ArrayList<>();
				
				condition=attribut+"<="+divideValue;
				
				//System.out.println(condition);

				for(HashMap<String, Double>dataLigne:data) {
					
					if(dataLigne.get(attribut)<=divideValue) {
					
						filsGauche.add(dataLigne);
					}
					else {
						filsDroit.add(dataLigne);
					}
				}
				
				gain=getGain(data,filsGauche,filsDroit);
				
				if( gain > maxGain) {

					maxGain = gain;
					maxCondition = condition;
					
				}
			}
		}

		return maxCondition;

	}
	
	public static double getGain(ArrayList<HashMap<String,Double>> data,ArrayList<HashMap<String,Double>>filsGauche,ArrayList<HashMap<String,Double>>filsDroit) {
		
		double entropieGenerale = 0;
		double entropieGauche, entropieDroite;
		
		entropieGenerale = getEntropie(data);
		
		entropieGauche = ( (  ((double)filsGauche.size())/data.size())*getEntropie(filsGauche));
		
		entropieDroite = (  ( ((double)filsDroit.size())/data.size())*getEntropie(filsDroit));
		
		return entropieGenerale-entropieGauche-entropieDroite;
	
	}
	
	public static double getEntropie(ArrayList<HashMap<String,Double>> data) {

		double entropie=0;
		double pk;
		int tailleData=data.size();

		HashMap<Double, Integer>classificationRef=new HashMap<>();
		ArrayList<Double> allClassification=new ArrayList<>();

		
		//compter les occurences de chaque classe
		
		for(HashMap<String,Double>element:data) {


			if(classificationRef.get(element.get(attributeToPredict))!=null) {

				classificationRef.put(element.get(attributeToPredict),classificationRef.get(element.get(attributeToPredict))+1);

			}
			else {
				//identification d'une nouvelle classe 
				classificationRef.put(element.get(attributeToPredict),1);

				allClassification.add(element.get(attributeToPredict));
			}
		}
		
		//System.out.println("...");

		for (double classification : allClassification) {

			
			//pk = nombre de fois ou la classe est trouveee / taille de l echantillon
			
			pk= ((double)classificationRef.get(classification))/tailleData;
			
			entropie += (pk*Math.log(pk));
 
		}
		
		return (-1 * entropie) ;

	}
	
	public static void addNode(ArrayList<HashMap<String,Double>> data,int indexcurrent) {

		if(data.size()==0)
			return;
		ArrayList<HashMap<String,Double>> donneesGauche = new ArrayList<>();
		ArrayList<HashMap<String,Double>> donneesDroite =  new ArrayList<>();
		

		String a=getCondition(data);
		
		arbre[indexcurrent]=a;
		

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
		
		//condition d'arret :tous les noeud doivent etre purs => 1 seule classe presente
		
		double differentClassGauche = donneesGauche.stream().map(e -> e.get(attributeToPredict)).distinct().count();
		
		if (differentClassGauche == 1) {
			
			//System.out.println("pur");
			
			arbre[(2*indexcurrent)+1]=mapClass.get(donneesGauche.get(0).get(attributeToPredict));
			
		}
		else {
			
			addNode(donneesGauche, (indexcurrent*2)+1);
		}
	
	
	
		double differentClassDroite = donneesDroite.stream().map(e -> e.get(attributeToPredict)).distinct().count();
		
		
		
		if (differentClassDroite == 1) {	
			
			//System.out.println("pur");
			
			arbre[(2*indexcurrent)+2]=mapClass.get(donneesDroite.get(0).get(attributeToPredict));
			
		}
		else {
			
			addNode(donneesDroite, (indexcurrent*2)+2);
		}
		
		

	}

	public static void predict(String [] arbre, ArrayList<HashMap<String,Double>> testData) {
		
		String node;
		double errors = 0;
		int total = testData.size();
		int index = 0;
		
		for (HashMap<String,Double> ligne : testData) {
			
			index = 0;
			
			node = arbre[index];
			
			//System.out.println(node);
			
			while (node.contains("<=")) {
				
				//node is a condition
				
				String attribut =node.split("<=")[0];
				double value=Double.parseDouble(node.split("<=")[1]);
				
				if (ligne.get(attribut) <= value) {
					index = index*2 +1;
				}
				else {
					index = index*2 +2;
				}
				
				node = arbre[index];
			}
				
			//node contains a class
			
			//System.out.println("index : "+index);
			
			String classeLigne = mapClass.get(ligne.get(attributeToPredict));
			
			//System.out.println("devrait : "+classeLigne+ " | trouve : " + node );
			
			if (classeLigne != node) {
				errors ++;
			}
			
		}
		System.out.println();
		
		System.out.println("taux erreur (prediction) : "+(errors/total*100)+" %");
		
	}
	
	/*
	
	public static double mean(List<Double> m) {


		int taille = m.size();
		double somme=0;

		for(double s:m) {
			somme=somme+s;
		}
		return somme/taille;

	}
	
	public static double median(List<Double> m) {


		int middle = m.size()/2;
	
		if (m.size()%2 == 1) {
			return m.get(middle);
		} else {
			return ((m.get(middle-1) + m.get(middle) )/ 2.0);
		}
	}
	
	public static Double percentile(List<Double> m, double percentile) {
	    int index = (int) Math.ceil(percentile / 100.0 * m.size());
	    return m.get(index-1);
	}
	
	*/
	
	public static void main(String[] args) throws IOException{
		
		
		
		
		// LETTERS
		/*
		attributeToPredict = "lettr";
		String attributeIndex =  "lettr,x-box,y-box,width,high,onpix,x-bar,y-bar,x2bar,y2bar,xybar,x2ybr,xy2br,x-ege,xegvy,y-ege,yegvx";
		String file = "D:\\Téléchargements\\letter-recognition.data";
		*/
		
		// IRIS
		/*
		attributeToPredict = "class";
		String attributeIndex = "sepalLength,sepalWidth,petalLength,petalWidth,class";
		String file = "D:\\Téléchargements\\iris.data";
		*/
		
		ArrayList<HashMap<String,Double>> fileData = getFileData(file,attributeToPredict, attributeIndex);
		
		Collections.shuffle(fileData);
		
		double proportion = 0.6;
		
		ArrayList<HashMap<String,Double>> T = new ArrayList<HashMap<String,Double>>(fileData.subList(0, (int)(fileData.size()*proportion)));
		
		ArrayList<HashMap<String,Double>> A = new ArrayList<HashMap<String,Double>>(fileData.subList((int)(fileData.size()*proportion), fileData.size()));
		
		String [] arbre = buildTree(T,"", "");
		
		displayChildren(0,1);
		
		predict(arbre, A);
		
		System.out.println("profondeur : "+getProfondeur(0));

	}
}

