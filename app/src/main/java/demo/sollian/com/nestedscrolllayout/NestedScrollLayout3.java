package demo.sollian.com.nestedscrolllayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author admin on 2017/12/22.
 */
public class NestedScrollLayout3 extends FrameLayout {
    private static final int IDLE = 0;
    private static final int DRAGGING = 1;

    private int hHead;
    //vHead y轴偏移量，范围[0, hHead]
    private int offsetY;

    private int scrollState = IDLE;

    private float beginY;

    private View vBody;

    public NestedScrollLayout3(@NonNull Context context) {
        super(context);
        init();
    }

    public NestedScrollLayout3(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedScrollLayout3(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (getChildCount() != 2) {
            throw new RuntimeException("child count must be 2");
        }
        View vHead = getChildAt(0);
        vBody = getChildAt(1);

        hHead = vHead.getMeasuredHeight();

        int parentLeft = getPaddingLeft();
        int parentTop = getPaddingTop();

        vHead.layout(parentLeft, parentTop, parentLeft + vHead.getMeasuredWidth(), parentTop + hHead);
        vBody.layout(parentLeft, parentTop + hHead, parentLeft + getMeasuredWidth(), parentTop + hHead + vBody.getMeasuredHeight());
    }

    private int oldScrollY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                beginY = ev.getY();
                oldScrollY = getScrollY();
                scrollState = IDLE;
                offsetY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getY() - beginY;
                if (
                        deltaY > 0 && oldScrollY == hHead
                        ||
                        deltaY < 0 && oldScrollY == 0) {
                    scrollState = DRAGGING;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                scrollState = IDLE;
                break;
            default:
                break;
        }
        return scrollState == DRAGGING || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getY() - beginY;
                if (deltaY > 0 && oldScrollY == hHead) {
                    //下滑
                    offsetY = oldScrollY - (int) deltaY;
                    if (offsetY >= 0) {
                        scrollState = DRAGGING;
                        scrollTo(getScrollX(), offsetY);
                    } else {
                        scrollTo(getScrollX(), 0);
                        scrollState = IDLE;
                    }
                } else if (deltaY < 0 && oldScrollY == 0) {
                    //上滑
                    offsetY = oldScrollY - (int) deltaY;
                    if (offsetY <= hHead) {
                        scrollState = DRAGGING;
                        scrollTo(getScrollX(), offsetY);
                    } else {
                        scrollTo(getScrollX(), hHead);
                        scrollState = IDLE;
                    }
                } else {
                    scrollState = IDLE;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                scrollState = IDLE;
                break;
            default:
                break;
        }
        return true;//scrollState == DRAGGING;
    }
}
