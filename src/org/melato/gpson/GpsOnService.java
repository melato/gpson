package org.melato.gpson;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/** The log service. */
public class GpsOnService extends Service {
  private GpsListener listener;
	private  boolean isOn;

	private GpsOnBinder binder = new GpsOnBinder();
	
	public class GpsOnBinder extends IGpsOnRemote.Stub {

	  
    @Override
    public boolean isOn() throws RemoteException {
      return isOn;
    }

    @Override
    public void setOn(boolean on) throws RemoteException {
      if ( on ) {
        listener.start();
      } else {
        listener.stop();
      }
      isOn = on;
    }
  }
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		listener = new GpsListener(this);
	}

	
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }
  
  @Override
  public void onDestroy() {
    listener.stop();
  }
  
}
