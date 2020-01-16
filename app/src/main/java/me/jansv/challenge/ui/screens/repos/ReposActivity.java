package me.jansv.challenge.ui.screens.repos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import io.reactivex.Observable;
import me.jansv.challenge.R;
import me.jansv.challenge.api.GithubService;
import me.jansv.challenge.model.User;
import me.jansv.challenge.model.UserList;
import me.jansv.challenge.network.NetworkManager;
import me.jansv.challenge.ui.screens.users.UsersActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReposActivity extends AppCompatActivity {

    @Inject
    GithubService api;

    @BindView(R.id.content_repos)
    View contentView;

    @BindView(R.id.rv_user_repositorie)
            RecyclerView userRepositorie;

    UsersActivity usersActivity = new UsersActivity();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repos);

        ButterKnife.bind(this);
        AndroidInjection.inject(this);

        RecyclerView recyclerView = findViewById(R.id.rv_user_repositorie);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        final String user = name;
        api = retrofit.create(GithubService.class);
        Call<List<User>> call = api.getRepos(user);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> repos = response.body();
                recyclerView.setAdapter(new ReposAdapter(ReposActivity.this,repos));
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });



    }
}
