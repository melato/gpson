package org.melato.gpson;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * The location listener.
 * 
 **/
public class GpsListener implements LocationListener {
  private Context context;
  
	public GpsListener(Context context) {
	  this.context = context;	
	  start();
	}
	
	public void start() {	  
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    locationManager.removeUpdates(this);
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000L, 1000, this);    
    }
	
	public void stop() {
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    locationManager.removeUpdates(this);
	}	
	
  @Override
  public void onLocationChanged(Location loc) {
  }

  @Override
  public void onProviderDisabled(String msg) {
  }

  @Override
  public void onProviderEnabled(String msg) {
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
  }
}
