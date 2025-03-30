package edu.cuhk.csci3310.learnleague;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileOtherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileOtherFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private User owner;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter viewPagerAdapter;
    private View view;
    int parentContainer;

    public ProfileOtherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ProfileOtherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileOtherFragment newInstance(User user, int parentContainer) {
        ProfileOtherFragment fragment = new ProfileOtherFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        fragment.setCurrentUser(user);
        fragment.setParentContainer(parentContainer);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile_other, container, false);

        // Create the nav tab layout for this page
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager2 = view.findViewById(R.id.viewPager2);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });


        // Show the profile owner's personal information
        updateProfileView(view);

        // Entries to see follow lists
        TextView following = view.findViewById(R.id.following);
        TextView followers = view.findViewById(R.id.followers);
        following.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(parentContainer, new FollowingFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Back button listener
        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    /**
     * Set on the user of this profile.
     */
    public void setCurrentUser(User user) {
        this.owner = user;
    }

    public void setParentContainer(int resID) {
        parentContainer = resID;
    }

    /**
     * Update the profile view with latest information
     * @param view The View of profile
     */
    private void updateProfileView(View view) {
        TextView username = view.findViewById(R.id.username);
        TextView userID = view.findViewById(R.id.user_id);
        TextView email = view.findViewById(R.id.email);
        TextView following = view.findViewById(R.id.following);
        TextView followers = view.findViewById(R.id.followers);
        ImageView avatar = view.findViewById(R.id.avatar);

        username.setText(owner.getUserName());
        userID.setText("@" + owner.getUserID());
        email.setText(owner.getUserEmail());
        following.setText(owner.getNumFollowing() + " Following");
        followers.setText(owner.getNumFollowers() + " Followers");
        if (owner.getAvatar() != null) {
            avatar.setImageBitmap(owner.getAvatar());
        }
    }
}