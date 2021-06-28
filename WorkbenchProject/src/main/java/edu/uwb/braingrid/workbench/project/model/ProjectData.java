package edu.uwb.braingrid.workbench.project.model;
// NOT CLEANED! (Needs Class Header / JavaDocs / Line comments in append f(x))

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Aaron
 */
public class ProjectData {

    private String name;
    private HashMap<String, Datum> data;
    private HashMap<String, String> attributes;

    public ProjectData(String name) {
        this.name = name;
        data = new HashMap<>();
        attributes = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Datum addDatum(String dName, String content, List<KeyValuePair> dAttributes) {
        Datum datum = data.get(dName);
        if (datum == null) {
            datum = new Datum(dName);
            data.put(dName, datum);
        }
        if (content != null) {
            datum.setContent(content);
            if (dAttributes != null) {
                datum.setAttributes(dAttributes);
            }
        }
        return datum;
    }

    public Datum getDatum(String dName) {
        Datum datum = data.get(dName);
        if (datum == null) {
            datum = addDatum(dName, null, null);
        }
        return datum;
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public void addAttributes(List<KeyValuePair> moreAttributes) {
        for (KeyValuePair attribute : moreAttributes) {
            attributes.put(attribute.getKey(), attribute.getValue());
        }
    }

    public Element appendElement(Document doc, Element parent) {
        Element e = doc.createElement(name);
        Set<String> attrKeys = attributes.keySet();
        for (String key : attrKeys) {
            e.setAttribute(key, attributes.get(key));
        }
        Set<String> datumKeys = data.keySet();
        for (String key : datumKeys) {
            e.appendChild(data.get(key).getElement(doc));
        }
        parent.appendChild(e);
        return e;
    }
}
