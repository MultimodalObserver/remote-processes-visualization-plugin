package mo;

import mo.communication.streaming.capture.CaptureConfig;
import mo.communication.streaming.visualization.VisualizationStreamingProvider;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.Configuration;
import mo.visualization.process.plugin.model.VisualizationConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Extension(
        xtends = {
                @Extends(
                        extensionPointId = "mo.communication.streaming.visualization.VisualizationStreamingProvider"
                )
        }
)
public class RemoteProcessesVisualizationPlugin implements VisualizationStreamingProvider {

    private static final String[] CREATORS = new String[] {"mo.capture.process.plugin.ProcessRecorder"};
    List<Configuration> configurations;

    public RemoteProcessesVisualizationPlugin(){
        this.configurations = new ArrayList<>();
    }
    @Override
    public String getName() {
        return "Remote Processes Visualization";
    }

    @Override
    public List<String> getCompatibleCreators() {
        return Arrays.asList(CREATORS);
    }

    @Override
    public Configuration initNewStreamingConfiguration(CaptureConfig captureConfig) {
        System.out.println("ESTOY INICIALIZANDO UNA NUEVA CONFIGURACION DE STREAMING");
        VisualizationConfiguration temporalConfig = new VisualizationConfiguration(captureConfig.getConfigID());
        RemoteProcessesVisualizationConfiguration configuration = new RemoteProcessesVisualizationConfiguration(temporalConfig);
        this.configurations.add(configuration);
        return configuration;
    }

    @Override
    public List<Configuration> getConfigurations() {
        return this.configurations;
    }
}
