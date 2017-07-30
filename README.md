# MessageBus
MessageBus is an Android library that helps you send and receive messages between each component during Android development. MessageBus class is generated at Build time. Because this library is an Annotation Processor. That's why MessageBus is the fastest.</br>
<img width="588" height="156" src="/doc/MessageBus_flow.png"/></br>

- MessageBus's inteface is looks like [EventBus](https://github.com/greenrobot/EventBus).</br>
But MessageBus uses integer Message.</br>So, it can be used more lightly than EventBus.

## How to use MessageBus
Use jcenter</br>
<pre><code>buildscript {
    repositories {
        jcenter()
    }
}</code></pre>
Via Gradle:</br>
<pre><code>compile 'com.github.UnsignedUSB:MessageBus:0.7.5'</code></pre>
