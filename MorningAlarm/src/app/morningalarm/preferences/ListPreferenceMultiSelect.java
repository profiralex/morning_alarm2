package app.morningalarm.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.widget.ListView;
import app.morningalarm.R;
import app.morningalarm.R.styleable;

/**
 * clasa care definesc lista de preferinte cu checkboxuri
 * in care se pot selecta mai mult de un singur element
 * @author ALEXANDR
 *
 */
public class ListPreferenceMultiSelect extends ListPreference {
	private String separator;
	private static final String DEFAULT_SEPARATOR = "OV=I=XseparatorX=I=VO"; 
	private String checkAllKey = null;
	private boolean[] mClickedDialogEntryIndices;
	
	/**
	 * constructorul clasei
	 * @param context
	 * @param attrs
	 */
	public ListPreferenceMultiSelect(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ListPreferenceMultiSelect);
        checkAllKey = a.getString( R.styleable.ListPreferenceMultiSelect_checkAll );
        String s = a.getString(R.styleable.ListPreferenceMultiSelect_separator );
        if( s != null ) {
        	separator = s;
        } else {
        	separator = DEFAULT_SEPARATOR;
        }
     // Initialize the array of boolean to the same size as number of entries
        mClickedDialogEntryIndices = new boolean[getEntries().length];
    }
	
	@Override
	/**
	 * Intializeaza elementele vectorului din care
	 * se vor alege checkboxurile
	 */
    public void setEntries(CharSequence[] entries) {
    	super.setEntries(entries);
        mClickedDialogEntryIndices = new boolean[entries.length];
    }
    
	/**
	 * constructorul clasei
	 * @param context
	 */
    public ListPreferenceMultiSelect(Context context) {
        this(context, null);
    }

    
    @Override
    /**
     * metoda ce pregateste fereastra de dialog
     */
    protected void onPrepareDialogBuilder(Builder builder) {
    	CharSequence[] entries = getEntries();
    	CharSequence[] entryValues = getEntryValues();
        if (entries == null || entryValues == null || entries.length != entryValues.length ) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array which are both the same length");
        }

        restoreCheckedEntries();
        builder.setMultiChoiceItems(entries, mClickedDialogEntryIndices, 
                new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which, boolean val) {
						if( isCheckAllValue( which ) == true ) {
							checkAll( dialog, val );
						}
						mClickedDialogEntryIndices[which] = val;
					}
        });
    }
    
    /**
     * metoda returneaza true daca a fost selectat elementul all
     * @param which elementul selectat
     * @return true daca a fost selectat elementul all
     */
    private boolean isCheckAllValue( int which ){
    	final CharSequence[] entryValues = getEntryValues();
    	if(checkAllKey != null) {
			return entryValues[which].equals(checkAllKey);
		}
    	return false;
    }
    
    /**
     * metoda ce seteaza toate elementele din lista
     * @param dialog
     * @param val
     */
    private void checkAll( DialogInterface dialog, boolean val ) {
    	ListView lv = ((AlertDialog) dialog).getListView();
		int size = lv.getCount();
		for(int i = 0; i < size; i++) {
	        lv.setItemChecked(i, val);
	        mClickedDialogEntryIndices[i] = val;
	    }
    }

    /**
     * metoda ce face parsing la valoarea preferintei si returneaza vetorul de stringuri
     * @param val
     * @return
     */
    public String[] parseStoredValue(CharSequence val) {
		if ( val==null || "".equals(val) ) {
			return null;
		}
		else {
			return ((String)val).split(separator);
		}
    }
    
    /**
     * metoda ce seteaza inapoi preferintele dupa ce sa iesit din 
     * fereastra de dialog
     */
    private void restoreCheckedEntries() {
    	CharSequence[] entryValues = getEntryValues();
    	
    	String[] vals = parseStoredValue(getValue());
    	
    	if ( vals != null ) {
    		List<String> valuesList = Arrays.asList(vals);
        	for ( int i=0; i<entryValues.length; i++ ) {
        		CharSequence entry = entryValues[i];
            	if ( valuesList.contains(entry) ) {
        			mClickedDialogEntryIndices[i] = true;
        		}
        	}
//        	}
    	}
    }

    
    /**
     * se apeleaza la inchiderea preferintei
     */
	@Override
    protected void onDialogClosed(boolean positiveResult) {
		ArrayList<String> values = new ArrayList<String>();
        
    	CharSequence[] entryValues = getEntryValues();
        if (positiveResult && entryValues != null) {
        	for ( int i=0; i<entryValues.length; i++ ) {
        		if ( mClickedDialogEntryIndices[i] == true ) {
        			// Don't save the state of check all option - if any
        			String val = (String) entryValues[i];
        			if( checkAllKey == null || (val.equals(checkAllKey) == false) ) {
        				values.add(val);
        			}
        		}
        	}
            if (callChangeListener(values)) {
        		setValue(join(values, separator));
            }
        }
    }
	
	/**
	 * metoda ce uneste valorile elementelor intrun singru string 
	 * folosind un separator al clasei
	 * @param pColl
	 * @param separator
	 * @return
	 */
	protected static String join( Iterable< ? extends Object > pColl, String separator )
    {
        Iterator< ? extends Object > oIter;
        if ( pColl == null || ( !( oIter = pColl.iterator() ).hasNext() ) )
            return "";
        StringBuilder oBuilder = new StringBuilder( String.valueOf( oIter.next() ) );
        while ( oIter.hasNext() )
            oBuilder.append( separator ).append( oIter.next() );
        return oBuilder.toString();
    }
	
	/**
	 * metoda ce afla daca in preferinte se contine un element
	 * @param straw String to be found
	 * @param haystack Raw string that can be read direct from preferences
	 * @param separator Separator string. If null, static default separator will be used
	 * @return boolean True if the straw was found in the haystack
	 */
	public static boolean contains( String straw, String haystack, String separator ){
		if( separator == null ) {
			separator = DEFAULT_SEPARATOR;
		}
		String[] vals = haystack.split(separator);
		for( int i=0; i<vals.length; i++){
			if(vals[i].equals(straw)){
				return true;
			}
		}
		return false;
	}
}
