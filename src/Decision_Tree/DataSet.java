package Decision_Tree;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.*;
import java.lang.Math.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class DataSet {

	static String [] arbre;
	static String  [] arbre2;
	static HashMap<Double,String> mapClass;
	static int classAttributeIndex;
	static String attributeToPredict;
	static String critere;//entropia or gini
	
	static final String DATATYPE_CONTINUOUS = "continuous";
	
	static final String CRITERIA_GINI = "gini";
	static final String CRITERIA_ENTROPIE = "entropie";
	static String [] criterePossible= {CRITERIA_GINI,CRITERIA_ENTROPIE};
	
	static int [] nodesIterations;
	static int [] nodesErrors;
	static String datasetName;
	
	static Map<String, Integer> [] classOccurencsInNode;
	
	static List<String> allClass;
	
	static ArrayList<String []> tempPrunedTrees;
	
	/**
	 * Returns an array of objects matching the file given in parameters
	 * 
	 * @param _path : the csv file (no column names)
	 * @param attributeToPredict : the name of the attribute to predict
	 * @param allAttributes : the attribute list in the good order
	 * @return
	 */
	public static ArrayList<HashMap<String,Double>> getFileData(String _path, String attributeToPredict, String allAttributes){


		//Path path=Paths.get(_path);

		ArrayList<String> lignes = new ArrayList<String>();

		try {
			//lignes = (ArrayList<String>) Files.readAllLines(path);
			
			lignes = getHTTP(_path);
		} catch (Exception e) {

			lignes=null;

			e.printStackTrace();
		}
		
		/*
		 * We chose to represent the data as an array of String-Double map to be able to select the attributes with their names
		 * e.i : attribute.get("sepalLength")
		 * 
		 */
		ArrayList<HashMap<String,Double>> all=new ArrayList<>();

		String [] split = allAttributes.split(",");

		/**
		 * The problem is that the class to predict is a String so we need 2 hashmaps mapClass/map2 that translate the class into a double
		 * so :
		 * map2 contains 
		 * setosa -> 1.0 
		 * versicolor -> 2.0 
		 * 
		 * mapClass contains 
		 * 1.0  -> setosa 
		 * 2.0 ->  versicolor
		 */
		for (int i=0; i<split.length ; i++)  {

			if (split[i].equals(attributeToPredict)) {
				classAttributeIndex = i;
			}

		}

		allClass = lignes.stream().filter(ligne -> !ligne.isEmpty()).map(ligne -> ligne.split(",")[classAttributeIndex]).distinct().sorted().collect(Collectors.toList());

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

	/**
	 * Returns an array containing all the nodes (String) of the decision tree
	 * @param fileData : the array of objects containing the data
	 * @param variableType : continuous, symbolic .. 
	 * @param critere : entropia/Gini
	 * @return
	 */
	public static String [] buildTree(ArrayList<HashMap<String,Double>> fileData,String variableType,String critere) {

		//critere=critere;

		if(!(Arrays.asList(criterePossible).contains(critere))) {

			throw new IllegalArgumentException("Unexpected value: " + critere);
		}

		long startTime = System.nanoTime();    

		/**
		 * The nodes are stored in an array with :
		 *  index0=root
		 *  index=index*2+1 is left child
		 *  index=index*2+2 is right child
		 *  
		 *  Requires a large array because of the potential depth and number of branches of the tree
		 *  Could be resized automatically and use Hashmap<Integer, String>
		 */
		arbre=new String [10000];

		addNode(fileData,0,critere) ;

		long timeToCalculate = System.nanoTime() - startTime;

		System.out.println("Time to calculate : "+timeToCalculate/1000/1000 + " ms" + " ou "+timeToCalculate/1000/1000/1000+ " s");

		return arbre;

	}

	/**
	 * RECURSIVE
	 * Function that is given the partition of data to branch and branch the tree
	 * 
	 * Calls getCondition that returns a String indicating the condition to branch
	 * Parses the String to create the boolean test i.e transform "x-ege<=2.0" into ligne.get("x-ege") <= 2.0
	 * Divides the data and calls itself with the children dataset
	 * 
	 * @param data : the partition to branch
	 * @param indexcurrent : the index of the array to add the children nodes
	 */

	public static void addNode(ArrayList<HashMap<String,Double>> data,int indexcurrent,String critere) {

		if(data.size()==0)
			return;

		ArrayList<HashMap<String,Double>> donneesGauche = new ArrayList<>();
		ArrayList<HashMap<String,Double>> donneesDroite =  new ArrayList<>();

		/*
		 * Get condition returned by the function getCondition which is used to determine the position of 
		 * node 
		 * the condition returned by getCondition is always written in  this format :attribut<= value
		 */
		String a=getCondition(data,critere);

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

			arbre[(2*indexcurrent)+1]=mapClass.get(donneesGauche.get(0).get(attributeToPredict));

		}
		else {

			addNode(donneesGauche, (indexcurrent*2)+1,critere);
		}

		double differentClassDroite = donneesDroite.stream().map(e -> e.get(attributeToPredict)).distinct().count();

		if (differentClassDroite == 1) {	

			arbre[(2*indexcurrent)+2]=mapClass.get(donneesDroite.get(0).get(attributeToPredict));

		}
		else {

			addNode(donneesDroite, (indexcurrent*2)+2,critere);
		}



	}

	/**
	 * Returns the optimal condition (max gain) to branch the decision tree and divide data
	 * Generate all conditions like attribute<=i for all attribute values
	 * Divides data according to condition
	 * Calculates gain for the condition
	 * Return condition that has max gain
	 * 
	 * @param data : the data partition to branch
	 * @return : a String containing the boolean test i.e : "attribute<=value" that will be parsed by addNode()
	 */
	public static String getCondition(ArrayList<HashMap<String,Double>> data,String critere) {

		double gain;
		double maxGain= 0;

		String condition;
		String maxCondition="";

		ArrayList<HashMap<String,Double>> filsGauche;
		ArrayList<HashMap<String,Double>> filsDroit;


		//Get all attributes to choose the condition from
		 
		ArrayList<String> attributs=new ArrayList<>();
		for (Entry<String, Double> entry : data.get(0).entrySet()) {
			attributs.add(entry.getKey());

		}

		attributs.remove(attributeToPredict);


		for(String attribut:attributs) {

			//Get all values for the attribute
			List<Double> allAttributValues=data.stream().map(e -> e.get(attribut)).collect(Collectors.toList());

			Collections.sort(allAttributValues);

			//All unique attribute values
			
			for (double divideValue : allAttributValues.stream().distinct().collect(Collectors.toList())) {

				filsGauche = new ArrayList<>();
				filsDroit =  new ArrayList<>();

				condition=attribut+"<="+divideValue;

				for(HashMap<String, Double>dataLigne:data) {

					if(dataLigne.get(attribut)<=divideValue) {

						filsGauche.add(dataLigne);
					}
					else {
						filsDroit.add(dataLigne);
					}
				}

				gain=getGain(data,filsGauche,filsDroit,critere);

				if( gain > maxGain) {

					maxGain = gain;
					maxCondition = condition;

				}
			}
		}

		return maxCondition;

	}

	/**
	 * Evaluates information gain from condition
	 * @param data : general data
	 * @param filsGauche : left child data
	 * @param filsDroit : right child data
	 * @return
	 */
	public static double getGain(ArrayList<HashMap<String,Double>> data,ArrayList<HashMap<String,Double>>filsGauche,ArrayList<HashMap<String,Double>>filsDroit,String critere) {

		double melangeGenerale=0;//peut etre entropie generale ou gini generale comme nommé dans le cours
		double melanageGauche, melangeDroit, gain;

		melangeGenerale=getCritere(data, critere);

		melanageGauche= ((((double)filsGauche.size())/data.size())*getCritere(filsGauche,critere));;

		melangeDroit=((((double)filsDroit.size())/data.size())*getCritere(filsDroit,critere));

		gain=melangeGenerale-melanageGauche-melangeDroit;

		return gain;

	}

	/**
	 * Evaluates criteria (entropy,gini) for the parameter data
	 * Store the number of occurences of each class in classificationRef
	 * Calculate pk from classificationRef
	 * @param data
	 * @return
	 */

	public static double getCritere(ArrayList<HashMap<String,Double>> data,String critere) {

		double entropie=0;

		double gini=0;
		double pk;
		int tailleData=data.size();

		//Store occurences of each class (class are stored as double)
		HashMap<Double, Integer>classificationRef=new HashMap<>();

		//Store each different class
		ArrayList<Double> allClassification=new ArrayList<>();


		//count each class occurences

		for(HashMap<String,Double>element:data) {


			//If the class is already present, just add 1 to the occurence number
			//Else initiate to 1

			if(classificationRef.get(element.get(attributeToPredict))!=null) {

				classificationRef.put(element.get(attributeToPredict),classificationRef.get(element.get(attributeToPredict))+1);

			}
			else {
				//new class identification
				classificationRef.put(element.get(attributeToPredict),1);

				allClassification.add(element.get(attributeToPredict));
			}
		}

		switch (critere) {
		
			case CRITERIA_ENTROPIE : {

			for (double classification : allClassification) {


				//pk = number of times where the class is found / size of the given sub-dataset

				pk= ((double)classificationRef.get(classification))/tailleData;

				entropie += (pk*Math.log(pk));

			}

			return (-1 * entropie) ;

			//yield type;
		}
			case CRITERIA_GINI :{

			//pk = number of times where the class is found / size of the given sub-dataset
			for (double classification : allClassification) {
				pk= ((double)classificationRef.get(classification))/tailleData;

				gini+=Math.pow(pk,2);


			}
			return(1-gini);


		}


		default:
			throw new IllegalArgumentException("Unexpected value: " + critere);
		}


	}


	/**
	 * RECURSIVE
	 * Displays tree with a tabulation offset for each level
	 * @param index
	 * @param offset
	 */
	public static void displayChildren(String [] arbre, int index, int offset) {


		System.out.print(arbre[index]);


		if (arbre[(index*2) +1] != null) {
			System.out.println();
			for (int i=0;i<offset;i++) {
				System.out.print("\t");
			}

			displayChildren(arbre, (index*2) +1, offset+1);
		}	
		if (arbre[(index*2) +2] != null) {
			System.out.println();
			for (int i=0;i<offset;i++) {
				System.out.print("\t");
			}

			displayChildren(arbre, (index*2) +2, offset+1);

		}	



	}

	/**
	 * RECURSIVE
	 * Calculates depth of the tree
	 * @param index
	 * @return
	 */
	public static int getProfondeur(int index) {


		if (arbre[index] != null) {

			return 1 +Math.max(getProfondeur(index*2 + 1),getProfondeur(index*2 + 2));
		}

		else {
			return 0;
		}


	}
	
	/**
	 * Returns the error % of the prediction for all the vectors in the test Dataset
	 * @param arbre
	 * @param testData
	 * @return
	 */
	
	public static double predictForWholeDataset (String [] arbre, ArrayList<HashMap<String,Double>> testData) {

		
		double errors = 0;
		int total = testData.size();
		
		//Loop for each vector to test
		for (HashMap<String,Double> vector : testData) {

			// The class is stored as a number in the loaded dataset (1.0, 2.0) 
			// So the value has to be translated to the correct String  
			String valueToPredict = mapClass.get(vector.get(attributeToPredict));
			
			
			// Compare the prediction to the correct value
			if (predict(arbre, vector)  != valueToPredict) {
				errors ++;
			}

		}
		System.out.println();

		//System.out.println("taux erreur (prediction) : "+(errors/total*100)+" %");

		return (errors/total*100);
	}
	
	/**
	 * Predicts the class of the testData individuals using the decision tree arbre
	 * 
	 * Returns nothing but displays error %
	 * 
	 * @param arbre
	 * @param testData
	 */
	
	public static String predict (String [] arbre, HashMap<String,Double> vector) {
		
		//Navigate the decision tree to find the node containing the class
		
		int index = 0;
		String node = arbre[index];
		
		while (node.contains("<=")) {

			//node is a condition

			String attribut =node.split("<=")[0];
			double value= Double.parseDouble(node.split("<=")[1]);

			if (vector.get(attribut) <= value) {
				index = index*2 +1;
			}
			else {
				index = index*2 +2;
			}

			node = arbre[index];
		}

		//node contains a class

		return node;
	}
	
	
	public static ArrayList<String> getHTTP(String urlToRead) throws Exception {
		  ArrayList<String> body = new ArrayList<String>();
	      //StringBuilder result = new StringBuilder();
	      URL url = new URL(urlToRead);
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("GET");
	      try (var reader = new BufferedReader(
	                  new InputStreamReader(conn.getInputStream()))) {
	          for (String line; (line = reader.readLine()) != null; ) {
	        	  body.add(line);
	          }
	      }
	      return body;
		   
	}
	
	
	
	public static int getClassOccurences (String [] arbre, int index, String classAttribute) {
		
		if (classOccurencsInNode[index] == null) {
			
			classOccurencsInNode[index] = new HashMap<String, Integer>();
			
		}
		
		if (!arbre[index].contains("<=")) {
			
			if (arbre[index].equals(classAttribute)) {
				return 1;
			}
			else {
				return 0;
			}
			
		}
		
		else {
			
			int result = getClassOccurences (arbre, index*2+1, classAttribute) + getClassOccurences (arbre, index*2+2, classAttribute);
			
			classOccurencsInNode[index].put(classAttribute, result);
			
			return result;
		}
		
		
		
	}
	
	
	public static void fillClassOccurencsInNode (String [] arbre) {
		
		for (String oneClass : allClass) {
			
			getClassOccurences (arbre, 0, oneClass);
			
		}
		
	}
	
	
	public static int getLeafNumber(int index) {
		int sum =0;
		for (String oneClass : allClass) {
			sum += classOccurencsInNode[index].get(oneClass);
		}
		return sum;
	}
	
	public static double getPruningCriteria (int index) {
		
		double critere;
		
		int maxOccurences = 0;
		int tempOccurences;
		
		int sumOfOccurences = 0;
		
		for (String oneClass : allClass) {
			
			tempOccurences = classOccurencsInNode[index].get(oneClass);
			
			sumOfOccurences += tempOccurences;
			
			if (tempOccurences > maxOccurences) {
				maxOccurences = tempOccurences;
			}
		}
		
		int individusMalClassesInNode = sumOfOccurences - maxOccurences;
		
		int nbFeuillesTotal = getLeafNumber(0);
		
		int nbLeafChildren = getLeafNumber(index); 
		
		critere = (double)(individusMalClassesInNode)/(nbFeuillesTotal*(nbLeafChildren - 1));
				
		return critere;
	}
	
	public static void killChildren (String [] arbre, int index) {
		
		arbre[index] = null;
		
		if (arbre[index*2+1] != null) {
			killChildren(arbre,index*2+1);
		}
		
		if (arbre[index*2+2] != null) {
			killChildren(arbre,index*2+2);
		}
		
		
	}
	
	public static String [] pruneTree(String [] arbre, ArrayList<HashMap<String,Double>> data, double alpha) {
	
		double minTauxErreur = 100;
		
		int indexMinTauxErreur = 0;
		
		int treeIndex = 1;
		
		tempPrunedTrees = new ArrayList<String []>();
	
		tempPrunedTrees.add(arbre);
		
		String [] prunedTree = arbre.clone();
		
		do {
			// Determine which node will be pruned
			classOccurencsInNode = (Map<String, Integer>[]) new Map [arbre.length];
			
			fillClassOccurencsInNode(prunedTree);
			
			double minCritere = 1;
			int minCritereIndex = 0;  
			for (int i=0; i<classOccurencsInNode.length ; i++) {
				
				if (classOccurencsInNode[i] != null) {
					
					if (!classOccurencsInNode[i].isEmpty()) {
						
						double pruningCritere = getPruningCriteria(i);
						
						if (pruningCritere < minCritere) {
							minCritere = pruningCritere;
							minCritereIndex = i;
						}
						
					}
					
				}
					
			}
			
			System.out.println("critere min : "+minCritere);
			
			// Determine which class will replace the node
			
			String dominantClass=allClass.get(0);
			int maxClass = 0;
			for (String oneClass : allClass) {
				if (classOccurencsInNode[minCritereIndex].get(oneClass) > maxClass) {
					dominantClass = oneClass;
				}
			}
			
			System.out.println("class dominante critere min : "+dominantClass);
			
			// prune the tree
			
			prunedTree = arbre.clone();
			
			killChildren(prunedTree, minCritereIndex);
			
			prunedTree[minCritereIndex] = dominantClass;
			
			tempPrunedTrees.add(prunedTree);
			
			displayChildren(prunedTree, 0, 1);
			
			System.out.println();
			
			double tauxErreur = predictForWholeDataset(prunedTree, data);
			
			System.out.println("Taux erreur : "+tauxErreur);
			
			if (tauxErreur < minTauxErreur) {
				minTauxErreur = tauxErreur;
				indexMinTauxErreur = treeIndex;
			}
			
			
			treeIndex ++;
		}
		
		while (prunedTree[0].contains("<="));
		
		System.out.println();
		System.out.println();
		System.out.println();
		
		System.out.println("min tx erreur : "+minTauxErreur);
		
		System.out.println();
		displayChildren(tempPrunedTrees.get(indexMinTauxErreur), 0, 1);
		System.out.println();
		
		return null;
	}
	
	public static void main(String[] args) throws IOException{


		//UNCOMMENT THE GROUP OF LINES CORRESPONDING TO THE DATASET YOU WANT

		/*
		// LETTERS
		
		datasetName = "Letters";
		attributeToPredict = "lettr";
		String attributeIndex =  "lettr,x-box,y-box,width,high,onpix,x-bar,y-bar,x2bar,y2bar,xybar,x2ybr,xy2br,x-ege,xegvy,y-ege,yegvx";
		
		//Will do HTTP GET Request on the link
		String file = "https://archive.ics.uci.edu/ml/machine-learning-databases/letter-recognition/letter-recognition.data";
		*/
		
		
		
		// IRIS
		
		datasetName = "Iris";
		attributeToPredict = "class";
		String attributeIndex = "sepalLength,sepalWidth,petalLength,petalWidth,class";
		
		//Will do HTTP GET Request on the link
		String file = "https://archive.ics.uci.edu/ml/machine-learning-databases/iris/iris.data";
		
		
		ArrayList<HashMap<String,Double>> fileData = getFileData(file,attributeToPredict, attributeIndex);
	
		double minErreur = 100;
		double erreur;

		int numberOfIterations = 1;
		
		System.out.println("Resultat de "+numberOfIterations+ " iteration"+ (numberOfIterations <=1 ? "" : "s") +" de "+ datasetName+" :");

		for (int i=0;i<numberOfIterations;i++) {

			System.out.println();
			System.out.println(" ---");
			System.out.println();

			Collections.shuffle(fileData);

			double proportion = 0.6;

			//Training dataset
			ArrayList<HashMap<String,Double>> A = new ArrayList<HashMap<String,Double>>(fileData.subList(0, (int)(fileData.size()*proportion)));

			//Test dataset
			ArrayList<HashMap<String,Double>> T = new ArrayList<HashMap<String,Double>>(fileData.subList((int)(fileData.size()*proportion), fileData.size()));

			arbre = buildTree(A,DATATYPE_CONTINUOUS, CRITERIA_GINI);
			
			System.out.println("erreur : "+predictForWholeDataset(arbre, T)+ " %");
			
			
			
			//arbre = buildTree(A,DATATYPE_CONTINUOUS, CRITERIA_ENTROPIE);
			
			displayChildren(arbre, 0,1);
			
			System.out.println();
			
			
			
			pruneTree(arbre, T, 1);
			
			
			/*
			erreur = predictForWholeDataset(arbre, T);

			if (erreur < minErreur) {
				minErreur = erreur;
			}

			System.out.println("profondeur : "+getProfondeur(0));
			System.out.println("erreur : "+erreur+ " %");
			
			*/
		}
		
		System.out.println("fini");
		
		/*
		System.out.println();
		System.out.println("min erreur : "+minErreur+ " %");
		*/
	}
}

