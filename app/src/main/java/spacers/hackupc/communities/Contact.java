package spacers.hackupc.communities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

public class Contact implements Parcelable, Comparable<Contact> {

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            String id = source.readString();
            String name = source.readString();
            String phone = source.readString();
            String photoUri = source.readString();
            return new Contact(id, name, phone, photoUri);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
    private String id;
    private String name;
    private String phone;
    private String photoUri;

    public Contact(String id, String name, String phone, String photoUri) {
        this.id = id;
        this.name = name;
        this.phone = phone.replaceAll("[^0-9]", "");  // Normalize phone numbers
        this.photoUri = photoUri;
    }

    public Contact(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getString("id");
            this.name = jsonObject.getString("name");
            this.phone = jsonObject.getString("phone");
            this.photoUri = "Nope";
            if (jsonObject.has("photoUri")) this.photoUri = jsonObject.getString("photoUri");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getPhotoUri() {
        return this.photoUri;
    }

    public JSONObject asJSONObj() {
        try {
            return new JSONObject()
                    .put("id", id)
                    .put("name", name)
                    .put("phone", phone)
                    .put("photoUri", photoUri);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int compareTo(@NonNull Contact contact) {
        return name.compareTo(contact.name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(photoUri);
    }
}
