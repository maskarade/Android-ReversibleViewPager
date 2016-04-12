package com.github.gfx.android.rvp.example;

import com.github.gfx.android.rvp.ReversibleViewPager;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReversibleViewPager viewPager = (ReversibleViewPager) findViewById(R.id.viewPager);
        assert viewPager != null;
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View page = getLayoutInflater().inflate(R.layout.page, container, false);
                TextView textView = (TextView) page.findViewById(R.id.text);
                assert textView != null;
                textView.setText(String.valueOf(position));
                container.addView(page);
                return page;
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
