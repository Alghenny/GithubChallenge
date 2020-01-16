package me.jansv.challenge.ui.screens.users;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.jansv.challenge.R;
import me.jansv.challenge.model.User;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.Holder>{

    private List<User> users;

    public UsersAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.bind(users.get(i));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_name)
        TextView userName;

        @BindView(R.id.user_state)
        TextView userState;

        @BindView(R.id.profile_image)
        ImageView userImage;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(User user) {
            userName.setText(user.getLogin());
            userState.setText("Lagos");
            Glide.with(itemView.getContext()).load(user.getAvatarUrl()).into(userImage);
        }
    }
}
