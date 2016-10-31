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
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
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
        if(imageFile != null)
        {
            Photo photoModel = new Photo();
            System.out.println(imageFile.getAbsolutePath());
            photoModel.insertPhotoFromFile(imageFile.getAbsolutePath());
        }
    }

}
