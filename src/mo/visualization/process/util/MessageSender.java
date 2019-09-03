package mo.visualization.process.util;

import com.google.gson.Gson;
import mo.communication.ClientConnection;
import mo.communication.Command;
import mo.communication.PetitionResponse;
import mo.visualization.process.plugin.RemoteProcessesVisualizationConfiguration;
import mo.visualization.process.plugin.model.ProcessRequest;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

public class MessageSender {

    public static void sendMessage(String action, long selectedProcessPID, String newProcessPath){
        HashMap<String, Object> data = new HashMap<>();
        Gson gson = new Gson();
        ProcessRequest processRequest = new ProcessRequest();
        processRequest.setAction(action);
        processRequest.setSelectedProcessPID(selectedProcessPID);
        processRequest.setNewProcessPath(newProcessPath);
        data.put(RemoteProcessesVisualizationConfiguration.CONTENT_MESSAGE_KEY, gson.toJson(processRequest));
        PetitionResponse petitionResponse = new PetitionResponse(RemoteProcessesVisualizationConfiguration.PLUGIN_MESSAGE_KEY,
                data);
        try {
            ClientConnection.getInstance().getServer().send(petitionResponse);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
