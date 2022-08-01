package com.fight2048.aweb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.fight2048.aweb.databinding.FragmentWebBinding;
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
public class WebFragment extends Fragment {
    private static final String TAG = WebFragment.class.getSimpleName();
    public static final String URL = "url";
    private FragmentWebBinding binding;
    protected AgentWeb mAgentWeb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWebBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
        initWebView();
        initListener();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> handleNavigation());
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
            } else if (item.getItemId() == R.id.web_go2browser) {
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
        Bundle bundle = getArguments();
        if (bundle != null) {
            String url = bundle.getString("url");
            Log.d(TAG, "url==>" + url);
            mAgentWeb = preAgentWeb.go(url);
        }
    }

    private void initListener() {
        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        handleNavigation();
                    }
                });
    }

    private void handleNavigation() {
        if (mAgentWeb == null) {
            return;
        }
        WebView webView = mAgentWeb.getWebCreator().getWebView();
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // TODO: 2022-08-01 0001 还是得分开，因为别人启动activity的时候，极有可能不是通过navigation启动的，所以会报错
//            Navigation.findNavController(getView()).popBackStack();
            ActivityCompat.finishAffinity(getActivity());
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
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroy();
    }

    private void share(String title, String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, title));
        }
    }

    private void go2Browser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public static Bundle bundle(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(URL, url);
        return bundle;
    }

    public static WebFragment of(Bundle bundle) {
        WebFragment fragment = new WebFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static WebFragment of(String url) {
        WebFragment fragment = new WebFragment();
        fragment.setArguments(bundle(url));
        return fragment;
    }
}
