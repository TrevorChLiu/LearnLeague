package edu.cuhk.csci3310.learnleague;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

public class RankingListAdapter extends Adapter<RankingListAdapter.RankingViewHolder> {
    private Context context;
    private LayoutInflater mInflater;

    private LinkedList<User> mUsersList;
    private int containerResID;


    class RankingViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatarView;
        public TextView userNameView;
        public TextView userIdView;
        public TextView userStudyHr;
        public TextView userRanking;


        final edu.cuhk.csci3310.learnleague.RankingListAdapter mAdapter;

        public RankingViewHolder(View itemView, edu.cuhk.csci3310.learnleague.RankingListAdapter adapter) {
            super(itemView);

            avatarView = itemView.findViewById(R.id.avatar);
            userNameView = itemView.findViewById(R.id.username);
            userIdView = itemView.findViewById(R.id.user_id);
            userStudyHr = itemView.findViewById(R.id.ranking_hrs);
            userRanking = itemView.findViewById(R.id.ranking_no);
            this.mAdapter = adapter;

            context = itemView.getContext();

            itemView.setOnClickListener(v -> {
                        User selectedUser = mUsersList.get((int) getItemId());
                        FragmentTransaction transaction = ((MainActivity)context).getSupportFragmentManager().beginTransaction();
                        transaction.replace(containerResID, ProfileOtherFragment.newInstance(selectedUser, containerResID, null));
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
            );
        }
    }

    public RankingListAdapter(Context context,
                             LinkedList<User> mUsersList, int containerResID) {

        mInflater = LayoutInflater.from(context);
        this.mUsersList = mUsersList;
        this.containerResID = containerResID;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.ranking_list_item, parent, false);
        return new RankingViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        User mUser = mUsersList.get(position);

        Glide.with(context)
                .load("http://192.168.31.41:5000/get_avatar/" + mUser.getUserID())
                .placeholder(R.drawable.default_avatar)
                .signature(new ObjectKey(mUser.getAvatarVersion()))
                .into(holder.avatarView);
        holder.userIdView.setText("@" + mUser.getUserID());
        holder.userNameView.setText(mUser.getUserName());

        // Convert to hours and keep 2 digits
        holder.userStudyHr.setText(Math.round(mUser.getTmp_seconds() / 3600.0 * 100) / 100.0 + " Hours");
        holder.userRanking.setText("No." + (position + 1));
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
