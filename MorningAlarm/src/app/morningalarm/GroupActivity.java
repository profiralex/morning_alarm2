package app.morningalarm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.database.AlarmDbUtilities;
import app.utils.Alarm;
import app.utils.Group;
import app.utils.Person;


/**
 * Created by alexandr on 1/9/14.
 */
public class GroupActivity extends Activity {

    private ArrayList<Person> personList;
    private PersonListAdapter personListAdapter;
    private int groupId;
    private String alarmId;
    private Alarm alarm;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(GroupActivity.this,"Click",Toast.LENGTH_SHORT);
        }
    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_group);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        groupId = this.getIntent().getIntExtra("group_id",0);
        alarmId = this.getIntent().getStringExtra("alarm_id");
        alarm = AlarmDbUtilities.fetchAlarm(this, alarmId);
        personList = AlarmDbUtilities.fetchAllPersonsFromGroup(this,groupId);

        emptyTextViewVisibility();


        LinearLayout alarmLayout = (LinearLayout) this.findViewById(R.id.alarm_item_layout);
        alarmLayout.setOnClickListener(onClickListener);

        personListAdapter = new PersonListAdapter(this, R.layout.list_item_persons, personList);
        ListView lv = (ListView) findViewById(R.id.listView1);
        lv.setAdapter(personListAdapter);
        registerForContextMenu(lv);
    }


    /**
     * metoda ce determina daca trebuie afisat sau nu textview cu textul ca nu sint persoane
     */
    private void emptyTextViewVisibility() {
        if (personList.size() > 0)
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
                removeAllPersons();
                break;
            case R.id.menu_new_person:
                addNewperson();
                break;
        }
        return true;
    }

    private void removeAllPersons() {
        AlarmDbUtilities.removeAllPersonsFromGroup(this, groupId);
        personList.removeAll(personList);
        AlarmDbUtilities.removeAllPersonsFromGroup(this,groupId);
        emptyTextViewVisibility();
        personListAdapter.notifyDataSetChanged();
    }

    private void addNewperson(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.person_add_dialog);
        dialog.setTitle("Add Person");
        dialog.show();

        final EditText emailEdit = (EditText) dialog.findViewById(R.id.email_edit);
        Button addButton = (Button) dialog.findViewById(R.id.add_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {;
            String email = emailEdit.getText().toString();
            if(email.isEmpty()){
                Toast.makeText(dialog.getContext(), "Email field must be filled", Toast.LENGTH_SHORT).show();
            }else{
                Person newPerson = AlarmDbUtilities.createPerson(GroupActivity.this, email, groupId);
                personList.add(newPerson);
                emptyTextViewVisibility();
                personListAdapter.notifyDataSetChanged();
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

        emptyTextViewVisibility();
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
                Person person= personList.get((int) info.id);
                personList.remove(person);
                AlarmDbUtilities.removePerson(this, person.getEmail(), person.getGroupId());
                emptyTextViewVisibility();
                personListAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    /**
     * clasa adaptor care adapteaza aplicatia la interfata clasei ListView
     *
     * @author ALEXANDR
     */
    public static class PersonListAdapter extends ArrayAdapter<Person> {

        private ArrayList<Person> persons;

        public PersonListAdapter(Context context, int textViewResourceId,
                                 ArrayList<Person> objects) {
            super(context, textViewResourceId, objects);
            this.persons = objects;
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
                v = vi.inflate(R.layout.list_item_persons, null);
            }

            final Person person = persons.get(position);
            if (person != null) {
                TextView email = (TextView) v.findViewById(R.id.textView);
                RadioButton accepted = (RadioButton) v.findViewById(R.id.radioButton);

                email.setText(person.getEmail());
                accepted.setChecked(person.isAccepted());
            }

            return v;
        }
    }

}
