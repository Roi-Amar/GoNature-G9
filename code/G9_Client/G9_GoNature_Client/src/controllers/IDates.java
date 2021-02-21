package controllers;

import java.time.LocalDate;

import com.jfoenix.controls.JFXDatePicker;

public interface IDates {
	public boolean checkLocalDate(LocalDate from, LocalDate to);
	
	public LocalDate getValueDate(JFXDatePicker date);
}
