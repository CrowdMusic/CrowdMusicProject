package com.hdm.crowdmusic.core;

import org.teleal.cling.binding.annotations.UpnpService;
import org.teleal.cling.binding.annotations.UpnpServiceId;
import org.teleal.cling.binding.annotations.UpnpServiceType;

@UpnpService(
        serviceId = @UpnpServiceId("CrowdMusicClient"),
        serviceType = @UpnpServiceType(value = "CrowdMusicClient", version = 1)
)
public class CrowdMusicClientService {
}
