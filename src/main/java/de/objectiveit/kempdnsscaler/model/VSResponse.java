package de.objectiveit.kempdnsscaler.model;

public class VSResponse {

    private boolean changed;
    private String result;

    private VSResponse(boolean changed, String result) {
        this.changed = changed;
        this.result = result;
    }

    public static VSResponse response(boolean changed, String result) {
        return new VSResponse(changed, result);
    }

    public boolean isChanged() {
        return changed;
    }

    public String getResult() {
        return result;
    }

}
