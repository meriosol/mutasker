MuTasker
===

Inception year: <tt>2014</tt>

## Overview
This library(stronger speaking - framework) was created for multi task launching using Java Concurrency packages for thread handling.
At least in current state the library is used for container-less load tests.
While performing tests a system gathers statistics about execution time, calculates SD(Standard Deviation), mean etc.
It allows testers to detect bottlenecks and deviations on early stages of load testing
(and then likely "dive" into heavier weight performance monitoring systems / profilers for further investigation).

### Major classes
Main class in this library is <tt>TaskPooledExecutor</tt>. The most important fragment is:

```Java
        List<Future<Result<T>>> futureWorkResults = new ArrayList<>(taskAmount);
        ExecutorService executor = Executors.newFixedThreadPool(taskAmount);

        for (int i = 0; i < taskAmount; i++) {
            Task task = tasks.get(i);
            CallableTask callableTask = new CallableTask(task);
            Future<Result<T>> future = executor.submit(callableTask);
            futureWorkResults.add(future);
        }
```

<tt>Task</tt> is extremely basic interface:
```Java
public interface Task<T> {
    Result<T> execute();
}
```

An interface <tt>Result</tt> holds work results and major statistics of finished task.
For convenience <tt>NamedMeasurableTaskResult</tt> implementation can be used in the most cases.

A fragment of stats:
```
   88>> ===============================================V:
   88>> Results basic stats for durations (work unit name 'emulUnit'):
      88>> Total amount of: '300' results; '3000' units_of_work; '30475133' output_items; total_duration = '4254' msecs (4 seconds).
      88>>   Duration/OutputItem: MEAN(AVG) = '0.0' msecs (note: unreliable value cause some items could be skipped).
      88>>   Duration/UnitOfWork: MEAN(AVG) = '14.1' msecs.
      88>>   Duration/UnitOfWork: SD        = '21.4' msecs ('151.4' pct from AVG).
      88>>   Duration/UnitOfWork: SD MEAN   = '21.2' msecs ('150.3' pct from AVG).
      88>>   1st the fastest/unit of work result: '10' work units; '11783' output items; total_duration = '2' msecs(0 seconds);
                duration/unit = '0' msecs; duration/output = '0' msecs; started at '2014-03-11T02:39:40.691-04:00'; name = 'WorkEmulator'.
      88>>   1st the slowest/unit of work result: '10' work units; '187093' output items; total_duration = '1114' msecs(1 second);
                duration/unit = '111' msecs; duration/output = '0' msecs; started at '2014-03-11T02:39:40.817-04:00'; name = 'WorkEmulator'.
   88>> ===============================================^
```

## Build and test
Maven 3 is used to build the project. To perform compile/test/jar use:
```
mvn -e clean package
```

## Logging
SLF4J API is used for logging. Log4J is provided as default impl in tests.

## Notable dependencies
<tt>Apache Commons</tt> and <tt>Joda Time</tt> (along with SLF4J) are the only notable external dependencies.
Notice also code has JDK7 specific syntax in some places(so you need JavaSE7 if you want to utilize this library).

## Future plans/ideas
To be more machine readable results report formatted in e.g. XML or JSON could be handy.
The Result can also gather memory statistics.
JMX can be used for stronger "enterprise readiness" (maybe the whole library can be rewritten using JMX).
In the end main priority is to provide minimal impact from observation and to show relevant statistics data.

