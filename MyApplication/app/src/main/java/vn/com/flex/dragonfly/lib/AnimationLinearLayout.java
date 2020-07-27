package vn.com.flex.dragonfly.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

/**
 * Created by ndung on 12/22/15.
 */
public class AnimationLinearLayout extends LinearLayout {

    /**
     * タッチアップ時のイベントディレイ[ms]
     */
    private static final int DELAY_ACTION_UP = 0;

    /**
     * アニメーション中の判定<br/>
     * DOWNアニメーション開始からUPアニメーション終了の間trueになる。<br/>
     * DOWNとUPのアニメーションは1セットなので複数回UPアニメーションを実行しないための制御に使用する。<br/>
     * <br/>
     * true:アニメーション中<br/>
     * false:アニメーションしていない
     */
    private boolean mIsRunningAnim = false;
    private int mDownAnimationId =  vn.com.flex.dragonfly.R.anim.button_down_anim;
    private int mUpAnimationId = vn.com.flex.dragonfly.R.anim.button_up_anim;

    public AnimationLinearLayout(Context context) {
        super(context);
    }

    public AnimationLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimationLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) return false;

        // 押下無効の場合はsuperに流す
        if(!isEnabled() || !isClickable()) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startDownAnimation();
                break;

            case MotionEvent.ACTION_UP:
                event.setAction(MotionEvent.ACTION_CANCEL);
                if (mIsRunningAnim) {
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // クリックイベント
                            performClick();
                        }
                    }, DELAY_ACTION_UP);
                }
                startUpAnimation();
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                // 範囲外に移動した場合
                if (x < 0 || getWidth() < x || y < 0 || getHeight() < y) {
                    // タッチアップをキャンセルする
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    startUpAnimation();
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            default:
                startUpAnimation();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * タッチダウンのアニメーション設定
     */
    public void setDownAnimation(int downAnimationId) {
        mDownAnimationId = downAnimationId;
    }

    /**
     * タッチアップのアニメーション設定
     */
    public void setUpAnimation(int upAnimationId) {
        mUpAnimationId = upAnimationId;
    }

    /**
     * タッチダウンのアニメーション開始
     */
    private void startDownAnimation() {
        mIsRunningAnim = true;
        clearAnimation();
        startAnimation(AnimationUtils.loadAnimation(getContext(), mDownAnimationId));
    }

    /**
     * タッチアップのアニメーション開始
     */
    private void startUpAnimation() {
        if (mIsRunningAnim) {
            mIsRunningAnim = false;
            clearAnimation();
            startAnimation(AnimationUtils.loadAnimation(getContext(), mUpAnimationId));
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(enabled) {
            setAlpha(1f);
        } else {
            setAlpha(0.3f);
        }
        super.setEnabled(enabled);
    }

    @Override
    public void setVisibility(int visibility) {
        switch (visibility) {
            case View.VISIBLE:
                // 特になし
                break;
            case View.INVISIBLE:
            case View.GONE:
                // アニメーション終了
                clearAnimation();
                break;
        }
        super.setVisibility(visibility);
    }
}
