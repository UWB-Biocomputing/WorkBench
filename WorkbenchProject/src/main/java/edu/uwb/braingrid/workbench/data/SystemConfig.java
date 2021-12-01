package edu.uwb.braingrid.workbench.data;

import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This class defines the constants and functions related to system configuration.
 *
 * @author Tom Wong
 */
public final class SystemConfig {

    // The base template config file path
    public static final String BASE_TEMPLATE_CONFIG_FILE_URL = "/templates/BaseTemplateConfig.xml";

    // Attribute names
    public static final String TEMPLATE_PATH_ATTRIBUTE_NAME = "templatePath";
    public static final String TEMPLATE_FILE_NAME_ATTRIBUTE_NAME = "templateFileName";
    public static final String TEMPLATE_DIRECTORY_ATTRIBUTE_NAME = "templateDirectory";
    public static final String NAME_ATTRIBUTE_NAME = "name";
    public static final String CLASS_ATTRIBUTE_NAME = "class";
    public static final String ALL_PARAMS_CLASSES_PATH_ATTRIBUTE_NAME
            = "allParamsClassesConfigFilePath";
    public static final String NODE_PATH_ATTRIBUTE_NAME = "nodePath";

    // Tag names
    public static final String VERTICES_PARAMS_CLASSES_TAG_NAME = "VerticesParamsClasses";
    public static final String EDGES_PARAMS_CLASSES_TAG_NAME = "EdgesParamsClasses";
    public static final String CONNECTIONS_PARAMS_CLASSES_TAG_NAME = "ConnectionsParamsClasses";
    public static final String LAYOUT_PARAMS_CLASSES_TAG_NAME = "LayoutParamsClasses";
    public static final String RECORDER_PARAMS_CLASSES_TAG_NAME = "RecorderParamsClasses";
//    public static final String RESULT_FILE_NAME_TAG_NAME = "stateOutputFileName";
    public static final String RESULT_FILE_NAME_TAG_NAME = "resultFileName";
    //TODO: update RESULT_FILE_NAME_TAG_NAME to "resultFileName" for Graphitti

    private SystemConfig() {
        // utility class cannot be instantiated
    }

    // Mapping between Tag Name and Input Type
    public static final HashMap<String, InputAnalyzer.InputType> TAG_NAME_INPUT_TYPE_MAPPING
            = new HashMap<String, InputAnalyzer.InputType>() {
        {
            put("activeNListFileName", InputAnalyzer.InputType.ACTIVE);
            put("inhNListFileName", InputAnalyzer.InputType.INHIBITORY);
            put("probedNListFileName", InputAnalyzer.InputType.PROBED);
        }
    };

    // Get Base Template Config Document
    public static Document getBaseTemplateInfoDoc() throws IOException, SAXException,
            ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(SystemConfig.class.getResourceAsStream(BASE_TEMPLATE_CONFIG_FILE_URL));
    }

    // Get Sim Params Document
    public static Document getSimParamsDoc() throws IOException, SAXException,
            ParserConfigurationException {
        Document baseTemplateInfoDoc = getBaseTemplateInfoDoc();
        Node baseTemplateInfoNode = baseTemplateInfoDoc.getFirstChild();
        String templatePath = ((Element) baseTemplateInfoNode)
                .getAttribute(TEMPLATE_PATH_ATTRIBUTE_NAME);

        return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(SystemConfig.class.getResourceAsStream("/templates/" + templatePath));
    }

    // Get All Params Classes Document
    public static Document getAllParamsClassesDoc() throws IOException, SAXException,
            ParserConfigurationException {
        Document baseTemplateInfoDoc = getBaseTemplateInfoDoc();
        Node baseTemplateInfoNode = baseTemplateInfoDoc.getFirstChild();
        String allParamsClassesPath = ((Element) baseTemplateInfoNode)
                .getAttribute(ALL_PARAMS_CLASSES_PATH_ATTRIBUTE_NAME);

        return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(SystemConfig.class.getResourceAsStream("/templates/"
                        + allParamsClassesPath));
    }
}
