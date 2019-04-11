package chemAnalytics;

public interface HidObserver {

    void updateConnection(ObservableHid o, Boolean isConnected);
    
    void updatePan(ObservableHid o, String pan);
	
}
