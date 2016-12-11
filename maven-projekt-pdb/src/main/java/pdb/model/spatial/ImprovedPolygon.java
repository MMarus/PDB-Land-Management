/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.spatial;

import javafx.scene.shape.Polygon;

/** Class ImprovedPolygon represents polygon with extra reference to Entity by which it was created
 * @author jan
 */
public class ImprovedPolygon extends Polygon {
    private Entity entityReference;
    private Estate estateReference;
    private boolean isEstate;

    /**
     *
     */
    public int dbId;
    
    /**
     *
     * @param isEstate
     * @param entityReference
     * @param estateReference
     * @param dbId
     */
    public ImprovedPolygon(boolean isEstate, Entity entityReference, Estate estateReference, int dbId) {
        super();
        this.entityReference = entityReference;
        this.estateReference = estateReference;
        this.isEstate = isEstate;
        this.dbId = dbId;
    }
    
    /**
     *
     * @return
     */
    public Entity getEntityReference() {
        return this.entityReference;
    }
    
    /**
     *
     * @return
     */
    public Estate getEstateReference() {
        return this.estateReference;
    }
    
    /**
     *
     * @return
     */
    public boolean isEstate() {
        return isEstate;
    }
}
