package cs371m.denisely.pitchel_it;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adobe.creativesdk.aviary.AdobeImageIntent;
import com.adobe.creativesdk.aviary.internal.headless.utils.MegaPixels;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Denise on 11/22/2016.
 */

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ImageViewHolder> {
    private ArrayList<File> listFile;
    private Context context;

    private CarouselClickListener carouselClickListener;

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);

        return new ImageViewHolder(itemView);
    }

    public CarouselAdapter(ArrayList<File> listFile) {
        this.listFile = listFile;
    }

    // Need this interface for editImage intent to go back to MainFragment's onActivityResult
    public interface CarouselClickListener {
        void onCarouselItemClicked(File pic);
    }

    public void setCarouselClickListener(CarouselClickListener carouselClickListener){
        this.carouselClickListener = carouselClickListener;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        File picture = listFile.get(position);
        Bitmap myBitmap;
        try {
            myBitmap = GalleryAdapter.scaleBitmapAndKeepRatio(BitmapFactory.decodeFile(picture.toString()));
        } catch (Exception e){
            return;
        }
        holder.thumbnail.setImageBitmap(myBitmap);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start new OneImage Activity
                holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        carouselClickListener.onCarouselItemClicked(picture);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listFile.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public View container;

        public ImageViewHolder(View view) {
            super(view);
            container = view;
            thumbnail = (ImageView) view.findViewById(R.id.recycler_item);
        }
    }
}
