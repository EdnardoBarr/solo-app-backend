package ednardo.api.soloapp.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestFriendshipDTO {
    private Long fromId;
    private Long toId;
}
