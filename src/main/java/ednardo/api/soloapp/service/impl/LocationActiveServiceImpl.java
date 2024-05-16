package ednardo.api.soloapp.service.impl;

import ednardo.api.soloapp.exception.handler.RestResponseExceptionHandler;
import ednardo.api.soloapp.model.LocationActivity;
import ednardo.api.soloapp.model.dto.LocationActivityDTO;
import ednardo.api.soloapp.repository.LocationActivityRepository;
import ednardo.api.soloapp.service.LocationActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LocationActiveServiceImpl implements LocationActivityService {
    @Autowired
    LocationActivityRepository locationActivityRepository;

    public LocationActivity create(LocationActivityDTO locationActivityDTO) {
        try {
            LocationActivity newLocationActivity = LocationActivity.builder()
                    .address(locationActivityDTO.getAddress())
                    .city(locationActivityDTO.getCity())
                    .country(locationActivityDTO.getCountry())
                    .coords(locationActivityDTO.getCoords())
                    .build();

        return this.locationActivityRepository.save(newLocationActivity);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to save activity");
        }
    }
}
