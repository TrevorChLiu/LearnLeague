package edu.cuhk.csci3310.learnleague;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.LinkedList;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView mRecyclerView;
    private FollowListAdapter mAdapter;
    // Used to record the old following list
    LinkedList<User> mUserList = new LinkedList<>();
    User owner;

    public FollowingFragment() {
        // Required empty public constructor
    }

    public FollowingFragment(User owner) {
        this.owner = owner;
    }

    public void setmUserList(LinkedList<User> mUserList) {
        this.mUserList.addAll(mUserList);
    }
    public FollowingFragment(User owner, OnFollowsListLoadedListener listener) {
        this.owner = owner;
        listener.onFollowsListLoaded(mUserList);
    }

    public FollowingFragment(User owner, LinkedList<User> mUserList) {
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
     * @return A new instance of fragment FollowingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowingFragment newInstance(String param1, String param2) {
        FollowingFragment fragment = new FollowingFragment();
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
        View view = inflater.inflate(R.layout.fragment_following, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerview);

        mAdapter = new FollowListAdapter(getActivity(), mUserList);


        User.getFollowingList(owner.getUserID(), new OnFollowsListLoadedListener() {
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