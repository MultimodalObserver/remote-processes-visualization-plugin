package mo.visualization.process.plugin.model;

import java.util.List;

public class Snapshot {
    private List<Process> processes;
    private Long captureTimestamp;

    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public Long getCaptureTimestamp() {
        return captureTimestamp;
    }

    public void setCaptureTimestamp(Long captureTimestamp) {
        this.captureTimestamp = captureTimestamp;
    }
}
