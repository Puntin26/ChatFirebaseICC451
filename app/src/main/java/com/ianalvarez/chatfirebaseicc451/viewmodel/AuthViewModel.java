package com.ianalvarez.chatfirebaseicc451.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import com.ianalvarez.chatfirebaseicc451.R;
import com.ianalvarez.chatfirebaseicc451.repository.AuthRepository;


public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<RegisterResult> registerResult;
    private final MutableLiveData<LoginResult> loginResult;


    public AuthViewModel(Application application) {
        super(application);
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
            registerResult.setValue(new RegisterResult(false, getApplication().getString(R.string.auth_name_required)));
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registerResult.setValue(new RegisterResult(false, getApplication().getString(R.string.auth_email_invalid)));
            return;
        }

        if (password.length() < 6) {
            registerResult.setValue(new RegisterResult(false, getApplication().getString(R.string.auth_password_short)));
            return;
        }

        if (!password.equals(confirmPassword)) {
            registerResult.setValue(new RegisterResult(false, getApplication().getString(R.string.auth_password_mismatch)));
            return;
        }

        authRepository.register(name, email, password, new AuthRepository.RegisterCallback() {
            @Override
            public void onSuccess() {
                registerResult.setValue(new RegisterResult(true, getApplication().getString(R.string.auth_register_success)));
            }

            @Override
            public void onError(String message) {
                registerResult.setValue(new RegisterResult(false, getRegisterErrorMessage(message)));
            }
        });
    }

    public void login(String email, String password){
        email = email.trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginResult.setValue(new LoginResult(false, getApplication().getString(R.string.auth_email_invalid)));
            return;
        }

        if (password.isEmpty()){
            loginResult.setValue(new LoginResult(false, getApplication().getString(R.string.auth_password_required)));
            return;
        }

        authRepository.login(email, password, new AuthRepository.LoginCallback() {
            @Override
            public void onSuccess() {
                loginResult.setValue(new LoginResult(true, getApplication().getString(R.string.auth_login_success)));
            }
            @Override
            public void onError(String message) {
                loginResult.setValue(new LoginResult(false, message));
            }
        });
    }

    private String getRegisterErrorMessage(String error) {
            if (error == null) {
                return getApplication().getString(R.string.auth_create_failed);
            }

            switch (error) {
                case "account_creation_failed":
                    return getApplication().getString(R.string.auth_create_failed);

                case "created_user_missing":
                    return getApplication().getString(R.string.auth_created_user_missing);

                case "profile_save_failed":
                    return getApplication().getString(R.string.auth_profile_save_failed);

                default:
                    return error;
            }
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
