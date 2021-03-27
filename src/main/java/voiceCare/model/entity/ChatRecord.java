package voiceCare.model.entity;

public class ChatRecord {
    private int userId;
    private int forId;
    private String judge;
    private String word;
    private String time;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getForId() {
        return forId;
    }

    public void setForId(int forId) {
        this.forId = forId;
    }

    public String getJudge() {
        return judge;
    }

    public void setJudge(String judge) {
        this.judge = judge;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
