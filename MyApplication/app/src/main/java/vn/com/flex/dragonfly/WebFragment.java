package vn.com.flex.dragonfly;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import vn.com.flex.dragonfly.lib.AnimationLinearLayout;

/**
 * Created by Nguyen on 12/22/2015.
 */
public class WebFragment extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        if (getIntent().getExtras().getString("url") != null && getIntent().getExtras().getString("title") != null) {
            WebView web = (WebView) findViewById(R.id.web_view);
            web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            web.getSettings().setJavaScriptEnabled(true);
            web.loadUrl(getIntent().getExtras().getString("url"));
            web.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            TextView title = (TextView) findViewById(R.id.txt_title);
            title.setText(getIntent().getExtras().getString("title"));
        }
        AnimationLinearLayout btnBack = (AnimationLinearLayout) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
