package cn.wjdiankong.xposedhook;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage {
	
	private void hook_method(String className, ClassLoader classLoader, String methodName,
			Object... parameterTypesAndCallback){
		try {
			XposedHelpers.findAndHookMethod(className, classLoader, methodName, parameterTypesAndCallback);
		} catch (Exception e) {
			XposedBridge.log(e);
		}
	}
	
	private void hook_methods(String className, String methodName, XC_MethodHook xmh){
		try {
			Class<?> clazz = Class.forName(className);
			for (Method method : clazz.getDeclaredMethods())
				if (method.getName().equals(methodName)
						&& !Modifier.isAbstract(method.getModifiers())
						&& Modifier.isPublic(method.getModifiers())) {
					XposedBridge.hookMethod(method, xmh);
				}
		} catch (Exception e) {
			XposedBridge.log(e);
		}
	}

	@Override
	public void handleLoadPackage(final LoadPackageParam lpp) throws Throwable{

		Log.i("jw", "pkg:"+lpp.packageName);
		
		hook_method("android.telephony.TelephonyManager", lpp.classLoader, "getDeviceId", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				Log.i("jw", "hook getDeviceId...");
				Object obj = param.getResult();
				Log.i("jw", "imei args:"+obj);
				param.setResult("jiangwei");
			}
		});
		
		//定位
		hook_methods("android.location.LocationManager", "getLastKnownLocation", new XC_MethodHook(){
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				Log.i("jw", "hook getLastKnownLocation...");
				Location l = new Location(LocationManager.PASSIVE_PROVIDER);
				double lo = -10000d; //经度
				double la = -10000d; //纬度
				l.setLatitude(la);
				l.setLongitude(lo);
				param.setResult(l);
			}
		});
		
		hook_methods("android.location.LocationManager", "requestLocationUpdates", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				Log.i("jw", "hook requestLocationUpdates...");

				if (param.args.length == 4 && (param.args[0] instanceof String)) {
					LocationListener ll = (LocationListener)param.args[3];
					Class<?> clazz = LocationListener.class;
					Method m = null;
					for (Method method : clazz.getDeclaredMethods()) {
						if (method.getName().equals("onLocationChanged")) {
							m = method;
							break;
						}
					}

					try {
						if (m != null) {
							Object[] args = new Object[1];
							Location l = new Location(LocationManager.PASSIVE_PROVIDER);
							double lo = -10000d; //经度
							double la = -10000d; //纬度
							l.setLatitude(la);
							l.setLongitude(lo);
							args[0] = l;
							m.invoke(ll, args);
						}
					} catch (Exception e) {
						XposedBridge.log(e);
					}
				}
			}
		});

	}

}




















