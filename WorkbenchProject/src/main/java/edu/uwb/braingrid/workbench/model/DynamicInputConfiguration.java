package edu.uwb.braingrid.workbench.model;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.uwb.braingrid.workbench.SystemConfig;

/**
 * Dynamically maintains data for an input configuration.
 *
 * @author Tom Wong
 */
public class DynamicInputConfiguration {

    private Document inputConfig;
    private ArrayList<Node> paramValues;
    private static final Logger LOG = Logger.getLogger(DynamicInputConfiguration.class.getName());

    /**
     * Responsible for initializing containers for parameters/values and their default values,
     * as well as constructing this input configuration object.
     *
     * @throws java.lang.Exception
     */
    public DynamicInputConfiguration() throws Exception {
        LOG.info("New " + getClass().getName());
        Document baseTemplateInfoDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(System.getProperty("user.dir") + File.separator + SystemConfig.BASE_TEMPLATE_INFO_XML_File_URL);
        Node baseTemplateNode = baseTemplateInfoDoc.getFirstChild();
        String templatePath = ((Element) baseTemplateNode).getAttribute(SystemConfig.TEMPLATE_PATH_ATTRIBUTE_NAME);
        inputConfig = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(System.getProperty("user.dir") + File.separator + templatePath);
    }

    /**
     * Initializes this configuration object from an existing configuration document.
     *
     * @param doc  Configuration document
     */
    public DynamicInputConfiguration(Document doc) {
        inputConfig = doc;
    }

    /**
     * Sets the value of all parameters in the input configuration.
     *
     * @param values  The values of the parameters to update
     */
    public void setValues(ArrayList<String> values) {
        for (int i = 0; i < values.size(); i++) {
            paramValues.get(i).setTextContent(values.get(i));
        }
    }

    ///**
    // * Provides the default value for a specified parameter.
    // *
    // * @param parameter  The key for the parameter that's default value should be provided
    // * @return The default value of the parameter, or an empty string if the parameter was not
    // *         contained in the default input configuration
    // */
    // public String getDefaultValue(String parameter) {
    // String value;
    // if ((value = defaultValues.get(parameter)) == null) {
    // value = "";
    // }
    // return value;
    // }

    /**
     * Provides the current input configuration map.
     *
     * @return The input configuration map
     */
    public Document getDocument() {
        return inputConfig;
    }

    /**
     * Set parameter values from a list of nodes.
     *
     * @param nodes  List of nodes containing parameter values
     */
    public void setElements(ArrayList<Node> nodes) {
        paramValues = nodes;
    }

    ///**
    // * Copies all of the default input configuration to the current configuration.
    // */
    // public void setAllToDefault() {
    // inputConfig = new HashMap<>(defaultValues);
    // }
}
