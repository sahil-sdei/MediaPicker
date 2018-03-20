package com.erikagtierrez.multiple_media_picker.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.erikagtierrez.multiple_media_picker.Adapters.MediaAdapter;
import com.erikagtierrez.multiple_media_picker.Gallery;
import com.erikagtierrez.multiple_media_picker.R;

import java.util.ArrayList;
import java.util.List;

public class VideosFragment extends Fragment {
    private static RecyclerView recyclerView;
    private MediaAdapter mAdapter;
    private List<String> mediaList = new ArrayList<>();
    public static List<Boolean> selected = new ArrayList<>();
    public static ArrayList<String> imagesSelected = new ArrayList<>();
    public static String parent;

    public static VideosFragment newInstance() {
        VideosFragment imagesFragment = new VideosFragment();
        return imagesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaList.clear();
        selected.clear();
        mediaList.addAll(TwoFragment.videosList);
        selected.addAll(TwoFragment.selected);
        populateRecyclerView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_one, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        for (int i = 0; i < selected.size(); i++) {
            if (imagesSelected.contains(mediaList.get(i))) {
                selected.set(i, true);
            } else {
                selected.set(i, false);
            }
        }
        mAdapter = new MediaAdapter(mediaList, selected, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (!selected.get(position).equals(true) && imagesSelected.size() < Gallery.maxSelection) {
                    imagesSelected.add(mediaList.get(position));
                    selected.set(position, !selected.get(position));
                    mAdapter.notifyItemChanged(position);
                } else if (selected.get(position).equals(true)) {
                    if (imagesSelected.indexOf(mediaList.get(position)) != -1) {
                        imagesSelected.remove(imagesSelected.indexOf(mediaList.get(position)));
                        selected.set(position, !selected.get(position));
                        mAdapter.notifyItemChanged(position);
                    }
                }
                Gallery.selectionTitle = imagesSelected.size();
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));


        populateRecyclerView();
        return v;
    }

    private void populateRecyclerView() {

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}



