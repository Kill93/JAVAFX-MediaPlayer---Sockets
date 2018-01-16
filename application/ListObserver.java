package application;

import java.util.ArrayList;
import java.util.Observer;

public class ListObserver implements Subject{
	
	private ArrayList<Observer> observers = new ArrayList<Observer>();
	private String list;
	
	public ListObserver(String serverList) {
		super();
		this.list = serverList;
	}

	@Override
	public void register(java.util.Observer observer) {
		// TODO Auto-generated method stub
		observers.add(observer);	
	}

	@Override
	public void notifyObserver() {
		 for (Observer ob : observers) {
             ob.update(null, this.list);
      }
		
	}

	
}
