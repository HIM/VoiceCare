package voiceCare.model.entity.BaiduAudio;

public class BaiduRequest {

    public String speech_url;

    public String format;

    public int pid;

    public int rate;

    public String getSpeech_url() {
        return speech_url;
    }

    public void setSpeech_url(String speech_url) {
        this.speech_url = speech_url;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
