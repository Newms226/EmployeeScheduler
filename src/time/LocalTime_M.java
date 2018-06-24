package time;

import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

import Availability.Availability;

public class LocalTime_M implements TimeUnit {
	public final LocalTime localTime;

	public LocalTime_M(LocalTime dateTime) {
		this.localTime = dateTime;
	}

	@Override
	public int compareTo(TimeUnit that) {
		return localTime.compareTo(that.toLocalTime());
	}

	@Override
	public LocalTime toLocalTime() {
		return localTime;
	}
	
	@Override
	public int toSecondOfDay() {
		return localTime.toSecondOfDay();
	}
	
	@Override
	public int toMinuteOfDay() {
		return (int) Math.round(((double)toSecondOfDay()) / 60.0);
	}

	@Override
	public boolean isSupported(TemporalUnit unit) {
		return localTime.isSupported(unit);
	}

	@Override
	public Temporal with(TemporalField field, long newValue) {
		return localTime.with(field, newValue);
	}

	@Override
	public Temporal plus(long amountToAdd, TemporalUnit unit) {
		return localTime.plus(amountToAdd, unit);
	}

	@Override
	public long until(Temporal endExclusive, TemporalUnit unit) {
		return localTime.until(endExclusive, unit);
	}

	@Override
	public boolean isSupported(TemporalField field) {
		return localTime.isSupported(field);
	}

	@Override
	public long getLong(TemporalField field) {
		return localTime.getLong(field);
	}
	
	public LocalTime_M minus(TemporalAmount amount) {
		return new LocalTime_M(localTime.minus(amount));
	}
	
	public LocalTime_M minusMinutes(long minutes) {
		return new LocalTime_M(localTime.minusMinutes(minutes));
	}
	
	public LocalTime_M minusMinutes() {
		return new LocalTime_M(localTime.minusMinutes(Availability.AVOID_MINUTE_AMOUNT));
	}
	
	public LocalTime_M plus(TemporalAmount amount) {
		return new LocalTime_M(localTime.plus(amount));
	}
	
	public LocalTime_M plusMinutes(long minutes) {
		return new LocalTime_M(localTime.plusMinutes(minutes));
	}
	
	
	public LocalTime_M plusMinutes() {
		return new LocalTime_M(localTime.plusMinutes(Availability.AVOID_MINUTE_AMOUNT));
	}
	
	public static void main(String[] args) {
		System.out.println(Math.round(5/2));
		System.out.println(Math.round(5.0/2.0));
	}
}
