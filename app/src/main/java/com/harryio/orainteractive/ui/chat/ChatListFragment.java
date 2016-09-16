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
    @BindString(R.string.error_no_internet_connection)
    String noConnectionMessage;

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

    /*
    Sets basic settings on recycler view
     */
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

    /*
    Fetch list of chats from the network
     */
    private void fetchChatList() {
        if (Utils.isNetworkAvailable(getActivity())) {
            //Show loading view before making network call to fetch chat list
            showLoadingView();
            String token = PrefUtils.getInstance(getActivity()).get(KEY_AUTH_TOKEN, null);

            if (token != null) {
                //Make network call
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
                                //Call failed
                                showErrorView(fetchChatListErrorMessage);
                            }

                            @Override
                            public void onNext(ChatList chatList) {
                                if (chatList.isSuccess()) {
                                    // Chat list was successfully fetched from the network.
                                    // Update chat list with data
                                    adapter.swapData(chatList.getData());
                                    showContentView();
                                    //Notify activity that the chat list was successfully fetched
                                    // so that the FAB can be shown
                                    listener.onChatsLoaded();
                                } else {
                                    //Failed to fetch chat list from the network
                                    showErrorView(fetchChatListErrorMessage);
                                }
                            }
                        });
            }
        } else {
            //Notify user that there is no internet connection
            showErrorView(noConnectionMessage);
        }
    }

    /**
     * Adds new chat to the chat list
     *
     * @param chat chat to be added
     */
    public void addNewChat(Chat.Data chat) {
        ChatList.Data data = new ChatList.Data();
        data.setCreated(chat.getCreated());
        data.setId(chat.getId());
        data.setName(chat.getName());
        data.setUser_id(chat.getUser_id());
        data.setUser(chat.getUser());

        //Notify adapter that a new item is inserted
        adapter.addItem(data);
        //Scroll recycler view to the top
        recyclerView.scrollToPosition(0);
    }

    /*
    Shows the loading view and hides error and content views. This is only shown before making network call
     */
    private void showLoadingView() {
        recyclerView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
    }

    /*
    Shows the error view and hides loading and content views. This is shown in case there was a network error
     */
    private void showErrorView(String errorMessage) {
        errorTextView.setText(errorMessage);
        recyclerView.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    /*
    Shows content view i.e. chat list and hides error and loading views. This is only shown when the chat list was
    successfully fetched from the network
     */
    private void showContentView() {
        progressView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /*
    Retry fetch chat list api call if there was an error
     */
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

        //unsubscribe from observable to avoid memory leak
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public interface OnFragmentInteractionListener {
        /**
         * Display a short {@link android.widget.Toast} message
         * @param message message to be shown
         */
        void showMessage(String message);

        /**
         * Notifies parent activity that chats were successfully loaded
         */
        void onChatsLoaded();

        /**
         * Launches {@link MessageListActivity} which displays messages from a chat
         * @param chat chat related to the clicked item
         */
        void onItemClick(ChatList.Data chat);
    }
}
