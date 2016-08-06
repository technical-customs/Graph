package kmean;


import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cluster{
    private int min = KMeans.min;
    private int max = KMeans.max;
    
    private Point centroid;
    private List<Point> points;
    
    public Cluster(){
        centroid = new Point();
        points = new ArrayList<>();
    }
    
    public synchronized Point getCentroid(){
        return centroid;
    }
    public synchronized void setCentroidLocation(Point point){
        centroid.setLocation(point);
    }
    public synchronized void setCentroidLocation(int x, int y){
        centroid.setLocation(x,y);
    }
    public synchronized void randomizeCentroidLocation(){
        setCentroidLocation(new Random().nextInt(max-min) + min, new Random().nextInt(max-min) + min);
    }
    
    public synchronized void addPoint(Point point){
        points.add(point);
    }
    public synchronized List<Point> getPoints(){
        return points;
    }
    public synchronized void clearPoints(){
        points.clear();
    }
    public synchronized Point getAverageOfCluster(){
        double xAv ,xTotal = 0, yAv, yTotal = 0;
        
        for(Point cc: points){
            xTotal += cc.getX();
            yTotal += cc.getY();
        }
        
        xAv = xTotal / points.size();
        yAv = yTotal / points.size();
        
        return new Point((int)xAv,(int)yAv);
    }
    
    public synchronized void listCluster(){
        int pointCount = 0;
        for(Point point: points){
            pointCount++;
            System.out.print("Point-(" + point.x + ", " + point.y + ") ");
        }
        System.out.println();
    }
}