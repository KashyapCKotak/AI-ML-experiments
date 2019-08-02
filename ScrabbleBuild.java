package janeStreet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;


class SeqWord{
	public ArrayList<SeqWord> prev=new ArrayList<SeqWord>();
	public ArrayList<SeqWord> next=new ArrayList<SeqWord>();
	String word="";
	public SeqWord(String word){
		this.word=word;
	}
	public SeqWord(String word,SeqWord neighbor,String dir){
		this.word=word;
		if(dir.equalsIgnoreCase("prev"))
			this.prev.add(neighbor);
		if(dir.equalsIgnoreCase("next"))
			this.next.add(neighbor);
	}
	public void addPrev(SeqWord prev){
		this.prev.add(prev);
	}
	public void addNext(SeqWord next){
		this.next.add(next);
	}
}

public class ScrabbleBuild {
	public static int matCounter=0;
	public static char[][] wordMatrix=new char[6][6];
//	public static TreeMap<Integer,Integer> wordMap=new TreeMap<Integer, Integer>();//<CharIndex,Position>
	public static HashMap<Integer,char[][]> freezedCharMatrix=new HashMap<Integer,char[][]>();
	public static HashSet<Character> usedConsonants=new HashSet<Character>();
	public static ArrayList<ArrayList<ArrayList<Integer>>> pathMatrix=new ArrayList<ArrayList<ArrayList<Integer>>>();
	public static HashMap<String,SeqWord> wordList=new HashMap<String,SeqWord>();
	public static HashMap<String,Integer> listWithScores=new HashMap<String,Integer>();
	public static TreeMap<Integer,ArrayList<String>> listWithScoreKeys=new TreeMap<Integer,ArrayList<String>>();
	public static HashMap<Character,TreeSet<String>> listWithCharKeys=new HashMap<Character, TreeSet<String>>();
	public static HashMap<Integer,Integer> matWiseEndMaskList=new HashMap<Integer,Integer>(){
		{put(0,-99);put(1,-99);put(2,-99);put(3,-99);}
	};
	static HashSet<Character> consonants=new HashSet<Character>() {
		{
			add('b');add('c');add('d');add('f');add('g');add('h');add('j');add('k');add('l');add('m');add('n');add('p');add('q');add('r');add('s');add('t');add('v');add('w');add('x');add('y');add('z');
		}
	};
	static Comparator<String> comparator = new Comparator<String>() {
		@Override
		public int compare(String s1, String s2) {
			return getWordScore(s2)-getWordScore(s1);//Word with more score is smaller in ascending order
		}
	};
//	public static boolean validInMatrix(int x,int y, int mat){
//		if(mat==0 && x<=2 && y<=2)
//			return true;
//		if(mat==1 && x<=3 && y<=3)
//		return false;
//	}
	public static int[] getIndex(int posId){
		return new int[]{(int) Math.floor(posId/6),(int) (posId%6)};
	}
	public static int getPos(int x, int y){
		return ((6*x)+y);
	}
	public static int getWordScore(String word){
		Map<Character,Integer> letterScores=new HashMap<Character,Integer>(){
			{put('a',1);put('b',3);put('c',3);put('d',2);put('e',1);put('f',4);put('g',2);put('h',4);put('i',1);put('j',8);put('k',5);put('l',1);put('m',3);put('n',1);put('o',1);put('p',3);put('q',10);put('r',1);put('s',1);put('t',1);put('u',1);put('v',4);put('w',4);put('x',8);put('y',4);put('z',10);
			}};
		int score=0;
		for(int i=0;i<word.length();i++) {
			score+=letterScores.get(word.charAt(i));
		}
		return score;
	}
	
	public static void getListWithScores(HashMap<String,Integer> listWithScores,ArrayList<String> words) {
		for(String word : words) {
			char lastChar='&';
			boolean skipFlag=false;
			if(word.charAt(0)=='u'||word.charAt(word.length()-1)=='u')
				continue;
			for(int i=0;i<word.length();i++) {
				char currChar=word.charAt(i);
				if((currChar==lastChar) || (lastChar=='u' && currChar=='i') || (lastChar=='i' && currChar=='u')
						|| (lastChar=='u' && currChar=='o') || (lastChar=='o' && currChar=='u'))
					skipFlag=true;
				lastChar=currChar;
			}
			if(skipFlag){
//				System.out.println("Word skipped:"+word);
				continue;
			}
			int score=getWordScore(word);
			if(score>10)
				listWithScores.put(word, score);
		}		
	}
	
