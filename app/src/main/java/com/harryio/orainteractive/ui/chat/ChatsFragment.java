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
import android.widget.ProgressBar;

import com.harryio.orainteractive.PrefUtils;
import com.harryio.orainteractive.R;
import com.harryio.orainteractive.rest.OraService;
import com.harryio.orainteractive.rest.OraServiceProvider;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.harryio.orainteractive.PrefUtils.KEY_AUTH_TOKEN;

public class ChatsFragment extends Fragment {
    @BindView(R.id.progressView)
    ProgressBar progressView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindString(R.string.fetch_chat_list_error)
    String fetchChatListErrorMessage;

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
        adapter = new ChatListAdapter(getActivity(), new ArrayList<Chat.Data>(0));
        recyclerView.setAdapter(adapter);
    }

    private void fetchChatList() {
        String token = PrefUtils.getInstance(getActivity()).get(KEY_AUTH_TOKEN, null);

        if (token != null) {
            OraService oraService = OraServiceProvider.getInstance();
            subscription = oraService.getChatList(token, "Chat", String.valueOf(1), 20)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Chat>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            progressView.setVisibility(View.GONE);
                            listener.showMessage(fetchChatListErrorMessage);
                        }

                        @Override
                        public void onNext(Chat chat) {
                            progressView.setVisibility(View.GONE);
                            if (chat.isSuccess()) {
                                adapter.swapData(chat.getData());
                                recyclerView.setVisibility(View.VISIBLE);
                            } else {
                                listener.showMessage(fetchChatListErrorMessage);
                            }
                        }
                    });
        }
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
        void showMessage(String message);
    }
}
