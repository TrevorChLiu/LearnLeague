package edu.cuhk.csci3310.learnleague;

import java.util.LinkedList;

public interface OnFollowsListLoadedListener {
    void onFollowsListLoaded(LinkedList<User> followsList);
}
