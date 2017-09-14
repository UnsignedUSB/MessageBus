# MessageBus
MessageBus is an Android library that helps you send and receive messages between each component for Android development. MessageBus class is generated at Build time, because this library is an Annotation Processor. That's why MessageBus is the fastest.</br>
<img width="500" height="150" src="/doc/MessageBus_flow.png"/></br>

- MessageBus's inteface is looks like [EventBus](https://github.com/greenrobot/EventBus).</br>
But MessageBus uses integer Messages.</br>So, it can be used more lightly than EventBus.

Ready to use MessageBus
----------
Write following code in your project build.gradle.
   ```
   buildscript {
       repositories {
           jcenter()
       }
   }
   ...  
   allprojects {
       repositories {
           jcenter()
       }
   }
   ```
Write following code in your app build.gradle.
   ```
   compile 'com.github.UnsignedUSB:MessageBus:0.9.0'
   ```

How to use MessageBus
----------
1. Define messages : 
   ```java
   public static final int MESSAGE_TEXT_UI_THREAD = 0x00000001;
   public static final int MESSAGE_TEXT_WORKER_THREAD = 0x00000002;
   ```
2. Register subscribers :
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
   
   @Subscribe(events = {SampleFragment1.MESSAGE_TEXT_WORKER_THREAD, SampleFragment1.MESSAGE_TEXT_UI_THREAD}, thread = Subscribe.Thread.MAIN)
   public void onMessageReceived() {
       Toast.makeText(this, "Message Received", Toast.LENGTH_SHORT).show();
   }
   ```
4. Send Messages :
   ```java
   MessageBus.getInstance().handle(MESSAGE_TEXT_UI_THREAD, null);
   MessageBus.getInstance().handle(MESSAGE_TEXT_UI_THREAD, text);
   ```
5. Unregister subscribers :
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
