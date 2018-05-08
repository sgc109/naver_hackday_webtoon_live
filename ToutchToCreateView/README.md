# 사용자 입력을 위한 View 예제입니다.
MainActivity 위에 TouchView 가 올라와있는 구조입니다.

## TouchView
- GestureDetector 를 통하여 longPress 이벤트를 받아줍니다.  이때 InputControllerView 를 TouvhView의 childView 로 등록해줍니다.
- onTouchEvent 를 통해 다른 입력 이벤트를 감지합니다. 
- MotionEvent.ACTION_MOVE 에서 pointer의 움직임을 보여줍니다.
- MotionEvent.ACTION_UP 에서 TouvhView의 childView중 InputControllerView 를 제거해줍니다. 
- TouvhView 생성시 InputControllerView를 한번만 생성해 주고, TouvhView의 child에 등록해주고 제거해주는 방식으로 instance를 새로 만들지 않고 재사용합니다.


## InputControllerView
- background 와 pointer 로 나뉘어집니다.
- 해당 InputControllerView는 상위 View인 TouchView 에 의해 동작하며, LongPress 와 ACTION_UP 사이에서 TouchView 의 child로써 화면에 보여집니다.


## 사용법
아래 파일들을 프로젝트에 추가하시고 원하시는 layout의 위치에 TouchView 를 삽입하시면 됩니다.
- java
    InputControllerView.java
    TouchView.java

- values
    dimens.xml

- drawable
    circle_background_down.xml
    circle_background_left.xml
    circle_background_normal.xml
    circle_background_right.xml
    circle_background_up.xml
