package ednardo.api.soloapp.converter;

import ednardo.api.soloapp.model.dto.ActivityCategoryDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ActivityCategoryDTOConverter implements Converter<String, ActivityCategoryDTO> {


    @Override
    public ActivityCategoryDTO convert(String source) {
        String[] data = source.split(",");
        if (data.length != 3) {
            throw new IllegalArgumentException("Invalid format for ActivityCategoryDTO");
        }
        int id = Integer.parseInt(data[0]);
        String label = data[1];
        String value = data[2];
        return new ActivityCategoryDTO(id, label, value);
    }
}
