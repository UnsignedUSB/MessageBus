package kr.sdusb.libs.messagebus;

/**
 *
 * Created by Yoosub-Song on 2017-06-23 PM 1:46.
 */
public @interface Subscribe {
    int[] groups() default 0;
    int[] events();
    @ThreadType int thread() default ThreadType.CURRENT;
    int priority() default Integer.MAX_VALUE;
    boolean ignoreCastException() default false;
    boolean withEventType() default false;
}
