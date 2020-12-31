package Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class UnboundedBuffer<T>{

	private List<T> buffer;
	public static int bufferId;
	private int id;
	public UnboundedBuffer() {
		buffer = new ArrayList<T>();
		bufferId=+1;
		this.id = bufferId;
	}
	@Override
	public String toString() {
		return "buffer_"+this.id;
	}

	public synchronized void insert(T item) {
		buffer.add(item);
		this.notifyAll();
	}

	public synchronized T extract() {
		while (buffer.isEmpty()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		//List<T> ans = new ArrayList<T>();
		/*
		for (T l1 : buffer) {
			if (l1 == null) {
				return null;
			}
			for (T t : l1) {
				ans.add(t);
			}
		}
	
		*/
		
		return buffer.remove(0);
	}
	public synchronized void removeAllMsgs() {
		buffer.clear();
		
	}
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return this.buffer.isEmpty();
	}
}



