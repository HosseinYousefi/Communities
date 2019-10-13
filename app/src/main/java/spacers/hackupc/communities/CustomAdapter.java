package spacers.hackupc.communities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Contact> implements Filterable {

    private Context mContext;
    private List<Contact> originalContactList;
    private List<Contact> filteredContactList;
    private ItemFilter mFilter = new ItemFilter();

    public CustomAdapter(@NonNull Context context, List<Contact> list) {
        super(context, 0, list);
        mContext = context;
        filteredContactList = list;
        originalContactList = list;
    }

    public List<Contact> getFilteredContactList() {
        return this.filteredContactList;
    }

    @Override
    public int getCount() {
        return this.filteredContactList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItem = inflater.inflate(R.layout.list_single, parent, false);
        Contact currentContact = filteredContactList.get(position);

        ImageView image = (ImageView) listItem.findViewById(R.id.contact_img);
        Drawable drawable;
        try {
            Uri contactPhotoUri = Uri.parse(currentContact.getPhotoUri());
            InputStream inputStream = getContext().getContentResolver().openInputStream(contactPhotoUri);
            drawable = Drawable.createFromStream(inputStream, contactPhotoUri.toString());
        } catch (Exception e) {
            drawable = getContext().getDrawable(R.drawable.avatar);
        }
        image.setImageDrawable(drawable);

        TextView name = (TextView) listItem.findViewById(R.id.contact_name);
        name.setText(currentContact.getName());

        return listItem;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<Contact> list = originalContactList;
            int count = list.size();
            final List<Contact> nlist = new ArrayList<>(count);

            Contact current;

            for (int i = 0; i < count; i++) {
                current = list.get(i);
                if (current.getName().toLowerCase().contains(filterString)) {
                    nlist.add(current);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredContactList = (ArrayList<Contact>) results.values;
            notifyDataSetChanged();
        }
    }
}