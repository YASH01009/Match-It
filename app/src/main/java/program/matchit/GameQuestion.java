package program.matchit;

public class GameQuestion extends GameRow {
    private String answer;

    public GameQuestion(String Meaning, String opta, String optb, String optc, String answer) {
        super(Meaning, opta, optb, optc);
        this.answer = answer;
    }

    public GameQuestion() {
    }

    public String getAnswer() {
        return answer;
    }
}
