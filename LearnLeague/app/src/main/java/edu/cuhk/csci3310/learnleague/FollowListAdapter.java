package edu.cuhk.csci3310.learnleague;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

public class FollowListAdapter extends Adapter<edu.cuhk.csci3310.learnleague.FollowListAdapter.FollowViewHolder>  {
    private Context context;
    private LayoutInflater mInflater;

    private LinkedList<User> usersList;

    // the following pre-set res image path is for debugging, but good to let students to start with
    private String mDrawableFilePath = "android.resource://edu.cuhk.csci3310.cusweetspot/drawable/";

    class FollowViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatarView;
        public TextView userNameView;
        public TextView userIdView;

        final edu.cuhk.csci3310.learnleague.FollowListAdapter mAdapter;

        public FollowViewHolder(View itemView, edu.cuhk.csci3310.learnleague.FollowListAdapter adapter) {
            super(itemView);

            avatarView = itemView.findViewById(R.id.avatar);
            userNameView = itemView.findViewById(R.id.username);
            userIdView = itemView.findViewById(R.id.user_id);
            this.mAdapter = adapter;


            context = itemView.getContext();
            itemView.setOnClickListener(v -> {

            }
            );

        }
    }

    public FollowListAdapter(Context context,
                             LinkedList<User> usersList) {
        mInflater = LayoutInflater.from(context);
        this.usersList = usersList;

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.followlist_user, parent, false);
        return new FollowViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        User mImagePath = usersList.get(position);
        //Uri uri = Uri.parse(mImagePath);
        //holder.imageItemView.setImageURI(uri);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public void updateData(LinkedList<User> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }
}