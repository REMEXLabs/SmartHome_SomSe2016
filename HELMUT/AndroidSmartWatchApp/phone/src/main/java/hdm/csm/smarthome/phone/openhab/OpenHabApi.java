package hdm.csm.smarthome.phone.openhab;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface OpenHabApi {

    @GET("things")
    Call<List<Thing>> getThings();

    @POST("things")
    Call<Thing> createThing(@Body Thing thing);

    @GET("things/{thingUID}")
    Call<Thing> getThing(@Path("thingUID") String thingUID);

    @DELETE("things/{thingUID}")
    Call<Thing> deleteThing(@Path("thingUID") String thingUID);

    @PUT("things/{thingUID}")
    Call<Thing> updateThing(@Path("thingUID") String thingUID, @Body Thing thing);

    @PUT("things/{thingUID}/config")
    Call<Thing> updateThingConfig(@Path("thingUID") String thingUID, @Body Map<String, String> config);

}
