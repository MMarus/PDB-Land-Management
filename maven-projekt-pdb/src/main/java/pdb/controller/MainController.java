/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import oracle.spatial.geometry.JGeometry;
import static oracle.spatial.geometry.JGeometry.GTYPE_POINT;
import pdb.model.DatabaseModel;
import pdb.model.spatial.Entity;
import pdb.model.spatial.Estate;
import pdb.model.spatial.ImprovedCircle;
import pdb.model.spatial.ImprovedPath;
import pdb.model.spatial.ImprovedPolygon;
import pdb.model.spatial.SpatialEntity;



/**
 * FXML Controller class
 * Main Controller which controll main window of appliacation
 * @author gulan and others
 */
public class MainController implements Initializable {

    @FXML
    private Accordion accordion;
    
    @FXML
    private CheckBox checkBox;

    @FXML
    private Label label;

    @FXML
    private Canvas canvas;

    /**
     * showed object on map in this date, Date si in format "dd. MM. yyyy"
     */
    public String dateOfCurrentlyShowedDatabaseSnapshot;
    
    @FXML
    private AnchorPane mapa;

    private GraphicsContext gc;
    
    private String currentTitledPane = "DEFAULT"; // default 

    private int clickedCount = 2;
    
    @FXML
    private AnchorPane mapPane;
    
    /**
     * Anchor pane instance which represents modal window to connect to database
     */
    @FXML
    public AnchorPane databaseSettingsModal;
    
    /**
     * Instance of Map Pane Controler, so we can use map in other controolers
     */
    @FXML 
    public MapPaneController mapPaneController;
    
    /**
     * Instance of Add Entity Pane Controler, so we can use this contoller in other controolers
     */
    @FXML 
    public AddEntityPaneController addEntityPaneController;
    
    /**
     * Instance of Entity Modification Pane Controler, so we can use this contoller in other controllers
     */
    @FXML 
    public EntityModificationPaneController entityModificationPaneController;
    
    /**
     * Instance of Database SEtting Controller, so we can use this contoller in other controllers
     */
    @FXML 
    public DatabaseSettingsController databaseSettingsController;
    
    /**
     * Instance of Free Holders Pane Controller, so we can use this contoller in other controllers
     */
    @FXML 
    public FreeholdersPaneController freeholdersPaneController;
    
    /**
     * Instance of Time Pane Controller, so we can use this contoller in other controllers
     */
    @FXML 
    public TimePaneController timePaneController;
    
    /**
     * Instance of Spatial Pane Controller, so we can use this contoller in other controllers
     */
    @FXML 
    public SpatialPaneController spatialPaneController;
    
    /**
     * Check box indicates if user want to show underground layer of map 
     */
    @FXML
    public CheckBox undergroundCheckbox;
    
    /**
     * Check box indicates if user want to show ground layer of map 
     */
    @FXML
    public CheckBox groundCheckbox;
    
    /**
     * Check box indicates if user want to show overground layer of map 
     */
    @FXML
    public CheckBox overgroundCheckbox;
    
    /**
     * Instance of MUltimedia Pane Controller, so we can use this contoller in other controllers
     */
    @FXML
    public MultimediaPaneController multimediaPaneController;

    /**
     * Currently selected spatial entity by user on map
     */
    public SpatialEntity selectedSpatialEntity = null; 

    /**
     * Originally (previously) selected spatial entity by user on map
     */
    public JGeometry originalSelectedSpatialEntityGeometry = null; 

    
    /**
     * Initializes the controller class.
     * @param url url
     * @param rb resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // detect change of current titledPane 
        accordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {
            @Override 
            public void changed(ObservableValue<? extends TitledPane> property, final TitledPane oldPane, final TitledPane newPane) {
                if (newPane == null) return;
                switch (newPane.getText()) {
                    case "Add entity":
                        // addEntityPaneController.resetState();
                        currentTitledPane = "Add entity";
                        break;
                    case "Entity modification":
                    {
                        try {
                            entityModificationPaneController.resetState();
                        } catch (SQLException ex) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                        currentTitledPane = "Entity modification";
                        break;
                    case "Multimedia":
                        // multimediaPaneController.resetState();
                        currentTitledPane = "Multimedia";
                        break;
                    case "Time":
                        timePaneController.resetState();
                        currentTitledPane = "Time";
                        break;
                    case "Spatial":
                        spatialPaneController.resetState();
                        currentTitledPane = "Spatial";
                        break;
                    case "Freeholders":
                        // freeHoldersPaneController.resetState();
                        currentTitledPane = "Freeholders";
                        break;
                } 
            }
          });
        this.injects();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy");
        LocalDate date = LocalDate.now();
        this.dateOfCurrentlyShowedDatabaseSnapshot = formatter.format(date);
    }
    
    /**
     * Inject this main controller to all his "children"
     */
    private void injects()
    {
        this.databaseSettingsController.injectMainController(this);
        this.multimediaPaneController.injectMainController(this);
        this.mapPaneController.addParent(this);
        this.addEntityPaneController.addParent(this);
        this.entityModificationPaneController.addParent(this);
        this.timePaneController.addParent(this);
        this.spatialPaneController.addParent(this);
        this.freeholdersPaneController.injectMainController(this);
    }
    
