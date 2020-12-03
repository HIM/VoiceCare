package voiceCare.model.entity.TuringResultBag;

public class Intent {

    public String ActionName;

    public int Code;

    public String IntentName;

    public String getActionName() {
        return ActionName;
    }

    public void setActionName(String actionName) {
        ActionName = actionName;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getIntentName() {
        return IntentName;
    }

    public void setIntentName(String intentName) {
        IntentName = intentName;
    }
}
