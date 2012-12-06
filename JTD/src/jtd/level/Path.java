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
	
	private class MyIter implements Iterator<PointF>{
		Iterator<PointI> iter;
		public MyIter(List<PointI> list) {
			iter = list.iterator();
		}
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}
		@Override
		public PointF next() {
			if(iter.hasNext()){
				return iter.next().getPointF(0.1f);
			} else {
				return null;
			}
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
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
		path.add(new PointI(0, 8));
		path.add(new PointI(0, 11));
		path.add(new PointI(4, 11));
	}
	
	public PointF getStart(){
		return path.getFirst().getPointF(0.5f);
	}
	
	public Iterator<PointF> getPointIterator(){
		return new MyIter(path);
	}
}
