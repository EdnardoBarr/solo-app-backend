package ednardo.api.soloapp.service;

import ednardo.api.soloapp.model.LocationActivity;
import ednardo.api.soloapp.model.dto.LocationActivityDTO;
import org.springframework.stereotype.Service;

public interface LocationActivityService {
    LocationActivity create(LocationActivityDTO locationActivityDTO);
}
