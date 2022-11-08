package iliasdm.com.anglolearner;

public class SentenceClass {
    private String originalsentence;
    private String targetsentence;
    private String task;

    public SentenceClass () {}

    public String getOriginalSentence() {
        return originalsentence;
    }

    public String getTargetSentence() {
        return targetsentence;
    }

    public String getTask() {
        return task;
    }

    public SentenceClass(String originalSentence, String targetSentence, String task) {

        this.originalsentence = originalSentence;
        this.targetsentence = targetSentence;
        this.task = task;
    }
}
