Android Studio 创建的app放到Android 源码下编译

### 环境

`Android Studio`版本：

```
Android Studio Dolphin | 2021.3.1 Patch 1
Build #AI-213.7172.25.2113.9123335, built on September 30, 2022
```

`gradle`版本：

```
distributionBase=GRADLE_USER_HOME
distributionUrl=https\://services.gradle.org/distributions/gradle-7.4-bin.zip
distributionPath=wrapper/dists
zipStorePath=wrapper/dists
zipStoreBase=GRADLE_USER_HOME
```

Android 源码的`Build ID`及其`Tag`：

```
SQ1A.220205.002
android-12.0.0_r28
```

### 步骤

#### 创建一个Android Studio 新项目

将该项目复现到源码`/vendor/${yout company}/apps/`中，并且复制`app/src/main/AndroidManifest.xml`到项目根目录。

```
TestCompileOnAosp/
├── Android.bp
├── AndroidManifest.xml
├── Android.mk_bak
├── app
├── build.gradle
├── gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
└── settings.gradle
```



**注意：由于`gradle-7.4`会`Move package from Android manifest to build files`，创建出来的`AndroidManifest-manifest`中是没有指定包名`package`的，但是Android 源码编译需要添加，则需要在`app/src/main/AndroidManifest.xml`的`manifest`标签下，添加包名`package="com.xxx.xxxxxxx"`。**

#### 编写编译脚本

有两种：

##### Android.mk

```
#指的当前目录变量LOCAL_PATH
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

#LOCAL_MODULE_TAGS := user eng tests optional
#user: 指该模块只在user版本下才编译
#eng: 指该模块只在eng版本下才编译
#tests: 指该模块只在tests版本下才编译
#optional:指该模块在全部版本下都编译
LOCAL_MODULE_TAGS := optional

#指定apk的src目录
LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main/java)
#指定apk的res目录
LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/app/src/main/res \

#指定额外的Manifest,这里的Manifest和根目录Manifest其实是合并关系，不是覆盖
LOCAL_FULL_LIBS_MANIFEST_FILES:=$(LOCAL_PATH)/app/src/main/AndroidManifest.xml

LOCAL_STATIC_ANDROID_LIBRARIES := \
    androidx.appcompat_appcompat \
    com.google.android.material_material \
    androidx-constraintlayout_constraintlayout \


#要编译成apk的名字
LOCAL_PACKAGE_NAME := TestCompileOnAosp
#去除dex相关优化，这样好处就是我们编译出来apk是可以正常adb install的不然经过dex优化后，其实apk是个空壳
LOCAL_DEX_PREOPT := false

#设置该标记后会使用sdk的hide的api來编译。编译的APK中使用了系统级API，必须设定该值
LOCAL_PRIVATE_PLATFORM_APIS := true
#平台签名
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

```

##### Android.bp

```
android_app{
	name: "TestCompileOnAosp",
    srcs: ["app/src/main/java/**/*.java"],
	resource_dirs: ["app/src/main/res"],
	additional_manifests: [ //和Android.mk一样会在根目录也有个AndroidManifest只不过他是个壳，这里这个才是真正的，二者会合并
        "app/src/main/AndroidManifest.xml",
    ],
    static_libs: [
    	"androidx.appcompat_appcompat",
    	"com.google.android.material_material",
    	"androidx-constraintlayout_constraintlayout",
    ],//依赖的jar包
    dex_preopt: {
        enabled: false,
    },
    platform_apis: true,//。和Android.mk中的	LOCAL_PRIVATE_PLATFORM_APIS的作用相同
    certificate: "platform",//代表签名
}
```

结果：

```
Install: out/target/product/xxxx/system/app/XxxxxApp
```

**附：`LOCAL_STATIC_ANDROID_LIBRARIES`所需要的静态库，可以在源码的`prebuilts/sdk/current`中查找**

