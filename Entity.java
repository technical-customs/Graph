package entities;

import gui.Screen;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.util.List;
import java.util.Random;

public abstract class Entity extends Rectangle{
    private volatile List<Entity> entities;
    private volatile List<Entity> deadEntities;
    private volatile boolean collided = false;
    private volatile Color color = Color.yellow;
    private volatile int xDir = 0, yDir = 0, speed = 1;
    
    private final Screen screen;
    
    public Entity(Screen screen){
        super();
        this.screen = screen;
    }
    public Entity(Screen screen, int x, int y, int width, int height){
        super(x,y,width,height);
        this.screen = screen;
    }
    
    //Entities
    public synchronized void setEntities(List<Entity> entities){
        this.entities = entities;
    }
    public synchronized List<Entity> getEntities(){
        return entities;
    }
    public synchronized void setDeadEntities(List<Entity> deadEntities){
        this.deadEntities = deadEntities;
    }
    public synchronized List<Entity> getDeadEntities(){
        return deadEntities;
    }
    private synchronized void syncEntities(){
        entities.addAll(Screen.entities);
        entities.addAll(Screen.entities);
    }
    
    //Color
    public void setRandColor(){
        color = new Color(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255));
    }
    public void setColor(Color color){
        this.color = color;
    }
    public Color getColor(){
        return color;
    }
    
    //Speed
    public void setRandSpeed(){
        this.speed = new Random().nextInt(4)+1;
    }
    public void setSpeed(int speed){
        this.speed = speed;
    }
    public int getSpeed(){
        return this.speed;
    }
    
    //Directions
    public void setXDir(int xDir){
        if(xDir != 0){
            this.xDir = xDir/Math.abs(xDir);
        }else if(xDir == 0){
            this.xDir = 0;
        }
        
    }
    public int getXDir(){
        return xDir;
    }
    public void setYDir(int yDir){
        if(yDir != 0){
            this.yDir = yDir/Math.abs(yDir);
        }else if(yDir == 0){
            this.yDir = 0;
        }
    }
    public int getYDir(){
        return yDir;
    }
    
    //Movement
    public void stepX(int dir, int steps){
        try{
            double xPos = this.x;
            //for(int stepCount = 0; stepCount < steps; stepCount++){
                if(dir != 0){
                    if(dir > 0){
                        while(this.x < xPos+steps){
                            this.x += 1* speed;
                            Thread.sleep(30);
                        }
                        //this.xDir = 1;
                    }
                    if(dir < 0){
                        while(this.x > xPos-steps){
                            this.x -= 1* speed;
                            Thread.sleep(30);
                        }
                        //this.xDir = -1;
                    }
                    Thread.sleep(30);
                }
            //}
            
            
        }catch(Exception ex){
            System.out.println("StepX ex: " + ex);
        }
        
        
    }
    public void stepY(int dir, int steps){
        try{
            double yPos = this.y;
            
            for(int stepCount = 0; stepCount < steps; stepCount++){
                if(dir != 0){
                    if(dir > 0){
                        while(this.x < yPos+steps){
                            this.y += 1* speed;
                            Thread.sleep(30);
                        }
                    }
                    if(dir < 0){
                        while(this.x < yPos+steps){
                            this.y -= 1* speed;
                            Thread.sleep(30);
                        }
                    }
                    Thread.sleep(30);
                }
            }
            
        }catch(Exception ex){
            System.out.println("StepX ex: " + ex);
        }
        
        if(dir != 0){
            if(dir > 0){
                this.y += 1 * speed;
            }
            if(dir < 0){
                this.y -= 1 * speed;
            }
        }
    }
    public void pushRectX(){
        do{
            xDir = new Random().nextInt(3)-1;
        }while(xDir == 0);
    } //Randomizes xDir
    public void pushRectY(){
        do{
            yDir = new Random().nextInt(3)-1;
        }while(yDir == 0);
    } //Randomizes yDir
    
    private void move(){
        this.x += xDir * speed;
        this.y += yDir * speed;
    }
    public void stop(){
        xDir = 0;
        yDir = 0;
    }
    
    //Collisions
    private void boundCollision(){
        //log("Collision");
        if(this.x <= 0){
            this.x = 0;
            this.setXDir(-xDir);

        }
        if( (this.x+this.width) >= screen.SSIZE.width){
            this.x = screen.SSIZE.width - this.width;
            this.setXDir(-xDir);

        }
        if(this.y <= 0){
            this.y = 0;
            this.setYDir(-yDir);
        }
        if( (this.y+this.height) >= screen.SSIZE.height){
            this.y = screen.SSIZE.height - this.height;
            this.setYDir(-yDir);
        }
                
    }
    protected abstract void entityCollision();
    
    //Draw and Updaate
    public void update(){
        entities = Screen.entities;
        deadEntities = Screen.deadEntities;
        
        this.move();
        this.boundCollision();
        this.entityCollision();
    }
    public void drawer(Graphics2D graphics){
        if(graphics == null){
            return;
        }
        
        graphics.setColor(color);
        graphics.fillRect(this.x, this.y, this.width, this.height);
        
        //graphics.drawArc(this.x - (this.width/2), this.y - this.height, this.width, this.height * 2, 0, 180);
    }
}