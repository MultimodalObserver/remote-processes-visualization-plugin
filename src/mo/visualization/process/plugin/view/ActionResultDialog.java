package mo.visualization.process.plugin.view;

import mo.core.I18n;
import mo.core.ui.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * @author Abraham
 * @version 1.0.0
 *
 *
Class that displays a panel with a result message after creating a new process or restarting or destroying an existing process
 */

class ActionResultDialog extends JDialog {

    private JLabel messageLabel;

    ActionResultDialog(){
        super(null, "Estado del proceso", ModalityType.APPLICATION_MODAL);
        I18n i18n = new I18n(ActionResultDialog.class);
        this.setTitle(i18n.s("actionResultDialogTitle"));
        this.initComponents();
        this.addComponents();
    }

    private void initComponents(){
        this.messageLabel = new JLabel();
        this.messageLabel.setVisible(true);
    }

    private void addComponents(){
        Container contentPane = this.getContentPane();
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        constraints.weighty=1.0;
        constraints.anchor=GridBagConstraints.CENTER;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        contentPane.add(this.messageLabel, constraints);
    }

    void showDialog(){
        this.setMinimumSize(new Dimension(300, 150));
        this.setPreferredSize(new Dimension(300, 150));
        this.pack();
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Utils.centerOnScreen(this);
        this.setVisible(true);
    }

    void setMessageLabelText(String message){
        this.messageLabel.setText(message);
    }

}
