package gui;

import entities.Bot;
import entities.Entity;
import entities.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class Screen extends JPanel implements ActionListener, Serializable{
    private static final long serialVersionUID = 1L; 
    private Image dbImage;
    private Graphics dbGraphics;
    
    //Entites
    private volatile boolean moveBots;
    public static List<Entity> entities;
    public static List<Entity> deadEntities;
    
    //SIZE
    public final int SWIDTH = 200;
    public final int SHEIGHT = SWIDTH / 16 * 9;
    public int SSCALE = 3;
    public Dimension SSIZE = new Dimension(SWIDTH * SSCALE, SHEIGHT * SSCALE);
    
    //GRAPHICS
    protected BufferStrategy bufferStrategy;
    
    //THREAD
    private volatile SwingWorker sw;
    private volatile Timer st;
    private volatile  boolean mainRunning;
    
    //FPS
    public static final int targetFPS = 30;
    
    public Screen(){
        entities = new LinkedList<>();
        deadEntities = new LinkedList<>();
        
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                setupGUI();
            }
        });
    }
    public Screen(Dimension size){
        entities = new LinkedList<>();
        deadEntities = new LinkedList<>();
        SSIZE = size;
        
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                setupGUI();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2= (Graphics2D) bufferStrategy.getDrawGraphics();
        
        g2.setColor(Color.black);
        g2.fillRect(0,0,SSIZE.width,SSIZE.height);
        
        //game draws
        render(g2);
        
        repaint();
        //g2.dispose();
        bufferStrategy.show();
        
        
        
        g2.dispose();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                if(mainRunning){
                    try{
                        tick();
                        repaint();
                    }catch(Exception ex){
                        
                    }
                }
                
            }
        });
        
    }
   
    private synchronized void setupGUI(){
        JFrame frame = new JFrame();
        
        setPreferredSize(SSIZE);
        setLayout(null);
        
        
        frame.setIgnoreRepaint(true);

        frame.getContentPane().add(this);
        frame.pack();
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent we){
                try{
                    stop();
                }finally{
                    System.exit(0);
                }
            }
        });
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        frame.createBufferStrategy(3);
        bufferStrategy = frame.getBufferStrategy();
        
    }
    
    public synchronized void start(){
        if(mainRunning){
            return;
        }
        
        mainRunning = true;
        
        st = new Timer(30,this);
        st.setRepeats(true);
        st.start();
        
        log("Started Thread");
    }
    public synchronized void stop(){
        if(!mainRunning){
            return;
        }
        if(!st.isRunning()){
            return;
        }
        stopBots();
        
        mainRunning = false;
        st.stop();
        log("Stopped Thread");
    }
    
    //Speed
    public synchronized void tick(){
        if(mainRunning){
            //game updates
            Iterator<Entity> entityIter = entities.iterator();
            while(entityIter.hasNext()){
                Entity entity = entityIter.next();

                if(deadEntities.contains(entity)){
                    continue;
                }
                entity.update();
                entity.setEntities(entities);
                entity.setDeadEntities(deadEntities);
            }
        }
    }
    public synchronized void render(Graphics2D g){
        //paint screen
        g.setColor(Color.black);
        g.fillRect(0, 0, SSIZE.width,SSIZE.height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        //game draws
        Iterator<Entity> entityIter = entities.iterator();
        while(entityIter.hasNext()){
            Entity entity = entityIter.next();
            
            if(deadEntities.contains(entity)){
                continue;
            }
            entity.drawer(g);
        }
        
        //revalidate();
        
        //Toolkit.getDefaultToolkit().sync();
    }
    
    //Entity
    public synchronized Entity addPlayer(){
        Entity entity = new Player(this);
        entities.add(entity);
        return entity;
    }
    public synchronized Entity addPlayer(int x, int y, int width, int height){
        Entity entity = new Player(this,x,y,width,height);
        entities.add(entity);
        return entity;
    }
    public synchronized Entity addBot(){
        Entity entity = new Bot(this);
        entities.add(entity);
        return entity;
    }
    public synchronized Entity addBot(int x, int y, int width, int height){
        Entity entity = new Bot(this,x,y,width,height);
        entities.add(entity);
        return entity;
    }
    
    private void startBots(){
        if(!mainRunning){
            return;
        }
        Iterator<Entity> entityIter = entities.iterator();
        while(entityIter.hasNext()){
            Entity entity = entityIter.next();
            
            if(deadEntities.contains(entity)){continue;}
            entity.pushRectX();
            entity.pushRectY();
        }
    }
    private void stopBots(){
        Iterator<Entity> entityIter = entities.iterator();
        while(entityIter.hasNext()){
            Entity entity = entityIter.next();
            
            if(deadEntities.contains(entity)){continue;}
            entity.stop();
        }
    }
    public synchronized void killEntity(Entity entity){
        deadEntities.add(entity);
        entities.remove(entity);
    }
    
    //GENERAL PURPOSE LOGGER
    public static void log(String string){
        System.out.println(string);
    }
    
    public static void main(String[] args) throws InterruptedException, InvocationTargetException{
        Screen screen = new Screen();
        SwingUtilities.invokeAndWait(new Runnable(){
            @Override
            public void run(){
                screen.start();
            }
        });
        
        Entity ent1 = screen.addBot();
        ent1.setColor(Color.CYAN);
        ent1.setRandSpeed();
        ent1.setBounds(0,0,20,20);
        ent1.pushRectX();
        ent1.pushRectY();
        
        Entity ent2 = screen.addBot();
        ent2.setRandColor();
        ent2.setRandSpeed();
        ent2.setBounds(150,80,20,20);
        ent2.pushRectX();
        ent2.pushRectY();
        
        Entity ent3 = screen.addBot();
        ent3.setRandColor();
        ent3.setRandSpeed();
        ent3.setBounds(20,50,20,20);
        ent3.pushRectX();
        ent3.pushRectY();
        
        Entity ent4 = screen.addBot();
        ent4.setRandColor();
        ent4.setRandSpeed();
        ent4.setBounds(100,50,20,20);
        ent4.pushRectX();
        ent4.pushRectY();
        
        Entity ent5 = screen.addBot();
        ent5.setRandColor();
        ent5.setSpeed(5);
        ent5.setBounds(300,80,20,20);
        ent5.pushRectX();
        ent5.pushRectY();
        
        Entity ent = null;
        while(!Screen.entities.isEmpty()){
            try{
                //screen.stopBots();
                Thread.sleep(1000);
                
                if(Screen.entities.size() == 1){
                    ent = (Entity)Screen.entities.toArray()[0];
                    break;
                }else{
                    int re = new Random().nextInt(Screen.entities.size() -1);
                
                    screen.killEntity((Entity)Screen.entities.toArray()[re]);
                }
            }catch(Exception ex){Screen.log("EX " + ex.getMessage());}
        }
        try{ 
            screen.stop();
            
            Thread.sleep(1000);
            
            screen.start();
            
            Thread.sleep(1000);
            
            ent.stepX(1, 150);
            ent.stepX(-1, 150);
            
        }catch(Exception ex){Screen.log("EX " + ex.getMessage());}
               
    }
}