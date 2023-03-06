package utils;


import com.mindsmiths.ruleEngine.util.Log;

public class Utils {
    
    public static String trimText(String text) {
        if (!text.contains(".") && !text.contains("!") && !text.contains("?")) {
            return text;
        }

        int lastPunctuation = Math.max(
                Math.max(text.lastIndexOf("."), text.lastIndexOf("!")),
                text.lastIndexOf("?")
        );
        if (lastPunctuation != -1) {
            String lastWord = text.substring(0, lastPunctuation).replaceAll("[^\\w\\s]+", "");
            String[] words = lastWord.split("\\s+");
            if (words.length > 0 && words[words.length - 1].matches("\\d+")) {
                int lastPunctuationIndex = text.lastIndexOf(".", lastPunctuation - 1);
                if (lastPunctuationIndex != -1) {
                    return text.substring(0, lastPunctuationIndex + 1);
                }
            }
        }
        return text.substring(0, lastPunctuation + 1);
    }
}