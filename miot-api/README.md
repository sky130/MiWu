# miot-api

[![](https://jitpack.io/v/sky130/MiWu.svg)](https://jitpack.io/#sky130/MiWu)

通过`miot-api`, 可以快速上手米家接口的调用

## 导包

库是为Kotlin开发的, 请使用Kotlin语言使用该库

```kotlin
implementation("com.github.sky130:MiWu:2.0.6")
```

## 入门

```
 `-- miot.kotlin
     |-- Config.kt 配置文件
     |-- Miot.kt 
     |-- MiotManager.kt 类库入口，提供登录和其他功能
     |-- exception 
     |   `-- MiotUnauthorizedException.kt 没啥用的错误
     |-- helper
     |   |-- DeviceHelper.kt 
     |   |-- MultiLanguage.kt 
     |-- model 
     |-- service Retrofit接口
     `-- utils
         |-- DataUtil.kt
         |-- Retrofit.kt
         `-- UrnUtil.kt

```

