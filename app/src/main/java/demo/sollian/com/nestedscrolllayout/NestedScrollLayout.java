package demo.sollian.com.nestedscrolllayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * @author admin on 2017/12/22.
 */
public class NestedScrollLayout extends FrameLayout implements NestedScrollingParent {
    private static final int IDLE = 0;
    private static final int SCROLL_UP = 1;
    private static final int SCROLL_DOWN = 2;

    private NestedScrollingParentHelper nestedScrollingParentHelper;
    private View vHead;
    private View vBody;

    private Scroller scroller;

    private int hHead;
    //vHead y轴偏移量，范围[0, hHead]
    private int offsetY;

    private int scrollState = IDLE;

    private boolean enableScroll = true;

    public NestedScrollLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public NestedScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        scroller = new Scroller(getContext(), new DecelerateInterpolator());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        if (vHead == null || vBody == null) {
//            if (getChildCount() != 2) {
//                throw new RuntimeException("child count must be 2");
//            }
//            vHead = getChildAt(0);
//            vBody = getChildAt(1);
//        }
//
//        hHead = vHead.getMeasuredHeight();
//        vHead.layout(0, 0, right-left, hHead);
//        vBody.layout(0, hHead, right-left, bottom - top + hHead);
        super.onLayout(changed, left, top, right, bottom);
        if (getChildCount() != 2) {
            throw new RuntimeException("child count must be 2");
        }
        vHead = getChildAt(0);
        vBody = getChildAt(1);

        hHead = vHead.getMeasuredHeight();
        vBody.setTranslationY(hHead);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes) {
        if (!scroller.isFinished()) {
            scroller.forceFinished(true);
        }
        scrollState = IDLE;
        return enableScroll && axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        nestedScrollingParentHelper.onStopNestedScroll(target);
        if (scrollState == SCROLL_UP) {
            if (offsetY < hHead) {
                scroller.startScroll(0, offsetY, 0, hHead - offsetY);
                invalidate();
            }
        } else if (scrollState == SCROLL_DOWN) {
            if (offsetY > 0) {
                scroller.startScroll(0, offsetY, 0, -offsetY);
                invalidate();
            }
        }
        scrollState = IDLE;
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        int dOffset;
        int remain = hHead - offsetY;
        if (dy > 0) {
            //上滑
            scrollState = SCROLL_UP;
            dOffset = remain >= dy ? dy : remain;
        } else {
            //下滑
            scrollState = SCROLL_DOWN;
            dOffset = offsetY + dy >= 0 ? dy : -offsetY;
        }
        consumed[1] = dOffset;
        offsetY += dOffset;

        scrollTo(0, offsetY);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            offsetY = scroller.getCurrY();
            scrollTo(0, offsetY);
            invalidate();
        }
    }

    public boolean isEnableScroll() {
        return enableScroll;
    }

    public void setEnableScroll(boolean enableScroll) {
        this.enableScroll = enableScroll;
    }

}
