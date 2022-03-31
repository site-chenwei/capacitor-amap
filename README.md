# capacitor-amap
 高德地图capacitor插件

##安装
```shell
npm install capacitor-amap
npx cap sync
```
##配置
### IOS
IOS安装插件后，需在XCODE的info.plist中为项目配置以下内容
```
    <key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
    <string>此应用需要定位权限才能正常使用</string>
    <key>NSLocationWhenInUseUsageDescription</key>
    <string>此应用需要定位权限才能正常使用</string>
```
<img width="974" alt="image" src="https://user-images.githubusercontent.com/23025255/161015257-a9f35c3d-fa05-4865-9fa2-65525a303739.png">
