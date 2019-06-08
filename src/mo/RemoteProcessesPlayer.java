package mo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mo.communication.streaming.visualization.PlayableStreaming;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;
import mo.visualization.process.plugin.view.RemoteProcessesPlayerPanel;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteProcessesPlayer implements PlayableStreaming {

    private static final Logger LOGGER = Logger.getLogger(RemoteProcessesPlayer.class.getName());
    private RemoteProcessesPlayerPanel panel;
    private JsonObject currentProcessesSnapshot;
    private JsonParser jsonParser;

    public RemoteProcessesPlayer(){
        this.panel = new RemoteProcessesPlayerPanel();
        this.jsonParser = new JsonParser();
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
        this.panel.updateData(this.currentProcessesSnapshot);
    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    public void setCurrentProcessesSnapshot(String data){
        if(this.panel.getStatus() == RemoteProcessesPlayerPanel.SELECTING_PROCESS){
            return;
        }
        this.currentProcessesSnapshot = this.jsonParser.parse(data).getAsJsonObject();
        this.play();
    }
}