	public static void getListWithScoreKeys(HashMap<String,Integer> listWithScores,TreeMap<Integer,ArrayList<String>> listWithScoreKeys) {
		int count=0;
		for(String word : listWithScores.keySet()) {
			count++;
			ArrayList<String> currList=listWithScoreKeys.get(listWithScores.get(word));
			if(currList==null) {
				ArrayList<String> newList=new ArrayList<String>() {
					{
						add(word);
					}
				};
//				System.out.println("currList size:0 for score:"+listWithScores.get(word));
//				System.out.println("currList size:"+newList.size()+" for score:"+listWithScores.get(word));
				listWithScoreKeys.put(listWithScores.get(word), newList);
			} else {
//				System.out.println("currList size:"+currList.size()+" for score:"+listWithScores.get(word));
				currList.add(word);
//				System.out.println("currList size:"+currList.size()+" for score:"+listWithScores.get(word));
				listWithScoreKeys.put(listWithScores.get(word), currList);
			}
		}
		System.out.println("Total number of words processed:"+count);
	}
	
//	public static TreeMap<Integer, String> get3WordSeq(TreeMap<Integer,ArrayList<String>> listWithScoreKeys) {
//		int[] scoreKeys=listWithScoreKeys.keySet().stream().mapToInt(Integer::intValue).toArray();
//		int currMaxScoreIntKey=listWithScoreKeys.lastKey();
//		TreeMap<Integer,String> seqWords=new TreeMap<Integer,String>();
//		int count=1;
//		while(seqWords.size()!=4) {
//			System.out.println("Size for current score:"+listWithScoreKeys.get(currMaxScoreIntKey).size());
//			for(String maxWord : listWithScoreKeys.get(currMaxScoreIntKey)) {
//				System.out.println("checking for word:"+maxWord);
//				if(seqWords.isEmpty()) { // first word
//					seqWords.put(0, maxWord);
//					continue;
//				}
//				String currLastWord=seqWords.get(seqWords.lastKey());
//				String currFirstWord=seqWords.get(seqWords.firstKey());
//				if(maxWord.charAt(maxWord.length()-1)==currFirstWord.charAt(0))
//					seqWords.put((seqWords.firstKey()-1),maxWord);
//				else if(maxWord.charAt(0)==currLastWord.charAt(currLastWord.length()-1))
//					seqWords.put((seqWords.lastKey()+1),maxWord);
//				if(seqWords.size()==4)
//					break;
//			}
//			count+=1;
//			System.out.println("scoreKeys index:"+(scoreKeys.length-count)+"current max weight:"+scoreKeys[scoreKeys.length-count]);
//			System.out.println("Next Max:"+(scoreKeys[scoreKeys.length-count]));
//			currMaxScoreIntKey=scoreKeys[scoreKeys.length-count];
//		}
//		return seqWords;
//	}
	
	public static void getListWithCharKeys(HashMap<String,Integer> listWithScores, HashMap<Character,TreeSet<String>> listWithCharKeys) {
		for(String word : listWithScores.keySet()) {
			if(listWithCharKeys.get(word.charAt(0))==null) {
				TreeSet<String> currSet=new TreeSet<String>(comparator);
				currSet.add(word);
				listWithCharKeys.put(word.charAt(0), currSet);
			} else {
				TreeSet<String> currSet=listWithCharKeys.get(word.charAt(0));
				currSet.add(word);
				listWithCharKeys.put(word.charAt(0), currSet);
			}
		}
	}
	
