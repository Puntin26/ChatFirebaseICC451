package com.ianalvarez.chatfirebaseicc451.viewmodel;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ianalvarez.chatfirebaseicc451.model.Message;
import com.ianalvarez.chatfirebaseicc451.repository.ChatRepository;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;
    private LiveData<List<Message>> messagesLiveData;
    private final MutableLiveData<Boolean> sending = new MutableLiveData<>(false);
    private final MutableLiveData<String> sentText = new MutableLiveData<>();
    private final MutableLiveData<Exception> sendError = new MutableLiveData<>();
    private final MutableLiveData<String> imageError = new MutableLiveData<>();

    public LiveData<String> getImageError() {
        return imageError;
    }
    public LiveData<Boolean> getMessagesReadError() {
        return chatRepository.getMessagesReadError();
    }

    public void clearMessagesReadError() {
        chatRepository.clearMessagesReadError();
    }

    public void clearImageError() {
        imageError.setValue(null);
    }

    public ChatViewModel() {
        chatRepository = new ChatRepository();
    }

    public String getCurrentUserId() {
        return chatRepository.getCurrentUserId();
    }

    // Activity observará estos resultados.
    public LiveData<Boolean> getSending() {
        return sending;
    }

    public LiveData<String> getSentText() {
        return sentText;
    }

    public LiveData<Exception> getSendError() {
        return sendError;
    }

    // Limpia el resultado después de que pantalla lo atienda.
    public void clearSendResult() {
        sentText.setValue(null);
        sendError.setValue(null);
    }

    // Envía al Repository y publica resultado mediante LiveData.
    private void saveMessage(String chatId, Message message) {
        sending.setValue(true);
        clearSendResult();

        chatRepository.sendMessage(chatId, message,
                new ChatRepository.SendCallback() {
                    @Override
                    public void onSuccess() {
                        sending.setValue(false);
                        sentText.setValue(message.getText());
                    }

                    @Override
                    public void onError(Exception error) {
                        sending.setValue(false);
                        sendError.setValue(error);
                    }
                });
    }

    public void sendMessage(String chatId, String text) {

        if (Boolean.TRUE.equals(sending.getValue())) {
            return;
        }

        String senderId = chatRepository.getCurrentUserId();
        String senderName = chatRepository.getCurrentUserName();
        long timestamp = System.currentTimeMillis();

        Message message = new Message("", senderId, senderName, text, timestamp, null);
        saveMessage(chatId, message);
    }

    public void sendImageMessage(String chatId, Uri imageUri, ContentResolver resolver) {
        if (Boolean.TRUE.equals(sending.getValue())) {
            return;
        }

        sending.setValue(true);
        clearSendResult();
        clearImageError();

        chatRepository.encodeImage(resolver, imageUri,
                new ChatRepository.UploadCallback() {
                    @Override
                    public void onSuccess(String base64) {
                        Message message = new Message("", chatRepository.getCurrentUserId(),
                                "", "", System.currentTimeMillis(), null
                        );

                        message.setImageBase64(base64);
                        saveMessage(chatId, message);
                    }

                    @Override
                    public void onError(String error) {
                        sending.setValue(false);
                        imageError.setValue("conversion_failed");
                    }
                });
    }

    public LiveData<List<Message>> getMessages(String chatId) {
        // Para evitar crear multiples listeners, solo inicializamos si es nulo
        if (messagesLiveData == null) {
            messagesLiveData = chatRepository.getMessages(chatId);
        }
        return messagesLiveData;
    }

    @Override
    protected void onCleared() {
        chatRepository.removeMessagesListener();
        super.onCleared();
    }


}
