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
