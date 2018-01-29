package subson;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @Author subson.
 * @E-mail subson@qq.com
 */
public class ProbabilityOfSentence {

    private String SOS = "st";  // Start Of String
    private String EOS = "ed";  // End Of String
    private HashSet<File> corpus;
    private double lamda;
    private HashMap<String, Double> bigram;
    private HashMap<String, Double> charFre;

    private void addKey(String key) {
        if(bigram.containsKey(key)) {
            bigram.put(key, 1d + bigram.get(key));
        } else {
            bigram.put(key, 1d);
        }
    }

    private void addWord(String word) {
        if(charFre.containsKey(word)) {
            charFre.put(word, 1d + charFre.get(word));
        } else {
            charFre.put(word, 1d);
        }
//        allWordNum++;
    }

    public ProbabilityOfSentence() {
        corpus = new HashSet<File>();
        lamda = 1d;
        bigram = new HashMap<String, Double>();
        charFre = new HashMap<String, Double>();
    }

    public ProbabilityOfSentence addCorpus(String path) {
        File file = new File(path);
        if(file.isDirectory()) {
            for (File f : file.listFiles()) {
                addCorpus(f.getAbsolutePath());
            }
        } else {
            this.corpus.add(file);
        }
        return this;
    }

    public ProbabilityOfSentence setAddOne(double lamda) {
        this.lamda = lamda;
        return this;
    }

    public ProbabilityOfSentence goIntoEffect() {
        if(this.corpus.size() == 0) {
            loadModel();
        } else {
            trainBigram();
            saveModel();
        }
        return this;
    }

    private void trainBigram() {
        for (File corpus : this.corpus) {
            System.out.print("training model from '" +corpus.getName() + "' file...");
            long st = System.currentTimeMillis();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(corpus));
                String line;
                while((line=reader.readLine()) != null) {
                    if(line.equals("")) continue;
                    addKey(SOS.concat(String.valueOf(line.charAt(0))));
                    addWord(SOS);
                    int i = 1;
                    char ch;
                    for (; i < line.length(); i++) {
                        addKey(line.substring(i-1, i+1));
                        ch = line.charAt(i-1);
                        addWord(String.valueOf(ch));
                    }
                    ch = line.charAt(i-1);
                    addKey(String.valueOf(ch).concat(EOS));
                    addWord(String.valueOf(line.charAt(i-1)));
                    addWord(EOS);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                System.err.println("Warning: Corpus '" + corpus + "' can't found.");
            } catch (IOException e) {
                System.err.println("Warning: Corpus '" + corpus + "' can't read.");
            }
            long ed = System.currentTimeMillis();
            System.out.println("it take " + String.format("%.2f", (ed - st) / 1000.0) + "s");
        }
    }

    private void saveModel() {
        System.out.print("saving model to local file...");
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(new File("model.txt")));
            writer.write("addOne<->1<->" + lamda);
            writer.newLine();
            writer.flush();
            for (Map.Entry<String, Double> entry : bigram.entrySet()) {
                writer.write(entry.getKey() + "<->2<->" + entry.getValue());
                writer.newLine();
                writer.flush();
            }
            for (Map.Entry<String, Double> entry : charFre.entrySet()) {
                writer.write(entry.getKey() + "<->3<->" + entry.getValue());
                writer.newLine();
                writer.flush();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("OK");
    }

    private void loadModel() {
        System.out.print("loading model from local file...");
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(new File("model.txt")));
            String line;
            while((line=reader.readLine()) != null) {
                String[] arr = line.split("<->");
                switch(arr[1]) {
                    case "1" :
                        if(lamda == 1d) lamda = Double.valueOf(arr[2]);break;
                    case "2" : bigram.put(arr[0], Double.valueOf(arr[2]));break;
                    case "3" : charFre.put(arr[0], Double.valueOf(arr[2]));break;
                    default:
                        System.err.println("Warning: model has redundancy message.");
                        System.out.println(line);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Warning: Model 'model.txt' can't found.");
        } catch (IOException e) {
            System.err.println("Warning: Model 'model.txt' can't read.");
        }
        System.out.println("OK");
    }

    public double probability(String sentence) {
        if(sentence.equals("")) return 0d;
        double prob;
        String key = SOS.concat(String.valueOf(sentence.charAt(0)));
        if(bigram.containsKey(key)) {
            prob = Math.log( (bigram.get(key) + lamda) /
                    (charFre.get(SOS) + lamda * charFre.size()));
        } else {
            prob = Math.log( lamda / (lamda * charFre.size()));
        }
        int i = 1;
        String ch;
        for (; i < sentence.length(); i++) {
            key = sentence.substring(i-1, i+1);
            ch = String.valueOf(sentence.charAt(i-1));
            if(bigram.containsKey(key) && charFre.containsKey(ch)) {
                prob += Math.log( (bigram.get(key) + lamda) /
                        (charFre.get(ch) + lamda * charFre.size()));
            } else {
                prob += Math.log( lamda / (lamda * charFre.size()));
            }
        }
        key = sentence.substring(i-1).concat(EOS);
        ch = String.valueOf(sentence.charAt(i-1));
        if(bigram.containsKey(key) && charFre.containsKey(ch)) {
            prob += Math.log( (bigram.get(key) + lamda) /
                    (charFre.get(ch) + lamda * charFre.size()));
        } else {
            prob += Math.log( lamda / (lamda * charFre.size()));
        }
        return Math.pow(Math.E, prob);
    }

    public double probability(String sentence, int len) {
        if(sentence.length() <= len) {
            return probability(sentence);
        } else {
            double prob = 0d;
            int i = 0;
            for (; i + len <= sentence.length(); i++) {
                prob += probability(sentence.substring(i, i + len));
            }
            return prob / i;
        }
    }

}
