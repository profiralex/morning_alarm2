package app.utils;

/**
 * clasa care defineste alarma
 * @author ALEXANDR
 *
 */
public class Alarm {
	
	public static final int 	ALARM_ENABLED = 1;
	public static final int 	ALARM_DISABLED = 0;
	
	private String id;
	private int    enabled;
	private String description;
	private long   time;
	private String daysOfWeek;
	private String wakeUpMode;
	private String ringtone;

	/**
	 * constructor
	 * @param id
	 * @param enabled
	 * @param description
	 * @param time
	 * @param daysOfWeek
	 * @param wakeUpMode
	 * @param ringtone
	 */
	public Alarm(String id, int enabled, String description, long time,String daysOfWeek, String wakeUpMode, String ringtone) {
		this.id = id;
		this.enabled=enabled;
		this.description = description;
		this.time = time;
		this.daysOfWeek=daysOfWeek;
		this.wakeUpMode = wakeUpMode;
		this.ringtone = ringtone;
	}

	/**
	 * metoda returneaza timpul la care a fost setata alarma
	 * @return timpul
	 */
	public long getTime() {
		return time;
	}

	/**
	 * metoda seteaza timpul la care a fost setata alarma
	 * @param time timpul
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * metoda returneaza modul de trezire la care a fost setata alarma
	 * @return modul
	 */
	public String getWakeUpMode() {
		return wakeUpMode;
	}

	/**
	 * metoda seteaza modul de trezire la care a fost setata alarma
	 * @param wakeUpMode module de trezire
	 */
	public void setWakeUpMode(String wakeUpMode) {
		this.wakeUpMode = wakeUpMode;
	}

	/**
	 * metoda returneaza melodia la care a fost setata alarma
	 * @return melodia
	 */
	public String getRingtone() {
		return ringtone;
	}

	/**
	 * * metoda seteaza melodia la care a fost setata alarma
	 * @param ringtone melodia
	 */
	public void setRingtone(String ringtone) {
		this.ringtone = ringtone;
	}

	/**
	 * metoda returneaza zilelele saptaminii la care a fost setata alarma
	 * @return zilele saptaminii
	 */
	public String getDaysOfWeek() {
		return daysOfWeek;
	}

	/**
	 * metoda seteaza zilele saptaminii la care a fost setata alarma
	 * @param zilele saptaminii
	 */
	public void setDaysOfWeek(String daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	/**
	 * metoda returneaza daca alarma este enabled
	 * @return daca este enabled
	 */
	public int isEnabled() {
		return enabled;
	}

	/**
	 * metoda seteaza daca alarma este enabled
	 * @param enabled daca este enabled
	 */
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	/**
	 * metoda returneaza idul alarmei
	 * @return idul
	 */
	public String getId() {
		return id;
	}

	/**
	 * * metoda seteaza id-ul alarmei
	 * @param id id-ul alarmei
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * metoda returneaza descrierea alarmei
	 * @return descrieirea
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * metoda seteaza descrierea alarmei
	 * @param description descrierea
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	
	
	
}
