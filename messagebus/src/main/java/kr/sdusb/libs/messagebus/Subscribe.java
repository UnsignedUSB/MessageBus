package kr.sdusb.libs.messagebus;

/**
 *
 * Created by Yoosub-Song on 2017-06-23 PM 1:46.
 */
public @interface Subscribe {
    @ThreadType int thread() default ThreadType.CURRENT;
    int[] events();
    int priority() default Integer.MAX_VALUE;
}
