package cs371m.denisely.pitchel_it;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
    public static int maxW = 500;
    public static int maxH = 500;
    private Context context;

    public GalleryAdapter(File[] files, Context context) {
        this.files = files;
        this.context = context;
    }


    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_thumbnail, parent, false);
        return new GalleryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        File pictureName = files[position];
        Bitmap bitmap = scaleBitmapAndKeepRatio(BitmapFactory.decodeFile(pictureName.toString()));
        holder.thumbnail.setImageBitmap(bitmap);

        holder.thumbnail.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                // Start new OneImage Activity
                Intent intent = new Intent(context, OneImage.class);
                intent.putExtra("thumbnail_path", files[position].toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                //getActivity.overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
            }
        });
    }

    public static Bitmap scaleBitmapAndKeepRatio(Bitmap bitmap) {
        Bitmap resizedBitmap = null;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int newWidth = -1;
        int newHeight = -1;
        float multFactor = -1.0F;
        if(originalHeight > originalWidth) {
            newHeight = maxH;
            multFactor = (float) originalWidth/(float) originalHeight;
            newWidth = (int) (newHeight*multFactor);
        } else if(originalWidth > originalHeight) {
            newWidth = maxW;
            multFactor = (float) originalHeight/ (float)originalWidth;
            newHeight = (int) (newWidth*multFactor);
        } else {
            // originalHeight==originalWidth
            newHeight = maxH;
            newWidth = maxW;
        }
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return resizedBitmap;
    }

    @Override
    public int getItemCount() {
        if (files == null){
            return 0;
        }
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
