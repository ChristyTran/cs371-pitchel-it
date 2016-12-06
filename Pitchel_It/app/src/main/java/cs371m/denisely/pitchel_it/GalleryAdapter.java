package cs371m.denisely.pitchel_it;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Denise on 11/6/2016.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private File[] files;
    File destination = new File(Environment.getExternalStorageDirectory(), "Pictures" + File.separator + "Pitchel It/");

    public static int maxW = 500;
    public static int maxH = 500;
    private Context context;

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference dbname;
    String userName;

    TriggerMapUpdate triggerMapUpdate;

    public GalleryAdapter(File[] files, Context context) {
        this.files = files;
        this.context = context;
    }

    public interface TriggerMapUpdate{
        public void updateMap();
    }

    public void setUpdateMapListener(TriggerMapUpdate triggerMapUpdate){
        this.triggerMapUpdate = triggerMapUpdate;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_thumbnail, parent, false);
        return new GalleryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        Log.d("files length", Integer.toString(files.length));
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null){
            userName = user.getEmail().replaceAll("\\.", "@");
            dbname = FirebaseDatabase.getInstance().getReference(userName);
        }

        File pictureName = files[position];
        Bitmap bitmap = scaleBitmapAndKeepRatio(BitmapFactory.decodeFile(pictureName.toString()));
        holder.thumbnail.setImageBitmap(bitmap);
        holder.deletePhoto.setVisibility(View.INVISIBLE);

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

        holder.thumbnail.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(20);
                if (holder.deletePhoto.getVisibility() == View.INVISIBLE) {
                    holder.deletePhoto.setVisibility(View.VISIBLE);
                    holder.deletePhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove from firebase
                            if (user != null){
                                String tempstring = files[position].toString().replace(".", "@");
                                String convertFilePath = tempstring.replace("/", "*");
                                dbname.child(convertFilePath).removeValue();
                            }
                            // Remove from device
                            File toDelete = files[position];
                            files[position].delete();

                            // Rescan the gallery after deleting from device
                            MediaScannerConnection.scanFile(context, // Refresh device's gallery
                                    new String[] { toDelete.getAbsolutePath() }, null,
                                    (path, uri) -> {
                                        Log.d("ExternalStorage", "Scanned " + path + ":");
                                        Log.d("ExternalStorage", "-> uri=" + uri);
                                    });

                            // Update recyclerView
                            files = destination.listFiles();
                            notifyDataSetChanged();
                            triggerMapUpdate.updateMap();

                        }
                    });
                } else {
                    holder.deletePhoto.setVisibility(View.INVISIBLE);
                }
                return true;
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
        public ImageButton deletePhoto;

        public GalleryViewHolder(View view){
            super(view);
            container = view;
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            deletePhoto = (ImageButton) view.findViewById(R.id.delete_from_gallery);
        }
    }
}
