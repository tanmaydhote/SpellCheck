import java.io.*;
import java.util.*;
import java.io.*;
import java.util.Scanner;
public class SpellCheck
{	public static final int MAXCONTEXTSIZE = 3;
	public static ArrayList<String> stopWords = new ArrayList<String>();
	public static ArrayList<String> correctWords = new ArrayList<String>();
	public static LinkedHashMap<String, Double> candidateScores = new LinkedHashMap<String , Double>();
	public static void main(String[] args) throws Exception
	{
	//	runWordSpellCheck();
		//runPhraseSpellCheck();
		runSentenceSpellCheck();
	}
	
	public static void runWordSpellCheck() throws Exception
    {
		//read the input words
		ArrayList<String> corrections = new ArrayList<String>();
		float score = 0;
		File file = new File("/home/tanmay/workspace/SpellCheck/src/words.txt");
        if(file.length() == 0)
        {
            System.out.println("File is empty");
        }
        else
        {
            BufferedReader fr = new BufferedReader(new FileReader(file));
            ArrayList<String> words = new ArrayList<String>();
            ArrayList<String> outputWords = new ArrayList<String>();;
            String[] line;
            String str;
            while((str=fr.readLine()) != null)
            {
                line = str.split(" ");
               /* for(String word : line)
                    words.add(word); */
                words.add(line[0]);
                outputWords.add(line[1]);
            }
            System.out.println(words);
            System.out.println(outputWords);
            WordCheck w = new WordCheck();
            w.createDictionary();
            w.createConfusionMatrix();
            // Printing the content of words
            int wordCount = 0;
            for(String word : words)
            {
           // for(String word : words)
            score = 0;
            long t1 = System.currentTimeMillis();
            w.getCandidates(word);
            corrections = w.candidates;
            System.out.println(System.currentTimeMillis()-t1);
            System.out.print(word+"		");
            for (int p = 0; p < corrections.size(); p++) {
            	System.out.print(corrections.get(p)+"	");
            	if(corrections.get(p).equals(outputWords.get(wordCount)))
            		score = 1/(float)(p+1);
                if(p==4) break;
            }
            System.out.print("Score =" + score +"\n");
            wordCount++;
            }
            } 
    }
	
