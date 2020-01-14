package spamfilter.sop;

import java.io.*;
import java.util.*;

/**
 *
 * @author Mikkel
 */
public class SpamfilterSOP {
    /**
     * @param args the command line arguments
     */
    
    static HashMap<String, Word> words = new HashMap<String,Word>();    //HashMap til at holde alle ord
    
    public static void main(String[] args) {
        java.util.Scanner input =new java.util.Scanner (System.in);
        
        System.out.println("train/filter");
        switch(input.next()){
            case "train":
                train();
                System.out.println("Words have been learned");
                break;
            case "filter":
                load();
                filter();
                break;
        }
    }

    public static void train(){
        try{
        FileReader read = new FileReader("training.txt");
        BufferedReader in = new BufferedReader(read);
        String line = in.readLine();
        
        while(line != null){
            if (!line.equals("")){
                String type = line.split("\\|")[0];
                String text = line.split("\\|")[1];
            
                for(String word : text.split(" ")){  //loopes for alle ord i strengen text (alle index i array)
                    word = word.replaceAll("\\W", "");
                    word = word.toLowerCase();
                    Word w;
                    boolean error = false;
                
                    if(words.containsKey(word)){     //der tjækkes om ordet allerede findes i HashMappet
                        w = (Word) words.get(word);
                    }
                    else if(word.equals("")){       //sikre at tomme ord ikke bliver gemt
                        w = (Word) words.get(word);
                        error = true;
                    }
                    else{
                        w = new Word(word); //ny instans af class "Word" bliver lavet med det nye ord
                        words.put(word, w); //sætter variabel word som key og w som object i HashMappet
                    }
                
                    if (type.equals("ham") && !error){
                        w.countHam();
                    }
                    else if(type.equals("spam") && !error){
                        w.countSpam();
                    }
                    if(!error){
                        w.calculateSpamProb();
                    }
                }
            }
            line = in.readLine();
        }
        in.close();
        save();
        }catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    public static void save(){
        try{
            FileWriter file = new FileWriter("words.txt");
            PrintWriter out = new PrintWriter(file);
            
            List<String> keyList = new ArrayList<String>(words.keySet());   //alle keys i HashMappet bliver anbringet i en liste for nemt at kunne læse dem
            for(int i = 0; i < words.size(); i++){                          //ordet, dets spamCount og hamCount bliver gemt i words.txt
                out.println(  words.get(keyList.get(i)).getWord() + "|" + words.get(keyList.get(i)).getSpamCount() + "|" + words.get(keyList.get(i)).getHamCount()  );
            }
            out.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    public static void load(){
        try{
            FileReader read = new FileReader("words.txt");
            BufferedReader in = new BufferedReader(read);
            
            String line = in.readLine();
            while(line != null){
                Word w;
                w = new Word(line);
                String[] str = line.split("\\|");
                words.put(str[0], w);
                w.setWord(str[0]);
                w.setSpamCount(Double.parseDouble(str[1]));
                w.setHamCount(Double.parseDouble(str[2]));
                w.calculateSpamProb();
                
                line = in.readLine();
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    public static void filter(){
        try{
            FileReader read = new FileReader("test.txt");
            BufferedReader in = new BufferedReader(read);

            String line = in.readLine();
            double textSpamProb = 0.55;                  //procentdel af alle mails der er spam
            double textHamProb = 1 - textSpamProb;       //procentdel af alle mails der er ham

            while(line != null){
                for(String word : line.split(" ")){
                    word = word.replaceAll("\\W", "");
                    word = word.toLowerCase();
                    Word w;

                    if(!words.containsKey(word)){       //ukendte ord får en default sandsynlighed 
                        w = new Word(word);
                        w.fixProb();
                    }
                    else{
                    w = (Word) words.get(word);
                    }

                    textSpamProb =                      //sandsynligheden for at beskeden er spam, bliver rettet med bayes formel
                                            (textSpamProb * w.getSpamProb() / 
                            (textSpamProb * w.getSpamProb() + textHamProb * w.getHamProb()) );

                    textHamProb = 1 - textSpamProb;     //sandsynligheden for at beskeden ikke er spam bliver rettet
                }
                
                System.out.println("PROBABILITY OF SPAM: " + textSpamProb); //spam sandsynlighed bliver udskrevet
                if(textSpamProb > 0.7){
                    System.out.println("MARKED AS SPAM:\n" + line);
                }
                else{
                    System.out.println("MARKED AS HAM:\n" + line);
                }
                line = in.readLine();
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}