/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pdb.model.multimedial.Photo;
import pdb.model.DatabaseModel;



/**
 * 
 * @author raiper34
 */
public class MultimediaPaneController implements Initializable {
    
    @FXML
    public Button selectImageButton; 
    
    @FXML
    public Button deleteImageButton; 
    
    @FXML 
    public Button moreButton;
    
    @FXML 
    public Button mirrorButton;
    
    @FXML 
    public Button flipButton;
    
    @FXML 
    public Button rotateButton;
    
    @FXML 
    public Button smiliarButton;
    
    @FXML
    public AnchorPane imageLayout;
    
    public MainController mainController;
    
    @FXML
    public AnchorPane smiliarPanel;
    
    @FXML
    public AnchorPane imageSmiliarLayout1;
    
    @FXML
    public AnchorPane imageSmiliarLayout2;
    
    @FXML
    public AnchorPane imageSmiliarLayout3;
    
    @FXML
    public AnchorPane imageSmiliarLayout4;
    
    @FXML
    public AnchorPane gamaContrastPanel;
    
    @FXML 
    public Slider gamaRed;
    
    @FXML 
    public Slider gamaGreen;
    
    @FXML 
    public Slider gamaBlue;
    
    @FXML 
    public Slider contrastRed;
    
    @FXML 
    public Slider contrastGreen;
    
