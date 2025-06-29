package natour.dev.zonetechnologiestask.core.util

object Constants {
    // MAKE SURE TO CHANGE THIS TO YOUR MACHINE'S IP ADDRESS
    // ipconfig

    const val HOST_MACHINE_IP = "192.168.1.9"
    const val BASE_URL =  "http://${HOST_MACHINE_IP}:8082/"
    const val DEVICE_ENDPOINT = "api/devices"
    const val PUSH_LOCATION_PORT = 5055
    const val DEVICE_ID_KEY = "deviceId" // declared here because appstrings cannot be accessed in viewmodel

}