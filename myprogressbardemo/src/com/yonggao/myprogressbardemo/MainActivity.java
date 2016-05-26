package com.yonggao.myprogressbardemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author YongGao 2015/7/5
 *
 */
public class MainActivity extends Activity {
	private MyProgressBar mProgressBar;
	private EditText max, nowprogress;
	private boolean flag = true;
	private int maxp, nowp;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			int progress = mProgressBar.getProgress();
			Log.i("TEST---->", progress + "");
			if (progress < (nowp * 100 / maxp)) {
				Log.i("TEST---->", (nowp * 100 / maxp) + "");
				mProgressBar.setProgress(++progress);

			} else
				flag = false;

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();

		findViewById(R.id.start).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getratio();
				Log.i("TEST", "START");
			}

		});
	}

	public void initView() {
		mProgressBar = (MyProgressBar) findViewById(R.id.progressbar);
		max = (EditText) findViewById(R.id.max);
		nowprogress = (EditText) findViewById(R.id.nowprogress);

	}

	// 指定额度
	public void getratio() {
		String total = max.getText().toString().trim();
		String range = nowprogress.getText().toString().trim();
		if (total.length() > 0 && range.length() > 0) {
			maxp = Integer.parseInt(total);
			nowp = Integer.parseInt(range);
		} else {
			Toast.makeText(getBaseContext(), "输入内容", 0).show();
			return;
		}
		startTest();
	}

	public void startTest() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (flag) {
					try {
						Thread.sleep(100);
						mHandler.sendEmptyMessage(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			flag = false;
			finish();
		}

		return true;
	}

}
