package com.harryio.orainteractive.ui.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.harryio.orainteractive.R;
import com.harryio.orainteractive.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatHolder> {
    private Context context;
    private List<ChatList.Data> chats;
    private OnItemClickListener onItemClickListener;

    public ChatListAdapter(Context context, ArrayList<ChatList.Data> chats) {
        this.chats = chats;
        this.context = context;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_chat, parent, false);

        return new ChatHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {
        ChatList.Data chat = chats.get(position);
        holder.bindData(chat);

        holder.chatNameTextView.setText(chat.getName());

        ChatList.Data.LastMessage lastMessage = chat.getLast_message();
        if (lastMessage != null) {
            String str = String.format("%1s - %2s", lastMessage.getUser().getName(),
                    Utils.getSimpleDateString(lastMessage.getCreated()));
            holder.fromUserTextView.setText(str);
            holder.lastMessageTextView.setText(lastMessage.getMessage());
        }

        String chatCreateTime = chat.getCreated();
        if (Utils.isToday(chatCreateTime)) {
            holder.chatTimeTextView.setText("Today");
        } else {
            holder.chatTimeTextView.setText(Utils.getSimpleDateString(chatCreateTime));
        }
    }

    public void swapData(List<ChatList.Data> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    public void addItem(ChatList.Data chat) {
        chats.add(0, chat);
        notifyItemInserted(0);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(ChatList.Data chat);
    }

    static class ChatHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chat_name)
        TextView chatNameTextView;
        @BindView(R.id.from_user)
        TextView fromUserTextView;
        @BindView(R.id.last_message)
        TextView lastMessageTextView;
        @BindView(R.id.chat_time)
        TextView chatTimeTextView;

        private ChatList.Data chat;
        private OnItemClickListener onItemClickListener;

        public ChatHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.onItemClickListener = onItemClickListener;
        }

        public void bindData(ChatList.Data chat) {
            this.chat = chat;
        }

        @OnClick(R.id.rootView)
        public void onClick() {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(chat);
            }
        }
    }
}
