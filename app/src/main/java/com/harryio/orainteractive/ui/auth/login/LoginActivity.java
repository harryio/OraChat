package com.harryio.orainteractive.ui.auth.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.harryio.orainteractive.PrefUtils;
import com.harryio.orainteractive.R;
import com.harryio.orainteractive.Utils;
import com.harryio.orainteractive.rest.OraService;
import com.harryio.orainteractive.rest.OraServiceProvider;
import com.harryio.orainteractive.ui.MainActivity;
import com.harryio.orainteractive.ui.auth.AuthResponse;
import com.harryio.orainteractive.ui.auth.register.RegisterActivity;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.harryio.orainteractive.PrefUtils.KEY_AUTH_TOKEN;
import static com.harryio.orainteractive.PrefUtils.KEY_IS_LOGGED_IN;
import static com.harryio.orainteractive.PrefUtils.KEY_USER_ID;
import static com.harryio.orainteractive.Utils.showMessage;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.email)
    TextInputEditText emailEditText;
    @BindView(R.id.password)
    TextInputEditText passwordEditText;
    @BindString(R.string.login_successful_message)
    String loginSuccessfulMessage;
    @BindString(R.string.login_failed_message)
    String loginErrorMessage;
    @BindString(R.string.login_progress_message)
    String loginProgressMessage;
    @BindString(R.string.error_no_internet_connection)
    String noConnectionError;

    private Subscription loginSubscription;
    private ProgressDialog progressDialog;
    private PrefUtils prefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(loginProgressMessage);
        prefUtils = PrefUtils.getInstance(this);
    }

    @OnClick({R.id.login, R.id.register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                if (Utils.isNetworkAvailable(this)) {
                    progressDialog.show();

                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    OraService service = OraServiceProvider.getInstance();
                    LoginRequest loginRequest = new LoginRequest(email, password);
                    loginSubscription = service.login(loginRequest)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<AuthResponse>() {
                                @Override
                                public void onCompleted() {
                                    if (loginSubscription != null && !loginSubscription.isUnsubscribed()) {
                                        loginSubscription.unsubscribe();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }

                                    showMessage(LoginActivity.this, loginErrorMessage);
                                }

                                @Override
                                public void onNext(AuthResponse authResponse) {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }

                                    if (authResponse.isSuccess()) {
                                        onSuccessfulLogin(authResponse);
                                    } else {
                                        showMessage(LoginActivity.this, loginErrorMessage);
                                    }
                                }
                            });
                } else {
                    showMessage(this, noConnectionError);
                }

                break;

            case R.id.register:
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                break;
        }
    }

    private void onSuccessfulLogin(AuthResponse authResponse) {
        prefUtils.put(KEY_AUTH_TOKEN,
                "Bearer " + authResponse.getData().getToken());
        prefUtils.put(KEY_USER_ID, authResponse.getData().getId());
        prefUtils.put(KEY_IS_LOGGED_IN, true);

        showMessage(this, loginSuccessfulMessage);

        Intent intent = Utils.getClearStackIntent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //unsubscribe from observable to avoid memory leak
        if (loginSubscription != null && !loginSubscription.isUnsubscribed()) {
            loginSubscription.unsubscribe();
        }
    }
}