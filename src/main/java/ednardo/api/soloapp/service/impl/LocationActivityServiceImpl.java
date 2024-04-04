package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.model.LocationActivity;
import ednardo.api.soloapp.repository.LocationActivityRepository;
import ednardo.api.soloapp.service.LocationActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationActivityServiceImpl implements LocationActivityService {
    @Autowired
    LocationActivityRepository locationActivityRepository;

    @Override
    public Optional<LocationActivity> getById(Long id) {
        return locationActivityRepository.findById(id);
    }

    @Override
    public LocationActivity create(LocationActivity locationActivity) {
        return locationActivityRepository.save(locationActivity);
    }

    @Override
    public void update(Long id, LocationActivity locationActivity) {
        LocationActivity locationUpdated = locationActivityRepository.findById(id).get();

        locationUpdated.setCountry(locationActivity.getCountry());
        locationUpdated.setCity(locationUpdated.getCity());
        locationUpdated.setAddress(locationUpdated.getAddress());
        locationUpdated.setCoords(locationUpdated.getCoords());

        locationActivityRepository.save(locationUpdated);
    }

}
