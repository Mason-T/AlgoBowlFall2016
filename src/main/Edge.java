package main;

public class Edge implements Comparable<Edge>{

	public Point p1, p2;
	
	public Edge( Point p1, Point p2 ) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public float distance() {
		return p1.distanceTo(p2);
	}

	@Override
	public int compareTo( Edge e ) {
		return Float.compare( distance(), e.distance() );
	}
	
	@Override
	public String toString() {
		return "Edge: {(" + p1 + "), (" + p2 + ")}";
	}
	
}
