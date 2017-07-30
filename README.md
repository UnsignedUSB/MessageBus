# MessageBus
MessageBus is an Android library that helps you send and receive messages between each component during Android development. MessageBus class is generated at Build time. Because this library is an Annotation Processor. That's why MessageBus is the fastest.</br>
<img width="588" height="156" src="/doc/MessageBus_flow.png"/></br>

- MessageBus's inteface is looks like [EventBus](https://github.com/greenrobot/EventBus).</br>
But MessageBus uses integer Message.</br>So, it can be used more lightly than EventBus.

Ready to use MessageBus
----------
Use jcenter</br>
   ```
   buildscript {
       repositories {
           jcenter()
       }
   }
   ```
Via Gradle :</br>
   ```
   compile 'com.github.UnsignedUSB:MessageBus:0.7.5'
   ```

How to use MessageBus
----------
1. Define messages : 
   ```java
   public static final int MESSAGE_TEXT_UI_THREAD = 0x00000001;
   public static final int MESSAGE_TEXT_WORKER_THREAD = 0x00000002;
   ```
2. Register subscriber :
   ```java
   public SampleFragment2() {
       MessageBus.getInstance().register(this);
   }
   ```
3. Prepare subscriber methods :
   ```java
   @Subscribe(events = SampleFragment1.MESSAGE_TEXT_UI_THREAD)
   public void onMessageChanged_UIThread(String message) {
       // Do Something
   }

   @Subscribe(events = SampleFragment1.MESSAGE_TEXT_WORKER_THREAD, thread = Subscribe.Thread.MAIN)
   public void onMessageChanged_WorkerThread(String message) {
       // Do Something
   }
   ```
4. Send Message :
   ```java
   MessageBus.getInstance().handle(MESSAGE_TEXT_UI_THREAD, null);
   MessageBus.getInstance().handle(MESSAGE_TEXT_UI_THREAD, text);
   ```
5. Unregister subscriber :
   ```java
   @Override
   public void onDestroy() {
       ...
       MessageBus.getInstance().unregister(this);
   }
   ```
License
------------
MessageBus binaries and source code can be used according to the [Apache License, Version 2.0](LICENSE).
