import Foundation
import Capacitor
var locateCalls = [CAPPluginCall]()
/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorAMapPlugin)
public class CapacitorAMapPlugin: CAPPlugin, AMapLocationManagerDelegate, AMapSearchDelegate {
    var aMapLocationManager : AMapLocationManager? = nil
    var completionBlock : AMapLocatingCompletionBlock? = nil
    var isInLocation = false
    var weatherCall : CAPPluginCall? = nil
    var search : AMapSearchAPI? = nil
    
    override public func load() {
        if let iosKey = getConfigValue("iosKey") as? String {
            AMapServices.shared().apiKey=iosKey
            AMapLocationManager.updatePrivacyAgree(AMapPrivacyAgreeStatus.didAgree)
            AMapLocationManager.updatePrivacyShow(AMapPrivacyShowStatus.didShow, privacyInfo:AMapPrivacyInfoStatus.didContain )
            self.aMapLocationManager=AMapLocationManager.init()
            self.aMapLocationManager?.delegate=self
            self.aMapLocationManager?.pausesLocationUpdatesAutomatically=false
            self.aMapLocationManager?.desiredAccuracy = kCLLocationAccuracyHundredMeters
            self.aMapLocationManager?.locationTimeout = 3
            self.aMapLocationManager?.reGeocodeTimeout = 3
        }
    }
    
    public func onWeatherSearchDone(_ request: AMapWeatherSearchRequest!, response: AMapWeatherSearchResponse!) {
        if(response.lives.isEmpty){
            self.weatherCall?.reject("获取天气失败")
            self.weatherCall = nil
            return
        }
        if let weather = response.lives.first{
            var result = [String:String]()
            result.updateValue(weather.weather ?? "", forKey: "weather")
            result.updateValue(weather.temperature ?? "", forKey: "temperature")
            result.updateValue(weather.windDirection ?? "", forKey: "windDirection")
            result.updateValue(weather.windPower ?? "", forKey: "windPower")
            result.updateValue(weather.humidity ?? "", forKey: "humidity")
            self.weatherCall?.resolve(result)
            self.weatherCall = nil
            return
        }
        self.weatherCall?.reject("获取天气失败")
        self.weatherCall = nil
        return
        
    }
    public func aMapSearchRequest(_ request: Any!, didFailWithError error: Error!) {
        self.weatherCall?.reject("获取天气失败")
        self.weatherCall = nil
        return
    }
    
    @objc func calculate(_ call: CAPPluginCall){
        //1.将两个经纬度点转成投影点
        let point1 = MAMapPointForCoordinate(CLLocationCoordinate2D(latitude: call.getDouble("startLatitude") ?? 0, longitude: call.getDouble("startLongitude") ?? 0))
        let point2 = MAMapPointForCoordinate(CLLocationCoordinate2D(latitude: call.getDouble("endLatitude") ?? 0, longitude: call.getDouble("endLongitude") ?? 0))
        let distance = MAMetersBetweenMapPoints(point1,point2)
        var result = [String:Double]()
        result.updateValue(distance, forKey: "distance")
        call.resolve(result)
                                             
    }
    
    @objc func weather(_ call: CAPPluginCall){
        if(weatherCall != nil){
            call.reject("已有正在执行中的天气查询任务")
            return
        }
        self.weatherCall = call
        self.search = AMapSearchAPI()
        self.search?.delegate = self
        let adCode = call.getString("adCode")
        let req:AMapWeatherSearchRequest = AMapWeatherSearchRequest.init()
        req.city = adCode
        req.type = AMapWeatherType.live
        self.search?.aMapWeatherSearch(req)
    }
    @objc func locate(_ call: CAPPluginCall) {
        locateCalls.append(call)
            self.aMapLocationManager?.requestLocation(withReGeocode: true, completionBlock: { [weak self] (location: CLLocation?, reGeocode: AMapLocationReGeocode?, error: Error?) in
                if let error = error {
                    let error = error as NSError
                    if error.code == AMapLocationErrorCode.locateFailed.rawValue {
                        NSLog("定位错误:{\(error.code) - \(error.localizedDescription)};")
                        for item in locateCalls{
                            item.reject("定位错误:{\(error.code) - \(error.localizedDescription)};")
                        }
                        locateCalls.removeAll()
                        return;
                    }
                    else if error.code == AMapLocationErrorCode.reGeocodeFailed.rawValue
                        || error.code == AMapLocationErrorCode.timeOut.rawValue
                        || error.code == AMapLocationErrorCode.cannotFindHost.rawValue
                        || error.code == AMapLocationErrorCode.badURL.rawValue
                        || error.code == AMapLocationErrorCode.notConnectedToInternet.rawValue
                        || error.code == AMapLocationErrorCode.cannotConnectToHost.rawValue {
                        NSLog("逆地理错误:{\(error.code) - \(error.localizedDescription)};")
                        for item in locateCalls{
                            item.reject("定位错误:{\(error.code) - \(error.localizedDescription)};")
                        }
                        locateCalls.removeAll()
                        return;
                    }
                    else if (error != nil && error.code == AMapLocationErrorCode.riskOfFakeLocation.rawValue)
                    {
                        NSLog("存在虚拟定位的风险:{\(error.code) - \(error.localizedDescription)};")
                        for item in locateCalls{
                            item.reject("定位错误:{\(error.code) - \(error.localizedDescription)};")
                        }
                        locateCalls.removeAll()
                        return;
                    }
                }
                if let location = location {
                        NSLog("location:%@", location)
                    if let reGeocode = reGeocode {
                        NSLog("reGeocode:%@", reGeocode);
                        var result = [String:String]()
                        result.updateValue(String(location.coordinate.latitude) ?? "", forKey: "latitude")
                        result.updateValue(String(location.coordinate.longitude) ?? "", forKey: "longitude")
                        result.updateValue(reGeocode.formattedAddress ?? "", forKey: "address")
                        result.updateValue(reGeocode.number ?? "", forKey: "streetNum")
                        result.updateValue(reGeocode.country ?? "", forKey: "country")
                        result.updateValue(reGeocode.district ?? "", forKey: "district")
                        result.updateValue(reGeocode.adcode ?? "", forKey: "adCode")
                        result.updateValue(reGeocode.province ?? "", forKey: "province")
                        result.updateValue(reGeocode.street ?? "", forKey: "street")
                        result.updateValue(reGeocode.city ?? "", forKey: "city")
                        result.updateValue(reGeocode.citycode ?? "", forKey: "cityCode")
                        result.updateValue(reGeocode.poiName ?? "", forKey: "poiName")
                        result.updateValue(reGeocode.aoiName ?? "", forKey: "aoiName")
                        for item in locateCalls{
                            item.resolve(result)
                        }
                        locateCalls.removeAll()
                        return;
                    }
                }
                NSLog("逆定理失败，无返回值")
                for item in locateCalls{
                    item.reject("逆定理失败，无返回值")
                }
                locateCalls.removeAll()
                return;
            
            })
    }
}
