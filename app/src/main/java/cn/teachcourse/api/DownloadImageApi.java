package cn.teachcourse.api;

/*
 @author TeachCourse
 @date 创建于：2015-10-26
 */

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import cn.teachcourse.MyApplication;
import cn.teachcourse.R;
import cn.teachcourse.utils.MyCompressImageFactory;

public class DownloadImageApi extends BaseAPI {
    protected static ImageLoader imageLoader = MyApplication
            .getmImageLoader();
    // protected static ImageLoader imageLoader = ImageLoader.getInstance();
    static ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    static ImageView headImgs;
    static String urls = null;

    /**
     *
     */
    private static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_bg)
            .showImageForEmptyUri(R.drawable.default_bg)
            .showImageOnFail(R.drawable.default_bg)
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .considerExifParams(false)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .delayBeforeLoading(0)
            .resetViewBeforeLoading(false)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(false)
            .build();

    public static void setImageView(ImageView imageViw, String url) {
        imageLoader.displayImage(url, imageViw, options);
    }

    /**
     * 指定加载图片的大小
     *
     * @param imageView
     * @param url
     */
    public static void setImageViewSize(final ImageView imageView, String url) {
        ImageSize targetSize = new ImageSize(80, 50);
        imageLoader.loadImage(url, targetSize, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
            }
        });
    }

    /**
     * 设置ImageView图片数据
     *
     * @param headImg 控件名称
     * @param url     图片地址
     */
    public static void setImageViewAware(ImageView headImg, String url) {
        headImgs = headImg;
        urls = url;
        ImageAware imageAware = new ImageViewAware(headImg, true);
        imageLoader
                .displayImage(url, imageAware, options, animateFirstListener);
    }

    /**
     * 异步加载图片
     *
     * @param headImg
     * @param url
     */
    public static void setImageViewSync(ImageView headImg, String url) {
        Bitmap bitmap = imageLoader.loadImageSync(url, options);
        headImg.setImageBitmap(bitmap);
    }

    /**
     * 获取Bitmap对象
     *
     * @param url 图片地址
     * @return Bitmap对象
     */
    public static Bitmap getBitmapFrom(String url) {

        return imageLoader.loadImageSync(url, options);
    }

    /**
     * 清空ImageLoader对象和缓存数据
     */
    public static void clear() {
        if (imageLoader != null) {// 不等于空
            imageLoader.destroy();
            imageLoader.clearMemoryCache();
            imageLoader.clearDiscCache();
        }
    }

    /**
     * 重写图片加载监听器，内部类
     */
    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        private static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      final Bitmap loadedImage) {
            if (loadedImage != null) {
                final ImageView imageView = (ImageView) view;

                boolean firstDisplay = !displayedImages.contains(imageUri);

                if (firstDisplay) {
                    displayedImages.add(imageUri);
                    FadeInBitmapDisplayer.animate(imageView, 100);
                    imageView.setImageBitmap(MyCompressImageFactory
                            .comp(MyCompressImageFactory.comp(loadedImage)));
                }
            }
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            super.onLoadingStarted(imageUri, view);
            imageLoader.displayImage(urls, headImgs, options,
                    animateFirstListener);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view,
                                    FailReason failReason) {
            super.onLoadingFailed(imageUri, view, failReason);
            imageLoader.displayImage(urls, headImgs, options,
                    animateFirstListener);
        }

    }

}
