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

import java.util.ArrayList;
import java.util.List;

import org.melato.gpson.util.Invokable;
import org.melato.gpson.util.LabeledValue;
import org.melato.gpson.util.LatitudeField;
import org.melato.gpson.util.LongitudeField;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ControlActivity extends Activity implements OnClickListener, OnItemClickListener, GpsStatus.Listener, LocationListener {
    static final String SERVICE = "org.melato.gpson.GpsOnService";
    LocationManager locationManager;
    Button button;      
    ListView listView;
    IGpsOnRemote onService;
    Location location;
    int satCount;
    int fixCount;
    int fixTime;
    private List<Object> properties = new ArrayList<Object>();

    class PropertyAdapter extends ArrayAdapter<Object> {
      public PropertyAdapter() {
        super(ControlActivity.this, R.layout.property, properties);
      }
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position,convertView,parent);
        Object p = properties.get(position);
        textView.setText(p.toString());
        if ( p instanceof Invokable ) {
          textView.setTextColor(getResources().getColor(R.color.link));
        } else {
          textView.setTextColor(getResources().getColor(R.color.detail));
        }
        return textView;
      }
    }
    
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
      // Called when the connection with the service is established
      public void onServiceConnected(ComponentName className, IBinder service) {
          // Following the example above for an AIDL interface,
          // this gets an instance of the IRemoteInterface, which we can use to call on the service
        onService = IGpsOnRemote.Stub.asInterface(service);
        try {
          onService.setOn(true);
        } catch (RemoteException e) {
          throw new RuntimeException( e );
        }
      }

      // Called when the connection with the service disconnects unexpectedly
      public void onServiceDisconnected(ComponentName className) {
          onService = null;
      }
  };
  
    public ControlActivity() {      
    }
    
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      locationManager.addGpsStatusListener(this);
      startService(new Intent(SERVICE));
      bindService(new Intent(SERVICE), serviceConnection, Context.BIND_AUTO_CREATE);
      setContentView(R.layout.control);
      button = (Button) findViewById(R.id.button);
      listView = (ListView) findViewById(R.id.listView);
      listView.setOnItemClickListener(this);
      button.setOnClickListener(this);
    }
    
    public void onDestroy() {
      LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      locationManager.removeGpsStatusListener(this);
      if ( onService != null ) {
        unbindService(serviceConnection);
        onService = null;
      }
      super.onDestroy();
    }
    
    @Override
    public void onClick(View v) {
      try {
        onService.setOn(false);
      } catch( RemoteException e) {        
      }
    }
    
    void updateUI() {
      List<Object> properties = new ArrayList<Object>();
      properties.add(new LabeledValue(this, R.string.visible_satellites, satCount));
      properties.add(new LabeledValue(this, R.string.satellites_used, fixCount));
      if ( location != null) {
        properties.add(new LatitudeField(getString(R.string.latitude), location));
        properties.add(new LongitudeField(getString(R.string.longitude), location));
      }
      this.properties = properties;
      listView.setAdapter(new PropertyAdapter());
    }
    
    void updateGpsStatus(GpsStatus gps) {
      satCount = 0;
      fixCount = 0;
      for(GpsSatellite sat: gps.getSatellites()) {
        satCount++;
        if ( sat.usedInFix()) {
          fixCount++;
        }        
      }
      fixTime = gps.getTimeToFirstFix() / 1000;
    }
    
    @Override
    public void onGpsStatusChanged(int event) {
      switch(event) {
        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
          updateGpsStatus(locationManager.getGpsStatus(null));
          updateUI();
          break;
        default:
          break;
      }
    }

    @Override
    public void onLocationChanged(Location location) {
      this.location = location;
      updateUI();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
        long id) {
      Object value = properties.get(position);
      Log.i("aa", "Item: " + value);
      if ( value instanceof Invokable) {
        Log.i("aa", "Invokable");
        ((Invokable)value).invoke(this);
      }
    }
}

