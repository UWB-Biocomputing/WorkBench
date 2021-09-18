package edu.uwb.braingrid.workbench.data;

import java.util.HashMap;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Contains the information relevant to one equivalent XML node, not including its children. A
 * ConfigDatum must have a type, which designates what data it is likely to contain. Any datum type
 * may contain any data, but some functions are designed for use with only certain types.
 *
 * @author Aaron
 */
public class ConfigDatum {

    public enum DatumType {
        /** Null datum type. */
        NULL_TYPE,
        /** Tab datum type. */
        TAB_TYPE,
        /** Subhead datum type. */
        SUBHEAD_TYPE,
        /** Param datum type. */
        PARAM_TYPE,
        /** Tab end. */
        TAB_END,
        /** Subhead end. */
        SUBHEAD_END
    }

    //Specific attribute tags
    private static final String LABEL_TAG = "name";
    private static final String FILE_CHOOSER_TAG = "fileChooser";

    private final DatumType datumType;
    private final String tagName;
    private String content;
    private final HashMap<String, String> attributes;

    /**
     * Creates a ConfigDatum object.
     *
     * @param element  An XML node (not including its children)
     * @param datumType  The type of data the node contains
     */
    public ConfigDatum(Node element, DatumType datumType) {
        //Check whether datum type is valid
        this.datumType = (datumType == null) ? DatumType.NULL_TYPE : datumType;
        attributes = new HashMap<>();

        if (element != null) {
            tagName = element.getNodeName();
            content = (this.datumType == DatumType.PARAM_TYPE) ? element.getTextContent() : null;

            NamedNodeMap nodes = element.getAttributes();
            if (nodes != null) {
                Node node;
                for (int i = 0, im = nodes.getLength(); i < im; i++) {
                    node = nodes.item(i);
                    attributes.put(node.getNodeName(), node.getNodeValue());
                }
            }
        } else {
            tagName = "EmptyNode";
            content = null;
        }
    }

    /**
     * Gets this datum's type.
     *
     * @return datumType
     */
    public DatumType getDatumType() {
        return datumType;
    }

    /**
     * Gets this datum's tag name.
     *
     * @return tagName
     */
    public String getName() {
        return tagName;
    }

    /**
     * Gets this datum's text content.
     *
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the text content to the new value.
     *
     * @param newContent  The new content value
     */
    public void setContent(String newContent) {
        content = newContent;
    }

    /**
     * Gets the content of the attribute "LABEL_TAG" if the datum is not of NULL_TYPE. Returns null
     * otherwise, or if the attribute doesn't exist.
     *
     * @return Content of attribute LABEL_TAG
     */
    public String getLabel() {
        String label = null;
        if (datumType != DatumType.NULL_TYPE) {
            label = attributes.get(LABEL_TAG);
        }
        return label;
    }

    /**
     * Gets the content of the attribute "FILE_CHOOSER_TAG" if the datum is of PARAM_TYPE, and
     * converts it to a boolean which is returned. False is return otherwise, or if the attribute
     * doesn't exist.
     *
     * @return Boolean content of attribute FILE_CHOOSER_TAG
     */
    public boolean isFileChooser() {
        boolean isChooser = false;
        if (datumType == DatumType.PARAM_TYPE) {
            String chooser = attributes.get(FILE_CHOOSER_TAG);
            if (chooser != null) {
                isChooser = Boolean.parseBoolean(chooser);
            }
        }
        return isChooser;
    }

    /**
     * Returns this datum structured as an Element. The Element will look the same as it was when
     * the datum was created, but possibly with the content changed.
     *
     * @param doc  Document
     * @return This datum as an element
     */
    public Element getElement(Document doc) {
        Element element = doc.createElement(tagName);
        Set<String> keys = attributes.keySet();
        for (String key : keys) {
            element.setAttribute(key, attributes.get(key));
        }
        element.setTextContent(content);
        return element;
    }
}
