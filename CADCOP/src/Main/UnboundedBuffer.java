package Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class UnboundedBuffer<T>{

	private List<List<T>> buffer;

	public UnboundedBuffer() {
		buffer = new ArrayList<List<T>>();
	}

	public synchronized void insert(List<T> item) {
		buffer.add(item);
		this.notifyAll();
	}

	public synchronized List<T> extract() 
			throws InterruptedException{
		while (buffer.isEmpty()) {
			this.wait();
		}
		return buffer.remove(0);
	}
}



