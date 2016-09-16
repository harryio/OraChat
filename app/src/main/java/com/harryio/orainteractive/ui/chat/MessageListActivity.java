package com.harryio.orainteractive.ui.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.harryio.orainteractive.PrefUtils;
import com.harryio.orainteractive.R;
import com.harryio.orainteractive.Utils;
import com.harryio.orainteractive.rest.OraService;
import com.harryio.orainteractive.rest.OraServiceProvider;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.harryio.orainteractive.PrefUtils.KEY_AUTH_TOKEN;
import static com.harryio.orainteractive.PrefUtils.KEY_USER_ID;

/**
 * Activity that displays messages related to a chat
 */
public class MessageListActivity extends AppCompatActivity {
    private static final String TAG = "MessageListActivity";
    private static final String EXTRA_CHAT_ID = "CHAT_ID";
    private static final String EXTRA_CHAT_NAME = "CHAT_NAME";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.error_message)
    TextView errorTextView;
    @BindView(R.id.error_view)
    LinearLayout errorView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressView)
    ProgressBar progressView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindString(R.string.fetch_message_list_error)
    String fetchMessageListError;
    @BindString(R.string.create_message_error)
    String createMessageError;
    @BindString(R.string.create_message_progress)
    String createMessageProgress;
    @BindString(R.string.error_no_internet_connection)
    String noConnectionMessage;

    private int chatId, userId;
    private String chatName;

    private PrefUtils prefUtils;
    private CompositeSubscription subscriptions;
    private MessageListAdapter adapter;
    private ProgressDialog createMessageDialog;
    private OraService oraService;

    /**
     * Returns intent through which this activity can be launched
     *
     * @param context  context of the calling activity
     * @param chatId   id of the chat for which messages are displayed
     * @param chatName name of the chat
     */
    public static Intent getCallingIntent(Context context, int chatId, String chatName) {
        Intent intent = new Intent(context, MessageListActivity.class);
        intent.putExtra(EXTRA_CHAT_ID, chatId);
        intent.putExtra(EXTRA_CHAT_NAME, chatName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);

        chatId = getIntent().getIntExtra(EXTRA_CHAT_ID, -1);
        chatName = getIntent().getStringExtra(EXTRA_CHAT_NAME);
        prefUtils = PrefUtils.getInstance(this);
        userId = prefUtils.get(KEY_USER_ID, -1);

        //Create dialog which is shown when new message is created
        createMessageDialog = new ProgressDialog(this);
        createMessageDialog.setMessage(createMessageProgress);

        subscriptions = new CompositeSubscription();
        oraService = OraServiceProvider.getInstance();

        setUpToolbar();
        setUpRecyclerView();
        fetchMessageList();
    }

    /*
    Sets basic settings for recycler view
     */
    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageListAdapter(this, new ArrayList<Message>(0));
        adapter.setUserId(userId);
        recyclerView.setAdapter(adapter);
    }

    /*
    Sets basic settings for toolbar
     */
    private void setUpToolbar() {
        toolbar.setTitle(chatName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /*
    Fetches message list from the network
     */
    private void fetchMessageList() {
        if (Utils.isNetworkAvailable(this)) {
            //Show loading view before making network call to fetch message list
            showLoadingView();
            String token = prefUtils.get(KEY_AUTH_TOKEN, null);

            if (token != null && chatId != -1 && userId != -1) {
                //Make network call
                Subscription subscription = oraService.getMessageList(token, String.valueOf(chatId), String.valueOf(1), 20)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<MessageList>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                //Call failed
                                showErrorView(fetchMessageListError);
                            }

                            @Override
                            public void onNext(MessageList messageList) {
                                if (messageList.isSuccess()) {
                                    //Message list was successfully fetched from the network
                                    //Update message list with data
                                    adapter.swapData(messageList.getMessages());
                                    showContentView();
                                } else {
                                    //Failed to fetch message list from network
                                    showErrorView(fetchMessageListError);
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
    Shows loading view and hides error and content views. This is only shown before making network calls
     */
    private void showLoadingView() {
        recyclerView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
    }

    /*
    Shows error view and hides content and loading views. This is only shown in case of network error
     */
    private void showErrorView(String errorMessage) {
        recyclerView.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
        errorTextView.setText(errorMessage);
        errorView.setVisibility(View.VISIBLE);
    }

    /*
    Shows content view i.e. message list and hides error and loading views. This is shown when
    message list is successfully fetched from the network
     */
    private void showContentView() {
        progressView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Make an api call to create new message in the chat
     */
    private void createMessage() {
        String token = prefUtils.get(KEY_AUTH_TOKEN, null);
        if (token != null) {
            if (Utils.isNetworkAvailable(this)) {
                //Show create message dialog before making api call
                createMessageDialog.show();
                //Make network call
                Subscription subscription = oraService.createMessage(token, String.valueOf(chatId),
                        new CreateMessageRequest("Hey there!"))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<CreateMessageResponse>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                //Dismiss create message dialog
                                if (createMessageDialog.isShowing()) {
                                    createMessageDialog.dismiss();
                                }
                                //Notify user that the call failed
                                Utils.showMessage(MessageListActivity.this, createMessageError);
                            }

                            @Override
                            public void onNext(CreateMessageResponse response) {
                                //Dismiss create message dialog
                                if (createMessageDialog.isShowing()) {
                                    createMessageDialog.dismiss();
                                }

                                if (response.isSuccess()) {
                                    //Message was successfully created
                                    //Add newly created message to the message list and notify adapter
                                    adapter.addMessage(response.getData());
                                    //Scroll recycler view to bottom
                                    recyclerView.scrollToPosition(adapter.getMessages().size() - 1);
                                } else {
                                    //Notify user that the call failed
                                    Utils.showMessage(MessageListActivity.this, createMessageError);
                                }
                            }
                        });

                subscriptions.add(subscription);
            } else {
                //Notify user that there is no internet connection
                Utils.showMessage(this, noConnectionMessage);
            }
        }
    }

    @OnClick({R.id.retry, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            //Retry fetching messages from the network if there was an error
            case R.id.retry:
                fetchMessageList();
                break;

            //Make network call to create new message
            case R.id.fab:
                createMessage();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unsubscribe from observable to avoid memory leak
        subscriptions.unsubscribe();
    }
}
