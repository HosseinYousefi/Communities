package spacers.hackupc.communities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class CommunitiesChooserFragment extends Fragment {

    SendCommunities SCN;
    private List<String> communities;
    private String service = "Contacts";
    private CommunitiesAdapter communitiesAdapter;

    public static CommunitiesChooserFragment newInstance() {
        // Default new chooser to go to contacts screen
        CommunitiesChooserFragment ccf = new CommunitiesChooserFragment().setService("Contacts");
        return ccf;
    }

    public CommunitiesChooserFragment setService(String service) {
        this.service = service;
        return this;
    }

    public void refreshCommunities() {
        communities.clear();
        communities.addAll(((MainActivity) getActivity()).getDataManager().getAllCommunities());
        communitiesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        communities = ((MainActivity) getActivity()).getDataManager().getAllCommunities();

        // Show the array of friends on the screen
        ListView communitiesListView = getView().findViewById(R.id.communities_list);

        // Create the custom adapter
        communitiesAdapter = new CommunitiesAdapter(getActivity(), (ArrayList<String>) communities);
        communitiesListView.setAdapter(communitiesAdapter);

        // Handle click on communities
        communitiesListView.setClickable(true);
        communitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Send the data to the new fragment
                System.out.println(communities.get(i));
                SCN.sendCommunities(communities.get(i), service);
            }
        });

        // Handle add communities
        FloatingActionButton floatingActionButton = (FloatingActionButton) getView().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                b.setTitle("New communities name:");
                final EditText input = new EditText(getActivity());
                b.setView(input);
                b.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        // Get the new communities name
                        String result = input.getText().toString();
                        // Add to JSON
                        boolean success = ((MainActivity) getActivity()).getDataManager().addCommunities(result);
                        if (!success) {
                            Toast.makeText(getActivity(), "Communities " + result + " already exists!", Toast.LENGTH_SHORT).show();
                        }
                        else { refreshCommunities(); }
                    }
                });
                b.setNegativeButton("CANCEL", null);
                b.create().show();
            }
        });

        // Handle remove communities
        communitiesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String removeCommunities = communities.get(i);
                ((MainActivity) getActivity()).getDataManager().removeCommunities(removeCommunities);
                refreshCommunities();
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
        return inflater.inflate(R.layout.fragment_communities_choose, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SCN = (SendCommunities) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    interface SendCommunities {
        void sendCommunities(String communities, String service);
    }

    class CommunitiesAdapter extends ArrayAdapter<String> {

        public CommunitiesAdapter(Context context, ArrayList<String> communities) {
            super(context, 0, communities);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            String communities = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_single, parent, false);
            }
            // Lookup view for data population
            TextView communitiesName = (TextView) convertView.findViewById(R.id.contact_name);
            ImageView communitiesPhoto = (ImageView) convertView.findViewById(R.id.contact_img);
            // Populate the data into the template view using the data object
            communitiesName.setText(communities);
            communitiesPhoto.setImageDrawable(getContext().getDrawable(R.drawable.ic));
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
