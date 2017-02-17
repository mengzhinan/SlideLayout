# SlideLayout
a simple slide layout library,just one java class file.  
一个侧滑删除的库和demo，仅仅只是一个自定义控件搞定，提供了普通用法和listview，recyclerview用法实例。  
关于滑动或touch功能，onTouchEvent方法可以搞定。但是本例没有这么复杂，借助ViewDragHelper工具类处理。  
本例提供4种滑动效果：  
* options隐藏菜单跟随content布局滑动  
* options隐藏菜单固定在content底部，仅由content布局滑动  
* 从左侧滑出  
* 从右侧滑出  
仅仅以上4种效果，代码逻辑就开始有点复杂，故没有考虑options和content布局从上下两侧滑动。  

==============
#效果图  
![](https://github.com/mengzhinan/SlideLayout/blob/master/demo.gif)  
#部分代码:  
##activity_main.xml:

    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:slide="http://schemas.android.com/apk/res-auto"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">
    
        <com.duke.slidelayout.SlideLayout
            android:id="@+id/slideLayout"
            android:layout_width="match_parent"
            android:layout_height="60dp"
            slide:canDrag="true"
            slide:contentResId="@layout/item_content"
            slide:optionsLayoutPosition="follow"
            slide:optionsResId="@layout/item_options"
            slide:slideDirection="right" />
        ...
    </LinearLayout>

-

slide:contentResId="@layout/item_content" 内容布局，即上层布局  
slide:optionsResId="@layout/item_options" 操作菜单布局，即下层布局

=

##MainActivity.java:  



    public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SlideLayout.OnOptionsStateChangeListener, SlideLayout.OnChildClickListener {  
        private SlideLayout slideLayout;  
        private Button setSlideDirection1, setSlideDirection2, setOptionsLayoutPosition1,  
        setOptionsLayoutPosition2, setOpenedfalse, setOpenedtrue;  
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            slideLayout = (SlideLayout) findViewById(R.id.slideLayout);
            slideLayout.setOnOptionsStateChangeListener(this);
            slideLayout.setOnChildClickListener(this);
    
            setSlideDirection1 = (Button) findViewById(R.id.setSlideDirection1);
            setSlideDirection2 = (Button) findViewById(R.id.setSlideDirection2);
            setOptionsLayoutPosition1 = (Button) findViewById(R.id.setOptionsLayoutPosition1);
            setOptionsLayoutPosition2 = (Button) findViewById(R.id.setOptionsLayoutPosition2);
            setOpenedfalse = (Button) findViewById(R.id.setOpenedfalse);
            setOpenedtrue = (Button) findViewById(R.id.setOpenedtrue);
            setSlideDirection1.setOnClickListener(this);
            setSlideDirection2.setOnClickListener(this);
            setOptionsLayoutPosition1.setOnClickListener(this);
            setOptionsLayoutPosition2.setOnClickListener(this);
            setOpenedfalse.setOnClickListener(this);
            setOpenedtrue.setOnClickListener(this);
        }
    
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.setSlideDirection1:
                    slideLayout.setSlideDirection(SlideLayout.SlideDirection.LEFT);
                    break;
                case R.id.setSlideDirection2:
                    slideLayout.setSlideDirection(SlideLayout.SlideDirection.RIGHT);
                    break;
                case R.id.setOptionsLayoutPosition1:
                    slideLayout.setOptionsLayoutPosition(SlideLayout.OptionsLayoutPosition.FIXED);
                    break;
                case R.id.setOptionsLayoutPosition2:
                    slideLayout.setOptionsLayoutPosition(SlideLayout.OptionsLayoutPosition.FOLLOW);
                    break;
                case R.id.setOpenedfalse:
                    slideLayout.setOpened(false);
                    break;
                case R.id.setOpenedtrue:
                    slideLayout.setOpened(true);
                    break;
            }
        }
    
        @Override
        public void onOptionsViewChange(boolean isOpened) {
            Toast.makeText(this, isOpened ? "菜单打开" : "菜单关闭", Toast.LENGTH_SHORT).show();
        }
    
        @Override
        public void onChildClick(View view) {
            String string = "";
            if (view.getId() == R.id.content_root) {
                string = "内容点击";
            } else if (view.getId() == R.id.content_text) {
                TextView textView = (TextView) view;
                string = textView.getText().toString();
            } else if (view.getId() == R.id.options_root) {
                string = "操作菜单点击";
            } else if (view.getId() == R.id.tv_open) {
                TextView textView = (TextView) view;
                string = textView.getText().toString();
            } else if (view.getId() == R.id.tv_delete) {
                TextView textView = (TextView) view;
                string = textView.getText().toString();
            }
            Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
        }
    }
    
-

无论是content布局控件还是options布局控件的点击事件，我不清楚各自的id。  
我统一都用SlideLayout.OnChildClickListener接口提供给您，具体是哪个点击了，请在回调中判断id，再做处理。  
参考public void onChildClick(View view) 方法；

=====

至于在listview和recyclerview中的用法，请参考github库中的demo：  
./demo/SlideLayout_listview  
./demo/SlideLayout_recyclerview  
###下面贴出效果图：
![](https://github.com/mengzhinan/SlideLayout/blob/master/listview.gif)    

-

![](https://github.com/mengzhinan/SlideLayout/blob/master/recyclerview.gif)    


更多资料请关注[我的博客](http://blog.csdn.net/fesdgasdgasdg?viewmode=contents)


