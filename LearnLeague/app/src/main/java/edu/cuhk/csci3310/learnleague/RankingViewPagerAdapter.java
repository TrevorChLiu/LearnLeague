package edu.cuhk.csci3310.learnleague;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.LinkedList;

public class RankingViewPagerAdapter extends FragmentStateAdapter {
    private int containerResID;

    RankingListFragment rankingListFragment;

    public RankingViewPagerAdapter(@NonNull Fragment fragment, int containerResID) {
        super(fragment);
        this.containerResID = containerResID;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: rankingListFragment = new RankingListFragment(User.getDayRanking(), containerResID); break;
            case 1: rankingListFragment = new RankingListFragment(User.getWeekRanking(), containerResID); break;
            case 2: rankingListFragment = new RankingListFragment(User.getMonthRanking(), containerResID); break;
            default: rankingListFragment = new RankingListFragment(User.getDayRanking(), containerResID); break;
        }
        return rankingListFragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
