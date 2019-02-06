package mo;

import mo.communication.Command;
import mo.communication.ConnectionListener;
import mo.communication.PetitionResponse;
import mo.communication.streaming.capture.CaptureEvent;
import mo.communication.streaming.visualization.PlayableStreaming;
import mo.communication.streaming.visualization.VisualizableStreamingConfiguration;
import mo.organization.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class RemoteProcessesVisualizationConfiguration implements VisualizableStreamingConfiguration, ConnectionListener {

    private static final String[] CREATORS = new String[] {"mo.controllers.ProcessRecorder"};
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
        CaptureEvent captureEvent = (CaptureEvent) petitionResponse.getHashMap().get("data");
        System.out.println("ESTO LLEGO AL PLUGIN "+(String)captureEvent.getContent());
        if(!this.temporalConfig.getName().equals(captureEvent.getConfigId())){
            return;
        }
        /* ESTO ES LO QUE QUEREMOS QUE SE HAGA EN EL PLAYER CUANDO SE RECIBE EL DATO

        EN MI CASO ME IMPORTA SOLO EL RENDER DE LA INFO QUE VIENE
         */
        this.player.setCurrentEvent((String)captureEvent.getContent());

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
}
