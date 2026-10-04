package com.ianalvarez.chatfirebaseicc451.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ianalvarez.chatfirebaseicc451.R;
import com.ianalvarez.chatfirebaseicc451.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
        
        // Configuramos la zona horaria a República Dominicana (Santo Domingo / America/Santo_Domingo)
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("America/Santo_Domingo"));
        String formattedTime = sdf.format(new Date(message.getTimestamp()));

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

        private static void showImage(ImageView view, Message message) {
            Glide.with(view).clear(view);
            view.setImageDrawable(null);

            String base64 = message.getImageBase64();
            String url = message.getImageUrl();

            if (base64 != null && !base64.isEmpty()) {
                view.setVisibility(View.VISIBLE);

                try {
                    byte[] bytes = Base64.decode(base64, Base64.NO_WRAP);
                    Glide.with(view).load(bytes).error(android.R.drawable.ic_menu_report_image)
                            .into(view);
                } catch (IllegalArgumentException error) {
                    view.setImageResource(android.R.drawable.ic_menu_report_image);
                }
            } else if (url != null && !url.isEmpty()) {
                view.setVisibility(View.VISIBLE);
                Glide.with(view).load(url).into(view);
            } else {
                view.setVisibility(View.GONE);
            }
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

            showImage(imgSent, message);
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

            // La lógica de mostrar la imagen
            showImage(imgReceived, message);
        }
    }
}