	public static void runPhraseSpellCheck() throws Exception
    {
		String S,collocatedString ="";
		Scanner enable = new Scanner(new File("/home/tanmay/workspace/SpellCheck/src/enable1.txt"));
		while(enable.hasNext()){
			S = enable.next();
			//a = input.nextLong();
			//System.out.print(S+"\t"+ a+"\t");
			correctWords.add(S);
		}
		enable = new Scanner(new File("/home/tanmay/workspace/SpellCheck/src/stop.txt"));
		while(enable.hasNext()){
			S = enable.next();
			//a = input.nextLong();
			//System.out.print(S+"\t"+ a+"\t");
			stopWords.add(S);
		}
		ArrayList<String> misspeltWords = new ArrayList<String>();
		ArrayList<Integer> misspeltWordsIndex = new ArrayList<Integer>();
		ArrayList<String> corrections = new ArrayList<String>();
		ArrayList<String> finalCorrections = new ArrayList<String>();
		ArrayList<String> phraseWords = new ArrayList<String>();
		int count;
		WordCheck w = new WordCheck();
        w.createDictionary();
        w.createConfusionMatrix();
        double candidateScore = 0;
        PhraseCheck p = new PhraseCheck();
        p.createNgrams();
        System.out.println("Preprocessing Done");
        String input = null;
        String phrase = null;
        BufferedReader br = new BufferedReader(new FileReader(new File("/home/tanmay/workspace/SpellCheck/src/phrases.txt")));	
        while ((input = br.readLine()) != null) {
        long t1 = System.currentTimeMillis();
        misspeltWords.clear();
        misspeltWordsIndex.clear();
        //phrase = input.toLowerCase();
        phrase = input;
       // String[] words = null;
        ArrayList<String> words = new ArrayList<String>();
        String[] words1 = phrase.split(" ");
      /*  for(int i=0;i<words1.length;i++)
        {
        	System.out.print(words1[i] + " ");
        }*/
        for(int wordc=0;wordc<words1.length/2;wordc++)
        {
        	words.add(words1[wordc]); 
        }
       /*
        for(int wordCount=0;wordCount<words.length;wordCount++)
        {
        	phraseWords.add(words[wordCount]);
        }
        
		        for (String word : phraseWords)  
		        {  
		           if(!correctWords.contains(word)&&!stopWords.contains(word)) 
		           {
		        	   misspeltWords.add(word);
		        	   misspeltWordsIndex.add(phraseWords.indexOf(word));
		           }   
		        }*/
        int wordc1 = 0;
        for (String word : words)  
        {  
        //	System.out.println("Wordc = " + wordc1);
           if(!correctWords.contains(word)&&!stopWords.contains(word)) 
           {
        	   misspeltWords.add(word);
        	 //  System.out.println("Word is "+word);
        	   misspeltWordsIndex.add(wordc1);   
           }   
           wordc1++;
        }
        wordc1 =0;
		//        System.out.println(misspeltWords);

		        
		        			/*Collocation Based*/
		    /*
		    int misspeltWordCount = 0;
		    for (String misspeltWord : misspeltWords)    
		    {
		    	w.getCandidates(misspeltWord);
		    	corrections = w.candidates;
		    	candidateScores.clear();
		    	for(String correction : corrections)
		    	{
		    	for(int collocSizeLeft = 1;collocSizeLeft<MAXCONTEXTSIZE;collocSizeLeft++)
		    	{
		    		for(int collocSizeRight = 1;collocSizeRight<MAXCONTEXTSIZE;collocSizeRight++)
		    		{
		    			collocatedString= "";
		    			for(int size = 0;size<collocSizeLeft;size++)
		    			{
		    				if(!(misspeltWordsIndex.get(misspeltWordCount)-size<0))
		    					collocatedString += phraseWords.get(misspeltWordsIndex.get(misspeltWordCount)-size)+" "; 
		    			}
		    		collocatedString +=correction;
		    		for(int size = 0;size<collocSizeRight;size++)
	    			{
	    				if(misspeltWordsIndex.get(misspeltWordCount)+size<phraseWords.size())
	    					collocatedString += phraseWords.get(misspeltWordsIndex.get(misspeltWordCount)-size)+" "; 
	    			}
		    		collocatedString = collocatedString.substring(0,collocatedString.length());
		    		}
		    		if(collocatedString.length()<6 && collocatedString.length()>1)
		    		{
		    			candidateScore += p.NgramsFull[collocatedString.length()-2].get(collocatedString);
		    		}
		    		
		    	}
		    	candidateScores.put(correction, candidateScore);
		    	candidateScore = 0.0;
		    }
		    	misspeltWordCount++;
		    }
		        
		    */
		    
		        			/* Context Based*/    
	        
       // System.out.println(words1.length);
        for (String misspeltword : misspeltWords)
	        {
        		float score = 0;
        //		System.out.println(words1.length);
        	//	System.out.println("wordc =" + wordc1 + "words1.length/2 " + words1.length/2 + "misspeltwordindex " +misspeltWordsIndex.get(wordc1) );
        		int total = misspeltWordsIndex.get(wordc1) + words1.length/2;
        	//	System.out.println("Total = " + total );
        		String correctWord = words1[misspeltWordsIndex.get(wordc1) + words1.length/2];
        	//	System.out.println("Correct Word " + correctWord);
        		wordc1++;
        		finalCorrections.clear();
		        candidateScores.clear();
		        w.getCandidates(misspeltword);
		        corrections = w.candidates;
		      //  System.out.println(corrections);
		        //System.out.println("Over");
		        /*   System.out.println(p.Ngrams[0].get("ground turmeric"));
		        System.out.println(p.Ngrams[1].get("baby carriage"));
		        System.out.println(p.Ngrams[2].get("moon earth"));
		        System.out.println(p.Ngrams[3].get("perspective society"));*/
		        for(String correction : corrections)
		        {
		        	if(!stopWords.contains(correction))
		        	{
		        	candidateScore = 0;
		        	candidateScores.put(correction, 0.0);
		        	for(String word : words)
		        	{
		        		if(!stopWords.contains(word))
		        		{
		        			for(int i=0;i<4;i++)
		        			{
		        				if(p.Ngrams[i].containsKey(correction+" "+word))
		        					candidateScore  += p.Ngrams[i].get(correction+" "+word);
		        				else candidateScore += 0;
		        			}
		        		}
		        	}
		        	candidateScores.put(correction,candidateScore);
		        }
		        }
		        LinkedHashMap<String,Double > map = w.sortByValues(candidateScores);
		   //     System.out.println(map);
		        ListIterator<String> iter =
					    new ArrayList(map.keySet()).listIterator(map.size());	
		        	while (iter.hasPrevious()) {
						String key = iter.previous();
					  //  System.out.println(key);
						if(map.get(key)!=0.0)
						{
						finalCorrections.add(key);
						}
						else break;
						if(finalCorrections.size()==3) break;
					}
		        	
		        	//Interpolation
		        	while(finalCorrections.size()<3)
		        	{
		        		 for (int q = 0; q < corrections.size(); q++) {
		        			if(!finalCorrections.contains(corrections.get(q))&&!stopWords.contains(corrections.get(q))) 			
		        			 finalCorrections.add(corrections.get(q));
		                 if(finalCorrections.size()==3) break;
		        		 }
		        	}
		        	System.out.println(System.currentTimeMillis()-t1);
		        	int wc1=0;
		        	for(String word :finalCorrections)
		        	{
		        		if(word.equals(correctWord)) score = 1/(float)(wc1+1);
		        	}
		        	System.out.println(misspeltword + " "+finalCorrections+ "Score = " + score);
    } 
    }
}

