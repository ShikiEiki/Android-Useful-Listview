package com.frank.demo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.frank.BackObject;
import com.frank.CustomListviewWithSlideItem;


/**
 * Created by fuhan on 2016/12/11.
 */

public class MainActivity extends Activity{
    CustomListviewWithSlideItem mainListview;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainListview = (CustomListviewWithSlideItem)findViewById(R.id.main_list);
        //设置adapter支持自定义adapter
        mainListview.setAdapter(new MyAdapter());
        //设置启用左划菜单按钮1,默认关闭
        mainListview.setBtn1Enable(true);
        //设置启用左划菜单按钮2,默认关闭
        mainListview.setBtn2Enable(true);
        //设置list分割线样式和高度.
        mainListview.setDivider(new ColorDrawable(Color.GRAY));
        mainListview.setDividerHeight(10);
        //设置list上拉下拉动作回调
        mainListview.setOnListActionListener(new CustomListviewWithSlideItem.OnListActionListener() {
            @Override
            public void onPreRefresh(ListView listView) {
                //此处可以做一些刷新前的动作,如显示loading,更改界面内容等,本方法内的操作会在UI线程中完成.
            }
            @Override
            public BackObject refreshDoInBackground(ListView listView, AsyncTask asyncTask) {
                //此处可以做一些刷新耗时操作,如网络和IO操作等,本方法内的操作会在异步线程中完成.
                //必须返回一个BackObject,如果返回null,会抛出NullPointerException.
                try {
                    //本处线程休眠2s来模拟网络耗时操作
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return new BackObject(BackObject.Error.SUCCESS);
            }
            @Override
            public void onRefreshSuccess(ListView listView, BackObject backObject) {
                //此处可以做一些刷新成功后的操作,如notifyDataSetChanged等,本方法内的操作会在UI线程中完成.
            }
            @Override
            public void onRefreshFail(ListView listView, BackObject backObject) {
                //此处可以做一些刷新失败后的操作,如提示错误信息,本方法内的操作会在UI线程中完成.
            }
            @Override
            public void onPreAddMore(ListView listView) {
                //此处可以做一些加载更多内容前的动作,如显示loading,更改界面内容等,本方法内的操作会在UI线程中完成.

            }
            @Override
            public BackObject addmoreDoInBackground(ListView listView, AsyncTask asyncTask) {
                //此处可以做一些加载更多时的耗时操作,如网络和IO操作等,本方法内的操作会在异步线程中完成.
                //必须返回一个BackObject,如果返回null,会抛出NullPointerException.
                try {
                    //本处线程休眠2s来模拟网络耗时操作
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return new BackObject(BackObject.Error.SUCCESS);
            }
            @Override
            public void onAddmoreSuccess(ListView listView, BackObject backObject) {
                //此处可以做一些加载更多成功后的操作,如notifyDataSetChanged等,本方法内的操作会在UI线程中完成.
            }
            @Override
            public void onAddmoreFail(ListView listView, BackObject backObject) {
                //此处可以做一些加载更多失败后的操作,如提示错误信息,本方法内的操作会在UI线程中完成.
            }
        });
        //设置list点击监听器
        mainListview.setOnItemClickListener(new CustomListviewWithSlideItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                popToast("点击了第" + position + "行");
            }
            @Override
            public void onBtn1Click(int position) {
                popToast("点击了第" + position + "行的第一个按钮");
            }
            @Override
            public void onBtn2Click(int position) {
                popToast("点击了第" + position + "行的第二个按钮");
            }
        });
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 50;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.my_item , null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.textview);
            textView.setText("item" + position);
            return convertView;
        }
    }

    public void popToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast != null){
                    toast.cancel();
                }
                toast = Toast.makeText(MainActivity.this , msg , Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
