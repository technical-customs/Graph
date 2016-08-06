package entities;

import gui.Screen;
import java.util.Iterator;

public class Player extends Entity{
    
    
    public Player(Screen screen){
        super(screen);
        
    }
    public Player(Screen screen, int x, int y, int width, int height) {
        super(screen, x, y, width, height);
    }
    
    @Override
    protected void entityCollision(){
        Iterator<Entity> entityIter = getEntities().iterator();
        
        while(entityIter.hasNext()){
            Entity entity = entityIter.next();
            
            if(getDeadEntities().contains(entity)){
                continue;
            }
            
            if(this.intersects(entity)){
                if(getXDir() > 0){
                    //means we're going right
                    this.x = entity.x - this.width;
                }
                if(getXDir() < 0){
                    //means we're going left
                    this.x = entity.x + entity.width;
                }
                if(getYDir() > 0){
                    //means we're going down
                    this.y = entity.y - this.height;
                }
                if(getYDir() < 0){
                    //means we're going up
                    this.y = entity.y + entity.height;
                }
            }
            
        }
    }
}