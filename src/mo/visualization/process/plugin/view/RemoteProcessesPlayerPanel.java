package mo.visualization.process.plugin.view;

import mo.core.I18n;
import mo.visualization.process.plugin.model.Process;
import mo.visualization.process.plugin.model.Separator;
import mo.visualization.process.plugin.model.Snapshot;
import mo.visualization.process.util.MessageSender;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Abraham
 * @version 1.0.0
 *  Class that shows a panel that contains a table with the processes that have been received. It also shows a section that allows you to start new processes
 */
public class RemoteProcessesPlayerPanel extends JPanel {
    private static final int PID_COLUMN_TABLE_INDEX = 1;
    private static final int NOT_SELECTING_PROCESS = 0;
    public static final int SELECTING_PROCESS = 1;
    private JTable table;
    /* Estos textos deben ser internacionalizados*/
    private final String[] tableHeaders;
    private I18n i18n;
    private DefaultTableModel tableModel;
    private JPopupMenu popupMenu;
    private ActionResultDialog actionResultDialog;
    private JLabel newProcessErrorLabel;
    private JTextField newProcessTextField;
    private JButton newProcessButton;
    private volatile int status;

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

    /**
     * Method that initializes the section that allows to start new processes
     */
    private void initSearchBar() {
        JPanel newProcessPanel = new JPanel();
        newProcessPanel.setLayout(new GridBagLayout());

        JLabel newProcessLabel = new JLabel(this.i18n.s("newProcessLabelText"));
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
        newProcessPanel.add(newProcessLabel, constraints);

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
        newProcessPanel.add(this.newProcessTextField, constraints);

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
        newProcessPanel.add(this.newProcessErrorLabel, constraints);

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
        newProcessPanel.add(this.newProcessButton, constraints);

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
        this.add(newProcessPanel);
    }

    /**
     * Method that initializes the popup menu that is displayed when right clicking on a table row.
     * The popup menu contains two items: destroy and restart process.
     */
    private void initPopMenu(){
        this.popupMenu = new JPopupMenu();
        String destroyProcessText = this.i18n.s("destroyProcess");
        JMenuItem destroyProcessItem = new JMenuItem(destroyProcessText);
        destroyProcessItem.addActionListener(e -> {
            int selectedRow = this.table.getSelectedRow();
            long selectedPID = (long) this.table.getValueAt(selectedRow, PID_COLUMN_TABLE_INDEX);
            MessageSender.sendMessage("destroy", selectedPID, null);
            this.status = NOT_SELECTING_PROCESS;
        });
        String restartProcessText = this.i18n.s("restartProcess");
        JMenuItem restartProcessItem = new JMenuItem(restartProcessText);
        restartProcessItem.addActionListener(e -> {
            int selectedRow = this.table.getSelectedRow();
            long selectedPID = (long) this.table.getValueAt(selectedRow, PID_COLUMN_TABLE_INDEX);
            MessageSender.sendMessage("restart", selectedPID, null);
            this.status = NOT_SELECTING_PROCESS;
        });
        this.popupMenu.add(destroyProcessItem);
        this.popupMenu.add(restartProcessItem);
    }

    /**
     * Method that initializes the table headers
     */
    private void initTableHeaders(){
        this.tableHeaders[0] = this.i18n.s("applicationColumnName");
        this.tableHeaders[1] = this.i18n.s("pidColumnName");
        this.tableHeaders[2] = this.i18n.s("usernameColumnName");
        this.tableHeaders[3] = this.i18n.s("startInstantColumnName");
        this.tableHeaders[4] = this.i18n.s("totalCPUDurationColumnName");
        this.tableHeaders[5] = this.i18n.s("parentPidColumnName");
    }

    /**
     * Method that initializes the whole table
     */
    private void initTable(){
        this.tableModel = new DefaultTableModel();
        for(String header: this.tableHeaders){
            this.tableModel.addColumn(header);
        }
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
        JScrollPane scrollPane = new JScrollPane(table);
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
        this.add(scrollPane, constraints);
    }

    /**Method that update the data displayed in the table, using a processes @see(Snapshot)
     * @param snapshot The Snapshot or processes that will be displayed on the table
     */
    public void updateData(Snapshot snapshot){
        if(snapshot == null || snapshot.getProcesses().isEmpty()){
            return;
        }
        SwingUtilities.invokeLater(() -> {
            this.clearTable();
            for(Process process : snapshot.getProcesses()){
                Object[] values = new Object[]{
                        this.getApplicationName(process),
                        process.getPid(),
                        process.getUserName(),
                        process.getStartInstant(),
                        process.getTotalCpuDuration(),
                        process.getParentPid()
                };
                this.tableModel.addRow(values);
            }
        });
    }


    /**Method that get the actual status of the panel
     * @return status of the panel: selecting or not selecting a process (row of the table)
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * Method that displays the action result dialog associated with the panel.
     * The dialog is shown after a item of the popup menu is pressed.
     * @param message The message that will be shown in the dialog.
     */
    public void displayMessage(String message){
        this.actionResultDialog.setMessageLabelText(message);
        this.actionResultDialog.showDialog();
    }

    /**
     *
     */
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

    private String getApplicationName(Process process){
        if(process == null || process.getCommand() == null || process.getCommand().isEmpty()){
            return null;
        }
        String separator = process.getCommand().contains(Separator.WINDOWS_FILE.getValue()) ? Separator.REGEX_WINDOW_FILE.getValue()
                : Separator.FILE.getValue();
        String[] parts = process.getCommand().split(separator);
        return parts[parts.length - 1];
    }

    private void clearTable(){
        this.tableModel.setRowCount(0);
    }
}
