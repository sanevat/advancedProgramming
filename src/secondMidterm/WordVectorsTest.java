package secondMidterm;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class WordVectors {
    private final Map<String,List<Integer>> stringListMap;
    private final Map<String,List<Integer>> readWords;
    public WordVectors(String[] words, List<List<Integer>> vectors){
        readWords = new LinkedHashMap<>();
        stringListMap = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            stringListMap.put(words[i],new ArrayList<>());
            stringListMap.get(words[i]).addAll(vectors.get(i));
        }
    }
    public void readWords(List<String> words){
        words.forEach(word->{
            readWords.put(word,new ArrayList<>());
            if (stringListMap.containsKey(word)){
                readWords.get(word).addAll(stringListMap.get(word));
            }else {
                readWords.get(word).addAll(IntStream.range(0,5).mapToObj(i->5).toList());
            }
        });
    }

    public List<Integer> slidingWindow(int n) {
        List<Integer> vectorRepresentation = new ArrayList<>();

        for (int i = 0; i <= readWords.keySet().size() - n; i++) {
            int maxTotal = 0;

            for (int j = 0; j < 5; j++) {
                AtomicInteger total = new AtomicInteger();
                int finalJ = j;
                readWords.entrySet().stream()
                        .skip(i)
                        .limit(n)
                        .forEach(entry -> total.addAndGet(entry.getValue().get(finalJ)));

                maxTotal = Math.max(maxTotal, total.get());
            }

            vectorRepresentation.add(maxTotal);
        }

        return vectorRepresentation;
    }
}

public class WordVectorsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] words = new String[n];
        List<List<Integer>> vectors = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            words[i] = parts[0];
            List<Integer> vector = Arrays.stream(parts[1].split(":"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            vectors.add(vector);
        }
        n = scanner.nextInt();
        scanner.nextLine();
        List<String> wordsList = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            wordsList.add(scanner.nextLine());
        }
        WordVectors wordVectors = new WordVectors(words, vectors);
        wordVectors.readWords(wordsList);
        n = scanner.nextInt();
        List<Integer> result = wordVectors.slidingWindow(n);
        System.out.println(result.stream()
                .map(java.lang.Object::toString)
                .collect(Collectors.joining(",")));
        scanner.close();
    }
}



