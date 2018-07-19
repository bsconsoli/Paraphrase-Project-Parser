import java.io.*;
import java.nio.charset.StandardCharsets;

public class UTF8ToLATIN1 {
    public static void main(String[] args) {
        try{
            for (String arg : args) {
                BufferedReader original = new BufferedReader(new InputStreamReader(new FileInputStream(arg), StandardCharsets.UTF_8));
                String line;
                PrintWriter output;
                output = new PrintWriter(arg + ".out", "ISO-8859-1");
                while ((line = original.readLine()) != null) {
                    output.println(line);
                }
                output.close();
                original.close();
            }
        } catch(Exception e){
            System.err.println(e);
        }

    }
}

