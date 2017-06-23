[![Build Status](https://travis-ci.org/memoizr/retro-optional.svg?branch=master)](https://travis-ci.org/memoizr/retro-optional)
[![codecov](https://codecov.io/gh/memoizr/retro-optional/branch/master/graph/badge.svg)](https://codecov.io/gh/memoizr/retro-optional)
[![](https://jitpack.io/v/memoizr/retro-optional.svg)](https://jitpack.io/#memoizr/retro-optional)
[![GitHub license](https://img.shields.io/github/license/kotlintest/kotlintest.svg)](http://www.apache.org/licenses/LICENSE-2.0.html) 
## Retro-Optional
A backport of Java8 monad optionals for Java7.

Example:
Let's say we have an instance of a class `A` which can be nullable, and we're interested in getting the
result of a chain of nested function calls, which eventually will result in a `boolean`. Every intermediate
step may fail and return a null. To get to the end result without optionals we'd have to do multiple
null checks:

```java
boolean result = a != null &&
      a.getB() != null &&
      a.getB().getC() != null &&
      a.getB().getC().isD();
```
But by converting everything to return monadic optionals, the syntax can be streamlined a bit:
```java
boolean result = a.flatMap(A::getB)
      .flatMap(A::getC)
      .filter(x -> x.isD())
      .isPresent();
```
For more examples of how to use Java 8 optional types, you can refer to this excellent article:
http://www.nurkiewicz.com/2013/08/optional-in-java-8-cheat-sheet.html

## Get it
```groovy
 repositories {
        // ...
        maven { url "https://jitpack.io" }
  }
```
```groovy
 dependencies {
        compile 'com.github.memoizr:retro-optional:0.2.0'
  }
```

Copyright 2015 memoizr

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
    implied.
    See the License for the specific language governing permissions and
    limitations under the License.
