package mo.visualization.process.plugin;

import com.google.gson.Gson;
import mo.communication.streaming.visualization.PlayableStreaming;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;
import mo.visualization.process.plugin.model.Snapshot;
import mo.visualization.process.plugin.view.RemoteProcessesPlayerPanel;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteProcessesPlayer implements PlayableStreaming {

    private static final Logger LOGGER = Logger.getLogger(RemoteProcessesPlayer.class.getName());
    private RemoteProcessesPlayerPanel panel;
    private Gson gson;
    private Snapshot currentSnapshot;

    RemoteProcessesPlayer(){
        this.panel = new RemoteProcessesPlayerPanel();
        this.gson = new Gson();
        this.currentSnapshot = null;
        try {
            SwingUtilities.invokeAndWait(() -> {
                    DockableElement e = new DockableElement();
                    e.add(this.panel);
                    DockablesRegistry.getInstance().addAppWideDockable(e);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void play() {
        this.panel.updateData(this.currentSnapshot);
    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    void setCurrentProcessesSnapshot(String data){
        if(this.panel.getStatus() == RemoteProcessesPlayerPanel.SELECTING_PROCESS){
            return;
        }
        this.currentSnapshot = this.gson.fromJson(data, Snapshot.class);
        this.play();
    }

    RemoteProcessesPlayerPanel getPanel() {
        return this.panel;
    }
}
