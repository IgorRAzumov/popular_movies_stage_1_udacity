package ru.geekbrains.popular_movies_stage_1_udacity.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.geekbrains.popular_movies_stage_1_udacity.R;
import ru.geekbrains.popular_movies_stage_1_udacity.data.networkData.VideoResult;
import ru.geekbrains.popular_movies_stage_1_udacity.utils.NetworkUtils;


public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.VideoResultHolder> {
    private final List<VideoResult> videosResult;
    private final RecycleViewOnItemClickListener recyclerClickListener;

    public MovieVideosAdapter(List<VideoResult> videosResult,
                              RecycleViewOnItemClickListener recyclerClickListener) {
        this.videosResult = videosResult;
        this.recyclerClickListener = recyclerClickListener;
    }

    @NonNull
    @Override
    public MovieVideosAdapter.VideoResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.movie_video_card, parent,
                        false);
        return new VideoResultHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieVideosAdapter.VideoResultHolder holder, int position) {
        VideoResult video = videosResult.get(position);
        holder.bindDescriptionUrl(video.getSite());
        holder.bindPreview(holder.itemView.getContext(), video.getKey());
    }

    @Override
    public int getItemCount() {
        return videosResult == null
                ? 0
                : videosResult.size();
    }

    public VideoResult getVideo(int position) {
        return videosResult.get(position);
    }

    public interface RecycleViewOnItemClickListener {
        void onItemVideoRecyclerClick(int position);
    }

    class VideoResultHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_movie_video_card)
        ImageView movieVideo;

        VideoResultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindDescriptionUrl(String site) {
            movieVideo.setContentDescription(site);
        }

        void bindPreview(Context context, String key) {
            NetworkUtils.loadYouTubeThumbnail(context, key, movieVideo);
        }

        @Override
        public void onClick(View view) {
            recyclerClickListener.onItemVideoRecyclerClick(getAdapterPosition());
        }
    }
}

