package mo;

import mo.communication.streaming.capture.CaptureConfig;
import mo.communication.streaming.visualization.VisualizationStreamingProvider;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.Configuration;

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

    private static final String[] CREATORS = new String[] {"mo.controllers.ProcessRecorder"};
    List<Configuration> configurations;

    public RemoteProcessesVisualizationPlugin(){
        this.configurations = new ArrayList<>();
    }
    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<String> getCompatibleCreators() {
        return Arrays.asList(CREATORS);
    }

    @Override
    public Configuration initNewStreamingConfiguration(CaptureConfig captureConfig) {

        return null;
    }

    @Override
    public List<Configuration> getConfigurations() {
        return this.configurations;
    }
}
