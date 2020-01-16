package me.jansv.challenge.api;

import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Observable;
import me.jansv.challenge.model.User;
import me.jansv.challenge.model.UserList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GithubService {
    @GET("/search/users")
    Call<UserList> getUserList(@Query("q") @Nullable String filter);

    @GET("users/{login}/repos")
    Call<List<User>> getRepos(@Path("login") @Nullable String user);

}
