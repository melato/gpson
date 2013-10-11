/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
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
