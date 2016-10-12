package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Cluster {

	private ArrayList<PriorityQueue<Point>> boundaries;		// Represents the objects in order of their distance in the 8 quadrants
	private ArrayList<HashSet<Point>> unqueuedPoints;		// Holds all of our points that we haven't yet put into our queues
	private ArrayList<Point> quadMaxes;						// Holds the maximum points in the 8 quadrants
	protected HashSet<Point> points;						// All of the points in this cluster
	
	public Cluster() {
		setup();
	}

	public Cluster( Cluster c ) {
		setup();
		addAll( c.getPoints() );
	}
	
	public Cluster( Collection<Point> points ) {
		setup();
		addAll( points );
	}
	
	public void setup() {
		points = new HashSet<Point>();
		
		boundaries = new ArrayList<PriorityQueue<Point>>();
		unqueuedPoints = new ArrayList<HashSet<Point>>();
		quadMaxes = new ArrayList<Point>();
		
		// Set up the variables needed to keep track of the boundaries in the 8 quadrants
		for( int i = 0; i < 8; i++ ) {
			boundaries.add( new PriorityQueue<Point>( 1000, Collections.reverseOrder( Quadrants.quadCompare.get( i ) ) ) );
			unqueuedPoints.add( new HashSet<Point>() );
			quadMaxes.add( null );
		}
	}
	
	// Adds a point p to this cluster. Operates in O(1)
	public void add( Point p ) {
		if( points.contains( p ) ) return;
		points.add( p );
		
		for( int i = 0; i < 8; i++ ) {
			// If this is our outermost point in this cluster for a certain quadrant, save it.
			if( quadMaxes.get( i ) == null || Quadrants.quadCompare.get( i ).compare( p, quadMaxes.get( i ) ) == 1 ) {
				quadMaxes.set( i, p );
			}
			// Add our point to a hash to be queued later, so this function has constant time
			unqueuedPoints.get( i ).add( p );
		}
	}
	
	public void addAll( Collection<Point> points ) {
		for( Point p: points ) {
			add( p );
		}
	}
	
	// Removes point p from this cluster.
	public void remove( Point p ) {
		if( !points.contains( p ) ) return;
		points.remove( p );
		
		for( int i = 0; i < 8; i++ ) {
			// If p hadn't been put into this priority queue yet, remove it from the set of unqueued points O(1)
			if ( !unqueuedPoints.get( i ).remove( p ) ) {
				// Otherwise remove it from the priority queue O(log n)
				boundaries.get( i ).remove( p );
			}
			
			// If we're removing a point we had saved as a max in a quadrant, we must find the new max
			if( quadMaxes.get( i ) == p ) {
				// Add all of the unqueued points to the queue. Here we make up for work skipped in our add function
				boundaries.get( i ).addAll( unqueuedPoints.get( i ) );
				unqueuedPoints.get( i ).clear();
				// Look at the first element
				quadMaxes.set( i, boundaries.get( i ).peek() );
			}
		}
	}
	
	public void removeAll( Collection<Point> points ) {
		for( Point p: points ) {
			remove( p );
		}
	}
	
	public Point getBoundary( int quadrant ) {
		if( quadrant > 7 || quadrant < 0 )
			throw new IllegalArgumentException( "Quadrant argument must be between 0 and 7 " );
		return quadMaxes.get( quadrant );
	}
	
	// Constant time function to determine the cluster's distance to a point
	public float distanceTo( Point p ) {
		if( points.size() == 0 ) {
			return 0;
		}

		// The maximum distance must be from one of the outliers in the 8 quadrants, so just check those
		float max = 0;
		for( int i = 0; i < 8; i++ ) {
			max = Math.max( max, p.distanceTo( quadMaxes.get( i ) ) );
		}
		return max;
	}
	
	public Edge maxEdge() {
		// This function operates off of the assumption that the farthest 2 points in a Cluster
		// must have the most extreme values in opposite quadrants
		if( points.isEmpty() ) return null;
		
		Edge max = null;
		for( int i = 0; i < 4; i++ ) {
			Edge e = new Edge( quadMaxes.get( i ), quadMaxes.get( Quadrants.pair[i] ) );
			if( max == null || max.distance() < e.distance() ) {
				max = e;
			}
		}
		return max;
	}
	
	public float maxDistance() {
		if( points.size() > 0 ) {
			return maxEdge().distance();
		}
		return 0;
	}

	public HashSet<Point> getPoints() {
		return points;
	}

	// By manually clearing sets and queues, this function operates closer to O(1)
	public void clear() {
		points.clear();
		
		for( int i = 0; i < 8; i++ ) {
			boundaries.get( i ).clear();
			unqueuedPoints.get( i ).clear();
			quadMaxes.set( i, null );
		}
	}
	
	@Override
	public String toString() {
		
		// Put the points into an array and sort them by index, so that they're not in random order
		Point[] array = new Point[points.size()];
		points.toArray( array );
		ArrayList<Point> list = new ArrayList<Point>(Arrays.asList( array ) );
		Collections.sort( list );
		
		String s = "";
		
		for( Point p: list ) {
			s += Integer.toString( p.idx + 1 ) + " ";
		}
		
		return s;
	}
	
}