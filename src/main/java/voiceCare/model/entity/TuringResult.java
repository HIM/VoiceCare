package voiceCare.model.entity;

import voiceCare.model.entity.TuringResultBag.Intent;
import voiceCare.model.entity.TuringResultBag.Results;

public class TuringResult {

    public Intent intent;

    public Results[] results;

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public Results[] getResults() {
        return results;
    }

    public void setResults(Results[] results) {
        this.results = results;
    }
}
