package me.jansv.challenge.ui.screens.users;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.jansv.challenge.R;
import me.jansv.challenge.api.GithubService;
import me.jansv.challenge.model.User;
import me.jansv.challenge.network.NetworkManager;
import me.jansv.challenge.ui.screens.repos.ReposActivity;
import me.jansv.challenge.util.schedulers.SchedulerProvider;

public class UsersActivity extends AppCompatActivity implements UsersContract.View {

    // injected components
    @Inject
    GithubService api;

    @Inject
    NetworkManager connectivity; // optional component

    @Inject
    SchedulerProvider schedulers; // optional component

    // views binding
    @BindView(R.id.content_main)
    View contentView;

    @BindView(R.id.user_list)
    RecyclerView userList;

    // private components
    private CompositeDisposable subscriptions = new CompositeDisposable();

    private UsersContract.Presenter mPresenter;

    private boolean mIsActive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        ButterKnife.bind(this);
        AndroidInjection.inject(this);

        mPresenter = new UsersPresenter(api);

        setupViews();
        scheduleForTitleChange();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsActive = true;
        mPresenter.bind(this);

        // this call is totally optional, you can without any damage comment it and uncomment the below one
        fetchUsersWhenReady();
         mPresenter.fetchUserList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsActive = false;
        mPresenter.unbind();
        subscriptions.clear();
    }

    private void setupViews() {
        userList.setHasFixedSize(true);
        userList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
    }


    @Override
    public void showNoNetworkMessage() {
        Snackbar.make(contentView, R.string.network_error, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, v ->
                    mPresenter.fetchUserList()
                )
                .show();
    }




    @Override
    public void showNetworkErrorMessage() {
        Snackbar.make(contentView, R.string.no_network, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showUserList(List<User> users) {
        userList.setAdapter(new UsersAdapter(users));
    }

    @Override
    public boolean isActive() {
        return mIsActive;
    }

    private void fetchUsersWhenReady() {
        subscriptions.clear();
        Observable<Boolean> isConnected = connectivity
                .connectivityOn(this)
                .subscribeOn(schedulers.io())
                .share();

        // handle initial disconnection
        Disposable d1 = isConnected
                .take(1)
                .filter(connected -> !connected) // if disconnected show "no connection message"
                .observeOn(schedulers.ui())
                .subscribe(connected -> showNoNetworkMessage() , error -> Log.e("***", "", error), () -> {});

        // handle initial connection
        Disposable d2 = isConnected
                .startWith(connectivity.isNetworkAvailableValue())
                .skipWhile(connected -> !connected)
                .take(1)
                .observeOn(schedulers.ui())
                .subscribe(connected -> mPresenter.fetchUserList(), error -> Log.e("***", "", error), () -> {});

        subscriptions.add(d1);
        subscriptions.add(d2);
    }

    public void listadoRepositorios(View v){
        TextView getname;
        getname = v.findViewById(R.id.user_name);
        String name = getname.getText().toString();
        Intent intent = new Intent(v.getContext(), ReposActivity.class);
        intent.putExtra("name", name);
        v.getContext().startActivity(intent);

    }
    private void scheduleForTitleChange() {
        final boolean delayTitleChange = getResources().getBoolean(R.bool.allowsDelayedTitleChange);

        if (delayTitleChange) {
            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                setTitle(R.string.users_title);
            }).start();
        }
    }
}
