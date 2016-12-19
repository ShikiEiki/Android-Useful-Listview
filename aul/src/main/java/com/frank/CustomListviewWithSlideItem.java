package com.frank;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by FH on 2015/12/22.
 */
public class CustomListviewWithSlideItem extends LinearLayout {
    String LOG_TAG = "CustomListviewNew";

    private BaseAdapter customAdapter;
    public ListView mainListView;
    TextView arrowDownImage , arrowDownText , arrowUpImage , arrowUpText;
    LinearLayout listLayout;
    LinearLayout tophintLayout , bottomHintLayout;
    Context mContext;

    AsyncTask currentAsyncTask;
    boolean btn1Enable = false;
    boolean btn2Enable = false;
    int btn1BackgroundColor = Color.RED;
    String btn1Text = "按钮1";
    int btn1TextColor = Color.WHITE;
    float btn1TextSize = 16;
    int btn1TextSizeUnit = TypedValue.COMPLEX_UNIT_SP;
    int btn2BackgroundColor = Color.GREEN;
    String btn2Text = "按钮2";
    int btn2TextColor = Color.WHITE;
    float btn2TextSize = 16;
    int btn2TextSizeUnit = TypedValue.COMPLEX_UNIT_SP;
    boolean hasInitFinish = false;

    OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {}
        @Override
        public void onBtn1Click(int position) {}
        @Override
        public void onBtn2Click(int position) {}
    };

    OnListActionListener mOnListActionListener = new OnListActionListener() {
        @Override
        public void onPreRefresh(ListView listView) {}
        @Override
        public BackObject refreshDoInBackground(ListView listView , AsyncTask asyncTask) {
            return new BackObject(BackObject.Error.NO_RESULT);
        }
        @Override
        public void onRefreshSuccess(ListView listView, BackObject backObject) {}
        @Override
        public void onRefreshFail(ListView listView, BackObject backObject) {}
        @Override
        public void onPreAddMore(ListView listView) {}
        @Override
        public BackObject addmoreDoInBackground(ListView listView , AsyncTask asyncTask) {
            return new BackObject(BackObject.Error.NO_MORE);
        }
        @Override
        public void onAddmoreSuccess(ListView listView, BackObject backObject) {}
        @Override
        public void onAddmoreFail(ListView listView, BackObject backObject) {}
    };

    private LinearLayout errorHintContainer;

    MyAdapter realAdapter = new MyAdapter();
    LayoutInflater layoutInflater;

    enum STATUS {
        REFRESHING , ADDING_MORE , READY
    }
    STATUS currentStatus = STATUS.READY;


    public CustomListviewWithSlideItem(Context context) {
        super(context);
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        addAndFindViews();
        initMainListView();
    }

    public CustomListviewWithSlideItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        addAndFindViews();
        initMainListView();
    }

    public CustomListviewWithSlideItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        addAndFindViews();
        initMainListView();
    }

    public void setBtn1Enable(boolean enable){
        btn1Enable = enable;
        realAdapter.notifyDataSetChanged();
    }

    public void setBtn2Enable(boolean enable){
        btn2Enable = enable;
        realAdapter.notifyDataSetChanged();
    }

    public void setDivider(Drawable drawable){
        mainListView.setDivider(drawable);
    }

    public void setDividerHeight(int height){
        mainListView.setDividerHeight(height);
    }
    private void addAndFindViews(){
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.frank_custom_listview_with_slide_delete, null);
        mainListView = (ListView) rootView.findViewById(R.id.custom_listview_with_slide_delete_mainlistview);
        arrowDownImage = (TextView) rootView.findViewById(R.id.custom_listview_with_slide_delete_arrow_down_icon);
        arrowDownText = (TextView) rootView.findViewById(R.id.custom_listview_with_slide_delete_arrow_down_text);
        arrowUpImage = (TextView) rootView.findViewById(R.id.custom_listview_with_slide_delete_arrow_up_icon);
        arrowUpText = (TextView) rootView.findViewById(R.id.custom_listview_with_slide_delete_arrow_up_text);
        listLayout = (LinearLayout) rootView.findViewById(R.id.custom_listview_with_slide_delete_list_layout);
        tophintLayout = (LinearLayout) rootView.findViewById(R.id.custom_listview_with_slide_delete_arrow_down_layout);
        bottomHintLayout = (LinearLayout) rootView.findViewById(R.id.custom_listview_with_slide_delete_arrow_up_layout);
        errorHintContainer = (LinearLayout) rootView.findViewById(R.id.custom_listview_with_slide_delete_error_hint_container);
        this.addView(rootView);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        //TODO 动态算出当前控件高度,并且设置内部listview高度为当前控件高度,可能不支持根据内部listview内容高度动态调整当前控件高度,以后再解决
        mainListView.getLayoutParams().height = specSize;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!hasInitFinish){
            listLayout.scrollTo(0 , tophintLayout.getMeasuredHeight());
            bottomHintLayout.measure(0 , 0);
            bottomHintLayout.getLayoutParams().height = bottomHintLayout.getMeasuredHeight();
            hasInitFinish = true;
        }
    }

    enum SCROLL_TYPE{
                HORIZIONTAL , VERTICAL , UNKNOWN
    }
    float baseTouchX = -1f;
    float baseTouchY = -1f;
    SCROLL_TYPE currentScrollType = SCROLL_TYPE.UNKNOWN;
    MotionEvent lastMotionEvent;
    int horizonScrollIndex = -1;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        if (mOnItemClickListener != null){
            this.mOnItemClickListener = mOnItemClickListener;
        }
    }

    public void setBtn1Attr(int backgroundColor , String text , int textColor , float textSize , int unitType){
        this.btn1BackgroundColor = backgroundColor;
        this.btn1Text = text;
        this.btn1TextColor = textColor;
        this.btn1TextSize = textSize;
        this.btn1TextSizeUnit = unitType;
    }
    public void setBtn2Attr(int backgroundColor , String text , int textColor , float textSize , int unitType){
        this.btn2BackgroundColor = backgroundColor;
        this.btn2Text = text;
        this.btn2TextColor = textColor;
        this.btn2TextSize = textSize;
        this.btn2TextSizeUnit = unitType;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int topHintLayoutHeight = tophintLayout.getHeight();
        int bottomHintLayoutHeight = bottomHintLayout.getMeasuredHeight();
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            baseTouchX = event.getX();
            baseTouchY = event.getY();
            return super.dispatchTouchEvent(event);
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float shiftX = event.getX() - baseTouchX;
            float shiftY = event.getY() - baseTouchY;
            if (currentScrollType == SCROLL_TYPE.UNKNOWN) {
                if (shiftX > 20 || shiftX < -20) {
                    currentScrollType = SCROLL_TYPE.HORIZIONTAL;
                } else if (shiftY > 20 || shiftY < -20) {
                    currentScrollType = SCROLL_TYPE.VERTICAL;
                }
                return super.dispatchTouchEvent(event);
            } else if (currentScrollType == SCROLL_TYPE.HORIZIONTAL) {
                if (horizonScrollIndex == -1){
                    lastMotionEvent = MotionEvent.obtain(event);
                    for (int i = 0 ; i <= mainListView.getLastVisiblePosition() - mainListView.getFirstVisiblePosition() ; i++){
                        if (event.getY() >= mainListView.getChildAt(i).getTop() && event.getY() <= mainListView.getChildAt(i).getBottom()){
                            horizonScrollIndex = i;
                            break;
                        }
                    }
                }
                else {
                    View view = mainListView.getChildAt(horizonScrollIndex);
                    TextView btn1 = (TextView) view.findViewById(R.id.frank_item_custom_listview_btn1);
                    TextView btn2 = (TextView) view.findViewById(R.id.frank_item_custom_listview_btn2);
                    float scrollXchange = lastMotionEvent.getX() - event.getX();
                    if (view.getScrollX() + scrollXchange <= 0){
                        view.scrollTo(0 , 0);
                    }
                    else if (view.getScrollX() + scrollXchange > ((btn1Enable) ? btn1.getWidth() : 0) + ((btn2Enable) ? btn2.getWidth() : 0)){
                        view.scrollTo(((btn1Enable) ? btn1.getWidth() : 0) + ((btn2Enable) ? btn2.getWidth() : 0), 0);
                    }
                    else {
                        view.scrollBy((int)scrollXchange, 0);
                    }
                    lastMotionEvent = MotionEvent.obtain(event);
                }
                return true;
            } else if (currentScrollType == SCROLL_TYPE.VERTICAL) {
                if (lastMotionEvent == null){
                    lastMotionEvent = MotionEvent.obtain(event);
                    return super.dispatchTouchEvent(event);
                }
                else {
                    if (event.getY() - lastMotionEvent.getY() > 0){
                        //当下拉时,优先复位下方正在刷新条
                        if (listLayout.getScrollY() > topHintLayoutHeight){
                            listLayout.scrollBy(0 , (int) (lastMotionEvent.getY() - event.getY()));
                            lastMotionEvent = MotionEvent.obtain(event);
                            return true;
                        }
                        //再把列表切换至页首
                        else if (!isListScrollToFirst()){
                            lastMotionEvent = MotionEvent.obtain(event);
                            return super.dispatchTouchEvent(event);
                        }
                        //再处理下拉刷新显示
                        else {
                            if (currentStatus != STATUS.ADDING_MORE){
                                float scrollY = lastMotionEvent.getY() - event.getY();
                                if (listLayout.getScrollY() >= (topHintLayoutHeight - 100)){
                                    scrollY = (float) (scrollY / 1.1);
                                }
                                else if (listLayout.getScrollY() < (topHintLayoutHeight - 100) && listLayout.getScrollY() >= (topHintLayoutHeight - 200)){
                                    scrollY = (float) (scrollY / 1.5);
                                }
                                else if (listLayout.getScrollY() < (topHintLayoutHeight - 200) && listLayout.getScrollY() >= (topHintLayoutHeight - 300)){
                                    scrollY = (float) (scrollY / 2.0);
                                }
                                else if (listLayout.getScrollY() < (topHintLayoutHeight - 300) && listLayout.getScrollY() >= (topHintLayoutHeight - 400)){
                                    scrollY = (float) (scrollY / 2.5);
                                }
                                else if (listLayout.getScrollY() < (topHintLayoutHeight - 400) && listLayout.getScrollY() >= (topHintLayoutHeight - 500)){
                                    scrollY = (float) (scrollY / 3.0);
                                }
                                else if (listLayout.getScrollY() < (topHintLayoutHeight - 500) && listLayout.getScrollY() >= (topHintLayoutHeight - 600)){
                                    scrollY = (float) (scrollY / 3.5);
                                }
                                else if (listLayout.getScrollY() < (topHintLayoutHeight - 600) && listLayout.getScrollY() >= (topHintLayoutHeight - 700)){
                                    scrollY = (float) (scrollY / 4.0);
                                }
                                else if (listLayout.getScrollY() < (topHintLayoutHeight - 700) && listLayout.getScrollY() >= (topHintLayoutHeight - 800)){
                                    scrollY = (float) (scrollY / 4.5);
                                }
                                else if (listLayout.getScrollY() < (topHintLayoutHeight - 800) && listLayout.getScrollY() >= (topHintLayoutHeight - 900)){
                                    scrollY = (float) (scrollY / 5.0);
                                }
                                else {
                                    scrollY = 0;
                                }
                                listLayout.scrollBy(0 , (int) scrollY);
                                lastMotionEvent = MotionEvent.obtain(event);
                                return true;
                            }
                            else {
                                return super.dispatchTouchEvent(event);
                            }
                        }
                    }
                    else {
                        //当上拉时,优先复位下拉刷新条
                        if (listLayout.getScrollY() < topHintLayoutHeight){
                            listLayout.scrollBy(0 , (int) (lastMotionEvent.getY() - event.getY()));
                            lastMotionEvent = MotionEvent.obtain(event);
                            return true;
                        }
                        //再把列表滚至表尾
                        else if (!isListScrollToLast()){
                            lastMotionEvent = MotionEvent.obtain(event);
                            return super.dispatchTouchEvent(event);
                        }
                        //再处理上拉刷新条
                        else {
                            if (currentStatus != STATUS.REFRESHING){
                                float scrollY = lastMotionEvent.getY() - event.getY();
                                if (listLayout.getScrollY() <= (topHintLayoutHeight + 100)){
                                    scrollY = (float) (scrollY / 2);
                                }
                                else if (listLayout.getScrollY() > (topHintLayoutHeight + 100) && listLayout.getScrollY() <= (topHintLayoutHeight + 200)){
                                    scrollY = (float) (scrollY / 2.5);
                                }
                                else if (listLayout.getScrollY() > (topHintLayoutHeight + 200) && listLayout.getScrollY() <= (topHintLayoutHeight + 300)){
                                    scrollY = (float) (scrollY / 3.0);
                                }
                                else if (listLayout.getScrollY() > (topHintLayoutHeight + 300) && listLayout.getScrollY() <= (topHintLayoutHeight + 400)){
                                    scrollY = (float) (scrollY / 3.5);
                                }
                                else if (listLayout.getScrollY() > (topHintLayoutHeight + 400) && listLayout.getScrollY() <= (topHintLayoutHeight + 500)){
                                    scrollY = (float) (scrollY / 4.0);
                                }
                                else if (listLayout.getScrollY() > (topHintLayoutHeight + 500) && listLayout.getScrollY() <= (topHintLayoutHeight + 600)){
                                    scrollY = (float) (scrollY / 4.5);
                                }
                                else if (listLayout.getScrollY() > (topHintLayoutHeight + 600) && listLayout.getScrollY() <= (topHintLayoutHeight + 700)){
                                    scrollY = (float) (scrollY / 5.0);
                                }
                                else if (listLayout.getScrollY() > (topHintLayoutHeight + 700) && listLayout.getScrollY() <= (topHintLayoutHeight + 800)){
                                    scrollY = (float) (scrollY / 6.0);
                                }
                                else if (listLayout.getScrollY() > (topHintLayoutHeight + 800) && listLayout.getScrollY() <= (topHintLayoutHeight + 900)){
                                    scrollY = (float) (scrollY / 7.0);
                                }
                                else {
                                    scrollY = 0;
                                }
                                listLayout.scrollBy(0 , (int) scrollY);
                                lastMotionEvent = MotionEvent.obtain(event);
                                return true;
                            }
                            else {
                                return super.dispatchTouchEvent(event);
                            }
                        }
                    }
                }
            } else {
                return super.dispatchTouchEvent(event);
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (currentScrollType == SCROLL_TYPE.VERTICAL){
                if (currentStatus == STATUS.READY){
                    if (listLayout.getScrollY() >= (topHintLayoutHeight + bottomHintLayoutHeight)){
                        forceAddmore();
                    }
                    else if (listLayout.getScrollY() <= 0){
                        forceRefresh();
                    }
                    else {
                        listLayout.scrollTo(0 , topHintLayoutHeight);
                    }
                }
                else if (currentStatus == STATUS.ADDING_MORE){
                    if (listLayout.getScrollY() >= (topHintLayoutHeight + bottomHintLayoutHeight)){
                        listLayout.scrollTo(0 , (topHintLayoutHeight + bottomHintLayoutHeight));
                    }
                    else {
                        listLayout.scrollTo(0 , topHintLayoutHeight);
                        currentAsyncTask.cancel(true);
                    }
                }
                else if (currentStatus == STATUS.REFRESHING){
                    if (listLayout.getScrollY() <= 0){
                        listLayout.scrollTo(0 , 0);
                    }
                    else {
                        listLayout.scrollTo(0 , topHintLayoutHeight);
                        currentAsyncTask.cancel(true);
                    }
                }
            }
            else if (currentScrollType == SCROLL_TYPE.HORIZIONTAL){
                if (horizonScrollIndex != -1){
                    View view = mainListView.getChildAt(horizonScrollIndex);
                    TextView btn1 = (TextView) view.findViewById(R.id.frank_item_custom_listview_btn1);
                    TextView btn2 = (TextView) view.findViewById(R.id.frank_item_custom_listview_btn2);
                    if (view.getScrollX() >= ((((btn1Enable) ? btn1.getWidth() : 0) + ((btn2Enable) ? btn2.getWidth() : 0))/2)){
                        view.scrollTo(((btn1Enable) ? btn1.getWidth() : 0) + ((btn2Enable) ? btn2.getWidth() : 0) , 0);
                    }
                    else {
                        view.scrollTo(0 , 0);
                    }
                    horizonScrollIndex = -1;
                }
                currentScrollType = SCROLL_TYPE.UNKNOWN;
                baseTouchX = -1f;
                baseTouchY = -1f;
                lastMotionEvent = null;
                return true;
            }
            currentScrollType = SCROLL_TYPE.UNKNOWN;
            baseTouchX = -1f;
            baseTouchY = -1f;
            lastMotionEvent = null;
            return super.dispatchTouchEvent(event);
        }
        else {
            return super.dispatchTouchEvent(event);
        }
    }

    public void forceAddmore(){
        listLayout.scrollTo(0 , (tophintLayout.getHeight() + bottomHintLayout.getHeight()));
        currentStatus = STATUS.ADDING_MORE;
        currentAsyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                mOnListActionListener.onPreAddMore(mainListView);
            }
            @Override
            protected Object doInBackground(Object[] params) {
                return mOnListActionListener.addmoreDoInBackground(mainListView , this);
            }
            @Override
            protected void onPostExecute(Object o) {
                BackObject backObject = (BackObject) o;
                if (backObject.error == BackObject.Error.SUCCESS){
                    mOnListActionListener.onAddmoreSuccess(mainListView , backObject);
                }
                else {
                    mOnListActionListener.onAddmoreFail(mainListView , backObject);
                }
                realAdapter.notifyDataSetChanged();
                listLayout.scrollTo(0 , tophintLayout.getHeight());
                currentStatus = STATUS.READY;
                currentAsyncTask = null;
                super.onPostExecute(o);
            }

            @Override
            protected void onCancelled() {
                currentStatus = STATUS.READY;
                currentAsyncTask = null;
                listLayout.scrollTo(0 , tophintLayout.getHeight());
                super.onCancelled();
            }
        };
        currentAsyncTask.execute();
    }

    public void forceRefresh(){
        hideErrorHint();
        listLayout.scrollTo(0 , 0);
        currentStatus = STATUS.REFRESHING;
        currentAsyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                mOnListActionListener.onPreRefresh(mainListView);
            }
            @Override
            protected Object doInBackground(Object[] params) {
                return mOnListActionListener.refreshDoInBackground(mainListView , this);
            }
            @Override
            protected void onPostExecute(Object o) {
                BackObject backObject = (BackObject) o;
                if (backObject.error == BackObject.Error.SUCCESS){
                    mOnListActionListener.onRefreshSuccess(mainListView , backObject);
                }
                else {
                    mOnListActionListener.onRefreshFail(mainListView , backObject);
                }
                realAdapter.notifyDataSetChanged();
                listLayout.scrollTo(0 , tophintLayout.getHeight());
                currentStatus = STATUS.READY;
                currentAsyncTask = null;
                mainListView.setSelection(0);
                super.onPostExecute(o);
            }
            @Override
            protected void onCancelled() {
                currentStatus = STATUS.READY;
                currentAsyncTask = null;
                listLayout.scrollTo(0 , tophintLayout.getHeight());
            }
        };
        currentAsyncTask.execute();
    }


    private void initMainListView(){
        realAdapter = new MyAdapter();
        mainListView.setAdapter(realAdapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnItemClickListener.onItemClick(position);
            }
        });
    }

    private boolean isListScrollToFirst(){
        View firstVisibleView = mainListView.getChildAt(0);
        if (firstVisibleView == null) {
            Log.e(LOG_TAG , "一个都没有，当然到头啦！");
            return true;
        } else if (mainListView.getFirstVisiblePosition() == 0 && firstVisibleView.getTop() == 0) {
            Log.e(LOG_TAG , "到头啦");
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isListScrollToLast(){
        View lastVisibleView = mainListView.getChildAt(mainListView.getChildCount() - 1);
        if (lastVisibleView == null) {
            Log.e(LOG_TAG, "一个都没有，当然到尾啦！");
            return true;
        } else if (mainListView.getLastVisiblePosition() == (mainListView.getAdapter().getCount() - 1)
                && lastVisibleView.getBottom() <= mainListView.getHeight()) {
            Log.e(LOG_TAG, "到尾啦");
            return true;
        }
        else {
            return false;
        }
    }

    public CustomListviewWithSlideItem setAdapter(BaseAdapter mAdapter) {
        this.customAdapter = mAdapter;
        return this;
    }

    public BaseAdapter getAdapter() {
        return customAdapter;
    }

    public void notifyDataSetChanged(){
        realAdapter.notifyDataSetChanged();
    }

    public CustomListviewWithSlideItem setOnListActionListener(OnListActionListener mOnListActionListener) {
        if (mOnListActionListener != null){
            this.mOnListActionListener = mOnListActionListener;
        }
        return this;
    }

    public void showErrorHint(View errorHintView){
        errorHintContainer.removeAllViews();
        errorHintContainer.addView(errorHintView, ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        errorHintContainer.setVisibility(View.VISIBLE);
    }
    public void hideErrorHint(){
        errorHintContainer.removeAllViews();
        errorHintContainer.setVisibility(GONE);
    }



    public interface OnListActionListener{
        public void onPreRefresh(ListView listView);
        public BackObject refreshDoInBackground(ListView listView, AsyncTask asyncTask);
        public void onRefreshSuccess(ListView listView, BackObject backObject);
        public void onRefreshFail(ListView listView, BackObject backObject);
        public void onPreAddMore(ListView listView);
        public BackObject addmoreDoInBackground(ListView listView, AsyncTask asyncTask);
        public void onAddmoreSuccess(ListView listView, BackObject backObject);
        public void onAddmoreFail(ListView listView, BackObject backObject);
    }

    public interface OnItemClickListener{
        public void onItemClick(int position);
        public void onBtn1Click(int position);
        public void onBtn2Click(int position);
    }


    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (customAdapter != null){
                return customAdapter.getCount();
            }
            else {
                return 0;
            }
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = layoutInflater.inflate(R.layout.frank_item_custom_listview, null);
            }
            convertView.scrollTo(0 ,0);
            LinearLayout container = (LinearLayout) convertView.findViewById(R.id.frank_item_custom_listview_content);
            TextView btn1 = (TextView) convertView.findViewById(R.id.frank_item_custom_listview_btn1);
            btn1.setBackgroundColor(btn1BackgroundColor);
            btn1.setText(btn1Text);
            btn1.setTextColor(btn1TextColor);
            btn1.setTextSize(btn1TextSizeUnit , btn1TextSize);
            TextView btn2 = (TextView) convertView.findViewById(R.id.frank_item_custom_listview_btn2);
            btn2.setBackgroundColor(btn2BackgroundColor);
            btn2.setText(btn2Text);
            btn2.setTextColor(btn2TextColor);
            btn2.setTextSize(btn2TextSizeUnit , btn2TextSize);
            if (btn1Enable){
                btn1.setVisibility(VISIBLE);
            }
            else {
                btn1.setVisibility(GONE);
            }
            if (btn2Enable){
                btn2.setVisibility(VISIBLE);
            }
            else {
                btn2.setVisibility(GONE);
            }
            btn1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onBtn1Click(position);
                }
            });
            btn2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onBtn2Click(position);
                }
            });
            if (customAdapter != null){
                View contentView = customAdapter.getView(position , container.getChildAt(0), parent);
                container.removeAllViews();
                container.addView(contentView);
            }
            return convertView;
        }
    }
}