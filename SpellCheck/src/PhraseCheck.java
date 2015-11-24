import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class PhraseCheck {

	@SuppressWarnings("unchecked")
	public HashMap<String, Double>[] Ngrams = (HashMap<String, Double>[]) new HashMap[4];
	//public HashMap<String, Double>[] NgramsFull = (HashMap<String, Double>[]) new HashMap[4];
	
	public void createNgrams() throws IOException {
		String[] fileNames = new String[4];
		for (int i = 0; i < 4; i++) {
			Ngrams[i] = new HashMap<String, Double>();
			//NgramsFull[i] = new HashMap<String, Double>();
			fileNames[i] = "/home/tanmay/workspace/SpellCheck/src/w"
					+ String.valueOf(i + 2) + "_.txt";
		}
		double a;
		double current = 0;
		String S = null, f = null, part6 = null,ngram="";
		String[] parts = null;
		//for (int n = 0; n == 0; n++)
		 for(int n=0;n<4;n++)
		{
			String input = null;
			BufferedReader br = new BufferedReader(new FileReader(new File(fileNames[n])));
			// Scanner input = new Scanner(new File(fileNames[n]));
			int counter = 0;
			// while (input.hasNext()) {
	         while ((input = br.readLine()) != null) {
			//	System.out.println(counter);
	        	 ngram = "";
	        	 if (counter == 1240) {
					// System.out.println("debug");
				}
				counter++;
				// f = input.nextLine();
				f = input;
				S = f.toLowerCase();
				parts = S.split("\\s+");
				// System.out.println(parts.length);
				a = Long.parseLong(parts[0]);
			///////////////////////////////for context words/////////////////////////////////
				for (int i = 1; i < parts.length; i++) {
					for (int j = 1; j < parts.length; j++) {
						if (i != j) {
							part6 = parts[i].concat(" " + parts[j]);
							if (Ngrams[n].containsKey(part6)) {
								// System.out.println("Here1");
								current = Ngrams[n].get(part6);
								Ngrams[n].put(part6, current + a);
							} else {
								Ngrams[n].put(part6, a);
								// System.out.println(Ngrams[n]);
								// System.out.println("Here2");
							}
						}
					}
				}
			}
	         
//////////////////////////////for collocations////////////////////////////////////	
/*	
for(int i=1;i<parts.length-1;i++)
{
 //ngram = ngram + parts[i] + " ";
   ngram = ngram.concat(parts[i].concat(" "));
}
ngram = ngram.concat(parts[parts.length - 1]);
NgramsFull[n].put(ngram,a);
*/



	         
		//	System.out.println(f);
			//System.out.println(Ngrams[n]);
			//input.close();
		}
	}
}
