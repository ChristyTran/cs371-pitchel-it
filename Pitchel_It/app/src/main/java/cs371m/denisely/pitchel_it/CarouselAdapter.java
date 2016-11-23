package cs371m.denisely.pitchel_it;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Denise on 11/22/2016.
 */

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ImageViewHolder> {
    private File[] listFile;

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);

        return new ImageViewHolder(itemView);
    }

    public CarouselAdapter(File[] listFile){
        this.listFile = listFile;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Bitmap myBitmap = GalleryAdapter.scaleBitmapAndKeepRatio(BitmapFactory.decodeFile(listFile[position].toString()));
        holder.thumbnail.setImageBitmap(myBitmap);
    }

    @Override
    public int getItemCount() {
        return listFile.length;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public View container;

        public ImageViewHolder(View view){
            super(view);
            container = view;
            thumbnail = (ImageView) view.findViewById(R.id.recycler_item);
        }
    }
}
