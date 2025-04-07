package edu.cuhk.csci3310.learnleague;

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

import edu.cuhk.csci3310.learnleague.adapters.PostAdapter;
import edu.cuhk.csci3310.learnleague.models.Post;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private RecyclerView postsRecyclerView;
    private String userID;

    public PostsFragment() {
        // Required empty public constructor
    }

    public PostsFragment(String userID) {
        this.userID = userID;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostsFragment newInstance(String param1, String param2) {
        PostsFragment fragment = new PostsFragment();
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
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        postsRecyclerView = view.findViewById(R.id.recycler_view);

        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), postList, false);
        postsRecyclerView.setAdapter(postAdapter);

        loadPosts();

        return view;
    }

    private void loadPosts() {
        ApiClient.getUserPosts(userID, new ApiClient.ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> result) {
                // 需要在主线程中更新UI
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        postList.clear();
                        postList.addAll(result);
                        postAdapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                /*
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to fetch posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
                 */
                // Do nothing
            }
        });
    }


}