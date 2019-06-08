package mo.visualization.process.plugin.view;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mo.RemoteProcessesPlayer;
import mo.communication.ClientConnection;
import mo.communication.PetitionResponse;
import mo.core.I18n;
import mo.visualization.process.plugin.model.ProcessRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

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
    private static final int PID_COLUMN_TABLE_INDEX = 4;
    private static final int COMMAND_COLUMN_NAME_INDEX = 0;
    public static final int NOT_SELECTING_PROCESS = 0;
    public static final int SELECTING_PROCESS = 1;
    private JScrollPane scrollPane;
    private JTable table;
    private JLabel waitingLabel;
    /* Estos textos deben ser internacionalizados*/
    private final String[] tableHeaders;
    private I18n i18n;
    private DefaultTableModel tableModel;
    private JPopupMenu popupMenu;
    private JMenuItem destroyProcessItem;
    private JMenuItem restartProcessItem;
    private RemoteProcessesPlayer player;
    private int status;

    public RemoteProcessesPlayerPanel(){
        this.i18n = new I18n(RemoteProcessesPlayerPanel.class);
        this.tableHeaders = new String[6];
        this.initTableHeaders();
        this.initPopMenu();
        this.initTable();
        this.setVisible(true);
        this.status = NOT_SELECTING_PROCESS;
    }

    private void initPopMenu(){
        this.popupMenu = new JPopupMenu();
        String destroyProcessText = this.i18n.s("destroyProcess");
        this.destroyProcessItem = new JMenuItem(destroyProcessText);
        this.destroyProcessItem.addActionListener(e -> {
            int selectedRow = this.table.getSelectedRow();
            long selectedPID = Long.parseLong((String) this.table.getValueAt(selectedRow, PID_COLUMN_TABLE_INDEX));
            this.sendMessage("destroy", selectedPID);
            this.status = NOT_SELECTING_PROCESS;
        });
        String restartProcessText = this.i18n.s("restartProcess");
        this.restartProcessItem = new JMenuItem(restartProcessText);
        this.restartProcessItem.addActionListener(e -> {
            int selectedRow = this.table.getSelectedRow();
            long selectedPID = Long.parseLong((String) this.table.getValueAt(selectedRow, PID_COLUMN_TABLE_INDEX));
            this.sendMessage("restart", selectedPID);
            this.status = NOT_SELECTING_PROCESS;
        });
        this.popupMenu.add(this.destroyProcessItem);
        this.popupMenu.add(this.restartProcessItem);
    }

    private void initTableHeaders(){
        this.tableHeaders[0] = this.i18n.s("commandColumnName");
        this.tableHeaders[1] = this.i18n.s("usernameColumnName");
        this.tableHeaders[2] = this.i18n.s("startInstantColumnName");
        this.tableHeaders[3] = this.i18n.s("totalCPUDurationColumnName");
        this.tableHeaders[4] = this.i18n.s("pidColumnName");
        this.tableHeaders[5] = this.i18n.s("parentPidColumnName");
    }

    private void initTable(){
        /*this.tableModel = new DefaultTableModel(null, this.tableHeaders){
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex != 0;
            }
        };
        this.table = new JTable(this.tableModel){
             @Override
            public void changeSelection(int rowIndex, int colIndex, boolean toggle, boolean extend){
                 if(convertColumnIndexToModel(colIndex) != 0){
                     return;
                 }
                 super.changeSelection(rowIndex, colIndex, toggle, extend);
             }
        };*/
        this.tableModel = new DefaultTableModel();
        this.table = new JTable(this.tableModel);
        this.table.setFillsViewportHeight(true);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.table.setCellSelectionEnabled(true);
        this.table.setShowHorizontalLines(false);
        this.table.setComponentPopupMenu(this.popupMenu);
        this.table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int currentRow = table.rowAtPoint(point);
                if(currentRow == -1){
                    return;
                }
                RemoteProcessesPlayerPanel.this.table.setRowSelectionInterval(currentRow, currentRow);
                //System.out.println("SE APRETO LA FILA " + currentRow);
                RemoteProcessesPlayerPanel.this.status = SELECTING_PROCESS;
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        /*this.table.getColumnModel().setSelectionModel(new DefaultListSelectionModel(){
            @Override
            public boolean isSelectedIndex(int index){
                return RemoteProcessesPlayerPanel.this.table.convertColumnIndexToModel(index) == 0;
            }
        });*/
        this.scrollPane = new JScrollPane(table);
        this.add(this.scrollPane);
    }

    public void updateData(JsonObject processesSnapshotData){
        Object[][] formattedProcessesData = this.parseData(processesSnapshotData);
        this.tableModel.setDataVector(formattedProcessesData, this.tableHeaders);
        this.tableModel.fireTableRowsUpdated(1, formattedProcessesData.length);
    };

    private Object[][] parseData(JsonObject processesSnapshotData){
        JsonArray processes = processesSnapshotData.get(PROCESSES_KEY).getAsJsonArray();
        int size = processes.size();
        Object[][] res = new Object[size][];
        for(int i=0 ; i < size; i++){
            JsonObject process = processes.get(i).getAsJsonObject();
            String[] processData = new String[]{
                    process.get(COMMAND_KEY).getAsString(),
                    process.get(USER_NAME_KEY).getAsString(),
                    process.get(START_INSTANT_KEY).getAsString(),
                    process.get(TOTAL_CPU_DURATION_KEY).getAsString(),
                    process.get(PID_KEY).getAsString(),
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

    public int getStatus(){
        return this.status;
    }

    private void sendMessage(String action, long selectedProcessPID){
        HashMap<String, Object> data = new HashMap<>();
        Gson gson = new Gson();
        ProcessRequest processRequest = new ProcessRequest();
        processRequest.setAction(action);
        processRequest.setSelectedProcessPID(selectedProcessPID);
        data.put("data", gson.toJson(processRequest));
        PetitionResponse petitionResponse = new PetitionResponse("procesos", data);
        try {
            ClientConnection.getInstance().getServer().send(petitionResponse);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
