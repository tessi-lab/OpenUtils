# OpenUtils
The open source utils of tessi lab, under Apache 2.0 license.

## Install
The easier way to install it is to download from maven central repository. For that, simply copy this to your pom in the dependencies block : 

        <dependency>
            <groupId>io.tessilab.oss</groupId>
            <artifactId>OpenUtils</artifactId>
            <version>1.0.0</version>
        </dependency>

## Content 

Tessilab OpenUtils contains : 

* Some usefull functions to read files, read a file and transform it to a String, etc. This is in file FileUtils on the package io.tessilab.openutils

* Two ways to implement the observer design pattern. In one, the observer must access to the subject using the subject accesors. In the other, the subject provides the information via a parametrizable object. Both ways are implemented in interfaces in the package io.tessilab.oss.openutils.designpatterns.observer

* Some classes to make the use of ElasticSearch (https://logging.apache.org/log4j/2.x/) java API easier, in the package io.tessilab.oss.openutils.elasticsearch

* An OutputStream and a PrintStream to print on a Log4j log (https://logging.apache.org/log4j/2.x/) in the package io.tessilab.oss.openutils.io

* The clases to create a lock system, using a database (or the file system) in the scenario when there are many workers who must perform several parallel tasks. The classes are in the packahe io.tessilab.oss.openutils.locker

* A very simple progressbar, taken from https://github.com/ctongfei/progressbar. The behaviour has been totaly change to allow to the main thread to control the progressbar. 

* A tree displaying sistem, like the Linux one, in the package io.tessilab.oss.openutils.treedisplaying.  It looks like  : 

```
├── root 
│   ├── param1 
│   │   ├── param2 
│   │   │   └── param3
│   │   └── param4
│   ├── param5
│   │   └── param6
```


