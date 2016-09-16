package com.harryio.orainteractive.ui.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.harryio.orainteractive.PrefUtils;
import com.harryio.orainteractive.R;
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

import static com.harryio.orainteractive.PrefUtils.KEY_AUTH_TOKEN;

public class ChatListFragment extends Fragment {
    @BindView(R.id.progressView)
    ProgressBar progressView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindString(R.string.fetch_chat_list_error)
    String fetchChatListErrorMessage;
    @BindView(R.id.error_message)
    TextView errorTextView;
    @BindView(R.id.error_view)
    LinearLayout errorView;

    private ChatListAdapter adapter;
    private Subscription subscription;
    private OnFragmentInteractionListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpRecyclerView();
        fetchChatList();
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ChatListAdapter(getActivity(), new ArrayList<ChatList.Data>(0));
        adapter.setOnItemClickListener(new ChatListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ChatList.Data chat) {
                listener.onItemClick(chat);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void fetchChatList() {
        showLoadingView();
        String token = PrefUtils.getInstance(getActivity()).get(KEY_AUTH_TOKEN, null);

        if (token != null) {
            OraService oraService = OraServiceProvider.getInstance();
            subscription = oraService.getChatList(token, "Chat", String.valueOf(1), 20)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ChatList>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            showErrorView(fetchChatListErrorMessage);
                        }

                        @Override
                        public void onNext(ChatList chatList) {
                            if (chatList.isSuccess()) {
                                adapter.swapData(chatList.getData());
                                showContentView();
                                listener.onChatsLoaded();
                            } else {
                                showErrorView(fetchChatListErrorMessage);
                            }
                        }
                    });
        }
    }

    public void addNewChat(Chat.Data chat) {
        ChatList.Data data = new ChatList.Data();
        data.setCreated(chat.getCreated());
        data.setId(chat.getId());
        data.setName(chat.getName());
        data.setUser_id(chat.getUser_id());
        data.setUser(chat.getUser());

        adapter.addItem(data);
    }

    private void showLoadingView() {
        recyclerView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
    }

    private void showErrorView(String errorMessage) {
        errorTextView.setText(errorMessage);
        recyclerView.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    private void showContentView() {
        progressView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.retry)
    public void onClick() {
        fetchChatList();
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public interface OnFragmentInteractionListener {
        void onChatsLoaded();

        void onItemClick(ChatList.Data chat);
    }
}
