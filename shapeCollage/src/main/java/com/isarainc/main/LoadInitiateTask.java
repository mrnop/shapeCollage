package com.isarainc.main;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class LoadInitiateTask extends AsyncTask<String, Integer, Integer> {

	private Context context;
	private LoadingTaskFinishedListener finishedListener;

	private static final String TAG = "LoadInitiateTask";

	public LoadInitiateTask(Context context,
			LoadingTaskFinishedListener finishedListener) {
		super();
		this.context = context;
		this.finishedListener = finishedListener;

	}

	@Override
	protected Integer doInBackground(String... params) {
		try {
		
//			TemplateManager templateManager=TemplateManager.getInstance(context);
//			List<String> templates=templateManager.listTemplates();
//			for(String template:templates){
//				try{
//					Template t=templateManager.loadTemplate(template);
//					templateManager.getImageScaleFromAssets(t.getImage());
//				}catch(Exception e){
//					System.gc();
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d(TAG, "finished doInBackground");
		return 1234;
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		Log.d(TAG, "onPostExecute " + finishedListener);
		if (finishedListener != null)
			finishedListener.onTaskFinished(); // Tell whoever was listening we
												// have
												// finished
	}

}
