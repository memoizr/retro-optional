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
        compile 'com.github.User:Repo:Tag'
  }
```


