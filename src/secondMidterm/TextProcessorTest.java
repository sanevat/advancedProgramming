package secondMidterm;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


class Line {
    String text;

    public Line(String text) {
        this.text = text;
    }

    public Line modifyLine() {
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (Character.isLetter(text.charAt(i)) || Character.isWhitespace(text.charAt(i))) {
                newString.append(text.charAt(i));
            }
        }
        newString = new StringBuilder(newString.toString().toLowerCase());
        return new Line(newString.toString());
    }
}

class TextProcessor {
    List<Line> lines;
    Set<String> words;
    Map<String, Integer> frequencyOfWord;
    Map<String, List<Integer>> frequencyOfLine;

    public TextProcessor() {
        this.lines = new ArrayList<>();
        this.frequencyOfWord = new TreeMap<>();
        this.words = new TreeSet<>();
        this.frequencyOfLine = new TreeMap<>();
    }

    public void readText(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        br.lines().forEach(line -> {
            lines.add(new Line(line));
            frequencyOfLine.putIfAbsent(line, new ArrayList<>());
        });
    }

    public void findUniqueWords() {
        lines.forEach(line -> {
            String[] parts = line.modifyLine().text.split("\\s+");
            Arrays.stream(parts).forEach(part -> {
                        words.add(part);
                        frequencyOfWord.putIfAbsent(part, 0);
                        frequencyOfWord.put(part, frequencyOfWord.get(part.toLowerCase()) + 1);
                    }
            );
        });
    }

    public int countOccurrences(String line, String word) {
        return (int) Arrays.stream(line.split("\\s+"))
                .filter(part -> part.equals(word))
                .count();
    }


    public void printTextsVectors(OutputStream out) {
        findUniqueWords();
        PrintWriter pw = new PrintWriter(out);

        lines.forEach(line -> {
            List<Integer> frequencies = words.stream()
                    .map(word -> countOccurrences(line.modifyLine().text, word))
                    .collect(Collectors.toList());

            frequencyOfLine.put(line.text, frequencies);
            pw.println(frequencies);
        });

        pw.flush();
    }

    public void printCorpus(OutputStream out, int n, boolean b) {
        PrintWriter pw = new PrintWriter(out);
        Comparator<Integer> c = Comparator.naturalOrder();

        if (!b)
            c = c.reversed();

        frequencyOfWord.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(c))
                .limit(n)
                .forEach(entry -> pw.println(entry.getKey() + " : " + entry.getValue()));

        pw.flush();
    }

    public void mostSimilarTexts(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        AtomicReference<Double> maxSimilarity = new AtomicReference<>((double) 0);
        AtomicReference<String> text = new AtomicReference<>("");

        lines.forEach(line1 -> lines.stream()
                .filter(line2 -> line1 != line2)
                .forEach(line2 -> {
                    double similarity = CosineSimilarityCalculator
                            .cosineSimilarity(frequencyOfLine.get(line1.text), frequencyOfLine.get(line2.text));
                    if ( similarity>maxSimilarity.get() ) {
                        maxSimilarity.set(similarity);
                        text.set(line1.text + "\n" + line2.text);
                    }
                }));

        pw.println(text);
        pw.println(String.format("%.10f", maxSimilarity.get()));
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