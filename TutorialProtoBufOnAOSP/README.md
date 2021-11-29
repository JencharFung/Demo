### Android.mk

```makefile
#加入当前路径下的proto目录所有proto文件
LOCAL_SRC_FILES += $(call all-proto-files-under, proto)
#或者具体加某个proto文件
LOCAL_SRC_FILES += test.proto
```

一般在源码开发都这么用

```makefile
#编译成java库
include $(CLEAR_VARS)
LOCAL_MODULE := managedprovisioning_protoslite
LOCAL_SRC_FILES := $(call all-proto-files-under, proto)
LOCAL_PROTOC_OPTIMIZE_TYPE := lite
LOCAL_MODULE_TAGS := optional
include $(BUILD_STATIC_JAVA_LIBRARY)
......
#使用这个java库
LOCAL_STATIC_JAVA_LIBRARIES := managedprovisioning_protoslite
......
include $(BUILD_PACKAGE)
```

或者

```makefile
LOCAL_SRC_FILES := \
    $(call all-java-files-under, src) \
    $(call all-proto-files-under, proto)
......
#指明nano,编译完成以后自动添加到LOCAL_STATIC_JAVA_LIBRARIES
LOCAL_PROTOC_OPTIMIZE_TYPE := nano 
......
include $(BUILD_PACKAGE)
```

### Android.bp

```makefile
srcs: [
		//加入当前路径下的proto目录所有proto文件
        "proto/**/*.proto",
        //或者具体加某个proto文件
        "test.proto",
    ],
```



```makefile
#编译成java库
java_library_static {
    name: "managedprovisioning_protoslite",
    proto: {
        type: "lite",
    },
    srcs: ["proto/**/*.proto"],
}
#使用这个java库
static_libs: "managedprovisioning_protoslite",

```

或者

```makefile
android_app {
    .......
    srcs: [
        "src/**/*.java",
        "proto/**/*.proto",
    ],
    proto: {
        type: "nano",
    },
}
```

