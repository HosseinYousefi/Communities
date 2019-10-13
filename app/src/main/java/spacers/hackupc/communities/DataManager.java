package spacers.hackupc.communities;

import android.content.Context;
import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataManager {

    private Context context;
    private JSONObject data;

    public DataManager(Context context) {
        this.context = context;
        if (isSavedStateFilePresent()) {
            loadDataJson();
            System.out.println("Loaded");
        } else {
            createNewDataJson();
            System.out.println("Created");
        }
    }

    private boolean isSavedStateFilePresent() {
        File file = new File(context.getFilesDir().getAbsolutePath(), context.getString(R.string.saved_state_file));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        } else if (file.length() == 0) {
            return false;
        }
        return true;
    }

    private void loadDataJson() {
        try {
            File file = new File(context.getFilesDir().getAbsolutePath(), context.getString(R.string.saved_state_file));
            BufferedReader br = new BufferedReader(new FileReader(file));
            String json = br.readLine();
            if (json.length() < 1) {
                createNewDataJson();
                return;
            }
            data = new JSONObject(json);
        } catch (FileNotFoundException fileNotFound) {
            fileNotFound.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createNewDataJson() {
        // If there's no data, create the default communities called "Friends"
        data = new JSONObject();
        addCommunities("Friends");
        addMessageToCommunities("Hey!", "Friends");
    }

    public JSONObject getData() {
        return data;
    }

    private JSONObject getCommunitiesData(String communities) {
        try {
            return data.getJSONObject(communities);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getAllCommunities() {
        Iterator<String> keys = data.keys();
        return Lists.newArrayList(keys);
    }

    public boolean addCommunities(String communities) {
        try {
            List<String> currentCommunities = getAllCommunities();
            if (currentCommunities.contains(communities)) return false;
            data.put(communities, new JSONObject()
                    .put("Messages", new JSONArray())
                    .put("Contacts", new JSONArray()));
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeCommunities(String communities) {
        data.remove(communities);
    }

    public List<String> getCommunitiesMessages(String communities) {
        JSONArray messagesArray = null;
        try {
            messagesArray = getCommunitiesData(communities).getJSONArray("Messages");
            ArrayList<String> messages = new ArrayList<>();
            for (int i = 0; i < messagesArray.length(); i++) {
                messages.add(messagesArray.get(i).toString());
            }
            return messages;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Contact> getCommunitiesContacts(String communities) {
        try {
            JSONArray contactsArray = getCommunitiesData(communities).getJSONArray("Contacts");
            List<Contact> contacts = new ArrayList<>();
            for (int i = 0; i < contactsArray.length(); i++) {
                JSONObject contactJson = (JSONObject) contactsArray.get(i);
                contacts.add(new Contact(contactJson));
            }
            return contacts;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addContactToCommunities(Contact contact, String communities) {
        List<Contact> communitiesContacts = getCommunitiesContacts(communities);
        for (Contact contact1 : communitiesContacts) {
            if (contact1.getId().equals(contact.getId())) return false;
        }
        communitiesContacts.add(contact);
        try {
            JSONArray contactsJson = new JSONArray();
            for (Contact contact1 : communitiesContacts) contactsJson.put(contact1.asJSONObj());
            data.getJSONObject(communities).put("Contacts", contactsJson);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeContactFromCommunities(Contact contact, String communities) {
        try {
            JSONArray contactsJson = new JSONArray();
            for (Contact contact1 : getCommunitiesContacts(communities)) {
                if (!contact1.getId().equals(contact.getId())) {
                    contactsJson.put(contact1.asJSONObj());
                }
            }
            data.getJSONObject(communities).put("Contacts", contactsJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void repositionCommunitiesContact(Contact contact, String communities) {
        removeContactFromCommunities(contact, communities);
        addContactToCommunities(contact, communities);
    }

    public boolean addMessageToCommunities(String message, String communities) {
        try {
            JSONArray ja = getCommunitiesData(communities).getJSONArray("Messages");
            for (int i = 0; i < ja.length(); i++)
                if (ja.getString(i).equals(message))
                    return false;
            ja.put(message);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String removeMessageFromCommunities(Integer index, String communities) {
        try {
            return (String) getCommunitiesData(communities).getJSONArray("Messages").remove(index);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void saveData() {
        System.out.println(data.toString());
        try {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), context.getString(R.string.saved_state_file)));
            fos.write(data.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
