package com.isarainc.instagram;


import com.isarainc.instagram.responces.SearchUserResponse;
import com.isarainc.instagram.responces.UserRecentFeedResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Date: 4/8/2014
 * Time: 2:28 PM
 *
 * @author MiG35
 */
@SuppressWarnings("InterfaceNeverImplemented")
public interface InstagramService {

	@GET("/v1/users/search?client_id=" + ServerConnector.CLIENT_ID)
	Call<SearchUserResponse> searchUser(@Query("q") final String query) throws Exception;

	@GET("/v1/users/{user-id}/media/recent/?client_id=" + ServerConnector.CLIENT_ID)
	Call<UserRecentFeedResponse> getUserRecentFeed(@Path("user-id") final String userId, @Query("count") final int count) throws Exception;

	@GET("/v1/users/{user-id}/media/recent/?client_id=" + ServerConnector.CLIENT_ID)
	Call<UserRecentFeedResponse> getUserRecentFeed(@Path("user-id") final String userId, @Query("count") final int count,
												   @Query("max_id") final String maxId) throws Exception;
}