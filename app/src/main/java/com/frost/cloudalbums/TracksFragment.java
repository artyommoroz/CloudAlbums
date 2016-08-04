package com.frost.cloudalbums;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.frost.cloudalbums.adapter.TrackAdapter;
import com.frost.cloudalbums.model.Album;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

public class TracksFragment extends Fragment implements TrackAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private static final String ALBUM_ID = "albumId";
    private int albumId;
    private Realm realm;
    private OnFragmentInteractionListener interactionListener;

    public TracksFragment() {}

    public static TracksFragment newInstance(int albumId) {
        TracksFragment fragment = new TracksFragment();
        Bundle args = new Bundle();
        args.putInt(ALBUM_ID, albumId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            albumId = getArguments().getInt(ALBUM_ID);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        realm = Realm.getDefaultInstance();
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        RealmQuery<Album> query = realm.where(Album.class).equalTo("id", albumId);
        Album album = query.findFirst();

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (album != null) {
            toolbar.setTitle(album.getTitle());
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interactionListener != null) {
                    interactionListener.onBackPressedTracks();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TrackAdapter trackAdapter = new TrackAdapter(getActivity(), album, this);
        recyclerView.setAdapter(trackAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void trackClicked(View v, int position) {

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
        void onBackPressedTracks();
    }
}
