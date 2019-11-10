package com.akat.filmreel.ui.movieDetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.akat.filmreel.R;
import com.akat.filmreel.util.Constants;
import com.akat.filmreel.util.InjectorUtils;
import com.bumptech.glide.Glide;

import java.util.Locale;

public class MovieDetailFragment extends Fragment {

    private ImageView posterView;
    private ImageView backdropView;
    private TextView titleView;
    private TextView origTitleView;
    private TextView languageView;
    private TextView releaseDateView;
    private TextView ratingView;
    private TextView popularityView;
    private TextView overviewView;

    private long movieId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        setHasOptionsMenu(true);

        Bundle bundle = requireArguments();
        movieId = bundle.getLong(Constants.PARAM.MOVIE_ID);

        posterView = rootView.findViewById(R.id.movie_poster_img);
        backdropView = rootView.findViewById(R.id.movie_backdrop);
        titleView = rootView.findViewById(R.id.movie_title);
        origTitleView = rootView.findViewById(R.id.movie_orig_title);
        languageView = rootView.findViewById(R.id.movie_language);
        releaseDateView = rootView.findViewById(R.id.movie_release_date);
        ratingView = rootView.findViewById(R.id.movie_rating);
        popularityView = rootView.findViewById(R.id.movie_popularity);
        overviewView = rootView.findViewById(R.id.movie_overview);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MovieDetailViewModelFactory factory
                = InjectorUtils.provideMovieDetailViewModelFactory(requireActivity(), movieId);
        MovieDetailViewModel viewModel = ViewModelProviders.of(this, factory).get(MovieDetailViewModel.class);
        viewModel.getMovie().observe(this, entry -> {
            if (entry != null) {
                titleView.setText(entry.getTitle());
                origTitleView.setText(entry.getOriginalTitle());
                overviewView.setText(entry.getOverview());
                languageView.setText(entry.getOriginalLanguage());
                popularityView.setText(
                        String.format(Locale.getDefault(),"%.3f", entry.getPopularity())
                );

                String dateFormat = requireActivity().getResources().getString(R.string.date_format);
                releaseDateView.setText(
                        String.format(dateFormat, entry.getReleaseDate())
                );

                String ratingFormat = requireActivity().getResources().getString(R.string.movie_rating_format);
                ratingView.setText(
                        String.format(ratingFormat,
                                entry.getVoteAverage(),
                                entry.getVoteCount()
                        )
                );

                String posterPath = entry.getPosterPath();
                if (posterPath != null) {
                    Glide.with(posterView.getContext())
                            .load(Constants.HTTP.POSTER_URL + posterPath)
                            .into(posterView);
                }

                String backdropPath = entry.getBackdropPath();
                if (posterPath != null) {
                    Glide.with(posterView.getContext())
                            .load(Constants.HTTP.BACKDROP_URL + backdropPath)
                            .into(backdropView);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==  R.id.menu_detail_share) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        String.format(getString(R.string.movie_share_text), titleView.getText())
                );
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}