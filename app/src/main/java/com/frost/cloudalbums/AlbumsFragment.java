package com.frost.cloudalbums;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.frost.cloudalbums.adapter.AlbumAdapter;
import com.frost.cloudalbums.api.SoundCloudService;
import com.frost.cloudalbums.model.Album;
import com.frost.cloudalbums.model.AlbumCollection;
import com.frost.cloudalbums.util.NetworkChecker;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AlbumsFragment extends Fragment implements AlbumAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    Spinner spinner;

    private static final String GENRE_ELECTRONIC = "edm";
    private static final String GENRE_RAP = "hip-hop";
    private static final String GENRE_ROCK = "rock";
    private static final String GENRE_FUNK = "funk";
    private static final String GENRE_BLUES = "blues";
    private static final String GENRE_JAZZ = "jazz";
    private static final String GENRE_PUNK = "punk rock";
    private static final String GENRE_METAL = "genre";

    private String genre = "rock";
    private RealmResults<Album> realmAlbums;
    private Realm realm;
    private AlbumAdapter albumAdapter;
    private List<Album> albumList = new RealmList<>();
    private OnFragmentInteractionListener interactionListener;
    private SoundCloudService service;

    public AlbumsFragment() {}

    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        realm = Realm.getDefaultInstance();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SoundCloudService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(SoundCloudService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        int orientation = getActivity().getResources().getConfiguration().orientation;
        GridLayoutManager layoutManager = orientation == Configuration.ORIENTATION_PORTRAIT ?
                new GridLayoutManager(getActivity(), 2) : new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        if (NetworkChecker.isOnline(getActivity())) {
            updateAlbumList(genre);
        } else {
            extractFromRealm(genre);
        }

        spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);
                switch (i) {
                    case 0:
                        genre = GENRE_ROCK;
                        break;
                    case 1:
                        genre = GENRE_ELECTRONIC;
                        break;
                    case 2:
                        genre = GENRE_BLUES;
                        break;
                    case 3:
                        genre = GENRE_JAZZ;
                        break;
                    case 4:
                        genre = GENRE_FUNK;
                        break;
                    case 5:
                        genre = GENRE_RAP;
                        break;
                    case 6:
                        genre = GENRE_PUNK;
                        break;
                    case 7:
                        genre = GENRE_METAL;
                        break;
                }

                if (NetworkChecker.isOnline(getActivity())) {
                    updateAlbumList(genre);
                } else {
                    extractFromRealm(genre);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search && interactionListener != null) {
            interactionListener.onSearchItemPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void updateAlbumList(final String genre) {
        progressBar.setVisibility(View.VISIBLE);
        albumList.clear();
        albumAdapter = new AlbumAdapter(getActivity(), albumList, AlbumsFragment.this);
        recyclerView.setAdapter(albumAdapter);

        Call<AlbumCollection> call = service.loadAlbums(genre);
        call.enqueue(new Callback<AlbumCollection>() {
            @Override
            public void onResponse(Call<AlbumCollection> call, Response<AlbumCollection> response) {
                progressBar.setVisibility(View.GONE);
                albumList = response.body().getAlbumList();
                albumList = filterAlbums(albumList, genre);
                albumAdapter = new AlbumAdapter(getActivity(), albumList, AlbumsFragment.this);
                recyclerView.setAdapter(albumAdapter);

                saveToRealm(albumList);
            }

            @Override
            public void onFailure(Call<AlbumCollection> call, Throwable t) {

            }
        });
    }

    private void saveToRealm(final List<Album> albumList) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Album album : albumList) {
                    realm.copyToRealmOrUpdate(album);
                }
            }
        });
    }

    private void extractFromRealm(String genre) {
        Toast.makeText(getActivity(), getString(R.string.no_connection_message), Toast.LENGTH_SHORT).show();
        RealmQuery<Album> query = realm.where(Album.class).equalTo("genre", genre);
        realmAlbums = query.findAll();
        albumAdapter = new AlbumAdapter(getActivity(), realmAlbums, AlbumsFragment.this);
        recyclerView.setAdapter(albumAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private List<Album> filterAlbums(List<Album> albumList, String genre) {
        List<Album> newAlbumList = new ArrayList<>();
        for (Album album : albumList) {
            if (!TextUtils.isEmpty(album.getArtworkUrl()) && !album.getTrackList().isEmpty()) {
                album.setArtworkUrl(album.getArtworkUrl().replace("large", "t300x300"));
                album.setGenre(genre);
                newAlbumList.add(album);
            }
        }
        return newAlbumList;
    }

    @Override
    public void albumClicked(View v, int position) {
        int albumId = NetworkChecker.isOnline(getActivity()) ? albumList.get(position).getId()
                : realmAlbums.get(position).getId();
        if (interactionListener != null) {
            interactionListener.onClickAlbum(albumId);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            interactionListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onClickAlbum(int albumId);
        void onSearchItemPressed();
    }
}

