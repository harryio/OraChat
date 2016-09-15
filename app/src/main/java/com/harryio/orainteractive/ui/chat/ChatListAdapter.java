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

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatHolder> {
    private Context context;
    private List<Chat.Data> chats;

    public ChatListAdapter(Context context, ArrayList<Chat.Data> chats) {
        this.chats = chats;
        this.context = context;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_chat, parent, false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {
        Chat.Data chat = chats.get(position);
        holder.chatNameTextView.setText(chat.getName());

        String str = String.format("%1s - %2s", chat.getLast_message().getUser().getName(),
                Utils.getSimpleDateString(chat.getLast_message().getCreated()));
        holder.fromUserTextView.setText(str);
        holder.lastMessageTextView.setText(chat.getLast_message().getMessage());

        String chatCreateTime = chat.getCreated();
        if (Utils.isToday(chatCreateTime)) {
            holder.chatTimeTextView.setText("Today");
        } else {
            holder.chatTimeTextView.setText(Utils.getSimpleDateString(chatCreateTime));
        }
    }

    public void swapData(List<Chat.Data> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return chats.size();
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

        public ChatHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
