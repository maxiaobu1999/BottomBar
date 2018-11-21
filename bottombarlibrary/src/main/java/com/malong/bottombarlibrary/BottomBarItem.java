package com.malong.bottombarlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;


/**
 * 底部tab条目
 */
public class BottomBarItem extends LinearLayout {
    private Context mContext;
    private int mIconNormalResourceId;//普通状态图标的资源id
    private int mIconSelectedResourceId;//选中状态图标的资源id
    private String mText;//文本
    private int mTextSize = 12;//文字大小 默认为12sp
    private int mTextColorNormal = 0xFF999999;    //描述文本的默认显示颜色
    private int mTextColorSelected = 0xFF46C01B;  //述文本的默认选中显示颜色
    private int mWhiteColor = 0xFFFFFFFF;  //白色
    private int mMarginTop = 0;//文字和图标的距离,默认0dp
    private boolean mOpenTouchBg = false;// 是否开启触摸背景，默认关闭
    private Drawable mTouchDrawable;//触摸时的背景
    private int mIconWidth;//图标的宽度
    private int mIconHeight;//图标的高度
    private int mItemPadding;//BottomBarItem的padding


    private ImageView mImageView;
    private TextView mTvUnread;
    private TextView mTvNotify;
    private TextView mTvMsg;
    private TextView mTextView;

    private int mUnreadTextSize = 10; //未读数默认字体大小10sp
    private int mMsgTextSize = 6; //消息默认字体大小6sp
    private int unreadNumThreshold = 99;//未读数阈值
    private int mUnreadTextColor;//未读数字体颜色
    private Drawable mUnreadTextBg;
    private int mMsgTextColor;
    private Drawable mMsgTextBg;
    private Drawable mNotifyPointBg;

    public BottomBarItem(Context context) {
        this(context, null);
    }

