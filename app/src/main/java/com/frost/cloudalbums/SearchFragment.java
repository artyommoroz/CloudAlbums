package com.frost.cloudalbums;


import android.content.Context;
import android.icu.util.ValueIterator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.frost.cloudalbums.adapter.TrackAdapter;
import com.frost.cloudalbums.api.SoundCloudService;
import com.frost.cloudalbums.model.Track;
import com.frost.cloudalbums.model.TrackCollection;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment implements TrackAdapter.ItemClickListener,
        SearchView.OnQueryTextListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private List<Track> trackList = new ArrayList<>();
    private TrackAdapter trackAdapter;
    private OnFragmentInteractionListener interactionListener;

    public SearchFragment() {}

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interactionListener != null) {
                    interactionListener.onBackPressedSearch();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void trackClicked(View v, int position) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        progressBar.setVisibility(View.VISIBLE);
        trackList.clear();
        trackAdapter = new TrackAdapter(getActivity(), trackList, SearchFragment.this);
        recyclerView.setAdapter(trackAdapter);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SoundCloudService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SoundCloudService service = retrofit.create(SoundCloudService.class);

        Call<TrackCollection> call = service.searchTracks(newText);
        call.enqueue(new Callback<TrackCollection>() {
            @Override
            public void onResponse(Call<TrackCollection> call, Response<TrackCollection> response) {
                progressBar.setVisibility(View.GONE);
                trackList = response.body().getTrackList();
                trackAdapter = new TrackAdapter(getActivity(), trackList, SearchFragment.this);
                recyclerView.setAdapter(trackAdapter);
            }

            @Override
            public void onFailure(Call<TrackCollection> call, Throwable t) {

            }
        });
        return false;
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
        void onBackPressedSearch();
    }
}
