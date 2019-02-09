package mo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RemoteProcessesPlayerPanel extends JPanel {
    private JsonObject processesSnapshotData;
    private static final String CAPTURE_MILLISECONDS_KEY = "captureMilliseconds";
    private static final String PID_KEY = "pid";
    private static final String USER_NAME_KEY = "userName";
    private static final String START_INSTANT_KEY = "startInstant";
    private static final String TOTAL_CPU_DURATION_KEY = "totalCpuDuration";
    private static final String COMMAND_KEY = "command";
    private static final String SUPPORTS_NORMAL_TERMINATION_KEY = "supportsNormalTermination";
    private static final String PARENT_PID_KEY = "parentPid";
    private static final String HAS_CHILDREN_KEY = "hasChildren";
    private static final String PROCESSES_KEY = "processes";
    private JScrollPane scrollPane;
    private JTable table;
    private JLabel waitingLabel;
    /* Estos textos deben ser internacionalizados*/
    private static final String[] TABLE_HEADERS = {"PID", "Username",
            "Start instant", "Total CPU Duration", "command", "Parent PID"};

    public RemoteProcessesPlayerPanel(){
        this.table = null;
        this.scrollPane = null;
        this.waitingLabel = new JLabel("Waiting for data...");
        this.waitingLabel.setVisible(true);
    }

    private void createTable(JsonObject processesSnapshotData){
        this.waitingLabel.setVisible(false);
        Object[][] formattedProcessesData = this.parseData(processesSnapshotData);
        this.table = new JTable(formattedProcessesData, TABLE_HEADERS);
        this.scrollPane = new JScrollPane(table);
        this.table.setFillsViewportHeight(true);
        this.add(this.scrollPane);
        this.setVisible(true);
    }

    public void updateData(JsonObject processesSnapshotData){
        if(this.table == null && this.scrollPane == null){
            this.createTable(processesSnapshotData);
            return;
        }
        Object[][] formattedProcessesData = this.parseData(processesSnapshotData);
        this.table.setModel(new DefaultTableModel(formattedProcessesData, TABLE_HEADERS));
    };

    private Object[][] parseData(JsonObject processesSnapshotData){
        JsonArray processes = processesSnapshotData.get(PROCESSES_KEY).getAsJsonArray();
        int size = processes.size();
        Object[][] res = new Object[size][];
        for(int i=0 ; i < size; i++){
            JsonObject process = processes.get(i).getAsJsonObject();
            String[] processData = new String[]{
                    process.get(PID_KEY).getAsString(),
                    process.get(USER_NAME_KEY).getAsString(),
                    process.get(START_INSTANT_KEY).getAsString(),
                    process.get(TOTAL_CPU_DURATION_KEY).getAsString(),
                    process.get(COMMAND_KEY).getAsString(),
                    process.get(PARENT_PID_KEY).getAsString()
            };
            res[i] = processData;
        }
        return res;
    }

    public void removeTable(){
        if(this.scrollPane == null){
            return;
        }
        this.remove(this.scrollPane);
        this.scrollPane = null;
        this.table = null;
    }
}
