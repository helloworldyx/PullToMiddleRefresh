package com.nope.hyx.pulltomiddlerefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.nope.hyx.pulltomiddlerefresh.view.PullToMiddleRefreshListView;
import com.nope.hyx.pulltomiddlerefresh.view.RefreshHeaderContentView;
import com.nope.hyx.pulltomiddlerefresh.view.RefreshView;

public class MainActivity extends AppCompatActivity {

    private PullToMiddleRefreshListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (PullToMiddleRefreshListView) findViewById(R.id.lv_pull_to_middle_refresh);
        View headerView = LayoutInflater.from(this).inflate(R.layout.layout_header, null);
        RefreshHeaderContentView headerContentView = (RefreshHeaderContentView) headerView.findViewById(R.id.header_content_view);
        listView.addHeaderView(headerView);

        RefreshView refreshView = new RefreshView(this);
        refreshView.setOriginalHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        listView.addHeaderView(refreshView);

        listView.setRefreshView(refreshView);
        listView.setHeaderContentView(headerContentView);
        listView.setAdapter(new MyAdapter(this));

        listView.setOnRefreshListener(new PullToMiddleRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.setRefreshComplete("宝宝刷新完咯~");
                    }
                }, 2000);
            }
        });
    }
}
