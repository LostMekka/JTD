/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.pathing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import jtd.Point;

/**
 *
 * @author LostMekka
 */
public class Path {
	
	private class MyIter<E> implements Iterator<E>{
		Iterator<E> iter;
		public MyIter(List<E> list) {
			iter = list.iterator();
		}
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}
		@Override
		public E next() {
			if(iter.hasNext()){
				return iter.next();
			} else {
				return null;
			}
		}
		@Override
		public void remove() {
			iter.remove();
		}
	}
	
	private LinkedList<Point> path = new LinkedList<>();
	
	// TODO: gen path
	
	public Point getStart(){
		return path.getFirst();
	}
	
	public Iterator<Point> getPointIterator(){
		return new MyIter(path);
	}
}
