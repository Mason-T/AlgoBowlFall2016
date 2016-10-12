package main;

import java.util.ArrayList;
import java.util.Comparator;

public class Quadrants implements Comparator<Point>{
	// This class provides 8 comparators for determining the relationship between points in regards to the 8 quadrants
	
	// Directions that point directly out in all 8 quadrants
	private final static int[][] directions = new int[][]{{ 1,  1,  1}, {-1,  1,  1}, {-1, -1,  1}, { 1, -1,  1}, { 1,  1, -1}, {-1,  1, -1}, {-1, -1, -1}, { 1, -1, -1}};
	// Stores the quadrant opposite to the one designated by the index (0 indexed)
	public static final int[] pair = { 6, 7, 4, 5, 2, 3, 0, 1 };
	// The Arraylist of comparators
	public static final ArrayList<Quadrants> quadCompare = setup();
	
	private int quadrant;

	private static ArrayList<Quadrants> setup() {
		ArrayList<Quadrants> comparators = new ArrayList<Quadrants>();
		for( int i = 0; i < 8; i++ ) {
			comparators.add( new Quadrants( i ) );
		}
		return comparators;
	}
	
	private Quadrants( int quadrant ) {
		this.quadrant = quadrant;
	}
	
	// Returns the distance between two points in regards to a certain quadrant
	public float distance( Point p1, Point p2 ) {
		float dist1 = p1.x*directions[quadrant][0] + p1.y*directions[quadrant][1] + p1.z*directions[quadrant][2];
		float dist2 = p2.x*directions[quadrant][0] + p2.y*directions[quadrant][1] + p2.z*directions[quadrant][2];
		return dist1 - dist2;
	}

	@Override
	public int compare( Point p1, Point p2 ) {
		int temp = quadrant;
		// If two points are equal in a quadrant, rotate the quadrant we're comparing in and try again.
		while( quadCompare.get( temp ).distance( p1, p2 ) == 0 ) {
			temp = ( temp + 1 ) % 8;
			if ( temp == quadrant ) break; // break after 8 tries
		}
		return Float.compare( quadCompare.get( temp ).distance( p1, p2 ), 0 );
	}
}
