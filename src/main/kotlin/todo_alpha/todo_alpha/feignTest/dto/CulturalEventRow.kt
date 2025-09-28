package todo_alpha.todo_alpha.feignTest.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CulturalEventRow(
    @JsonProperty("CODENAME") val CODENAME: String?,
    @JsonProperty("GUNAME") val GUNAME: String?,
    @JsonProperty("TITLE") val TITLE: String,
    @JsonProperty("DATE") val DATE: String?,
    @JsonProperty("PLACE") val PLACE: String?,
    @JsonProperty("ORG_NAME") val ORG_NAME: String?,
    @JsonProperty("USE_TRGT") val USE_TRGT: String?,
    @JsonProperty("USE_FEE") val USE_FEE: String?,
    @JsonProperty("PLAYER") val PLAYER: String?,
    @JsonProperty("PROGRAM") val PROGRAM: String?,
    @JsonProperty("ETC_DESC") val ETC_DESC: String?,
    @JsonProperty("ORG_LINK") val ORG_LINK: String?,
    @JsonProperty("MAIN_IMG") val MAIN_IMG: String?,
    @JsonProperty("RGSTDATE") val RGSTDATE: String?,
    @JsonProperty("TICKET") val TICKET: String?,
    @JsonProperty("STRTDATE") val STRTDATE: String?,
    @JsonProperty("END_DATE") val END_DATE: String?,
    @JsonProperty("THEMECODE") val THEMECODE: String?,
    @JsonProperty("LOT") val LOT: String?, // 경도(Longitude)
    @JsonProperty("LAT") val LAT: String?, // 위도(Latitude)
    @JsonProperty("IS_FREE") val IS_FREE: String?,
    @JsonProperty("HMPG_ADDR") val HMPG_ADDR: String?
)
