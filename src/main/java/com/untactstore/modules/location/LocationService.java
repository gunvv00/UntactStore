package com.untactstore.modules.location;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public Location findOrCreateNew(String name) {
        Location location = locationRepository.findByName(name);
        if (location == null) {
            location = locationRepository.save(Location.builder().name(name).build());
        }
        return location;
    }
}
