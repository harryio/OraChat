package com.harryio.orainteractive.ui.chat;

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

    private int chatId, userId;
    private String chatName;

    private PrefUtils prefUtils;
    private Subscription subscription;
    private MessageListAdapter adapter;

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

        setUpToolbar();
        setUpRecyclerView();
        fetchMessageList();
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        showLoadingView();
        String token = prefUtils.get(KEY_AUTH_TOKEN, null);

        if (token != null && chatId != -1 && userId != -1) {
            OraService oraService = OraServiceProvider.getInstance();
            subscription = oraService.getMessageList(token, String.valueOf(chatId), String.valueOf(1), 20)
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

    @OnClick({R.id.retry, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.retry:
                fetchMessageList();
                break;

            case R.id.fab:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
