package main;

public class Point implements Comparable<Point>{

	private static int index = 0;

	public float x, y, z;
	public int idx;

	public Point( float x, float y, float z ) {
		this.x = x;
		this.y = y;
		this.z = z;
		idx = index++;
	}

	public float distanceTo( Point p ) {
		return Math.abs( x - p.x ) + Math.abs( y - p.y ) + Math.abs( z - p.z );
	}

	@Override
	public int compareTo(Point p) {
		return Integer.compare( idx, p.idx );
	}
	
	@Override
	public String toString() {
		return new String( ( idx + 1 ) + ": " + x + " " + y + " " + z );
	}

}
