package edu.cuhk.csci3310.learnleague;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RankingViewPagerAdapter extends FragmentStateAdapter {
    public RankingViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new RankingDayFragment();
            case 1: return new RankingWeekFragment();
            case 2: return new RankingMonthFragment();
            default: return new RankingDayFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
