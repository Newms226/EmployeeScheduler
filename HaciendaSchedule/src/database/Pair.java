package database;

public class Pair<P> {
	public final P source, destination;
	
	public Pair(P source, P destination) {
		this.source = source;
		this.destination = destination;
	}
}
