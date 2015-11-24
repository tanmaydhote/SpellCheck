
public class Levenshtein {	
	
	public int editDistance(String word1,String word2)
	{
		int len1 = word1.length();
		int len2 = word2.length();
		int insert,delete,replace,min;
		int[][] matrix = new int[len1 + 1][len2 + 1];
	 
		for (int i = 0; i <= len1; i++) {
			matrix[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			matrix[0][j] = j;
		}
	 
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update matrix value for +1 length
					matrix[i + 1][j + 1] = matrix[i][j];
				} else {
					insert = matrix[i][j + 1] + 1;
					delete = matrix[i + 1][j] + 1;
					replace = matrix[i][j] + 1;
					
					if(delete>insert) 
					  min = insert;
					else min = delete;
					if(min>replace) min = replace;
					matrix[i + 1][j + 1] = min;
				}
			}
		}
	 
		return matrix[len1][len2];
	}
}
