package com.scwang.refreshlayout.activity.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.scwang.refreshlayout.R;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

/**
 * 越界回弹使用演示
 */
public class OverScrollExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_overscroll);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        final WebView webView = findViewById(R.id.webView);
        final RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener((OnRefreshListener) refreshLayout1 -> webView.loadUrl("http://github.com"));
        refreshLayout.autoRefresh();


        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                refreshLayout.finishRefresh();
            }
        });
//        TextView textView = findViewById(R.id.textView);
//        textView.setMovementMethod(new ScrollingMovementMethod());
    }

}
