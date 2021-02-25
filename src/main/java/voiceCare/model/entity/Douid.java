package voiceCare.model.entity;

public class Douid {
    private int postId;
    private int userId;
//切换后的成员声音id, 当前登录用户id
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
