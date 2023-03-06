# Text Provider

The code provides the `TextProvider` class which serves lines from a text file. The class provides
the `getLine()` method which takes as an input the line number to be served and returns the content
of the line as a string. Line numbering starts from 1. If invalid line numbers are passed to the
method (line numbers less than 1 or larger than the largest line number in the file)
the `TextProviderException` is raised.

The code also includes a simple app that makes use of the `TextProvider`. The app reads the command
line arguments, **assumes** the first one is the text file name, and the rest are line numbers that
are served from the input file. The app splits the line numbers into two lists for even and odd line
numbers and serves each list on a separate thread.

## Getting Started

The code is in jdk 19 and can be built with maven (Apache Maven 3.8.6).

To build the code:

```shell
$ ./build.sh
```

To run the app, with some input text file and a set of line numbers, execute:

```shell
$ ./runme.sh <input.txt> <line-num-1> <line-num-2> ... <line-num-n>
```

All output is logged in to a rotating log file under the `logs/` directory.

For example, the text file `example/in.txt` contains 1000 line of text:

```shell
$ ./runme.sh example/in.txt 100 101 99 1 2 3 -1 1001
```

```shell
$ cat logs/textprovider.log 
2021-10-15 12:11:31.402 [INFO] TextProviderApp - 101 --> [101: this is line 101]
2021-10-15 12:11:31.402 [INFO] TextProviderApp - 100 --> [100: this is line 100]
2021-10-15 12:11:31.405 [INFO] TextProviderApp - 99 --> [99: this is line 99]
2021-10-15 12:11:31.405 [INFO] TextProviderApp - 2 --> [2: this is line 2]
2021-10-15 12:11:31.405 [INFO] TextProviderApp - 1 --> [1: this is line 1]
2021-10-15 12:11:31.405 [INFO] TextProviderApp - 3 --> [3: this is line 3]
2021-10-15 12:11:31.405 [ERROR] TextProviderApp - line -1 failed: line number should be greater than 0
2021-10-15 12:11:31.405 [INFO] TextProviderApp - even list failures: 0
2021-10-15 12:11:31.428 [ERROR] TextProviderApp - line 1001 failed: line 1001 does not exist in the file
2021-10-15 12:11:31.429 [INFO] TextProviderApp - odd list failures: 2
```

## `TextProvider` Structure

The `TextProvider` class is composed of an LRU cache and a text file accessor component. In each
call to `getLine()`, first the cache is checked to see if the line is already in the cache, and in
that case the line is served from the cache. In case of a cache miss, the text file accessor is used
to retrieve the requested line from the file. If the line is retrieved successfully, the cache is
updated and the content of the line is returned. In case of a failure (either because an invalid
line number is requested, or reading from file has failed), a `TextProviderException` is raised to
inform the user.

The **LRU cache** is implemented using the jdk-provided `LinkedHashMap` collection, wrapped
in `Collections.synchronizedMap()`. The cache maps line numbers to line contents and has a default
capacity of `1024 * 1024` entries.

The **text file accessor** is implemented in the `TextFileAccessor` class, and it is the component
that is accessing the text file. It maintains a state through a `ConcurrentNavigableMap` which maps
line numbers to their corresponding byte offset in the text file. The map is initialized with the
pair `(1, 0)` indicating that the first line is at the beginning of the file. Calls to the `read()`
method return the content of the line specified in the argument. If the line number is in the map,
then the corresponding byte offset is used to read the line from the text file using
a `RandomAccessFile` object. If the line number is not in the map, then the largest line number in
the map, which is less than the requested line number, is used to sequentially read lines from the
file until the requested line is read or the end of file is reached. During this sequential read,
the map is updated to improve performance for subsequent requests. If the end of file is reached and
no line has been found with the requested line number, `null` is returned.

## Unit Tests

The three main components, `TextProvider`, `LruCache` and `TextFileAccessor` have a small set of
unit tests to test their basic functionality.
