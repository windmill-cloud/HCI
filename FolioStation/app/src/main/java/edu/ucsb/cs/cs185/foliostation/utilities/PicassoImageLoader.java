/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.utilities;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.lzy.imagepicker.loader.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.File;

import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.models.Cards;

/**
 * Created by xuanwang on 2/24/17.
 */

public class PicassoImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Picasso.with(activity)//
                .load(new File(path))//
                .placeholder(R.mipmap.default_image)//
                .error(R.mipmap.default_image)//
                .resize(width, height)//
                .centerInside()//
                //.memoryPolicy(MemoryPolicy., MemoryPolicy.NO_STORE)//
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
        //这里是清除缓存的方法,根据需要自己实现
    }

    public static void loadImageToView(Context context, Cards.CardImage image, ImageView view, int width, int height) {
        if (image.isFromPath()) {
            File file = new File(image.mUrl);
            if(!file.exists()){
                Picasso.with(context)
                        .load(R.drawable.fileremoved)
                        //.resize(width, height)
                        .fit()
                        .noFade()
                        .into(view);
            } else {
                Picasso.with(context)
                        .load(new File(image.mUrl))
                        .resize(width, height)
                        .centerCrop()
                        .noFade()
                        .into(view);
            }
        } else {
            Picasso.with(context)
                    .load(image.mUrl)
                    .resize(width, height)
                    .centerCrop()
                    .noFade()
                    .into(view);
        }
    }

}