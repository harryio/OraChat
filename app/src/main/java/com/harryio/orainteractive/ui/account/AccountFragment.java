package com.harryio.orainteractive.ui.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.harryio.orainteractive.PrefUtils;
import com.harryio.orainteractive.R;
import com.harryio.orainteractive.rest.OraService;
import com.harryio.orainteractive.rest.OraServiceProvider;
import com.harryio.orainteractive.ui.auth.AuthResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class AccountFragment extends Fragment {
    private static final String TAG = "AccountFragment";

    @BindView(R.id.progressView)
    ProgressBar progressView;
    @BindView(R.id.name)
    TextInputEditText nameEditText;
    @BindView(R.id.email)
    TextInputEditText emailEditText;
    @BindView(R.id.password)
    TextInputEditText passwordEditText;
    @BindView(R.id.confirm)
    TextInputEditText confirmEditText;
    @BindView(R.id.contentView)
    LinearLayout contentView;

    private CompositeSubscription subscriptions;
    private String infoFetchError = "Could not fetch user details";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeSubscription();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PrefUtils prefUtils = PrefUtils.getInstance(getContext());
        String token = prefUtils.get(PrefUtils.KEY_AUTH_TOKEN, null);
        if (token != null) {
            fetchAccountInfo(token);
        }
    }

    private void fetchAccountInfo(String authToken) {
        OraService oraService = OraServiceProvider.getInstance();
        Subscription subscription = oraService.viewProfile(authToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AuthResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showMessage(infoFetchError);
                        contentView.setVisibility(View.VISIBLE);
                        progressView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(AuthResponse authResponse) {
                        contentView.setVisibility(View.VISIBLE);
                        progressView.setVisibility(View.GONE);

                        if (authResponse.isSuccess()) {
                            AuthResponse.Data data = authResponse.getData();
                            nameEditText.setText(data.getName());
                            emailEditText.setText(data.getEmail());
                        } else {
                            showMessage(infoFetchError);
                        }
                    }
                });
        subscriptions.add(subscription);
    }

    private void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.save)
    public void onClick() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }
}
