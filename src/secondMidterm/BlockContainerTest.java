package secondMidterm;

import java.util.*;
import java.util.stream.Collectors;

class Block<T extends Comparable<T>>{
    Set<T>elements;

    public Block() {
        this.elements=new TreeSet<>();
    }
    public void add(T elem){
        elements.add(elem);
    }
    public void remove(T elem){
        elements.remove(elem);
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
class BlockContainer<T extends Comparable<T>>{
    public static int countBlocks;
    public int maxSizeOfBlock;
    Map<Integer,Block<T>>blocks;

    public BlockContainer(int maxSizeOfBlock) {
        this.maxSizeOfBlock = maxSizeOfBlock;
        this.blocks=new HashMap<>();
        countBlocks=1;
        this.blocks.put(countBlocks, new Block<>());
    }
    public void add(T a){
        if(blocks.containsKey(countBlocks) && blocks.get(countBlocks).elements.size()<maxSizeOfBlock)
            blocks.get(countBlocks).add(a);
        else{
            countBlocks++;
            blocks.put((countBlocks),new Block<>());
            blocks.get(countBlocks).add(a);
        }
    }
    public void remove(T a){
        blocks.get(countBlocks).remove(a);
        if(blocks.get(countBlocks).elements.isEmpty()){
            blocks.remove(countBlocks,blocks.get(countBlocks));
            countBlocks--;
        }
    }
    public void sort(){
        List<T>allElements=blocks.values()
                .stream()
                .map(block->block.elements)
                .flatMap(Set::stream)
                .sorted(Comparator.naturalOrder())
                .toList();
        blocks.clear();
        countBlocks=0;
        for(T elem:allElements)
            add(elem);
    }

    @Override
    public String toString() {
        return blocks.values().stream()
                .map(Block::toString)
                .collect(Collectors.joining(","));
    }
}
public class BlockContainerTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int size = scanner.nextInt();
        BlockContainer<Integer> integerBC = new BlockContainer<>(size);
        scanner.nextLine();
        Integer lastInteger = null;
        for(int i = 0; i < n; ++i) {
            int element = scanner.nextInt();
            lastInteger = element;
            integerBC.add(element);
        }
        System.out.println("+++++ Integer Block Container +++++");
        System.out.println(integerBC);
        System.out.println("+++++ Removing element +++++");
        integerBC.remove(lastInteger);
        System.out.println("+++++ Sorting container +++++");
        integerBC.sort();
        System.out.println(integerBC);
        BlockContainer<String> stringBC = new BlockContainer<>(size);
        String lastString = null;
        for(int i = 0; i < n; ++i) {
            String element = scanner.next();
            lastString = element;
            stringBC.add(element);
        }
        System.out.println("+++++ String Block Container +++++");
        System.out.println(stringBC);
        System.out.println("+++++ Removing element +++++");
        stringBC.remove(lastString);
        System.out.println("+++++ Sorting container +++++");
        stringBC.sort();
        System.out.println(stringBC);
    }
}