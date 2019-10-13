package spacers.hackupc.communities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class AllContactsFragment extends Fragment {

    protected ArrayList<Contact> filteredContacts;

    public static AllContactsFragment newInstance() {
        AllContactsFragment fragment = new AllContactsFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Vibrator v = (Vibrator) getContext().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        final EditText contactSearch = getView().findViewById(R.id.searchContact);
        ListView contactListView = getView().findViewById(R.id.contactsList);

        // Load contacts
        ((MainActivity) getActivity()).getWam().loadContacts();
        filteredContacts = ((MainActivity) getActivity()).getWam().getContacts();
        Collections.sort(filteredContacts);

        // create adapter
        final CustomAdapter customAdapter = new CustomAdapter(getActivity(), filteredContacts);
        contactListView.setAdapter(customAdapter);

        // Search options
        contactListView.setTextFilterEnabled(true);
        contactSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                customAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // When contact is clicked, add to friendsList
        contactListView.setClickable(true);
        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Contact chosenContact = customAdapter.getFilteredContactList().get(i);
                final String[] communities = ((MainActivity) getActivity()).getDataManager().getAllCommunities().toArray(new String[0]);

                // Get the choose communities window
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pick a Community");
                builder.setItems(communities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Add the contact to the json
                        String chosenCommunities = communities[which];
                        boolean success = ((MainActivity) getActivity()).getDataManager().addContactToCommunities(chosenContact, chosenCommunities);
                        // Information
                        String toast;
                        if (success) toast = " added to ";
                        else { toast = " is already in "; }
                        Toast.makeText(getContext(), chosenContact.getName() + toast + chosenCommunities + "!", Toast.LENGTH_SHORT).show();
                        v.vibrate(VibrationEffect.createOneShot(10L, VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }
}
