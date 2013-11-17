package com.hdm.crowdmusic.core;

import org.teleal.cling.binding.annotations.UpnpService;
import org.teleal.cling.binding.annotations.UpnpServiceId;
import org.teleal.cling.binding.annotations.UpnpServiceType;

@UpnpService(
        serviceId = @UpnpServiceId("CrowdMusic"),
        serviceType = @UpnpServiceType(value = "CrowdMusic", version = 1)
)
public class CrowdMusicUPnPService {
}
