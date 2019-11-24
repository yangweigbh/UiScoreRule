package com.github.yangweigbh.uiscore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a test as having a performance component to it.
 *
 * TODO: Perhaps add a timeout parameter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PerfTest {

}
