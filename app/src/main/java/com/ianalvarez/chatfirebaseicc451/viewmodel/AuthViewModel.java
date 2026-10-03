package com.ianalvarez.chatfirebaseicc451.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ianalvarez.chatfirebaseicc451.repository.AuthRepository;


public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<RegisterResult> registerResult;
    private final MutableLiveData<LoginResult> loginResult;


    public AuthViewModel() {
        authRepository = new AuthRepository();
        registerResult = new MutableLiveData<>();
        loginResult = new MutableLiveData<>();
    }

    public LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }
    public LiveData<LoginResult> getLoginResult(){
        return loginResult;
    }


    public void register(String name, String email, String password, String confirmPassword) {

        name = name.trim();
        email = email.trim();

        if (name.isEmpty()) {
            registerResult.setValue(new RegisterResult(false, "Ingresa tu nombre."));
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registerResult.setValue(new RegisterResult(false, "Ingresa un correo válido."));
            return;
        }

        if (password.length() < 6) {
            registerResult.setValue(new RegisterResult(false, "La contraseña debe tener al menos 6 caracteres."));
            return;
        }

        if (!password.equals(confirmPassword)) {
            registerResult.setValue(new RegisterResult(false, "Las contraseñas no coinciden."));
            return;
        }

        authRepository.register(name, email, password, new AuthRepository.RegisterCallback() {
            @Override
            public void onSuccess() {
                registerResult.setValue(new RegisterResult(true, "Cuenta creada correctamente."));
            }

            @Override
            public void onError(String message) {
                registerResult.setValue(new RegisterResult(false, message));
            }
        });
    }

    public void login(String email, String password){
        email = email.trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginResult.setValue(new LoginResult(false, "Ingrese un correo valido"));
            return;
        }

        if (password.isEmpty()){
            loginResult.setValue(new LoginResult(false, "Ingrese su contraseña"));
            return;
        }

        authRepository.login(email, password, new AuthRepository.LoginCallback() {
            @Override
            public void onSuccess() {
                loginResult.setValue(new LoginResult(true, "Sesion Iniciada"));
            }
            @Override
            public void onError(String message) {
                loginResult.setValue(new LoginResult(false, message));
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

    public static class LoginResult{
        private final boolean success;
        private final String message;

        public LoginResult(boolean success, String message) {
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
