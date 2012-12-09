/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.level;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import jtd.PointF;
import jtd.PointI;

/**
 *
 * @author LostMekka
 */
public class Path {
	
	public static final float WALK_COST = 0.6f;
	
	private static final Random random = new Random();
	
	private static class Node implements Comparable<Node> {
		public PointI loc;
		public Node src;
		public float weight;
		public Node(PointI loc, Node src, float weight) {
			this.loc = loc;
			this.src = src;
			this.weight = src.weight + weight;
		}
		public Node(PointI loc) {
			this.loc = loc;
			this.src = null;
			this.weight = 0;
		}
		@Override
		public int compareTo(Node o) {
			if((loc.x == o.loc.x) && (loc.y == o.loc.y)) return 0;
			if(weight <= o.weight) return -1;
			return 1;
		}
		@Override
		public String toString() {
			return "Node(" + loc.x + ", " + loc.y + ") = " + weight;
		}
	}
	
	public class PathIterator implements Iterator<PointF>{
		private List<PointI> list;
		private Iterator<PointI> iter;
		private int index = 0;
		private PointI currentPointI = null;
		private float randomComponent;
		public PathIterator(List<PointI> list, float randomComponent) {
			this.list = list;
			this.randomComponent = randomComponent;
			iter = list.iterator();
		}
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}
		@Override
		public PointF next() {
			if(iter.hasNext()){
				index++;
				currentPointI = iter.next();
				return currentPointI.getPointF(randomComponent);
			} else {
				return null;
			}
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		public float getDistanceLeft(){
			Iterator<PointI> iter2 = list.iterator();
			PointI p = null;
			while((p != currentPointI) && iter2.hasNext()) p = iter2.next();
			float dist = 0;
			while(iter2.hasNext()){
				PointI p2 = iter2.next();
				dist += p.distanceTo(p2);
				p = p2;
			}
			return dist;
		}
	}
	
	private LinkedList<PointI> path;

	private Path(LinkedList<PointI> path) {
		this.path = path;
	}
	
	
	public static Path generate(Level level){
		return generate(level, level.sources, level.destinations);
	}
	
	public static Path generate(Level level, List<PointI> sources, List<PointI> destinations){
		LinkedList<Node> currNodes = new LinkedList<>();
		LinkedList<Node> finalNodes = new LinkedList<>();
		boolean[][] visited = new boolean[level.h][level.w];
		// init node list
		for(PointI p:sources){
			currNodes.add(new Node(p));
			visited[p.y][p.x] = true;
		}
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
			while(!nodesToExpand.isEmpty()){
				Node node = nodesToExpand.get(random.nextInt(nodesToExpand.size()));
				nodesToExpand.remove(node);
				int x = node.loc.x;
				int y = node.loc.y;
				if(x == 3 && y == 8){
					int i = 0;
				}
				PointI[] reach = new PointI[4];
				if((node.loc.x > 0) && !visited[y][x - 1]){
					reach[0] = new PointI(x - 1, y);
				}
				if((node.loc.x < level.w - 1) && !visited[y][x + 1]){
					reach[1] = new PointI(x + 1, y);
				}
				if((node.loc.y > 0) && !visited[y - 1][x]){
					reach[2] = new PointI(x, y - 1);
				}
				if((node.loc.y < level.h - 1) && !visited[y + 1][x]){
					reach[3] = new PointI(x, y + 1);
				}
				for(PointI p:reach){
					if(p != null){
						visited[p.y][p.x] = true;
						if(!level.isWalkable(p)) continue;
						Node n = new Node(p, node, level.getPathingWeight(p) + WALK_COST);
						insertNode(n, currNodes);
						if(destinations.contains(n.loc)) finalNodes.add(n);
					}
				}
			}
			if(!finalNodes.isEmpty() || currNodes.isEmpty()) break;
		}
		if(finalNodes.isEmpty()) return null;
		// construct path
		LinkedList<PointI> path = new LinkedList<>();
		Node n = finalNodes.get(random.nextInt(finalNodes.size()));
		path.add(n.loc);
		while(n.src != null){
			n = n.src;
			path.addFirst(n.loc);
		}
		return new Path(path);
	}
	
	public static void insertNode(Node node, LinkedList<Node> list){
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
	
	public PointF getStart(){
		return path.getFirst().getPointF(0.5f);
	}
	
	public PathIterator getPointIterator(float randomComponent){
		return new PathIterator(path, randomComponent);
	}
}
