package chemAnalytics;

import org.hid4java.HidDevice;
import org.hid4java.HidException;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.event.HidServicesEvent;

public class HidLoop extends ObservableHid implements HidServicesListener{

	//DEVICE INFORMATION: Mag-Tek Mini SureSwipe Reader
		private static final Integer VENDOR_ID = 0x801;
		private static final Integer PRODUCT_ID = 0x2;
		private static final String SERIAL_NUMBER = null;
		
		private HidServices hidServices;
		private HidDevice hidDevice;
	
	public HidLoop() {
		initHidDevice();
		startHidLoop();
	}
	
	private void startHidLoop() {
		while (hidDevice.isOpen()) {
			byte[] data = new byte[64];
			int readStatus = -1;
			String studentID = null;
			
			readStatus = hidDevice.read(data);
			switch(readStatus) {
			case -1:
				connectReader();
				break;
			case 0:
				System.err.println("No Data Was Found");
				break;
			default:
				studentID = new String(data).trim();
				try {								//		  indexOf('%')				 indexOf('?')
					studentID = studentID.substring(studentID.indexOf(37) + 1, studentID.indexOf(63));
					setChanged();
					notifyPanChange(studentID);
					System.out.println("Student ID: " + studentID);
				} catch (IndexOutOfBoundsException ex) {
					System.err.println("Error Reading Student ID. Please Try Again.");
					ex.printStackTrace();
				}
			}
		}
	}
	
	private void initHidDevice() throws HidException {

		hidServices = HidManager.getHidServices();

		// Start the services
		System.out.println("Starting HID services.");
		hidServices.start();

		connectReader();
	}
	
	@Override
	public void hidDeviceAttached(HidServicesEvent event) {

		System.out.println("Device attached: " + event);

	}

	@Override
	public void hidDeviceDetached(HidServicesEvent event) {

		// System.err.println("Device detached: " + event);

	}

	@Override
	public void hidFailure(HidServicesEvent event) {

		System.err.println("HID failure: " + event);

	}
	
	private void connectReader() {
		hidDevice = hidServices.getHidDevice(VENDOR_ID, PRODUCT_ID, SERIAL_NUMBER);
		if(hidDevice == null)
			setChanged();
		while(hidDevice == null) {
			//System.out.println("Reader Not Connected");
			notifyConnectionChange(false);
			hidDevice = hidServices.getHidDevice(VENDOR_ID, PRODUCT_ID, SERIAL_NUMBER);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		//System.out.println("Reader Connected!");
		setChanged();
		notifyConnectionChange(true);
	}

}
