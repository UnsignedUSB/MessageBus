**Android Event 전달 Library MessageBus**를 소개한번 드려보려고 합니다. 

사실 비슷한 라이브러리로 많은 분들이 알고 계시는 **EventBus**가 있는데요.
작년에 한번 검토해보니, 제 생각에 조금 더 심플한 방법이 있을 것 같아서 해보다가 
이렇게 MessageBus라고 Library를 만들게 되었습니다.. 

## 1. 기본 설명
-----
 - **MessageBus Github** : https://github.com/UnsignedUSB/MessageBus
 - **MessageBus는 Annotation Processor를 이용해서 Build time에 Message를 전달할 Code를 생성해 주는 Library**입니다.
 - Event를 Object형태로 던지는 EventBus와는 달리, **MessageBus는 Integer 형태**로 던지도록 되어있습니다.
 - **EventBus대비 11배~13배 정도 빠른 이벤트 전달 성능**을 보여줍니다.
   (참고 : http://blog.unsignedusb.com/2017/08/android-eventbus-messagebus.html)


## 2. 사용방법
----
1. **시작하기**
	- Gradle
	  > compile 'com.github.UnsignedUSB:MessageBus:0.9.9.4-Java8'
	  
	  Application Gradle에서 위의 내용을 추가해 주시면 됩니다. 
	  추가로 Annotation Processor를 사용하기 위해 아래의 내용도 추가가 필요합니다. 
	  ![1.jpg](/files/0a705587-61bc-1688-8161-c237f39c4392)


</br>

2. **적용하기 (기본)**
	- Message 전달
	Message를 던지는 방법은 아래와 같습니다.
	![2.jpeg](/files/0a705587-61bc-1688-8161-c237f3774389)
    조금 더 확실하게 보기 위해 handle()을 살펴보면 아래와 같습니다.
	![3.jpg](/files/0a705587-61bc-1688-8161-c237f35a4384)
    what : 이벤트 식별자
	data : 전달하고자 하는 Data Class (이벤트만 전달하고자 할 때는 null)
 </br>

	- Message 수신
	Message수신은 어떤 Java Object라도 가능하도록 되어있습니다.
	**1단계 : MessageBus에 Register**
	![MessageBus Register.jpg](/files/0a705587-61bc-1688-8161-c237f3674387)
	**2단계 : Register된 Class 내부에서 @Subscriber Method 정의**
	![MessageBus_Subscribe.jpg](/files/0a705587-61bc-1688-8161-c237f37c438a)
    여기까지 했을 때, Message를 수신할 수 있습니다.
</br>
	
	- MessageBus의 Unregister
	더 이상 Message 수신이 필요없어질 때는 아래와 같이 처리할 수 있습니다.
	![MessageBus unregister.jpg](/files/0a705587-61bc-1688-8161-c237f3664385)

## 3. @Subscribe 세부사항
----

![4.jpg](/files/0a705587-61bc-1688-8161-c237f385438d)

1. **int[] groups**   (Default = 0)
	**Message를 일부에만 전달하고 싶을 때, group을 지정**할 수 있습니다. 
	![Subscribe group ex.jpg](/files/0a705587-61bc-1688-8161-c237f37f438b)
    group을 지정한 경우, Message를 전달하는 시점에 아래와 같이 Group 지정을 해서 전달해야 합니다. 
	![1519378631448.jpg](/files/0a705587-61bc-1688-8161-c237f3444380)
	![group handle.jpg](/files/0a705587-61bc-1688-8161-c237f35a4383)

2. **int[] events** <font color=red>**(필수)**</font>
	수신하고자 하는 Message들을 넣을 수 있습니다. 하나의 Method에서 여러 Message를 수신하고 싶을때 Array 형태로 등록할 수 있습니다. 
   ![다중 이벤트.jpg](/files/0a705587-61bc-1688-8161-c237f39b4391)
</br>

3. **int thread** (Default = ThreadType.CURRENT)
	**Message를 수신할 때, Thread를 정할 수 있습니다.**
	Thread는 MAIN, CURRENT, NEW 가 있습니다.
    ex. ThreadType.MAIN으로 설정하면, 해당 Method가 Main Thread에서 실행됨을 보장
    ![1519380142946.jpg](/files/0a705587-61bc-1688-8161-c237f34c4381)
</br>

4. **int priority** (Default = Integer.MAX_VALUE)
    **동일한 Message를 수신하는 Method가 여러개 있을 때, 그 순서를 지정할 때 사용**됩니다.
	priority가 낮을수록 먼저 호출됩니다.
   ![priority.jpg](/files/0a705587-61bc-1688-8161-c237f37f438c)
</br>

5. **boolean ignoreCastException** (Default = false)
   Message를 수신할 때, 함께 넘어온 Data가 있다면, Casting해서 수신하게 되는데, 
   만약 거기서 Casting Exception이 발생했다면, Crash가 발생하게 됩니다. 
   이때 ignoreCastException=true 로 넣어준다면, 해당 Method 호출시 Crash가 발생하지 않고, 넘어가게 됩니다.
   보통은 개발단계에서 Crash가 나는 것이 정상 동작을 보장하지만, 
   통계 등의 중요도가 낮은 작업을 수행할 때 사용될 수 있습니다. 
   ![ignore.jpg](/files/0a705587-61bc-1688-8161-c237f36c4388)
   위와 같이 작성한 경우 실제 MessageBus.java에 생성된 코드는 아래와 같습니다.
   ![1519380900429.jpg](/files/0a705587-61bc-1688-8161-c237f3514382)

</br>

6. **boolean withEventType** (Default = false)
   하나의 Method에서 여러 이벤트를 수신하게 될 때, 
   "**어떤 이벤트로 인해 해당 Method가 호출되었는지**" 필요할 때가 있습니다. 
   이때 withEventType=true로 주게 되면, 어떤 Message로 Method 호출이 되었는지 
   Method의 1번 Parameter로 넘겨주게 됩니다. 
   <font color=red>**즉, withEventType=true일 경우는 반드시 Method의 1번 Param은 int형이어야 합니다.**</font>
   (수신하는 Data 는 2번 Param에 위치..)
   ![withEventType.jpg](/files/0a705587-61bc-1688-8161-c237f38a438f)
</br>
## 4. @ListSubscriber
----
MessageBus의 기본 구조상, 하나의 클래스를 여러 인스턴스로 만들어서 사용하는 경우, 모든 인스턴스가 Message를 받지 못합니다.
그럴 때, **인스턴스를 List로 관리할 수 있게 해주는 역할을 하는 것이 @ListSubscriber**입니다.
사용방법은 아래와 같이 간단합니다.
![ListSubscriber.jpg](/files/0a705587-61bc-1688-8161-c237f3664386)
**하나의 클래스가 여러 인스턴스로 만들어 지는경우, 
해당 클래스 최상단에 @ListSubscriber 라고 Annotation만 달아주면, 
MessageBus 내부에서 List로 관리하게 됩니다.**
</br>

## 5. 참고 사항
----
1. **MessageBus.java 코드는 빌드타임에 생성**됩니다.
2. MessageBus 최초 적용시에는 MessageBus.java가 생성되어있지 않고, 
    **최소 1개의 @Subscribe Method가 존재해야 그때부터 Class가 생성**됩니다.
    : 이 부분은 현재 개선중에 있습니다.
3. 여타 다른 이유로 빌드가 실패한 경우, MessageBus.java 파일이 없다는 에러가 가장 먼저 뜰 수 있습니다.
    하지만 대부분의 경우 MessageBus 외부에서 발생한 에러로 인해 Code가 생성되지 못해 발생한 문제로,
    Error Message를 맨 아래로 내리면 진짜 문제를 발견하실 수 있습니다.
    (물론... MessageBus의 문제일수도 있습니다. 그런경우 제보 주시면 빠르게 해결하겠습니다 ㅎㅎ..)
4. 현재 실 서비스되는 App에서 Message를 몇천개 가량 사용하고 있는데, 
    이때 **MessageBus의 생성시간은 100~200ms 정도 수준**입니다. 
    MessageBus의 사용이 빌드 속도에 크게 영향을 주진 않습니다.
</br>

여기까지 MessageBus Library에 대해서 소개 드려봤습니다.
Github에도 Library가 업데이트 됨에 따라 문서를 업데이트 했어야 했는데,
미루다가 이번에 한번 이곳에 작성해봤습니다. 

관심있으신 분께서는 한번 검토해보셔도 좋지 않을까 싶습니다.
다양한 의견 주시면 감사하겠습니다.

읽어주셔서 감사합니다.