	public static void printMatrix() {
		System.out.println("Char Matrix");
		for(int i=0;i<6;i++) {
			for(int j=0;j<6;j++) {
				System.out.print(" "+wordMatrix[i][j]);
			}System.out.println();
		}
		System.out.println("Path Matrix");
		for(int j=0;j<6;j++) {
			for(int i=0;i<6;i++) {
				ArrayList<ArrayList<Integer>> currCol=pathMatrix.get(i);
				ArrayList<Integer> rowCell=currCol.get(j);
				System.out.print("[");
				for(Integer cell : rowCell) {
					System.out.print(cell+",");
				}
				System.out.print("]\t");
			}System.out.println();
		}
	}
//	public static void resetSeqMatrix() {
//		for(int i=0;i<6;i++) {
//			for(int j=0;j<6;j++) {
//				seqMatrix[i][j]=1000;
//			}
//		}
//		System.out.println("seq matrix reset");
//	}
	public static void makeMatrix() {
		for(int i=0;i<6;i++) {
			for(int j=0;j<6;j++) {
				wordMatrix[i][j]='-';
			}
		}
		for(int j=0;j<6;j++) {
			ArrayList<ArrayList<Integer>> currCol=new ArrayList<ArrayList<Integer>>();
			for(int i=0;i<6;i++) {
				ArrayList<Integer> currRowCell=new ArrayList<Integer>();
				currRowCell.add(-99);
				currCol.add(currRowCell);
			}
			pathMatrix.add(currCol);
		}
		wordMatrix[0][1]='o';wordMatrix[0][3]='e';wordMatrix[0][5]='u';
		wordMatrix[1][0]='i';wordMatrix[1][2]='a';wordMatrix[1][4]='a';
		wordMatrix[2][1]='e';wordMatrix[2][3]='i';wordMatrix[2][5]='o';
		wordMatrix[3][0]='a';wordMatrix[3][2]='o';wordMatrix[3][4]='e';
		wordMatrix[4][1]='e';wordMatrix[4][3]='a';wordMatrix[4][5]='i';
		wordMatrix[5][0]='u';wordMatrix[5][2]='i';wordMatrix[5][4]='o';
		System.out.println("Matrix reset");
	}
	
	
	
	public static HashMap<Integer,ArrayList<Integer>> findStartEndIndex(String word, int mat) {
		HashMap<Integer,ArrayList<Integer>> indexes=new HashMap<Integer, ArrayList<Integer>>();
		indexes.put(0,new ArrayList<Integer>());indexes.put(1,new ArrayList<Integer>());indexes.put(10,new ArrayList<Integer>());indexes.put(11,new ArrayList<Integer>());
		char startChar=word.charAt(0),endChar=word.charAt(word.length()-1);
		boolean startCharPresent=false,endCharPresent=false;
		for(int i=mat;i<mat+3;i++) {
			for(int j=mat;j<mat+3;j++) {
				int posId=getPos(i, j);
				if(wordMatrix[i][j]==startChar) {
					indexes.get(0).add(posId);		// start indexes
					startCharPresent=true;
				}
				if(wordMatrix[i][j]==endChar) {
					indexes.get(1).add(posId);
					endCharPresent=true;			// end indexes
				}
				if(wordMatrix[i][j]=='-') {
					indexes.get(10).add(posId);		// start indexes
					indexes.get(11).add(posId);		// end indexes
				}
			}
		}
		if(!startCharPresent && indexes.get(10) != null)				// Ignore the blank cell if char already present
			indexes.get(0).addAll(indexes.get(10));
		else indexes.remove(10);
		if(!endCharPresent && indexes.get(11) != null)
			indexes.get(1).addAll(indexes.get(11));
		else indexes.remove(11);
		return indexes;
	}
	
	public static ArrayList<int[]> getNext(int x, int y, char nextChar){
		ArrayList<int[]> neighbours=new ArrayList<>();
		boolean isNextVowel=false;boolean isCurrVowel=false;
		System.out.println("finding next pos for char:"+nextChar+" already?:"+usedConsonants.contains(nextChar));
		if(!consonants.contains(nextChar))//it is vowel
			isNextVowel=true;
		if(!consonants.contains(wordMatrix[x][y]))
			isCurrVowel=true;
		if((x!=5 && y!=0) && (wordMatrix[x+1][y-1]=='-' || wordMatrix[x+1][y-1]==nextChar) && !(isCurrVowel ^ isNextVowel) && !usedConsonants.contains(nextChar))
			neighbours.add(new int[]{x+1,y-1});
		if(y!=0 && (wordMatrix[x][y-1]=='-' || wordMatrix[x][y-1]==nextChar) && (!isNextVowel || wordMatrix[x][y-1]==nextChar)  && !usedConsonants.contains(nextChar))
			neighbours.add(new int[]{x,y-1});
		if((x!=0 && y!=0) && (wordMatrix[x-1][y-1]=='-' || wordMatrix[x-1][y-1]==nextChar) && !(isCurrVowel ^ isNextVowel)  && !usedConsonants.contains(nextChar))
			neighbours.add(new int[]{x-1,y-1});
		if(x!=0 && (wordMatrix[x-1][y]=='-' || wordMatrix[x-1][y]==nextChar) && (!isNextVowel || wordMatrix[x-1][y]==nextChar)  && !usedConsonants.contains(nextChar))
			neighbours.add(new int[]{x-1,y});
		if((x!=0 && y!=5) && (wordMatrix[x-1][y+1]=='-' || wordMatrix[x-1][y+1]==nextChar) && !(isCurrVowel ^ isNextVowel)  && !usedConsonants.contains(nextChar))
			neighbours.add(new int[]{x-1,y+1});
		if(y!=5 && (wordMatrix[x][y+1]=='-' || wordMatrix[x][y+1]==nextChar) && (!isNextVowel || wordMatrix[x][y+1]==nextChar)  && !usedConsonants.contains(nextChar))
			neighbours.add(new int[]{x,y+1});
		if((x!=5 && y!=5) && (wordMatrix[x+1][y+1]=='-' || wordMatrix[x+1][y+1]==nextChar) && !(isCurrVowel ^ isNextVowel)  && !usedConsonants.contains(nextChar))
			neighbours.add(new int[]{x+1,y+1});
		if(x!=5 && (wordMatrix[x+1][y]=='-' || wordMatrix[x+1][y]==nextChar) && (!isNextVowel || wordMatrix[x+1][y]==nextChar)  && !usedConsonants.contains(nextChar))
			neighbours.add(new int[]{x+1,y});
		return neighbours;
	}
	
