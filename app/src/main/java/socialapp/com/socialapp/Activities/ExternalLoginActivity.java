package socialapp.com.socialapp.Activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.squareup.otto.Subscribe;

import socialapp.com.socialapp.R;
import socialapp.com.socialapp.Services.Account;
import socialapp.com.socialapp.infrastructure.YoraApplication;

public class ExternalLoginActivity extends BaseActivity {


    public static final String EXTRA_EXTERNAL_SERVICE = "EXTRA_EXTERNAL_SERVICE";


    private static final int REQUEST_REGISTER = 1;

    private WebView webView;
    private String service;
    private View progressFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        CookieManager.getInstance().removeAllCookie();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_login);
        webView = (WebView) findViewById(R.id.activity_external_login_webView);


        service = getIntent().getStringExtra(EXTRA_EXTERNAL_SERVICE);

        progressFrame = findViewById(R.id.activity_external_login_progressFrame);

        progressFrame.setVisibility(View.GONE);

        webView.loadUrl(YoraApplication.API_ENDPOINT
            + "api/v1/auth/externallogin?provider=" + service +
                "&response_type=token&client_id=android&redirect_url=https%3A%2F%2F" + YoraApplication.API_ENDPOINT.getHost() + "%2Fmobile-login" +
                "&studentToken=" + YoraApplication.STUDENT_TOKEN
        );

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);

                if (uri.getHost().equals(YoraApplication.API_ENDPOINT.getHost()) && uri.getPath().equals("/mobile-login")) {
                    externalLoginSuccess(uri);
                    return true;
                }

                return false;
            }
        });

    }

    private void externalLoginSuccess(Uri uri) {
        String externalToken = uri.getQueryParameter("externalAccessToken");
        String provider = uri.getQueryParameter("provider");
        boolean hasLocalAccount = uri.getQueryParameter("hasLocalAccount").equalsIgnoreCase("true");
        String externalUsername = uri.getQueryParameter("externalUsername");

        if (!hasLocalAccount) {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra(RegisterActivity.EXTRA_EXTERNAL_PROVIDER, provider);
            intent.putExtra(RegisterActivity.EXTRA_EXTERNAL_USERNAME, externalUsername);
            intent.putExtra(RegisterActivity.EXTRA_EXTERNAL_TOKEN, externalToken);
            startActivityForResult(intent, REQUEST_REGISTER);
        } else {
            progressFrame.setVisibility(View.VISIBLE);
            bus.post(new Account.LoginWithExternalTokenRequest(provider, externalToken));

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == REQUEST_REGISTER) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Subscribe
    public void onFindLogin(Account.LoginWithExternalTokenResponse response ) {
        if (!response.didSucceed()) {
            progressFrame.setVisibility(View.GONE);
            response.showErrorToast(this);
            return;
        }

        setResult(RESULT_OK);
        finish();
    }
}