	public static void runSentenceSpellCheck() throws Exception
    {
		String S,collocatedString ="";
		Scanner enable = new Scanner(new File("/home/tanmay/workspace/SpellCheck/src/enable1.txt"));
		while(enable.hasNext()){
			S = enable.next();
			//a = input.nextLong();
			//System.out.print(S+"\t"+ a+"\t");
			correctWords.add(S);
		}
		enable = new Scanner(new File("/home/tanmay/workspace/SpellCheck/src/stop.txt"));
		while(enable.hasNext()){
			S = enable.next();
			//a = input.nextLong();
			//System.out.print(S+"\t"+ a+"\t");
			stopWords.add(S);
		}
		ArrayList<String> misspeltWords = new ArrayList<String>();
		ArrayList<Integer> misspeltWordsIndex = new ArrayList<Integer>();
		ArrayList<String> corrections = new ArrayList<String>();
		ArrayList<String> finalCorrections = new ArrayList<String>();
		ArrayList<String> phraseWords = new ArrayList<String>();
		int count;
		WordCheck w = new WordCheck();
        w.createDictionary();
        w.createConfusionMatrix();
        double candidateScore = 0;
        PhraseCheck p = new PhraseCheck();
        p.createNgrams();
        System.out.println("Preprocessing Done");
        String input = null;
        String phrase = null;
        BufferedReader br = new BufferedReader(new FileReader(new File("/home/tanmay/workspace/SpellCheck/src/sentences.txt")));	
        while ((input = br.readLine()) != null) {
        long t1 = System.currentTimeMillis();
        misspeltWords.clear();
        misspeltWordsIndex.clear();
        //phrase = input.toLowerCase();
        phrase = input;
       // String[] words = null;
        ArrayList<String> words = new ArrayList<String>();
        String[] words1 = phrase.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
      /*  for(int i=0;i<words1.length;i++)
        {
        	System.out.print(words1[i] + " ");
        }*/
        for(int wordc=0;wordc<words1.length/2;wordc++)
        {
        	words.add(words1[wordc]); 
        }
       /*
        for(int wordCount=0;wordCount<words.length;wordCount++)
        {
        	phraseWords.add(words[wordCount]);
        }
        
		        for (String word : phraseWords)  
		        {  
		           if(!correctWords.contains(word)&&!stopWords.contains(word)) 
		           {
		        	   misspeltWords.add(word);
		        	   misspeltWordsIndex.add(phraseWords.indexOf(word));
		           }   
		        }*/
        int wordc1 = 0;
        for (String word : words)  
        {  
        //	System.out.println("Wordc = " + wordc1);
           if(!correctWords.contains(word)&&!stopWords.contains(word)) 
           {
        	   misspeltWords.add(word);
        	 //  System.out.println("Word is "+word);
        	   misspeltWordsIndex.add(wordc1);   
           }   
           wordc1++;
        }
        wordc1 =0;
		//        System.out.println(misspeltWords);

		        
		        			/*Collocation Based*/
		    /*
		    int misspeltWordCount = 0;
		    for (String misspeltWord : misspeltWords)    
		    {
		    	w.getCandidates(misspeltWord);
		    	corrections = w.candidates;
		    	candidateScores.clear();
		    	for(String correction : corrections)
		    	{
		    	for(int collocSizeLeft = 1;collocSizeLeft<MAXCONTEXTSIZE+3;collocSizeLeft++)
		    	{
		    		for(int collocSizeRight = 1;collocSizeRight<MAXCONTEXTSIZE+3;collocSizeRight++)
		    		{
		    			collocatedString= "";
		    			for(int size = 0;size<collocSizeLeft;size++)
		    			{
		    				if(!(misspeltWordsIndex.get(misspeltWordCount)-size<0))
		    					collocatedString += phraseWords.get(misspeltWordsIndex.get(misspeltWordCount)-size)+" "; 
		    			}
		    		collocatedString +=correction;
		    		for(int size = 0;size<collocSizeRight;size++)
	    			{
	    				if(misspeltWordsIndex.get(misspeltWordCount)+size<phraseWords.size())
	    					collocatedString += phraseWords.get(misspeltWordsIndex.get(misspeltWordCount)-size)+" "; 
	    			}
		    		collocatedString = collocatedString.substring(0,collocatedString.length());
		    		}
		    		if(collocatedString.length()<6 && collocatedString.length()>1)
		    		{
		    			candidateScore += p.NgramsFull[collocatedString.length()-2].get(collocatedString);
		    		}
		    		
		    	}
		    	candidateScores.put(correction, candidateScore);
		    	candidateScore = 0.0;
		    }
		    	misspeltWordCount++;
		    }
		        
		    */
		    
		        			/* Context Based*/    
	        
       // System.out.println(words1.length);
        for (String misspeltword : misspeltWords)
	        {
        		float score = 0;
        //		System.out.println(words1.length);
        	//	System.out.println("wordc =" + wordc1 + "words1.length/2 " + words1.length/2 + "misspeltwordindex " +misspeltWordsIndex.get(wordc1) );
        		int total = misspeltWordsIndex.get(wordc1) + words1.length/2;
        	//	System.out.println("Total = " + total );
        		String correctWord = words1[misspeltWordsIndex.get(wordc1) + words1.length/2];
        	//	System.out.println("Correct Word " + correctWord);
        		wordc1++;
        		finalCorrections.clear();
		        candidateScores.clear();
		        w.getCandidates(misspeltword);
		        corrections = w.candidates;
		      //  System.out.println(corrections);
		        //System.out.println("Over");
		        /*   System.out.println(p.Ngrams[0].get("ground turmeric"));
		        System.out.println(p.Ngrams[1].get("baby carriage"));
		        System.out.println(p.Ngrams[2].get("moon earth"));
		        System.out.println(p.Ngrams[3].get("perspective society"));*/
		        for(String correction : corrections)
		        {
		        	if(!stopWords.contains(correction))
		        	{
		        	candidateScore = 0;
		        	candidateScores.put(correction, 0.0);
		        	for(String word : words)
		        	{
		        		if(!stopWords.contains(word))
		        		{
		        			for(int i=0;i<4;i++)
		        			{
		        				if(p.Ngrams[i].containsKey(correction+" "+word))
		        					candidateScore  += p.Ngrams[i].get(correction+" "+word);
		        				else candidateScore += 0;
		        			}
		        		}
		        	}
		        	candidateScores.put(correction,candidateScore);
		        }
		        }
		        LinkedHashMap<String,Double > map = w.sortByValues(candidateScores);
		   //     System.out.println(map);
		        ListIterator<String> iter =
					    new ArrayList(map.keySet()).listIterator(map.size());	
		        	while (iter.hasPrevious()) {
						String key = iter.previous();
					  //  System.out.println(key);
						if(map.get(key)!=0.0)
						{
						finalCorrections.add(key);
						}
						else break;
						if(finalCorrections.size()==3) break;
					}
		        	
		        	//Interpolation
		        	while(finalCorrections.size()<3)
		        	{
		        		 for (int q = 0; q < corrections.size(); q++) {
		        			if(!finalCorrections.contains(corrections.get(q))&&!stopWords.contains(corrections.get(q))) 			
		        			 finalCorrections.add(corrections.get(q));
		                 if(finalCorrections.size()==3) break;
		        		 }
		        	}
		        	System.out.println(System.currentTimeMillis()-t1);
		        	int wc1=0;
		        	for(String word :finalCorrections)
		        	{
		        		if(word.equals(correctWord)) score = 1/(float)(wc1+1);
		        	}
		        	System.out.println(misspeltword + " "+finalCorrections+ "Score = " + score);
		        	int misspeltWordIndex = words.indexOf(misspeltword);
		        	words.add(misspeltWordIndex,finalCorrections.get(0));
	        } 
    }
}
	
	
	
}