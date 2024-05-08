# miot-api

[![](https://jitpack.io/v/sky130/MiWu.svg)](https://jitpack.io/#sky130/MiWu)

通过`miot-api`, 可以快速上手米家接口的调用

## 导包

库是为Kotlin开发的, 请使用Kotlin语言使用该库

```kotlin
implementation("com.github.sky130:MiWu:2.0.6")
```

## 项目树

```
miot.kotlin
     |-- Config.kt 配置文件
     |-- Miot.kt 
     |-- MiotManager.kt 类库入口
     |-- helper
     |   |-- DeviceHelper.kt 设备辅助
     |   |-- MultiLanguage.kt 多语言翻译
     |-- model 
     |-- service Retrofit 接口
     `-- utils
         |-- DataUtil.kt
         |-- Retrofit.kt
         `-- UrnUtil.kt 
```

