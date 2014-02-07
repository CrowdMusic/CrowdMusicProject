package com.hdm.crowdmusic.gui.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.Client;
import com.hdm.crowdmusic.core.Track;
import com.hdm.crowdmusic.core.streaming.actions.IAction;
import com.hdm.crowdmusic.core.streaming.actions.IOnFailureHandler;
import com.hdm.crowdmusic.core.streaming.actions.SimplePostTask;
import com.hdm.crowdmusic.core.streaming.actions.Vote;
import com.hdm.crowdmusic.gui.activities.ClientActivity;
import com.hdm.crowdmusic.util.Constants;

import java.util.List;

public class PlaylistTrackAdapter extends ArrayAdapter<Track> {


    private final Context context;
    private IOnFailureHandler noResponse = new IOnFailureHandler() {

        @Override
        public void execute() {
            new NoServerResponseDialog(context).show();
        }
    };

    public PlaylistTrackAdapter(Context context, int textViewResourceId, List<Track> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_playlistentry, null);

        }

        final Track track = ((ArrayAdapter<Track>) this).getItem(position);

        if (track != null) {
            TextView trackName = (TextView) v.findViewById(R.id.playlist_item_track);
            TextView artist = (TextView) v.findViewById(R.id.playlist_item_artist);
            TextView rating = (TextView) v.findViewById(R.id.playlist_item_rating);

            ImageView voteUp = (ImageView) v.findViewById(R.id.playlist_upvote);
            ImageView voteDown = (ImageView)  v.findViewById(R.id.playlist_downvote);


            //TODO: Extend for cover and duration, maybe upvote count

            if (artist != null) {
                artist.setText(track.getArtist());
            }

            if (trackName != null) {
                trackName.setText(track.getTrackName());
            }

            if (rating != null) {
                rating.setText("Votes: " + String.valueOf(track.getRating()));
            }

            if (voteUp != null){
                voteUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getContext() instanceof ClientActivity)
                        {
                        ClientActivity activity = (ClientActivity) getContext();

                        final Client client = ((IOnClientRequestListener) activity).getClientData();
                        SimplePostTask<Vote> task = new SimplePostTask<Vote>(client.getServerIP(), Constants.PORT, null, noResponse);
                        task.execute(new IAction<Vote>() {
                            @Override
                            public String getPostTarget() {
                                return "vote/up";
                            }

                            @Override
                            public Vote getParam() {
                                return  new Vote(track.getId(), client.getClientIP());
                            }
                        });
                        }

                    }
                });
            }

            if (voteDown != null){

                voteDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getContext() instanceof ClientActivity)
                        {
                            ClientActivity activity = (ClientActivity) getContext();

                            final Client client = ((IOnClientRequestListener) activity).getClientData();
                            SimplePostTask<Vote> task = new SimplePostTask<Vote>(client.getServerIP(), Constants.PORT, null, noResponse);
                            task.execute(new IAction<Vote>() {
                                @Override
                                public String getPostTarget() {
                                    return "vote/down";
                                }

                                @Override
                                public Vote getParam() {
                                    return  new Vote(track.getId(), client.getClientIP());
                                }
                            });
                        }
                    }
                });
            }


        }
        return v;
    }


}
