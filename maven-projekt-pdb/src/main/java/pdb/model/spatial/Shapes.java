/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.spatial;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;

/** 
 * Class Shapes represents lists of ImprovedCircles, ImprovedPaths, ImprovedPolygons
 * @author mmarus
 */
public class Shapes {

    /**
     * List of circles
     */
    public List<ImprovedCircle> circles;

    /**
     * List of paths
     */
    public List<ImprovedPath> paths;

    /**
     * List of polygons
     */
    public List<ImprovedPolygon> polygons;
    
    /**
     * Constructor initialize internal attributes
     */
    public Shapes() {
        this.circles = new ArrayList<>();
        this.paths = new ArrayList<>();
        this.polygons = new ArrayList<>();
    }
    
    /**
     * adds new path
     * @param entityReference entity to which add new path
     * @param dbId id of entity in db
     */
    public void addNewPath(Entity entityReference, int dbId) {
        paths.add(new ImprovedPath(entityReference, dbId));
    }
    
    /**
     * add Element To Last Path from the list of paths
     * @param x x coordinate of point added to path
     * @param y y coordinate of point added to path
     */
    public void addElementToLastPath(double x, double y) {
        if (paths.isEmpty())
            return;
        PathElement newElement = null;
        ImprovedPath lastPath = getLastPath();
        if( lastPath.getElements().size() < 1) {
            newElement = new MoveTo(x,y);
        } else {
            newElement = new LineTo(x,y);
        }
        lastPath.getElements().add(newElement);
    }
    
    /**
     * get Last Path from the list of paths
     * @return ImprovedPath
     */
    public ImprovedPath getLastPath(){
        if (paths.isEmpty())
            return null;
        return paths.get(paths.size()-1);
    }
    
}
