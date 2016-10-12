package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class SesameChicken {

	private static int numSets;
	private static ArrayList<Point> points;
	private static float currentMin = Float.MAX_VALUE;

	public static void main( String[] args ) throws Exception {
		int group = 3;
		readFile( group );
		readCurrentMax( group );
		buckshot( 40, 10, group );
	}
	
	// Finds the current maximum answer for this group's input, so we can overwrite it when we find something better 
	private static void readCurrentMax( int groupNumber ) {
		Scanner scanner;
		try {
			scanner = new Scanner( new File( "src/input/input_group" + groupNumber + "OUTPUTS" +".txt" ) );
		} catch ( Exception e ) {
			return;
		}
		currentMin = scanner.nextFloat();
		System.out.println( "Current best answer is " + currentMin );
		scanner.close();
	}
	
	
	public static void writeOutput(ArrayList<Cluster> best, int groupNumber){
		String filename = "src/input/input_group" + groupNumber + "OUTPUTS" +".txt";
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename, "UTF-8");
			writer.println(getOutputString(best));
			writer.close();
		} catch ( Exception e ) {
			System.out.println( "FAILED TO WRITE ANSWER" );
		}
	}
	
	public static ArrayList<Cluster> buckshot( int distinctSets, int minimizeEfforts, int groupNumber ) {
		
		ArrayList<ArrayList<Cluster>> answers = new ArrayList<ArrayList<Cluster>>();
		for( int i = 0; i < distinctSets; i++ ) {
			answers.add( Partitioner.randomPartition( points, numSets ) );
		}
	
		ArrayList<Cluster> best = null;

		for( int i = 0; i < minimizeEfforts; i++ ) {
			for( int j = 0; j < answers.size(); j++ ) {
				// minimize our answer with our two minimize functions
				Minimizer.resort( answers.get( j ) );
				Minimizer.minimize( answers.get( j ) );
				Minimizer.resort( answers.get( j ) );
				Minimizer.minimize2( answers.get( j ) );
				
				// log so you can see progress being made
				System.out.println( "index " + j + " got: " + evaluate( answers.get( j ) ) );
				
				// write answers if they're better than what already exists
				if( evaluate( answers.get( j ) ) < currentMin ) {
					System.out.println( "writing with score " + evaluate( answers.get( j ) ) );
					writeOutput( answers.get( j ), groupNumber );
					currentMin = evaluate( answers.get( j ) );
					best = answers.get( j );
				}
			}
		}
		
		return best;
	}
	
	public static int evaluate( ArrayList<Cluster> list ) {
		int max = 0;
		HashSet<Point> notSeen = new HashSet<Point>( points );
		for( Cluster c: list ) {
			max = Math.max( max, (int)c.maxDistance() );
			notSeen.removeAll( c.getPoints() );
		}
		
		// if we don't include all the points or have too many sets, return a huge value
		if( !notSeen.isEmpty() || list.size() > numSets )
			return Integer.MAX_VALUE;
		// otherwise act normal
		return max;
	}
	
	public static String getOutputString( ArrayList<Cluster> list ) {
		// Find the maximum distance;
		int max = evaluate( list );
		
		String s = Integer.toString( max ) + "\n";
		for( int i = 0; i < list.size(); i++ ) {
			s += list.get(i) + "\n";
		}
		
		return s;
	}
	
	public static void readFile( int groupNumber ) throws FileNotFoundException {
		Scanner scanner = new Scanner( new File( "src/input/input_group" + groupNumber + ".txt" ) );
		
		int numPoints = scanner.nextInt();
		numSets = scanner.nextInt();
		points = new ArrayList<Point>( numPoints );
		
		int x, y, z;
		for( int i = 0; i < numPoints && scanner.hasNextLine(); i++ ) {
			x = scanner.nextInt();
			y = scanner.nextInt();
			z = scanner.nextInt();
			points.add( new Point( x, y, z ) );
		}
		
		scanner.close();
	}
	
}
