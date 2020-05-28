package program.matchit;

public class GameRow {
    protected String Meaning;
    protected String opta;
    protected String optb;
    protected String optc;

    public GameRow(String Meaning, String opta, String optb, String optc) {
        this.Meaning = Meaning;
        this.opta = opta;
        this.optb = optb;
        this.optc = optc;
    }

    public GameRow() {
    }

    public String getMeaning() {
        return Meaning;
    }

    public void setMeaning(String Meaning) {
        this.Meaning = Meaning;
    }

    public String getOpta() {
        return opta;
    }

    public void setOpta(String opta) {
        this.opta = opta;
    }

    public String getOptb() {
        return optb;
    }

    public void setOptb(String optb) {
        this.optb = optb;
    }

    public String getOptc() {
        return optc;
    }

    public void setOptc(String optc) {
        this.optc = optc;
    }
}
