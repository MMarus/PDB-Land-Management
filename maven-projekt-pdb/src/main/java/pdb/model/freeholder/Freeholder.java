/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.freeholder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import pdb.model.DatabaseModel;
import pdb.model.spatial.Estate;

/**
 *
 * @author gulan
 */
public class Freeholder 
{

    private final SimpleStringProperty name;
    public String first_name;
    public String surname;
    public String birthDate;
    public int id;
    
    Freeholder(int id, String first_name, String surname, String birthDate)
    {
        this.id = id;
        this.first_name = first_name;
        this.surname = surname;
        this.birthDate = birthDate;
        name = new SimpleStringProperty(first_name + " " + surname + " (" + birthDate + ")"); 
    }
    
    public String getName() 
    {
        return name.get();
    }
    
    public void setName(String fName) 
    {
        name.set(fName);
    }
    
    public ObservableList<Estate> ownedEstates() throws SQLException
    {
        DatabaseModel database = DatabaseModel.getInstance();
        Connection connection = database.getConnection();
        
        ObservableList<Estate> estates = FXCollections.observableArrayList();
        
        Estate estate = null;
        OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) connection.prepareStatement(
            "select * from estates where id = " + this.id
        );
        try 
        {
            OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery();
            try 
            {
                while (rset.next()) 
                {
                    int id = (int) rset.getInt("id");
                    String name = (String) rset.getString("name");
                    String description = (String) rset.getString("description");
                    Date valid_from = (Date) rset.getDate("valid_from");
                    Date valid_to = (Date) rset.getDate("valid_to");
                    estate = new Estate(id, name, description, null, valid_from, valid_to, null);
                    estates.add(estate);
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
        
        return estates;
    }
}
