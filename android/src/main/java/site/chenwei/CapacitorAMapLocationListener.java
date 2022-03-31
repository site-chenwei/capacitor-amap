package site.chenwei;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CapacitorAMapLocationListener implements AMapLocationListener, WeatherSearch.OnWeatherSearchListener {
    private static final String TAG = "CapacitorAMap";
    private final List<PluginCall> pluginCalls = new ArrayList<>();
    private PluginCall pluginCall;
    private final CapacitorAMapPlugin capacitorAMap;


    CapacitorAMapLocationListener(CapacitorAMapPlugin capacitorAMap) {
        this.capacitorAMap = capacitorAMap;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && 0 == aMapLocation.getErrorCode()) {
            Log.i(TAG, "Location Success");
            JSObject jsObject = new JSObject();
            jsObject.put("accuracy", aMapLocation.getAccuracy());
            jsObject.put("adCode", aMapLocation.getAdCode());
            jsObject.put("address", aMapLocation.getAddress());
            jsObject.put("city", aMapLocation.getCity());
            jsObject.put("cityCode", aMapLocation.getCityCode());
            jsObject.put("altitude", aMapLocation.getAltitude());
            jsObject.put("latitude", aMapLocation.getLatitude());
            jsObject.put("longitude", aMapLocation.getLongitude());
            jsObject.put("aoiName", aMapLocation.getAoiName());
            jsObject.put("country", aMapLocation.getCountry());
            jsObject.put("district", aMapLocation.getDistrict());
            jsObject.put("poiName", aMapLocation.getPoiName());
            jsObject.put("province", aMapLocation.getProvince());
            jsObject.put("street", aMapLocation.getStreet());
            jsObject.put("streetNum", aMapLocation.getStreetNum());
            jsObject.put("locationTime", new Date());
            resolve(jsObject);
        } else if (aMapLocation != null) {
            Log.e(TAG, "Location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
            reject("Location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
        } else {
            Log.e(TAG, "Location Error, No Error Message");
            reject("Location Error, No Error Message");
        }
        this.capacitorAMap.stopLocation();
    }

    public void addPluginCall(PluginCall call) {
        this.pluginCalls.add(call);
    }

    public void setPluginCall(PluginCall call) {
        this.pluginCall = call;
    }

    public boolean isInWeather() {
        return this.pluginCall != null;
    }

    private void resolve(JSObject jsObject) {
        for (PluginCall pluginCall : this.pluginCalls) {
            pluginCall.resolve(jsObject);
            this.pluginCalls.remove(pluginCall);
        }
    }

    private void reject(String errMsg) {
        for (PluginCall pluginCall : this.pluginCalls) {
            pluginCall.reject(errMsg);
            this.pluginCalls.remove(pluginCall);
        }
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult result, int code) {
        if (code == 1000) {
            if (result != null && result.getLiveResult() != null) {
                JSObject jsObject = new JSObject();
                LocalWeatherLive live = result.getLiveResult();
                jsObject.put("weather", live.getWeather());
                jsObject.put("temperature", live.getTemperature());
                jsObject.put("city", live.getCity());
                jsObject.put("province", live.getProvince());
                jsObject.put("windDirection", live.getWindDirection());
                jsObject.put("windPower", live.getWindPower());
                jsObject.put("humidity", live.getHumidity());
                pluginCall.resolve(jsObject);
            } else {
                Log.e(TAG, "获取天气失败");
                pluginCall.reject("获取天气失败");
            }
        } else {
            Log.e(TAG, "获取天气失败");
            pluginCall.reject("获取天气失败");
        }
        pluginCall = null;
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {
        System.out.printf("");
    }
}
