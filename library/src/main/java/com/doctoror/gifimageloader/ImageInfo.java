/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.doctoror.gifimageloader;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Contains information about an image URL and type
 */
public final class ImageInfo implements Parcelable {

    public enum Type {

        /**
         * Static image that can be decoded to a Bitmap
         */
        STATIC,

        /**
         * Animated gif image
         */
        ANIMATED
    }

    @NonNull
    private final String mUrl;

    @NonNull
    private final Type mType;

    public ImageInfo(@NonNull final String url, @NonNull final Type type) {
        mUrl = url;
        mType = type;
    }

    private ImageInfo(final Parcel p) {
        mUrl = p.readString();
        mType = (Type) p.readSerializable();
    }

    @Override
    public void writeToParcel(final Parcel p, final int flags) {
        p.writeString(mUrl);
        p.writeSerializable(mType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    public String getUrl() {
        return mUrl;
    }

    @NonNull
    public Type getType() {
        return mType;
    }

    public boolean isAnimated() {
        return mType == Type.ANIMATED;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ImageInfo imageInfo = (ImageInfo) o;

        if (mType != imageInfo.mType) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (!mUrl.equals(imageInfo.mUrl)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = mUrl.hashCode();
        result = 31 * result + mType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "mUrl='" + mUrl + '\'' +
                ", mType='" + mType + '\'' +
                '}';
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(final Parcel source) {
            return new ImageInfo(source);
        }

        @Override
        public ImageInfo[] newArray(final int size) {
            return new ImageInfo[size];
        }
    };
}