package com.mini.ass.kindo;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    private View mRoot;
    private WebView mWebView;
    private RepairShop mRepairShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mRoot = this.findViewById(R.id.root);

        this.setupActionBar();

        final String appUrl = getString(R.string.app_site);
        this.mWebView = this.setupBaseWebView(this);
        this.mRepairShop = this.setupRepairShop();

        this.mWebView.loadUrl(appUrl);
    }

    private void setupActionBar() {
        ActionBar action = getSupportActionBar();
        action.hide();
    }

    private WebView setupBaseWebView(MainActivity main) {
        WebView web = main.findViewById(R.id.webView);
        web.setWebViewClient(this.getWebViewClient(web, true));

        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        return web;
    }

    public WebViewClient getWebViewClient(final WebView web, final boolean doRepairs) {
        final MainActivity self = this;

        final Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.3f);
        fadeOutAnimation.setDuration(600);
        fadeOutAnimation.setFillAfter(true);

        final Animation fadeInAnimation = new AlphaAnimation(0.3f, 1.0f);
        fadeInAnimation.setDuration(600);
        fadeInAnimation.setFillAfter(true);

        return new WebViewClient() {
            private Snackbar snackbar = null;
            private boolean firstScreen = true;
            private boolean pseudoConstructor = false;

            private void construct() {
                if (!this.pseudoConstructor) {
                    this.snackbar = Snackbar.make(self.mRoot, R.string.progress_loading_title, Snackbar.LENGTH_INDEFINITE);
                    ViewGroup table = (ViewGroup) this.snackbar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();

                    ProgressBar progress = new ProgressBar(self);
                    progress.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

                    int diameter = (int) self.getResources().getDimension(R.dimen.progress_loading_diameter);
                    table.addView(progress, diameter, diameter);
                    ((LinearLayout) table).setGravity(Gravity.CENTER_VERTICAL);
                }

                this.pseudoConstructor = true;
            }

            private View.OnTouchListener getTouchy(final boolean returns) {
                return new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return returns;
                    }
                };
            }

            private void showLoadingUI() {
                this.construct();

                self.mWebView.setOnTouchListener(this.getTouchy(true));
                self.mWebView.startAnimation(fadeOutAnimation);

                this.snackbar.show();
            }

            private void hideLoadingUI() {
                if (this.snackbar.isShown()) {
                    this.snackbar.dismiss();
                    self.mWebView.startAnimation(fadeInAnimation);
                    self.mWebView.setOnTouchListener(this.getTouchy(false));
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (this.snackbar != null)
                    this.hideLoadingUI();

                if (doRepairs) {
                    String js = "javascript:" + self.mRepairShop.getRepairSauce();
                    view.loadUrl(js);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (this.firstScreen) {
                    this.showLoadingUI();
                    this.firstScreen = false;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                if (this.snackbar != null)
                    if (this.snackbar.isShown())
                        this.snackbar.dismiss();

                this.showLoadingUI();
                return false;
            }

        };
    }

    private RepairShop setupRepairShop() {
        RepairShop repairShop = new RepairShop(this);

        String[] dropRepairs = getResources().getStringArray(R.array.drop_repairs);
        for (int l = 0; l < dropRepairs.length; l++)
            repairShop.add(dropRepairs[l], RepairShop.RepairType.DROP);

        return repairShop;
    }

    @Override
    public void onBackPressed(){
        if (this.mWebView.canGoBack()){
            this.mWebView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}
