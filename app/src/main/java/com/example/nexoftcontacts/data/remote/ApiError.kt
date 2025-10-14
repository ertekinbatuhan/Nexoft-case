package com.example.nexoftcontacts.data.remote

enum class ApiError(val code: Int, val message: String) {
    UNAUTHORIZED(401, "Unauthorized: Invalid API key"),
    BAD_REQUEST(400, "Bad Request: Invalid request format"),
    NOT_FOUND(404, "Resource not found"),
    SERVER_ERROR(500, "Server Error: Please try again later"),
    NETWORK_ERROR(-1, "Network error: Please check your connection"),
    INVALID_FILE_FORMAT(-2, "Invalid file format. Only PNG and JPG files are accepted"),
    UNKNOWN_ERROR(-3, "An unknown error occurred");

    companion object {
        fun fromHttpCode(code: Int): ApiError {
            return values().find { it.code == code } ?: UNKNOWN_ERROR
        }
        
        fun getErrorMessage(code: Int, defaultMessage: String = ""): String {
            return when (code) {
                401 -> UNAUTHORIZED.message
                400 -> BAD_REQUEST.message
                404 -> NOT_FOUND.message
                500 -> SERVER_ERROR.message
                else -> defaultMessage.ifEmpty { UNKNOWN_ERROR.message }
            }
        }
    }
}

sealed class ApiException(message: String) : Exception(message) {
    class Unauthorized(message: String = ApiError.UNAUTHORIZED.message) : ApiException(message)
    class BadRequest(message: String = ApiError.BAD_REQUEST.message) : ApiException(message)
    class NotFound(message: String = ApiError.NOT_FOUND.message) : ApiException(message)
    class ServerError(message: String = ApiError.SERVER_ERROR.message) : ApiException(message)
    class NetworkError(message: String = ApiError.NETWORK_ERROR.message) : ApiException(message)
    class InvalidFileFormat(message: String = ApiError.INVALID_FILE_FORMAT.message) : ApiException(message)
    class Unknown(message: String = ApiError.UNKNOWN_ERROR.message) : ApiException(message)
    
    companion object {
        fun fromHttpCode(code: Int, customMessage: String? = null): ApiException {
            val message = customMessage ?: ApiError.getErrorMessage(code)
            return when (code) {
                401 -> Unauthorized(message)
                400 -> BadRequest(message)
                404 -> NotFound(message)
                500 -> ServerError(message)
                else -> Unknown(message)
            }
        }
    }
}

