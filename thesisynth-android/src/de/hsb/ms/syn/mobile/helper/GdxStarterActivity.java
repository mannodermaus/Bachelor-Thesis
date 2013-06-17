package de.hsb.ms.syn.mobile.helper;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;

import de.hsb.ms.syn.mobile.MainActivity;

/**
 * Wrapper class for starting the LibGDX context from within Android.
 * I wanted to have a native Activity before GDX comes into play, which
 * is why this activity's main job is to start GDX. It receives an Intent
 * to do so from the MainActivity at will.
 * @author Marcel
 *
 */
public class GdxStarterActivity extends AndroidApplication {
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		initialize(MainActivity.program, MainActivity.cfg);
	}
}