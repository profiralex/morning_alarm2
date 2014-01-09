package app.morningalarm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import app.alarmmanager.AlarmSetter;
import app.database.AlarmDbUtilities;


/**
 * Created by alexandr on 1/9/14.
 */
public class AssociateAlarmsActivity extends Activity {

    private ArrayList<PersonListAdapter.PersonHolder> personList;
    private PersonListAdapter personListAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_association);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        personList = new ArrayList();
        addSomePersons();
        emptyTextViewVisibility();

        personListAdapter = new PersonListAdapter(this, R.layout.list_item_persons, personList);
        ListView lv = (ListView) findViewById(R.id.listView1);
        lv.setAdapter(personListAdapter);
        registerForContextMenu(lv);
    }

    private void addSomePersons() {
        for (int i = 0; i < 10; i++) {
            PersonListAdapter.PersonHolder person = new PersonListAdapter.PersonHolder();
            person.email = i + "a";
            person.accepted = (i % 2 == 0) ? true : false;
            personList.add(person);
        }

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
                addNewPerson();
                break;
        }
        return true;
    }

    private void removeAllPersons() {
        personList = new ArrayList();
        personListAdapter.notifyDataSetChanged();
    }

    private void addNewPerson(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.person_add_dialog);
        dialog.setTitle("Add Person");
        dialog.show();

        final EditText email = (EditText) dialog.findViewById(R.id.email_edit);
        Button addButton = (Button) dialog.findViewById(R.id.add_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonListAdapter.PersonHolder ph = new PersonListAdapter.PersonHolder();
                ph.accepted = false;
                ph.email = email.getText().toString();
                personList.add(ph);
                personListAdapter.notifyDataSetChanged();
                dialog.cancel();
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
                PersonListAdapter.PersonHolder person = personList.get((int) info.id);
                personList.remove(person);
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
    public static class PersonListAdapter extends ArrayAdapter<PersonListAdapter.PersonHolder> {

        private ArrayList<PersonHolder> persons;

        public PersonListAdapter(Context context, int textViewResourceId,
                                 ArrayList<PersonHolder> objects) {
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

            final PersonHolder person = persons.get(position);
            if (person != null) {
                RadioButton radioButton = (RadioButton) v.findViewById(R.id.radioButton);
                TextView email = (TextView) v.findViewById(R.id.textView);

                radioButton.setChecked(person.accepted);
                email.setText(person.email);
            }

            return v;
        }

        public static class PersonHolder {
            public String email;
            public boolean accepted;
        }

    }


}
