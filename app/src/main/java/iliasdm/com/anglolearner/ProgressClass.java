package iliasdm.com.anglolearner;

public class ProgressClass {

    private String completion;
    private String sentenceReached;
    private String noMistakes;

    ProgressClass () {}

    public String getCompletion() {
        return completion;
    }

    public String getSentenceReached() {
        return sentenceReached;
    }

    public String getNoMistakes() {
        return noMistakes;
    }

    public ProgressClass(String completion, String sentenceReached, String noMistakes) {

        this.completion = completion;
        this.sentenceReached = sentenceReached;
        this.noMistakes = noMistakes;
    }
}
