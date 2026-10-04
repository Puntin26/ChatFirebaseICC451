package com.ianalvarez.chatfirebaseicc451.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ianalvarez.chatfirebaseicc451.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;
    private final MutableLiveData<Boolean> usersReadError = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> profileReadError = new MutableLiveData<>(false);

    public LiveData<Boolean> getUsersReadError() {
        return usersReadError;
    }

    public LiveData<Boolean> getProfileReadError() {
        return profileReadError;
    }

    public void clearUsersReadError() {
        usersReadError.setValue(false);
    }

    public void clearProfileReadError() {
        profileReadError.setValue(false);
    }

    public UserRepository() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<User> getCurrentUserProfile() {
        MutableLiveData<User> profileLiveData = new MutableLiveData<>();

        if (auth.getCurrentUser() == null){
            return profileLiveData;
        }

        String uid = auth.getCurrentUser().getUid();

        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    User user = document.toObject(User.class);

                    if (user == null) {
                        profileReadError.setValue(true);
                        return;
                    }

                    profileLiveData.setValue(user);
                })
                .addOnFailureListener(error -> {
                    Log.e("UserRepository", "Error leyendo perfil", error);
                    profileReadError.setValue(true);
                });

        return profileLiveData;
    }

    public void saveFcmToken(String token) {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        firestore.collection("users").document(uid)
                .update("fcmToken", token)
                .addOnSuccessListener(unused ->
                        Log.d("FCM", "Token guardado en Firestore"))
                .addOnFailureListener(error ->
                        Log.e("FCM", "Error guardando token", error));
    }

    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
        String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

        firestore.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        User user = doc.toObject(User.class);
                        // No añadimos al usuario actual a la lista
                        if (user.getUid() != null && !user.getUid().equals(currentUserId)) {
                            userList.add(user);
                        }
                    }
                    usersLiveData.setValue(userList);
                })
                .addOnFailureListener(error -> {
                    Log.e("UserRepository", "Error obteniendo usuarios", error);
                    usersReadError.setValue(true);
                });
        return usersLiveData;
    }
}
