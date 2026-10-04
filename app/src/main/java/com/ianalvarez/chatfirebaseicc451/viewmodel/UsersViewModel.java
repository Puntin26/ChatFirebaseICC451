package com.ianalvarez.chatfirebaseicc451.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ianalvarez.chatfirebaseicc451.model.User;
import com.ianalvarez.chatfirebaseicc451.repository.UserRepository;

import java.util.List;

public class UsersViewModel extends ViewModel {
    private final UserRepository userRepository;
    private LiveData<List<User>> usersLiveData;
    private LiveData<User> currentUserProfile;
    public LiveData<Boolean> getUsersReadError() {
        return userRepository.getUsersReadError();
    }

    public LiveData<Boolean> getProfileReadError() {
        return userRepository.getProfileReadError();
    }

    public void clearUsersReadError() {
        userRepository.clearUsersReadError();
    }

    public void saveFcmToken(String token) {
        userRepository.saveFcmToken(token);
    }
    public void clearProfileReadError() {
        userRepository.clearProfileReadError();
    }


    public UsersViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<List<User>> getUsers() {
        if (usersLiveData == null) {
            usersLiveData = userRepository.getAllUsers();
        }
        return usersLiveData;
    }

    public LiveData<User> getCurrentUserProfile(){
        if (currentUserProfile == null) {
            currentUserProfile = userRepository.getCurrentUserProfile();
        }

        return currentUserProfile;
    }
}
