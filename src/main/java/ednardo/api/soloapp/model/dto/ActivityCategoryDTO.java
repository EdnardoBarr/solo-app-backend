package ednardo.api.soloapp.model.dto;

import ednardo.api.soloapp.enums.ActivityCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityCategoryDTO {
    private int id;
    private String label;
    private String value;

    public ActivityCategoryDTO(int id, String label, String value) {
        this.id = id;
        this.label = label;
        this.value = value;
    }

    public ActivityCategory toEntity() {
        return ActivityCategory.valueOf(this.value);
    }
}
