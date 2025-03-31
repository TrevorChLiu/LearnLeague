package edu.cuhk.csci3310.learnleague;

import java.util.LinkedList;

public interface OnDataLoadedListener {
    void onUserLoaded(User user);

    void onFollowsListLoaded(LinkedList<User> followsList);
}
