package spamfilter.sop;

/**
 *
 * @author Mikkel
 */
public class Word {
    
    private String word;
    private double spamCount;
    private double hamCount;
    private double spamProb;
    private double hamProb;
    
    public Word(String word){
        this.word = word;
        this.spamCount = 0;
        this.hamCount = 0;
        this.spamProb = 0;
        this.hamProb = 0;
    }
    
    public void setWord(String str){
        word = str;
    }
    
    public void setSpamCount(double i){
        spamCount = i;
    }
    
    public void setHamCount(double i){
        hamCount = i;
    }
    
    public void fixProb(){
        spamProb = 0.65;
        hamProb = 1 - spamProb;
    }
    
    public void countSpam(){
        spamCount++;
    }
    
    public void countHam(){
        hamCount++;
    }
    
    public void calculateSpamProb(){
        
        if(spamCount>0) spamProb = spamCount/(hamCount + spamCount);    //spamProb udregnes
        else spamProb = 0;        
        
        spamProb = Math.round(spamProb * 10000.0) / 10000.0;    //spamProb bliver rundet af til fire decimaler
        
        if (spamProb<0.01 || Double.isNaN(spamProb) ) spamProb = 0.01;
        
        if (spamProb>0.99) spamProb = 0.99;
        
        hamProb = 1 - spamProb;                                 //hamProb udregnes ud fra spamProb
    }
    
    public String getWord(){
        return word;
    }
    
    public double getSpamProb(){
        return spamProb;
    }
    
    public double getHamProb(){
        return hamProb;
    }
    
    public double getSpamCount(){
        return spamCount;
    }
    
    public double getHamCount(){
        return hamCount;
    }
}
