package entities;

import gui.Screen;
import java.util.Iterator;

public class Bot extends Entity{
    
    public Bot(Screen screen){
        super(screen);
    }

    public Bot(Screen screen, int x, int y, int width, int height) {
        super(screen, x, y, width, height);
    }

    @Override
    protected void entityCollision(){ //bounce back for bot
        Iterator<Entity> entityIter = getEntities().iterator();
        
        while(entityIter.hasNext()){
            Entity entity = entityIter.next();
            
            if(getDeadEntities().contains(entity)){
                continue;
            }
            
            if(!this.equals(entity) && this.intersects(entity)){
                int xWay = this.getXDir();
                int yWay = this.getYDir();
                
                if(this.getXDir() > 0){
                    if(this.intersectsLine(entity.x,entity.y, entity.x,entity.y+entity.height)){
                        this.setXDir(-1);
                    }
                }
                if(this.getXDir() < 0){
                    if(this.intersectsLine(entity.x+entity.width,entity.y, entity.x+entity.width,entity.y+entity.height)){
                        this.setXDir(1);
                    }
                }
                if(this.getYDir() > 0){
                    if(this.intersectsLine(entity.x,entity.y, entity.x+entity.width,entity.y)){
                        this.setYDir(-1);
                    }
                }
                if(this.getYDir() < 0){
                    if(this.intersectsLine(entity.x,entity.y+entity.height, entity.x+entity.width,entity.y+entity.height)){
                        this.setYDir(1);
                    }
                }
            }
            
        }
    }
}