    @FXML 
    public Slider contrastBlue;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.smiliarPanel.setVisible(false);
        this.gamaContrastPanel.setVisible(false);
        this.lock();
    }
    
    @FXML
    public void selectImageClick(ActionEvent event) throws SQLException, IOException
    {
        final Stage stage = (Stage) this.selectImageButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));                 
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png")
        );
        File imageFile = fileChooser.showOpenDialog(stage);
        Photo photoModel = new Photo();
        if(imageFile != null)
        {
            int id = this.mainController.selectedSpatialEntity.id;
            photoModel.insertPhotoFromFile(imageFile.getAbsolutePath(), id);
        }
        this.setImageById(this.mainController.selectedSpatialEntity.id);
    }
    
    @FXML
    public void deleteImageClick(ActionEvent event) throws SQLException, IOException
    {
        Photo photoModel = new Photo();
        photoModel.deleteEstatePhoto(this.mainController.selectedSpatialEntity.id);
        this.setImageById(this.mainController.selectedSpatialEntity.id);
    }
    
    @FXML
    public void findSmiliarClick(ActionEvent event) throws SQLException, IOException
    {
        this.imageSmiliarLayout1.getChildren().clear();
        this.imageSmiliarLayout2.getChildren().clear();
        this.imageSmiliarLayout3.getChildren().clear();
        this.imageSmiliarLayout4.getChildren().clear();
        this.smiliarPanel.setVisible(true);
        Photo photoModel = new Photo();
        int id = photoModel.estatesPhotoId(this.mainController.selectedSpatialEntity.id);
        ObservableList<Image> images = FXCollections.observableArrayList();
        images = photoModel.findSmiliar(id);
        final AtomicInteger indexHolder = new AtomicInteger();
        images.forEach((image) -> 
            {
                final int count = indexHolder.getAndIncrement();
                System.out.println(count);
                if(count > 3)
                {
                    return;
                }
                else 
                {
                    ImageView imgView = new ImageView(image);
                    if(count == 0)
                    {
                        imgView.fitWidthProperty().bind(this.imageSmiliarLayout1.widthProperty());
                        imgView.fitHeightProperty().bind(this.imageSmiliarLayout1.heightProperty());
                        this.imageSmiliarLayout1.getChildren().add(imgView);
                    }
                    else if(count == 1)
                    {
                        imgView.fitWidthProperty().bind(this.imageSmiliarLayout2.widthProperty());
                        imgView.fitHeightProperty().bind(this.imageSmiliarLayout2.heightProperty());
                        this.imageSmiliarLayout2.getChildren().add(imgView);
                    }
                    else if(count == 2)
                    {
                        imgView.fitWidthProperty().bind(this.imageSmiliarLayout3.widthProperty());
                        imgView.fitHeightProperty().bind(this.imageSmiliarLayout3.heightProperty());
                        this.imageSmiliarLayout3.getChildren().add(imgView);
                    }
                    else if(count == 3)
                    {
                        imgView.fitWidthProperty().bind(this.imageSmiliarLayout4.widthProperty());
                        imgView.fitHeightProperty().bind(this.imageSmiliarLayout4.heightProperty());
                        this.imageSmiliarLayout4.getChildren().add(imgView);
                    }
                }
            }
        );
    }
    
    @FXML
    public void moreClick(ActionEvent event)
    {
        this.gamaContrastPanel.setVisible(true);
    }
    
    @FXML
    public void backClick(ActionEvent event)
    {
        this.smiliarPanel.setVisible(false);
        this.gamaContrastPanel.setVisible(false);
    }
    
    @FXML
    public void rotateClick(ActionEvent event) throws SQLException, IOException
    {
        this.setProcessedImageById(this.mainController.selectedSpatialEntity.id, "rotate 90");
    }
    
    @FXML
    public void mirrorClick(ActionEvent event) throws SQLException, IOException
    {
        this.setProcessedImageById(this.mainController.selectedSpatialEntity.id, "mirror");
    }
    
    @FXML
    public void flipClick(ActionEvent event) throws SQLException, IOException
    {
        this.setProcessedImageById(this.mainController.selectedSpatialEntity.id, "flip");
    }
    
    @FXML
    public void gammaAplyClick(ActionEvent event) throws SQLException, IOException
    {
        double red = ((double)this.gamaRed.getValue()) / 100.0;
        double green = ((double)this.gamaGreen.getValue()) / 100.0;
        double blue = ((double)this.gamaBlue.getValue()) / 100.0;
        this.setProcessedImageById(this.mainController.selectedSpatialEntity.id, "gamma " + red + " " + green + " " + blue);
        this.smiliarPanel.setVisible(false);
        this.gamaContrastPanel.setVisible(false);
    }
    
    @FXML
    public void contrastApplyClick(ActionEvent event) throws SQLException, IOException
    {
        double red = ((double)this.contrastRed.getValue());
        double green = ((double)this.contrastGreen.getValue());
        double blue = ((double)this.contrastBlue.getValue());
        this.setProcessedImageById(this.mainController.selectedSpatialEntity.id, "contrast " + red + " " + green + " " + blue);
        this.smiliarPanel.setVisible(false);
        this.gamaContrastPanel.setVisible(false);
    }
    
    public void setImageById(int id) throws SQLException, IOException
    {
        this.imageLayout.getChildren().clear();
        Photo photoModel = new Photo();
        Image img = photoModel.getPhotoFromDatabase(photoModel.estatesPhotoId(id));
        if(img != null)
        {
            ImageView imgView = new ImageView(img);
            imgView.fitWidthProperty().bind(this.imageLayout.widthProperty());
            imgView.fitHeightProperty().bind(this.imageLayout.heightProperty());
            this.imageLayout.getChildren().add(imgView);
            this.switchButtons(false);
        }
        else
        {
            this.switchButtons(true);
        }
    }
    
    private void switchButtons(boolean val)
    {
        this.deleteImageButton.setDisable(val);
        this.moreButton.setDisable(val); 
        this.mirrorButton.setDisable(val); 
        this.flipButton.setDisable(val); 
        this.rotateButton.setDisable(val);
        this.smiliarButton.setDisable(val);
    }
    
    public void lock()
    {
        this.imageLayout.getChildren().clear();
        this.selectImageButton.setDisable(true);
        this.switchButtons(true);
    }
    
    public void unlock()
    {
        this.imageLayout.getChildren().clear();
        this.selectImageButton.setDisable(false);
        this.switchButtons(false);
    }
    
    public void setProcessedImageById(int id, String process) throws SQLException, IOException
    {
        this.imageLayout.getChildren().clear();
        Photo photoModel = new Photo();
        Image img = photoModel.getProcessedPhotoFromDatabase(photoModel.estatesPhotoId(id), process);
        if(img != null)
        {
            ImageView imgView = new ImageView(img);
            imgView.fitWidthProperty().bind(this.imageLayout.widthProperty());
            imgView.fitHeightProperty().bind(this.imageLayout.heightProperty());
            this.imageLayout.getChildren().add(imgView);
            this.switchButtons(false);
        }
        else
        {
            this.switchButtons(true);
        }
    }
    
    public void injectMainController(MainController mainController)
    {
        this.mainController = mainController;
    }
    
    public void handleInputEventForShape(InputEvent t, Shape shape) throws SQLException, IOException 
    {
        if (t.getEventType() == MouseEvent.MOUSE_CLICKED)
        {
            this.setImageById(this.mainController.selectedSpatialEntity.id);
            this.smiliarPanel.setVisible(false);
            this.gamaContrastPanel.setVisible(false);
        }
    }

}
