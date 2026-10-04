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

    public UserRepository() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
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
                .addOnFailureListener(e -> Log.e("UserRepository", "Error obteniendo usuarios", e));

        return usersLiveData;
    }
}
