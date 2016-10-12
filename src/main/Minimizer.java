package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

public class Minimizer{
	
	private static Random r = new Random();

	// Comparator that returns the smaller of two clusters
	private static Comparator<Cluster> pickSmaller = new Comparator<Cluster>() {
		@Override
		public int compare( Cluster c1, Cluster c2 ) {
			return Float.compare( c1.maxDistance(), c2.maxDistance() );
		}
	};
	
	// Tries to break up the biggest edges on the largest clusters
	public static void minimize( Collection<Cluster> collection ) {
		
		// Create a queue that stores our clusters in descending total size
		PriorityQueue<Cluster> queue = new PriorityQueue<Cluster>( Collections.reverseOrder( pickSmaller ) );
		
		boolean madeChange = true;
		for( int i = 0; i < 10000 && madeChange; i++ ) {
			madeChange = false;

			queue.clear();
			queue.addAll( collection );
			
			// Walk through our queue, trying to break up the biggest edges.
			// Break out of this loop once we make a change.
			while( !queue.isEmpty() ) {
				Cluster current = queue.poll(); 										// The cluster that's orphaning out points
				float currentDistance = current.maxDistance();							// The current distance across this cluster
				if( currentDistance == 0 ) continue;									// If our cluster has no size, then orphaning won't help
				Edge bigEdge = current.maxEdge();										// Largest edge found in the current cluster
				ArrayList<Cluster> potentialAdopters = new ArrayList<Cluster>();		// clusters that could take the new point
				
				for( Cluster c: collection ) {
					float distance = Math.min( c.distanceTo( bigEdge.p1 ), c.distanceTo( bigEdge.p2 ) );
					// If we find a cluster that is closer to this point, save them as a potential adopter
					if( distance < currentDistance ) {
						potentialAdopters.clear();
						potentialAdopters.add( c );
						currentDistance = distance;
					} else if( distance == currentDistance && c != current ) {
						potentialAdopters.add( c );
					}
				}
				
				// If there are clusters that could take the point, then give it to one at random
				if( potentialAdopters.size() > 0 ) {
					Cluster adopter = potentialAdopters.get( r.nextInt( potentialAdopters.size() ) );
					
					// Only take the closer point
					Point p = bigEdge.p1;
					if( adopter.distanceTo( bigEdge.p2 ) < adopter.distanceTo( bigEdge.p1 ) ) {
						p = bigEdge.p2;
					}
					
					current.remove( p );
					adopter.add( p );
					madeChange = true;
					break;
				}
			}
		}
	}

	// Makes the smallest clusters adopt points from larger clusters while it doesn't make the solution worse
	public static void minimize2( ArrayList<Cluster> clusters ) {
		
		PriorityQueue<Cluster> queue = new PriorityQueue<Cluster>( pickSmaller );
		PriorityQueue<Cluster> runnerqueue = new PriorityQueue<Cluster>( pickSmaller );
		
		boolean madeChange = true;
		for( int i = 0; i < 200 && madeChange; i++ ) {
			madeChange = false;

			queue.addAll( clusters );
			HashSet<Cluster> largerClusters = new HashSet<Cluster>( queue );
			
			// Determine the maximum distance found in our clusters
			float maxAcross = SesameChicken.evaluate( clusters );
			
			while( !queue.isEmpty() ) {
				Cluster small = queue.poll();
				largerClusters.remove( small );
				runnerqueue.addAll( largerClusters );
				
				while( !runnerqueue.isEmpty() ) {
					Cluster large = runnerqueue.poll();
					
					// Sort the points from our large cluster by their distance from that cluster 
					Comparator<Point> farthestFromLargest = new Comparator<Point>() {
						@Override
						public int compare(Point p1, Point p2) {
							// If one point is farther from large, choose that first
							int compareLarge = Float.compare( large.distanceTo( p2 ), large.distanceTo( p1 ) );
							if( compareLarge != 0 ) return compareLarge;
							// Otherwise return the point that is closest to small
							return Float.compare( small.distanceTo( p1 ), small.distanceTo( p2 ) );
						}
					};
					
					ArrayList<Point> largePoints = new ArrayList<Point>( large.getPoints() );
					Collections.sort( largePoints, farthestFromLargest );
					
					for( Point p: largePoints ) {
						// Add this point to our smaller cluster if it is closer to it than our solution distance
						if( maxAcross > Math.max( small.maxDistance(), small.distanceTo( p ) ) ) {
							large.remove( p );
							small.add( p );
							madeChange = true;
						}
					}
				}
			}
		}
	}
	
	// Adds points from our smallest clusters to the larger clusters whose maximum size won't be affected by them
	public static ArrayList<Cluster> resort( ArrayList<Cluster> clusters ) {
		
		// Sort our cluster in ascending size
		Collections.sort( clusters, pickSmaller );
		
		for( int i = 0; i < clusters.size(); i++ ) {
			Cluster small = clusters.get( i );
			
			// Try to pawn points off to the largest clusters first
			for( int j = clusters.size() - 1; j > i; j-- ) {
				Cluster big = clusters.get( j );
				
				// take the points farthest from our small cluster first
				ArrayList<Point> smallPoints = new ArrayList<Point>( small.getPoints() );
				Collections.sort( smallPoints, new Comparator<Point>() {
					@Override
					public int compare(Point p1, Point p2) {
						return Float.compare( small.distanceTo( p2 ), small.distanceTo( p1 ) );
					}
				});
				
				// Add all the points to the larger cluster if it doesn't make it bigger
				for( Point p: smallPoints ) {
					if( big.distanceTo( p ) <= big.maxDistance() ) {
						small.remove( p );
						big.add( p );
					}
				}
			}
			
		}
		
		return clusters;
	}
	
}