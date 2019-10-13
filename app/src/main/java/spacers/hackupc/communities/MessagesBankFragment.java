package spacers.hackupc.communities;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;

public class MessagesBankFragment extends Fragment {

    private List<String> messages;
    private EditText editText;
    private Button addButton;
    private ArrayAdapter<String> arrayAdapter;
    private String communities;

    public static MessagesBankFragment newInstance() {
        MessagesBankFragment fragment = new MessagesBankFragment();
        return fragment;
    }

    public MessagesBankFragment setCommunities(String communities) {
        this.communities = communities;
        return this;
    }

    public void refreshMessages() {
        messages.clear();
        messages.addAll(((MainActivity) getActivity()).getDataManager().getCommunitiesMessages(communities));
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        // Show the array of messages on the screen
        messages = ((MainActivity) getActivity()).getDataManager().getCommunitiesMessages(communities);
        ListView msgListView = getView().findViewById(R.id.messagesList);
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, messages);
        msgListView.setAdapter(arrayAdapter);

        // Handle adding a new message
        editText = getView().findViewById(R.id.editText);
        addButton = getView().findViewById(R.id.addItem);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String newMsg = editText.getText().toString();
                boolean success = ((MainActivity) getActivity()).getDataManager().addMessageToCommunities(newMsg, communities);
                if (!success) {
                    Toast.makeText(getContext(), "Message \"" + newMsg + "\" already in bank!", Toast.LENGTH_SHORT).show();
                }
                refreshMessages();
                editText.getText().clear();
            }
        });

        // Handle remove message
        msgListView.setClickable(true);
        msgListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Remove a message when someone long clicks on it.
                String removed = ((MainActivity) getActivity()).getDataManager().removeMessageFromCommunities(i, communities);
                Toast.makeText(getContext(),
                        "Message \"" + removed + "\" removed from bank!",
                        Toast.LENGTH_SHORT).show();
                v.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE));
                refreshMessages();
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
        return inflater.inflate(R.layout.fragment_messages_bank, container, false);
    }

}