    public BottomBarItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBarItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BottomBarItem);

        initAttrs(ta); //初始化属性

        ta.recycle();

        checkValues();//检查值是否合法

        init();//初始化相关操作
    }

    private void initAttrs(TypedArray ta) {
        mIconNormalResourceId = ta.getResourceId(R.styleable.BottomBarItem_iconNormal, -1);
        mIconSelectedResourceId = ta.getResourceId(R.styleable.BottomBarItem_iconSelected, -1);

        mText = ta.getString(R.styleable.BottomBarItem_itemText);
        mTextSize = ta.getDimensionPixelSize(R.styleable.BottomBarItem_itemTextSize, UIUtils.sp2px(mContext, mTextSize));

        mTextColorNormal = ta.getColor(R.styleable.BottomBarItem_textColorNormal, mTextColorNormal);
        mTextColorSelected = ta.getColor(R.styleable.BottomBarItem_textColorSelected, mTextColorSelected);

        mMarginTop = ta.getDimensionPixelSize(R.styleable.BottomBarItem_itemMarginTop, UIUtils.dip2Px(mContext, mMarginTop));

        mOpenTouchBg = ta.getBoolean(R.styleable.BottomBarItem_openTouchBg, mOpenTouchBg);
        mTouchDrawable = ta.getDrawable(R.styleable.BottomBarItem_touchDrawable);

        mIconWidth = ta.getDimensionPixelSize(R.styleable.BottomBarItem_iconWidth, 0);
        mIconHeight = ta.getDimensionPixelSize(R.styleable.BottomBarItem_iconHeight, 0);
        mItemPadding = ta.getDimensionPixelSize(R.styleable.BottomBarItem_itemPadding, 0);

        mUnreadTextSize = ta.getDimensionPixelSize(R.styleable.BottomBarItem_unreadTextSize, UIUtils.sp2px(mContext, mUnreadTextSize));
        mUnreadTextColor = ta.getColor(R.styleable.BottomBarItem_unreadTextColor, 0xFFFFFFFF);
        mUnreadTextBg = ta.getDrawable(R.styleable.BottomBarItem_unreadTextBg);

        mMsgTextSize = ta.getDimensionPixelSize(R.styleable.BottomBarItem_msgTextSize, UIUtils.sp2px(mContext, mMsgTextSize));
        mMsgTextColor = ta.getColor(R.styleable.BottomBarItem_msgTextColor, 0xFFFFFFFF);
        mMsgTextBg = ta.getDrawable(R.styleable.BottomBarItem_msgTextBg);

        mNotifyPointBg = ta.getDrawable(R.styleable.BottomBarItem_notifyPointBg);

        unreadNumThreshold = ta.getInteger(R.styleable.BottomBarItem_unreadThreshold, 99);
    }

    /**
     * 检查传入的值是否完善
     */
    private void checkValues() {
        if (mIconNormalResourceId == -1) {
            throw new IllegalStateException("您还没有设置默认状态下的图标，请指定iconNormal的图标");
        }

        if (mIconSelectedResourceId == -1) {
            throw new IllegalStateException("您还没有设置选中状态下的图标，请指定iconSelected的图标");
        }

        if (mOpenTouchBg && mTouchDrawable == null) {
            //如果有开启触摸背景效果但是没有传对应的drawable
            throw new IllegalStateException("开启了触摸效果，但是没有指定touchDrawable");
        }

        if (mUnreadTextBg == null) {
            mUnreadTextBg = getResources().getDrawable(R.drawable.shape_unread);
        }

        if (mMsgTextBg == null) {
            mMsgTextBg = getResources().getDrawable(R.drawable.shape_msg);
        }

        if (mNotifyPointBg == null) {
            mNotifyPointBg = getResources().getDrawable(R.drawable.shape_notify_point);
        }
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        View view = initView();

        mImageView.setImageResource(mIconNormalResourceId);

        if (mIconWidth != 0 && mIconHeight != 0) {
            //如果有设置图标的宽度和高度，则设置ImageView的宽高
            FrameLayout.LayoutParams imageLayoutParams = (FrameLayout.LayoutParams) mImageView.getLayoutParams();
            imageLayoutParams.width = mIconWidth;
            imageLayoutParams.height = mIconHeight;
            mImageView.setLayoutParams(imageLayoutParams);
        }

        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);//设置底部文字字体大小

        mTvUnread.setTextSize(TypedValue.COMPLEX_UNIT_PX, mUnreadTextSize);//设置未读数的字体大小
        mTvUnread.setTextColor(mUnreadTextColor);//设置未读数字体颜色
        mTvUnread.setBackground(mUnreadTextBg);//设置未读数背景

        mTvMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMsgTextSize);//设置提示文字的字体大小
        mTvMsg.setTextColor(mMsgTextColor);//设置提示文字的字体颜色
        mTvMsg.setBackground(mMsgTextBg);//设置提示文字的背景颜色

        mTvNotify.setBackground(mNotifyPointBg);//设置提示点的背景颜色

        mTextView.setTextColor(mTextColorNormal);//设置底部文字字体颜色
        mTextView.setText(mText);//设置标签文字

        LayoutParams textLayoutParams = (LayoutParams) mTextView.getLayoutParams();
        textLayoutParams.topMargin = mMarginTop;
        mTextView.setLayoutParams(textLayoutParams);

        if (mOpenTouchBg) {
            //如果有开启触摸背景
            setBackground(mTouchDrawable);
        }

        addView(view);
    }

    @NonNull
    private View initView() {
        //根布局
        LinearLayout linearLayout = new LinearLayout(mContext);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(VERTICAL);

        FrameLayout frameLayout = new FrameLayout(mContext);
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        frameLayout.setLayoutParams(params1);

        mImageView = new ImageView(mContext);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.CENTER;
        mImageView.setLayoutParams(params2);
        mImageView.setId(R.id.iv_icon);


        mTvUnread = new TextView(mContext);
        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params3.gravity = Gravity.CENTER_HORIZONTAL;
        params3.leftMargin = UIUtils.dip2Px(mContext, 14);
        mTvUnread.setLayoutParams(params3);
        mTvUnread.setBackgroundResource(R.drawable.shape_unread);
        mTvUnread.setMinWidth(UIUtils.dip2Px(mContext, 15));
        mTvUnread.setGravity(Gravity.CENTER);
        mTvUnread.setTextColor(Color.parseColor("#ffffff"));
        mTvUnread.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mTvUnread.setVisibility(GONE);
        mTvUnread.setText("99+");
        mTvUnread.setId(R.id.tv_unread_num);

        mTvMsg = new TextView(mContext);
        FrameLayout.LayoutParams params4 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params4.leftMargin = UIUtils.dip2Px(mContext, 14);
        mTvMsg.setLayoutParams(params4);
        mTvMsg.setBackgroundResource(R.drawable.shape_msg);
        mTvMsg.setGravity(Gravity.CENTER);
        mTvMsg.setTextColor(Color.parseColor("#ffffff"));
        mTvMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 6);
        mTvMsg.setVisibility(GONE);
        mTvMsg.setText("NEW");
        mTvMsg.setId(R.id.tv_msg);


        mTvNotify = new TextView(mContext);
        mTvNotify.setId(R.id.tv_point);
        FrameLayout.LayoutParams params5 = new FrameLayout.LayoutParams(UIUtils.dip2Px(mContext,10), UIUtils.dip2Px(mContext,10));
        params5.gravity = Gravity.CENTER_HORIZONTAL;
        params5.leftMargin = UIUtils.dip2Px(mContext, 10);
        mTvNotify.setLayoutParams(params5);
        mTvNotify.setBackgroundResource(R.drawable.shape_notify_point);
        mTvNotify.setGravity(Gravity.CENTER);
        mTvNotify.setTextColor(Color.parseColor("#ffffff"));
        mTvNotify.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mTvNotify.setVisibility(GONE);


        mTextView = new TextView(mContext);
        mTextView.setId(R.id.tv_text);
        LayoutParams params6 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTextView.setLayoutParams(params6);

        frameLayout.addView(mImageView);
        frameLayout.addView(mTvUnread);
        frameLayout.addView(mTvMsg);
        frameLayout.addView(mTvNotify);
        linearLayout.addView(frameLayout);
        linearLayout.addView(mTextView);


        return linearLayout;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setIconNormalResourceId(int mIconNormalResourceId) {
        this.mIconNormalResourceId = mIconNormalResourceId;
    }

    public void setIconSelectedResourceId(int mIconSelectedResourceId) {
        this.mIconSelectedResourceId = mIconSelectedResourceId;
    }

    public void setStatus(boolean isSelected) {
        mImageView.setImageDrawable(getResources().getDrawable(isSelected ? mIconSelectedResourceId : mIconNormalResourceId));
        mTextView.setTextColor(isSelected ? mTextColorSelected : mTextColorNormal);
    }

    private void setTvVisiable(TextView tv) {
        //都设置为不可见
        mTvUnread.setVisibility(GONE);
        mTvMsg.setVisibility(GONE);
        mTvNotify.setVisibility(GONE);

        tv.setVisibility(VISIBLE);//设置为可见
    }

    public int getUnreadNumThreshold() {
        return unreadNumThreshold;
    }

    public void setUnreadNumThreshold(int unreadNumThreshold) {
        this.unreadNumThreshold = unreadNumThreshold;
    }

    /**
     * 设置未读数
     *
     * @param unreadNum 小于等于{@link }则隐藏，
     *                  大于0小于{@link }则显示对应数字，
     *                  超过{@link }
     *                  显示{@link }+
     */
    public void setUnreadNum(int unreadNum) {
        setTvVisiable(mTvUnread);
        if (unreadNum <= 0) {
            mTvUnread.setVisibility(GONE);
        } else if (unreadNum <= unreadNumThreshold) {
            mTvUnread.setText(String.valueOf(unreadNum));
        } else {
            mTvUnread.setText(String.format(Locale.CHINA, "%d+", unreadNumThreshold));
        }
    }

    public void setMsg(String msg) {
        setTvVisiable(mTvMsg);
        mTvMsg.setText(msg);
    }

    public void hideMsg() {
        mTvMsg.setVisibility(GONE);
    }

    public void showNotify() {
        setTvVisiable(mTvNotify);
    }

    public void hideNotify() {
        mTvNotify.setVisibility(GONE);
    }
}
