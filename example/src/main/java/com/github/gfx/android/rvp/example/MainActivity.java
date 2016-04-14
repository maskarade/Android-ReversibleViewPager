package com.github.gfx.android.rvp.example;

import com.github.gfx.android.rvp.ReversibleViewPager;
import com.github.gfx.android.rvp.example.databinding.PageBinding;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {
    SparseIntArray countMap = new SparseIntArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ReversibleViewPager viewPager = (ReversibleViewPager) findViewById(R.id.viewPager);
        assert viewPager != null;
        viewPager.setAdapter(new PagerAdapter() {
            static final String TAG = "PagerAdapter";

            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                Log.d(TAG, "instantiateItem: " + position);
                PageBinding page = PageBinding.inflate(getLayoutInflater(), container, false);

                int count = countMap.get(position, 0);
                countMap.put(position, count + 1);
                page.counter.setText(String.valueOf(count));

                page.counter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.getAdapter().notifyDataSetChanged();
                    }
                });

                page.left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.arrowScroll(View.FOCUS_LEFT);
                    }
                });
                page.right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.arrowScroll(View.FOCUS_RIGHT);
                    }
                });
                container.addView(page.getRoot());
                return page.getRoot();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return String.valueOf(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }
}
