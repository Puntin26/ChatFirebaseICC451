package com.ianalvarez.chatfirebaseicc451.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ianalvarez.chatfirebaseicc451.repository.AuthRepository;


public class AuthViewModel extends ViewModel {    private final AuthRepository authRepository;
    private final MutableLiveData<RegisterResult> registerResult;

    public AuthViewModel() {
        authRepository = new AuthRepository();
        registerResult = new MutableLiveData<>();
    }

    public LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String name, String email, String password,
                         String confirmPassword) {

        name = name.trim();
        email = email.trim();

        if (name.isEmpty()) {
            registerResult.setValue(new RegisterResult(false,
                    "Ingresa tu nombre."));
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registerResult.setValue(new RegisterResult(false,
                    "Ingresa un correo válido."));
            return;
        }

        if (password.length() < 6) {
            registerResult.setValue(new RegisterResult(false,
                    "La contraseña debe tener al menos 6 caracteres."));
            return;
        }

        if (!password.equals(confirmPassword)) {
            registerResult.setValue(new RegisterResult(false,
                    "Las contraseñas no coinciden."));
            return;
        }

        authRepository.register(name, email, password,
                new AuthRepository.RegisterCallback() {
                    @Override
                    public void onSuccess() {
                        registerResult.setValue(new RegisterResult(true,
                                "Cuenta creada correctamente."));
                    }

                    @Override
                    public void onError(String message) {
                        registerResult.setValue(new RegisterResult(false,
                                message));
                    }
                });
    }

    public static class RegisterResult {

        private final boolean success;
        private final String message;

        public RegisterResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

}
