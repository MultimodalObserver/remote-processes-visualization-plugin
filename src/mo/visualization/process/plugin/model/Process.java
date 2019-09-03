package mo.visualization.process.plugin.model;

public class Process {

    private long pid;
    private String userName;
    private String startInstant;
    private long totalCpuDuration;
    private String command;
    private long parentPid;
    private int hasChildren;
    private int supportsNormalTermination;

    public Process() {
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStartInstant() {
        return startInstant;
    }

    public void setStartInstant(String startInstant) {
        this.startInstant = startInstant;
    }

    public long getTotalCpuDuration() {
        return totalCpuDuration;
    }

    public void setTotalCpuDuration(long totalCpuDuration) {
        this.totalCpuDuration = totalCpuDuration;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public long getParentPid() {
        return parentPid;
    }

    public void setParentPid(long parentPid) {
        this.parentPid = parentPid;
    }

    public int getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(int hasChildren) {
        this.hasChildren = hasChildren;
    }

    public int getSupportsNormalTermination() {
        return supportsNormalTermination;
    }

    public void setSupportsNormalTermination(int supportsNormalTermination) {
        this.supportsNormalTermination = supportsNormalTermination;
    }
}
