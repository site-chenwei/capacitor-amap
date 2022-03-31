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
