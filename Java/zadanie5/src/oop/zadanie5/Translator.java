package oop.zadanie5; 

import java.util.HashMap;
import java.util.Map;

public class Translator  {
    private Map<String, String> dictionary;

    public Translator(){
        dictionary = new HashMap<>();
    }

    public void set(String word,String translation){
        dictionary.put(word,translation);
    }

    public String translate(String word){
        return dictionary.get(word);
    }

    public boolean canTranslate(String word) {
        return dictionary.containsKey(word);
    }

    public int getSize() {
        return dictionary.size();
    }

}
