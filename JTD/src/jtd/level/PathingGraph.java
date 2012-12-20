/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import jtd.PointI;

/**
 *
 * @author LostMekka
 */
public final class PathingGraph {
	
	public static final float WALK_COST = 0.6f;
	public static final float WALK_DIAGONAL_COST = WALK_COST * (float)Math.sqrt(2f);
	private static final Random random = new Random();
	
	private static class Node{
		public PointI loc;
		public float weight;
		public Node(PointI loc, Node src, float weight) {
			this.loc = loc;
			this.weight = src.weight + weight;
		}
		public Node(PointI loc) {
			this.loc = loc;
			this.weight = 0;
		}
		@Override
		public String toString() {
			return "Node(" + loc.x + ", " + loc.y + ") = " + weight;
		}
	}
	public class PathingGraphIterator implements Iterator<PointI>{
		LinkedList<PointI> nextPoints;
		PointI lastPoint = null;
		public PathingGraphIterator(PointI start) {
			lastPoint = start;
			nextPoints = transitions.get(start.x).get(start.y);
		}
		@Override
		public boolean hasNext() {
			return !nextPoints.isEmpty();
		}
		@Override
		public PointI next() {
			if(nextPoints.isEmpty()) return null;
			lastPoint = nextPoints.get(random.nextInt(nextPoints.size()));
			nextPoints = transitions.get(lastPoint.x).get(lastPoint.y);
			return lastPoint;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
		public PointI getLastPoint() {
			return lastPoint;
		}
		public LinkedList<PointI> getNextPoints(){
			return nextPoints;
		}
		public float getDistanceLeft(){
			if(!hasNext()) return 0f;
			PathingGraphIterator iter = new PathingGraphIterator(lastPoint);
			float ans = 0f;
			while(iter.hasNext()){
				PointI p1 = iter.lastPoint;
				PointI p2 = iter.next();
				ans += p1.distanceTo(p2);
			}
			return ans;
		}
	}
	
	public LinkedList<PointI> startingPoints;
	public ArrayList<ArrayList<LinkedList<PointI>>> transitions;
	public long lastTime = 0;

	public PathingGraphIterator iterator(PointI start){
		return new PathingGraphIterator(start);
	}
	
	public PathingGraphIterator iterator(){
		return new PathingGraphIterator(startingPoints.get(random.nextInt(startingPoints.size())));
	}
	
	public PathingGraph(int mobSize, Level level) {
		transitions = new ArrayList<>(level.w);
		for(int x=0; x<level.w; x++){
			ArrayList<LinkedList<PointI>> l = new ArrayList<>(level.h);
			transitions.add(l);
			for(int y=0; y<level.h; y++){
				l.add(new LinkedList<PointI>());
			}
		}
		generate(mobSize, level);
	}
	
	private static final byte UNVISITED = 0;
	private static final byte VISITING = 1;
	private static final byte VISITED = 2;
	
	public long generate(int mobSize, Level level){
		long startTime = System.currentTimeMillis();
		LinkedList<PointI> newStartingPoints = new LinkedList<>();
		byte[][] visited = new byte[level.w][level.h];
		for(int x=0; x<level.w; x++){
			for(int y=0; y<level.h; y++){
				visited[x][y] = UNVISITED;
			}
		}
		LinkedList<Node> currNodes = new LinkedList<>();
		// init node list
		for(PointI p:level.destinations){
			currNodes.add(new Node(p));
			visited[p.x][p.y] = VISITED;
		}
		float startingPointCost = -1f;
		for(;;){
			// construct list with all nodes, that have equal, minimal weights
			LinkedList<Node> nodesToExpand = new LinkedList<>();
			float currWeigth = -1;
			ListIterator<Node> iter = currNodes.listIterator();
			while(iter.hasNext()){
				Node n = iter.next();
				if(nodesToExpand.isEmpty()){
					nodesToExpand.add(n);
					iter.remove();
					currWeigth = n.weight;
				} else {
					if(n.weight == currWeigth){
						nodesToExpand.add(n);
						iter.remove();
					} else {
						break;
					}
				}
			}
			// expand all nodes in that list
			LinkedList<Node> newNodes = new LinkedList<>();
			while(!nodesToExpand.isEmpty()){
				Node node = nodesToExpand.get(random.nextInt(nodesToExpand.size()));
				nodesToExpand.remove(node);
				int x = node.loc.x;
				int y = node.loc.y;
				LinkedList<PointI> reach = level.getWalkableTilesFrom(node.loc);
				for(PointI p:reach){
					if((visited[p.x][p.y] == VISITED) || !level.isWalkable(p)) continue;
					int hDist = p.hammingDistanceTo(node.loc);
					if((hDist > 2) || (hDist < 1)) continue;
					// add node to new nodes
					Node n;
					if(hDist == 1){
						n = new Node(p, node, level.getPathingWeightAt(p) + WALK_COST);
					} else {
						PointI tmp1 = new PointI(x, p.y);
						PointI tmp2 = new PointI(p.x, y);
						float w = (float)level.getPathingWeightAt(tmp1) + (float)level.getPathingWeightAt(tmp2);
						w *= 0.5f * (WALK_DIAGONAL_COST - WALK_COST);
						w += (float)level.getPathingWeightAt(p);
						n = new Node(p, node, w + WALK_DIAGONAL_COST);
					}
					if(!newNodes.contains(n)) newNodes.add(n);
					// add transition in graph
					if(visited[p.x][p.y] == UNVISITED){
						transitions.get(p.x).get(p.y).clear();
						visited[p.x][p.y] = VISITING;
					}
					LinkedList<PointI> l = transitions.get(p.x).get(p.y);
					if(!l.contains(node.loc)) l.add(node.loc);
					// add location to stating point list, if cost is not higher
					if(level.sources.contains(p)){
						if(newStartingPoints.isEmpty() || (n.weight <= startingPointCost)){
							newStartingPoints.add(p);
							startingPointCost = n.weight;
						}
					}
				}
			}
			// add new nodes to curr nodes, mark them as visited
			for(Node n:newNodes){
				visited[n.loc.x][n.loc.y] = VISITED;
				insertNode(n, currNodes);
			}
			if(currNodes.isEmpty()) break;
		}
		startingPoints = newStartingPoints;
		long endTime = System.currentTimeMillis();
		lastTime = endTime - startTime;
		return lastTime;
	}

	private void insertNode(Node node, LinkedList<Node> list){
		// if list is empty, insert node as the only element
		if(list.isEmpty()){
			list.add(node);
			return;
		}
		// traverse the list and search for the first node with same or equal weight
		ListIterator<Node> iter = list.listIterator();
		while(iter.hasNext()){
			Node next = iter.next();
			if(next.weight >= node.weight){
				// greater node found. insert node one step before this one
				iter.previous();
				iter.add(node);
				return;
			}
		}
		// no node has a greater or equal weight. insert this node at the end of the list
		list.addLast(node);
	}
	
}
