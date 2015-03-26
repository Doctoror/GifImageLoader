package com.doctoror.gifimageloader.sample;

import com.doctoror.gifimageloader.Configuration;
import com.doctoror.gifimageloader.DefaultGifImageLoader;
import com.doctoror.gifimageloader.GifImageLoader;
import com.doctoror.gifimageloader.ImageInfo;
import com.doctoror.gifimageloader.NetworkGifImageView;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;


public class DemoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        final GridView grid = (GridView) findViewById(R.id.activity_demo_grid);
        grid.setAdapter(new DemoAdapter(this, generateImageInfo()));
    }

    @NonNull
    private static List<ImageInfo> generateImageInfo() {
        final List<ImageInfo> list = new ArrayList<>(12);
        list.add(new ImageInfo("http://i.imgur.com/eezCO.gif", ImageInfo.Type.ANIMATED));
        list.add(new ImageInfo("http://i.imgur.com/0SBuk.gif", ImageInfo.Type.ANIMATED));
        list.add(new ImageInfo("http://i.imgur.com/xzPki.gif", ImageInfo.Type.ANIMATED));
        list.add(new ImageInfo("http://i.imgur.com/f6due.gif", ImageInfo.Type.ANIMATED));
        list.add(new ImageInfo("http://i.imgur.com/6N2Fa.gif", ImageInfo.Type.ANIMATED));
        list.add(new ImageInfo("http://i.imgur.com/tvwQC.gif", ImageInfo.Type.ANIMATED));
        list.add(new ImageInfo("http://i.imgur.com/Gq3F2.gif", ImageInfo.Type.ANIMATED));
        list.add(new ImageInfo("http://i.imgur.com/XAxaV.gif", ImageInfo.Type.ANIMATED));
        list.add(new ImageInfo("http://i.imgur.com/DYO6X.gif", ImageInfo.Type.ANIMATED));
        list.add(new ImageInfo("http://i.imgur.com/9DWBx.gif", ImageInfo.Type.ANIMATED));
        list.add(new ImageInfo(
                "http://vignette3.wikia.nocookie.net/robber-penguin-agency/images/7/72/Gaming-Rage-Face.jpg/revision/latest?cb=20140618172440",
                ImageInfo.Type.STATIC));
        list.add(new ImageInfo(
                "http://mylolface.com/assets/faces/happy-i-see-what-you-did-there-clean.jpg",
                ImageInfo.Type.STATIC));
        return list;
    }

    private static final class DemoAdapter extends BaseAdapter2<ImageInfo> {

        private final GifImageLoader mImageLoader;

        private DemoAdapter(@NonNull final Context context,
                @Nullable final List<ImageInfo> items) {
            super(context, items);
            mImageLoader = DefaultGifImageLoader
                    .getInstance(context, Configuration.LRU_CACHE_SIZE_IN_BYTES);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.grid_item_demo, parent, false);
            }

            final NetworkGifImageView imageView = (NetworkGifImageView) convertView;
            imageView.setImageInfo(getItem(position), mImageLoader);

            return convertView;
        }
    }
}
