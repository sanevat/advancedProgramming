package secondMidterm;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class TextLine {
    List<String> words;
    List<String> modifiedWords;

    public TextLine(List<String> words) {
        this.words = words;
        modifyWords();
    }

    public void modifyWords() {
        modifiedWords = words.stream().map(word -> {
            StringBuilder newWord = new StringBuilder();
            for (Character c : word.toCharArray()) {
                if (Character.isLetter(c)) {
                    newWord.append(c);
                }
            }
            return newWord.toString().toLowerCase();
        }).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        words.forEach(word -> sb.append(word).append(" "));
        return sb.toString();
    }
}

class TextProcessor {
    List<TextLine> lines;
    Set<String> uniqueWords;


    public TextProcessor() {
        this.lines = new ArrayList<>();
        this.uniqueWords = new TreeSet<>();
    }

    public void readText(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.lines().forEach(line -> {
            TextLine tx = createTextLine(line);
            lines.add(tx);
            uniqueWords.addAll(tx.modifiedWords);
        });
    }

    public TextLine createTextLine(String line) {
        return new TextLine(List.of(line.split("\\s+")));
    }

    public List<Integer> frequencyOfLine(TextLine line) {
        List<Integer> frequencyOfLine = new ArrayList<>();

        uniqueWords.forEach(uniqueWord -> {
            AtomicInteger counter = new AtomicInteger(0);
            line.modifiedWords.forEach(word -> {
                if (word.equals(uniqueWord)) counter.getAndIncrement();
            });
            frequencyOfLine.add(counter.get());
        });

        return frequencyOfLine;
    }

    public void printTextsVectors(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        lines.forEach(line -> pw.println(frequencyOfLine(line).toString()));
        pw.flush();
    }

    public void printCorpus(OutputStream out, int n, boolean ascending) {
        PrintWriter pw = new PrintWriter(out);
        Map<String, Integer> frequencyOfWord = new TreeMap<>();

        uniqueWords.forEach(word -> {
            AtomicInteger countFrequencies = new AtomicInteger();
            lines.forEach(line -> line.modifiedWords.forEach(part -> {
                if (part.equals(word))
                    countFrequencies.getAndIncrement();
            }));
            frequencyOfWord.put(word, countFrequencies.get());
        });


        frequencyOfWord.entrySet().stream()
                .sorted(ascending
                        ? Map.Entry.comparingByValue()
                        : Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(n)
                .forEach(entry -> pw.println(entry.getKey() + " : " + entry.getValue()));
        pw.flush();
    }

    public void mostSimilarTexts(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        TextLine mostSimilarFirst = null;
        TextLine mostSimilarSecond = null;
        double maxSimilarity = 0;

        for (TextLine line1 : lines) {
            for (TextLine line2 : lines) {
                if (!line1.equals(line2)) {
                    double similarity = CosineSimilarityCalculator.cosineSimilarity(frequencyOfLine(line1), frequencyOfLine(line2));
                    if (similarity > maxSimilarity) {
                        maxSimilarity = similarity;
                        mostSimilarFirst = new TextLine(line1.words);
                        mostSimilarSecond = new TextLine(line2.words);
                    }
                }
            }
        }

        pw.println(mostSimilarFirst);
        pw.println(mostSimilarSecond);
        pw.println(maxSimilarity);
        pw.flush();
    }
}

class CosineSimilarityCalculator {
    public static double cosineSimilarity(Collection<Integer> c1, Collection<Integer> c2) {
        int[] array1;
        int[] array2;
        array1 = c1.stream().mapToInt(i -> i).toArray();
        array2 = c2.stream().mapToInt(i -> i).toArray();
        double up = 0.0;
        double down1 = 0, down2 = 0;

        for (int i = 0; i < c1.size(); i++) {
            up += (array1[i] * array2[i]);
        }

        for (int i = 0; i < c1.size(); i++) {
            down1 += (array1[i] * array1[i]);
        }

        for (int i = 0; i < c1.size(); i++) {
            down2 += (array2[i] * array2[i]);
        }

        return up / (Math.sqrt(down1) * Math.sqrt(down2));
    }
}

public class TextProcessorTest {

    public static void main(String[] args) {
        TextProcessor textProcessor = new TextProcessor();

        textProcessor.readText(System.in);

        System.out.println("===PRINT VECTORS===");
        textProcessor.printTextsVectors(System.out);

        System.out.println("PRINT FIRST 20 WORDS SORTED ASCENDING BY FREQUENCY ");
        textProcessor.printCorpus(System.out, 20, true);

        System.out.println("PRINT FIRST 20 WORDS SORTED DESCENDING BY FREQUENCY");
        textProcessor.printCorpus(System.out, 20, false);

        System.out.println("===MOST SIMILAR TEXTS===");
        textProcessor.mostSimilarTexts(System.out);
    }
}