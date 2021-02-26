package voiceCare.model.entity;

public class Clock {
    private int userId;
    private String time;
    private int state;
    private String comment;
    private String createTime;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Clock{" +
                "userId=" + userId +
                ", time='" + time + '\'' +
                ", state=" + state +
                ", comment='" + comment + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
