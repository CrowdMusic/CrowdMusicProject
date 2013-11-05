package com.hdm.crowdmusic.core;

import org.teleal.cling.binding.annotations.*;

@UpnpService(
        serviceId = @UpnpServiceId("CrowdMusicServer"),
        serviceType = @UpnpServiceType(value = "CrowdMusicServer", version = 1)
)
public class CrowdMusicServerService {
}
