package edu.cuhk.csci3310.learnleague;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import androidx.recyclerview.widget.LinearLayoutManager;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankingListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;
    private RankingListAdapter mAdapter;

    private LinkedList<User> userRanking;
    private int containerResID;

    public RankingListFragment() {
        // Required empty public constructor
    }

    public RankingListFragment(LinkedList<User> userRanking, int containerResID) {
        this.userRanking = userRanking;
        this.containerResID = containerResID;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RankingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RankingListFragment newInstance(String param1, String param2) {
        RankingListFragment fragment = new RankingListFragment();
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
        View view = inflater.inflate(R.layout.fragment_ranking_list, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerview_ranking);


        mAdapter = new RankingListAdapter(getActivity(), userRanking, containerResID);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

}