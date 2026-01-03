package com.isarainc.instagram;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Contains all service components such as Instgram and general Http Client.
 * Thread safe singleton.
 * <p/>
 * Date: 6/28/13
 * Time: 7:24 PM
 *
 * @author MiG35
 */
public final class ServerConnector {

	private static final String SERVER_URL = "https://api.instagram.com/";
	private static final String VERSION = "v1/";
	private static final String SERVER_URL_WITH_VERSION = SERVER_URL + VERSION;

	public static final String CLIENT_ID = "9848277110b84cc8b7af9532c362a3b3";
	private static final long TIMEOUT_TIME = 10;

	private final InstagramService mInstagramService;
	private final OkHttpClient mOkHttpClient;

	private ServerConnector() {
		final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();

		mOkHttpClient = new OkHttpClient();
		// mOkHttpClient.setConnectTimeout(TIMEOUT_TIME, TimeUnit.SECONDS);
		// mOkHttpClient.setReadTimeout(TIMEOUT_TIME, TimeUnit.SECONDS);

		mOkHttpClient.interceptors().add(new LoggingInterceptor());

		final Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(SERVER_URL)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.client(mOkHttpClient)
				.build();

		mInstagramService = retrofit.create(InstagramService.class);

	}

	public static InstagramService getInstagramService() {
		return Holder.SERVER_CONNECTOR.mInstagramService;
	}

	public static OkHttpClient getOkHttpClient() {
		return Holder.SERVER_CONNECTOR.mOkHttpClient;
	}

	private static final class Holder {

		private static final ServerConnector SERVER_CONNECTOR = new ServerConnector();
	}

	public static class LoggingInterceptor implements Interceptor {
		@Override
		public Response intercept(Chain chain) throws IOException {
			Request request = chain.request();
			long t1 = System.nanoTime();
			String requestLog = String.format("Sending request %s on %s%n%s",
					request.url(), chain.connection(), request.headers());
			// YLog.d(String.format("Sending request %s on %s%n%s",
			// request.url(), chain.connection(), request.headers()));
			if (request.method().compareToIgnoreCase("post") == 0) {
				requestLog = "\n" + requestLog + "\n" + bodyToString(request);
			}
			Log.d("TAG", "request" + "\n" + requestLog);

			Response response = chain.proceed(request);
			long t2 = System.nanoTime();

			String responseLog = String.format("Received response for %s in %.1fms%n%s",
					response.request().url(), (t2 - t1) / 1e6d, response.headers());

			String bodyString = response.body().string();

			Log.d("TAG", "response" + "\n" + responseLog + "\n" + bodyString);

			return response.newBuilder()
					.body(ResponseBody.create(response.body().contentType(), bodyString))
					.build();
			// return response;
		}
	}

	public static String bodyToString(final Request request) {
		try {
			final Request copy = request.newBuilder().build();
			final Buffer buffer = new Buffer();
			copy.body().writeTo(buffer);
			return buffer.readUtf8();
		} catch (final IOException e) {
			return "did not work";
		}
	}
}