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
    <key>NSLocationWhenInUseUsageDescription</key>
    <string>此应用需要定位权限才能正常使用</string>
```
<img width="1026" alt="image" src="https://user-images.githubusercontent.com/23025255/161018082-6904e5b1-e5e8-4621-bed1-772c7f1d5fbf.png">

### Android
Android安装插件后，需要在安卓项目app模块中修改自己的高德安卓API_KEY
