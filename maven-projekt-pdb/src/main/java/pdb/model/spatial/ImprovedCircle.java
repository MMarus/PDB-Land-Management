/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.spatial;

import javafx.scene.shape.Circle;

/** Class ImprovedCircle represents circles with extra reference to Entity by which it was created
 * @author jan
 */
public class ImprovedCircle extends Circle {
    private Entity entityReference;

    /**
     * id which has entity represented by circle in db
     */
    public int dbId;
    
    /**
     * Constructor initialize internal attributes
     * @param entityReference entity from which origin circle
     * @param dbId id which has entity represented by circle in db
     */
    public ImprovedCircle(Entity entityReference, int dbId) {
        super();
        this.dbId = dbId;
        this.entityReference = entityReference;
    }
    
    /**
     * return entity from which origin circle
     * @return entity from which origin circle
     */
    public Entity getEntityReference() {
        return this.entityReference;
    }
}
