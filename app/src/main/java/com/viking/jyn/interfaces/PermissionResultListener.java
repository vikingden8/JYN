package com.viking.jyn.interfaces;

public interface PermissionResultListener {
    void onPermissionResult(int requestCode, String permissions[], int[] grantResults);
}
