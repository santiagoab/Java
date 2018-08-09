/*
 * Santiago Alarcos Belzuces - CE306 Information Retrieval
 * */

package Assignment1;

import Assignment1.Stemmer;
import java.net.*;
import java.util.*;

import java.io.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Assignment1 {
    public static void main(String[] args) throws Exception {
    
    	//Ask the user for input website to retrieve information
    	BufferedReader consolein = new BufferedReader(new InputStreamReader(System.in));
    	System.out.println("Enter input website URL:");
		String input = consolein.readLine();

        System.out.println("Reading and parsing URL...");
        
        // Fetch the given website HTML and parse it to a DOM (using JSOUP parser)
        Document doc = Jsoup.connect(input).get(); 
              
        //Open a textfile to save plain text extracted.
        PrintWriter writer = new PrintWriter("webtext.txt", "UTF-8"); 
        
        //Search for metadata such as keywords and description into the website HTML and save to txt file
        if(doc.hasAttr("meta[name=keywords]")) {
        String keywords = doc.select("meta[name=keywords]").get(0).attr("content");
        writer.println(keywords);
        }
        
        if(doc.hasAttr("meta[name=description]")) {
        String description = doc.select("meta[name=description]").get(0).attr("content");
        writer.println(description);
        }
        
        //Retrieve h1 elements from HTML and save to txt file (using Jsoup)
        Elements h1 = doc.select("h1");
        for(Element e: h1 ){
            writer.println(e.text());
        }
        
        //Retrieve h2 elements from HTML and save to txt file (using Jsoup)
        Elements h2 = doc.select("h2");
        for(Element e: h2 ){
            writer.println(e.text());
        }
        
        //Retrieve h3 elements from HTML and save to txt file (using Jsoup)
        Elements h3 = doc.select("h3");
        for(Element e: h3 ){
            writer.println(e.text());
        }
        
        //Retrieve bold elements from HTML and save to txt file (using Jsoup)
        Elements bold = doc.select("b");
        for(Element e: bold ){
            writer.println(e.text());
        }
        
        //Retrieve paragraph elements from HTML and save to txt file (using Jsoup)
        Elements paragraphs = doc.select("p");
        for(Element e: paragraphs){
            writer.println(e.text());
        	
        }
        
        //Retrieve list elements from HTML and save to txt file (using Jsoup)
        Elements list = doc.select("li");
        for(Element e: list ){
            writer.println(e.text());
        }
        writer.close();
        
        System.out.println("HMTL parsed sucessfully and saved to webtext.txt");
        
        //Read webtext.txt and open new txt file to save pre-processing 
        BufferedReader inputFileReader = new BufferedReader(new FileReader("webtext.txt"));
        String inputFileLine;
        PrintWriter outputFile = new PrintWriter(new FileWriter("textclean.txt"));
        
        System.out.println("Pre-processing text...");
        
        //Loop over all non-empty lines of text in the file
        while((inputFileLine = inputFileReader.readLine()) != null) {
        	if(inputFileLine.length() == 0)
        		continue;
        	
        	//Eliminate unnecessary punctuation from text
        	inputFileLine = inputFileLine.replaceAll("[\\[()\\]{}+\\\\\\/-]", " ")
        								.replaceAll(" +", " ")
        								.replaceAll(",", "")
        								.replaceAll(":", "");
        	
        	//Tokenize word by word and save into new file
        	StringTokenizer st = new StringTokenizer(inputFileLine);
        	while (st.hasMoreTokens()) {
        	outputFile.println(st.nextToken());
        	}
        } 
        inputFileReader.close();
        outputFile.close();
        
        System.out.println("Text pre-processed sucessfully and saved to textclean.txt");
        
        //Initialize StanfordNLP tagger
        MaxentTagger tagger = new MaxentTagger("taggers/left3words-wsj-0-18.tagger");
        
		BufferedReader textcleanReader = new BufferedReader(new FileReader("textclean.txt"));
		PrintWriter outputTagged = new PrintWriter(new FileWriter("texttagged.txt"));
		
		//Read file to a single string
        String textcleanLine = textcleanReader.readLine();
        StringBuilder sb = new StringBuilder(); 
        while(textcleanLine != null){
        	sb.append(textcleanLine).append("\n"); 
        	textcleanLine = textcleanReader.readLine();
        	}
        textcleanReader.close();
        
        String fileAsString = sb.toString();
        
        //Tag all word with POS tagger and save to file
        String textTagged = tagger.tagString(fileAsString);
        outputTagged.println(textTagged);
        outputTagged.close();
        
        System.out.println("Text tagged sucessfully and saved to texttagged.txt");
        
        System.out.println("Extracting nouns and proper nouns...");
        
        //Scan tagged words for nouns, and save these into new textfile
        Scanner scanner = new Scanner(textTagged);
        PrintWriter outputNouns = new PrintWriter(new FileWriter("nouns.txt"));
        String nounsString = "";
        while(scanner.hasNext()) {
           String word = scanner.next();
           String[] tagged = word.split("/");
           if(tagged[1].matches("NN")||tagged[1]
        		       .matches("NNP")||tagged[1]
        		       .matches("NNS")||tagged[1]
        		       .matches("NNPS")) {
        	   outputNouns.println("Word:"+tagged[0]+ " Tag:"+tagged[1]);
               nounsString = nounsString +" "+ tagged[0].toString();
           }              
        }
        outputNouns.close();
        scanner.close();
        System.out.println("Nouns and proper nouns extracted and saved to nouns.txt");
        
        System.out.println("Calculating term frequency...");
        String[] words = nounsString.split(" ");
        PrintWriter outputntf = new PrintWriter(new FileWriter("nounstermfreq.txt"));
        //Calculate term frequency and save word and frequency to HashMap
        Map<String, Integer> frequencies = new LinkedHashMap<String, Integer>();
        for (String word : words) {
            if (!word.isEmpty()) {
                Integer frequency = frequencies.get(word);

                if (frequency == null) {
                    frequency = 0;
                }

                ++frequency;
                frequencies.put(word, frequency);
            }
        }
        
        //Sort Nouns by frequency in descending order
        Map<String, Integer> sortedFreq = sortByValue(frequencies);
        
        Iterator iterator = sortedFreq.keySet().iterator();
        while (iterator.hasNext()) {
           String key = iterator.next().toString();
           String value = frequencies.get(key).toString();
           outputntf.println(key + " " + value);
        }
        outputntf.close();
        System.out.println("Term frequency stored to nounstermfreq.txt");
        
        System.out.println("Stemming nouns...");
        
        //Loop again sorted nouns HashMap, perform word stemming, and save final stemmed nouns by frequency into textfile
        Iterator iterator2 = sortedFreq.keySet().iterator();
        PrintWriter outputFinal = new PrintWriter(new FileWriter("FinalData.txt"));
        Stemmer state = new Stemmer(); 
        while (iterator2.hasNext()) {
            String key = iterator2.next().toString();
            String value = frequencies.get(key).toString();
            
            //Stem words with CoreNPL Stemmer class
            String keyStem = state.stem(key);
            outputFinal.println(keyStem + " " + value);
         }
        outputFinal.close();
        
        System.out.println("Pipeline completed! Indexed terms stored to FinalData.txt");
   
    }

    
    //Function to sort LinkedHashMap by descending order by value 
    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        //Convert Map to List of frequencies
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        //Sort list with Collections.sort(), provide a custom Comparator
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o2,
                               Map.Entry<String, Integer> o1) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        //Loop the sorted list and put it into a new insertion order frequencies LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
        
    }



