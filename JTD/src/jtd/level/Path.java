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
				return iter.next().getPountF();
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
		
	}
	
	public PointF getStart(){
		return path.getFirst().getPountF();
	}
	
	public Iterator<PointF> getPointIterator(){
		return new MyIter(path);
	}
}
