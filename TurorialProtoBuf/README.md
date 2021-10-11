@[TOC](Android Studio 配置并使用Protocol Buffer生成java文件)

### android studio配置

#### Project的build.gradle

```groovy
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath 'com.google.protobuf:protobuf-gradle-plugin:0.8.17'
  }
}
```

目前`Protobuf Plugin for Gradle`最版本是**0.8.17**，安装该版本的插件需要**Gradle 5.6** 和 **Java 8**，在`Maven Central`可用

#### Module的build.gradle

##### 声明依赖的protobuf插件

###### 采用apply方法

```groovy
apply plugin: 'com.android.application'  // or 'com.android.library'
apply plugin: 'com.google.protobuf' //必须apply plugin: 'com.android.application'语句后，再声明
....
```

###### 采用Gradle plugin DSL

```groovy
plugins {
    id 'com.android.application'
    id 'com.google.protobuf' //这个没有先后顺序
}
```

##### 指定被编译proto文件所在的路径

```groovy
android {
	.....
    sourceSets {
        main {
            java {
                srcDir 'src/main/java'
            }
            proto {
                srcDir 'src/main/proto'
            }
        }
    }
    ....
}
```

**注意：不指定时，默认与java同级的目录(即${ProjectName}/app/src/main/java)**

####  添加protobuf依赖与proto生成任务

##### 采用版本为**3.0.x~3.7.x**的`Protobuf`

```groovy
dependencies {
  // You need to depend on the lite runtime library, not protobuf-java
  implementation 'com.google.protobuf:protobuf-lite:3.0.0'
}

protobuf {
  protoc {
    // You still need protoc like in the non-Android case
    artifact = 'com.google.protobuf:protoc:3.7.0'
  }
  plugins {
    javalite {
      // The codegen for lite comes as a separate artifact
      artifact = 'com.google.protobuf:protoc-gen-javalite:3.0.0'
    }
  }
  generateProtoTasks {
    all().each { task ->
      task.builtins {
        // In most cases you don't need the full Java output
        // if you use the lite output.
        remove java
      }
      task.plugins {
        javalite { }
      }
    }
  }
}
```

##### 采用版本为**3.8.0**之后的`Protobuf`

```groovy
dependencies {
  // You need to depend on the lite runtime library, not protobuf-java
  //implementation 'com.google.protobuf:protobuf-javalite:3.8.0'
  //目前protobuf-javalite的最新版本
  implementation 'com.google.protobuf:protobuf-javalite:3.18.1'  
}

protobuf {
  protoc {
    //指定protobuf compile版本  
    //artifact = 'com.google.protobuf:protoc:3.8.0'
    //目前protoc的最新版本
    artifact = 'com.google.protobuf:protoc:3.18.1'  
  }
  generateProtoTasks {
    all().each { task ->
      task.builtins {
        java {
          option "lite"
        }
      }
    }
  }
}
```

**注意：`protobuf-javalit`与`protoc`的最新版本可在[maven仓库](https://search.maven.org/)搜索该两个字眼得到**

### 编译proto文件

`${ProjectName}/app/src/main/proto/addressbook.proto`

```protobuf
syntax = "proto2";

package tutorial;

option java_multiple_files = true;
option java_package = "com.example.tutorial.protos";
option java_outer_classname = "AddressBookProtos";

message Person {
  optional string name = 1;
  optional int32 id = 2;
  optional string email = 3;

  enum PhoneType {
    MOBILE = 0;
    HOME = 1;
    WORK = 2;
  }

  message PhoneNumber {
    optional string number = 1;
    optional PhoneType type = 2 [default = HOME];
  }

  repeated PhoneNumber phones = 4;
}

message AddressBook {
  repeated Person people = 1;
}
```

**`最后编译产物路径：${ProjectName}/app/build/generated/source/proto`**

[protobuf 语法教程](https://developers.google.com/protocol-buffers/docs/javatutorial)

### 使用proto编译产物

```java
Person john =
                Person.newBuilder()
                        .setId(1234)
                        .setName("John Doe")
                        .setEmail("jdoe@example.com")
                        .addPhones(
                                Person.PhoneNumber.newBuilder()
                                        .setNumber("555-4321")
                                        .setType(Person.PhoneType.HOME))
                        .build();
AddressBook.Builder addressBookBuilder = AddressBook.newBuilder();
addressBookBuilder.addPeople(john);
AddressBook addressBook = addressBookBuilder.build();
```

### 参考

[android studio 配置protobuf 参考](https://github.com/google/protobuf-gradle-plugin/blob/master/README.md)

