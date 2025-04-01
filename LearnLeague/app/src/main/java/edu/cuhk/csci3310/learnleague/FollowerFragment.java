package edu.cuhk.csci3310.learnleague;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.LinkedList;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowerFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView mRecyclerView;
    private FollowListAdapter mAdapter;
    // Used to record the old follower list
    LinkedList<User> mUserList = new LinkedList<>();
    User owner;

    public FollowerFragment() {
        // Required empty public constructor
    }

    public FollowerFragment(User owner) {
        this.owner = owner;
    }

    public FollowerFragment(User owner, OnFollowsListLoadedListener listener) {
        this.owner = owner;
        listener.onFollowsListLoaded(mUserList);
    }



    public FollowerFragment(User owner, LinkedList<User> mUserList) {
        this.owner = owner;
        // The user list might be clean
        this.mUserList.addAll(mUserList);

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowerFragment newInstance(String param1, String param2) {
        FollowerFragment fragment = new FollowerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follower, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerview);

        mAdapter = new FollowListAdapter(getActivity(), mUserList, new OnStopProfileOtherListener() {
            @Override
            public void OnStop() {
                User.getFollowersList(owner.getUserID(), new OnFollowsListLoadedListener() {
                    @Override
                    public void onFollowsListLoaded(LinkedList<User> followsList) {
                        if (getActivity() != null)
                            getActivity().runOnUiThread(() -> {
                                // Don't load again if the data is not changed
                                if (mAdapter != null) {
                                    if (!mUserList.equals(followsList)) {
                                        mAdapter.updateData(followsList);
                                        mUserList.clear();
                                        mUserList.addAll(followsList);
                                    }
                                }
                            });
                    }
                });
            }
        }, R.id.fragment_container_follower_list);

        // Preload the follower list in other's profile and use in if ready
        User.getFollowersList(owner.getUserID(), new OnFollowsListLoadedListener() {
            @Override
            public void onFollowsListLoaded(LinkedList<User> followsList) {
                if (getActivity() != null)
                    getActivity().runOnUiThread(() -> {
                        // Don't load again if the data is not changed
                        if (mAdapter != null) {
                            if (!mUserList.equals(followsList)) {
                                mAdapter.updateData(followsList);
                                mUserList.clear();
                                mUserList.addAll(followsList);
                            }
                        }
                    });
            }
        });


        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.updateData(mUserList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set on the listener for back button
        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}