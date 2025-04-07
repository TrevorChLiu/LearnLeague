package edu.cuhk.csci3310.learnleague;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.cuhk.csci3310.learnleague.adapters.CommentAdapter;
import edu.cuhk.csci3310.learnleague.models.Comment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private String userID;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public CommentsFragment(String userID) {
        this.userID = userID;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommentsFragment newInstance(String param1, String param2) {
        CommentsFragment fragment = new CommentsFragment();
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
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        commentsRecyclerView = view.findViewById(R.id.recycler_view);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentAdapter = new CommentAdapter(getContext(), commentList, new CommentAdapter.OnReplyClickListener() {
            @Override
            public void onReplyClick(Comment comment) {
                Intent intent = new Intent(getContext(), PostDetailActivity.class);
                intent.putExtra("POST_ID", comment.getPostId());
                getActivity().startActivity(intent);
            }
        });
        commentsRecyclerView.setAdapter(commentAdapter);

        loadComments();

        return view;
    }

    private void loadComments() {
        ApiClient.getUserComments(userID, new ApiClient.ApiCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> result) {
                getActivity().runOnUiThread(() -> {
                    commentList.clear();
                    commentList.addAll(result);
                    commentAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Exception e) {/*

                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Failed to load comments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });*/
                // Do nothing
            }
        });
    }
}