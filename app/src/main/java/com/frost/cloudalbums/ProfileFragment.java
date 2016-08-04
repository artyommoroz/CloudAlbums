package com.frost.cloudalbums;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.frost.cloudalbums.api.SoundCloudService;
import com.frost.cloudalbums.model.User;
import com.frost.cloudalbums.util.CircleTransform;
import com.frost.cloudalbums.util.NetworkChecker;
import com.frost.cloudalbums.util.PreferenceHelper;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {

    @Bind(R.id.avatar)
    ImageView avatar;
    @Bind(R.id.username)
    TextView username;
    @Bind(R.id.followers)
    TextView followers;
    @Bind(R.id.followings)
    TextView followings;
    @Bind(R.id.city)
    TextView city;
    @Bind(R.id.tracks)
    TextView tracks;
    @Bind(R.id.playlists)
    TextView playlists;
    @Bind(R.id.description)
    TextView description;

    private Realm realm;

    public ProfileFragment() {}

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setRetainInstance(true);

        Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);

        if (NetworkChecker.isOnline(getActivity())) {
            updateProfile();
        } else {
            int userId = PreferenceHelper.extractProfileId(getActivity());
            extractProfileFromRealm(userId);
        }
    }

    public void updateProfile() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SoundCloudService.BASE_URL_OLD)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SoundCloudService service = retrofit.create(SoundCloudService.class);

        String accessToken = PreferenceHelper.extractAccessToken(getActivity());
        Call<User> call = service.getProfileInfo(accessToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User profile = response.body();
                populateViews(profile);

                PreferenceHelper.saveProfileId(getActivity(), profile.getId());
                saveProfileToRealm(profile);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void saveProfileToRealm(final User profile) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(profile);
            }
        });
    }

    public void extractProfileFromRealm(int userId) {
        RealmQuery<User> query = realm.where(User.class).equalTo("id", userId);
        User profile = query.findFirst();
        if (profile != null) {
            populateViews(profile);
        }
    }

    private void populateViews(User profile) {
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        final ImageView headerImage = (ImageView) header.findViewById(R.id.icon);
        final TextView headerName = (TextView) header.findViewById(R.id.name);

        String avatarUrl = profile.getAvatarUrl().replace("large", "t300x300");
        Picasso.with(getActivity()).load(avatarUrl).fit().centerCrop()
                .transform(new CircleTransform()).into(avatar);
        username.setText(profile.getUsername());
        followers.setText(profile.getFollowersCount() + followers.getText().toString());
        followings.setText(profile.getFollowingsCount() + followings.getText().toString());
        if (profile.getCity() != null) {
            city.setText(city.getText() + profile.getCity());
        }
        tracks.setText(profile.getTrackCount() + tracks.getText().toString());
        playlists.setText(profile.getPlaylistCount() + playlists.getText().toString());
        if (profile.getDescription() != null) {
            description.setText(profile.getDescription());
        }

        headerName.setText(profile.getUsername());
        Picasso.with(getActivity()).load(avatarUrl).fit().centerCrop()
                .transform(new CircleTransform()).into(headerImage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
