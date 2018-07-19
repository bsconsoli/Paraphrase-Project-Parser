import java.io.PrintWriter;
import java.util.ArrayList;

public class Parse {
    public static void main(String[] args) {
        ArrayList<String> sentences = new ArrayList<>();
        for (String arg: args) {
            //System.out.println(arg);
            sentences.addAll(Parser.parseCorpXML(arg));
        }
        try {
            PrintWriter pw = new PrintWriter("sentences.txt");
            for(int i = 0; sentences.size() > i; i=i+2){
                pw.println(sentences.get(i));
                pw.println(sentences.get(i+1));
                //pw.println();
            }
            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
