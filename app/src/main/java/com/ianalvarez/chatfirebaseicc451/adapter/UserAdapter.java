package com.ianalvarez.chatfirebaseicc451.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ianalvarez.chatfirebaseicc451.R;
import com.ianalvarez.chatfirebaseicc451.model.User;
import com.ianalvarez.chatfirebaseicc451.ui.ChatActivity;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private List<User> userListFull;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
        this.userListFull = new ArrayList<>(userList);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        
        // Poner la inicial del nombre en el círculo
        if (user.getName() != null && !user.getName().isEmpty()) {
            holder.txtUserInitial.setText(user.getName().substring(0, 1).toUpperCase());
        } else {
            holder.txtUserInitial.setText("?");
        }
        
        holder.txtUserName.setText(user.getName());
        holder.txtUserEmail.setText(user.getEmail());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            // Pasamos la info de la otra persona al ChatActivity
            intent.putExtra("receiverId", user.getUid());
            intent.putExtra("receiverName", user.getName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    
    // Método para filtrar la lista al buscar
    public void filterList(String text) {
        List<User> filteredList = new ArrayList<>();
        
        if (text.isEmpty()) {
            filteredList.addAll(userListFull);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (User item : userListFull) {
                if ((item.getName() != null && item.getName().toLowerCase().contains(filterPattern)) ||
                    (item.getEmail() != null && item.getEmail().toLowerCase().contains(filterPattern))) {
                    filteredList.add(item);
                }
            }
        }
        
        this.userList = filteredList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtUserEmail, txtUserInitial;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtUserEmail = itemView.findViewById(R.id.txtUserEmail);
            txtUserInitial = itemView.findViewById(R.id.txtUserInitial);
        }
    }
}
