package cn.wjdiankong.xposedhook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

/**
 * 演示地图缩放，旋转，视角控制
 */
public class MainActivity extends Activity {

	private LocationManager locationManager;
	
	private TextView locationTxt, imeiTxt;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
		
		locationTxt = (TextView)findViewById(R.id.location);
		imeiTxt = (TextView)findViewById(R.id.imei);

		//获取地理位置管理器  
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		//获取Location  
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  
		if(location!=null){  
			//不为空,显示地理位置经纬度  
			showLocation(location);  
		}  
		//监视地理位置变化  
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, locationListener);
		
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		imeiTxt.setText("imei:"+imei);
		
	}

	/** 
	 * 显示地理位置经度和纬度信息 
	 * @param location 
	 */  
	private void showLocation(Location location){  
		String locationStr = "纬度：" + location.getLatitude() + ",经度：" + location.getLongitude();  
		locationTxt.setText(locationStr);
		Log.i("jw", "location:"+locationStr);
	}  

	/** 
	 * LocationListern监听器 
	 * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器 
	 */  

	LocationListener locationListener =  new LocationListener() {  

		@Override  
		public void onStatusChanged(String provider, int status, Bundle arg2) {  

		}  

		@Override  
		public void onProviderEnabled(String provider) {  

		}  

		@Override  
		public void onProviderDisabled(String provider) {  

		}  

		@Override  
		public void onLocationChanged(Location location) {  
			//如果位置发生变化,重新显示  
			showLocation(location);  

		}  
	};  

}
