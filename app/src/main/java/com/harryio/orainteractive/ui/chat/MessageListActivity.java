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
import android.util.Log;
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
import java.util.List;

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

        createMessageDialog = new ProgressDialog(this);
        createMessageDialog.setMessage(createMessageProgress);

        subscriptions = new CompositeSubscription();
        oraService = OraServiceProvider.getInstance();

        setUpToolbar();
        setUpRecyclerView();
        fetchMessageList();
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageListAdapter(this, new ArrayList<Message>(0));
        adapter.setUserId(userId);
        recyclerView.setAdapter(adapter);
    }

    private void setUpToolbar() {
        toolbar.setTitle(chatName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void fetchMessageList() {
        if (Utils.isNetworkAvailable(this)) {
            showLoadingView();
            String token = prefUtils.get(KEY_AUTH_TOKEN, null);

            if (token != null && chatId != -1 && userId != -1) {
                Subscription subscription = oraService.getMessageList(token, String.valueOf(chatId), String.valueOf(1), 20)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<MessageList>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                showErrorView(fetchMessageListError);
                            }

                            @Override
                            public void onNext(MessageList messageList) {
                                if (messageList.isSuccess()) {
                                    List<Message> messages = messageList.getMessages();
                                    if (messages == null) {
                                        Log.e(TAG, "Messages are null");
                                    } else {
                                        adapter.swapData(messageList.getMessages());
                                        showContentView();
                                    }
                                } else {
                                    showErrorView(fetchMessageListError);
                                }
                            }
                        });

                subscriptions.add(subscription);
            }
        } else {
            showErrorView(noConnectionMessage);
        }
    }

    private void showLoadingView() {
        recyclerView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
    }

    private void showErrorView(String errorMessage) {
        recyclerView.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
        errorTextView.setText(errorMessage);
        errorView.setVisibility(View.VISIBLE);
    }

    private void showContentView() {
        progressView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void createMessage() {
        String token = prefUtils.get(KEY_AUTH_TOKEN, null);
        if (token != null) {
            if (Utils.isNetworkAvailable(this)) {
                createMessageDialog.show();
                Subscription subscription = oraService.createMessage(token, String.valueOf(chatId),
                        new CreateMessageRequest("Hey there!"))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<CreateMessageResponse>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (createMessageDialog.isShowing()) {
                                    createMessageDialog.dismiss();
                                }
                                Utils.showMessage(MessageListActivity.this, createMessageError);
                            }

                            @Override
                            public void onNext(CreateMessageResponse response) {
                                if (createMessageDialog.isShowing()) {
                                    createMessageDialog.dismiss();
                                }

                                if (response.isSuccess()) {
                                    adapter.addMessage(response.getData());
                                    recyclerView.scrollToPosition(adapter.getMessages().size() - 1);
                                } else {
                                    Utils.showMessage(MessageListActivity.this, createMessageError);
                                }
                            }
                        });

                subscriptions.add(subscription);
            } else {
                Utils.showMessage(this, noConnectionMessage);
            }
        }
    }

    @OnClick({R.id.retry, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.retry:
                fetchMessageList();
                break;

            case R.id.fab:
                createMessage();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }
}
