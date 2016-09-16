package com.harryio.orainteractive.ui.account;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.harryio.orainteractive.PrefUtils;
import com.harryio.orainteractive.R;
import com.harryio.orainteractive.rest.OraService;
import com.harryio.orainteractive.rest.OraServiceProvider;
import com.harryio.orainteractive.ui.auth.AuthResponse;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.harryio.orainteractive.PrefUtils.KEY_AUTH_TOKEN;

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
    @BindString(R.string.fetch_user_details_error)
    String infoFetchError;
    @BindString(R.string.edit_user_details_successful)
    String profileEditSuccessfulMessage;
    @BindString(R.string.edit_user_details_error)
    String profileEditFailedMessage;
    @BindView(R.id.error_message)
    TextView errorTextView;
    @BindView(R.id.error_view)
    LinearLayout errorView;
    @BindView(R.id.save)
    Button save;

    private CompositeSubscription subscriptions;
    private OnFragmentInteractionListener listener;
    private OraService oraService;
    private PrefUtils prefUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeSubscription();
        oraService = OraServiceProvider.getInstance();
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

        prefUtils = PrefUtils.getInstance(getContext());
        fetchAccountInfo();
    }

    private void fetchAccountInfo() {
        showLoadingView();

        String token = prefUtils.get(KEY_AUTH_TOKEN, null);
        if (token != null) {
            Subscription subscription = oraService.viewProfile(token)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<AuthResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            showErrorView(infoFetchError);
                        }

                        @Override
                        public void onNext(AuthResponse authResponse) {
                            if (authResponse.isSuccess()) {
                                setUpViews(authResponse.getData());
                                showContentView();
                            } else {
                                showErrorView(infoFetchError);
                            }
                        }
                    });
            subscriptions.add(subscription);
        }
    }

    private void setUpViews(AuthResponse.Data data) {
        nameEditText.setText(data.getName());
        emailEditText.setText(data.getEmail());
    }

    @OnClick(R.id.save)
    public void onClick() {
        listener.showProfileEditDialog();

        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirm = confirmEditText.getText().toString();

        EditProfileRequest request = new EditProfileRequest(name, email, password, confirm);
        String token = prefUtils.get(KEY_AUTH_TOKEN, null);
        if (token != null) {
            Subscription subscription = oraService.editProfile(token, request)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<AuthResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            listener.showMessage(profileEditFailedMessage);
                            listener.hideProfileEditDialog();
                        }

                        @Override
                        public void onNext(AuthResponse authResponse) {
                            listener.hideProfileEditDialog();

                            if (authResponse.isSuccess()) {
                                listener.showMessage(profileEditSuccessfulMessage);
                                setUpViews(authResponse.getData());
                            } else {
                                listener.showMessage(profileEditFailedMessage);
                            }
                        }
                    });
            subscriptions.add(subscription);
        }
    }

    @OnClick(R.id.retry)
    public void onRetryButtonClick() {
        fetchAccountInfo();
    }

    private void showLoadingView() {
        contentView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
    }

    private void showErrorView(String errorMessage) {
        errorTextView.setText(errorMessage);
        contentView.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    private void showContentView() {
        progressView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else
            throw new IllegalStateException("OnFragmentInteractionListener not implemented");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }

    public interface OnFragmentInteractionListener {
        void showMessage(String message);

        void showProfileEditDialog();

        void hideProfileEditDialog();
    }
}
