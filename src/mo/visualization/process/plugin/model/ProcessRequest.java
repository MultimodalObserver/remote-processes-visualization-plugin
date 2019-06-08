package mo.visualization.process.plugin.model;

import java.io.Serializable;

public class ProcessRequest implements Serializable{

    private final static long SERIAL_VERSION_UID = 1L;

    private String action;
    private long selectedProcessPID;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getSelectedProcessPID() {
        return selectedProcessPID;
    }

    public void setSelectedProcessPID(long selectedProcessPID) {
        this.selectedProcessPID = selectedProcessPID;
    }
}
