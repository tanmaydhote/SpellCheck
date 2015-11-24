import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.io.*;
import java.util.Scanner;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class WordCheck {

	public static LinkedHashMap<String, Double> dictionary = new LinkedHashMap<String , Double>();
	public static ArrayList<String> dictionaryWords = new ArrayList<String>();
	public static int insertionMatrix[][];
	public static int deletionMatrix[][];
	public static int substitutionMatrix[][];
	public static int transpositionMatrix[][];
	public ArrayList<String> candidateWords = new ArrayList<String>();
	public ArrayList<String> insertion = new ArrayList<String>();
	public ArrayList<String> deletion = new ArrayList<String>();
	public ArrayList<String> substitution = new ArrayList<String>();
	public ArrayList<String> transposition = new ArrayList<String>();//has been assumed as one transformation, not in editDistance2
	public ArrayList<String> editDistance1 = new ArrayList<String>();
	public ArrayList<String> editDistance2 = new ArrayList<String>();
	public ArrayList<String> editDistance3 = new ArrayList<String>();
	public static LinkedHashMap<String, Integer> letter2num = new LinkedHashMap<String , Integer>();
	public static double ED1Likelihood;
	public static double ED2Likelihood;
	public static double ED3Likelihood;
	public ArrayList<String> candidates = new ArrayList<String>();
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Double>[] lengthDictionary = (LinkedHashMap<String, Double>[]) new LinkedHashMap[20];
	
	@SuppressWarnings("unchecked")
	public ArrayList<String>[] lengthDictionaryWords = (ArrayList<String>[])new ArrayList[20];
	public void createDictionary( ) throws IOException{
		for(int i=0;i<20;i++)
		{
			lengthDictionary[i]= new LinkedHashMap<String, Double>();
			lengthDictionaryWords[i] = new ArrayList<String>();
		}
		ED1Likelihood=700.9436619718;
		ED2Likelihood=10.444444;
		ED3Likelihood=0.1;
		dictionary.clear();
		String S;
		double a;
		double largest=0;
		Scanner input = new Scanner(new File("/home/tanmay/workspace/SpellCheck/src/wordList.txt"));
		while(input.hasNext()){
			S = input.next();
			dictionaryWords.add(S);
			a = input.nextDouble();
			if(a>largest) largest = a;
			//System.out.print(S+"\t"+ a+"\t");
			dictionary.put(S,a);
		}
		for (String key : dictionary.keySet())
		{
		    a = dictionary.get(key)/largest;
		    dictionary.put(key,a);
		    if(key.length()<20)
		    {	
		    	lengthDictionary[key.length()].put(key,a);
		    	lengthDictionaryWords[key.length()].add(key);
		    }
		    else 
		    {	
		    	lengthDictionary[19].put(key,a);
		    	lengthDictionaryWords[19].add(key);
		    }
		    }
		//System.out.println(dictionary.get("respect"));
	}
	public void getCandidates(String word) throws IOException{
		candidateWords.clear();
		insertion.clear();
		deletion.clear();
		substitution.clear();
		transposition.clear();
		editDistance1.clear();
		editDistance2.clear();
		editDistance3.clear();
		int editDistance;
		int upperBound,lowerBound,wordLength;
		wordLength = word.length();
		if((wordLength -3<20))
				lowerBound = wordLength -3;
		else lowerBound = 19;
		
		if((wordLength -3<0))
			lowerBound = 1;
		
		if((wordLength +3<20))
			upperBound = wordLength +3;
		else upperBound = 19;
		boolean flag;
		Levenshtein lev = new Levenshtein();
		for(int l=lowerBound ;l<=upperBound;l++)
		{
			flag = false;
		for(String dictionaryWord : lengthDictionaryWords[l])
		{
		editDistance = lev.editDistance(dictionaryWord, word);
		if(editDistance<=3)
		{
			candidateWords.add(dictionaryWord);
			if (editDistance==1)
			{
				if(dictionaryWord.length()>word.length())
					deletion.add(dictionaryWord);
				else if(dictionaryWord.length()<word.length())
					insertion.add(dictionaryWord);
				else if(dictionaryWord.length()==word.length())
				 substitution.add(dictionaryWord);
				editDistance1.add(dictionaryWord);
			}
			else if(editDistance==2)
			{
				if(dictionaryWord.length()==word.length())
				{
					for(int i=1;i<word.length()-1;i++)
					{
						if((word.substring(i, i+1)).equals(dictionaryWord.substring(i+1, i+2)) && 
								   (word.substring(i+1, i+2)).equals(dictionaryWord.substring(i, i+1)))
						{
							transposition.add(dictionaryWord);
							editDistance1.add(dictionaryWord);
						flag = true;
						break;
						}
					}
				}
				if(flag==false) editDistance2.add(dictionaryWord);
			}
			else editDistance3.add(dictionaryWord);
		}
		}
		//if(candidateWords.size()==500) break;
		}
		scoreCandidates();
	/*	for(String candidateWord : candidateWords)
		{	
			System.out.println(candidateWord);	
		}*/
		//int total = substitution.size()+insertion.size()+deletion.size()+transposition.size()+editDistance2.size()+editDistance3.size();
	   //System.out.println("Count1 = "+ total );
	  //System.out.println("Count2 = "+ candidateWords.size() );
	}
	public void scoreCandidates() throws IOException{
		candidates.clear();
	 LinkedHashMap<String, Double> CandidateMap = new LinkedHashMap<String , Double>();
	 Double Prior,Likelihood,Score;
	 int count = 0;
		for(String candidate :editDistance1)
	{	
			Prior = dictionary.get(candidate);
			Likelihood = ED1Likelihood; ;
			Score = Prior * Likelihood;
			CandidateMap.put(candidate,Score);
	}
		for(String candidate:editDistance2)
		{
			Prior = dictionary.get(candidate);
			Likelihood = ED2Likelihood;;
			Score = Prior * Likelihood;
			CandidateMap.put(candidate,Score);
		}
		for(String candidate:editDistance3)
		{
			Prior = dictionary.get(candidate);
			Likelihood = ED3Likelihood;;
			Score = Prior * Likelihood;
			CandidateMap.put(candidate,Score);
		}
		//System.out.println(CandidateMap.size());
		//	System.out.print(CandidateMap);
		LinkedHashMap<String,Double > map = sortByValues(CandidateMap);
		//System.out.print(map);
		ListIterator<String> iter =
			    new ArrayList(map.keySet()).listIterator(map.size());
		
			while (iter.hasPrevious()) {
			    count++;
				String key = iter.previous();
			  //  System.out.println(key);
				candidates.add(key);
				if(count==10) break;
			}
	}
	  public LinkedHashMap sortByValues(LinkedHashMap map) { 
	       List list = new LinkedList(map.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o1)).getValue())
	                  .compareTo(((Map.Entry) (o2)).getValue());
	            }
	       });

	       
	       LinkedHashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	  }

	  public void createConfusionMatrix( ) throws IOException{
	  
		  
		  for(int i =0;i<26;i++)
		  {
			  String charac = Character.toString((char)(i+96));
			  letter2num.put(charac,i);
		  }
		  letter2num.put("blank",26);
		  
		  int[][] del = {{0,7,58,21,3,5,18,8,61,0,4,43,5,53,0,9,0,98,28,53,62,1,0,0,2,0},
				  {2,2,1,0,22,0,0,0,183,0,0,26,0,0,2,0,0,6,17,0,6,1,0,0,0,0},
				  {37,0,70,0,63,0,0,24,320,0,9,17,0,0,33,0,0,46,6,54,17,0,0,0,1,0},
				  {12,0,7,25,45,0,10,0,62,1,1,8,4,3,3,0,0,11,1,0,3,2,0,0,6,0},
				  {80,1,50,74,89,3,1,1,6,0,0,32,9,76,19,9,1,237,223,34,8,2,1,7,1,0},
				  {4,0,0,0,13,46,0,0,79,0,0,12,0,0,4,0,0,11,0,8,1,0,0,0,1,0},
				  {25,0,0,2,83,1,37,25,39,0,0,3,0,29,4,0,0,52,7,1,22,0,0,0,1,0},
				  {15,12,1,3,20,0,0,25,24,0,0,7,1,9,22,0,0,15,1,26,0,0,1,0,1,0},
				  {26,1,60,26,23,1,9,0,1,0,0,38,14,82,41,7,0,16,71,64,1,1,0,0,1,7},
				  {0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0},
				  {4,0,0,1,15,1,8,1,5,0,1,3,0,17,0,0,0,1,5,0,0,0,1,0,0,0},
				  {24,0,1,6,48,0,0,0,217,0,0,211,2,0,29,0,0,2,12,7,3,2,0,0,11,0},
				  {15,10,0,0,33,0,0,1,42,0,0,0,180,7,7,31,0,0,9,0,4,0,0,0,0,0},
				  {21,0,42,71,68,1,160,0,191,0,0,0,17,144,21,0,0,0,127,87,43,1,1,0,2,0},
				  {11,4,3,6,8,0,5,0,4,1,0,13,9,70,26,20,0,98,20,13,47,2,5,0,1,0},
				  {25,0,0,0,22,0,0,12,15,0,0,28,1,0,30,93,0,58,1,18,2,0,0,0,0,0},
				  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,18,0,0,0,0,0},
				  {63,4,12,19,188,0,11,5,132,0,3,33,7,157,21,2,0,277,103,68,0,10,1,0,27,0},
				  {16,0,27,0,74,1,0,18,231,0,0,2,1,0,30,30,0,4,265,124,21,0,0,0,1,0},
				  {24,6,9,10,15,0,1,0,28,0,0,39,2,111,1,0,0,129,31,66,0,0,0,0,1,0},
				  {26,6,9,10,15,0,1,0,28,0,0,39,2,111,1,0,0,129,31,66,0,0,0,0,1,0},
				  {9,0,0,0,58,0,0,0,31,0,0,0,0,0,2,0,0,1,0,0,0,0,0,0,1,0},
				  {40,0,0,1,11,1,0,11,15,0,0,1,0,2,2,0,0,2,24,0,0,0,0,0,0,0},
				  {1,0,17,0,3,0,0,1,0,0,0,0,0,0,0,6,0,0,0,5,0,0,0,0,1,0},
				  {2,1,34,0,2,0,1,0,1,0,0,1,2,1,1,1,0,0,17,1,0,0,1,0,0,0},
				  {1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
				  {20,14,41,31,20,20,7,6,20,3,6,22,16,5,5,17,0,28,26,6,2,1,24,0,0,2}};
		  
		  deletionMatrix = del;
		  
		  int[][] sub =  {{0,0,7,1,342,0,0,2,118,0,1,0,0,3,76,0,0,1,35,9,9,0,1,0,5,0},
					{0,0,9,9,2,2,3,1,0,0,0,5,11,5,0,10,0,0,2,1,0,0,8,0,0,0},
					{6,5,0,16,0,9,5,0,0,0,1,0,7,9,1,10,2,5,39,40,1,3,7,1,1,0},
					{1,10,13,0,12,0,5,5,0,0,2,3,7,3,0,1,0,43,30,22,0,0,4,0,2,0},
					{388,0,3,11,0,2,2,0,89,0,0,3,0,5,93,0,0,14,12,6,15,0,1,0,18,0},
					{0,15,0,3,1,0,5,2,0,0,0,3,4,1,0,0,0,6,4,12,0,0,2,0,0,0},
					{4,1,11,11,9,2,0,0,0,1,1,3,0,0,2,1,3,5,13,21,0,0,1,0,3,0},
					{1,8,0,3,0,0,0,0,0,0,2,0,12,14,2,3,0,3,1,11,0,0,2,0,0,0},
					{103,0,0,0,146,0,1,0,0,0,0,6,0,0,49,0,0,0,2,1,47,0,2,1,15,0},
					{0,1,1,9,0,0,1,0,0,0,0,2,1,0,0,0,0,0,5,0,0,0,0,0,0,0},
					{1,2,8,4,1,1,2,5,0,0,0,0,5,0,2,0,0,0,6,0,0,0,4,0,0,3},
					{2,10,1,4,0,4,5,6,13,0,1,0,0,14,2,5,0,11,10,2,0,0,0,0,0,0},
					{1,3,7,8,0,2,0,6,0,0,4,4,0,180,0,6,0,0,9,15,13,3,2,2,3,0},
					{2,7,6,5,3,0,1,19,1,0,4,35,78,0,0,7,0,28,5,7,0,0,1,2,0,2},
					{91,1,1,3,116,0,0,0,25,0,2,0,0,0,0,14,0,2,4,14,39,0,0,0,18,0},
					{0,11,1,2,0,6,5,0,2,9,0,2,7,6,15,0,0,1,3,6,0,4,1,0,0,0},
					{0,0,1,0,0,0,27,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
					{0,14,0,30,12,2,2,8,2,0,5,8,4,20,1,14,0,0,12,22,4,0,0,1,0,0},
					{11,8,27,33,35,4,0,1,0,1,0,27,0,6,1,7,0,14,0,15,0,0,5,3,20,1},
					{3,4,9,42,7,5,19,5,0,1,0,14,9,5,5,6,0,11,37,0,0,2,19,0,7,6},
					{20,0,0,0,44,0,0,0,64,0,0,0,0,2,43,0,0,4,0,0,0,0,2,0,8,0},
					{0,0,7,0,0,3,0,0,0,0,0,1,0,0,1,0,0,0,8,3,0,0,0,0,0,0},
					{2,2,1,0,1,0,0,2,0,0,1,0,0,0,0,7,0,6,3,3,1,0,0,0,0,0},
					{0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0},
					{0,0,2,0,15,0,1,7,15,0,0,0,2,0,6,1,0,7,36,8,5,0,0,1,0,0},
					{0,0,0,7,0,0,0,0,0,0,0,7,5,0,0,0,0,2,21,3,0,0,0,0,3,0}};
		  substitutionMatrix = sub;
		  
		  int[][] inser = {{15,1,14,7,10,0,1,1,33,1,4,31,2,39,12,4,3,28,134,7,28,0,1,1,4,1},
					{3,11,0,0,7,0,1,0,50,0,0,15,0,1,1,0,0,5,16,0,0,3,0,0,0,0},
					{19,0,54,1,13,0,0,18,50,0,3,1,1,1,7,1,0,7,25,7,8,4,0,1,0,0},
					{18,0,3,17,14,2,0,0,9,0,0,6,1,9,13,0,0,6,119,0,0,0,0,0,5,0},
					{39,2,8,76,147,2,0,1,4,0,3,4,6,27,5,1,0,83,417,6,4,1,10,2,8,0},
					{1,0,0,0,2,27,1,0,12,0,0,10,0,0,0,0,0,5,23,0,1,0,0,0,1,0},
					{8,0,0,0,5,1,5,12,8,0,0,2,0,1,1,0,1,5,69,2,3,0,1,0,0,0,},
					{4,1,0,1,24,0,10,18,17,2,0,1,0,1,4,0,0,16,24,22,1,0,5,0,3,0},
					{10,3,13,13,25,0,1,1,69,2,1,17,11,33,27,1,0,9,30,29,11,0,0,1,0,1},
					{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0},
					{2,4,0,1,9,0,0,1,1,0,1,1,0,0,2,1,0,0,95,0,1,0,0,0,4,0},
					{3,1,0,1,38,0,0,0,79,0,2,128,1,0,7,0,0,0,97,7,3,1,0,0,2,0},
					{11,1,1,0,17,0,0,1,6,0,1,0,102,44,7,2,0,0,47,1,2,0,1,0,0,0},
					{15,5,7,13,52,4,17,0,34,0,1,1,26,99,12,0,0,2,156,53,1,1,0,0,1,0},
					{14,1,1,3,7,2,1,0,28,1,0,6,3,13,64,30,0,16,59,4,19,1,0,0,1,1},
					{23,0,1,1,10,0,0,20,3,0,0,2,0,0,26,70,0,29,52,9,1,1,1,0,0,0},
					{0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0},
					{15,2,1,0,89,1,1,2,64,0,0,5,9,7,10,0,0,132,273,29,7,0,1,0,10,0},
					{13,1,7,20,41,0,1,50,101,0,2,2,10,7,3,1,0,1,205,49,7,0,1,0,7,0},
					{39,0,0,3,65,1,10,24,59,1,0,6,3,1,23,1,0,54,264,183,11,0,5,0,6,0},
					{15,0,3,0,9,0,0,1,24,1,1,3,3,9,1,3,0,49,19,27,26,0,0,2,3,0},
					{0,2,0,0,36,0,0,0,10,0,0,1,0,1,0,1,0,0,0,0,1,5,1,0,0,0},
					{0,0,0,1,10,0,0,1,1,0,1,1,0,2,0,0,1,1,8,0,2,0,4,0,0,0},
					{0,0,18,0,1,0,0,6,1,0,0,0,1,0,3,0,0,0,2,0,0,0,0,1,0,0},
					{5,1,2,0,3,0,0,0,2,0,0,1,1,6,0,0,0,1,33,1,13,0,1,0,2,0},
					{2,0,0,0,5,1,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,4},
					{46,8,9,8,26,11,14,3,5,1,17,5,6,2,2,10,0,6,23,2,11,1,2,1,1,2}};

		  insertionMatrix = inser;
		  
		  int[][] tran = {{0,0,2,1,1,0,0,0,19,0,1,14,4,25,10,3,0,27,3,5,31,0,0,0,0,0},
					{0,0,0,0,2,0,0,0,0,0,0,1,1,0,2,0,0,0,2,0,0,0,0,0,0,0},
					{0,0,0,0,1,0,0,1,85,0,0,15,0,0,13,0,0,0,3,0,7,0,0,0,0,0},
					{0,0,0,0,0,0,0,0,7,0,0,0,0,0,0,0,0,1,0,0,2,0,0,0,0,0},
					{1,0,4,5,0,0,0,0,60,0,0,21,6,16,11,2,0,29,5,0,85,0,0,0,2,0},
					{0,0,0,0,0,0,0,0,12,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
					{4,0,0,0,2,0,0,0,0,0,0,1,0,15,0,0,0,3,0,0,3,0,0,0,0,0},
					{12,0,0,0,15,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10,0,0,0,0,0,0},
					{15,8,31,3,66,1,3,0,0,0,0,9,0,5,11,0,1,13,42,35,0,6,0,0,0,3},
					{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
					{0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
					{11,0,0,12,20,0,1,0,4,0,0,0,0,0,1,3,0,0,1,1,3,9,0,0,7,0},
					{9,0,0,0,20,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,4,0,0,0,0,0},
					{15,0,6,2,12,0,8,0,1,0,0,0,3,0,0,0,0,0,6,4,0,0,0,0,0,0},
					{5,0,2,0,4,0,0,0,5,0,0,1,0,5,0,1,0,11,1,1,0,0,7,1,0,0},
					{17,0,0,0,4,0,0,1,0,0,0,0,0,0,1,0,0,5,3,6,0,0,0,0,0,0},
					{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
					{12,0,0,0,24,0,3,0,14,0,2,2,0,7,30,1,0,0,0,2,10,0,0,0,2,0},
					{4,0,0,0,9,0,0,5,15,0,0,5,2,0,1,22,0,0,0,1,3,0,0,0,16,0},
					{4,0,3,0,4,0,0,21,49,0,0,4,0,0,3,0,0,5,0,0,11,0,2,0,0,0},
					{22,0,5,1,1,0,2,0,2,0,0,2,1,0,20,2,0,11,11,2,0,0,0,0,0,0},
					{0,0,0,0,1,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
					{0,0,0,0,0,0,0,4,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,8,0},
					{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
					{0,1,2,0,0,0,1,0,0,0,0,3,0,0,0,2,0,1,10,0,0,0,0,0,0,0},
					{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},};
		  
		  transpositionMatrix = tran;
	  }
	  
	  public int getLikelihoodScores(String correctWord,String incorrectWord ) throws IOException
	  {
		  String Cp="a",Cp1="a";
		  int score;
		  if(correctWord.length()>incorrectWord.length()) //Deletion
		  {
			  for(int i=0;i<correctWord.length();i++)
			  {
				 if(!(correctWord.substring(i,i+1).equals(incorrectWord.substring(i,i+1))))
				 {
					 if(i==0)
					 {
						 Cp = "blank";
						 Cp1 = correctWord.substring(0,1);
					 }
					 else
					 {
						 Cp = correctWord.substring(i-1,i);
						 Cp1 = correctWord.substring(i,i+1);
					 }
					 break;
				 }
			  }
			  score = insertionMatrix[letter2num.get(Cp)][letter2num.get(Cp1)];
		  }
		  else if(correctWord.length()<incorrectWord.length()) //Addition
		  {
			  for(int i=0;i<incorrectWord.length();i++)
			  {
				 if(!(correctWord.substring(i,i+1).equals(incorrectWord.substring(i,i+1))))
				 {
					 if(i==0)
					 {
						 Cp = "blank";
						 Cp1 = incorrectWord.substring(0,1);
					 }
					 else
					 {
						 Cp = incorrectWord.substring(i-1,i);
						 Cp1 = incorrectWord.substring(i,i+1);
					 }
					 break;
				 }
			  }
			  score = insertionMatrix[letter2num.get(Cp)][letter2num.get(Cp1)];
		  } 
		  else if(correctWord.length()==incorrectWord.length()) //Substitution
		  {
			  for(int i=0;i<incorrectWord.length();i++)
			  {
				 if(!(correctWord.substring(i,i+1).equals(incorrectWord.substring(i,i+1))))
				 {
					 Cp = correctWord.substring(i,i+1);
					 Cp1 = incorrectWord.substring(i,i+1);
					 break;
				 }
			  }
			  score = substitutionMatrix[letter2num.get(Cp)][letter2num.get(Cp1)];
		  }
		  else
		  {
			  for(int i=0;i<incorrectWord.length();i++)
			  {
				 if(!(correctWord.substring(i,i+1).equals(incorrectWord.substring(i,i+1))))
				 {
					 Cp = correctWord.substring(i,i+1);
					 Cp1 = incorrectWord.substring(i+1,i+2);
					 break;
				 }
			  }
			  score = substitutionMatrix[letter2num.get(Cp)][letter2num.get(Cp1)];
		  }
		  return score;
	  }
}