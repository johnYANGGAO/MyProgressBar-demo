package com.yonggao.myprogressbardemo;

import android.app.Activity;
import android.os.Bundle;

public class TestActivity extends Activity {
	ProgressbarCust progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hello);
		progress = (ProgressbarCust) findViewById(R.id.progress);

	}

}
