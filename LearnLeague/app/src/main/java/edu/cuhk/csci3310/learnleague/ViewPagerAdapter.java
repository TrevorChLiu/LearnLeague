package edu.cuhk.csci3310.learnleague;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new PostsFragment();
            case 1: return new CommentsFragment();
            case 3: return new RepliesFragment();
            default: return new PostsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
