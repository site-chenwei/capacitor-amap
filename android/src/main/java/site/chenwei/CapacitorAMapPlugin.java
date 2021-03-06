package site.chenwei;

import android.Manifest;
import android.os.Build;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

@CapacitorPlugin(name = "CapacitorAMap", permissions = {
        @Permission(alias = "capacitorAMapLocation", strings = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
        })
})
public class CapacitorAMapPlugin extends Plugin {
    private static final String TAG = "CapacitorAMap";
    private AMapLocationClient locationClient;
    private CapacitorAMapLocationListener locationListener;
    private boolean isInLocation = false;

    @Override
    public void load() {
        super.load();
        try {
            AMapLocationClient.updatePrivacyShow(getContext(), true, true);
            AMapLocationClient.updatePrivacyAgree(getContext(), true);
            this.locationListener = new CapacitorAMapLocationListener(this);
            locationClient = new AMapLocationClient(getContext().getApplicationContext());
            locationClient.setLocationListener(this.locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PermissionCallback
    public void LocationPermissionCallback(PluginCall call) {
        if (getPermissionState("capacitorAMapLocation") != PermissionState.GRANTED) {
            call.reject("????????????????????????");
        } else {
            this.locateWhenPermitted(call);
        }
    }

    @PluginMethod
    public void locate(PluginCall call) {
        if (getPermissionState("capacitorAMapLocation") != PermissionState.GRANTED) {
            requestPermissionForAlias("capacitorAMapLocation", call, "LocationPermissionCallback");
        } else {
            this.locateWhenPermitted(call);
        }

    }

    private void locateWhenPermitted(PluginCall call) {
        this.locationListener.addPluginCall(call);
        if (!this.isInLocation) {
            this.isInLocation = true;
            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setMockEnable(false);
            option.setLocationCacheEnable(false);
            option.setOnceLocationLatest(true);
            option.setHttpTimeOut(8000);
            locationClient.setLocationOption(option);
            locationClient.stopLocation();
            locationClient.startLocation();
        }
    }

    @PluginMethod
    public void weather(PluginCall call) {
        if (this.locationListener.isInWeather()) {
            call.reject("??????????????????????????????????????????");
            return;
        }
        try {
            WeatherSearchQuery weatherSearchQuery = new WeatherSearchQuery(call.getString("adCode"), WeatherSearchQuery.WEATHER_TYPE_LIVE);
            WeatherSearch weatherSearch = new WeatherSearch(getContext());
            weatherSearch.setOnWeatherSearchListener(this.locationListener);
            this.locationListener.setPluginCall(call);
            weatherSearch.setQuery(weatherSearchQuery);
            weatherSearch.searchWeatherAsyn();
        } catch (Exception e) {
            e.printStackTrace();
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void calculate(PluginCall call) {
        try {
            DPoint startPoint = new DPoint(call.getDouble("startLatitude"), call.getDouble("startLongitude"));
            DPoint endPoint = new DPoint(call.getDouble("endLatitude"), call.getDouble("endLongitude"));
            float v = CoordinateConverter.calculateLineDistance(startPoint, endPoint);
            JSObject jsObject = new JSObject();
            jsObject.put("distance", v);
            call.resolve(jsObject);
        } catch (Exception e) {
            e.printStackTrace();
            call.reject(e.getMessage());
        }
    }

    public void stopLocation() {
        this.locationClient.stopLocation();
        this.isInLocation = false;
    }
}