    /**
     * Make Database settings modal invisible
     */
    public void makeModalInvisible()
    {
        this.databaseSettingsModal.setVisible(false);
    }
    
    /**
     * Initialize database by initializing SQL script and redraw map
     * @param event action event
     */
    @FXML
    public void initializeClick(ActionEvent event)
    {
        DatabaseModel db = DatabaseModel.getInstance();
        db.initializeDatabase();
        this.mapPaneController.clearMemoryAndMap();
        this.databaseInitialized();
    }
    
    /**
     * Close program
     * @param event action event
     */
    @FXML
    public void closeClick(ActionEvent event)
    {
        final Stage stage = (Stage) this.mapPane.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Redraw map after database was initialized by SQL script
     */
    public void databaseInitialized()
    {
        this.mapPaneController.initializeSpatialEntitiesModel();
        this.mapPaneController.loadEntities();
        this.mapPaneController.loadEstates();
        this.mapPaneController.drawSpatialEntities();
        try {
            this.freeholdersPaneController.initList();
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Handling input events (which come from MapPaneController and which are redirected here) for shape, especially mouseclick event
     * This input events are next redirected to active Contoller (variable this.currentTitledPane)
     * @param t input event
     * @param shape shape on which event begin
     * @throws SQLException
     * @throws IOException
     */
    public void handleInputEventForShape(InputEvent t, Shape shape) throws SQLException, IOException {
        if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (shape instanceof ImprovedPolygon) {
                ImprovedPolygon improvedShape = (ImprovedPolygon) shape;
                if (improvedShape.isEstate()) {
                    this.multimediaPaneController.unlock();
                    setSelectedSpatialEntity((SpatialEntity) improvedShape.getEstateReference() );
                }
                else {
                    this.multimediaPaneController.lock();
                    setSelectedSpatialEntity((SpatialEntity) improvedShape.getEntityReference() );
                }
            } else if ( shape instanceof ImprovedCircle) {
                this.multimediaPaneController.lock();
                ImprovedCircle improvedShape = (ImprovedCircle) shape;
                setSelectedSpatialEntity((SpatialEntity) improvedShape.getEntityReference() );
            } else if ( shape instanceof ImprovedPath) {
                this.multimediaPaneController.lock();
                ImprovedPath improvedShape = (ImprovedPath) shape;
                setSelectedSpatialEntity((SpatialEntity) improvedShape.getEntityReference() );
            }
            else
            {
                this.multimediaPaneController.lock();
            }
        }
        
        //setSelectedSpatialEntity();
        switch (this.currentTitledPane) {
            case "DEFAULT":
                if (shape instanceof ImprovedPolygon) {
                    ImprovedPolygon polygon = (ImprovedPolygon) shape;
                    if (!polygon.isEstate()) {
                        if (polygon.getEntityReference().getEntityType().equals("house")) {
                            //System.out.println("Event: " + t.getEventType() + " on house");
                        }
                    }
                    else {
                        //System.out.println("Clicked estate");
                    }
                }
                break;
            case "Add entity":
                // addEntityPaneController.handleInputEventForShape(InputEvent t, Shape shape);
                break;
            case "Entity modification":
                entityModificationPaneController.handleInputEventForShape(t, shape);
                break;
            case "Multimedia":
                if (shape instanceof ImprovedPolygon) {
                    ImprovedPolygon improvedShape = (ImprovedPolygon) shape;
                    if (improvedShape.isEstate()) {
                        multimediaPaneController.handleInputEventForShape(t, shape);
                    }
                }
                break;
            case "Time":
                timePaneController.handleInputEventForShape(t, shape);
                break;
            case "Spatial":
                spatialPaneController.handleInputEventForShape(t, shape);
                break;
            case "Freeholders":
                // freeHoldersPaneController.handleInputEventForShape(InputEvent t, Shape shape);
                break;
        
        }
    }
    
    /**
     * This method is called when user click to appripiate checkbox to change visible lyer in map
     * Method redraw map and show only layers which want user to show
     * @param event action event
     * @throws SQLException
     */
    @FXML
    public void groundCheckboxClick(ActionEvent event) throws SQLException
    {
        setSelectedSpatialEntity(null);
        this.mapPaneController.clearMap();
        this.mapPaneController.drawSpatialEntities(this.undergroundCheckbox.isSelected(), this.groundCheckbox.isSelected(), this.overgroundCheckbox.isSelected());
        this.timePaneController.reloadComboBox();
        this.spatialPaneController.resetState(); // must be after this.selectedSpatialEntity = null
        this.entityModificationPaneController.resetState();
    }
    
    /**
     * Handling input events (which come from MapPaneController and which are redirected here) for map
     * This input events are next redirected to active Contoller (variable this.currentTitledPane)
     * @param event input event
     */
    public void handleInputEventForMap(InputEvent event) {
        switch (this.currentTitledPane) {
            case "DEFAULT":
                break;
            case "Add entity":
                addEntityPaneController.handleInputEventForMap(event);
                break;
            case "Entity modification":
                entityModificationPaneController.handleInputEventForMap(event);
                break;
            case "Multimedia":
                //multimediaPaneController.handleInputEventForMap(InputEvent event);
                break;
            case "Time":
                // timePaneController.handleInputEventForMap(InputEvent event);
                break;
            case "Spatial":
                // spatialPaneController.handleInputEventForMap(InputEvent event);
                break;
            case "Freeholders":
                // freeHoldersPaneController.handleInputEventForMap(InputEvent event);
                break;
        
        }     
    }
    
    /**
     * Method to change active controller to which redirect input events on map and on shapes
     * @param state Contoller to which redirect inout events
     * possible values: "DEFAULT", "Add entity", "Entity modification", "Multimedia", "Time", "Spatial", "Freeholders"
     */
    public void setCurrentTitledPane(String state) {
        this.currentTitledPane = state;
    }
    
    /**
     * Method which is called when user click on disconnet from DB
     * Method reshow dialog window to connect to db
     * @param event action event
     */
    @FXML
    public void disconnectClick(ActionEvent event)
    {
        this.mapPaneController.clearMemoryAndMap();
        this.databaseSettingsModal.setVisible(true);

    }
    
    /**
     * For current time context (dateOfCurrentlyShowedDatabaseSnapshot) showe on map all valid objects for selected layers
     * @param event action event
     */
    @FXML
    public void showAllObjectsInSelectedTimeContext(ActionEvent event) {
        this.mapPaneController.clearMap();
        this.mapPaneController.initializeSpatialEntitiesModel();
        this.mapPaneController.loadEntities(this.dateOfCurrentlyShowedDatabaseSnapshot);
        this.mapPaneController.loadEstates(this.dateOfCurrentlyShowedDatabaseSnapshot);
        this.mapPaneController.drawSpatialEntities(this.undergroundCheckbox.isSelected(), this.groundCheckbox.isSelected(), this.overgroundCheckbox.isSelected());
    }
    
    /**
     * Method to select certain spatial entity on map
     * @param spatialEntity spatial entity which will be selected
     */
    public void setSelectedSpatialEntity(SpatialEntity spatialEntity) {
        
        if(selectedSpatialEntity != null && originalSelectedSpatialEntityGeometry != null){
            if(spatialEntity != null && spatialEntity.id == selectedSpatialEntity.id)
                return;
            selectedSpatialEntity.geometry = originalSelectedSpatialEntityGeometry;
        }
        if(spatialEntity != null)
            originalSelectedSpatialEntityGeometry = (JGeometry) spatialEntity.geometry.clone();
        else
            originalSelectedSpatialEntityGeometry = null;
        selectedSpatialEntity = spatialEntity;
    }
}
