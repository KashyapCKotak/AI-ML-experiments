package scrabble.build;

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
	public static char[][] wordMatrix=new char[6][6];
	public static ArrayList<ArrayList<ArrayList<Integer>>> pathMatrix=new ArrayList<ArrayList<ArrayList<Integer>>>();
	public static HashMap<String,SeqWord> wordList=new HashMap<String,SeqWord>();
	HashSet<Character> consonants=new HashSet<Character>() {
		{
			add('B');add('C');add('D');add('F');add('G');add('H');add('J');add('K');add('L');add('M');add('N');add('P');add('Q');add('R');add('S');add('T');add('V');add('W');add('X');add('Y');add('Z');
		}
	};
	static Comparator<String> comparator = new Comparator<String>() {
		@Override
		public int compare(String s1, String s2) {
			return getWordScore(s2)-getWordScore(s1);//Word with more score is smaller in ascending order
		}
	};
	public static int getWordScore(String word){
		Map<Character,Integer> letterScores=new HashMap<Character,Integer>(){
			{put('a',1);put('b',3);put('c',3);put('d',2);put('e',1);put('f',4);put('g',2);put('h',4);put('i',1);put('j',8);put('k',5);put('l',1);put('m',3);put('n',1);put('o',1);put('p',3);put('q',10);put('r',1);put('s',1);put('t',1);put('u',1);put('v',4);put('w',4);put('x',8);put('y',4);put('z',10);
			}};
		int score=0;
		int wordLength=word.length();
		for(int i=0;i<wordLength;i++) {
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
	
	public static HashMap<Integer,ArrayList<int[]>> findStartEndIndex(String word, int mat) {
		HashMap<Integer,ArrayList<int[]>> indexes=new HashMap<Integer, ArrayList<int[]>>();
		indexes.put(0,new ArrayList<int[]>());indexes.put(1,new ArrayList<int[]>());
		char startChar=word.charAt(0),endChar=word.charAt(word.length()-1);
		for(int i=mat;i<mat+3;i++) {
			for(int j=mat;j<mat+3;j++) {
				int[] index=new int[2];
				index[0]=i;index[1]=j;
				if(wordMatrix[i][j]==startChar) {
					indexes.get(0).add(index);
				}
				if(wordMatrix[i][j]==endChar) {
					indexes.get(1).add(index);
				}
				if(wordMatrix[i][j]=='-') {
					indexes.get(0).add(index);
					indexes.get(1).add(index);
				}
			}
		}
		return indexes;
	}
	
	public static void makeSeq(TreeMap<Integer,ArrayList<String>> listWithScoreKeys, HashMap<Character,TreeSet<String>> listWithCharKeys, HashMap<String,Integer> listWithScores) {
		int[] scoreKeys=listWithScoreKeys.keySet().stream().mapToInt(Integer::intValue).toArray();
		makeMatrix();
		printMatrix();
		for(int i=(scoreKeys.length-1);i>=0;i--) {
			System.out.println("current score:"+scoreKeys[i]);
			ArrayList<String> maxWords=listWithScoreKeys.get(scoreKeys[i]);
			for(String maxWord : maxWords) {// for every max score word. Starting point. Consider for first matrix only
				HashMap<Integer,ArrayList<int[]>> indexes=findStartEndIndex(maxWord,0);
				System.out.println("for word"+maxWord);
				if(indexes.get(0).size()==0 || indexes.get(1).size()==0)
					continue;
				//got start and end indexes. Go ahead
				
			}
		}
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
		HashMap<String,Integer> listWithScores=new HashMap<String,Integer>();
		System.out.println("Total number of words read:"+words.size());
		getListWithScores(listWithScores,words);
		System.out.println("Sample word with score:"+listWithScores.get("oxyphenbutazone"));
		System.out.println("Total number of valid words:"+listWithScores.size());
		TreeMap<Integer,ArrayList<String>> listWithScoreKeys=new TreeMap<Integer,ArrayList<String>>();
		getListWithScoreKeys(listWithScores,listWithScoreKeys);
		System.out.println("Sample scores with words:"+listWithScoreKeys.get(listWithScoreKeys.lastKey()).toString());
		System.out.println("All Score Keys:"+listWithScoreKeys.keySet());
		System.out.println("Number of words with score 19:"+listWithScoreKeys.get(19).size());
		System.out.println("Max Score:"+listWithScoreKeys.lastKey());
		HashMap<Character,TreeSet<String>> listWithCharKeys=new HashMap<Character, TreeSet<String>>();
		getListWithCharKeys(listWithScores, listWithCharKeys);
		System.out.println("Sample list with char keys:");
		System.out.println("Word with e:"+listWithCharKeys.get('e'));
//		for(String word : listWithCharKeys.get('z')) {
//			System.out.println("Word:"+word+" score:"+listWithScores.get(word));
//		}
		makeSeq(listWithScoreKeys, listWithCharKeys, listWithScores);
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