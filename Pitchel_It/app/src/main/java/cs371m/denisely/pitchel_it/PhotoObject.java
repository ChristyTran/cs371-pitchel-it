package cs371m.denisely.pitchel_it;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ctt494 on 12/4/16.
 */

public class PhotoObject {
    public String tag;
    public LatLng coordinates;
    public String title;

    public PhotoObject (){
    }

    public PhotoObject (String tag, LatLng coordinates) {
        this.tag = tag;
        this.coordinates = coordinates;
    }

    public String getTag() { return tag; }

    public LatLng getCoordinates() { return coordinates; }

}