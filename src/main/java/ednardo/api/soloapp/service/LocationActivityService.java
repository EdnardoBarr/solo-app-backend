package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.LocationActivity;

import java.util.List;
import java.util.Optional;

public interface LocationActivityService {
    Optional<LocationActivity> getById(Long id);
    LocationActivity create(LocationActivity locationActivity);
    void update(Long id, LocationActivity locationActivity);
}
