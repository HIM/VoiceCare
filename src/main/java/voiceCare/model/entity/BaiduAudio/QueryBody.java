package voiceCare.model.entity.BaiduAudio;

public class QueryBody {

    public String[] task_ids = new String[1];

    public String[] getTask_ids() {
        return task_ids;
    }

    public void setTask_ids(String task_ids) {
        this.task_ids[0] = task_ids;
    }
}
