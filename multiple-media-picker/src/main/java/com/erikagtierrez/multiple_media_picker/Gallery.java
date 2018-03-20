package com.erikagtierrez.multiple_media_picker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.erikagtierrez.multiple_media_picker.Fragments.ImagesFragment;
import com.erikagtierrez.multiple_media_picker.Fragments.OneFragment;
import com.erikagtierrez.multiple_media_picker.Fragments.TwoFragment;
import com.erikagtierrez.multiple_media_picker.Fragments.VideosFragment;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static int selectionTitle;
    public static String title;
    public static int maxSelection;
    public static int mode;
    PreviousPageFragmentListener previousPageFragmentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnResult();
            }
        });

        title = getIntent().getExtras().getString("title");
        maxSelection = getIntent().getExtras().getInt("maxSelection");
        if (maxSelection == 0) maxSelection = Integer.MAX_VALUE;
        mode = getIntent().getExtras().getInt("mode");
        setTitle(title);
        selectionTitle = 0;

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        OpenGallery.selected.clear();
        OpenGallery.imagesSelected.clear();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (selectionTitle > 0) {
            setTitle(String.valueOf(selectionTitle));
        }
    }

    //This method set up the tab view for images and videos
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (mode == 1 || mode == 2) {
            adapter.addFragment(new OneFragment(), "Images");
        }
        if (mode == 1 || mode == 3)
            adapter.addFragment(new TwoFragment(), "Videos");
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private Fragment mFragmentAtPos0;
        private Fragment mFragmentAtPos1;


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (mFragmentAtPos0 == null) {
                    mFragmentAtPos0 = OneFragment.newInstance(new FirstPageFragmentListener() {
                        public void onSwitchToNextFragment() {
                            getSupportFragmentManager().beginTransaction().remove(mFragmentAtPos0).commit();
                            mFragmentAtPos0 = ImagesFragment.newInstance();
                            notifyDataSetChanged();
                        }
                    });
                }

                if (viewPager.getCurrentItem() == 0)
                    previousPageFragmentListener = new PreviousPageFragmentListener() {
                        @Override
                        public void onSwitchToPreviousFragment() {
                            getSupportFragmentManager().beginTransaction().remove(mFragmentAtPos0).commit();
                            mFragmentAtPos0 = OneFragment.newInstance();
                            notifyDataSetChanged();
                        }
                    };

                return mFragmentAtPos0;
            } else if (position == 1) {
                if (mFragmentAtPos1 == null) {
                    mFragmentAtPos1 = TwoFragment.newInstance(new FirstPageFragmentListener() {
                        public void onSwitchToNextFragment() {
                            getSupportFragmentManager().beginTransaction().remove(mFragmentAtPos1).commit();
                            mFragmentAtPos1 = VideosFragment.newInstance();
                            notifyDataSetChanged();
                        }
                    });
                }

                if (viewPager.getCurrentItem() == 1)
                    previousPageFragmentListener = new PreviousPageFragmentListener() {
                        @Override
                        public void onSwitchToPreviousFragment() {
                            getSupportFragmentManager().beginTransaction().remove(mFragmentAtPos1).commit();
                            mFragmentAtPos1 = TwoFragment.newInstance();
                            notifyDataSetChanged();
                        }
                    };

                return mFragmentAtPos1;
            }
            return null;

//            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof OneFragment && mFragmentAtPos0 instanceof ImagesFragment)
                return POSITION_NONE;
            else if (object instanceof ImagesFragment && mFragmentAtPos0 instanceof OneFragment)
                return POSITION_NONE;
            else if (object instanceof TwoFragment && mFragmentAtPos1 instanceof VideosFragment)
                return POSITION_NONE;
            else if (object instanceof VideosFragment && mFragmentAtPos1 instanceof TwoFragment)
                return POSITION_NONE;
            return POSITION_UNCHANGED;
        }

    }


    public interface FirstPageFragmentListener {
        void onSwitchToNextFragment();
    }

    public interface PreviousPageFragmentListener {
        void onSwitchToPreviousFragment();
    }

    private void returnResult() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("result", ImagesFragment.imagesSelected);
        returnIntent.putStringArrayListExtra("resultVideo", VideosFragment.imagesSelected);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        boolean found = false;
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fObj : fragments) {

            if (viewPager.getCurrentItem() == 0)
                if (fObj instanceof ImagesFragment) {
                    found = true;
                    break;
                }

            if (viewPager.getCurrentItem() == 1)
                if (fObj instanceof VideosFragment) {
                    found = true;
                    break;
                }
        }
        if (found) {
            previousPageFragmentListener.onSwitchToPreviousFragment();
        } else {
            super.onBackPressed();
        }
    }
}
