package com.duke.slidelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * @Author: duke
 * @DateTime: 2016-12-25 20:50
 * @Description: 侧滑操作布局
 */
public class SlideLayout extends ViewGroup {
    private View optionsView;//item的滑动操作选项view
    private int optionsResId;
    private View contentView;//item的内容view
    private int contentResId;

    //记录滑动view上次的left值，以便根据此次的left判断滑动方向
    private int movedLengthTemp;
    private boolean slideDirectionRight;//记录滑动方向
    //滑动阀值
    private static final int THRESHOLD_MOVED_VALUE = 25;//px
    private int thresholdMoved = THRESHOLD_MOVED_VALUE;//px

    private int contentBgColor = Color.WHITE;//内容部分背景颜色
    private int contentBgResId;//内容部分背景资源id
    private boolean hasContentBgColor;//是否设置了content背景颜色

    //滑动方向：【从左侧、从右侧】
    private int slideDirection = SlideDirection.RIGHT.getIntValue();
    //操作菜单view摆放方式：【固定，跟随】
    private int optionsLayoutPosition = OptionsLayoutPosition.FIXED.getIntValue();

    private boolean isOpened = false;//是否有滑动出来的item
    private boolean canDrag = true;//是否可以拖拽
    private ViewDragHelper helper;//拖拽辅助类
    private OnOptionsStateChangeListener onOptionsStateChangeListener;
    private OnChildClickListener onChildClickListener;

    public void setOnChildClickListener(OnChildClickListener l) {
        this.onChildClickListener = l;
    }

    public void setOnOptionsStateChangeListener(OnOptionsStateChangeListener l) {
        this.onOptionsStateChangeListener = l;
    }

    public View getOptionsView() {
        return optionsView;
    }

    public View getContentView() {
        return contentView;
    }

    public int getThresholdMoved() {
        return thresholdMoved;
    }

    /**
     * 设置滑动阀值
     *
     * @param thresholdMovedDP dp or dp of dimens
     */
    public void setThresholdMoved(int thresholdMovedDP) {
        this.thresholdMoved = getContext().getResources().getDimensionPixelSize(thresholdMoved);
    }

    public int getSlideDirection() {
        return slideDirection;
    }

    public int getOptionsLayoutPosition() {
        return optionsLayoutPosition;
    }

    public boolean isCanDrag() {
        return canDrag;
    }

