package edu.uwb.braingrid.workbench.project;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * Custom serializer for the Project class.
 *
 * @author Steven Leighton
 */
public class ProjectSerializer extends JsonSerializer<Project> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(Project project, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeStringField("name", project.getName());
        gen.writeArrayFieldStart("simulations");
        for (Simulation sim : project.getSimulations().values()) {
            gen.writeStartObject();
            gen.writeStringField("name", sim.getName());
            gen.writeBooleanField("provEnabled", sim.isProvenanceEnabled());
            gen.writeStringField("simConfigFile", sim.getSimConfigFilename());
            gen.writeStringField("resultFileName", sim.getSimResultFile());
            provider.defaultSerializeField("simSpec", sim.getSimSpec(), gen);
            provider.defaultSerializeField("scriptHistory", sim.getScriptHistory(), gen);
            gen.writeEndObject();
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
