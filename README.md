simple-cache
============

Simple two-level cache written in Java.

It's not thread-safe, it hasn't got outstanding performance or smallest memory usage.
Anyway it's just a sample bunch of classes.

Features:
* level-1 cache uses memory, level-2 cache uses disk (filesystem); 
* LRU/MRU strategies are implemented;
* no third-party libraries are used for cache implementation. 

See [SampleApp](src/SampleApp.java) for usage example. 

Build sample application:
```
gradle jar
```

Usage (it will just read all files from directory using cache):
```
java -jar build/libs/simple-cache-{version}.jar [OPTIONS] <DIRECTORY> 
 -c1 N          : level-1 (memory) cache capacity
 -c2 N          : level-2 (disk) cache capacity
 -d VAL         : level-2 cache directory (will be erased and recreated)
 -s [LRU | MRU] : cache strategy
```

Links:
* [Java LRU cache](http://www.javaspecialist.ru/2012/02/java-lru-cache.html)
* [В чем преимущество LinkedHashMap перед HashMap?](http://javatalks.ru/topics/34786)
* [Структуры данных в картинках. LinkedHashMap](http://habrahabr.ru/post/129037/)
* [Intro to Caching,Caching algorithms and caching frameworks ](http://javalandscape.blogspot.ru/2009/01/cachingcaching-algorithms-and-caching.html)
* [Implement Simple Threadsafe Cache using HashMap without using Synchronized Collection](http://crunchify.com/implement-simple-threadsafe-cache-using-hashmap-without-using-synchronized-collection/)
* [JakeWharton/DiskLruCache](https://github.com/JakeWharton/DiskLruCache)