package com.harryio.orainteractive.ui.auth.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.harryio.orainteractive.PrefUtils;
import com.harryio.orainteractive.R;
import com.harryio.orainteractive.Utils;
import com.harryio.orainteractive.rest.OraService;
import com.harryio.orainteractive.rest.OraServiceProvider;
import com.harryio.orainteractive.ui.MainActivity;
import com.harryio.orainteractive.ui.auth.AuthResponse;
import com.harryio.orainteractive.ui.auth.login.LoginActivity;

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

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.name)
    TextInputEditText nameEdittext;
    @BindView(R.id.email)
    TextInputEditText emailEdittext;
    @BindView(R.id.password)
    TextInputEditText passwordEdittext;
    @BindView(R.id.confirm)
    TextInputEditText confirmEdittext;
    @BindString(R.string.register_successful_message)
    String registerSuccessfulMessage;
    @BindString(R.string.register_failed_message)
    String registerFailedMessage;
    @BindString(R.string.register_progress_message)
    String registerProgressMessage;
    @BindString(R.string.error_no_internet_connection)
    String noConnectionMessage;

    private ProgressDialog progressDialog;

    private PrefUtils prefUtils;
    private Subscription registerSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        //Create dialog which is shown when making register api call
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(registerProgressMessage);

        prefUtils = PrefUtils.getInstance(this);
    }

    @OnClick({R.id.register, R.id.login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                if (Utils.isNetworkAvailable(this)) {
                    //Show register progress dialog before making api call
                    progressDialog.show();

                    //Get user's credentials
                    String name = nameEdittext.getText().toString();
                    String email = emailEdittext.getText().toString();
                    String password = passwordEdittext.getText().toString();
                    String confirm = confirmEdittext.getText().toString();
                    RegisterRequest registerRequest = new RegisterRequest(name, email,
                            password, confirm);

                    //Make register api call
                    OraService oraService = OraServiceProvider.getInstance();
                    registerSubscription = oraService.register(registerRequest)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<AuthResponse>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    //Dismiss register progress dialog
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }

                                    //Notify user that the call failed
                                    Utils.showMessage(RegisterActivity.this, registerFailedMessage);
                                }

                                @Override
                                public void onNext(AuthResponse authResponse) {
                                    //Dismiss register progress dialog
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }

                                    if (authResponse.isSuccess()) {
                                        //User is successfully registered
                                        onSuccessfulRegister(authResponse);
                                    } else {
                                        //Notify user that the call failed
                                        Utils.showMessage(RegisterActivity.this, registerFailedMessage);
                                    }
                                }
                            });
                } else {
                    //Notify user that there is no internet connection
                    Utils.showMessage(this, noConnectionMessage);
                }

                break;

            case R.id.login:
                //Launch login screen
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                break;
        }
    }

    private void onSuccessfulRegister(AuthResponse authResponse) {
        //Save auth-token
        prefUtils.put(KEY_AUTH_TOKEN, "Bearer " + authResponse.getData().getToken());
        //Save user's id
        prefUtils.put(KEY_USER_ID, authResponse.getData().getId());
        //Set user's status as logged in
        prefUtils.put(KEY_IS_LOGGED_IN, true);

        //Notify user that new account is successfully created
        Utils.showMessage(RegisterActivity.this, registerSuccessfulMessage);

        //Close RegisterActivity and launch MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //unsubscribe from observable to avoud memory leaks
        if (registerSubscription != null && !registerSubscription.isUnsubscribed()) {
            registerSubscription.unsubscribe();
        }
    }
}
