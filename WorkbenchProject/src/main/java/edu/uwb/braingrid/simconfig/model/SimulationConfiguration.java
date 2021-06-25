package edu.uwb.braingrid.simconfig.model;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.uwb.braingrid.simconfig.model.ConfigDatum.DatumType;

/**
 * Maintains the collection of parameters to be used in a simulation once persisted. In practical
 * terms, this class is equivalent to the template, and eventual final, XML document that stores all
 * the information. Important to note is that this class will only record four hierarchical levels
 * of the XML document, including the root. All other levels will not be recorded.
 *
 * @author Aaron
 */
public class SimulationConfiguration {

    private List<ConfigDatum> configData;

    /**
     * Creates a SimulationConfiguration object.
     *
     * @param doc  Document containing all the information
     */
    public SimulationConfiguration(Document doc) {
        load(doc);
    }

    /**
     * Loads the file with the given name, parses it, and creates a SimulationConfiguration based
     * off that file.
     *
     * @param doc  Document containing all the information
     * @return This SimulationConfiguration
     */
    public SimulationConfiguration load(Document doc) {
        Element root = doc.getDocumentElement();

        configData = new ArrayList<>();
        configData.add(new ConfigDatum(root, DatumType.NULL_TYPE));

        NodeList tabs = root.getChildNodes();
        Node tab;
        NodeList tabChildren;
        Node tabChild;
        NodeList tabChildrenChildren;
        Node tabChildChild;

        //For each tab
            //Add tab to list
            //For each tabChild
                //If it has children, add subhead datum
                    //For each of those children, add paramtype datum
                //If it has no children, add paramtype datum
        for (int i = 0, im = tabs.getLength(); i < im; i++) {
            tab = tabs.item(i);
            if (tab.getNodeType() == Node.ELEMENT_NODE) {
                configData.add(new ConfigDatum(tab, DatumType.TAB_TYPE));
                tabChildren = tab.getChildNodes();
                for (int j = 0, jm = tabChildren.getLength(); j < jm; j++) {
                    tabChild = tabChildren.item(j);
                    if (tabChild.getNodeType() == Node.ELEMENT_NODE) {
                        if (tabChild.hasChildNodes()) {
                            configData.add(new ConfigDatum(tabChild, DatumType.SUBHEAD_TYPE));
                            tabChildrenChildren = tabChild.getChildNodes();
                            for (int k = 0, km = tabChildrenChildren.getLength(); k < km; k++) {
                                tabChildChild = tabChildrenChildren.item(k);
                                if (tabChildChild.getNodeType() == Node.ELEMENT_NODE) {
                                    configData.add(new ConfigDatum(tabChildChild,
                                            DatumType.PARAM_TYPE));
                                }
                            }
                        } else {
                            configData.add(new ConfigDatum(tabChild, DatumType.PARAM_TYPE));
                        }
                        configData.add(new ConfigDatum(null, DatumType.SUBHEAD_END));
                    }
                }
                configData.add(new ConfigDatum(null, DatumType.TAB_END));
            }
        }

        return this;
    }

    /**
     * Builds the configuration...
     *
     * @param doc  Document containing all the information
     * @param root  Root element
     */
    public void build(Document doc, Element root) {
        //Underlying structure to build document
        Element currentTab = null;
        Element currentSubhead = null;
        Element currentElement = null;
        ConfigDatum datum;
        DatumType datumType;

        for (int index = 1, im = configData.size(); index < im; index++) {
            datum = configData.get(index);
            datumType = datum.getDatumType();
            currentElement = datum.getElement(doc);
            if (datumType == DatumType.TAB_TYPE) {
                //Append this to root
                root.appendChild(currentElement);
                currentTab = currentElement;
                currentSubhead = null;
            } else if (datumType == DatumType.TAB_END) {
                currentTab = null;
                currentSubhead = null;
            } else if (datumType == DatumType.SUBHEAD_TYPE) {
                //Append this to most recent tab
                if (currentTab != null) {
                    currentTab.appendChild(currentElement);
                    currentSubhead = currentElement;
                } else {
                    root.appendChild(currentElement);
                }
            } else if (datumType == DatumType.SUBHEAD_END)  {
                currentSubhead = null;
            } else if (datumType == DatumType.PARAM_TYPE) {
                //Append this to most recent subhead if it exists, to the tab otherwise
                if (currentSubhead != null) {
                    currentSubhead.appendChild(currentElement);
                } else if (currentTab != null) {
                    currentTab.appendChild(currentElement);
                } else {
                    root.appendChild(currentElement);
                }
            } else {
                //Append this to root
                root.appendChild(currentElement);
            }
        }
    }

    /**
     * Finds the ConfigDatum at the specified position and returns it.
     *
     * @param position  Position of the desired datum
     * @return ConfigDatum if found, null otherwise
     */
    public ConfigDatum getDatum(int position) {
        ConfigDatum retVal = null;
        if (position >= 0 && position < configData.size()) {
            retVal = configData.get(position);
        }
        return retVal;
    }
}
