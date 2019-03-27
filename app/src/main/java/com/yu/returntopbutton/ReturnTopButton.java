package com.yu.returntopbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;



/**
 * 列表页回顶部的按钮 用法： 1.实例化:ReturnTopButton returnTopButton = new
 * ReturnTopButton(context);
 * 2.指定AbsListView:returnTopButton.setListView(listView);
 * 3.设置监听器:returnTopButton.setOnScrollListener(onScrollListener);
 * 4.设置列表的最大条目数:returnTopButton.setmTotalCount(totalCount);
 * 5.设置列表显示到第几个条目时，按钮出现（
 * 默认是3）:returnTopButton.setmFirstViewToShow(firstViewToShow);
 * 
 *
 * 
 */
public class ReturnTopButton extends LinearLayout {

    private Context mContext;

    /**
     * ��ʾ�����Ĳ���
     */
    private LinearLayout mLayoutNum;
    /**
     * ��ʾ�ض����Ĳ���
     */
    private ImageView mViewTop;
    /**
     * ��ǰ��Ʒ����
     */
    private TextView mTVCurNum;
    /**
     * ��Ʒ������
     */
    private TextView mTVTotalNum;
    /**
     * �ؼ���ʧ�����Զ���
     */
    private ObjectAnimator mAnimator;
    /**
     * �ؼ����ڵ��б�������ɵ���ؼ��б�ض����Ĺ���
     */
    private AbsListView mListView;

    private OnScrollListener mOnScrollListener;
    /**
     * ListView当前第一个可见的条目大于这个数字，才会显示返回顶部的按钮
     */
    private int mFirstViewToShow = 3;
    /**
     * 总数
     */
    private int mTotalCount;
    /**
     * 倍数：专题列表和特卖列表，这个值为2
     */
    private int mDoubleCount = 1;

    private boolean mShowNumber = true;

    private MyOnClickListener mOnClickListener;

    public int getmFirstViewToShow() {
        return mFirstViewToShow;
    }

    public int getmDoubleCount() {
        return mDoubleCount;
    }

    public void setmDoubleCount(int mDoubleCount) {
        this.mDoubleCount = mDoubleCount;
    }

    public void setmFirstViewToShow(int mFirstViewToShow) {
        this.mFirstViewToShow = mFirstViewToShow;
    }

    public int getmTotalCount() {
        return mTotalCount;
    }

    public void setmTotalCount(int mTotalCount) {
        this.mTotalCount = mTotalCount;
    }

    public ReturnTopButton(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ReturnTopButton(Context context, boolean showNumber) {
        super(context);
        mContext = context;
        mShowNumber = showNumber;
        init();
    }

    public ReturnTopButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setMyOnClickListener(MyOnClickListener listener) {
        mOnClickListener = listener;
    }

    /**
     * ��ʼ������
     */
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_return_top,
                this);

        mLayoutNum = (LinearLayout) findViewById(R.id.ll_num);
        mViewTop = (ImageView) findViewById(R.id.iv_arrow);
        mTVCurNum = (TextView) findViewById(R.id.tv_top_num);
        mTVTotalNum = (TextView) findViewById(R.id.tv_bottom_num);

        mAnimator = ObjectAnimator.ofFloat(this, "alpha", 1, 0);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.GONE);
            }
        });
        mAnimator.setDuration(500);
        setClickable(true);
        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListView != null) {
                    mListView.setSelection(0);
                }
                if (mOnClickListener != null) {
                    mOnClickListener.onClick();
                }
            }
        });
    }

    /**
     * ���õ�ǰ��Ʒ��
     * 
     * @param curNum
     */
    public void setCurNum(int curNum) {
        mTVCurNum.setText("" + curNum);
    }

    /**
     * ���û�Ʒ����
     * 
     * @param totalNum
     */
    public void setTotalNum(int totalNum) {
        mTVTotalNum.setText("" + totalNum);
    }

    /**
     * ���ؿؼ�
     */
    public void dismiss() {
        // �������δ��ʼ����ʼ���������⶯������ε���
        if (!mAnimator.isStarted()) {
            mAnimator.start();
        }
    }

    /**
     * ��ʾ�ؼ�
     */
    public void show() {
        if (mAnimator.isStarted()) {
            mAnimator.cancel();
        }
        setAlpha(1f);
        setVisibility(View.VISIBLE);
    }

    private int dipToPixels(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                r.getDisplayMetrics());
        return (int) px;
    }

    /**
     * ���ÿؼ����ڵ��б�(ListView��GridView������)
     * 
     * @param listView
     */
    public void setListView(AbsListView listView) {
        mListView = listView;
        ViewGroup.LayoutParams lp = listView.getLayoutParams();
        ViewParent parent = listView.getParent();
        FrameLayout container = new FrameLayout(mContext);

        // TODO verify that parent is indeed a ViewGroup
        ViewGroup group = (ViewGroup) parent;
        int index = group.indexOfChild(listView);

        group.removeView(listView);
        group.addView(container, index, lp);

        container.addView(listView);

        this.setVisibility(View.GONE);
        // container.addView(this);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        flp.setMargins(0, 0, dipToPixels(5), dipToPixels(5));
        container.addView(this, flp);

        group.invalidate();

        initScrollListener();
    }

    /**
     * 初始化ListView的滑动监听器，方法内动态改变按钮的显示
     */
    private void initScrollListener() {
        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    setIdle();
                } else {
                    setScrolling();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScroll(view, firstVisibleItem,
                            visibleItemCount, totalItemCount);
                }
                if (firstVisibleItem < mFirstViewToShow) {
                    dismiss();
                } else {
                    show();
                    // 减1的目的，是去掉HeaderView
                    int headerViewCount = 0;
                    if (mListView instanceof ListView) {
                        // 如果是ListView，则计算出HeaderView的数量
                        headerViewCount = ((ListView) mListView)
                                .getHeaderViewsCount();
                    }
                    int curNum = mDoubleCount
                            * (firstVisibleItem + visibleItemCount - headerViewCount);
                    if (curNum < mTotalCount) {
                        setCurNum(curNum);
                    } else {
                        setCurNum(mTotalCount);
                    }
                    setTotalNum(mTotalCount);
                }
            }
        });
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    /**
     * �б��ڻ���״̬���ؼ�Ӧ����ʾ���ֲ���
     */
    public void setScrolling() {
        if (mShowNumber) {
            mLayoutNum.setVisibility(View.VISIBLE);
            mViewTop.setVisibility(View.GONE);
        }
    }

    /**
     * �б��ھ�ֹ״̬���ؼ�Ӧ����ʾ�ض�������
     */
    public void setIdle() {
        // �������δ��ʼ������ʾ�ض������֣�����ͻȻ����������,�����������л����ض�����Ȼ������ʧ�����
        if (!mAnimator.isStarted()) {
            mLayoutNum.setVisibility(View.GONE);
            mViewTop.setVisibility(View.VISIBLE);
        }
    }

    public interface MyOnClickListener {
        void onClick();
    }

}
