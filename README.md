# OpenUtils
The open source utils of tessi lab, under Apache 2.0 license.

## Install
The easier way to install it is to download from maven central repository. For that, simply copy-paste this to your pom in the dependencies block : 

        <dependency>
            <groupId>io.tessilab.oss</groupId>
            <artifactId>OpenUtils</artifactId>
            <version>1.0.0</version>
        </dependency>

## Content 

Tessilab OpenUtils contains : 

* Some useful functions to read files, to transform files it to a String, etc. (cf FileUtils, from the package _io.tessilab.openutils_)

* Two ways to implement the observer design pattern. In the first one, the observer must access the subject using the subject accessors. In the other, the subject provides the information via a parametrizable object. Both ways are implemented in interfaces in the package _io.tessilab.oss.openutils.designpatterns.observer_

* Some classes to make the java API of ElasticSearch (https://logging.apache.org/log4j/2.x/) easier to use, in the package _io.tessilab.oss.openutils.elasticsearch_

* An OutputStream and a PrintStream to print on a Log4j log (https://logging.apache.org/log4j/2.x/) in the package _io.tessilab.oss.openutils.io_

* The classes to create a lock system, using a database (or the file system) in the scenario when there are many workers who must perform several parallel tasks. The classes are in the package _io.tessilab.oss.openutils.locker_

* A very simple progress bar, taken from https://github.com/ctongfei/progressbar. The behaviour has been totaly changed to allow to the main thread to control the progress bar. 

* A tree to display a system, like the Linux one, in the package _io.tessilab.oss.openutils.treedisplaying_.  It looks like  : 

```
├── root 
│   ├── param1 
│   │   ├── param2 
│   │   │   └── param3
│   │   └── param4
│   ├── param5
│   │   └── param6
```


