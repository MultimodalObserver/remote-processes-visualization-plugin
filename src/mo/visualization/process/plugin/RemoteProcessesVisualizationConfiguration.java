package mo.visualization.process.plugin;

import mo.communication.ClientConnection;
import mo.communication.ConnectionListener;
import mo.communication.PetitionResponse;
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
    public static final String CONTENT_MESSAGE_KEY = "data";
    public static final String ERROR_MESSAGE_KEY = "error";
    public static final String SUCCESS_MESSAGE_KEY = "success";
    public static final String PLUGIN_MESSAGE_KEY = "procesos";


    public RemoteProcessesVisualizationConfiguration(VisualizationConfiguration temporalConfig){
        this.temporalConfig = temporalConfig;
        this.player = new RemoteProcessesPlayer();
    }

    @Override
    public void onMessageReceived(Object o, PetitionResponse petitionResponse) {
        if(petitionResponse.getType().equals(PLUGIN_MESSAGE_KEY)){
            /* Vemos que tipo de mensaje recibimos:

        - data --> info procesos enviada por el capturador
        - success --> se ejecuto exitosamente la accion que se envio en el servidor
        - error --> hubo error al ejecutar la accion
         */
            if(petitionResponse.getHashMap().containsKey(CONTENT_MESSAGE_KEY)){
                String jsonData = petitionResponse.getHashMap().get(CONTENT_MESSAGE_KEY).toString();
                this.player.setCurrentProcessesSnapshot(jsonData);
            }
            else if(petitionResponse.getHashMap().containsKey(SUCCESS_MESSAGE_KEY)){
                //Manejamos el success
                System.out.println(petitionResponse.getHashMap().get(SUCCESS_MESSAGE_KEY));
                this.player.getPanel().displayMessage(petitionResponse.getHashMap().get(SUCCESS_MESSAGE_KEY).toString());
            }
            else if(petitionResponse.getHashMap().containsKey(ERROR_MESSAGE_KEY)){
                System.out.println(petitionResponse.getHashMap().get(ERROR_MESSAGE_KEY));
                this.player.getPanel().displayMessage(petitionResponse.getHashMap().get(ERROR_MESSAGE_KEY).toString());
            }
        }
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
