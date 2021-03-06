package com.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Animatable2;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spauldhaliwal.spotifytoptracksplayer.R;
import com.spauldhaliwal.spotifytoptracksplayer.model.impl.ArtistModel;
import com.spauldhaliwal.spotifytoptracksplayer.view.ArtistListView;
import com.spauldhaliwal.spotifytoptracksplayer.view.impl.adapters.ArtistsAdapter;
import com.spauldhaliwal.spotifytoptracksplayer.view.impl.uihelper.GridRecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class ArtistSearchFragment extends Fragment implements ArtistsAdapter.ArtistAdapterHolder,
        ArtistListView {

    private static final String TAG = "ArtistSearchFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private onSearchFragmentQueryListener mListener;
    private ArrayList<ArtistModel> artistList;
    private GridRecyclerView recyclerView;
    private ArtistsAdapter artistAdapter;
    private EditText searchParameter;
    private boolean searchIsActive;
    private GridLayoutManager layoutManager;
    private int animationShortDuration;

    public ArtistSearchFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ArtistSearchFragment newInstance() {
        ArtistSearchFragment fragment = new ArtistSearchFragment();
        Bundle args = new Bundle();
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
        animationShortDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        searchParameter = rootView.findViewById(R.id.searchField);
        LinearLayout searchBar = rootView.findViewById(R.id.searchBar);

        ImageView searchBackButtom = rootView.findViewById(R.id.searchIcon);

        searchBackButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchIsActive) {
                    mListener.searchCancelled();
                    mListener.closeKeyboard(searchParameter);
                    searchParameter.clearFocus();
                    searchParameter.getText().clear();
                    closeSearch();

                }
            }
        });

        searchParameter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openSearch();
                } else if (!hasFocus) {
                    closeSearch();
                }
            }
        });

        searchParameter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    String searchQuery = searchParameter.getText().toString();
                    mListener.queryArtist(searchQuery);
                    mListener.closeKeyboard(searchParameter);
                    searchParameter.clearFocus();
                    searchParameter.getText().clear();
                    return true;
                } else {
                    return true;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.searchResultsRecyclerView);
    }

    @Override
    public void displayArtists(List<ArtistModel> artistList) {
        if (artistAdapter == null) {
            artistAdapter = new ArtistsAdapter((ArrayList<ArtistModel>) artistList, this);
            recyclerView.setAdapter(artistAdapter);
            layoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
            artistAdapter.notifyDataSetChanged();
            OverScrollDecoratorHelper.setUpOverScroll(recyclerView,
                    OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        } else {
            updateSearchResults(recyclerView, (ArrayList<ArtistModel>) artistList);
        }
    }

    @Override
    public void onArtistSelected(ArtistModel artistModel, List artistList) {
        layoutManager.scrollToPositionWithOffset(0, 0);
        mListener.closeKeyboard(searchParameter);
        mListener.onArtistSelected(artistModel);
        searchParameter.clearFocus();
    }

    private void updateSearchResults(final RecyclerView recyclerView, ArrayList<ArtistModel> artistList) {
        final Context context = recyclerView.getContext();

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.grid_layout_animation);

        recyclerView.animate()
                .alpha(0f)
                .translationX(recyclerView.getWidth())
                .setDuration(animationShortDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        artistAdapter.updateResults(artistList);
                        recyclerView.setTranslationX(0);
                        recyclerView.setLayoutAnimation(controller);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        recyclerView.scheduleLayoutAnimation();
                        recyclerView.setAlpha(1f);
                    }
                });

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

    public void openSearch() {
        searchIsActive = true;
        ImageView searchIcon = getView().findViewById(R.id.searchIcon);
        searchIcon.setImageResource(R.drawable.avd_searchback_search_to_back);
        Animatable2 searchIconAnimatable = (Animatable2) searchIcon.getDrawable();
        searchParameter.setCursorVisible(true);
        searchIconAnimatable.start();
    }

    public void closeSearch() {
        searchIsActive = false;
        ImageView searchIcon = getView().findViewById(R.id.searchIcon);
        searchIcon.setImageResource(R.drawable.avd_trimclip_searchback_back_to_search);
        Animatable2 searchIconAnimatable = (Animatable2) searchIcon.getDrawable();
        searchParameter.setCursorVisible(false);
        searchIconAnimatable.start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Context retrieveContext() {
        return this.getContext();
    }

    public interface onSearchFragmentQueryListener {
        // TODO: Update argument type and name
        void searchCancelled();

        void queryArtist(String artistQuery);

        void onArtistSelected(ArtistModel artistModel);

        void closeKeyboard(View viewWithKeyboard);
    }
}
