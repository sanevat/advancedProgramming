import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class Round{
    List<Integer>attacker;
    List<Integer>defender;

    public Round(List<Integer> attacker, List<Integer> defender) {
        this.attacker = attacker;
        this.defender = defender;
    }
    public static Round createRound(String line){
        String[]parts=line.split(";");

        List<Integer>attacker= Arrays.stream(parts[0].split("\\s+"))
                .map(Integer::parseInt)
                .toList();
        List<Integer>defender= Arrays.stream(parts[1].split("\\s+"))
                .map(Integer::parseInt)
                .toList();

        return new Round(attacker,defender);
    }
    public int pointsFromRound(){
        attacker=attacker.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        defender=defender.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        for(int i=0;i<3;i++){
            if(attacker.get(i)<=defender.get(i))
                return 0;
        }
        return 1;
    }
}
class Risk{
    List<Round>rounds;
    public Risk() {
        this.rounds=new ArrayList<>();
    }
    public int processAttacksData(InputStream in) {
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        rounds=br.lines()
                .map(Round::createRound)
                .collect(Collectors.toList());
        return rounds.stream().mapToInt(Round::pointsFromRound).sum();
    }
}
public class RiskTester {
    public static void main(String[] args) {
        Risk risk = new Risk();
        System.out.println(risk.processAttacksData(System.in));

    }
}
