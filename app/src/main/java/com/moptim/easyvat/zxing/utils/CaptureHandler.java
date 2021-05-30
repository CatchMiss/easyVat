/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific ic_language governing permissions and
 * limitations under the License.
 */

package com.moptim.easyvat.zxing.utils;

import android.os.Handler;
import android.os.Message;

import com.google.zxing.Result;
import com.moptim.easyvat.R;
import com.moptim.easyvat.zxing.activity.CaptureActivity;
import com.moptim.easyvat.zxing.camera.CameraManager;
import com.moptim.easyvat.zxing.decode.DecodeThread;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class CaptureHandler extends Handler {

	private final CaptureActivity captureActivity;
	private final DecodeThread decodeThread;
	private final CameraManager cameraManager;

	public CaptureHandler(CaptureActivity activity, CameraManager manager, int decodeMode) {
		captureActivity = activity;
		
		decodeThread = new DecodeThread(activity, decodeMode);
		decodeThread.start();

		// Start ourselves capturing previews and decoding.
		cameraManager = manager;
		cameraManager.startPreview();
		cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case R.id.decode_succeeded:
			captureActivity.handleDecode((Result) message.obj, message.getData());
			break;
		case R.id.decode_failed:
			cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
			break;
		}
	}

	public void quitSynchronously() {
		cameraManager.stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
		quit.sendToTarget();
		try {
			// Wait at most half a second; should be enough time, and onPause()
			// will timeout quickly
			decodeThread.join(500L);
		} catch (InterruptedException e) {
			// continue
		}

		// Be absolutely sure we don't send any queued up messages
		removeMessages(R.id.decode_succeeded);
		removeMessages(R.id.decode_failed);
	}

}