	public static boolean canDetele(int x,int y,int mat,int currIndex) {
		if(pathMatrix.get(y).get(x).get(0)==mat) {
			int i=0;
			for(int charInd : pathMatrix.get(y).get(x)) {
				if(i==0) {
					i++;
					continue;
				}
				if(charInd<currIndex){
					System.out.println("charindex to check for delete:"+currIndex+" x:"+x+" y:"+y+" currChar at x,y:"+pathMatrix.get(y).get(x)+" result=false");
					return false;
				}
			}
		}
		System.out.println("charindex to check for delete:"+currIndex+" x:"+x+" y:"+y+" currChar at x,y value:"+pathMatrix.get(y).get(x)+" result=true");
		return true;
	}
	
	public static void pushPathMatrix(int x, int y, int mat, int charIndex) {
		if(pathMatrix.get(y).get(x).get(0)==-99) {
			pathMatrix.get(y).get(x).clear();
			pathMatrix.get(y).get(x).add(mat);
		}
		pathMatrix.get(y).get(x).add(charIndex);
	}
	public static void popPathMatrix(int x, int y, int mat, int charIndex) {
		System.out.println("entered pop. x:"+x+" y:"+y+" mat:"+mat+" charIndex:"+charIndex);
		if(pathMatrix.get(y).get(x).get(0)==mat) {
			int ind=pathMatrix.get(y).get(x).lastIndexOf(charIndex);
			System.out.println("in pop, got index:"+ind+" for cahrIndex:"+charIndex);
			if(ind!=0 && ind!=-1)
				pathMatrix.get(y).get(x).remove(ind);
			if(pathMatrix.get(y).get(x).size()==1){
				pathMatrix.get(y).get(x).set(0, -99);
			}
			if(pathMatrix.get(y).get(x).size()==1 && pathMatrix.get(y).get(x).get(0)==mat && consonants.contains(wordMatrix[x][y])){
				System.out.println("removing from used constants "+wordMatrix[x][y]);
				usedConsonants.remove(wordMatrix[x][y]);
			}
		}
	}
	
	
	public static boolean fitSeq(String word,int x,int y,int charIndex, int mat){
		boolean result=false;
		System.out.println("finding in word "+word+" for char index:"+charIndex+" in mat:"+mat);
		System.out.println("Curr pos x:"+x+",y:"+y);
		ArrayList<int[]> nextPos=null;
		int posId=getPos(x,y);
		if(charIndex==0){
			wordMatrix[x][y]=word.charAt(charIndex);
			if(consonants.contains(word.charAt(charIndex)))
				usedConsonants.add(word.charAt(charIndex));
			pushPathMatrix(x, y, mat, charIndex);
//			wordMap.put(charIndex, posId);
		}
		if(charIndex==(word.length()-1)){
			System.out.println("The current end posId:"+posId);
			System.out.println("the current mat:"+mat+" mask id:"+matWiseEndMaskList.get(mat));
		}
		if(!(charIndex==(word.length()-1))){
			nextPos=getNext(x,y,word.charAt(charIndex+1));
			System.out.println("next possible poss:"+nextPos.size());
		}
		if(charIndex==(word.length()-1) && !(matWiseEndMaskList.get(mat)==posId)){
			System.out.println("valid word ending");
			if((x<=(mat+2) && y<=(mat+2)) && (x>mat && y>mat)){
				matWiseEndMaskList.put(mat,posId);
				return true;
			}
			else {System.out.println("returning false");return false;}
		} 
		else if (charIndex==(word.length()-1) && (matWiseEndMaskList.get(mat)==posId))
			return false;
		for(int[] next : nextPos){
			if(word.equals("hydroxyzines"))
				System.out.println("here");
			int nextPosId=getPos(next[0], next[1]);
//			wordMap.put((charIndex+1), nextPosId);
			System.out.println("Next pos:"+next[0]+","+next[1]);
			wordMatrix[next[0]][next[1]]=word.charAt(charIndex+1);
			if(consonants.contains(word.charAt(charIndex+1)))
				usedConsonants.add(word.charAt(charIndex+1));
			pushPathMatrix(next[0],next[1], mat, (charIndex+1));
			printMatrix();
			result=fitSeq(word, next[0], next[1], charIndex+1, mat);
			System.out.println("returned to:"+(word.charAt(charIndex))+" with result:"+result+" charIndes:"+charIndex+" in mat:"+mat);
			if(!result)
				popPathMatrix(next[0], next[1], mat, (charIndex+1));
			if(canDetele(next[0], next[1], mat, (charIndex+1)) && (!result) && consonants.contains(word.charAt(charIndex+1))) {
				wordMatrix[next[0]][next[1]]='-';
				System.out.println("deleted here");
			}
			else if(result)
				break;
		}
		if(!result)
			popPathMatrix(x, y, mat, charIndex);
		if(canDetele(x, y, mat, (charIndex)) && (!result) && consonants.contains(wordMatrix[x][y]) && charIndex==0) {
			System.out.println("char index:"+charIndex);
			System.out.println("deleting here");
			if(word.equals("hydroxyzines"))
				System.out.println("here");
			wordMatrix[x][y]='-';
//			wordMap.remove((charIndex));
		}
		printMatrix();
		return result;
	}

