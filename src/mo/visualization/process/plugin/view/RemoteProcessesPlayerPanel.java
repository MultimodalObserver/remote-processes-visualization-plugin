package mo.visualization.process.plugin.view;

import mo.core.I18n;
import mo.visualization.process.plugin.model.Process;
import mo.visualization.process.plugin.model.Snapshot;
import mo.visualization.process.util.MessageSender;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class RemoteProcessesPlayerPanel extends JPanel {
    private static final int PID_COLUMN_TABLE_INDEX = 4;
    private static final int COMMAND_COLUMN_NAME_INDEX = 0;
    public static final int NOT_SELECTING_PROCESS = 0;
    public static final int SELECTING_PROCESS = 1;
    private JScrollPane scrollPane;
    private JTable table;
    /* Estos textos deben ser internacionalizados*/
    private final String[] tableHeaders;
    private I18n i18n;
    private DefaultTableModel tableModel;
    private JPopupMenu popupMenu;
    private ActionResultDialog actionResultDialog;
    private JLabel newProcessLabel;
    private JLabel newProcessErrorLabel;
    private JTextField newProcessTextField;
    private JButton newProcessButton;
    private JPanel newProcessPanel;
    private int status;

    public RemoteProcessesPlayerPanel(){
        this.i18n = new I18n(RemoteProcessesPlayerPanel.class);
        this.tableHeaders = new String[6];
        this.actionResultDialog = new ActionResultDialog();
        this.actionResultDialog.setVisible(false);
        this.setLayout(new GridBagLayout());
        this.initTableHeaders();
        this.initPopMenu();
        this.initSearchBar();
        this.initTable();
        this.addListeners();
        this.setVisible(true);
        this.status = NOT_SELECTING_PROCESS;
    }

    private void initSearchBar() {
        this.newProcessPanel = new JPanel();
        this.newProcessPanel.setLayout(new GridBagLayout());

        this.newProcessLabel = new JLabel(this.i18n.s("newProcessLabelText"));
        this.newProcessErrorLabel = new JLabel();
        this.newProcessErrorLabel.setVisible(false);
        this.newProcessTextField = new JTextField();
        this.newProcessButton = new JButton(this.i18n.s("newProcessButtonText"));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy= 0;
        constraints.gridwidth = 1;
        constraints.gridheight= 1;
        constraints.weighty = 1.0;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10,10,10,10);
        this.newProcessPanel.add(this.newProcessLabel, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill= GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(10,10,10,10);
        this.newProcessPanel.add(this.newProcessTextField, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill= GridBagConstraints.HORIZONTAL;
        constraints.weighty = 1.0;
        constraints.weightx = 1.0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10,10,10,10);
        this.newProcessPanel.add(this.newProcessErrorLabel, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(10,10,10,10);
        this.newProcessPanel.add(this.newProcessButton, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10,10,10,10);
        this.add(this.newProcessPanel);
    }

    private void initPopMenu(){
        this.popupMenu = new JPopupMenu();
        String destroyProcessText = this.i18n.s("destroyProcess");
        JMenuItem destroyProcessItem = new JMenuItem(destroyProcessText);
        destroyProcessItem.addActionListener(e -> {
            int selectedRow = this.table.getSelectedRow();
            long selectedPID = Long.parseLong((String) this.table.getValueAt(selectedRow, PID_COLUMN_TABLE_INDEX));
            MessageSender.sendMessage("destroy", selectedPID, null);
            this.status = NOT_SELECTING_PROCESS;
        });
        String restartProcessText = this.i18n.s("restartProcess");
        JMenuItem restartProcessItem = new JMenuItem(restartProcessText);
        restartProcessItem.addActionListener(e -> {
            int selectedRow = this.table.getSelectedRow();
            long selectedPID = Long.parseLong((String) this.table.getValueAt(selectedRow, PID_COLUMN_TABLE_INDEX));
            MessageSender.sendMessage("restart", selectedPID, null);
            this.status = NOT_SELECTING_PROCESS;
        });
        this.popupMenu.add(destroyProcessItem);
        this.popupMenu.add(restartProcessItem);
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
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weighty = 1.0;
        constraints.weightx = 1.0;
        constraints.insets = new Insets(10,10,10,10);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.EAST;
        this.add(this.scrollPane, constraints);
    }

    public void updateData(Snapshot snapshot){
        Object[][] formattedProcessesData = this.parseData(snapshot);
        this.tableModel.setDataVector(formattedProcessesData, this.tableHeaders);
        this.tableModel.fireTableRowsUpdated(1, formattedProcessesData.length);
    };

    private Object[][] parseData(Snapshot snapshot){
        List<Process> processes = snapshot.getProcesses();
        int size = processes.size();
        Object[][] res = new Object[size][];
        for(int i=0 ; i < size; i++){
            Process process = processes.get(i);
            String[] processData = new String[]{
                    process.getCommand(),
                    process.getUserName(),
                    process.getStartInstant(),
                    String.valueOf(process.getTotalCpuDuration()),
                    String.valueOf(process.getPid()),
                    String.valueOf(process.getParentPid())
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void displayMessage(String message){
        this.actionResultDialog.setMessageLabelText(message);
        this.actionResultDialog.showDialog();
    }

    private void addListeners(){
        /* Manejar evento del boton de iniciar nuevo proceso..*/
        this.newProcessButton.addActionListener(e -> {
            this.newProcessErrorLabel.setText("");
            this.newProcessErrorLabel.setVisible(false);
            String newProcessPath = this.newProcessTextField.getText();
            if(newProcessPath == null || newProcessPath.equals("")){
                this.newProcessErrorLabel.setText(this.i18n.s("newProcessErrorLabelText"));
                this.newProcessErrorLabel.setVisible(true);
                return;
            }
            MessageSender.sendMessage("newProcess", 0, newProcessPath);
        });
    }
}
