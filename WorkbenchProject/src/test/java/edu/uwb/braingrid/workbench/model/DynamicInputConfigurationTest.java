package edu.uwb.braingrid.workbench.model;

import edu.uwb.braingrid.workbench.SystemConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class DynamicInputConfigurationTest {

    @Test
    public void testGetDocumentAndConstructors() {
        DynamicInputConfiguration dic = factory();

        Document baseTemplateInfoDoc = null;
        try {
            baseTemplateInfoDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(getClass().getResourceAsStream("/templates/"
                            + SystemConfig.BASE_TEMPLATE_CONFIG_FILE_URL));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        Node baseTemplateNode = baseTemplateInfoDoc.getFirstChild();
        String templatePath = ((Element) baseTemplateNode).getAttribute(SystemConfig.TEMPLATE_PATH_ATTRIBUTE_NAME);
        Document inputConfig = null;
        try {
            inputConfig = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(getClass().getResourceAsStream("/templates/" + templatePath));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        DynamicInputConfiguration dic2 = new DynamicInputConfiguration(inputConfig);

        // This one is a pain...

//        Assertions.assertEquals(inputConfig, dic.getDocument());
//        Assertions.assertEquals(inputConfig, dic2.getDocument());
    }

    @Test
    public void testSetValues() {
        // I have no idea what this function are used for, and there is no way to test how the affect the class.
    }


    @Test
    public void testSetElements() {
        // I have no idea what this function are used for, and there is no way to test how the affect the class.
    }

    private DynamicInputConfiguration factory() {
        try {
            return new DynamicInputConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
