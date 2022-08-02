package com.fight2048.aweb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fight2048.aweb.databinding.LayoutWebBinding;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

/**
 * @author: fight2048
 * @e-mail: fight2048@outlook.com
 * @blog: https://github.com/fight2048
 * @time: 2020-03-07 0007 下午 10:46
 * @version: v0.0.0
 * @description: 负责项目中的web部分。
 */
public class WebActivity extends AppCompatActivity {
    public static final String TAG = WebActivity.class.getSimpleName();
    public static final String URL = "url";
    private LayoutWebBinding binding;
    protected AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutWebBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initWebView();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.toolbar.inflateMenu(R.menu.menu_web);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.web_share) {
                if (mAgentWeb != null
                        && mAgentWeb.getWebCreator() != null) {
                    String url = mAgentWeb.getWebCreator().getWebView().getUrl();
                    share(getString(R.string.web_share), url);
                }
            } else if (item.getItemId() == R.id.web_refresh) {
                if (mAgentWeb != null
                        && mAgentWeb.getUrlLoader() != null) {
                    mAgentWeb.getUrlLoader().reload();
                }
            } else if (item.getItemId() == R.id.web_browser) {
                if (mAgentWeb != null
                        && mAgentWeb.getWebCreator() != null) {
                    String url = mAgentWeb.getWebCreator().getWebView().getUrl();
                    go2Browser(url);
                }
            }
            return true;
        });
    }

    private void initWebView() {
        AgentWeb.PreAgentWeb preAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(binding.getRoot(), -1,
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//传入AgentWeb的父控件。
                .useDefaultIndicator()
                .setMainFrameErrorView(R.layout.layout_state_empty, R.id.iv_empty_state)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
//                        String title = view.getTitle();
                        binding.toolbar.setSubtitle(url);
                    }
                })
                .setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        binding.toolbar.setTitle(title);
                    }
                })
                .createAgentWeb();
        String url = getUrlFromIntent();
        Log.d(TAG, "url==>" + url);
        mAgentWeb = preAgentWeb.go(url);
    }

    private String getUrlFromIntent() {
        //目的是为了兼容两种启动方式
        //1.指定启动，这种的url是存Bundle
        //2.隐式启动，与浏览器行为一致，url都是放date里
        Intent intent = getIntent();
        Uri uri = intent.getData();
        Bundle bundle = intent.getExtras();
        String url = null;
        if (Intent.ACTION_VIEW.equals(intent.getAction())
                && uri != null) {
            url = uri.toString();
        } else if (bundle != null) {
            url = bundle.getString(URL);
        }
        return url;
    }

    @Override
    public void onBackPressed() {
        if (mAgentWeb == null) {
            ActivityCompat.finishAfterTransition(this);
        } else {
            WebView webView = mAgentWeb.getWebCreator().getWebView();
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                ActivityCompat.finishAfterTransition(this);
            }
        }
    }

    @Override
    public void onPause() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        binding = null;
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroy();
    }

    private void share(String title, String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, title));
        }
    }

    private void go2Browser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public static void start(@NonNull Context context, @NonNull String url, @Nullable Bundle options) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(WebFragment.URL, url);
        ContextCompat.startActivity(context, intent, options);
    }

    public static void start(@NonNull Context context, @NonNull String url) {
        start(context, url, null);
    }
}