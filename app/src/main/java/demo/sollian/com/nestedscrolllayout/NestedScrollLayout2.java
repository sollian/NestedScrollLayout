package demo.sollian.com.nestedscrolllayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * @author admin on 2017/12/22.
 */
public class NestedScrollLayout2 extends FrameLayout {
    private static final int IDLE = 0;
    private static final int SCROLL_UP = 1;
    private static final int SCROLL_DOWN = 2;

    private int scrollThreashold;

    private Scroller scroller;

    private int hHead;
    //vHead y轴偏移量，范围[0, hHead]
    private int offsetY;

    private int scrollState = IDLE;

    private boolean enableScroll;
    private boolean validTouch;

    private float beginY;

    private View vBody;

    public NestedScrollLayout2(@NonNull Context context) {
        super(context);
        init();
    }

    public NestedScrollLayout2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedScrollLayout2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        scroller = new Scroller(getContext(), new DecelerateInterpolator());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (getChildCount() != 2) {
            throw new RuntimeException("child count must be 2");
        }
        View vHead = getChildAt(0);
        vBody = getChildAt(1);

        hHead = vHead.getMeasuredHeight();
        scrollThreashold = hHead >> 2;
        vHead.layout(left, top, right, top + hHead);
        vBody.layout(left, top + hHead, right, bottom + hHead);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!enableScroll) {
            return super.dispatchTouchEvent(ev);
        }

        if (!scroller.isFinished()) {
            return true;
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            validTouch = ev.getY() > vBody.getTop() - offsetY;
        }

        if (!validTouch) {
            return super.dispatchTouchEvent(ev);
        }

        boolean handleTouchEvent = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                beginY = ev.getY();
                scrollState = IDLE;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getY() - beginY;
                if (deltaY > 0) {
                    //下拉
                    if (offsetY > 0) {
                        scrollState = SCROLL_DOWN;
                        handleTouchEvent = true;
                        offsetY = hHead - (int) deltaY;

                        if (offsetY < 0) {
                            offsetY = 0;
                        }
                        scrollTo(0, offsetY);
                    } else {
                        if (scrollState != IDLE) {
                            scrollState = IDLE;
                        }
                        beginY = ev.getY();
                    }
                } else if (deltaY < 0) {
                    //上拉
                    if (offsetY < hHead) {
                        scrollState = SCROLL_UP;
                        handleTouchEvent = true;
                        offsetY = -(int) deltaY;


                        if (offsetY > hHead) {
                            offsetY = hHead;
                        }
                        scrollTo(0, offsetY);
                    } else {
                        if (scrollState != IDLE) {
                            scrollState = IDLE;
                        }
                        beginY = ev.getY();
                    }
                } else {
                    scrollState = IDLE;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (scrollState == SCROLL_UP) {
                    if (offsetY < hHead) {
                        if (offsetY > scrollThreashold) {
                            scroller.startScroll(0, offsetY, 0, hHead - offsetY);
                        } else {
                            scrollState = SCROLL_DOWN;
                            scroller.startScroll(0, offsetY, 0, -offsetY);
                        }
                        invalidate();
                    }
                    handleTouchEvent = true;
                } else if (scrollState == SCROLL_DOWN) {
                    if (offsetY > 0) {
                        if (hHead - offsetY > scrollThreashold) {
                            scroller.startScroll(0, offsetY, 0, -offsetY);
                        } else {
                            scrollState = SCROLL_UP;
                            scroller.startScroll(0, offsetY, 0, hHead - offsetY);
                        }
                        invalidate();
                    }
                    handleTouchEvent = true;
                }
                break;
            default:
                break;
        }
        return handleTouchEvent || super.dispatchTouchEvent(ev);
    }

    public boolean isEnableScroll() {
        return enableScroll;
    }

    public void setEnableScroll(boolean enableScroll) {
        this.enableScroll = enableScroll;
    }
}
