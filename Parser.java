import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Parser {

    public static ArrayList<String> parseCorpXML(String corpXML) {
        ArrayList<String> sentencas = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(corpXML), "ISO-8859-1"))) {
            String line;
            String numCadeia = "0";
            sentencas = new ArrayList<>();
            ArrayList<NounPhrase> CorpNPs = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                if (line.contains("Mencoes_Unicas")) {
                    break;
                }

                if (line.contains("<Texto conteudo=")){
                    int indexInit = line.indexOf(('"'));
                    int indexEnd = line.indexOf('.');
                    sentencas.add(line.substring(indexInit+1,indexEnd+1));
                    indexInit = indexEnd+1;
                    indexEnd = line.lastIndexOf('"');
                    sentencas.add(line.substring(indexInit+1,indexEnd));
                }

                if (line.contains("Cadeia_")) {
                    int indexCad = line.indexOf("Cadeia");
                    int indexCadNumInit = line.indexOf('_', indexCad);
                    int indexCadNumFin = line.indexOf('>', indexCad);
                    numCadeia = line.substring(indexCadNumInit + 1, indexCadNumFin);
                }

                if (line.contains("sn id=")) {
                    int indexSent = line.indexOf("sentenca=");
                    int indexSentNumInit = line.indexOf('"', indexSent);
                    int indexSentNumFin = line.indexOf('"', indexSentNumInit + 1);
                    String sent = line.substring(indexSentNumInit + 1, indexSentNumFin);
                    int indexSint = line.indexOf("sintagma=");
                    int indexSintNumInit = line.indexOf('"', indexSint);
                    int indexSintNumFin = line.indexOf('"', indexSintNumInit + 1);
                    String sint = line.substring(indexSintNumInit + 1, indexSintNumFin);
                    int indexNuc = line.indexOf("nucleo=");
                    int indexNucNumInit = line.indexOf('"', indexNuc);
                    int indexNucNumFin = line.indexOf('"', indexNucNumInit + 1);
                    String nuc = line.substring(indexNucNumInit + 1, indexNucNumFin);
                    int indexCat = line.indexOf("Categoria=");
                    int indexCatNumInit = line.indexOf('"', indexCat);
                    int indexCatSecondCat = line.indexOf('/', indexCat);
                    int indexCatNumFin = line.indexOf('"', indexCatNumInit + 1);
                    String cat;
                    if (indexCatSecondCat != -1) {
                        cat = line.substring(indexCatNumInit + 1, indexCatSecondCat);
                    } else cat = line.substring(indexCatNumInit + 1, indexCatNumFin);
                    CorpNPs.add(new NounPhrase(numCadeia, sent, sint, cat, nuc));
                }
            }

            for(int i = 0; sentencas.size() > i;i++){
                for(NounPhrase np:CorpNPs){
                    if (Integer.parseInt(np.getSentenceNumber()) == i+1){
                        StringBuilder annotatedSentence = new StringBuilder(sentencas.get(i));
                        boolean isNucleus = false;
                        int indInit = annotatedSentence.indexOf(np.getNounPhrase());
                        if (indInit == -1){
                            indInit = annotatedSentence.indexOf(np.getNucleus());
                            if(np.getNounPhrase().length() !=np.getNucleus().length()){
                                continue;
                            }
                            isNucleus = true;
                        } if (indInit == -1){
                            continue;
                        }
                        int indEnd = indInit + np.getNounPhrase().length();
                        if(isNucleus){
                            indEnd = indEnd;
                            isNucleus = false;
                        }
                        annotatedSentence.insert(indInit,'(');
                        annotatedSentence.insert(indEnd+1,')');
                        annotatedSentence.insert(indEnd+1,"["+np.getChainNumber()+"]");
                        sentencas.set(i, annotatedSentence.toString());
                        //System.out.println(annotatedSentence.toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return sentencas;
    }

    //Structure to help parse ASSIN Sentence Corpus
    private static class SentencePair{
        String head;
        String tail;
        SentencePair(String h, String t){
            head = h;
            tail = t;
        }
    }

    static void parseAssinXML(String AssinXML) {
        LinkedList<SentencePair> paraphrasePairs = new LinkedList<>();
        PrintWriter ppWriter; //Paraphrase Pair .txt file writer
        PrintWriter fnWriter; //File Name Writer (for use in console commands)
        int index = 1;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(AssinXML), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Paraphrase")){
                    String tail = br.readLine();
                    String t = tail.substring(11,tail.length()-4);
                    String head = br.readLine();
                    String h = head.substring(11,head.length()-4);
                    paraphrasePairs.add(new SentencePair(h,t));
                }
            }
            SentencePair sp;
            fnWriter = new PrintWriter("ppFileNames.txt");
            for (SentencePair paraphrasePair : paraphrasePairs) {
                String fileName = "pair" + index + ".txt";
                fnWriter.println(fileName + ".xml");
                ppWriter = new PrintWriter(fileName, "ISO-8859-1");
                index++;
                sp = paraphrasePair;
                ppWriter.println(sp.tail);
                ppWriter.println(sp.head);
                ppWriter.close();
            }
            fnWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}