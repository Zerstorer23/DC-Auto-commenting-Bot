package Markov.Crawler;

public abstract class Crawler {

    private static String regexNumber ="\\d+";
    static boolean emergency = true;
    public abstract void scrollMain()  ;
    static boolean isValidIndex(String aa){
        return aa.matches(regexNumber);
    }
    // Collects sentences. need
    //          writeToFileRaw("network-"+gallID+"-Raw.txt");
    //         buildNetwork("network-"+gallID+"-Raw.txt");
    //         writeToFile("network-"+gallID+".txt");

    // connects to each page
}
