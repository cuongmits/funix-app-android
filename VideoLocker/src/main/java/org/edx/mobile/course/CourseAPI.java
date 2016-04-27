package org.edx.mobile.course;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jakewharton.retrofit.Ok3Client;

import org.edx.mobile.discussion.RetroHttpExceptionHandler;
import org.edx.mobile.http.RetroHttpException;
import org.edx.mobile.model.Page;
import org.edx.mobile.model.api.ProfileModel;
import org.edx.mobile.module.prefs.PrefManager;
import org.edx.mobile.module.prefs.UserPrefs;
import org.edx.mobile.util.Config;
import org.edx.mobile.util.DateUtil;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;

@Singleton
public class CourseAPI {

    @NonNull
    private final CourseService courseService;
    @NonNull
    private final UserPrefs userPrefs;

    // KEON
    @Inject
    Context context;
    @Inject
    Config config;

    @Inject
    public CourseAPI(@NonNull RestAdapter restAdapter, @NonNull UserPrefs userPrefs) {
        RestAdapter ra = restAdapter;
        courseService = restAdapter.create(CourseService.class);
        this.userPrefs = userPrefs;
    }

    public
    @NonNull
    Page<CourseDetail> getCourseList(int page) throws RetroHttpException {
        //return courseService.getCourseList(getUsername(), true, page);
        return createCourseService().getCourseList(getUsername(), true, page);
    }

    public
    @NonNull
    CourseDetail getCourseDetail(@NonNull String courseId) throws RetroHttpException {
        // Empty courseId will return a 200 for a list of course details, instead of a single course
        if (TextUtils.isEmpty(courseId)) throw new IllegalArgumentException();
        //return courseService.getCourseDetail(courseId, getUsername());
        return createCourseService().getCourseDetail(courseId, getUsername());
    }

    @Nullable
    private String getUsername() {
        final ProfileModel profile = userPrefs.getProfile();
        return null == profile ? null : profile.username;
    }

    @NonNull
    private CourseService createCourseService() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat(DateUtil.ISO_8601_DATE_TIME_FORMAT)
                .serializeNulls()
                .create();
        // KEON
        // generate auth headers
        PrefManager pref = new PrefManager(context, PrefManager.Pref.LOGIN);
        final String auth_rest = pref.getCurrentRestAuth();
        final String auth_rest_checker = pref.getCurrentRestAuthChecker();

        RestAdapter ra = new RestAdapter.Builder()
                //.setClient(new Ok3Client(client))
                .setClient(new Ok3Client()) //keon
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) { //keon
                        request.addHeader("Authorization", auth_rest);
                    }
                })
                .setEndpoint(config.getApiHostURL())
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new RetroHttpExceptionHandler())
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("******"))
                .build();

        return ra.create(CourseService.class);
    }

}
