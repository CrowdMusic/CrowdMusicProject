package com.hdm.crowdmusic.core;

import org.teleal.cling.binding.annotations.*;

@UpnpService(
        serviceId = @UpnpServiceId("CrowdMusic"),
        serviceType = @UpnpServiceType(value = "CrowdMusic", version = 1)
)
public class CrowdMusicService {
}
