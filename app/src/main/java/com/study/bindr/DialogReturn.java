package com.study.bindr;
/**
 * Interface for returning data from dialog.
 * */
public interface DialogReturn {
    void onPositive(String message );
    void onNegative(String message );

}
