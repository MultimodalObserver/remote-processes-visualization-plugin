package mo;

import mo.communication.ClientConnection;
import mo.communication.Command;
import mo.communication.ConnectionListener;
import mo.communication.PetitionResponse;
import mo.communication.streaming.capture.CaptureEvent;
import mo.communication.streaming.visualization.PlayableStreaming;
import mo.communication.streaming.visualization.VisualizableStreamingConfiguration;
import mo.organization.Configuration;
import mo.visualization.process.plugin.model.VisualizationConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class RemoteProcessesVisualizationConfiguration implements VisualizableStreamingConfiguration, ConnectionListener {

    private static final String[] CREATORS = new String[] {"mo.capture.process.plugin.ProcessRecorder"};
    private VisualizationConfiguration temporalConfig;
    private RemoteProcessesPlayer player;

    public RemoteProcessesVisualizationConfiguration(VisualizationConfiguration temporalConfig){
        this.temporalConfig = temporalConfig;
        this.player = new RemoteProcessesPlayer();
    }

    @Override
    public void onMessageReceived(Object o, PetitionResponse petitionResponse) {
        if(!petitionResponse.getType().equals(Command.DATA_STREAMING) || petitionResponse.getHashMap() == null){
            return;
        }
        else if(petitionResponse.getType().equals("procesos")){
            System.out.println(petitionResponse.getHashMap().get("actionResponse"));
            return;
        }
        CaptureEvent captureEvent = (CaptureEvent) petitionResponse.getHashMap().get("data");
        if(!this.temporalConfig.getName().equals(captureEvent.getConfigId())){
            return;
        }
        String data = String.valueOf(captureEvent.getContent());
        this.player.setCurrentProcessesSnapshot(data);
    }

    @Override
    public List<String> getCompatibleCreators() {
        return Arrays.asList(CREATORS);
    }

    @Override
    public PlayableStreaming getPlayer() {
        return this.player;
    }

    @Override
    public String getId() {
        return this.temporalConfig.getName();
    }


    /* Estos no son utilizados*/
    @Override
    public File toFile(File file) {
        return null;
    }

    @Override
    public Configuration fromFile(File file) {
        return null;
    }

    @Override
    public void subscribeToConnection(ClientConnection cc){
        cc.subscribeListener(this);
    }
}
