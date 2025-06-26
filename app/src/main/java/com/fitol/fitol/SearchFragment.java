package com.fitol.fitol;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.fitol.FitOl.R;
import com.fitol.FitOl.databinding.FragmentSearchBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private FirebaseFirestore database;
    private List<Post> posts;
    private RecycleViewAdapter adapterItem;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);


        database = FirebaseFirestore.getInstance();


        binding = FragmentSearchBinding.inflate(inflater, container, false);


        posts = new ArrayList<>();
        adapterItem = new RecycleViewAdapter(posts, getContext());
        binding.searchRecycleView.setAdapter(adapterItem);

        binding.searchRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));


        getDatas();

        return binding.getRoot();
    }


    public void getDatas() {
        database.collection("POSTS").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Log.e("SearchFragment", "No documents found in the 'posts' collection.");
            }
            else
            {
                for (DocumentSnapshot document : queryDocumentSnapshots)
                {
                    String username = document.getString("username");
                    String messageText = document.getString("message");
                    String imageUrl = document.getString("imageUrl");

                    Post post = new Post(username, messageText, imageUrl);
                    posts.add(post);
                }
                adapterItem.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            Log.e("SearchFragment", "Error getting documents: " + e.getMessage());
            Toast.makeText(getContext(), "Veriler alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}