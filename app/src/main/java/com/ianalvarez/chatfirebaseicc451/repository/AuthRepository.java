package com.ianalvarez.chatfirebaseicc451.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ianalvarez.chatfirebaseicc451.model.User;

public class AuthRepository {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public interface RegisterCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface LoginCallback{
        void onSuccess();
        void onError(String message);
    }

    public void register(String name, String email, String password, RegisterCallback callback) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                String message = task.getException() != null ? task.getException().getMessage() : "No se pudo crear la cuenta.";

                callback.onError(message);
                return;
            }

            FirebaseUser firebaseUser = auth.getCurrentUser();

            if (firebaseUser == null) {
                callback.onError("No se pudo obtener el usuario creado.");
                return;
            }

            User user = new User(firebaseUser.getUid(), name, email);

            db.collection("users").document(firebaseUser.getUid()).set(user)
                    .addOnSuccessListener(unused -> callback.onSuccess())
                    .addOnFailureListener(error -> callback.onError("La cuenta fue creada, pero no se pudo guardar el perfil."));
        });
    }

    public void login(String email, String password, LoginCallback callback) {
        auth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(result -> callback.onSuccess())
                .addOnFailureListener(error -> callback.onError(error.getMessage()));
    }
}
