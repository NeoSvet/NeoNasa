package ru.neosvet.neonasa.repository

import com.google.gson.annotations.SerializedName

//for Photo
data class PhotoResponse(
    val title: String?,
    val explanation: String?,
    val url: String?,
    val hdurl: String?
)

data class VideoResponse(
    val title: String?,
    val explanation: String?,
    val url: String?
)

data class DayPhotoResponse(
    @field:SerializedName("copyright") val copyright: String?,
    @field:SerializedName("date") val date: String?,
    @field:SerializedName("explanation") val explanation: String?,
    @field:SerializedName("media_type") val mediaType: String?,
    @field:SerializedName("title") val title: String?,
    @field:SerializedName("url") val url: String?,
    @field:SerializedName("hdurl") val hdurl: String?
)

data class EarthPhotoResponse(
    @field:SerializedName("identifier") val identifier: String,
    @field:SerializedName("caption") val caption: String,
    @field:SerializedName("image") val image: String,
    @field:SerializedName("version") val version: String,
    @field:SerializedName("centroid_coordinates") val centroid_coordinates: Coord,
    @field:SerializedName("date") val date: String,
)

data class Coord(
    @field:SerializedName("lat") val lat: Double,
    @field:SerializedName("lon") val lon: Double
)

data class MarsPhotoResponse(
    @field:SerializedName("photos") val photos: List<Photos>
)

data class Photos(
    @field:SerializedName("id") val id: Int,
    @field:SerializedName("sol") val sol: Int,
    @field:SerializedName("camera") val camera: Camera,
    @field:SerializedName("img_src") val imgSrc: String,
    @field:SerializedName("earth_date") val earthDate: String,
    @field:SerializedName("rover") val rover: Rover
)

data class Camera(
    @field:SerializedName("id") val id: Int,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("rover_id") val roverId: Int,
    @field:SerializedName("full_name") val fullName: String
)

data class Rover(
    @field:SerializedName("id") val id: Int,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("landing_date") val landingDate: String,
    @field:SerializedName("launch_date") val launchDate: String,
    @field:SerializedName("status") val status: String
)

//for Asteroids
data class AsteroidsData(
    val links: Links?,
    val elementCount: Int?,
    val days: List<ADay>
)
data class ADay(
    val date: String,
    val asteroids: List<Asteroid>
)

data class AsteroidsResponse(
    @field:SerializedName("links") val links: Links?,
    @field:SerializedName("element_count") val elementCount: Int?,
    @field:SerializedName("near_earth_objects") val objectsPerDay: Map<String, List<Asteroid>>?
)

data class Links(
    @field:SerializedName("next") val next: String?,
    @field:SerializedName("prev") val prev: String?,
    @field:SerializedName("self") val self: String?
)

data class Asteroid(
    @field:SerializedName("links") val links: Links,
    @field:SerializedName("id") val id: String,
    @field:SerializedName("neo_reference_id") val neoReferenceId: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("nasa_jpl_url") val nasaJplUrl: String,
    @field:SerializedName("absolute_magnitude_h") val absoluteMagnitudeH: Double,
    @field:SerializedName("estimated_diameter") val estimatedDiameter: EstimatedDiameter,
    @field:SerializedName("is_potentially_hazardous_asteroid") val isPotentiallyHazardousAsteroid: Boolean,
    @field:SerializedName("close_approach_data") val closeApproachData: List<CloseApproachData>,
    @field:SerializedName("is_sentry_object") val isSentryObject: Boolean
)

data class CloseApproachData(
    @field:SerializedName("close_approach_date") val closeApproachDate: String,
    @field:SerializedName("close_approach_date_full") val closeApproachDateFull: String,
    @field:SerializedName("epoch_date_close_approach") val epochDateCloseApproach: Long,
    @field:SerializedName("relative_velocity") val relativeVelocity: RelativeVelocity,
    @field:SerializedName("miss_distance") val missDistance: MissDistance,
    @field:SerializedName("orbiting_body") val orbitingBody: String
)

data class MissDistance(
    @field:SerializedName("astronomical") val astronomical: String,
    @field:SerializedName("lunar") val lunar: String,
    @field:SerializedName("kilometers") val kilometers: String,
    @field:SerializedName("miles") val miles: String
)

data class RelativeVelocity(
    @field:SerializedName("kilometers_per_second") val kilometersPerSecond: String,
    @field:SerializedName("kilometers_per_hour") val kilometersPerHour: String,
    @field:SerializedName("miles_per_hour") val milesPerHour: String
)

