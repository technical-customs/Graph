package kmean;

import entities.Bot;
import entities.Entity;
import entities.Player;
import gui.Screen;
import static gui.Screen.deadEntities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.shape.Line;
import javax.swing.SwingWorker;

public class Graph extends Screen{
    private int numberOfXLines = 10;
    private int numberOfYLines = 10;
    private final int lineLength = 10;
    private int xZero = SSIZE.width/2, yZero = SSIZE.height/2;
    private volatile int xScale = 1, yScale = 1;
    private volatile boolean drawGraphLines = false, labelLines = false, cutoffX = false, cutoffY = false;
   
    private volatile Map<Integer,Integer> xZeroPoint, yZeroPoint, xMapPoints, yMapPoints;
    
    private int defaultPointSize = lineLength;
    private final List<Entity> points;
    private final List<Entity> centroids;
    private final List<Cluster> clusters;
    private final List<Line2D> lines;
    
    private SwingWorker sw;
    
    public Graph(){
        super(new Dimension(650,650));
        points = new LinkedList();
        centroids = new LinkedList();
        clusters = new LinkedList();
        lines = new LinkedList();
    }
    
    public Graph(int scale){
        super(new Dimension(scale,scale));
        
        points = new LinkedList();
        centroids = new LinkedList();
        clusters = new LinkedList();
        lines = new LinkedList();
    }
    //Graph drawing and getters and setters
    private synchronized void drawGraph(Graphics2D g){
        //x = 0
        //mark axis
        g.setColor(Color.white);
        g.drawLine(0, yZero, SSIZE.width, yZero);
        g.drawLine(xZero, 0, xZero, SSIZE.height);
        
        g.setColor(Color.yellow);
        
        xZeroPoint.clear();
        yZeroPoint.clear();
        //mark 0,0
        g.drawString("0,0", xZero, yZero);
        xZeroPoint.put(0, xZero);
        yZeroPoint.put(0, yZero);
        
        //marks dashes with center of rect on the x,y
        //X-AXIS
        
        //INCREMENTING
        xMapPoints.clear();
        int xLineSpace = ( (xZero) - ( (xZero)/numberOfXLines ) ) /numberOfXLines;
        
        int xICount = 1*xScale;
        for(int lineCount = (xZero) + xLineSpace; lineCount < SSIZE.width - xLineSpace; lineCount += xLineSpace){ 
            g.drawLine(lineCount, (yZero) - (lineLength/2), lineCount, (yZero) + (lineLength/2));
            if(labelLines) g.drawString(xICount+"", lineCount, ( (yZero) + (lineLength) + (lineLength/2)));
            xMapPoints.put(xICount, lineCount);
            xICount+=xScale;
            if(cutoffX){
                if(xICount > numberOfXLines*xScale){
                    break;
                }
            }
        }
        //DECREMENTING
        int xDCount = -1*xScale;
        for(int lineCount = (xZero) - xLineSpace; lineCount > xLineSpace; lineCount -= xLineSpace){
            g.drawLine(lineCount, (yZero) - (lineLength/2), lineCount, (yZero) + (lineLength/2));
            if(labelLines) g.drawString(xDCount+"", lineCount, (yZero) + lineLength + (lineLength/2) );
            xMapPoints.put(xDCount, lineCount);
            xDCount-=xScale;
            if(cutoffX){
                if(xDCount < -numberOfXLines*xScale){
                    break;
                }
            }
        }
        //Y-AXIS
        //INCREMENTING
        yMapPoints.clear();
        int yLineSpace = ((yZero) - ( (yZero)/numberOfYLines)) /numberOfYLines;
        
        int yICount = 1*yScale;
        for(int lineCount = (yZero) - yLineSpace; lineCount > yLineSpace; lineCount -= yLineSpace){
            g.drawLine((xZero) - (lineLength/2), lineCount, (xZero) + (lineLength/2), lineCount);
            if(labelLines) g.drawString(yICount+"", ((xZero) + (lineLength/2) ), lineCount);
            yMapPoints.put(yICount, lineCount);
            yICount+=yScale;
            if(cutoffY){
                if(yICount > numberOfYLines*yScale){
                    break;
                }
            }
            
        }
        
        
        //DECREMENTING
        int yDCount = -1;
        for(int lineCount = (yZero) + yLineSpace; lineCount < SSIZE.height - yLineSpace; lineCount += yLineSpace){
            g.drawLine((xZero) - (lineLength/2), lineCount, (xZero) + (lineLength/2), lineCount);
            if(labelLines) g.drawString(yDCount+"", (xZero) + (lineLength/2), lineCount);
            yMapPoints.put(yDCount, lineCount);
            yDCount-=yScale;
            
            if(cutoffY){
                if(yDCount < -numberOfYLines*yScale){
                    break;
                }
            }
        }
    }
    public synchronized void drawGraphLines(int xZero, int yZero, int numberOfXLines, int numberOfYLines){
        if(xZero <= 0 || xZero > SSIZE.width){
            xZero = SSIZE.width/2;
        }
        if(yZero <= 0 || yZero > SSIZE.height){
            yZero = SSIZE.height/2;
        }
        this.xZero = xZero;
        this.yZero = yZero;
        
        if(numberOfXLines <= 0){
            numberOfXLines = 10;
        }
        this.numberOfXLines = numberOfXLines;
        
        if(numberOfYLines <= 0){
            numberOfYLines = 10;
        }
        this.numberOfYLines = numberOfYLines;
        
        xZeroPoint = new HashMap<>();
        yZeroPoint = new HashMap<>();
        
        xMapPoints = new HashMap<>();
        yMapPoints = new HashMap<>();
        
        drawGraphLines = true;  
    }
    public synchronized void removeGraphLines(){
        drawGraphLines = false;
    }
    public synchronized void labelLines(boolean set){
        labelLines = set;
    }
    public synchronized void setXScale(int xScale){
        if(xScale == 0){
            return;
        }
        this.xScale = xScale;
    }
    public synchronized void setYScale(int yScale){
        if(yScale == 0){
            return;
        }
        this.yScale = yScale;
    }
    public synchronized void cutoffX(boolean set){
        cutoffX = set;
    }
    public synchronized void cutoffY(boolean set){
        cutoffY = set;
    }
    //*********************END GRAPH DRAWING************************
    
