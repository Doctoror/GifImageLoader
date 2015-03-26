package com.doctoror.gifimageloader.sample;

import com.doctoror.gifimageloader.GifImageView;

import android.app.Activity;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class GifImageViewDemoActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image_demo);

        final GifImageView view = (GifImageView) findViewById(R.id.image);

        final byte[] result = loadMovie();
        view.setAnimatedGif(result);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                //view.setAnimatedGif(result);
            }
        }, 1000);
    }

    private byte[] loadMovie() {
        try {
            final InputStream is = getAssets().open("cat.gif");
            final byte[] buf = new byte[1024];
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            int read;
            while ((read = is.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, read);
            }

            is.close();
            os.close();

            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
