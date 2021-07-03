package edu.uwb.braingrid.workbenchdashboard;

import java.util.logging.Logger;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import edu.uwb.braingrid.workbench.provvisualizer.ProVis;
import edu.uwb.braingrid.workbenchdashboard.simstarter.SimStartWiz;
import edu.uwb.braingrid.workbenchdashboard.nledit.NLEdit;
import edu.uwb.braingrid.workbenchdashboard.simstarter.SimManager;
import edu.uwb.braingrid.workbenchdashboard.userView.UserView;
import edu.uwb.braingrid.workbenchdashboard.utils.RepoManager;

/**
 * Defines the main display of the screen along with global functionality.
 *
 * @author Max Wright, extended and updated by Joseph Conquest
 */
public class WorkbenchDisplay extends BorderPane {

    private static final Logger LOG = Logger.getLogger(WorkbenchDisplay.class.getName());

    /** The Stage of the FX program. */
    private static Stage primaryStage;
    /** The top menu bar of the screen. */
    private MenuBar menuBar;
    /** The main content of the screen. */
    private TabPane tabPane = new TabPane();

    /**
     * Creates a WorkbenchDisplay which represents the main display.
     *
     * @param primaryStage  The Stage of the FX program
     */
    public WorkbenchDisplay(Stage primaryStage) {
        LOG.info("new " + getClass().getName());
        WorkbenchDisplay.primaryStage = primaryStage;
        setTop(generateMenuBar());
        setBottom(new WorkbenchStatusBar());
        pushProVisStarterPage();
        setCenter(tabPane);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    private MenuBar generateMenuBar() {
        menuBar = new MenuBar();
        menuBar.getMenus().add(generateMenuFile());
        menuBar.getMenus().add(generateMenuRepo());
        return menuBar;
    }

    /**
     * Generates all functionality associated with the "File" tab of the menu bar.
     *
     * @return A complete menu.
     */
    private Menu generateMenuFile() {
        Menu fileMenu = new Menu("_File");
        fileMenu.getItems().add(generateMenuNew());
        fileMenu.getItems().add(generateMenuItemOpen());
        fileMenu.getItems().add(generateMenuRecentProjects());
        return fileMenu;
    }

    private Menu generateMenuRecentProjects() {
        Menu recentProjectMenu = new Menu("Recent Projects");
        return recentProjectMenu;
    }

    private MenuItem generateMenuItemOpen() {
        // Generate menu item
        MenuItem openMenu = new MenuItem("Open");
        // Define functionality
        openMenu.setOnAction(event -> pushSimOpen());
        return openMenu;
    }

    private Menu generateMenuNew() {
        Menu newMenu = new Menu("_New");

        // Generate Items
        MenuItem gsle = new MenuItem("_Growth Simulation Layout Editor");

        // Define Functionality
        gsle.setOnAction(event -> pushGSLEPane());

        // Generate Items
        MenuItem simstarter = new MenuItem("_Simulation Starter");

        // Define Functionality
        simstarter.setOnAction(event -> pushSimWizPop());

        // Generate Items
        MenuItem provis = new MenuItem("_ProVis");

        // Define Functionality
        provis.setOnAction(event -> pushProVisStarterPage());

        // Add
        newMenu.getItems().add(gsle);
        newMenu.getItems().add(simstarter);
        newMenu.getItems().add(provis);
        return newMenu;
    }

    private Menu generateMenuRepo() {
        Menu repoMenu = new Menu("_Repo");
        MenuItem updateMain = new MenuItem("Update Main");

        updateMain.setOnAction(event -> RepoManager.getMasterBranch());

        repoMenu.getItems().add(updateMain);

        return repoMenu;
    }

    /**
     * Adds a new Growth Simulator Layout Editor tab.
     */
    public void pushGSLEPane() {
        Tab tab = new Tab();
        NLEdit pv = new NLEdit(tab);
        tab.setContent(pv.getDisplay());
        tabPane.getTabs().add(tab);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
    }

    /**
     * initializes SimManager and allows the opening of project specification.
     */
    public void pushSimOpen() {
        SimManager pv = new SimManager();
        pv.openProject();
    }

    /**
     Creates a new Simulation Starter Pop-up.
     */
    public void pushSimWizPop() {
        new SimStartWiz();
    }

    /**
     * Add a new Providence Visualizer tab.
     */
    public void pushProVisStarterPage() {
        Tab tab = new Tab();
        ProVis pv = new ProVis(tab);
        tab.setContent(pv.getDisplay());
        tabPane.getTabs().add(tab);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
    }

    public void pushUserViewPage() {
        Tab tab = new Tab();
        UserView uv = new UserView(tab);
        tab.setContent(uv.getDisplay());
        tabPane.getTabs().add(tab);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
    }
}
