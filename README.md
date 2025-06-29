# Location Tracker App

An Android app that tracks device location and pushes updates to a local Traccar server.

## Features

- Real-time location tracking using Fused Location Provider
- Background location updates via Foreground Service
- Device registration and location push to Traccar API
- Permission handling for location access
- Location history display in a RecyclerView
- Alerts user to enable GPS and location permissions

## Architecture

- MVVM pattern with ViewModel handling business logic
- Location tracking moved to a dedicated Service for reliability
- SharedPreferences used for device ID and tracking state persistence
- Retrofit and OkHttp for API communication

# NOTE:

- in order for this to work, make sure to change the HOST_MACHINE_IP in the Constants.kt file,
    - also make sure traccar server is running. Below is the contents of the traccar.xml file that
      allow the server to function correctly:

```<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM 'http://java.sun.com/dtd/properties.dtd'>
<properties>

    <!-- Documentation: https://www.traccar.org/configuration-file/ -->
    <entry key='protocols.enable'>osmand</entry>
    <entry key='osmand.enable'>true</entry>

    <entry key='osmand.port'>5055</entry>
    
    <entry key='database.driver'>org.h2.Driver</entry>
    <entry key='database.url'>jdbc:h2:./data/database</entry>
    <entry key='database.user'>sa</entry>
    <entry key='database.password'></entry>


</properties>
```

- this assumes that the default username and password for BasicAuthentication are 'admin' and '
  admin', respectively
