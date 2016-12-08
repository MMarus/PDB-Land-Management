/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import javafx.util.Callback;
import jdk.internal.util.xml.impl.Input;
import oracle.spatial.geometry.JGeometry;
import pdb.model.freeholder.Freeholder;
import pdb.model.freeholder.FreeholderModel;

/**
 *
 * @author archie
 */
public class EntityModificationPaneController implements Initializable {
    
    public MainController mainController;
    
    @FXML
    private MainController fXMLController;
    
    @FXML
    public AnchorPane entityModificationAnchorPane;
    
    @FXML 
    private Button buttonDeleteObjInInterval;
    
    @FXML
    private ComboBox comboboxFreeholders;
    
    @FXML
    private Button buttonSave;
    
    @FXML
    public TextArea descriptionArea;
    
    @FXML
    public TextField nameField;
    
    @FXML
    public DatePicker pickerFrom;
    
    @FXML
    public DatePicker pickerTo;
    
    @FXML
    public ToggleGroup editation;
    
    String editationMode = "Move";
    
    Point2D start = null;
    Point2D end = null;
    JGeometry originalGeometry;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //drawTest();
        System.out.println("Hello from EntityModificationPane");
        editation.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                RadioButton checkedButton = (RadioButton) t1.getToggleGroup().getSelectedToggle();
                editationMode = checkedButton.getText();
            }
        });
    }
    
    /*
    * @param MainController mainController
    */
    public void addParent(MainController mainController) {
        this.mainController = mainController;
    }
    
    public void saveClick(ActionEvent event) throws SQLException
    {
       Freeholder test = (Freeholder) this.comboboxFreeholders.getSelectionModel().getSelectedItem();
       if(test != null)
       {
        System.out.println(test.id);
       }
    }
    
    public void handleInputEventForShape(InputEvent t, Shape shape) throws SQLException, IOException 
    {
        //this.mainController.selectedSpatialEntity;
        if (t.getEventType() == MouseEvent.MOUSE_CLICKED)
        {
            FreeholderModel freeholdersModel = new FreeholderModel();
            freeholdersModel.getFreeHoldersFromDatabase();
            ObservableList<Freeholder> freehodlers = freeholdersModel.getListAllFreeHolders();
            this.comboboxFreeholders.setItems(freehodlers);

            this.comboboxFreeholders.setCellFactory(new Callback<ListView<Freeholder>,ListCell<Freeholder>>(){
                 @Override
                 public ListCell<Freeholder> call(ListView<Freeholder> l){
                     return new ListCell<Freeholder>(){
                         @Override
                         protected void updateItem(Freeholder item, boolean empty) {
                             super.updateItem(item, empty);
                             if (item == null || empty) {
                                 setGraphic(null);
                             } else {
                                 setText(item.first_name + " " +item.surname);
                             }
                         }
                     } ;
                 }
             });
            
            //SetDefault Values
            this.nameField.setText(this.mainController.selectedSpatialEntity.name);
            this.descriptionArea.setText(this.mainController.selectedSpatialEntity.description);
            int index = 0;
            for(Freeholder freeholder : freehodlers) {
                if(freeholder.id == this.mainController.selectedSpatialEntity.id)
                 {
                    break;
                 }
                index++;
            }
            this.comboboxFreeholders.getSelectionModel().select(index);
            
            Instant instant = Instant.ofEpochMilli(this.mainController.selectedSpatialEntity.validFrom.getTime());
            this.pickerFrom.setValue(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate());
            instant = Instant.ofEpochMilli(this.mainController.selectedSpatialEntity.validTo.getTime());
            this.pickerTo.setValue(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate());
        }
    }
    
    
    
    
    public void handleInputEventForMap(InputEvent t){
        switch (editationMode) {
            case "Move":
                doMove(t);
                break;
            case "Resize":
                doResize(t);
                break;
            case "Rotate":
                break;
        }
        
    }
    
    public void doMove(InputEvent t){
        if(t.getEventType() == MouseEvent.MOUSE_PRESSED && this.mainController.selectedSpatialEntity != null){
            JGeometry point = new JGeometry(((MouseEvent) t).getX(), ((MouseEvent) t).getY(), 0);
            try {
                if( ! point.anyInteract(this.mainController.selectedSpatialEntity.geometry, 10, "FALSE"))
                    return;
            } catch (Exception ex) {
                Logger.getLogger(EntityModificationPaneController.class.getName()).log(Level.SEVERE, null, ex);
            }
            start = new Point2D(((MouseEvent) t).getX(), ((MouseEvent) t).getY());
            originalGeometry = this.mainController.selectedSpatialEntity.geometry;
        } else if(t.getEventType() == MouseEvent.MOUSE_DRAGGED){
            if (start == null || this.mainController.selectedSpatialEntity == null) {
                return;
            }
            
            end = new Point2D(((MouseEvent) t).getX(), ((MouseEvent) t).getY());
            Point2D translation = end.subtract(start);
            JGeometry translated = null;
            System.out.println("Posun o x:" + translation.getX() + " a y: = " + translation.getY());
            try {
                translated = originalGeometry.affineTransforms(true, translation.getX(), translation.getY(),
                    0, false, null, 0, 0, 0, false, null, null, 0, 0, false, 0, 0, 0, 0, 0, 0, false, null, null, 0, false, null, null);
                if(! transInMap(translated)){
                    System.out.println("pdb.controller.EntityModificationPaneController.handleInputEventForMap(): NOT IN BOUNDS");
                    return;
                }
                this.mainController.selectedSpatialEntity.geometry = translated;
                this.mainController.mapPaneController.drawSpatialEntities();
            } catch (Exception ex) {
                Logger.getLogger(EntityModificationPaneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if (t.getEventType() == MouseEvent.MOUSE_RELEASED) {
            start = null;
            originalGeometry = null;
        }
    }
    
    public boolean transInMap(JGeometry translated){
        JGeometry map = new JGeometry(3, 0, new int[]{1, 1003, 1},
            new double[]{0,0, 650,0, 650,650, 0,650, 0,0}
        );
        try {
            if(translated.isInside(map, 0, "FALSE"))
                return true;
        } catch (Exception ex) {
            Logger.getLogger(EntityModificationPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void doResize(InputEvent t) {
        if(t.getEventType() == ScrollEvent.SCROLL_STARTED){
            
        } else if (t.getEventType() == ScrollEvent.SCROLL_FINISHED){
            
        }
    }

}
