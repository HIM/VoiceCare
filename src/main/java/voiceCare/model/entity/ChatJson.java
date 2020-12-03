package voiceCare.model.entity;

import voiceCare.model.entity.ChatJsonContent.Perception;
import voiceCare.model.entity.ChatJsonContent.UserInfo;

public class ChatJson {

    public int reqType;

    public Perception perception = new Perception();

    public UserInfo userInfo = new UserInfo();

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }
}