data class EstimatedDiameter(
    @field:SerializedName("kilometers") val kilometers: Kilometers,
    @field:SerializedName("meters") val meters: Meters,
    @field:SerializedName("miles") val miles: Miles,
    @field:SerializedName("feet") val feet: Feet
)

data class Kilometers(
    @field:SerializedName("estimated_diameter_min") val estimatedDiameterMin: Double,
    @field:SerializedName("estimated_diameter_max") val estimatedDiameterMax: Double
)

data class Meters(
    @field:SerializedName("estimated_diameter_min") val estimatedDiameterMin: Double,
    @field:SerializedName("estimated_diameter_max") val estimatedDiameterMax: Double
)

data class Miles(
    @field:SerializedName("estimated_diameter_min") val estimatedDiameterMin: Double,
    @field:SerializedName("estimated_diameter_max") val estimatedDiameterMax: Double
)

data class Feet(
    @field:SerializedName("estimated_diameter_min") val estimatedDiameterMin: Double,
    @field:SerializedName("estimated_diameter_max") val estimatedDiameterMax: Double
)


//for Weather
data class LinkedEvent(
    @field:SerializedName("activityID") val activityID: String?
)

data class Instrument(
    @field:SerializedName("displayName") val displayName: String
)

/*
data class ImpactList(
    @field:SerializedName("isGlancingBlow") val isGlancingBlow: Boolean,
    @field:SerializedName("location") val location: String?,
    @field:SerializedName("arrivalTime") val arrivalTime: String
)
*/

//Coronal Mass Ejection (CME)
data class WeatherCMEResponse(
    @field:SerializedName("activityID") val activityID: String,
    @field:SerializedName("catalog") val catalog: String,
    @field:SerializedName("startTime") val startTime: String,
    @field:SerializedName("sourceLocation") val sourceLocation: String,
    @field:SerializedName("activeRegionNum") val activeRegionNum: Int,
    @field:SerializedName("link") val link: String?,
    @field:SerializedName("note") val note: String?,
    //@field:SerializedName("cmeAnalyses") val cmeAnalyses: List<CmeAnalyses>,
    @field:SerializedName("instruments") val instruments: List<Instrument>?,
    @field:SerializedName("linkedEvents") val linkedEvents: List<LinkedEvent>?
)

/*
data class CmeAnalyses(
    @field:SerializedName("time21_5") val time21_5: String?,
    @field:SerializedName("latitude") val latitude: Int?,
    @field:SerializedName("longitude") val longitude: Int?,
    @field:SerializedName("halfAngle") val halfAngle: Int?,
    @field:SerializedName("speed") val speed: Int?,
    @field:SerializedName("type") val type: String?,
    @field:SerializedName("isMostAccurate") val isMostAccurate: Boolean,
    @field:SerializedName("note") val note: String?,
    @field:SerializedName("levelOfData") val levelOfData: Int?,
    @field:SerializedName("link") val link: String?,
    @field:SerializedName("enlilList") val enlilList: List<EnlilList>
)

data class EnlilList(
    @field:SerializedName("modelCompletionTime") val modelCompletionTime: String?,
    @field:SerializedName("au") val au: Int?,
    @field:SerializedName("estimatedShockArrivalTime") val estimatedShockArrivalTime: String?,
    @field:SerializedName("estimatedDuration") val estimatedDuration: String?,
    @field:SerializedName("rmin_re") val rmin_re: String?,
    @field:SerializedName("kp_18") val kp_18: String?,
    @field:SerializedName("kp_90") val kp_90: String?,
    @field:SerializedName("kp_135") val kp_135: String?,
    @field:SerializedName("kp_180") val kp_180: String?,
    @field:SerializedName("isEarthGB") val isEarthGB: Boolean,
    @field:SerializedName("link") val link: String?,
    @field:SerializedName("impactList") val impactList: List<ImpactList>,
    @field:SerializedName("cmeIDs") val cmeIDs: List<String>
)
*/

//Geomagnetic Storm (GST)
data class WeatherGSTResponse(
    @field:SerializedName("gstID") val gstID: String,
    @field:SerializedName("startTime") val startTime: String,
    @field:SerializedName("allKpIndex") val kpIndices: List<KpIndex>,
    @field:SerializedName("linkedEvents") val linkedEvents: List<LinkedEvent>?,
    @field:SerializedName("link") val link: String?
)

data class KpIndex(
    @field:SerializedName("observedTime") val observedTime: String,
    @field:SerializedName("kpIndex") val kpIndex: Int,
    @field:SerializedName("source") val source: String
)

