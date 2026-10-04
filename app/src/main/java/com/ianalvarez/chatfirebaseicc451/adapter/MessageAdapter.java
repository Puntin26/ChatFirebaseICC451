package com.ianalvarez.chatfirebaseicc451.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ianalvarez.chatfirebaseicc451.R;
import com.ianalvarez.chatfirebaseicc451.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<Message> messageList;
    private String currentUserId;

    public MessageAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        String formattedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(message.getTimestamp()));

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message, formattedTime);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message, formattedTime);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtSentMessage, txtSentTime;
        ImageView imgSent;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSentMessage = itemView.findViewById(R.id.txtSentMessage);
            txtSentTime = itemView.findViewById(R.id.txtSentTime);
            imgSent = itemView.findViewById(R.id.imgSent);
        }

        void bind(Message message, String time) {
            if (message.getText() != null && !message.getText().isEmpty()) {
                txtSentMessage.setText(message.getText());
                txtSentMessage.setVisibility(View.VISIBLE);
            } else {
                txtSentMessage.setVisibility(View.GONE);
            }

            txtSentTime.setText(time);

            // La lógica de mostrar la imagen se usará el sábado
            if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                imgSent.setVisibility(View.VISIBLE);
                // Glide.with(itemView.getContext()).load(message.getImageUrl()).into(imgSent);
            } else {
                imgSent.setVisibility(View.GONE);
            }
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtReceivedMessage, txtReceivedTime, txtSenderName;
        ImageView imgReceived;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtReceivedMessage = itemView.findViewById(R.id.txtReceivedMessage);
            txtReceivedTime = itemView.findViewById(R.id.txtReceivedTime);
            txtSenderName = itemView.findViewById(R.id.txtSenderName);
            imgReceived = itemView.findViewById(R.id.imgReceived);
        }

        void bind(Message message, String time) {
            txtSenderName.setText(message.getSenderName());

            if (message.getText() != null && !message.getText().isEmpty()) {
                txtReceivedMessage.setText(message.getText());
                txtReceivedMessage.setVisibility(View.VISIBLE);
            } else {
                txtReceivedMessage.setVisibility(View.GONE);
            }

            txtReceivedTime.setText(time);

            // La lógica de mostrar la imagen se usará el sábado
            if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                imgReceived.setVisibility(View.VISIBLE);
                // Glide.with(itemView.getContext()).load(message.getImageUrl()).into(imgReceived);
            } else {
                imgReceived.setVisibility(View.GONE);
            }
        }
    }
}