	public static boolean makeSeq(String maxWord,int mat, int[] startIndexes) {
		HashMap<Integer, ArrayList<Integer>> indexes = findStartEndIndex(maxWord, mat);
		if(mat!=0){
			indexes.put(0,new ArrayList<Integer>(){
				{add(getPos(startIndexes[0],startIndexes[1]));}	
			});
		}
		System.out.println("Current word for makeSeq:" + maxWord + " in mat:"+mat);
		System.out.println("possible end indexes:"+indexes.get(1).size());
		if (indexes.get(0).size() == 0 || indexes.get(1).size() == 0)
			return false;
		// got start and end indexes. Go ahead
		boolean result = false;
		for (int index : indexes.get(0)) {// for every start index
			System.out.println("------ fitting word "+maxWord+" for new beginning -------");
			for (int e = 0; e < indexes.get(1).size(); e++) {// for every end index in that start index 
				int[] currIdx = getIndex(index);
				System.out.println("------ fitting word "+maxWord+" for new end -------");
				result = fitSeq(maxWord, currIdx[0], currIdx[1], 0, mat);
				indexes.get(1).remove(matWiseEndMaskList.get(mat));
				System.out.println("returned with result:" + result + " in mat:"+mat+" for word:"+maxWord);
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.out.println("Current mat counter:"+mat+" With last end at:"+matWiseEndMaskList.get(mat));
				printMatrix();
				if(!result){ // result
					e=1000;//skip for all other "ends". 1000 is impossible
//					indexes.get(1).clear();
				}
				else if (result && mat!=3) {// Go for the second word
					freezedCharMatrix.put(mat, wordMatrix.clone());
					char currLastChar=maxWord.charAt(maxWord.length()-1);
					TreeSet<String> newMaxWordSet=listWithCharKeys.get(currLastChar);
					System.out.println("################### START POSITION FOR NEXT WORD:"+matWiseEndMaskList.get(mat));
					int[] startIndex=getIndex(matWiseEndMaskList.get(mat));
					matCounter++;
					for(String newMaxWord : newMaxWordSet){
						result=false;
						char[][] currMatrix= wordMatrix.clone();
						result=makeSeq(newMaxWord,mat+1,startIndex);
						if(!result)
							wordMatrix=currMatrix.clone();
						System.out.println("got child result for mat:"+(mat+1)+" as:"+result+" for word:"+newMaxWord);
						if(result)
							break;
					}
				}
				else if(result && mat==3){
					System.out.println("returned from mat:"+mat);
					return true;
				}
				if(result)// got true from child. not need to check for other endings
					break;
//				else if(!result && mat!=0 && mat!=1)//got false from child after all childs
//					wordMatrix=freezedCharMatrix.get(mat-2);
//				else if(!result && mat==1)
//					makeMatrix();
			}
			if(result){// got true from child. not need to check for other startings
				break;
			}
		}
		if (result) {
			printMatrix();
		}
		System.out.println("returned from mat:"+mat+" with word:"+maxWord);
		return result;
	}

