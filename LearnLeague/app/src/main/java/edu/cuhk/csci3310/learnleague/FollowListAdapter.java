package edu.cuhk.csci3310.learnleague;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

public class FollowListAdapter extends Adapter<edu.cuhk.csci3310.learnleague.FollowListAdapter.FollowViewHolder>  {
    private Context context;
    private LayoutInflater mInflater;

    private LinkedList<User> mUsersList;
    private int fragmentContainerID;
    private OnStopProfileOtherListener onStopProfileOtherListener;
    private int containerResID;

    // the following pre-set res image path is for debugging, but good to let students to start with

    class FollowViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatarView;
        public TextView userNameView;
        public TextView userIdView;
        public Button followButton;

        final edu.cuhk.csci3310.learnleague.FollowListAdapter mAdapter;

        public FollowViewHolder(View itemView, edu.cuhk.csci3310.learnleague.FollowListAdapter adapter) {
            super(itemView);

            avatarView = itemView.findViewById(R.id.avatar);
            userNameView = itemView.findViewById(R.id.username);
            userIdView = itemView.findViewById(R.id.user_id);
            this.mAdapter = adapter;
            followButton = itemView.findViewById(R.id.follow_list_button);


            context = itemView.getContext();


            // Register for user item in follow list
            itemView.setOnClickListener(v -> {
                User selectedUser = mUsersList.get((int) getItemId());
                FragmentTransaction transaction = ((MainActivity)context).getSupportFragmentManager().beginTransaction();
                transaction.replace(containerResID, ProfileOtherFragment.newInstance(selectedUser, containerResID,
                        new OnStopProfileOtherListener() {
                            @Override
                            public void OnStop() {
                                adapter.onStopProfileOtherListener.OnStop();
                            }
                        }));
                transaction.addToBackStack(null);
                transaction.commit();
            }
            );


        }
    }

    public FollowListAdapter(Context context,
                             LinkedList<User> mUsersList, OnStopProfileOtherListener onStopProfileOtherListener, int containerResID) {
        mInflater = LayoutInflater.from(context);
        this.mUsersList = mUsersList;
        this.onStopProfileOtherListener = onStopProfileOtherListener;
        this.containerResID = containerResID;
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
        User mUser = mUsersList.get(position);

        Glide.with(context)
                .load("http://192.168.31.41:5000/get_avatar/" + mUser.getUserID())
                .placeholder(R.drawable.default_avatar)
                .signature(new ObjectKey(mUser.getAvatarVersion()))
                .into(holder.avatarView);
        holder.userIdView.setText("@" + mUser.getUserID());
        holder.userNameView.setText(mUser.getUserName());

        // Register follow button
        String followButtonText = User.getCurrentUser().hasFollowed(mUser) ? "Following" : "Follow";
        holder.followButton.setText(followButtonText);
        holder.followButton.setOnClickListener(v -> {
            if (holder.followButton.getText().toString().equals("Following")) {
                holder.followButton.setText("Follow");
                User.getCurrentUser().unfollow(mUser);  // Unfollow
            } else {
                holder.followButton.setText("Following");
                User.getCurrentUser().follow(mUser);  // Follow
            }
        });
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public void updateData(LinkedList<User> usersList) {
        this.mUsersList = usersList;
        notifyDataSetChanged();
    }
}