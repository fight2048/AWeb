package com.fight2048.aweb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //目的是为了兼容两种启动方式
        //1.指定启动，这种的url是存Bundle
        //2.隐式启动，与浏览器行为一致，url都是放date里
        Intent intent = getIntent();
        Uri uri = intent.getData();
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }

        if (Intent.ACTION_VIEW.equals(intent.getAction()) || uri != null) {
            bundle.putString(WebFragment.URL, uri.toString());
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, WebFragment.of(bundle))
                .commit();
    }

    public static void start(Activity activity, String url) {
        Intent intent = new Intent(activity, WebActivity.class);
        intent.putExtra(WebFragment.URL, url);
        ContextCompat.startActivity(activity, intent, null);
    }
}