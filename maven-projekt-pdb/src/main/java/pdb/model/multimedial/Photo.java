/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.multimedial;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import oracle.ord.im.OrdImage;
import oracle.jdbc.OraclePreparedStatement;
import java.sql.PreparedStatement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import oracle.jdbc.OracleResultSet;
import pdb.model.DatabaseModel;
import pdb.model.freeholder.Freeholder;

/**
 *
 * @author archie
 */
public class Photo {

    private DatabaseModel database;
    private Connection connection;
    
    public Photo() throws SQLException {
        this.database = DatabaseModel.getInstance();
        this.connection = this.database.getConnection();
    }

    
    public OrdImage getProxy(int id) throws SQLException
    {
        OrdImage imgProxy = null;
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) this.connection.prepareStatement(
            "select photo from photos where id=" + id + " for update"
        );
        try 
        {
            OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery();
            try 
            {
                if (rset.next()) 
                {
                    imgProxy = (OrdImage) rset.getORAData("photo", OrdImage.getORADataFactory());
                }
            } 
            finally 
            {
                rset.close();
            }
            } 
        finally 
        {
            pstmtSelect.close();
        }
        return imgProxy;
    }
    
    public void insertPhotoFromFile(String filename, int estate_id) throws SQLException, IOException 
    {
        int id = this.getMaxId() + 1;
        boolean autoCommit = this.connection.getAutoCommit();
        this.connection.setAutoCommit(false);
        try 
        {

            // insert a new record with an empty ORDImage object
            OraclePreparedStatement pstmtInsert = (OraclePreparedStatement) this.connection.prepareStatement(
                    "insert into photos(id, photo) values (" + id + ", ordsys.ordimage.init())");

            pstmtInsert.executeUpdate();
            pstmtInsert.close();
            
            this.deleteEstatePhoto(estate_id);
            this.assignPhotoToEstate(estate_id, id);

            OrdImage imgProxy = this.getProxy(id);
            
            // load the media data from a file to the ORDImage Java object
            imgProxy.loadDataFromFile(filename);
            imgProxy.setProperties();
            
            OraclePreparedStatement pstmtUpdate1 = (OraclePreparedStatement) this.connection.prepareStatement(
                "update photos set photo = ? where id = " + id
            );
            try {
                pstmtUpdate1.setORAData(1, imgProxy);
                pstmtUpdate1.executeUpdate();
            } 
            finally 
            {
                pstmtUpdate1.close();
            }
            
            PreparedStatement pstmtUpdate2 = this.connection.prepareStatement(
                "update photos p set p.photo_si=SI_StillImage(p.photo.getContent()) where id = " + id
            );
            try 
            {
                pstmtUpdate2.executeUpdate();
            } 
            finally 
            {
                pstmtUpdate2.close();
            }
            
            PreparedStatement pstmtUpdate3 = this.connection.prepareStatement(
                    "update photos p set"+
                    " p.photo_ac=SI_AverageColor(p.photo_si),"+
                    " p.photo_ch=SI_ColorHistogram(p.photo_si),"+
                    " p.photo_pc=SI_PositionalColor(p.photo_si),"+
                    " p.photo_tx=SI_Texture(p.photo_si) where id = "+id
            );
            try 
            {
                pstmtUpdate3.executeUpdate();
            } 
            finally 
            {
                pstmtUpdate3.close();
            }
            this.connection.commit();
        } 
        finally 
        {
            this.connection.setAutoCommit(autoCommit);
        }
    }
    
    public Image getPhotoFromDatabase(int id) throws SQLException, IOException
    {
        OrdImage imgProxy = this.getProxy(id);
        if(imgProxy == null)
        {
            return null;
        }
        BufferedImage bufferedImg = ImageIO.read(new ByteArrayInputStream(imgProxy.getDataInByteArray()));
        Image image = SwingFXUtils.toFXImage(bufferedImg, null);
        return image;
    }
    
    private int getMaxId() throws SQLException
    {
        int max = 0;
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) this.connection.prepareStatement(
            "select MAX(id) as max from photos"
        );
        OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery();
        if (rset.next()) 
        {
            max = (int) rset.getInt("max");
        }
        rset.close();
        pstmtSelect.close();
        return max;
    }
    
    public int estatesPhotoId(int estate_id) throws SQLException
    {
        int id = 0;
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) this.connection.prepareStatement(
            "select photos_id from estates where id = " + estate_id
        );
        OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery();
        if (rset.next()) 
        {
            id = (int) rset.getInt("photos_id");
        }
        rset.close();
        pstmtSelect.close();
        return id;
    }
    
    public void deleteEstatePhoto(int estate_id) throws SQLException
    {
        int photo_id = this.estatesPhotoId(estate_id);
        this.setEstatePhotoToNull(estate_id);
        this.deletePhotoById(photo_id);
    }
    
    private void deletePhotoById(int id) throws SQLException
    {
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) this.connection.prepareStatement(
            "delete from photos where id = " + id
        );
        pstmtSelect.executeQuery();
        pstmtSelect.close();
    }
    
    private void setEstatePhotoToNull(int id) throws SQLException
    {
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) this.connection.prepareStatement(
            "update estates set photos_id=null where id = " + id
        );
        pstmtSelect.executeQuery();
        pstmtSelect.close();
    }
    
    private void assignPhotoToEstate(int estate_id, int photo_id) throws SQLException
    {
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) this.connection.prepareStatement(
            "update estates set photos_id= " + photo_id + " where id = " + estate_id
        );
        pstmtSelect.executeQuery();
        pstmtSelect.close();
    }
    
    public ObservableList<Image> findSmiliar(int id) throws SQLException, IOException
    {
        ObservableList<Image> images = FXCollections.observableArrayList();
        OrdImage imgProxy = null;
        PreparedStatement pstmtSelect = connection.prepareStatement(
                "SELECT dst.*, SI_ScoreByFtrList(" +
                "new SI_FeatureList(src.photo_ac,0.7,src.photo_ch,0.1,src.photo_pc,0.1,src.photo_tx,0.1),dst.photo_si)" + 
                " as similarity FROM photos src, photos dst " + 
                "WHERE (src.id <> dst.id) AND src.id = " + id + 
                " ORDER BY similarity ASC"
        );
        OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery();
        while (rset.next()) 
        {
            imgProxy = (OrdImage) rset.getORAData("photo", OrdImage.getORADataFactory());
            if(imgProxy != null)
            {
                if(imgProxy.getDataInByteArray() != null)
                {
                    BufferedImage bufferedImg = ImageIO.read(new ByteArrayInputStream(imgProxy.getDataInByteArray()));
                    images.add(SwingFXUtils.toFXImage(bufferedImg, null));
                }
            }
        }
        rset.close();
        pstmtSelect.close();
        return images;
    }

}