	public static void get3WordSeq(TreeMap<Integer,ArrayList<String>> listWithScoreKeys, HashMap<String, SeqWord> wordList) {
		int[] scoreKeys=listWithScoreKeys.keySet().stream().mapToInt(Integer::intValue).toArray();
		int currMaxScoreIntKey=listWithScoreKeys.lastKey();
		int count=1;
		while(scoreKeys.length>=count) {
			System.out.println("Size for current score:"+listWithScoreKeys.get(currMaxScoreIntKey).size());
			for(String maxWord : listWithScoreKeys.get(currMaxScoreIntKey)) {
				SeqWord currSeqWord=new SeqWord(maxWord);
				for(String listWord : wordList.keySet()){
					SeqWord currListSeqWord=wordList.get(listWord);
					if(maxWord.charAt(maxWord.length()-1)==listWord.charAt(0)){
						currSeqWord.addNext(currListSeqWord);
						currListSeqWord.addPrev(currSeqWord);
					}
					else if(maxWord.charAt(0)==listWord.charAt(listWord.length()-1)){
						currSeqWord.addPrev(currListSeqWord);
						currListSeqWord.addNext(currSeqWord);
					}
				}
				wordList.put(maxWord, currSeqWord);
			}
			count+=1;
			if(count>scoreKeys.length)
				break;
			System.out.println("scoreKeys index:"+(scoreKeys.length-count)+"current max weight:"+scoreKeys[scoreKeys.length-count]);
			System.out.println("Next Max:"+(scoreKeys[scoreKeys.length-count]));
			currMaxScoreIntKey=scoreKeys[scoreKeys.length-count];
		}
	}


	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<String> words = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("./MerriamWebsterWordList.csv"));) {
		    while (scanner.hasNextLine()) {
		        words.add(scanner.nextLine());
		    }
		}
		System.out.println("Sample word read:"+words.get(0));
		System.out.println("Total number of words read:"+words.size());
		getListWithScores(listWithScores,words);
		System.out.println("Sample word with score:"+listWithScores.get("oxyphenbutazone"));
		System.out.println("Total number of valid words:"+listWithScores.size());
		getListWithScoreKeys(listWithScores,listWithScoreKeys);
		System.out.println("Sample scores with words:"+listWithScoreKeys.get(listWithScoreKeys.lastKey()).toString());
		System.out.println("All Score Keys:"+listWithScoreKeys.keySet());
		System.out.println("Number of words with score 19:"+listWithScoreKeys.get(19).size());
		System.out.println("Max Score:"+listWithScoreKeys.lastKey());
		getListWithCharKeys(listWithScores, listWithCharKeys);
		System.out.println("Sample list with char keys:");
		System.out.println("Word with e:"+listWithCharKeys.get('e'));
//		for(String word : listWithCharKeys.get('z')) {
//			System.out.println("Word:"+word+" score:"+listWithScores.get(word));
//		}
		makeMatrix();
		printMatrix();
//		makeSeq();
		int[] scoreKeys=listWithScoreKeys.keySet().stream().mapToInt(Integer::intValue).toArray();
		boolean result=false;
		for(int i=(scoreKeys.length-1);i>=0;i--) {
			System.out.println("################### CHANGED SCORE. CURRENT SCORE:"+scoreKeys[i]);
			ArrayList<String> maxWords=listWithScoreKeys.get(scoreKeys[i]);
			for(String maxWord : maxWords) {
				result=makeSeq(maxWord,matCounter,new int[]{0,0});
				if(result)
					break;
			}
			if(result)
				break;
		}
		System.out.println("here");
		printMatrix();
//		get3WordSeq(listWithScoreKeys,wordList);
//		System.out.println("Sample Sequence:");
//		System.out.println(wordList.get("oxyphenbutazone").next.get(0).word);
//		System.out.println(wordList.get("oxyphenbutazone").next.get(1).word);
//		System.out.println(wordList.get("oxyphenbutazone").next.get(2).word);		
	}
}

//exoerythrocytic
//extemporization
//extemporizing
