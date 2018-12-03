package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.ArtistModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.ArtistListView;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.adapters.ArtistsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ArtistSearchFragment extends Fragment implements ArtistsAdapter.ArtistAdapterHolder,
        ArtistListView {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private onSearchFragmentQueryListener mListener;
    private ArrayList<ArtistModel> artistList;
    private RecyclerView recyclerView;
    private ArtistsAdapter artistAdapter;

    public ArtistSearchFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ArtistSearchFragment newInstance() {
        ArtistSearchFragment fragment = new ArtistSearchFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        TextView searchParameter = rootView.findViewById(R.id.searchField);

        searchParameter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    mListener.queryArtist(searchParameter.getText().toString());
                    return false;
                } else {
                    return false;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.searchResultsRecyclerView);
        if (recyclerView != null) {
//            ArtistsAdapter artistAdapter = new ArtistsAdapter(artistList, this);
//            recyclerView.setAdapter(artistAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            recyclerView.setHasFixedSize(true);
//            artistAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayArtists(List<ArtistModel> artistList) {
        if (artistAdapter == null) {
            artistAdapter = new ArtistsAdapter((ArrayList<ArtistModel>) artistList, this);
            recyclerView.setAdapter(artistAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            artistAdapter.notifyDataSetChanged();
        } else {
            artistAdapter.updateResults((ArrayList<ArtistModel>) artistList);
            artistAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onArtistSelected(ArtistModel artistModel, List artistList) {
        mListener.onArtistSelected(artistModel);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onSearchFragmentQueryListener) {
            mListener = (onSearchFragmentQueryListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onSearchFragmentQueryListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface onSearchFragmentQueryListener {
        // TODO: Update argument type and name
        void queryArtist(String artistQuery);
        void onArtistSelected(ArtistModel artistModel);
    }
}
