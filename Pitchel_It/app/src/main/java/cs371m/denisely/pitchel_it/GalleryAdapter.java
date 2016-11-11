package cs371m.denisely.pitchel_it;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Denise on 11/6/2016.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private File[] files;

    public GalleryAdapter(File[] files) { this.files = files; }


    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_thumbnail, parent, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Test", Toast.LENGTH_LONG);
            }
        });

        return new GalleryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        File pictureName = files[position];
        Bitmap bitmap = BitmapFactory.decodeFile(pictureName.getAbsolutePath());
        holder.thumbnail.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return files.length;
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {
        public View container;
        public ImageView thumbnail;

        public GalleryViewHolder(View view){
            super(view);
            container = view;
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }
}
