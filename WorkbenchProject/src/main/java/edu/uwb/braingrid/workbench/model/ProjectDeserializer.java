package edu.uwb.braingrid.workbench.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Iterator;

/**
 * Custom deserializer for the Project class.
 *
 * @author Steven Leighton
 */
public class ProjectDeserializer extends JsonDeserializer<Project> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Project deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        ObjectCodec codec = parser.getCodec();
        JsonNode root = codec.readTree(parser);

        final String projectName = root.get("name").asText();
        Project project = new Project(projectName);

        Iterator<JsonNode> iter = root.get("simulations").elements();
        while (iter.hasNext()) {
            JsonNode node = iter.next();
            String simName = node.get("name").asText();
            boolean provEnabled = node.get("provEnabled").asBoolean();
            String simConfigFile = node.get("simConfigFile").asText();
            String resultFileName = node.get("resultFileName").asText();
            JsonNode simSpecNode = node.get("simSpec");
            JsonNode scriptNode = node.get("scriptHistory");

            Simulation sim = new Simulation(simName);
            sim.setProvenanceEnabled(provEnabled);
            sim.setSimConfigFile(simConfigFile);
            sim.setSimResultFile(resultFileName);

            SimulationSpecification simSpec = mapper.readValue(simSpecNode.traverse(),
                    SimulationSpecification.class);
            sim.setSimSpec(simSpec);

            ScriptHistory script = mapper.readValue(scriptNode.traverse(), ScriptHistory.class);
            sim.setScriptHistory(script);

            project.addSimulation(sim);
        }

        return project;
    }
}