    public void setCanDrag(boolean canDrag) {
        this.canDrag = canDrag;
        setOpened(false);
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOptionsView(View optionsView) {
        this.optionsView = optionsView;
        addChildren();
        requestLayout();
    }

    public void setOptionsResId(int optionsResId) {
        this.optionsResId = optionsResId;
        try {
            this.optionsView = inflateChild(optionsResId);
            setOptionsView(this.optionsView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
        addChildren();
        requestLayout();
    }

    public void setContentResId(int contentResId) {
        this.contentResId = contentResId;
        try {
            this.contentView = inflateChild(contentResId);
            setContentView(this.contentView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setContentBgColor(int contentBgColor) {
        this.contentBgColor = contentBgColor;
        hasContentBgColor = true;
        if (contentView != null) {
            contentView.setBackgroundColor(contentBgColor);
        }
    }

    public void setContentBgResId(int contentBgResId) {
        this.contentBgResId = contentBgResId;
        if (this.contentBgResId > 0) {
            contentView.setBackgroundResource(this.contentBgResId);
        }
    }

    public void setSlideDirection(SlideDirection slideDirection) {
        this.slideDirection = slideDirection.getIntValue();
        requestLayout();
    }

    public void setOptionsLayoutPosition(OptionsLayoutPosition optionsLayoutPosition) {
        this.optionsLayoutPosition = optionsLayoutPosition.getIntValue();
        requestLayout();
    }

    public void setOpened(boolean opened) {
        if (!canDrag) {
            opened = false;
        }
        if (isOpened != opened) {
            isOpened = opened;
            int optionsViewLeft = 0;
            int contentViewLeft = 0;
            if (optionsLayoutPosition == OptionsLayoutPosition.FOLLOW.getIntValue()) {
                //操作布局跟随在item的内容布局后面
                if (slideDirection == SlideDirection.LEFT.getIntValue()) {
                    if (isOpened) {
                        optionsViewLeft = 0;
                        contentViewLeft = optionsView.getMeasuredWidth();
                    } else {
                        optionsViewLeft = -optionsView.getMeasuredWidth();
                        contentViewLeft = 0;
                    }
                } else if (slideDirection == SlideDirection.RIGHT.getIntValue()) {
                    if (isOpened) {
                        optionsViewLeft = contentView.getMeasuredWidth() - optionsView.getMeasuredWidth();
                        contentViewLeft = -optionsView.getMeasuredWidth();
                    } else {
                        optionsViewLeft = contentView.getMeasuredWidth();
                        contentViewLeft = 0;
                    }
                }
            } else if (optionsLayoutPosition == OptionsLayoutPosition.FIXED.getIntValue()) {
                //固定操作布局
                if (slideDirection == SlideDirection.LEFT.getIntValue()) {
                    optionsViewLeft = 0;
                    if (isOpened) {
                        contentViewLeft = optionsView.getMeasuredWidth();
                    } else {
                        contentViewLeft = 0;
                    }
                } else if (slideDirection == SlideDirection.RIGHT.getIntValue()) {
                    optionsViewLeft = contentView.getMeasuredWidth() - optionsView.getMeasuredWidth();
                    if (isOpened) {
                        contentViewLeft = -optionsView.getMeasuredWidth();
                    } else {
                        contentViewLeft = 0;
                    }
                }
            }
            //注意下两行代码先后顺序
            helper.smoothSlideViewTo(optionsView, optionsViewLeft, 0);
            helper.smoothSlideViewTo(contentView, contentViewLeft, 0);
            //重绘，在view的onDraw里面会调用computeScroll()方法
            invalidate();
            if (onOptionsStateChangeListener != null) {
                onOptionsStateChangeListener.onOptionsViewChange(isOpened);
            }
        }
    }

    public SlideLayout(Context context) {
        this(context, null, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout, defStyleAttr, 0);
        int size = array.getIndexCount();
        for (int i = 0; i < size; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.SlideLayout_contentResId) {//获取内容布局
                contentResId = array.getResourceId(attr, -1);
            } else if (attr == R.styleable.SlideLayout_optionsResId) {//获取操作布局
                optionsResId = array.getResourceId(attr, -1);
            } else if (attr == R.styleable.SlideLayout_slideDirection) {//获取item布局的滑动方向
                slideDirection = array.getInt(attr, SlideDirection.RIGHT.getIntValue());
            } else if (attr == R.styleable.SlideLayout_optionsLayoutPosition) {//获取item的操作布局摆放位置
                optionsLayoutPosition = array.getInt(attr, OptionsLayoutPosition.FOLLOW.getIntValue());
            } else if (attr == R.styleable.SlideLayout_contentBgColor) {//内容部分背景颜色
                contentBgColor = array.getColor(attr, Color.WHITE);
                hasContentBgColor = true;
            } else if (attr == R.styleable.SlideLayout_contentBgResId) {//内容部分背景资源id
                contentBgResId = array.getResourceId(attr, -1);
            } else if (attr == R.styleable.SlideLayout_canDrag) {//是否可以拖拽
                canDrag = array.getBoolean(attr, true);
            } else if (attr == R.styleable.SlideLayout_thresholdMoved) {//滑动最小阀值
                thresholdMoved = array.getDimensionPixelSize(attr, THRESHOLD_MOVED_VALUE);
            }
        }
        array.recycle();
        //构建dragHelper对象，监听当前容器的onTouchEvent事件
        helper = ViewDragHelper.create(this, new MyCallBack());
    }

    @Override
    protected void onFinishInflate() {
        try {
            optionsView = inflateChild(optionsResId);
            contentView = inflateChild(contentResId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addChildren();
    }

    private void addChildren() {
        if (getChildCount() > 0)
            removeAllViews();
        if (optionsView != null) {
            setRecursionClick(optionsView);
            //添加滑动操作选项view
            addView(optionsView);
        } else {
            throw new NullPointerException("view is null");
        }
        if (contentView != null) {
            if (contentBgResId > 0) {
                contentView.setBackgroundResource(contentBgResId);
            }
            if (hasContentBgColor) {
                contentView.setBackgroundColor(contentBgColor);
            }
            /**
             * 内容布局不能递归设置点击事件，否则被里面的item消耗了，达不到item的点击效果
             */
            contentView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onChildClickListener != null) {
                        onChildClickListener.onChildClick(v);
                    }
                }
            });
            //添加内容view
            addView(contentView);
        } else {
            throw new NullPointerException("view is null");
        }
    }

    //递归设置点击事件
    private void setRecursionClick(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            group.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onChildClickListener != null) {
                        onChildClickListener.onChildClick(v);
                    }
                }
            });
            for (int i = 0; i < group.getChildCount(); i++) {
                setRecursionClick(group.getChildAt(i));
            }
        } else {
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onChildClickListener != null) {
                        onChildClickListener.onChildClick(v);
                    }
                }
            });
        }
    }

    //加载资源布局
    private View inflateChild(int resId) throws Exception {
        if (resId <= 0) {
            throw new IllegalArgumentException("resId is invalid!");
        }
        return LayoutInflater.from(this.getContext()).inflate(resId, this, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量children的宽高属性
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //内容布局view
        contentView.layout(0, 0, contentView.getMeasuredWidth(), contentView.getMeasuredHeight());
        //操作布局view
        if (optionsLayoutPosition == OptionsLayoutPosition.FOLLOW.getIntValue()) {
            //操作布局跟随在item的内容布局后面
            if (slideDirection == SlideDirection.LEFT.getIntValue()) {
                optionsView.layout(-optionsView.getMeasuredWidth(), 0, 0, optionsView.getMeasuredHeight());
            } else if (slideDirection == SlideDirection.RIGHT.getIntValue()) {
                optionsView.layout(contentView.getMeasuredWidth(), 0, contentView.getMeasuredWidth() + optionsView.getMeasuredWidth(), optionsView.getMeasuredHeight());
            }
        } else if (optionsLayoutPosition == OptionsLayoutPosition.FIXED.getIntValue()) {
            //固定操作布局
            if (slideDirection == SlideDirection.LEFT.getIntValue()) {
                optionsView.layout(0, 0, optionsView.getMeasuredWidth(), optionsView.getMeasuredHeight());
            } else if (slideDirection == SlideDirection.RIGHT.getIntValue()) {
                optionsView.layout(contentView.getMeasuredWidth() - optionsView.getMeasuredWidth(), 0, contentView.getMeasuredWidth(), optionsView.getMeasuredHeight());
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!canDrag) {
            return false;
        }
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            if (helper != null)
                helper.cancel();
            return false;
        }
        if (helper != null) {
            return helper.shouldInterceptTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!canDrag) {
            return false;
        }
        if (helper != null) {
            //helper接管onTouchEvent所有的事件
            helper.processTouchEvent(event);
            return true;
        }
        return false;
    }

    private class MyCallBack extends ViewDragHelper.Callback {
        /**
         * 尝试捕获需要drag的childView(ACTION_DOWN)
         *
         * @return : ViewDragHelper是否继续分析处理child的相关drag事件
         * @child ： 需要drag的childView
         * @pointerId : 指针标识，多点触控的第几个手指按下
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //通过判断决定哪些childView需要处理拖动
            if (optionsLayoutPosition == OptionsLayoutPosition.FOLLOW.getIntValue()) {
                return contentView == child || optionsView == child;
            } else if (optionsLayoutPosition == OptionsLayoutPosition.FIXED.getIntValue()) {
                return contentView == child;
            }
            return false;
        }

        /**
         * child在某方向上被拖动时会调用对应方法，返回值是child移动过后的坐标位置
         *
         * @param child 移动的孩子View
         * @param left  父容器的左上角到孩子View的距离
         * @param dx    增量值，其实就是移动的孩子View的左上角距离控件（父亲）的距离，包含正负
         * @return 横向拖动的坐标位置
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //处理横向的拖动，left参数即为childView的left，为了防止越界，故需要判断left的范围。
            if (child == contentView) { // 解决内容部分左右拖动的越界问题
                if (slideDirection == SlideDirection.LEFT.getIntValue()) {
                    if (left < 0) {
                        left = 0;
                    }
                    if (left > optionsView.getMeasuredWidth()) {
                        left = optionsView.getMeasuredWidth();
                    }
                } else if (slideDirection == SlideDirection.RIGHT.getIntValue()) {
                    if (left > 0) {
                        left = 0;
                    }
                    if (-left > optionsView.getMeasuredWidth()) {
                        left = -optionsView.getMeasuredWidth();
                    }
                }
            }
            if (child == optionsView && optionsLayoutPosition == OptionsLayoutPosition.FOLLOW.getIntValue()) { // 解决删除部分左右拖动的越界问题
                if (slideDirection == SlideDirection.LEFT.getIntValue()) {
                    if (left > 0) {
                        left = 0;
                    }
                    if (-left > optionsView.getMeasuredWidth()) {
                        left = -optionsView.getMeasuredWidth();
                    }
                } else if (slideDirection == SlideDirection.RIGHT.getIntValue()) {
                    if (left < contentView.getMeasuredWidth() - optionsView.getMeasuredWidth()) {
                        left = contentView.getMeasuredWidth() - optionsView.getMeasuredWidth();
                    }
                    if (left > contentView.getMeasuredWidth()) {
                        left = contentView.getMeasuredWidth();
                    }
                }
            }
            return left;
        }

        /**
         * child在某方向上被拖动时会调用对应方法，返回值是child移动过后的坐标位置
         *
         * @param child 要拖拽的子View实例
         * @param top   期望的移动后位置子View的top值
         * @param dy    移动的距离
         * @return 返回值为子View在最终位置时的top值，一般直接返回第二个参数即可
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return super.clampViewPositionVertical(child, top, dy);
        }

        /**
         * 如果ViewGroup的子控件会消耗点击事件，例如按钮，在触摸屏幕的时候就会先走onInterceptTouchEvent方法，<br/>
         * 判断是否可以捕获，而在判断的过程中会去判断另外两个回调的方法：getViewHorizontalDragRange <br/>
         * 和getViewVerticalDragRange，只有这两个方法返回大于0的值才能正常的捕获。<br/>
         *
         * @param child Child view to check
         * @return 返回给定的child在相应的方向上可以被拖动的最远距离，默认返回0
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            if (child == contentView) {
                return optionsView.getMeasuredWidth();
            } else if (child == optionsView) {
                if (optionsLayoutPosition == OptionsLayoutPosition.FOLLOW.getIntValue()) {
                    return optionsView.getMeasuredWidth();
                }
            }
            return super.getViewHorizontalDragRange(child);
        }

        //返回给定的child在相应的方向上可以被拖动的最远距离，默认返回0
        @Override
        public int getViewVerticalDragRange(View child) {
            return super.getViewVerticalDragRange(child);
        }

        /**
         * 当View的位置改变时的回调
         *
         * @param changedView touch改变的view
         * @param left        changedView的left
         * @param top         changedView的top
         * @param dx          x方向的上的增量值
         * @param dy          y方向上的增量值
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (movedLengthTemp < left) {
                slideDirectionRight = true;
            } else {
                slideDirectionRight = false;
            }
            movedLengthTemp = left;
            if (optionsLayoutPosition == OptionsLayoutPosition.FOLLOW.getIntValue()) {
                int tempLeft = 0;
                int tempRight = 0;
                if (changedView == contentView) {
                    //如果移动的是contentView，根据contentView移动时的left和top重新布局optionsView
                    if (slideDirection == SlideDirection.LEFT.getIntValue()) {
                        tempLeft = left - optionsView.getMeasuredWidth();
                    }
                    if (slideDirection == SlideDirection.RIGHT.getIntValue()) {
                        tempLeft = left + contentView.getMeasuredWidth();
                    }
                    tempRight = tempLeft + optionsView.getMeasuredWidth();
                    optionsView.layout(tempLeft, 0, tempRight, optionsView.getMeasuredHeight());
                }
                if (changedView == optionsView) {
                    //如果移动的是optionsView，则需要手动给contentView布局定位
                    if (slideDirection == SlideDirection.LEFT.getIntValue()) {
                        tempLeft = left + optionsView.getMeasuredWidth();
                        tempRight = tempLeft + contentView.getMeasuredWidth();
                    } else if (slideDirection == SlideDirection.RIGHT.getIntValue()) {
                        tempLeft = left - contentView.getMeasuredWidth();
                        tempRight = left;
                    }
                    contentView.layout(tempLeft, 0, tempRight, contentView.getMeasuredHeight());
                }
            }
        }

        /**
         * 相当于Touch的up的事件会回调onViewReleased这个方法
         *
         * @param releasedChild
         * @param xvel          x方向的速率
         * @param yvel          y方向的速率
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            //在ViewDragHelper.Callback的onViewReleased()方法里调用settleCapturedViewAt()、flingCapturedView()，
            //或在任意地方调用smoothSlideViewTo()方法
            int optionsViewLeft = 0;
            int contentViewLeft = 0;
            boolean isChange = false;
            if (optionsLayoutPosition == OptionsLayoutPosition.FOLLOW.getIntValue()) {
                //操作布局跟随在item的内容布局后面
                if (slideDirection == SlideDirection.LEFT.getIntValue()) {
                    if (releasedChild == contentView) {
                        if (slideDirectionRight) {//向右
                            if (releasedChild.getLeft() > thresholdMoved) {
                                isChange = true;
                                optionsViewLeft = 0;
                                contentViewLeft = optionsView.getMeasuredWidth();
                            } else {
                                isChange = false;
                                optionsViewLeft = -optionsView.getMeasuredWidth();
                                contentViewLeft = 0;
                            }
                        } else {//向左
                            if (releasedChild.getLeft() < (optionsView.getMeasuredWidth() - thresholdMoved)) {
                                isChange = true;
                                optionsViewLeft = -optionsView.getMeasuredWidth();
                                contentViewLeft = 0;
                            } else {
                                isChange = false;
                                optionsViewLeft = 0;
                                contentViewLeft = optionsView.getMeasuredWidth();
                            }
                        }
                    } else if (releasedChild == optionsView) {
                        if (slideDirectionRight) {//向右
                            if (Math.abs(releasedChild.getLeft()) < optionsView.getMeasuredWidth() - thresholdMoved) {
                                isChange = true;
                                optionsViewLeft = 0;
                                contentViewLeft = optionsView.getMeasuredWidth();
                            } else {
                                isChange = false;
                                optionsViewLeft = -optionsView.getMeasuredWidth();
                                contentViewLeft = 0;
                            }
                        } else {//向左
                            if (Math.abs(releasedChild.getLeft()) > thresholdMoved) {
                                isChange = true;
                                optionsViewLeft = -optionsView.getMeasuredWidth();
                                contentViewLeft = 0;
                            } else {
                                isChange = false;
                                optionsViewLeft = 0;
                                contentViewLeft = optionsView.getMeasuredWidth();
                            }
                        }
                    }
                } else if (slideDirection == SlideDirection.RIGHT.getIntValue()) {
                    if (releasedChild == contentView) {
                        if (slideDirectionRight) {//向右
                            if (Math.abs(releasedChild.getLeft()) < optionsView.getMeasuredWidth() - thresholdMoved) {
                                isChange = true;
                                optionsViewLeft = contentView.getMeasuredWidth();
                                contentViewLeft = 0;
                            } else {
                                isChange = false;
                                optionsViewLeft = contentView.getMeasuredWidth() - optionsView.getMeasuredWidth();
                                contentViewLeft = -optionsView.getMeasuredWidth();
                            }
                        } else {//向左
                            if (Math.abs(releasedChild.getLeft()) > thresholdMoved) {
                                isChange = true;
                                optionsViewLeft = contentView.getMeasuredWidth() - optionsView.getMeasuredWidth();
                                contentViewLeft = -optionsView.getMeasuredWidth();
                            } else {
                                isChange = false;
                                optionsViewLeft = contentView.getMeasuredWidth();
                                contentViewLeft = 0;
                            }
                        }
                    } else if (releasedChild == optionsView) {
                        if (slideDirectionRight) {//向右
                            if (Math.abs(releasedChild.getLeft()) > contentView.getMeasuredWidth() - optionsView.getMeasuredWidth() + thresholdMoved) {
                                isChange = true;
                                optionsViewLeft = contentView.getMeasuredWidth();
                                contentViewLeft = 0;
                            } else {
                                isChange = false;
                                optionsViewLeft = contentView.getMeasuredWidth() - optionsView.getMeasuredWidth();
                                contentViewLeft = -optionsView.getMeasuredWidth();
                            }
                        } else {//向左
                            if (Math.abs(releasedChild.getLeft()) < contentView.getMeasuredWidth() - thresholdMoved) {
                                isChange = true;
                                optionsViewLeft = contentView.getMeasuredWidth() - optionsView.getMeasuredWidth();
                                contentViewLeft = -optionsView.getMeasuredWidth();
                            } else {
                                isChange = false;
                                optionsViewLeft = contentView.getMeasuredWidth();
                                contentViewLeft = 0;
                            }
                        }
                    }
                }
            } else if (optionsLayoutPosition == OptionsLayoutPosition.FIXED.getIntValue()) {
                //固定操作布局
                if (slideDirection == SlideDirection.LEFT.getIntValue()) {
                    optionsViewLeft = 0;
                    if (releasedChild == contentView) {
                        if (slideDirectionRight) {//向右
                            if (Math.abs(releasedChild.getLeft()) > thresholdMoved) {
                                isChange = true;
                                contentViewLeft = optionsView.getMeasuredWidth();
                            } else {
                                isChange = false;
                                contentViewLeft = 0;
                            }
                        } else {//向左
                            if (Math.abs(releasedChild.getLeft()) < optionsView.getMeasuredWidth() - thresholdMoved) {
                                isChange = true;
                                contentViewLeft = 0;
                            } else {
                                isChange = false;
                                contentViewLeft = optionsView.getMeasuredWidth();
                            }
                        }
                    }
                } else if (slideDirection == SlideDirection.RIGHT.getIntValue()) {
                    optionsViewLeft = contentView.getMeasuredWidth() - optionsView.getMeasuredWidth();
                    if (releasedChild == contentView) {
                        if (slideDirectionRight) {//向右
                            if (Math.abs(releasedChild.getLeft()) < optionsView.getMeasuredWidth() - thresholdMoved) {
                                isChange = true;
                                contentViewLeft = 0;
                            } else {
                                isChange = false;
                                contentViewLeft = -optionsView.getMeasuredWidth();
                            }
                        } else {//向左
                            if (Math.abs(releasedChild.getLeft()) > thresholdMoved) {
                                isChange = true;
                                contentViewLeft = -optionsView.getMeasuredWidth();
                            } else {
                                isChange = false;
                                contentViewLeft = 0;
                            }
                        }
                    }
                }
            }
            //注意下两行代码先后顺序
            helper.smoothSlideViewTo(optionsView, optionsViewLeft, 0);
            helper.smoothSlideViewTo(contentView, contentViewLeft, 0);
            //重绘，在view的onDraw里面会调用computeScroll()方法
            invalidate();
            if (onOptionsStateChangeListener != null && isChange != isOpened) {
                isOpened = isChange;
                onOptionsStateChangeListener.onOptionsViewChange(isOpened);
            }
            //方向判断标记重置
            movedLengthTemp = 0;
        }
    }

    @Override
    public void computeScroll() {
        if (helper != null && helper.continueSettling(true)) {
            //获取对应的left和top，继续重绘
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public interface OnOptionsStateChangeListener {
        void onOptionsViewChange(boolean isOpened);
    }

    public interface OnChildClickListener {
        void onChildClick(View view);
    }

    //滑动布局滑出的方向
    public enum SlideDirection {
        RIGHT(0), LEFT(1);

        SlideDirection(int intValue) {
            this.intValue = intValue;
        }

        private int intValue;

        public int getIntValue() {
            return intValue;
        }
    }

    //滑动布局的布局方式
    public enum OptionsLayoutPosition {
        FOLLOW(0), FIXED(1);

        OptionsLayoutPosition(int intValue) {
            this.intValue = intValue;
        }

        private int intValue;

        public int getIntValue() {
            return intValue;
        }
    }
}