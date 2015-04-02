/*
 * Copyright (C) 2015 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.doctoror.gifimageloader.sample;

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
            final NetworkGifImageView imageView;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.grid_item_demo, parent, false);
                imageView = (NetworkGifImageView) convertView.findViewById(R.id.image);
                convertView.setTag(imageView);
            } else {
                imageView = (NetworkGifImageView) convertView.getTag();
            }

            imageView.setImageInfo(getItem(position), mImageLoader);
            return convertView;
        }
    }
}
