package labs.labs8;

import java.util.ArrayList;
import java.util.Scanner;

enum typeOfQuestion{
    TRUEFALSE,
    FREEFORM
}

class TriviaQuestion {

    public typeOfQuestion type;

    public String question;		// Actual question
    public String answer;		// Answer to question

    public int points;

    public TriviaQuestion( String question, String answer, int points, typeOfQuestion type) {
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.points = points;
    }

    public typeOfQuestion getType() {
        return type;
    }


}

class Game {

    private final ArrayList<TriviaQuestion> questions;

    public Game() {
        questions = new ArrayList<TriviaQuestion>();
    }

    public void addQuestion(String q, String a, int points, typeOfQuestion type) {
        TriviaQuestion question = new TriviaQuestion(q, a, points,type);
        questions.add(question);
    }

    public void showQuestion(int index) {
        TriviaQuestion q = questions.get(index);

        System.out.println("Question " + (index + 1) + ".  " + q.points + " points.");
        System.out.println(q.question);
        if (q.type == typeOfQuestion.TRUEFALSE) {
            System.out.println("Enter 'T' for true or 'F' for false.");
        }
    }

    public int numQuestions() {
        return questions.size();
    }

    public TriviaQuestion getQuestion(int index) {
        return questions.get(index);
    }
}

public class TriviaGame {

    public static void main(String[] args) {

        Game game = new Game();
        int totalScore=0;
        game.addQuestion("The possession of more than two sets of chromosomes is termed?",
                "polyploidy", 3, typeOfQuestion.FREEFORM);
        game.addQuestion("Erling Kagge skiied into the north pole alone on January 7, 1993.",
                "F", 1, typeOfQuestion.TRUEFALSE);
        game.addQuestion("1997 British band that produced 'Tub Thumper'",
                "Chumbawumba", 2, typeOfQuestion.FREEFORM);
        game.addQuestion("I am the geometric figure most like a lost parrot",
                "polygon", 2, typeOfQuestion.FREEFORM);
        game.addQuestion("Generics were introducted to Java starting at version 5.0.",
                "T", 1, typeOfQuestion.TRUEFALSE);


        Scanner keyboard = new Scanner(System.in);

        for(int i=0;i<game.numQuestions();i++) {

            game.showQuestion(i);
            String answer = keyboard.nextLine();

            TriviaQuestion q = game.getQuestion(i);

            if (q.type == typeOfQuestion.TRUEFALSE) {
                if (answer.charAt(0) == q.answer.charAt(0)) {
                    System.out.println("That is correct!  You get " + q.points + " points.");
                    totalScore += q.points;
                } else {
                    System.out.println("Wrong, the correct answer is " + q.answer);
                }
            } else if (q.type == typeOfQuestion.FREEFORM) {
                if (answer.equalsIgnoreCase(q.answer)) {
                    System.out.println("That is correct!  You get " + q.points + " points.");
                    totalScore += q.points;
                } else {
                    System.out.println("Wrong, the correct answer is " + q.answer);
                }
            }
            System.out.println("Your score is " + totalScore);
        }
        System.out.println("Game over!  Thanks for playing!");
    }
}
