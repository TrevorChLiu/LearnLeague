package edu.cuhk.csci3310.learnleague;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ProfileViewPagerAdapter extends FragmentStateAdapter {

    private String userID;

    public ProfileViewPagerAdapter(@NonNull Fragment fragment, String userID) {
        super(fragment);
        this.userID = userID;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new PostsFragment(userID);
            case 1: return new CommentsFragment(userID);
            case 2: return new RepliesFragment(userID);
            default: return new PostsFragment(userID);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
