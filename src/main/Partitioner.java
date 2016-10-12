package main;

import java.util.ArrayList;
import java.util.Random;

public class Partitioner {

	// Randomly partitions the points into a number of clusters designated by numSets
	public static ArrayList<Cluster> randomPartition( ArrayList<Point> points, int numSets ) {
		ArrayList<Cluster> clusterSet = new ArrayList<Cluster>();
		Random r = new Random();
		
		for( int i = 0; i < numSets; i++ ) {
			clusterSet.add( new Cluster() );
		}
		
		for( Point p: points ) {
			clusterSet.get( r.nextInt( numSets ) ).add( p );
		}
		
		return clusterSet;
	}
	
}
