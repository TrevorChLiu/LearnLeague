package edu.cuhk.csci3310.learnleague;

import java.util.LinkedList;

public interface OnRankingLoadedListener {
    void onLoaded(LinkedList<User> dayRanking, LinkedList<User> weekRanking,
                  LinkedList<User> monthRanking);
}
