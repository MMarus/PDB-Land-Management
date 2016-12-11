/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdb.model.spatial;

import javafx.scene.shape.Path;
/** Class ImprovedPath represents paths with extra reference to Entity by which it was created
 * @author jan
 */
public class ImprovedPath extends Path {
    private Entity entityReference;

    /**
     *
     */
    public int dbId;
    
    /**
     *
     * @param entityReference
     * @param dbId
     */
    public ImprovedPath(Entity entityReference, int dbId) {
        super();
        this.dbId = dbId;
        this.entityReference = entityReference;
    }
    
    /**
     *
     * @return
     */
    public Entity getEntityReference() {
        return this.entityReference;
    }
}
