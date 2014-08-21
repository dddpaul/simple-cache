simple-cache
===========

Simple two-level cache written in Java.

It's not thread-safe, it hasn't got outstanding performance or smallest memory usage.
Anyway it's just a sample bunch of classes.

See [SampleApp](blob/master/src/SampleApp.java) for usage example. 

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