package kr.sdusb.libs.messagebus;

/**
 *
 * Created by Yoosub-Song on 2017-06-23 PM 1:46.
 */
public @interface Subscribe {
    @Thread int thread() default Thread.CURRENT;
    int[] events();

    @interface Thread{
        int MAIN = 0;
        int CURRENT = 1;
    }
}
