package com.ianalvarez.chatfirebaseicc451.repository;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


import com.ianalvarez.chatfirebaseicc451.model.Message;
import com.google.firebase.firestore.ListenerRegistration;


import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;
    private ListenerRegistration messagesListener;
    private final MutableLiveData<Boolean> messagesReadError = new MutableLiveData<>(false);

    public LiveData<Boolean> getMessagesReadError() {
        return messagesReadError;
    }

    public void clearMessagesReadError() {
        messagesReadError.setValue(false);
    }

    public ChatRepository() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public String getCurrentUserId() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }

    public String getCurrentUserName() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getDisplayName() != null ? auth.getCurrentUser().getDisplayName() : auth.getCurrentUser().getEmail();
        }
        return "Anónimo";
    }

    // Permite comunicar a ViewModel cómo terminó el envío.
    public interface SendCallback {
        void onSuccess();
        void onError(Exception error);
    }

    public void sendMessage(String chatId, Message message,
                            SendCallback callback) {

        String uid = getCurrentUserId();

        if (uid == null) {
            callback.onError(new IllegalStateException("No hay una sesión iniciada."));
            return;
        }

        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    String name = document.getString("name");

                    if (name == null || name.trim().isEmpty()) {
                        callback.onError(new IllegalStateException("El perfil no tiene nombre."));
                        return;
                    }

                    message.setSenderName(name.trim());

                    String messageId = firestore.collection("chats")
                            .document(chatId)
                            .collection("messages")
                            .document()
                            .getId();

                    message.setMessageId(messageId);

                    firestore.collection("chats").document(chatId)
                            .collection("messages").document(messageId)
                            .set(message)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(error ->
                                    callback.onError(error));
                })
                .addOnFailureListener(error -> callback.onError(error));
    }

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    public void encodeImage(ContentResolver resolver, Uri uri,
                            UploadCallback callback) {
        Handler main = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            Bitmap bitmap = null;

            try {
                // Lee dimensiones sin cargar toda la imagen.
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                try (InputStream input = resolver.openInputStream(uri)) {
                    BitmapFactory.decodeStream(input, null, options);
                }

                if (options.outWidth <= 0 || options.outHeight <= 0) {
                    throw new IllegalArgumentException("Imagen inválida");
                }

                // Reduce resolución antes de cargarla en memoria.
                options.inSampleSize = 1;
                while (Math.max(options.outWidth, options.outHeight)
                        / options.inSampleSize > 512) {
                    options.inSampleSize *= 2;
                }

                options.inJustDecodeBounds = false;

                try (InputStream input = resolver.openInputStream(uri)) {
                    bitmap = BitmapFactory.decodeStream(input, null, options);
                }

                if (bitmap == null) {
                    throw new IllegalArgumentException("No se pudo leer la imagen");
                }

                ByteArrayOutputStream output = new ByteArrayOutputStream();

                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output)) {
                    throw new IllegalStateException("No pudo comprimir");
                }

                byte[] bytes = output.toByteArray();

                // Deja margen para Base64 y los demás campos del documento.
                if (bytes.length > 400 * 1024) {
                    throw new IllegalArgumentException("Imagen demasiado grande");
                }

                String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
                main.post(() -> callback.onSuccess(base64));

            } catch (Exception error) {
                Log.e("ChatRepository", "Error convirtiendo imagen", error);
                main.post(() -> callback.onError(error.getMessage()));
            } finally {
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        }).start();
    }

    public LiveData<List<Message>> getMessages(String chatId) {
        MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();

        // Escuchar cambios en tiempo real ordenados por timestamp
        messagesListener = firestore.collection("chats")
                .document(chatId).collection("messages")                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("ChatRepository", "Error leyendo mensajes", error);
                        messagesReadError.setValue(true);
                        return;
                    }

                    List<Message> messages = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Message message = doc.toObject(Message.class);
                            messages.add(message);
                        }
                    }
                    messagesLiveData.setValue(messages);
                });

        return messagesLiveData;
    }

    public void removeMessagesListener() {
        if (messagesListener != null) {
            messagesListener.remove();
            messagesListener = null;
        }
    }

}