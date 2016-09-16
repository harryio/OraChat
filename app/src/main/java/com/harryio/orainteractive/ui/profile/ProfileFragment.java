package com.harryio.orainteractive.ui.profile;

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
import com.harryio.orainteractive.Utils;
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

public class ProfileFragment extends Fragment {
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
    @BindView(R.id.error_message)
    TextView errorTextView;
    @BindView(R.id.error_view)
    LinearLayout errorView;
    @BindView(R.id.save)
    Button save;
    @BindString(R.string.fetch_user_details_error)
    String infoFetchError;
    @BindString(R.string.edit_user_details_successful)
    String profileEditSuccessfulMessage;
    @BindString(R.string.edit_user_details_error)
    String profileEditFailedMessage;
    @BindString(R.string.error_no_internet_connection)
    String noConnectionMessage;

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
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prefUtils = PrefUtils.getInstance(getContext());
        fetchAccountInfo();
    }

    /*
    Fetch account information from the network
     */
    private void fetchAccountInfo() {
        if (Utils.isNetworkAvailable(getActivity())) {
            //Show loading view before fetching account info
            showLoadingView();

            String token = prefUtils.get(KEY_AUTH_TOKEN, null);
            if (token != null) {
                //Make api call to fetch account info
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
                                    //Account information was successfully fetched
                                    //Set fetched information to the corresponding views
                                    setUpViews(authResponse.getData());
                                    showContentView();
                                } else {
                                    //Failed to fetch account information so show error view
                                    showErrorView(infoFetchError);
                                }
                            }
                        });
                subscriptions.add(subscription);
            }
        } else {
            //No internet connection
            showErrorView(noConnectionMessage);
        }
    }

    /*
    Set data fetched from the network to corresponding views
     */
    private void setUpViews(AuthResponse.Data data) {
        nameEditText.setText(data.getName());
        emailEditText.setText(data.getEmail());
    }

    /*
    Save updated profile to network
     */
    @OnClick(R.id.save)
    public void onClick() {
        if (Utils.isNetworkAvailable(getActivity())) {
            //Show profile editing dialog before making api call to save updated profile
            listener.showProfileEditDialog();

            //Get updated user's credentials
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirm = confirmEditText.getText().toString();

            //Make network call
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
                                //Notify user that the api call failed
                                listener.showMessage(profileEditFailedMessage);
                                //Dismiss profile editing dialog
                                listener.hideProfileEditDialog();
                            }

                            @Override
                            public void onNext(AuthResponse authResponse) {
                                //Dismiss profile editing dialog
                                listener.hideProfileEditDialog();

                                if (authResponse.isSuccess()) {
                                    //Notify user that profile was successfully updated
                                    listener.showMessage(profileEditSuccessfulMessage);
                                    //Set updated profile to corresponding views
                                    setUpViews(authResponse.getData());
                                } else {
                                    //Notify user that the call failed
                                    listener.showMessage(profileEditFailedMessage);
                                }
                            }
                        });
                subscriptions.add(subscription);
            }
        } else {
            //Notify user that there is no internet connection
            listener.showMessage(noConnectionMessage);
        }
    }

    /*
    Retry fetch account information api call if there was an error
     */
    @OnClick(R.id.retry)
    public void onRetryButtonClick() {
        fetchAccountInfo();
    }

    /*
     * Shows the loading view and hides error and content views. This is shown before making network call
     */
    private void showLoadingView() {
        contentView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
    }

    /*
     * Shows error view and hides loading and content views. This is shown in case there was
     * a network error.
     */
    private void showErrorView(String errorMessage) {
        errorTextView.setText(errorMessage);
        contentView.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    /*
     * Shows content view i.e. account info and hides loading and error views. This is only shown
      * when account information is successfully fetched from the network
     */
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
        //unsubsribe from observables to avoid memory leaks
        subscriptions.unsubscribe();
    }

    public interface OnFragmentInteractionListener {
        /**
         * Displays short {@link android.widget.Toast} message
         *
         * @param message message to be shown
         */
        void showMessage(String message);

        /**
         * Show profile edit dialog
         */
        void showProfileEditDialog();

        /**
         * Dismiss profile edit dialog
         */
        void hideProfileEditDialog();
    }
}
