# UiScoreRule

60fps is critical metrics that Ui render smoothly for the end user, so it's important to monitor the ui performance in our automation Ui test. UiScoreRule is an android instrumentation test rule to measure ui smoothness, the test rule will collect the frame stats during the test run, and output the score calculated based on [this](http://androidxref.com/9.0.0_r3/xref/frameworks/base/tests/JankBench/app/src/main/java/com/android/benchmark/results/UiBenchmarkResult.java#125)

###

## Setup:

add it in your root `build.gradle`

```java

allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
in your app `build.gradle`

```java

android {
    ...
    defaultConfig {
        ...
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments 'listener': 'com.github.yangweigbh.uiscore.TestListener'
    }
}

dependencies {
    androidTestImplementation 'com.github.yangweigbh:UiScoreRule:1.1'
}

```

## Usage

In the Instrumentation test, add UiScoreRule in your test class

```java
public class MainActivityTest {
    @Rule
    public UiScoreRule mUiScoreRule = new UiScoreRule();

    ....
}
```

Annotate `@PerfTest` to the test that you want to measure ui score

```java
public class MainActivityTest {
    @Rule
    public UiScoreRule mUiScoreRule = new UiScoreRule();

    @Test
    @PerfTest
    public void scrollRecyclerView() {
        .....
    }
}
```
the output will be print into logcat like below, you can use UiScore TAG to filter in the logcat:

```
2019-11-24 22:13:14.821 20850-20941/com.github.yangweigbh.uiscorerule I/UiScore: Result: 
    ┌───────────────────────────────────┬────────────┬──────────────────────┬───────────────────┬─────────────────────────┬─────────────────┐
    │             Test Name             │Total Frames│Average frame duration│Frame duration 99th│Bad frames standDeviation│      Score      │
    ├───────────────────────────────────┼────────────┼──────────────────────┼───────────────────┼─────────────────────────┼─────────────────┤
    │MainActivityTest.scrollRecyclerView│    4344    │         9.04         │       21.17       │          40.61          │       96        │
    ├───────────────────────────────────┼────────────┼──────────────────────┼───────────────────┼─────────────────────────┼─────────────────┤
    │               Total               │            │                      │                   │                         │95.99999999999999│
    └───────────────────────────────────┴────────────┴──────────────────────┴───────────────────┴─────────────────────────┴─────────────────┘

```