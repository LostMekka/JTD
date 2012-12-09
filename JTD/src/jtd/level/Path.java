/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.level;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import jtd.PointF;
import jtd.PointI;

/**
 *
 * @author LostMekka
 */
public class Path {
	
	private class Node{
		public PointI loc;
		public Node src;
		public Node(PointI loc, Node src) {
			this.loc = loc;
			this.src = src;
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
	
	private LinkedList<PointI> path = new LinkedList<>();
	
	public void generate(){
		path = new LinkedList<>();
		path.add(new PointI(0, 0));
		path.add(new PointI(4, 0));
		path.add(new PointI(4, 2));
		path.add(new PointI(2, 2));
		
		path.add(new PointI(2, 4));
		path.add(new PointI(4, 4));
		path.add(new PointI(4, 6));
		path.add(new PointI(2, 6));
		
		path.add(new PointI(2, 8));
		path.add(new PointI(4, 8));
	}
	
	public PointF getStart(){
		return path.getFirst().getPointF(0.5f);
	}
	
	public PathIterator getPointIterator(float randomComponent){
		return new PathIterator(path, randomComponent);
	}
}
