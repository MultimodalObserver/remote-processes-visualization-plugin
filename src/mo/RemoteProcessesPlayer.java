package mo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mo.communication.streaming.visualization.PlayableStreaming;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteProcessesPlayer implements PlayableStreaming {

    private static final Logger LOGGER = Logger.getLogger(RemoteProcessesPlayer.class.getName());
    private RemoteProcessesPlayerPanel panel;
    private JsonObject currentProcessesSnapshot;
    private static final JsonParser JSON_PARSER = new JsonParser();

    public RemoteProcessesPlayer(){
        System.out.println( "ESTOY CREANDO EL PLAYER REMOTO");
        this.panel = new RemoteProcessesPlayerPanel();
        SwingUtilities.invokeLater(() -> {
            try {
                DockableElement e = new DockableElement();
                e.add(this.panel);
                DockablesRegistry.getInstance().addAppWideDockable(e);
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, null, ex);
            }
        });
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
        this.currentProcessesSnapshot = JSON_PARSER.parse(data).getAsJsonObject();
        this.play();
    }
}
