package application;

import java.util.Observer;

public interface Subject {
	
	public void register(Observer observer);
	public void notifyObserver();
	
}
