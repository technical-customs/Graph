package kmean;

import kmean.Cluster;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class KMeans {
    public static int min, max;
    
    private int K; // number of clusters to make
    private Cluster[] clusters; //list of clusters
    private Point[] trainingSet;
    private boolean doneClustering = false;
    
    public KMeans(int min, int max){
        if(max <= min) return;
        KMeans.min = min;
        KMeans.max = max;
        K = 2;
        initClusters();
    }
    public KMeans(int min, int max, int numberOfCluster){
        if(max <= min) return;
        KMeans.min = min;
        KMeans.max = max;
        K = numberOfCluster;
        initClusters();
    }
    public KMeans(int min, int max, Point[] trainingSet){
        if(max <= min) return;
        KMeans.min = min;
        KMeans.max = max;
        this.trainingSet = trainingSet;
        K = trainingSet.length;
        initClusters();
    }
    
    public boolean testClusters(Cluster c1, Cluster c2){
        if(c1.getPoints().equals(c2.getPoints())){
            return true;
        }
        return false;
    }
    
    public Cluster[] getClusters(){
        return clusters;
    }
    private void initClusters(){
        if(K <= 0){
            K = 2;
        }
        clusters = new Cluster[K];
        
        for(int x = 0; x < K; x++){
            clusters[x] = new Cluster();
        }
    }
    private synchronized void clearClusters(){
        for(Cluster cluster: clusters){
            cluster.clearPoints();
        }
    }
    
    public void randomizeCentroids(){
        for(Cluster cluster: clusters){
            cluster.randomizeCentroidLocation();
        }
    }
    
    public void setTrainingSet(Point[] trainingSet){
        this.trainingSet = trainingSet;
    }
    public Point[] getTrainingSet(){
        return trainingSet;
    }
    public void randomizeTrainingSet(int minRand, int maxRand){
        if(maxRand < minRand) return;
        trainingSet = new Point[new Random().nextInt(maxRand-minRand) + minRand];
        
        for(int x = 0; x < trainingSet.length; x++){
            trainingSet[x] = new Point(new Random().nextInt(max-min) + min,new Random().nextInt(max-min) + min);
        }
    }
    
    public synchronized void clusterAssign(){
        if(trainingSet.length == 0 || trainingSet == null){
            return;
        }
        //Create a lis
        List clusterz = new ArrayList();
        
        for(Cluster c: clusters){
            clusterz.add(c);
        }
        
        clearClusters();
        
        for(int i = 0; i < trainingSet.length; i++){
            double dist = Double.MAX_VALUE;
            
            Cluster c = null;
            
            //find which cluster point is closer to and add to cluster
            for(Cluster cluster: clusters){
                //if the distance between this and centroid is less than prev distance, distance = this-centroid
                if(getDistance(trainingSet[i], cluster.getCentroid()) < dist){
                    dist = getDistance(trainingSet[i], cluster.getCentroid());
                    c = cluster;
                }
            }
            //at the end, need to decide which centroid is closest
            if(c != null){
                c.addPoint(trainingSet[i]);
            }
        }
    }
    public synchronized void moveCentroids(){
        for(Cluster cluster: clusters){
            cluster.setCentroidLocation(cluster.getAverageOfCluster());
        }
    }
    
    public double getDistance(Point pa,Point pb){
        return Point.distance(pa.x, pa.y, pb.x, pb.y);
    }
    
    public void listTrainingSet(){
        if(trainingSet == null){
            return;
        }
        
        int sc= 0;
        System.out.print("Training Set: ");
        
        for(Point tp: trainingSet){
            System.out.print("Point " + ++sc + "(" + tp.x + ", " + tp.y + ") ");
        }
        
        System.out.println();
    }
    public void listCentroids(){
        if(clusters == null){
            return;
        }
        int cc = 0;
        for(Cluster cluster: clusters){
            System.out.println("Centroid " + ++cc + ": (" + cluster.getCentroid().x + ", " + cluster.getCentroid().y + ")");
        }
    }
    public void listClusters(){
        
        int clusterCount = 0;
        for(Cluster cluster: clusters){
            clusterCount++;
            System.out.print("Cluster " + clusterCount + ": ");
            cluster.listCluster();
        }
        System.out.println();
    }
    
    public static void main(String[] args){
        KMeans kmeans = new KMeans(-5,5);
        
        try{
            kmeans.listClusters();
            kmeans.listCentroids();
            System.out.println();
            
            Thread.sleep(2000);
            
            kmeans.randomizeCentroids();
            System.out.println();
            
            Thread.sleep(2000);
            
            kmeans.listClusters();
            kmeans.listCentroids();
            System.out.println();
            
            Thread.sleep(2000);

            kmeans.randomizeTrainingSet(10,20);
            kmeans.listTrainingSet();
            System.out.println();

            Thread.sleep(2000);
            
            kmeans.listClusters();
            kmeans.listCentroids();
            System.out.println();

            Thread.sleep(2000);
            
            while(true){
                kmeans.clusterAssign();

                Thread.sleep(2000);

                kmeans.listCentroids();
                kmeans.listClusters();
                System.out.println();

                Thread.sleep(2000);

                kmeans.moveCentroids();

                Thread.sleep(2000);

                kmeans.listCentroids();
                kmeans.listClusters();
                System.out.println();
                
                Thread.sleep(2000);
            }
        }catch(Exception ex){
            
        }
        
        
        
        
        
    }
}