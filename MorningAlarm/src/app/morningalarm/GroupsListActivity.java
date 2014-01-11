package app.morningalarm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.database.AlarmDbAdapter;
import app.database.AlarmDbUtilities;
import app.utils.Alarm;
import app.utils.Group;


/**
 * Created by alexandr on 1/9/14.
 */
public class GroupsListActivity extends Activity {

    private ArrayList<Group> groupList;
    private GroupListAdapter groupListAdapter;

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int pos = (int) position;
            Intent i  = new Intent(GroupsListActivity.this, GroupActivity.class);
            i.putExtra("id", groupList.get(pos).getId());
            GroupsListActivity.this.startActivityForResult(i, 0);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_group_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        groupList = AlarmDbUtilities.fetchAllGroups(this);
        emptyTextViewVisibility();

        groupListAdapter = new GroupListAdapter(this, R.layout.list_item_groups, groupList);
        ListView lv = (ListView) findViewById(R.id.listView1);
        lv.setAdapter(groupListAdapter);
        lv.setOnItemClickListener(itemClickListener);
        registerForContextMenu(lv);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        groupList = AlarmDbUtilities.fetchAllGroups(this);
        groupListAdapter.notifyDataSetChanged();
    }

    /**
     * metoda ce determina daca trebuie afisat sau nu textview cu textul ca nu sint persoane
     */
    private void emptyTextViewVisibility() {
        if (groupList.size() > 0)
            findViewById(R.id.id_empty_list_text_view).setVisibility(View.GONE);
        else
            findViewById(R.id.id_empty_list_text_view).setVisibility(View.VISIBLE);
    }

    @Override
    /**
     * creaza meniu cu optiuni
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_associations, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    /**
     * se apeleaza la alegerea unui element din meniul cu optiuni
     * si sterge toate alarmele
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_delete_all_option:
                removeAllGroups();
                break;
            case R.id.menu_new_person:
                addNewGroup();
                break;
        }
        return true;
    }

    private void removeAllGroups() {
        groupList.removeAll(groupList);
        AlarmDbUtilities.removeAllGroups(this);
        emptyTextViewVisibility();
        groupListAdapter.notifyDataSetChanged();
    }

    private void addNewGroup(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.group_add_dialog);
        dialog.setTitle("Add Group");
        dialog.show();

        final EditText nameEdit = (EditText) dialog.findViewById(R.id.group_name);
        final EditText messageEdit = (EditText) dialog.findViewById(R.id.group_message);
        Button addButton = (Button) dialog.findViewById(R.id.add_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {;
            String groupName = nameEdit.getText().toString();
            String groupMessage = messageEdit.getText().toString();
            if(groupMessage.isEmpty() || groupName.isEmpty()){
                Toast.makeText(dialog.getContext(), "Both fields are required", Toast.LENGTH_SHORT).show();
            }else{
                Alarm newAlarm = AlarmDbUtilities.fetchNewAlarm(GroupsListActivity.this);
                Group newGroup = AlarmDbUtilities.createGroup(GroupsListActivity.this, groupName, groupMessage, newAlarm.getId());
                groupList.add(newGroup);
                emptyTextViewVisibility();
                groupListAdapter.notifyDataSetChanged();
                dialog.cancel();
            }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    @Override
    /**
     * se apeleaza la crearea de meniu context
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }


    @Override
    /**
     * se apeleaza la alegerea unui element din meniul context
     * si sterge elementul ales
     */
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        switch (item.getItemId()) {
            case R.id.delete_option:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Group group= groupList.get((int) info.id);
                groupList.remove(group);
                AlarmDbUtilities.removeGroup(this, group.getId());
                emptyTextViewVisibility();
                groupListAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    /**
     * clasa adaptor care adapteaza aplicatia la interfata clasei ListView
     *
     * @author ALEXANDR
     */
    public static class GroupListAdapter extends ArrayAdapter<Group> {

        private ArrayList<Group> groups;

        public GroupListAdapter(Context context, int textViewResourceId,
                                 ArrayList<Group> objects) {
            super(context, textViewResourceId, objects);
            this.groups = objects;
        }

        /**
         * reprezinta metoda ce adapteaza vederile cu continutul alarmelor
         * la lista de alarme
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_groups, null);
            }

            final Group group = groups.get(position);
            if (group != null) {
                TextView name = (TextView) v.findViewById(R.id.textView);
                TextView message = (TextView) v.findViewById(R.id.textView2);

                name.setText(group.getName());
                message.setText(group.getMessage());
            }

            return v;
        }
    }


}
