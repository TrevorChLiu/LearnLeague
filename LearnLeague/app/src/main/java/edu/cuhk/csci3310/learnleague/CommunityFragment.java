package edu.cuhk.csci3310.learnleague;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.cuhk.csci3310.learnleague.adapters.PostAdapter;
import edu.cuhk.csci3310.learnleague.models.Post;

/**
 * CommunityFragment
 */
public class CommunityFragment extends Fragment {

    private RecyclerView postsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton newPostButton;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();

    public CommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化视图
        postsRecyclerView = view.findViewById(R.id.posts_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        newPostButton = view.findViewById(R.id.new_post_button);

        // 设置RecyclerView
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), postList, false);
        postsRecyclerView.setAdapter(postAdapter);

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(this::loadPosts);

        // set add new post event listener
        newPostButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewPostActivity.class);
            startActivity(intent);
        });

        // 加载帖子数据
        loadPosts();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次回到页面时刷新数据
        loadPosts();
    }

    /**
     * 加载帖子列表数据
     */
    private void loadPosts() {
        swipeRefreshLayout.setRefreshing(true);

        ApiClient.getPosts(new ApiClient.ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> result) {
                // 需要在主线程中更新UI
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        postList.clear();
                        postList.addAll(result);
                        postAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to fetch posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }
        });
    }
}