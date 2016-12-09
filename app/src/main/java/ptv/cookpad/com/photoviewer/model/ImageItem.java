package ptv.cookpad.com.photoviewer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by VS on 12/6/2016.
 */

public class ImageItem implements Parcelable {
    private String thumb;
    private String path;
    private String name;
    private long sizeOfByte;

    public ImageItem() {

    }

    protected ImageItem(Parcel in) {
        thumb = in.readString();
        path = in.readString();
        name = in.readString();
        sizeOfByte = in.readLong();
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel in) {
            return new ImageItem(in);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSizeOfByte() {
        return sizeOfByte;
    }

    public void setSizeOfByte(long sizeOfByte) {
        this.sizeOfByte = sizeOfByte;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thumb);
        dest.writeString(path);
        dest.writeString(name);
        dest.writeLong(sizeOfByte);
    }


}