    //GRAPH FEATURES
    
    public Map<Integer,Integer> getXMapPoints(){
        return xMapPoints;
    }
    public Map<Integer,Integer> getYMapPoints(){
        return yMapPoints;
    }
    public Point getPointOnGraph(int x, int y){
        if(x > numberOfXLines || y > numberOfYLines || x < -numberOfXLines || y < -numberOfYLines){
            return null;
        }
        if(xMapPoints == null){
            return null;
        }
        if(yMapPoints == null){
            return null;
        }
        int xLocation, yLocation;
        
        if(x == 0){
            xLocation = xZeroPoint.get(0);
        }else{
            xLocation = xMapPoints.get(x);
        }
        
        if(y == 0){
            yLocation = yZeroPoint.get(0);
        }
        else{
            yLocation = yMapPoints.get(y);
        }
        
        return new Point(xLocation,yLocation);
    }
    //draw line connecting two points
    
    public synchronized Entity addPoint(int x, int y){
        if(xMapPoints.isEmpty() || yMapPoints.isEmpty()){
            return null;
        }
        
        Point pc = getPointOnGraph(x,y);
        
        
        Bot e = new Bot(this,pc.x - (defaultPointSize/2) ,pc.y - (defaultPointSize/2) ,defaultPointSize,defaultPointSize);
        points.add(e);
        return e;
    }
    
    private synchronized void lineDraw(Graphics2D g){
        Iterator lineIter = lines.iterator();
        
        while(lineIter.hasNext()){
            Line2D entity = (Line2D) lineIter.next();
            Point2D p1 = entity.getP1();
            Point2D p2 = entity.getP2();
            
            g.setColor(Color.green);
            
            Point pp1 = getPointOnGraph((int) p1.getX(),(int)p1.getY());
            Point pp2 = getPointOnGraph((int)p2.getX() ,(int) p2.getY());
            
            g.drawLine(pp1.x,pp1.y,pp2.x,pp2.y);
        }
    }
    
    
    //points
    public void addPoint(Entity e){
        points.add(e);
    }
    public List<Entity> getPoints(){
        return points;
    }
    
    //cluster points
    public void addCluster(Cluster cluster){
        clusters.add(cluster);
    }
    public List getClusters(){
        return clusters;
    }
    
    
    public void addCentroid(Entity e){
       centroids.add(e);
    }
    public List<Entity> getCentroids(){
        return centroids;
    }
    public void moveCentroid(Entity centroid, Point point){
        centroid.setLocation(point);
    }
    
    public int[] convertIntegers(List<Integer> integers){
        int[] ret = new int[integers.size()];
        
        Iterator<Integer> iterator = integers.iterator();
        
        for (int i = 0; i < ret.length; i++){
            ret[i] = iterator.next();
        }
        return ret;
    }
    public Polygon groupPointsOfCluster(Cluster cluster){
        List<Integer> xPoints = new ArrayList<>();
        List<Integer> yPoints = new ArrayList<>();
        
        Iterator pi = cluster.getPoints().iterator();
        while(pi.hasNext()){
            Point p = (Point)pi.next();
            
            xPoints.add(p.x);
            yPoints.add(p.y);
        }
        
        return new Polygon(convertIntegers(xPoints), convertIntegers(yPoints), cluster.getPoints().size());
    }
    
    
    //draw line connecting two points
    
