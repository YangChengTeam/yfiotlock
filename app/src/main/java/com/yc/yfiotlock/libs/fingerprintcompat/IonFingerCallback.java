package com.yc.yfiotlock.libs.fingerprintcompat;

public interface IonFingerCallback
{

	void onSucceed();

	void onFailed();

	void onHelp(String help);

	void onError(String error);

	void onCancel();
}
