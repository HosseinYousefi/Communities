package spacers.hackupc.communities;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommunitiesFragment extends Fragment {

    private List<Contact> friendsList = new ArrayList<>();
    private CustomAdapter customAdapter;
    private String communities;
    private Random rand = new Random();

    public static CommunitiesFragment newInstance() {
        CommunitiesFragment fragment = new CommunitiesFragment();
        return fragment;
    }

    public CommunitiesFragment setCommunities(String communities) {
        this.communities = communities;
        return this;
    }

    private String getRandomMessage(List<String> messages) {
        Integer randIndex = rand.nextInt(messages.size());
        return messages.get(randIndex);
    }

    public void refreshFriendsList() {
        friendsList.clear();
        friendsList.addAll(((MainActivity) getActivity()).getDataManager().getCommunitiesContacts(communities));
        customAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        // Show the array of friends on the screen with a custom adapter
        final ListView friendsListView = getView().findViewById(R.id.friendsList);
        customAdapter = new CustomAdapter(getActivity(), friendsList);
        friendsListView.setAdapter(customAdapter);

        // Handle click on friend
        friendsListView.setClickable(true);
        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Contact contact = friendsList.get(i);
                // Get random message from Communities's messages bank
                try {
                    String msg = getRandomMessage(((MainActivity) getActivity()).getDataManager().getCommunitiesMessages(communities));
                    // Call the WhatsApp API `sendMessage` method with the right parameters
                    ((MainActivity) getActivity()).getWam().sendMessage(contact, msg);
                    // Update the place of the contact in the list, and update the adapter
                    ((MainActivity) getActivity()).getDataManager().repositionCommunitiesContact(contact, communities);
                    refreshFriendsList();
                }
                catch (IllegalArgumentException e) {  // This happens when there are no messages configured for a communities
                    Toast.makeText(getContext(), "Whoops! This communities has no configured greetings!", Toast.LENGTH_LONG).show();
                    v.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }

        });

        // Handle remove friend
        friendsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Remove a message when someone long clicks on it.
                Contact noLongerFriend = friendsList.get(i);
                ((MainActivity) getActivity()).getDataManager().removeContactFromCommunities(noLongerFriend, communities);
                Toast.makeText(getContext(), noLongerFriend.getName() + " removed from friends!", Toast.LENGTH_SHORT).show();
                v.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE));
                refreshFriendsList();
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
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

}