    private void connectPoints(Entity a, Entity b){
        
    }
    private synchronized void connectPoints(int xa, int ya, int xb, int yb){
        if(xa < -numberOfXLines || xa > numberOfXLines || xb < -numberOfXLines || xb > numberOfXLines){
            return;
        }
        if(ya < -numberOfYLines || ya > numberOfYLines || yb < -numberOfYLines || yb > numberOfYLines){
            return;
        }
        lines.add(new Line2D.Double(xa,ya,xb,yb));
    }
    
    private void clusterDraw(Graphics2D g){
        int cc = 0;
        Iterator ci = clusters.iterator();
        while(ci.hasNext()){
            Cluster cl = (Cluster) ci.next();
            
            Entity entity = (Entity) centroids.toArray()[cc];
            
            if(deadEntities.contains(entity)){
                continue;
            }
            entity.drawer(g);
            
            g.setColor(entity.getColor());
            g.drawPolygon(groupPointsOfCluster(cl));
            
            cc++;
        }
    }
    private void pointDraw(Graphics2D g){
        Iterator pointIter = points.iterator();
        while(pointIter.hasNext()){
            Entity entity = (Entity) pointIter.next();
            
            if(deadEntities.contains(entity)){
                continue;
            }
            entity.drawer(g);
        }
    }
    
    
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2= (Graphics2D)g;
        
        g2.setColor(Color.black);
        g2.fillRect(0,0,SSIZE.width,SSIZE.height);
        
        render(g2);
        
        repaint();
        g2.dispose();
        Toolkit.getDefaultToolkit().sync();
    }
    @Override
    public synchronized void render(Graphics2D g){
        //paint screen
        
        g.setColor(Color.black);
        g.fillRect(0, 0, SSIZE.width,SSIZE.height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        //game draws
        if(drawGraphLines){
            drawGraph(g);
            
        }
        lineDraw(g);
        pointDraw(g);
        clusterDraw(g);
        
    }
    public static void main(String[] args){
        int width = 10, height = 10;
        
        
        Graph graph = new Graph();
        graph.start();
        
        graph.setXScale(1);
        graph.setYScale(1);
        graph.labelLines(false);
        
        graph.cutoffX(false);
        graph.cutoffY(false);
        graph.drawGraphLines(0,0,10,10);
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }
        Entity p = graph.addPoint(5,5);
        //graph.connectPoints(0,0, 5,5);
        
        for(int xx = 0; xx < 3; xx++){
           
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
            Point np = graph.getPointOnGraph(1,0);
            p.setLocation(np.x,np.y);
        }
        
        
        /*
        KMeans kmeans = new KMeans(0, graph.SSIZE.height, 3);
        
        kmeans.randomizeCentroids();
        
        for(Cluster c: kmeans.getClusters()){
            Player player = (Player) graph.addPlayer(c.getCentroid().x,c.getCentroid().y,width,height);
            player.setRandColor();
            graph.addCentroid(player);
            graph.addCluster(c);
        }
        
        kmeans.randomizeTrainingSet(10,30);
        
        for(Point point: kmeans.getTrainingSet()){
            
            Bot bot = (Bot)graph.addBot(point.x,point.y,width,height);
            bot.setColor(Color.red);
            graph.addPoint(bot);
        }
        try {
            kmeans.listTrainingSet();
            kmeans.listClusters();
            kmeans.listCentroids();
            System.out.println();
            
            Thread.sleep(2000);
            
            System.out.println("Starting");
            for(int cc = 0; cc < 20; cc++){
                
                kmeans.clusterAssign();
                 Thread.sleep(3000);
                 
                //SwingUtilities.invokeLater(new Runnable(){
                    //@Override
                    //public void run(){
                        kmeans.moveCentroids();
                        for(int xx = 0; xx < kmeans.getClusters().length; xx++ ){
                            Entity e = (Entity) graph.getCentroids().get(xx);
                            e.setLocation(kmeans.getClusters()[xx].getCentroid());
                        }
                    //}
                //});
                Thread.sleep(3000);
            }
            Graph.log("Done");
            Thread.sleep(1000);
            
            Cluster tc = null;
            Entity ent = null;
            for(Object c: graph.getClusters()){
                Cluster cluster = (Cluster)c;
                
                if(tc == null || cluster.getPoints().size() > tc.getPoints().size()){
                    tc = cluster;
                }
            }
            
            for(Object c: graph.getClusters()){
                Cluster cl = (Cluster)c;
                
                if(!tc.equals(cl)){
                    
                    for(Entity cc: graph.getCentroids()){
                        if(cl.getCentroid().x == cc.x && cl.getCentroid().y == cc.y){
                            //graph.killEntity(cc);
                        }
                    }
                    
                    for(Point p: cl.getPoints()){
                        for(Entity pp: graph.getPoints()){
                            if(pp.x == p.x && pp.y == p.y){
                                //graph.killEntity(pp);
                            }
                        }
                    }
                }
            }
            
            
            Graph.log("Biggest Cluster is " + tc.getCentroid() + " with " + tc.getPoints().size() + " points");
            //find biggest cluster and 
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        */
    }
}