package voiceCare.model.entity.AudioTransWord;

public class Sentences {
    private int EndTime;
    private int SilenceDuration;
    private int BeginTime;
    private String Text;
    private int ChannelId;
    private int SpeechRate;
    private double EmotionValue;

    public int getEndTime() {
        return EndTime;
    }

    public void setEndTime(int endTime) {
        EndTime = endTime;
    }

    public int getSilenceDuration() {
        return SilenceDuration;
    }

    public void setSilenceDuration(int silenceDuration) {
        SilenceDuration = silenceDuration;
    }

    public int getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(int beginTime) {
        BeginTime = beginTime;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public int getChannelId() {
        return ChannelId;
    }

    public void setChannelId(int channelId) {
        ChannelId = channelId;
    }

    public int getSpeechRate() {
        return SpeechRate;
    }

    public void setSpeechRate(int speechRate) {
        SpeechRate = speechRate;
    }

    public double getEmotionValue() {
        return EmotionValue;
    }

    public void setEmotionValue(double emotionValue) {
        EmotionValue = emotionValue;
    }
}