//Interplanetary Shock (IPS)
data class WeatherIPSResponse(
    @field:SerializedName("catalog") val catalog: String,
    @field:SerializedName("activityID") val activityID: String,
    @field:SerializedName("location") val location: String,
    @field:SerializedName("eventTime") val eventTime: String,
    @field:SerializedName("link") val link: String?,
    @field:SerializedName("instruments") val instruments: List<Instrument>?
)

//Solar Flare (FLR)
data class WeatherFLRResponse(
    @field:SerializedName("flrID") val flrID: String,
    @field:SerializedName("beginTime") val beginTime: String,
    @field:SerializedName("peakTime") val peakTime: String,
    @field:SerializedName("endTime") val endTime: String,
    @field:SerializedName("classType") val classType: String,
    @field:SerializedName("sourceLocation") val sourceLocation: String,
    @field:SerializedName("activeRegionNum") val activeRegionNum: Int,
    @field:SerializedName("instruments") val instruments: List<Instrument>?,
    @field:SerializedName("linkedEvents") val linkedEvents: List<LinkedEvent>?,
    @field:SerializedName("link") val link: String?
)

//Solar Energetic Particle (SEP)
data class WeatherSEPResponse(
    @field:SerializedName("sepID") val sepID: String,
    @field:SerializedName("eventTime") val eventTime: String,
    @field:SerializedName("instruments") val instruments: List<Instrument>?,
    @field:SerializedName("linkedEvents") val linkedEvents: List<LinkedEvent>?,
    @field:SerializedName("link") val link: String?
)

//Magnetopause Crossing (MPC)
data class WeatherMPCResponse(
    @field:SerializedName("mpcID") val mpcID: String,
    @field:SerializedName("eventTime") val eventTime: String,
    @field:SerializedName("instruments") val instruments: List<Instrument>?,
    @field:SerializedName("linkedEvents") val linkedEvents: List<LinkedEvent>?,
    @field:SerializedName("link") val link: String?
)

//Radiation Belt Enhancement (RBE)
data class WeatherRBEResponse(
    @field:SerializedName("rbeID") val rbeID: String,
    @field:SerializedName("eventTime") val eventTime: String,
    @field:SerializedName("instruments") val instruments: List<Instrument>?,
    @field:SerializedName("linkedEvents") val linkedEvents: List<LinkedEvent>?,
    @field:SerializedName("link") val link: String?
)

//Hight Speed Stream (HSS)
data class WeatherHSSResponse(
    @field:SerializedName("hssID") val hssID: String,
    @field:SerializedName("eventTime") val eventTime: String,
    @field:SerializedName("instruments") val instruments: List<Instrument>?,
    @field:SerializedName("linkedEvents") val linkedEvents: List<LinkedEvent>?,
    @field:SerializedName("link") val link: String?
)

//WSA+EnlilSimulation
data class WeatherWSAResponse(
    @field:SerializedName("simulationID") val simulationID: String,
    @field:SerializedName("modelCompletionTime") val modelCompletionTime: String,
    @field:SerializedName("au") val au: Int,
    //@field:SerializedName("cmeInputs") val cmeInputs : List<CmeInputs>,
    @field:SerializedName("estimatedShockArrivalTime") val estimatedShockArrivalTime: String?,
    @field:SerializedName("estimatedDuration") val estimatedDuration: String?,
    @field:SerializedName("rmin_re") val rminRe: String?,
    @field:SerializedName("kp_18") val kp18: String?,
    @field:SerializedName("kp_90") val kp90: String?,
    @field:SerializedName("kp_135") val kp135: String?,
    @field:SerializedName("kp_180") val kp180: String?,
    @field:SerializedName("isEarthGB") val isEarthGB: Boolean,
    //@field:SerializedName("impactList") val impactList : List<ImpactList>,
    @field:SerializedName("link") val link: String?
)

/*
data class CmeInputs (
    @field:SerializedName("cmeStartTime") val cmeStartTime : String,
    @field:SerializedName("latitude") val latitude : Int,
    @field:SerializedName("longitude") val longitude : Int,
    @field:SerializedName("speed") val speed : Int,
    @field:SerializedName("halfAngle") val halfAngle : Int,
    @field:SerializedName("time21_5") val time215 : String,
    @field:SerializedName("isMostAccurate") val isMostAccurate : Boolean,
    @field:SerializedName("levelOfData") val levelOfData : Int,
    @field:SerializedName("ipsList") val ipsList : List<String>,
    @field:SerializedName("cmeid") val cmeid : String
)
*/