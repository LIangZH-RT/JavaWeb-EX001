package cloud.liang.Question;

public class Questions {
    private int ID;
    private String context;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private char answer;

    public Questions(){}

    public Questions(int ID, String Context, String answerA, String answerB, String answerC, String answerD, char Answer ){
        this.ID = ID;
        this.context = Context;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.answer = Answer;
    }


    public String getAnswerD() {
        return answerD;
    }

    public char getAnswer() {
        return answer;
    }

    public String getAnswerText() {
        return answer == '\0' ? "" : String.valueOf(answer);
    }

    public String getAnswerC() {
        return answerC;
    }

    public String getAnswerB() {
        return answerB;
    }

    public String getAnswerA() {
        return answerA;
    }

    public String getContext() {
        return context;
    }

    public int getID() {
        return ID;
    }

    public int getId() {
        return ID;
    }

}
