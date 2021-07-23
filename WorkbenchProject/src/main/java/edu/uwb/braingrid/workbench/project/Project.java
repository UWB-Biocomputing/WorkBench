package edu.uwb.braingrid.workbench.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.uwb.braingrid.workbench.project.model.Datum;
import edu.uwb.braingrid.workbench.project.model.ProjectData;

/**
 *
 * @author Aaron Conrad
 */
public class Project {

    private HashMap<String, ProjectData> projectData;
    private String projectName;

    public Project(String projectName) {
        this.projectName = projectName;
    }

    /**
     *
     * @return filename
     */
    public String persist() throws ParserConfigurationException, TransformerException, IOException {
        // Build New XML Document
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        //  Build Root Node
        Element root = doc.createElement("project");
        doc.appendChild(root);
        // record the project name as an attribute of the root element
        root.setAttribute("name", projectName);

        Set<String> keys = projectData.keySet();
        for (String s : keys) {
            projectData.get(s).appendElement(doc, root);
        }

        // calculate the full path to the project file
        Path projectFilePath = getProjectFilePath();

        // create any necessary non-existent directories
        Files.createDirectories(projectFilePath.getParent());

        // write the content into xml file
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(new DOMSource(doc), new StreamResult(projectFilePath.toFile()));

        return projectFilePath.toString();
    }

    /**
     *
     * @return this Project
     */
    public Project load() throws SAXException, ParserConfigurationException,
            IOException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(getProjectFilePath().toFile());
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();

        NodeList childList = root.getChildNodes();
        NodeList elemChildren;
        ProjectData projData;
        Element elem;
        Element elemChild;
        Datum datum;
        NamedNodeMap attributes;
        String tagName;
        for (int i = 0, im = childList.getLength(); i < im; i++) {
            try {
                elem = (Element) childList.item(i);
                tagName = elem.getTagName();
                projData = new ProjectData(tagName);
                elemChildren = elem.getChildNodes();
                for (int j = 0, jm = elemChildren.getLength(); j < jm; j++) {
                    elemChild = (Element) elemChildren.item(j);
                    datum = new Datum(elemChild.getTagName());
                    attributes = elemChild.getAttributes();
                    for (int k = 0, km = attributes.getLength(); k < km; k++) {
                        datum.setAttribute(attributes.item(k).getNodeName(),
                                elemChild.getAttribute(attributes.item(k).getNodeName()));
                    }
                    datum.setContent(elemChild.getTextContent());
                }
            } catch (ClassCastException e) {
            }
        }
        return this;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void addProjectData(ProjectData projData) {
        projectData.put(projData.getName(), projData);
    }

    public ProjectData getProjectData(String key) {
        ProjectData data = projectData.get(key);
        if (data == null) {
            projectData.put(key, new ProjectData(key));
        }
        return data;
    }

    /**
     * Provides the full path, including the filename, containing the XML for this project.
     *
     * @return The full path, including the filename, for the file containing the XML for this
     *         project
     */
    public Path getProjectFilePath() {
        return getProjectLocation().resolve(projectName + ".xml");
    }

    public ProjectData remove(String projectDataKey) {
        return projectData.remove(projectDataKey);
    }

    /**
     * Provides the folder location for a project based on the currently loaded configuration.
     *
     * @return The path to the project folder for the specified project
     */
    public final Path getProjectLocation() {
        return ProjectManager.getProjectLocation(projectName);
    }
}
