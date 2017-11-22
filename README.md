# OpenUtils
The open source utils of tessi lab, under Apache 2.0 license.

## Install
The easier way to install it is to download from maven central repository. For that, simply copy-paste this to your pom in the dependencies block : 

        <dependency>
            <groupId>io.tessilab.oss</groupId>
            <artifactId>OpenUtils</artifactId>
            <version>1.0.0</version>
        </dependency>
        
Many files have been added. To have the last updates (not avaible yet at maven central repository), the easier way to use OpenUtils, is to clone the repository, and run a maven install. Next, add to the pom the next dependency : 

        <dependency>
            <groupId>io.tessilab.oss</groupId>
            <artifactId>OpenUtils</artifactId>
            <version>1.1.0-SNAPSHOT</version>
        </dependency>
        
## JavaDocs

You can find the project JavaDocs at https://tessi-lab.github.io/OpenUtils/latest/index.html

## Content 

Tessilab OpenUtils contains, in each package : 

### _io.tessilab.openutils_

* **FileUtils** : Some useful functions to read files, to transform files it to a String, etc. 

* **CSVReader** : A simple CSV reader

* **HashMapMap** : Some data structures, as a HashMap containing (as values) anothers HashMaps.

* **HistogramUtils** : A library to work with histograms

* **InstanceFactory** : An abstract objet factory used in concurrent context. It allows to share a limited number of instances of a certain class by many threads. 

* **MyDate** : A Tessi lab implementation of a date object.

* **MyToleranceMath** : An object to perform some mathematical operations using a given tolerance. 

* **SharedCloseable** : A class to manage the use of an instance of a closeable by many objects. When the last object ends using the object, the object is close. 

* **StringUtils** :  An API to perform operations on String.

### _io.tessilab.openutils.bbox_

Some classes to work with the interface BBoxable. An object that implements the BBoxable interface is an object that can be surrounded by a rectangle. A generic object is given (BBoxer) and some util files (BBoxableGroup, BBoxableUtils). 

### _io.tessilab.openutils.data_

A package containing the data acess utilies, the ContentLoader class, and the class to manage the instances, the ContentLoaderProvider. A ContentLoader is a class that adds an abstract layer in the files and external elements manage by your application. 

A ContentLoader loads objects indexed by other types of object, both of them parametrized. A cache can be enable, so save the output in a map. 

### _io.tessilab.oss.openutils.designpatterns.observer_

Two ways to implement the observer design pattern. In the first one, the observer must access the subject using the subject accessors. In the other, the subject provides the information via a parametrizable object. Both ways are implemented in interfaces in this package.

### _io.tessilab.oss.openutils.dictionary_

Many dictionnary implementations. Words are stocked in a BK-Tree ( http://blog.notdot.net/2007/4/Damn-Cool-Algorithms-Part-1-BK-Treeshttp://blog.notdot.net/2007/4/Damn-Cool-Algorithms-Part-1-BK-Trees ) 

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

