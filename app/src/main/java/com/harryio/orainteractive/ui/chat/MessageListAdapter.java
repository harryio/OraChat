package com.harryio.orainteractive.ui.chat;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harryio.orainteractive.R;
import com.harryio.orainteractive.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageHolder> {
    private Context context;
    private List<Message> messages;
    private int userId;

    public MessageListAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_message, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        Message message = messages.get(position);

        LinearLayout.LayoutParams messageLayoutParams = (LinearLayout.LayoutParams)
                holder.messageTextView.getLayoutParams();
        LinearLayout.LayoutParams fromLayoutParams = (LinearLayout.LayoutParams)
                holder.fromTextView.getLayoutParams();

        if (userId == message.getUser_id()) {
            messageLayoutParams.setMargins(100, 0, 0, 0);
            messageLayoutParams.gravity = Gravity.END;
            holder.messageTextView.setLayoutParams(messageLayoutParams);
            holder.messageTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_400));

            fromLayoutParams.gravity = Gravity.END;
            holder.fromTextView.setLayoutParams(fromLayoutParams);
        } else {
            messageLayoutParams.setMargins(0, 0, 100, 0);
            holder.messageTextView.setLayoutParams(messageLayoutParams);
            holder.messageTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.messageTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.messageTextView.setText(message.getMessage());
        String str = message.getUser().getName() + " - " +
                Utils.getSimpleDateString(message.getCreated());
        holder.fromTextView.setText(str);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void swapData(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class MessageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message)
        TextView messageTextView;
        @BindView(R.id.from)
        TextView fromTextView;

        public MessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
