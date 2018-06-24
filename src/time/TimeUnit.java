package time;

import java.time.LocalTime;
import java.time.temporal.*;

public interface TimeUnit extends Temporal {
	
	public int compareTo(TimeUnit that);
	
	public LocalTime toLocalTime();
	
	public int toSecondOfDay();
	
	public int toMinuteOfDay();
}
